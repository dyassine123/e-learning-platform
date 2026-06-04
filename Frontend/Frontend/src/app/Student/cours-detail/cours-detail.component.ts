import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CourseResponse, CourseSectionResponse, LessonResponse } from '../../dtos/course.dto';
import { CourseService } from '../../services/course.service';
import { AuthService } from '../../services/auth.service';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

interface StudentSection extends CourseSectionResponse {
  lessons: LessonResponse[];
  expanded: boolean;
}

@Component({
  selector: 'app-cours-detail',
  templateUrl: './cours-detail.component.html',
  styleUrls: ['./cours-detail.component.css']
})
export class CoursDetailComponent implements OnInit {
  courseId!: number;
  course?: CourseResponse;
  sections: StudentSection[] = [];
  isEnrolled: boolean = false;
  loading: boolean = true;
  enrolling = false;
  paymentOpen = false;
  paymentError = '';
  paymentSuccess = '';
  readonly paymentForm = {
    cardholderName: '',
    cardNumber: '',
    expiry: '',
    cvv: '',
    email: '',
    billingAddress: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private courseService: CourseService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.courseId = +idParam;
      this.loadCourseDetails();
    }
  }

  loadCourseDetails(): void {
    this.loading = true;
    this.courseService.getById(this.courseId).subscribe({
      next: (course) => {
        this.course = course;
        this.loadCurriculum();
        this.checkEnrollment();
      },
      error: (err) => {
        console.error('Error loading course', err);
        this.loading = false;
      }
    });
  }

  loadCurriculum(): void {
    this.courseService.listSectionsByCourse(this.courseId).subscribe({
      next: (sections) => {
        if (sections.length === 0) {
          this.sections = [];
          this.loading = false;
          return;
        }

        const lessonRequests = sections.map(section =>
          this.courseService.listLessonsBySection(section.id).pipe(
            map(lessons => ({
              ...section,
              expanded: false,
              lessons: lessons.sort((a, b) => a.displayOrder - b.displayOrder)
            })),
            catchError(() => of({
              ...section,
              expanded: false,
              lessons: []
            }))
          )
        );

        forkJoin(lessonRequests).subscribe({
          next: (sectionsWithLessons) => {
            this.sections = sectionsWithLessons.sort((a, b) => a.displayOrder - b.displayOrder);
            if (this.sections.length > 0) {
              this.sections[0].expanded = true;
            }
            this.loading = false;
          },
          error: (err) => {
            console.error('Error loading curriculum', err);
            this.loading = false;
          }
        });
      },
      error: (err) => {
        console.error('Error loading sections', err);
        this.loading = false;
      }
    });
  }

  checkEnrollment(): void {
    if (this.authService.isLoggedIn()) {
      this.courseService.checkEnrollment(this.courseId).subscribe(enrolled => {
        this.isEnrolled = enrolled;
      });
    }
  }

  enroll(): void {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url } });
      return;
    }

    if (!this.course?.free) {
      this.paymentOpen = true;
      this.paymentError = '';
      this.paymentSuccess = '';
      return;
    }

    this.completeEnrollment();
  }

  submitPayment(): void {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url } });
      return;
    }

    this.paymentError = '';
    this.paymentSuccess = '';

    if (!this.isPaymentFormValid()) {
      return;
    }

    this.paymentSuccess = 'Payment information accepted.';
    this.completeEnrollment();
  }

  completeEnrollment(): void {
    this.enrolling = true;
    this.courseService.enroll(this.courseId).subscribe({
      next: () => {
        this.isEnrolled = true;
        this.enrolling = false;
        this.paymentOpen = false;
        this.paymentError = '';
        this.paymentSuccess = this.course?.free ? '' : 'Payment completed and enrollment confirmed.';
      },
      error: (err) => {
        this.enrolling = false;
        console.error('Enrollment failed', err);
        this.paymentError = err?.error?.message || 'Failed to enroll. Please try again.';
      }
    });
  }

  toggleSection(sectionId: number): void {
    this.sections = this.sections.map(section =>
      section.id === sectionId
        ? { ...section, expanded: !section.expanded }
        : section
    );
  }

  get totalLessons(): number {
    return this.sections.reduce((count, section) => count + section.lessons.length, 0);
  }

  get totalMinutes(): number {
    return this.sections.reduce(
      (count, section) => count + section.lessons.reduce((sum, lesson) => sum + (lesson.estimatedMinutes || 0), 0),
      0
    );
  }

  get firstLessonId(): number | null {
    return this.sections.flatMap(section => section.lessons)[0]?.id ?? null;
  }

  private isPaymentFormValid(): boolean {
    const number = this.paymentForm.cardNumber.replace(/\s+/g, '');
    const expiry = this.paymentForm.expiry.trim();
    const cvv = this.paymentForm.cvv.trim();

    if (!this.paymentForm.cardholderName.trim()) { this.paymentError = 'Cardholder name is required.'; return false; }
    if (!this.paymentForm.email.trim()) { this.paymentError = 'Billing email is required.'; return false; }
    if (!/^\d{16}$/.test(number)) { this.paymentError = 'Card number must be 16 digits.'; return false; }
    if (!/^(0[1-9]|1[0-2])\/\d{2}$/.test(expiry)) { this.paymentError = 'Expiry must be in MM/YY format (e.g. 12/25).'; return false; }
    if (!/^\d{3,4}$/.test(cvv)) { this.paymentError = 'CVV must be 3 or 4 digits.'; return false; }
    if (!this.paymentForm.billingAddress.trim()) { this.paymentError = 'Billing address is required.'; return false; }

    return true;
  }
}
