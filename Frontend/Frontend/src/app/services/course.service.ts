import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  CourseCreateRequest,
  CourseResponse,
  CourseUpdateRequest,
  CourseSectionCreateRequest,
  CourseSectionResponse,
  LessonCreateRequest,
  LessonResponse,
  LessonMaterialCreateRequest,
  LessonMaterialResponse,
  EnrollmentResponse
} from '../dtos/course.dto';
import { Page } from '../models/page';

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  private readonly coursesUrl = `${environment.apiUrl}/courses`;
  private readonly sectionsUrl = `${environment.apiUrl}/sections`;
  private readonly lessonsUrl = `${environment.apiUrl}/lessons`;
  private readonly materialsUrl = `${environment.apiUrl}/lesson-materials`;
  private readonly enrollmentsUrl = `${environment.apiUrl}/enrollments`;

  constructor(private http: HttpClient) {}

  // --- Courses ---

  listPublished(categoryId?: number, page: number = 0, size: number = 10): Observable<Page<CourseResponse>> {
    let params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    if (categoryId) params = params.set('categoryId', categoryId.toString());
    return this.http.get<Page<CourseResponse>>(this.coursesUrl, { params });
  }

  searchPublished(keyword: string, page: number = 0, size: number = 10): Observable<Page<CourseResponse>> {
    const params = new HttpParams()
      .set('keyword', keyword)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<CourseResponse>>(`${this.coursesUrl}/search`, { params });
  }

  listByInstructor(instructorId: number, page: number = 0, size: number = 10): Observable<Page<CourseResponse>> {
    const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    return this.http.get<Page<CourseResponse>>(`${this.coursesUrl}/instructor/${instructorId}`, { params });
  }

  getById(id: number): Observable<CourseResponse> {
    return this.http.get<CourseResponse>(`${this.coursesUrl}/${id}`);
  }

  create(request: CourseCreateRequest): Observable<CourseResponse> {
    return this.http.post<CourseResponse>(this.coursesUrl, request);
  }

  update(id: number, request: CourseUpdateRequest): Observable<CourseResponse> {
    return this.http.put<CourseResponse>(`${this.coursesUrl}/${id}`, request);
  }

  deleteCourse(id: number): Observable<void> {
    return this.http.delete<void>(`${this.coursesUrl}/${id}`);
  }

  publish(id: number): Observable<CourseResponse> {
    return this.http.post<CourseResponse>(`${this.coursesUrl}/${id}/publish`, {});
  }

  unpublish(id: number): Observable<CourseResponse> {
    return this.http.post<CourseResponse>(`${this.coursesUrl}/${id}/unpublish`, {});
  }

  archive(id: number): Observable<CourseResponse> {
    return this.http.post<CourseResponse>(`${this.coursesUrl}/${id}/archive`, {});
  }

  listMine(page: number = 0, size: number = 10): Observable<Page<CourseResponse>> {
    const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    return this.http.get<Page<CourseResponse>>(`${this.coursesUrl}/me`, { params });
  }

  submitForApproval(id: number): Observable<CourseResponse> {
    return this.http.post<CourseResponse>(`${this.coursesUrl}/${id}/submit`, {});
  }

  listPending(page: number = 0, size: number = 10): Observable<Page<CourseResponse>> {
    const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    return this.http.get<Page<CourseResponse>>(`${this.coursesUrl}/pending`, { params });
  }

  approve(id: number): Observable<CourseResponse> {
    return this.http.post<CourseResponse>(`${this.coursesUrl}/${id}/approve`, {});
  }

  reject(id: number): Observable<CourseResponse> {
    return this.http.post<CourseResponse>(`${this.coursesUrl}/${id}/reject`, {});
  }

  // --- Sections ---

  createSection(request: CourseSectionCreateRequest): Observable<CourseSectionResponse> {
    return this.http.post<CourseSectionResponse>(this.sectionsUrl, request);
  }

  listSectionsByCourse(courseId: number): Observable<CourseSectionResponse[]> {
    return this.http.get<CourseSectionResponse[]>(`${this.sectionsUrl}/course/${courseId}`);
  }

  // --- Lessons ---

  createLesson(request: LessonCreateRequest): Observable<LessonResponse> {
    return this.http.post<LessonResponse>(this.lessonsUrl, request);
  }

  publishLesson(lessonId: number): Observable<LessonResponse> {
    return this.http.post<LessonResponse>(`${this.lessonsUrl}/${lessonId}/publish`, {});
  }

  unpublishLesson(lessonId: number): Observable<LessonResponse> {
    return this.http.post<LessonResponse>(`${this.lessonsUrl}/${lessonId}/unpublish`, {});
  }

  listLessonsBySection(sectionId: number): Observable<LessonResponse[]> {
    return this.http.get<LessonResponse[]>(`${this.lessonsUrl}/section/${sectionId}`);
  }

  // --- Materials ---

  createMaterial(formData: FormData): Observable<LessonMaterialResponse> {
    return this.http.post<LessonMaterialResponse>(this.materialsUrl, formData);
  }

  listMaterialsByLesson(lessonId: number): Observable<LessonMaterialResponse[]> {
    return this.http.get<LessonMaterialResponse[]>(`${this.materialsUrl}/lesson/${lessonId}`);
  }

  deleteMaterial(materialId: number): Observable<void> {
    return this.http.delete<void>(`${this.materialsUrl}/${materialId}`);
  }

  getLessonById(lessonId: number): Observable<LessonResponse> {
    return this.http.get<LessonResponse>(`${this.lessonsUrl}/${lessonId}`);
  }

  // --- Enrollments ---

  enroll(courseId: number): Observable<EnrollmentResponse> {
    return this.http.post<EnrollmentResponse>(`${this.enrollmentsUrl}/courses/${courseId}`, {});
  }

  unenroll(courseId: number): Observable<void> {
    return this.http.delete<void>(`${this.enrollmentsUrl}/courses/${courseId}`);
  }

  listMyEnrollments(page: number = 0, size: number = 10): Observable<Page<EnrollmentResponse>> {
    const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    return this.http.get<Page<EnrollmentResponse>>(`${this.enrollmentsUrl}/me`, { params });
  }

  checkEnrollment(courseId: number): Observable<boolean> {
    // A simple way to check if enrolled is to try to get the course's enrollment list (which might fail if not admin/owner)
    // or just fetch all my enrollments and find the ID. 
    // Given the API, let's just use listMyEnrollments and find it for now, or assume error 404/403 means not enrolled if there was a check endpoint.
    // For now, listMyEnrollments is safest.
    return new Observable<boolean>(observer => {
      this.listMyEnrollments(0, 100).subscribe({
        next: (page) => {
          const isEnrolled = page.content.some(e => e.courseId === courseId);
          observer.next(isEnrolled);
          observer.complete();
        },
        error: () => {
          observer.next(false);
          observer.complete();
        }
      });
    });
  }
}
