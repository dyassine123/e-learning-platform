import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { CourseResponse } from '../dtos/course.dto';
import { CourseStatus } from '../models/CourseStatus';
import { CourseService } from '../services/course.service';

@Component({
  selector: 'app-visitor',
  templateUrl: './visitor.component.html',
  styleUrls: ['./visitor.component.css']
})
export class VisitorComponent implements OnInit {
  courses: CourseResponse[] = [];
  loading = true;
  error = '';

  constructor(
    private courseService: CourseService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPublishedCourses();
  }

  loadPublishedCourses(): void {
    this.loading = true;
    this.error = '';
    this.courseService.listPublished(undefined, 0, 12).subscribe({
      next: (page) => {
        this.courses = page.content;
        this.loading = false;
      },
      error: (err: HttpErrorResponse) => {
        // Backend protects course endpoint by token; keep visitor UX by showing preview catalog.
        if (err.status === 401 || err.status === 403) {
          this.courses = this.getGuestPreviewCourses();
          this.error = '';
          this.loading = false;
          return;
        }
        this.error = 'Failed to load courses.';
        this.loading = false;
      }
    });
  }

  goToLoginForCourse(courseId: number): void {
    this.router.navigate(['/login'], { queryParams: { next: `/student/course-detail/${courseId}` } });
  }

  private getGuestPreviewCourses(): CourseResponse[] {
    const now = new Date().toISOString();
    return [
      {
        id: 1,
        ownerId: 0,
        instructorName: 'Alexandru G.',
        categoryId: 1,
        categoryName: 'Development',
        title: 'Fullstack Web Architect',
        description: 'Build fullstack apps with modern frontend and backend architecture.',
        free: false,
        price: 49,
        language: 'English',
        level: 'Intermediate',
        thumbnailUrl: 'https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&q=80&w=2072',
        previewVideoUrl: '',
        status: CourseStatus.PUBLISHED,
        createdAt: now,
        updatedAt: now,
        publishedAt: now
      },
      {
        id: 2,
        ownerId: 0,
        instructorName: 'Sarah Jenkins',
        categoryId: 2,
        categoryName: 'Design',
        title: 'UI Industrial Systems',
        description: 'Design clean, scalable interfaces and practical design systems.',
        free: true,
        price: 0,
        language: 'English',
        level: 'Beginner',
        thumbnailUrl: 'https://images.unsplash.com/photo-1558655146-d09347e92766?auto=format&fit=crop&q=80&w=1964',
        previewVideoUrl: '',
        status: CourseStatus.PUBLISHED,
        createdAt: now,
        updatedAt: now,
        publishedAt: now
      },
      {
        id: 3,
        ownerId: 0,
        instructorName: 'Dr. Michael Chen',
        categoryId: 3,
        categoryName: 'Data Science',
        title: 'AI Data Infrastructure',
        description: 'Learn data pipelines, AI-ready storage, and model-serving foundations.',
        free: false,
        price: 79,
        language: 'English',
        level: 'Advanced',
        thumbnailUrl: 'https://images.unsplash.com/photo-1550751827-4bd374c3f58b?auto=format&fit=crop&q=80&w=2070',
        previewVideoUrl: '',
        status: CourseStatus.PUBLISHED,
        createdAt: now,
        updatedAt: now,
        publishedAt: now
      }
    ];
  }
}
