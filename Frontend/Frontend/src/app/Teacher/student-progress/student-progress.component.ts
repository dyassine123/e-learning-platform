import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CourseService } from '../../services/course.service';
import { DashboardService } from '../../services/dashboard.service';
import { CourseStudentLessonProgressResponse, StudentProgressRowResponse } from '../../dtos/dashboard.dto';
import { CourseResponse } from '../../dtos/course.dto';

@Component({
  selector: 'app-student-progress',
  templateUrl: './student-progress.component.html',
  styleUrls: ['./student-progress.component.css']
})
export class StudentProgressComponent implements OnInit {
  managedCourses: CourseResponse[] = [];
  selectedCourseId?: number;
  studentRows: StudentProgressRowResponse[] = [];
  selectedStudentId?: number;
  progressData?: CourseStudentLessonProgressResponse;
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private courseService: CourseService,
    private dashboardService: DashboardService
  ) {}

  ngOnInit(): void {
    this.loadInitialData();
  }

  loadInitialData(): void {
    this.loading = true;
    this.courseService.listMine(0, 100).subscribe({
      next: (res) => {
        this.managedCourses = res.content;
        if (this.managedCourses.length > 0) {
          const requestedCourseId = Number(this.route.snapshot.queryParamMap.get('courseId'));
          const isValidRequested = this.managedCourses.some(c => c.id === requestedCourseId);
          this.onCourseSelect(isValidRequested ? requestedCourseId : this.managedCourses[0].id);
        } else {
          this.loading = false;
        }
      },
      error: (err) => {
        console.error('Error loading courses', err);
        this.loading = false;
      }
    });
  }

  onCourseSelect(courseId: number): void {
    this.loading = true;
    this.selectedCourseId = courseId;
    this.dashboardService.getCourseLessonProgress(courseId).subscribe({
      next: (data) => {
        this.progressData = data;
        this.studentRows = data.students;
        this.selectedStudentId = this.studentRows[0]?.studentId;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading student progress', err);
        this.studentRows = [];
        this.progressData = undefined;
        this.loading = false;
      }
    });
  }

  selectStudent(studentId: number): void {
    this.selectedStudentId = studentId;
  }

  get selectedStudent(): StudentProgressRowResponse | undefined {
    return this.studentRows.find(s => s.studentId === this.selectedStudentId);
  }
}
