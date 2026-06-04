import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest } from '../dtos/auth.dto';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient, private router: Router) { }

  register(request: RegisterRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/register`, request);
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(response => this.saveToken(response.accessToken))
    );
  }

  logout(): void {
    localStorage.removeItem('access_token');
    this.router.navigate(['/']).then(() => {
      window.location.reload(); // Ensure all state is cleared
    });
  }

  getToken(): string | null {
    return localStorage.getItem('access_token');
  }

  private saveToken(token: string): void {
    localStorage.setItem('access_token', token);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getUserRole(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const role = payload.role || payload.roles?.[0];
      return role ? String(role).toUpperCase() : null;
    } catch {
      return null;
    }
  }

  /** Display name from JWT (same claims as nav bar). */
  getUserDisplayName(): string {
    const token = this.getToken();
    if (!token) return 'Student';
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const name = payload.fullName || payload.sub;
      return name ? String(name) : 'Student';
    } catch {
      return 'Student';
    }
  }

  getHomeRouteByRole(role?: string | null): string {
    const normalizedRole = (role || this.getUserRole() || '').toUpperCase();
    switch (normalizedRole) {
      case 'ADMIN':
        return '/admin/dashboard';
      case 'TEACHER':
        return '/teacher/dashboard';
      case 'STUDENT':
        return '/student/dashboard';
      default:
        return '/';
    }
  }
}
