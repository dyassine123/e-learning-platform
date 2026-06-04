import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { Role } from '../models/Role';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  firstName = "";
  lastName = ""
  email = "";
  password = ""
  confirmPassword = "";
  role: Role | "" = "";
  errorMessage = "";
  loading = false;
  constructor(private authService: AuthService, private router: Router) { }
  on_submit(): void {
    if (!this.firstName || !this.lastName || !this.email || !this.password || !this.confirmPassword || !this.role) {
      this.errorMessage = "Please fill in all fields";
      return;
    }
    if (this.password !== this.confirmPassword) {
      this.errorMessage = "Passwords do not match";
      return;
    }
    this.loading = true;
    this.errorMessage = "";
    const registrationData = {
      fullName: `${this.firstName} ${this.lastName}`.trim(),
      email: this.email,
      password: this.password,
      role: this.role as Role
    }
    this.authService.register(registrationData).subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || "Registration failed. Please try again.";
        this.loading = false;
      }
    });
  }
}

