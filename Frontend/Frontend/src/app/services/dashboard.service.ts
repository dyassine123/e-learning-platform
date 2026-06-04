import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  EnrollmentStudentResponse,
  CourseStudentLessonProgressResponse,
  QuizSubmissionSummaryResponse,
  QuizSubmissionDetailResponse
} from '../dtos/dashboard.dto';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly apiUrl = `${environment.apiUrl}/dashboard`;

  constructor(private http: HttpClient) {}

  getCourseEnrollments(courseId: number): Observable<EnrollmentStudentResponse[]> {
    return this.http.get<EnrollmentStudentResponse[]>(`${this.apiUrl}/courses/${courseId}/enrollments`);
  }

  getCourseLessonProgress(courseId: number): Observable<CourseStudentLessonProgressResponse> {
    return this.http.get<CourseStudentLessonProgressResponse>(`${this.apiUrl}/courses/${courseId}/lesson-progress`);
  }

  getQuizSubmissions(quizId: number): Observable<QuizSubmissionSummaryResponse[]> {
    return this.http.get<QuizSubmissionSummaryResponse[]>(`${this.apiUrl}/quizzes/${quizId}/submissions`);
  }

  getSubmissionDetail(submissionId: number): Observable<QuizSubmissionDetailResponse> {
    return this.http.get<QuizSubmissionDetailResponse>(`${this.apiUrl}/submissions/${submissionId}`);
  }
}
