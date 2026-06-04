import { Component, OnInit } from '@angular/core';
import {
  CourseResponse,
  EnrollmentResponse,
  LessonProgressResponse,
  LessonResponse
} from '../../dtos/course.dto';
import { SubmissionResponse } from '../../dtos/assessment.dto';
import { CourseService } from '../../services/course.service';
import { ProgressService } from '../../services/progress.service';
import { AssessmentService } from '../../services/assessment.service';
import { AuthService } from '../../services/auth.service';
import { Page } from '../../models/page';
import { forkJoin, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

interface CourseLessonsSummary {
  courseId: number;
  lessons: LessonResponse[];
}

interface CourseProgressVm {
  progressPercent: number;
  completedLessons: number;
  totalLessons: number;
}

@Component({
  selector: 'app-dashboard-student',
  templateUrl: './dashboard-student.component.html',
  styleUrls: ['./dashboard-student.component.css']
})
export class DashboardStudentComponent implements OnInit {
  readonly maxCatalogCourses = 6;
  readonly maxEnrollmentsOnDashboard = 6;
  readonly enrollmentsPageSize = 200;

  studentName = 'Student';
  greeting = 'Hello';

  stats = {
    totalCourses: 0,
    learningTimeLabel: '0h 0m',
    coursesCompleted: 0,
    quizzesCompleted: 0
  };

  publishedCourses: CourseResponse[] = [];
  enrolledCourses: EnrollmentResponse[] = [];
  featuredEnrollments: EnrollmentResponse[] = [];

  courseProgress = new Map<number, CourseProgressVm>();

  loading = true;
  statsLoading = false;
  error = '';

  constructor(
    private courseService: CourseService,
    private progressService: ProgressService,
    private assessmentService: AssessmentService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.studentName = this.authService.getUserDisplayName();
    this.greeting = this.buildGreeting();
    this.loadDashboard();
  }

  private buildGreeting(): string {
    const h = new Date().getHours();
    if (h < 12) return 'Good morning';
    if (h < 18) return 'Good afternoon';
    return 'Good evening';
  }

  private emptyPage<T>(): Page<T> {
    return {
      content: [],
      totalElements: 0,
      totalPages: 0,
      size: 0,
      number: 0,
      first: true,
      last: true,
      empty: true
    };
  }

  private loadDashboard(): void {
    this.loading = true;
    this.error = '';

    forkJoin({
      published: this.courseService.listPublished(undefined, 0, this.maxCatalogCourses).pipe(
        catchError(() => of(this.emptyPage<CourseResponse>()))
      ),
      enrollments: this.courseService
        .listMyEnrollments(0, this.enrollmentsPageSize)
        .pipe(catchError(() => of(this.emptyPage<EnrollmentResponse>()))),
      submissions: this.assessmentService.listMySubmissions().pipe(catchError(() => of([] as SubmissionResponse[])))
    }).subscribe({
      next: ({ published, enrollments, submissions }) => {
        this.publishedCourses = published.content;
        this.enrolledCourses = enrollments.content;
        this.featuredEnrollments = enrollments.content.slice(0, this.maxEnrollmentsOnDashboard);
        this.stats.totalCourses = enrollments.totalElements;
        this.stats.quizzesCompleted = submissions.length;
        this.loading = false;
        this.computeStudentStats();
      },
      error: () => {
        this.loading = false;
        this.error = 'We could not load your dashboard. Please refresh the page.';
      }
    });
  }

  getCourseProgress(courseId: number): CourseProgressVm {
    return this.courseProgress.get(courseId) ?? { progressPercent: 0, completedLessons: 0, totalLessons: 0 };
  }

  trackByCourseId(_: number, e: EnrollmentResponse): number {
    return e.courseId;
  }

  trackByPublishedId(_: number, c: CourseResponse): number {
    return c.id;
  }

  private computeStudentStats(): void {
    if (this.enrolledCourses.length === 0) {
      this.stats.learningTimeLabel = '0h 0m';
      this.stats.coursesCompleted = 0;
      this.courseProgress.clear();
      return;
    }

    this.statsLoading = true;

    const courseLessonCalls = this.enrolledCourses.map((enrollment) =>
      this.courseService.listSectionsByCourse(enrollment.courseId).pipe(
        switchMap((sections) => {
          if (sections.length === 0) return of([] as LessonResponse[][]);
          const lessonCalls = sections.map((section) =>
            this.courseService.listLessonsBySection(section.id).pipe(catchError(() => of([] as LessonResponse[])))
          );
          return forkJoin(lessonCalls);
        }),
        map((lessonsBySection: LessonResponse[][]) => {
          const lessons = lessonsBySection.flat();
          return { courseId: enrollment.courseId, lessons } as CourseLessonsSummary;
        }),
        catchError(() => of({ courseId: enrollment.courseId, lessons: [] as LessonResponse[] } as CourseLessonsSummary))
      )
    );

    forkJoin(courseLessonCalls)
      .pipe(
        switchMap((courseLessons) =>
          this.progressService.myProgress().pipe(
            map((progressRows) => ({ courseLessons, progressRows })),
            catchError(() => of({ courseLessons, progressRows: [] as LessonProgressResponse[] }))
          )
        )
      )
      .subscribe({
        next: ({ courseLessons, progressRows }) => {
          const progressByLessonId = new Map<number, number>(
            progressRows.map((row) => [row.lessonId, row.progressPercent])
          );

          const progressMap = new Map<number, CourseProgressVm>();
          let coursesCompleted = 0;

          const totalMinutes = courseLessons.reduce((acc, course) => {
            const lessons = course.lessons;
            const total = lessons.length;
            if (!total) {
              progressMap.set(course.courseId, { progressPercent: 0, completedLessons: 0, totalLessons: 0 });
              return acc;
            }

            let sumPercent = 0;
            let completedCount = 0;
            for (const lesson of lessons) {
              const progress = progressByLessonId.get(lesson.id) || 0;
              sumPercent += progress;
              if (progress >= 100) completedCount++;
            }

            const progressPercent = Math.round(sumPercent / total);
            progressMap.set(course.courseId, {
              progressPercent,
              completedLessons: completedCount,
              totalLessons: total
            });

            if (completedCount === total) coursesCompleted++;

            return (
              acc +
              lessons.reduce((sum: number, lesson: LessonResponse) => {
                const progress = progressByLessonId.get(lesson.id) || 0;
                const estimated = lesson.estimatedMinutes || 0;
                return sum + Math.round((estimated * progress) / 100);
              }, 0)
            );
          }, 0);

          const hours = Math.floor(totalMinutes / 60);
          const minutes = totalMinutes % 60;
          this.stats.learningTimeLabel = `${hours}h ${minutes}m`;
          this.stats.coursesCompleted = coursesCompleted;
          this.courseProgress = progressMap;
          this.statsLoading = false;
        },
        error: () => {
          this.statsLoading = false;
        }
      });
  }
}
