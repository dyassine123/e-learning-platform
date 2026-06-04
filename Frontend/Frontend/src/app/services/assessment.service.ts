import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  QuizCreateRequest,
  QuizResponse,
  QuizQuestionResponse,
  QuestionCreateRequest,
  SubmissionRequest,
  SubmissionResponse
} from '../dtos/assessment.dto';

@Injectable({
  providedIn: 'root'
})
export class AssessmentService {
  private readonly quizzesUrl = `${environment.apiUrl}/quizzes`;
  private readonly submissionsUrl = `${environment.apiUrl}/submissions`;

  constructor(private http: HttpClient) {}

  // --- Quizzes ---

  createQuiz(request: QuizCreateRequest): Observable<QuizResponse> {
    return this.http.post<QuizResponse>(this.quizzesUrl, request);
  }

  publishQuiz(quizId: number): Observable<QuizResponse> {
    return this.http.post<QuizResponse>(`${this.quizzesUrl}/${quizId}/publish`, {});
  }

  addQuestion(request: QuestionCreateRequest): Observable<void> {
    return this.http.post<void>(`${this.quizzesUrl}/questions`, request);
  }

  listQuizzesForCourse(courseId: number): Observable<QuizResponse[]> {
    return this.http.get<QuizResponse[]>(`${this.quizzesUrl}/course/${courseId}`);
  }

  listQuestionsForQuiz(quizId: number): Observable<QuizQuestionResponse[]> {
    return this.http.get<QuizQuestionResponse[]>(`${this.quizzesUrl}/${quizId}/questions`);
  }

  // --- Submissions ---

  submitQuiz(request: SubmissionRequest): Observable<SubmissionResponse> {
    return this.http.post<SubmissionResponse>(this.submissionsUrl, request);
  }

  listMySubmissions(courseId?: number): Observable<SubmissionResponse[]> {
    let params = new HttpParams();
    if (courseId) params = params.set('courseId', courseId.toString());
    return this.http.get<SubmissionResponse[]>(`${this.submissionsUrl}/me`, { params });
  }
}
