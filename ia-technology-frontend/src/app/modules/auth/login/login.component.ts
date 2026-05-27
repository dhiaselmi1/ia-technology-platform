import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);

  email = '';
  password = '';
  rememberMe = false;
  showPassword = false;
  loading = false;
  error = '';

  ngOnInit(): void {
    const saved = localStorage.getItem('iat_remember');
    if (saved) {
      const data = JSON.parse(saved);
      this.email = data.email || '';
      this.password = data.password || '';
      this.rememberMe = true;
    }
  }

  onSubmit(): void {
    if (!this.email || !this.password) return;
    this.loading = true;
    this.error = '';

    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: () => {
        if (this.rememberMe) {
          localStorage.setItem('iat_remember', JSON.stringify({ email: this.email, password: this.password }));
        } else {
          localStorage.removeItem('iat_remember');
        }
        this.loading = false;
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Email ou mot de passe incorrect';
      }
    });
  }
}
