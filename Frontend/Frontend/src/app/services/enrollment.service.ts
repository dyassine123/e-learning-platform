import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { EnrollmentResponse } from '../dtos/enrollment.dto';
import { Page } from '../models/page';

@Injectable({
  providedIn: 'root'
})
export class EnrollmentService {
  private readonly apiUrl = `${environment.apiUrl}/enrollments`;

  constructor(private http: HttpClient) {}

  enroll(courseId: number): Observable<EnrollmentResponse> {
    return this.http.post<EnrollmentResponse>(`${this.apiUrl}/courses/${courseId}`, {});
  }

  unenroll(courseId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/courses/${courseId}`);
  }

  myEnrollments(page: number = 0, size: number = 10): Observable<Page<EnrollmentResponse>> {
    const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    return this.http.get<Page<EnrollmentResponse>>(`${this.apiUrl}/me`, { params });
  }

  getCourseEnrollments(courseId: number, page: number = 0, size: number = 10): Observable<Page<EnrollmentResponse>> {
    const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    return this.http.get<Page<EnrollmentResponse>>(`${this.apiUrl}/courses/${courseId}`, { params });
  }
}
