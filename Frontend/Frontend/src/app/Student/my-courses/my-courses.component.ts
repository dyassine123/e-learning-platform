import { Component, OnInit } from '@angular/core';
import { EnrollmentResponse, LessonProgressResponse, LessonResponse } from '../../dtos/course.dto';
import { CourseService } from '../../services/course.service';
import { ProgressService } from '../../services/progress.service';
import { Observable, forkJoin, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-my-courses',
  templateUrl: './my-courses.component.html',
  styleUrls: ['./my-courses.component.css']
})
export class MyCoursesComponent implements OnInit {
  enrolledCourses: (EnrollmentResponse & { progress: number })[] = [];
  filteredCourses: (EnrollmentResponse & { progress: number })[] = [];
  activeFilter: 'ALL' | 'IN_PROGRESS' | 'COMPLETED' = 'ALL';

  constructor(
    private courseService: CourseService,
    private progressService: ProgressService
  ) {}

  ngOnInit(): void {
    this.loadEnrollments();
  }

  loadEnrollments(): void {
    this.courseService.listMyEnrollments(0, 50).subscribe({
      next: (page) => {
        const enrollments = page.content;
        if (enrollments.length === 0) {
          this.enrolledCourses = [];
          return;
        }

        const localCompletedLessonIds = this.getLocalCompletedLessonIds();

        this.progressService.myProgress().pipe(
          switchMap((progressRows) => {
            const completedLessonIds = new Set<number>([
              ...localCompletedLessonIds,
              progressRows
                .filter((p: LessonProgressResponse) => p.completed || p.progressPercent >= 100)
                .map((p: LessonProgressResponse) => p.lessonId)
            ].flat());

            const courseCards$ = enrollments.map((enrollment) =>
              this.calculateCourseProgress(enrollment.courseId, completedLessonIds).pipe(
                map((progress) => ({ ...enrollment, progress })),
                catchError(() => of({ ...enrollment, progress: 0 }))
              )
            );

            return forkJoin(courseCards$);
          }),
          catchError(() => {
            const completedLessonIds = new Set<number>(localCompletedLessonIds);
            const courseCards$ = enrollments.map((enrollment) =>
              this.calculateCourseProgress(enrollment.courseId, completedLessonIds).pipe(
                map((progress) => ({ ...enrollment, progress })),
                catchError(() => of({ ...enrollment, progress: 0 }))
              )
            );
            return forkJoin(courseCards$);
          })
        ).subscribe((cards) => {
          this.enrolledCourses = cards;
          this.applyFilter();
        });
      },
      error: (err) => console.error('Error loading my courses', err)
    });
  }

  setFilter(filter: 'ALL' | 'IN_PROGRESS' | 'COMPLETED'): void {
    this.activeFilter = filter;
    this.applyFilter();
  }

  private applyFilter(): void {
    switch (this.activeFilter) {
      case 'IN_PROGRESS':
        this.filteredCourses = this.enrolledCourses.filter(c => c.progress > 0 && c.progress < 100);
        break;
      case 'COMPLETED':
        this.filteredCourses = this.enrolledCourses.filter(c => c.progress === 100);
        break;
      default:
        this.filteredCourses = [...this.enrolledCourses];
    }
  }

  private calculateCourseProgress(courseId: number, completedLessonIds: Set<number>): Observable<number> {
    return this.courseService.listSectionsByCourse(courseId).pipe(
      switchMap((sections) => {
        if (sections.length === 0) return of([] as LessonResponse[][]);
        const lessonCalls = sections.map((section) =>
          this.courseService.listLessonsBySection(section.id).pipe(catchError(() => of([] as LessonResponse[])))
        );
        return forkJoin(lessonCalls);
      }),
      map((lessonsBySection) => {
        const allLessons = lessonsBySection.flat();
        const totalLessons = allLessons.length;
        if (totalLessons === 0) return 0;
        const completed = allLessons.filter((lesson) => completedLessonIds.has(lesson.id)).length;
        return Math.round((completed / totalLessons) * 100);
      }),
      catchError(() => of(0))
    );
  }

  private getLocalCompletedLessonIds(): number[] {
    try {
      const raw = localStorage.getItem('completedLessonIds');
      if (!raw) return [];
      const ids = JSON.parse(raw);
      return Array.isArray(ids) ? ids.filter((id) => typeof id === 'number') : [];
    } catch {
      return [];
    }
  }
}
