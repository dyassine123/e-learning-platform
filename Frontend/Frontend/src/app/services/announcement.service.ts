import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AnnouncementCreateRequest, AnnouncementResponse } from '../dtos/announcement.dto';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService {
  private readonly apiUrl = `${environment.apiUrl}/announcements`;

  constructor(private http: HttpClient) {}

  create(request: AnnouncementCreateRequest): Observable<AnnouncementResponse> {
    return this.http.post<AnnouncementResponse>(this.apiUrl, request);
  }

  listForCourse(courseId: number): Observable<AnnouncementResponse[]> {
    return this.http.get<AnnouncementResponse[]>(`${this.apiUrl}/course/${courseId}`);
  }

  listMyAnnouncements(): Observable<AnnouncementResponse[]> {
    return this.http.get<AnnouncementResponse[]>(`${this.apiUrl}/me`);
  }
}
