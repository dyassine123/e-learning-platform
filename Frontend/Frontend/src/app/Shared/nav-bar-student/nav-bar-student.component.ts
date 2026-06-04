import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { STUDENT_NAV_ITEMS, StudentNavItem } from '../student-navigation';

@Component({
  selector: 'app-nav-bar-student',
  templateUrl: './nav-bar-student.component.html'
})
export class NavBarStudentComponent implements OnInit {
  fullName = '';
  role = '';
  readonly quickLinks: StudentNavItem[] = STUDENT_NAV_ITEMS
    .filter(i => i.enabled && i.section === 'main' && i.route !== '/student/dashboard');

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    const token = this.authService.getToken();
    if (!token) {
      this.logout();
      return;
    }
    this.fullName = this.authService.getUserDisplayName();
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      this.role = payload.role || payload.roles?.[0] || '';
    } catch {
      this.logout();
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['']);
  }
}