import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree {
    const expectedRole = String(route.data['role'] || '').toUpperCase();
    const currentRole = this.authService.getUserRole();

    if (!this.authService.isLoggedIn() || !currentRole) {
      return this.router.createUrlTree(['/login'], { queryParams: { next: state.url } });
    }

    if (currentRole !== expectedRole) {
      return this.router.createUrlTree([this.authService.getHomeRouteByRole(currentRole)]);
    }

    return true;
  }
}
