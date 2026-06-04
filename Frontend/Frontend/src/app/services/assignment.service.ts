import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  AssignmentCreateRequest,
  AssignmentResponse,
  AssignmentSubmissionRequest,
  AssignmentSubmissionResponse,
  GradeSubmissionRequest
} from '../dtos/assignment.dto';

@Injectable({
  providedIn: 'root'
})
export class AssignmentService {
  private readonly apiUrl = `${environment.apiUrl}/assignments`;

  constructor(private http: HttpClient) {}

  create(request: AssignmentCreateRequest): Observable<AssignmentResponse> {
    return this.http.post<AssignmentResponse>(this.apiUrl, request);
  }

  publish(assignmentId: number): Observable<AssignmentResponse> {
    return this.http.post<AssignmentResponse>(`${this.apiUrl}/${assignmentId}/publish`, {});
  }

  listForCourse(courseId: number): Observable<AssignmentResponse[]> {
    return this.http.get<AssignmentResponse[]>(`${this.apiUrl}/course/${courseId}`);
  }

  submit(assignmentId: number, request: AssignmentSubmissionRequest): Observable<AssignmentSubmissionResponse> {
    return this.http.post<AssignmentSubmissionResponse>(`${this.apiUrl}/${assignmentId}/submit`, request);
  }

  listSubmissions(assignmentId: number): Observable<AssignmentSubmissionResponse[]> {
    return this.http.get<AssignmentSubmissionResponse[]>(`${this.apiUrl}/${assignmentId}/submissions`);
  }

  grade(submissionId: number, request: GradeSubmissionRequest): Observable<AssignmentSubmissionResponse> {
    return this.http.post<AssignmentSubmissionResponse>(`${this.apiUrl}/submissions/${submissionId}/grade`, request);
  }

  listMySubmissions(courseId?: number): Observable<AssignmentSubmissionResponse[]> {
    let params = new HttpParams();
    if (courseId) params = params.set('courseId', courseId.toString());
    return this.http.get<AssignmentSubmissionResponse[]>(`${this.apiUrl}/submissions/me`, { params });
  }

  listPendingSubmissions(courseId: number): Observable<AssignmentSubmissionResponse[]> {
    return this.http.get<AssignmentSubmissionResponse[]>(`${this.apiUrl}/course/${courseId}/submissions/pending`);
  }
}
