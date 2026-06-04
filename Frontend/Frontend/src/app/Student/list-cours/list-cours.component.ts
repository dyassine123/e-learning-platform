import { Component, OnInit } from '@angular/core';
import { CourseResponse } from '../../dtos/course.dto';
import { CourseService } from '../../services/course.service';

@Component({
  selector: 'app-list-cours',
  templateUrl: './list-cours.component.html',
  styleUrls: ['./list-cours.component.css']
})
export class ListCoursComponent implements OnInit {
  courses: CourseResponse[] = [];
  loading: boolean = true;
  searchKeyword: string = '';

  constructor(private courseService: CourseService) {}

  ngOnInit(): void {
    this.fetchCourses();
  }

  fetchCourses(): void {
    this.loading = true;
    this.courseService.listPublished(undefined, 0, 50).subscribe({
      next: (page) => {
        this.courses = page.content;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching courses', err);
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    if (!this.searchKeyword.trim()) {
      this.fetchCourses();
      return;
    }
    this.loading = true;
    this.courseService.searchPublished(this.searchKeyword, 0, 50).subscribe({
      next: (page) => {
        this.courses = page.content;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error searching courses', err);
        this.loading = false;
      }
    });
  }
}
