import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { ApiService } from '../../../core/services/api.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-publication-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './publication-detail.component.html',
  styleUrl: './publication-detail.component.scss'
})
export class PublicationDetailComponent implements OnInit {
  private api = inject(ApiService);
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);
  private auth = inject(AuthService);

  private readonly BASE = 'http://localhost:8080/api';

  publication: any = null;
  loading = true;

  keywords: string[] = [];
  loadingKeywords = false;

  recommendations: any[] = [];
  loadingRecommendations = false;

  get isLoggedIn(): boolean {
    return this.auth.isLoggedIn();
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) this.loadPublication(+params['id']);
    });
  }

  loadPublication(id: number): void {
    this.loading = true;
    this.api.get<any>(`publications/${id}`).subscribe({
      next: (res) => {
        this.publication = res;
        this.loading = false;
        if (this.isLoggedIn) {
          this.extractKeywords();
          this.loadRecommendations(id);
        }
      },
      error: () => this.loading = false
    });
  }

  extractKeywords(): void {
    const text = (this.publication.title || '') + ' ' + (this.publication.abstractText || this.publication.abstract_ || '');
    if (!text.trim()) return;
    this.loadingKeywords = true;
    this.http.post<any>(`${this.BASE}/ai/keywords?text=${encodeURIComponent(text)}&n=8`, {}).subscribe({
      next: (res) => {
        this.keywords = res?.keywords || [];
        this.loadingKeywords = false;
      },
      error: () => this.loadingKeywords = false
    });
  }

  loadRecommendations(id: number): void {
    this.loadingRecommendations = true;
    this.api.get<any>(`ai/recommend/${id}`, { topN: '5' }).subscribe({
      next: (res) => {
        this.recommendations = (res?.recommendations || []).filter((r: any) => r.id !== this.publication.id);
        this.loadingRecommendations = false;
      },
      error: () => this.loadingRecommendations = false
    });
  }

  summaryText = '';
  summaryMode = '';
  summaryError = '';
  loadingSummary = false;

  summarize(mode: string): void {
    const text = (this.publication.abstractText || this.publication.abstract_ || '');
    if (!text.trim()) return;
    this.loadingSummary = true;
    this.summaryMode = mode;
    this.summaryText = '';
    this.summaryError = '';
    this.http.post<any>(`${this.BASE}/ai/summarize?text=${encodeURIComponent(text)}&mode=${mode}`, {}).subscribe({
      next: (res) => {
        this.summaryText = res?.summary || '';
        if (!this.summaryText && res?.error) this.summaryError = res.error;
        this.loadingSummary = false;
      },
      error: (err) => {
        this.summaryError = err.error?.detail || 'Service de résumé indisponible (clé Groq requise)';
        this.loadingSummary = false;
      }
    });
  }

  get pdfUrl(): string | null {
    if (!this.publication?.filePath) return null;
    return `http://localhost:8080/uploads/${this.publication.filePath}`;
  }

  getSimilarityPercent(score: number): string {
    return Math.round(score * 100) + '%';
  }
}
