import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive, Router, NavigationEnd } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  authService = inject(AuthService);
  private router = inject(Router);

  isAdminRoute = false;
  isModeratorRoute = false;
  isAuthRoute = false;
  isUserRoute = false;
  currentUrl = '';

  constructor() {
    this.router.events.pipe(
      filter((e): e is NavigationEnd => e instanceof NavigationEnd)
    ).subscribe(e => {
      this.currentUrl = e.urlAfterRedirects;
      this.isAdminRoute = this.currentUrl.startsWith('/admin');
      this.isModeratorRoute = this.currentUrl.startsWith('/moderator');
      this.isAuthRoute = this.currentUrl.startsWith('/auth');
      this.isUserRoute = this.currentUrl.startsWith('/user');
    });
  }

  get isLoggedIn() { return this.authService.isLoggedIn(); }
  get userRole() { return this.authService.getUserRole(); }
  get username() { return this.authService.getUsername(); }
  get initials() {
    const name = this.username;
    if (!name) return '?';
    const parts = name.split(' ');
    return parts.length > 1
      ? (parts[0][0] + parts[1][0]).toUpperCase()
      : name.substring(0, 2).toUpperCase();
  }
  get showSidebar() {
    if (this.isAdminRoute || this.isModeratorRoute) return true;
    if (this.isUserRoute && (this.userRole === 'ADMIN' || this.userRole === 'MODERATEUR')) return true;
    return false;
  }

  get sidebarContext(): string {
    if (this.isAdminRoute || (this.isUserRoute && this.userRole === 'ADMIN')) return 'admin';
    if (this.isModeratorRoute || (this.isUserRoute && this.userRole === 'MODERATEUR')) return 'moderator';
    return '';
  }

  logout(): void {
    this.authService.logout();
  }

  navigateTo(path: string): void {
    this.router.navigate([path]);
  }
}
