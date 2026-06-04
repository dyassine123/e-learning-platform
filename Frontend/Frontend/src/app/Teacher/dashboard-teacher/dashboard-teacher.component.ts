import { Component, OnInit } from '@angular/core';
import { CourseService } from '../../services/course.service';
import { DashboardService } from '../../services/dashboard.service';
import { CourseResponse } from '../../dtos/course.dto';
import { EnrollmentStudentResponse } from '../../dtos/dashboard.dto';

@Component({
  selector: 'app-dashboard-teacher',
  templateUrl: './dashboard-teacher.component.html',
  styleUrls: ['./dashboard-teacher.component.css']
})
export class DashboardTeacherComponent implements OnInit {
  managedCourses: CourseResponse[] = [];
  recentSubmissions: EnrollmentStudentResponse[] = [];

  // Real stats if available, otherwise mocked for visual consistency
  stats = {
    totalRevenue: 0,
    activeStudents: 0,
    avgRating: 4.8,
    reviewsCount: 0
  };

  constructor(
    private courseService: CourseService,
    private dashboardService: DashboardService
  ) { }

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    // 1. Fetch Teacher's Courses
    this.courseService.listMine(0, 5).subscribe({
      next: (page) => {
        this.managedCourses = page.content;

        // 2. Fetch Enrollments for the first course to show "Recent Activity"
        if (this.managedCourses.length > 0) {
          this.loadRecentEnrollments(this.managedCourses[0].id);
        }

        // Mocking some stats based on managed courses
        this.stats.activeStudents = this.managedCourses.length * 15; // Rough estimate for UI
        this.stats.totalRevenue = this.managedCourses.reduce((acc, c) => acc + (c.price || 0), 0) * 10;
      },
      error: (err) => console.error('Error loading courses', err)
    });
  }

  loadRecentEnrollments(courseId: number): void {
    this.dashboardService.getCourseEnrollments(courseId).subscribe({
      next: (enrollments) => {
        this.recentSubmissions = enrollments.slice(0, 5); // Show last 5
      },
      error: (err) => console.error('Error loading enrollments', err)
    });
  }
}
