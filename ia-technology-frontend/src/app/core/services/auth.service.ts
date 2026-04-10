import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';

export interface LoginRequest   { email: string; password: string; }
export interface RegisterRequest { username: string; email: string; password: string; }
export interface AuthResponse   { token: string; role: string; username: string; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly API       = 'http://localhost:8080/api/auth';
  private readonly TOKEN_KEY = 'iat_token';
  private readonly USER_KEY  = 'iat_user';

  isAuthenticated = signal<boolean>(this.hasToken());

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API}/login`, credentials).pipe(
      tap(res => this.saveSession(res))
    );
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API}/register`, data).pipe(
      tap(res => this.saveSession(res))
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.isAuthenticated.set(false);
    this.router.navigate(['/auth/login']);
  }

  getToken(): string | null        { return localStorage.getItem(this.TOKEN_KEY); }
  isLoggedIn(): boolean            { return !!this.getToken(); }

  getUserRole(): string {
    const u = localStorage.getItem(this.USER_KEY);
    return u ? JSON.parse(u).role : '';
  }

  getUsername(): string {
    const u = localStorage.getItem(this.USER_KEY);
    return u ? JSON.parse(u).username : '';
  }

  private hasToken(): boolean { return !!localStorage.getItem(this.TOKEN_KEY); }

  private saveSession(res: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, res.token);
    localStorage.setItem(this.USER_KEY, JSON.stringify({ role: res.role, username: res.username }));
    this.isAuthenticated.set(true);
  }
}
