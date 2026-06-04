import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ReviewCreateRequest, ReviewResponse, ReviewSummaryResponse } from '../dtos/review.dto';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private apiUrl(courseId: number): string {
    return `${environment.apiUrl}/courses/${courseId}/reviews`;
  }

  constructor(private http: HttpClient) {}

  create(courseId: number, request: ReviewCreateRequest): Observable<ReviewResponse> {
    return this.http.post<ReviewResponse>(this.apiUrl(courseId), request);
  }

  list(courseId: number): Observable<ReviewResponse[]> {
    return this.http.get<ReviewResponse[]>(this.apiUrl(courseId));
  }

  summary(courseId: number): Observable<ReviewSummaryResponse> {
    return this.http.get<ReviewSummaryResponse>(`${this.apiUrl(courseId)}/summary`);
  }

  getMyReview(courseId: number): Observable<ReviewResponse> {
    return this.http.get<ReviewResponse>(`${this.apiUrl(courseId)}/me`);
  }

  update(courseId: number, request: ReviewCreateRequest): Observable<ReviewResponse> {
    return this.http.put<ReviewResponse>(`${this.apiUrl(courseId)}/me`, request);
  }

  delete(courseId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl(courseId)}/me`);
  }
}
