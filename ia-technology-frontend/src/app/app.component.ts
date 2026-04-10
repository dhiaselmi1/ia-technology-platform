import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, Router } from '@angular/router';
import { ToolbarModule } from 'primeng/toolbar';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, ToolbarModule, ButtonModule, MenuModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  authService = inject(AuthService);
  private router = inject(Router);

  get isLoggedIn() { return this.authService.isLoggedIn(); }
  get userRole() { return this.authService.getUserRole(); }
  get username() { return this.authService.getUsername(); }

  logout(): void {
    this.authService.logout();
  }
}
