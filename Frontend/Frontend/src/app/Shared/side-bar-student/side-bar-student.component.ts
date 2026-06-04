import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { STUDENT_NAV_ITEMS, StudentNavItem } from '../student-navigation';

@Component({
  selector: 'app-side-bar-student',
  templateUrl: './side-bar-student.component.html',
  styleUrls: ['./side-bar-student.component.css']
})
export class SideBarStudentComponent {
  readonly mainItems: StudentNavItem[] = STUDENT_NAV_ITEMS.filter(i => i.section === 'main' && i.enabled);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
