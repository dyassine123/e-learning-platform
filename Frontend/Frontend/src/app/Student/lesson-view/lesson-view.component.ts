import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CourseService } from '../../services/course.service';
import { 
  LessonResponse, 
  LessonMaterialResponse, 
  CourseSectionResponse,
  LessonProgressResponse
} from '../../dtos/course.dto';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { environment } from '../../../environments/environment';
import { ProgressService } from '../../services/progress.service';

interface SectionWithLessons extends CourseSectionResponse {
  lessons: LessonResponse[];
}

@Component({
  selector: 'app-lesson-view',
  templateUrl: './lesson-view.component.html',
  styleUrls: ['./lesson-view.component.css']
})
export class LessonViewComponent implements OnInit {
  lessonId!: number;
  lesson?: LessonResponse;
  materials: LessonMaterialResponse[] = [];
  sections: SectionWithLessons[] = [];
  loading = true;
  selectedMaterial?: LessonMaterialResponse;
  readonly baseUrl = environment.apiUrl.replace(/\/api$/, '');
  progressPercent = 0;
  progressCompleted = false;
  isEnrolled = true; // Default to true until checked

  constructor(
    private route: ActivatedRoute,
    private courseService: CourseService,
    private sanitizer: DomSanitizer,
    private progressService: ProgressService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.lessonId = Number(params.get('id')); // or 'lessonId' depending on routing
      if (this.lessonId) {
        this.loadLessonData();
      }
    });
  }

  loadLessonData(): void {
    this.loading = true;
    this.courseService.getLessonById(this.lessonId).subscribe({
      next: (lesson) => {
        this.lesson = lesson;
        this.courseService.checkEnrollment(lesson.courseId).subscribe(enrolled => {
          this.isEnrolled = enrolled;
          if (!enrolled) {
            // Optional: redirect or show overlay
          }
        });
        this.loadCurrentProgress();
        this.markLessonAsOpened();
        this.loadMaterials();
        this.loadCourseStructure(lesson.courseId);
      },
      error: (err) => {
        console.error('Error loading lesson', err);
        this.loading = false;
      }
    });
  }

  loadMaterials(): void {
    this.courseService.listMaterialsByLesson(this.lessonId).subscribe({
      next: (materials) => {
        this.materials = materials.filter(m => m.type === 'PDF_URL');
        this.selectedMaterial = this.materials[0];
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading materials', err);
        this.loading = false;
      }
    });
  }

  getFullUrl(path: string): string {
    if (path.startsWith('http')) return path;
    return this.baseUrl + path;
  }

  get selectedPdfUrl(): SafeResourceUrl | null {
    if (!this.selectedMaterial) return null;
    return this.sanitizer.bypassSecurityTrustResourceUrl(this.getFullUrl(this.selectedMaterial.content));
  }

  loadCourseStructure(courseId: number): void {
    this.courseService.listSectionsByCourse(courseId).subscribe({
      next: (sections) => {
        this.sections = sections.map(s => ({ ...s, lessons: [] }));
        this.sections.forEach(s => {
          this.courseService.listLessonsBySection(s.id).subscribe({
            next: (lessons) => {
              s.lessons = lessons
                .sort((a, b) => a.displayOrder - b.displayOrder);
            }
          });
        });
      }
    });
  }

  selectMaterial(material: LessonMaterialResponse): void {
    this.selectedMaterial = material;
  }

  private loadCurrentProgress(): void {
    this.progressService.myProgress().subscribe({
      next: (progressRows) => {
        const lessonProgress = progressRows.find(p => p.lessonId === this.lessonId);
        if (!lessonProgress) return;
        this.applyProgress(lessonProgress);
      },
      error: () => {
        // Keep UI usable even if progress endpoint is temporarily unavailable.
      }
    });
  }

  private markLessonAsOpened(): void {
    if (!this.lessonId) return;
    this.progressService.updateProgress({
      lessonId: this.lessonId,
      progressPercent: 100
    }).subscribe({
      next: (saved) => this.applyProgress(saved),
      error: () => {
        // Student may not be enrolled yet; do not block lesson view.
      }
    });
  }

  private applyProgress(progress: LessonProgressResponse): void {
    this.progressPercent = progress.progressPercent;
    this.progressCompleted = progress.completed;
    if (progress.completed || progress.progressPercent >= 100) {
      const key = 'completedLessonIds';
      const existing = localStorage.getItem(key);
      const ids: number[] = existing ? JSON.parse(existing) : [];
      if (!ids.includes(this.lessonId)) {
        ids.push(this.lessonId);
        localStorage.setItem(key, JSON.stringify(ids));
      }
    }
  }
}
