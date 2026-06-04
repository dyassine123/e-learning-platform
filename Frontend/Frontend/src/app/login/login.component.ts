import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email ="";
  password ="";
  errorMessage ="";
  loading = false ;

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  on_submit() : void 
   {
    if(!this.email || !this.password) {
      this.errorMessage = "Please fill in all fields";
      return;
    }
    this.loading = true;
    this.errorMessage = "";
    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: () => {
        const  role = this.getRoleFromToken();
        this.redirectByRole(role);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || "Login failed. Please try again.";
        this.loading = false;
      }
    });
  }

  private getRoleFromToken(): string | null {
    const token = this.authService.getToken();
    if (!token) return null;
    
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.role || payload.roles?.[0] || '';
    } catch (e) {
      console.error("Failed to parse token:", e);
      return null;
    }
  }
  private redirectByRole(role: string | null): void {
    const next = this.route.snapshot.queryParamMap.get('next');
    if (next) {
      this.router.navigateByUrl(next);
      return;
    }
    
    const targetRoute = this.authService.getHomeRouteByRole(role);
    this.router.navigate([targetRoute]);
  }

 }
