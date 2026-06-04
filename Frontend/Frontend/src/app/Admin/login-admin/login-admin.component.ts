import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login-admin',
  templateUrl: './login-admin.component.html',
  styleUrls: ['./login-admin.component.css']
})
export class LoginAdminComponent {
  email = "";
  password = "";
  errorMessage = "";
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  on_submit(): void {
    if (!this.email || !this.password) {
      this.errorMessage = "Please enter both administrator email and secret key.";
      return;
    }

    this.loading = true;
    this.errorMessage = "";

    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: () => {
        const role = this.authService.getUserRole();
        if (role === 'ADMIN') {
          this.router.navigate(['/admin/dashboard']);
        } else {
          // If a non-admin tries to log in through the admin portal
          this.errorMessage = "Access denied. This portal is for administrators only.";
          this.authService.logout();
          this.loading = false;
        }
      },
      error: (err) => {
        this.errorMessage = err.error?.message || "Authentication failed. Please check your credentials.";
        this.loading = false;
      }
    });
  }
}
