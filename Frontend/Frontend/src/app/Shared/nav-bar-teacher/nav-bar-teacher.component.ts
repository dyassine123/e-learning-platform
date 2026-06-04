import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { TEACHER_NAV_ITEMS, TeacherNavItem } from '../teacher-navigation';

@Component({
  selector: 'app-nav-bar-teacher',
  templateUrl: './nav-bar-teacher.component.html',
  styleUrls: ['./nav-bar-teacher.component.css']
})
export class NavBarTeacherComponent implements OnInit {
  fullName = 'Sarah Jenkins'; // Default/Mock
  role = 'Instructor';
  readonly quickLinks: TeacherNavItem[] = TEACHER_NAV_ITEMS
    .filter(i => i.enabled && i.section === 'main' && i.route !== '/teacher/dashboard');

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    const token = this.authService.getToken();
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.fullName = payload.fullName || payload.sub || this.fullName;
        this.role = payload.role || payload.roles?.[0] || this.role;
      } catch (e) {
        console.error('Error parsing token', e);
      }
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
