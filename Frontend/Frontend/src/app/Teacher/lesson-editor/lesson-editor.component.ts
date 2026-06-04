import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CourseService } from '../../services/course.service';
import { 
  CourseSectionResponse, 
  LessonResponse, 
  CourseSectionCreateRequest, 
  LessonCreateRequest,
  LessonMaterialResponse,
  LessonMaterialCreateRequest
} from '../../dtos/course.dto';
import { LessonType } from '../../models/Lesson';

interface LessonWithMaterials extends LessonResponse {
  materials?: LessonMaterialResponse[];
  showMaterialForm?: boolean;
}

interface SectionWithLessons extends CourseSectionResponse {
  lessons: LessonWithMaterials[];
  showLessonForm?: boolean;
}

@Component({
  selector: 'app-lesson-editor',
  templateUrl: './lesson-editor.component.html',
  styleUrls: ['./lesson-editor.component.css']
})
export class LessonEditorComponent implements OnInit {
  courseId!: number;
  sections: SectionWithLessons[] = [];
  loading = false;

  // Forms data
  newSection = { title: '', description: '', displayOrder: 1 };
  newLesson = { title: '', type: LessonType.ARTICLE, displayOrder: 1, estimatedMinutes: 10 };
  newMaterial = { title: '', type: 'PDF_URL' };
  selectedFile: File | null = null;

  constructor(
    private route: ActivatedRoute,
    private courseService: CourseService
  ) {}

  ngOnInit(): void {
    this.courseId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.courseId) {
      this.loadSections();
    }
  }

  loadSections(): void {
    this.loading = true;
    this.courseService.listSectionsByCourse(this.courseId).subscribe({
      next: (sections) => {
        this.sections = sections.map(s => ({ ...s, lessons: [] }));
        this.sections.forEach(s => this.loadLessons(s));
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading sections', err);
        this.loading = false;
      }
    });
  }

  loadLessons(section: SectionWithLessons): void {
    this.courseService.listLessonsBySection(section.id).subscribe({
      next: (lessons) => {
        section.lessons = lessons.sort((a, b) => a.displayOrder - b.displayOrder);
        section.lessons.forEach(l => this.loadMaterials(l));
      },
      error: (err) => console.error('Error loading lessons', err)
    });
  }

  loadMaterials(lesson: LessonWithMaterials): void {
    this.courseService.listMaterialsByLesson(lesson.id).subscribe({
      next: (materials) => {
        lesson.materials = materials;
      },
      error: (err) => console.error('Error loading materials', err)
    });
  }

  addSection(): void {
    if (!this.newSection.title) return;

    const request: CourseSectionCreateRequest = {
      courseId: this.courseId,
      title: this.newSection.title,
      description: this.newSection.description,
      displayOrder: this.sections.length + 1
    };

    this.courseService.createSection(request).subscribe({
      next: (section) => {
        this.sections.push({ ...section, lessons: [] });
        this.newSection = { title: '', description: '', displayOrder: this.sections.length + 1 };
      },
      error: (err) => alert('Error creating section: ' + err.message)
    });
  }

  addLesson(section: SectionWithLessons): void {
    if (!this.newLesson.title) return;

    const request: LessonCreateRequest = {
      sectionId: section.id,
      title: this.newLesson.title,
      type: this.newLesson.type,
      displayOrder: section.lessons.length + 1,
      estimatedMinutes: this.newLesson.estimatedMinutes
    };

    this.courseService.createLesson(request).subscribe({
      next: (lesson) => {
        section.lessons.push(lesson);
        section.showLessonForm = false;
        this.newLesson = { title: '', type: LessonType.ARTICLE, displayOrder: 1, estimatedMinutes: 10 };
      },
      error: (err) => alert('Error creating lesson: ' + err.message)
    });
  }

  toggleMaterialForm(lesson: LessonWithMaterials): void {
    lesson.showMaterialForm = !lesson.showMaterialForm;
    if (lesson.showMaterialForm) {
      this.newMaterial = { title: '', type: 'PDF_URL' };
      this.selectedFile = null;
    }
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  addMaterial(lesson: LessonWithMaterials): void {
    if (!this.selectedFile) {
        alert('Please select a PDF file first!');
        return;
    }

    const formData = new FormData();
    formData.append('lessonId', lesson.id.toString());
    formData.append('title', this.newMaterial.title || '');
    formData.append('file', this.selectedFile);

    this.courseService.createMaterial(formData).subscribe({
      next: (material) => {
        if (!lesson.materials) lesson.materials = [];
        lesson.materials.push(material);
        lesson.showMaterialForm = false;
        this.newMaterial = { title: '', type: 'PDF_URL' };
        this.selectedFile = null;
      },
      error: (err) => alert('Error adding material: ' + err.message)
    });
  }

  deleteMaterial(lesson: LessonWithMaterials, materialId: number): void {
    if (!confirm('Are you sure you want to delete this material?')) return;

    this.courseService.deleteMaterial(materialId).subscribe({
      next: () => {
        if (lesson.materials) {
          lesson.materials = lesson.materials.filter(m => m.id !== materialId);
        }
      },
      error: (err) => alert('Error deleting material: ' + err.message)
    });
  }
}
