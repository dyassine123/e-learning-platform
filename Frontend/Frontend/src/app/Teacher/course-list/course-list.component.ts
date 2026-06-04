import { Component, OnInit } from '@angular/core';
import { CourseService } from '../../services/course.service';
import { CourseResponse } from '../../dtos/course.dto';
import { DashboardService } from '../../services/dashboard.service';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Component({
  selector: 'app-course-list',
  templateUrl: './course-list.component.html',
  styleUrls: ['./course-list.component.css']
})
export class CourseListComponent implements OnInit {
  courses: (CourseResponse & { studentsCount: number })[] = [];
  currentPage = 0;
  totalElements = 0;

  constructor(
    private courseService: CourseService,
    private dashboardService: DashboardService
  ) {}

  ngOnInit(): void {
    this.loadCourses();
  }

  loadCourses(page: number = 0): void {
    this.courseService.listMine(page, 10).subscribe({
      next: (res) => {
        const baseCourses = res.content;
        if (baseCourses.length === 0) {
          this.courses = [];
          this.currentPage = res.number;
          this.totalElements = res.totalElements;
          return;
        }

        const countCalls = baseCourses.map((c: CourseResponse) =>
          this.dashboardService.getCourseEnrollments(c.id).pipe(
            map((enrollments) => ({ ...c, studentsCount: enrollments.length })),
            catchError(() => of({ ...c, studentsCount: 0 }))
          )
        );

        forkJoin(countCalls).subscribe((coursesWithCounts) => {
          this.courses = coursesWithCounts;
          this.currentPage = res.number;
          this.totalElements = res.totalElements;
        });
      },
      error: (err) => console.error('Error loading my courses', err)
    });
  }

  archiveCourse(id: number): void {
    if (confirm('Are you sure you want to archive this course?')) {
      this.courseService.archive(id).subscribe({
        next: () => this.loadCourses(this.currentPage),
        error: (err) => console.error('Error archiving course', err)
      });
    }
  }

  deleteCourse(id: number): void {
    if (confirm('Are you sure you want to PERMANENTLY delete this course? This action cannot be undone.')) {
      this.courseService.deleteCourse(id).subscribe({
        next: () => {
          alert('Course deleted successfully.');
          this.loadCourses(this.currentPage);
        },
        error: (err) => {
          console.error('Error deleting course', err);
          alert('Failed to delete course.');
        }
      });
    }
  }

  submitForApproval(id: number): void {
    if (confirm('Submit this course for admin approval?')) {
      this.courseService.submitForApproval(id).subscribe({
        next: () => {
          alert('Course submitted for approval successfully.');
          this.loadCourses(this.currentPage);
        },
        error: (err) => {
          console.error('Error submitting course', err);
          alert('Failed to submit course for approval.');
        }
      });
    }
  }

  get isEmpty(): boolean {
    return !this.courses || this.courses.length === 0;
  }
}
