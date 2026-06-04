import { Component, OnInit } from '@angular/core';
import { EnrollmentResponse } from '../../dtos/course.dto';
import { CourseService } from '../../services/course.service';

@Component({
  selector: 'app-list-enrollement',
  templateUrl: './list-enrollement.component.html',
  styleUrls: ['./list-enrollement.component.css']
})
export class ListEnrollementComponent implements OnInit {
  enrollments: (EnrollmentResponse & { progress: number })[] = [];
  loading: boolean = true;

  constructor(private courseService: CourseService) {}

  ngOnInit(): void {
    this.fetchEnrollments();
  }

  fetchEnrollments(): void {
    this.loading = true;
    this.courseService.listMyEnrollments(0, 50).subscribe({
      next: (page) => {
        this.enrollments = page.content.map(e => ({
          ...e,
          progress: 0 // Default progress until tracking is implemented
        }));
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching enrollments', err);
        this.loading = false;
      }
    });
  }
}
