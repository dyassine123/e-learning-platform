import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { LessonProgressResponse, LessonProgressUpdateRequest } from '../dtos/course.dto';

@Injectable({
  providedIn: 'root'
})
export class ProgressService {
  private readonly apiUrl = `${environment.apiUrl}/lesson-progress`;

  constructor(private http: HttpClient) {}

  updateProgress(request: LessonProgressUpdateRequest): Observable<LessonProgressResponse> {
    return this.http.post<LessonProgressResponse>(this.apiUrl, request);
  }

  myProgress(): Observable<LessonProgressResponse[]> {
    return this.http.get<LessonProgressResponse[]>(`${this.apiUrl}/me`);
  }
}
