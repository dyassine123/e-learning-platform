import { Component, OnInit } from '@angular/core';
import { CourseService } from '../../services/course.service';
import { CategoryService } from '../../services/category.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CategoryResponse } from '../../dtos/category.dto';

@Component({
  selector: 'app-create-cours',
  templateUrl: './create-cours.component.html',
  styleUrls: ['./create-cours.component.css']
})
export class CreateCoursComponent implements OnInit {
  categories: CategoryResponse[] = [];
  editMode = false;
  courseId: number | null = null;
  courseData = {
    title: '',
    description: '',
    categoryId: null as number | null,
    price: 0,
    free: true,
    language: 'EN',
    level: 'BEGINNER',
    thumbnailUrl: '',
    previewVideoUrl: ''
  };
  loading = false;

  constructor(
    private courseService: CourseService,
    private categoryService: CategoryService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.categoryService.list(0, 50).subscribe({
      next: (res) => this.categories = res.content,
      error: (err) => console.error('Error loading categories', err)
    });

    this.route.queryParams.subscribe(params => {
      if (params['edit']) {
        this.editMode = true;
        this.courseId = +params['edit'];
        this.loadCourseForEdit(this.courseId);
      }
    });
  }

  private loadCourseForEdit(id: number): void {
    this.courseService.getById(id).subscribe({
      next: (course) => {
        this.courseData = {
          title: course.title,
          description: course.description || '',
          categoryId: course.categoryId,
          price: course.price || 0,
          free: !course.price,
          language: course.language || 'EN',
          level: course.level || 'BEGINNER',
          thumbnailUrl: course.thumbnailUrl || '',
          previewVideoUrl: course.previewVideoUrl || ''
        };
      },
      error: (err) => console.error('Error loading course for edit', err)
    });
  }

  onSubmit(): void {
    if (!this.courseData.title || !this.courseData.categoryId) {
      alert('Please fill in required fields');
      return;
    }

    this.loading = true;
    
    // Prepare data: Ensure price is null if the course is free to avoid validation conflicts
    const payload = { ...this.courseData };
    if (payload.free) {
      payload.price = null as any;
    }

    if (this.editMode && this.courseId) {
      this.courseService.update(this.courseId, payload as any).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/teacher/courses']);
        },
        error: (err) => {
          this.loading = false;
          console.error('Course Update Error:', err);
          alert(`Update failed: ${err.error?.message || err.message}`);
        }
      });
    } else {
      this.courseService.create(payload as any).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/teacher/courses']);
        },
        error: (err) => {
          this.loading = false;
          console.error('Detailed Course Creation Error:', err);
          alert(`Creation failed: ${err.error?.message || err.message}`);
        }
      });
    }
  }
}
