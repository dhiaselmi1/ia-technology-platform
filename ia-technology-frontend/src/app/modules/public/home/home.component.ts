import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { ApiService } from '../../../core/services/api.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  private api = inject(ApiService);
  private http = inject(HttpClient);
  private router = inject(Router);
  private auth = inject(AuthService);

  private readonly BASE = 'http://localhost:8080/api';

  allPublications: any[] = [];
  news: any[] = [];
  domains: any[] = [];
  searchQuery = '';

  qaQuestion = '';
  qaResults: any[] = [];
  loadingQa = false;
  qaSearched = false;

  get isLoggedIn(): boolean {
    return this.auth.isLoggedIn();
  }

  ngOnInit(): void {
    this.api.get<any[]>('publications').subscribe({ next: (r) => this.allPublications = r || [] });
    this.api.get<any[]>('news').subscribe({ next: (r) => this.news = (r || []).sort((a: any, b: any) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()) });
    this.api.get<any[]>('domains').subscribe({ next: (r) => this.domains = (r || []).slice(0, 4) });
  }

  get featuredPublications(): any[] {
    return this.allPublications.filter(p => p.featured).slice(0, 6);
  }

  get featuredNewsList(): any[] {
    return this.news.filter(n => n.featured);
  }

  get mainFeatured(): any {
    return this.featuredNewsList[0] || null;
  }

  get otherFeatured(): any[] {
    return this.featuredNewsList.slice(1, 7);
  }

  onSearch(): void {
    if (this.searchQuery.trim()) {
      this.router.navigate(['/search'], { queryParams: { q: this.searchQuery } });
    }
  }

  askQuestion(): void {
    if (!this.qaQuestion.trim()) return;
    this.loadingQa = true;
    this.qaSearched = true;
    this.qaResults = [];
    this.http.post<any>(`${this.BASE}/ai/ask?question=${encodeURIComponent(this.qaQuestion)}&topN=3`, {}).subscribe({
      next: (res) => {
        const results = res?.results || [];
        this.qaResults = results.map((r: any) => {
          const pub = this.allPublications.find(p => p.id === r.id);
          return pub ? { ...pub, answer: r.answer, score: r.score } : null;
        }).filter((r: any) => r !== null);
        this.loadingQa = false;
      },
      error: () => { this.loadingQa = false; }
    });
  }

  getScorePercent(score: number): string {
    return Math.round(score * 100) + '%';
  }
}
