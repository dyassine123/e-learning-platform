import { Component, OnInit } from '@angular/core';
import { CourseService } from '../../services/course.service';
import { CourseResponse } from '../../dtos/course.dto';

@Component({
  selector: 'app-course-approval',
  templateUrl: './course-approval.component.html',
  styleUrls: ['./course-approval.component.css']
})
export class CourseApprovalComponent implements OnInit {
  courses: CourseResponse[] = [];
  loading = false;

  constructor(private courseService: CourseService) {}

  ngOnInit(): void {
    this.fetchPendingCourses();
  }

  fetchPendingCourses(): void {
    this.loading = true;
    this.courseService.listPending().subscribe({
      next: (page) => {
        this.courses = page.content;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to fetch pending courses', err);
        this.loading = false;
      }
    });
  }

  approveCourse(course: CourseResponse): void {
    this.courseService.approve(course.id).subscribe({
      next: () => {
        this.courses = this.courses.filter(c => c.id !== course.id);
        alert(`Course "${course.title}" has been approved and published.`);
      },
      error: (err) => {
        console.error('Failed to approve course', err);
        alert('Failed to approve course. Please check backend logs.');
      }
    });
  }

  rejectCourse(course: CourseResponse): void {
    this.courseService.reject(course.id).subscribe({
      next: () => {
        this.courses = this.courses.filter(c => c.id !== course.id);
        alert(`Course "${course.title}" has been rejected and returned to teacher.`);
      },
      error: (err) => {
        console.error('Failed to reject course', err);
        alert('Failed to reject course.');
      }
    });
  }

  getInitials(name: string): string {
    if (!name) return '??';
    return name.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2);
  }
}
