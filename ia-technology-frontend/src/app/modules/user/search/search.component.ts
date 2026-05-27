import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { ApiService } from '../../../core/services/api.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './search.component.html',
  styleUrl: './search.component.scss'
})
export class SearchComponent implements OnInit {
  private api = inject(ApiService);
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);
  private auth = inject(AuthService);

  private readonly BASE = 'http://localhost:8080/api';

  searchQuery = '';
  publications: any[] = [];
  allPublications: any[] = [];
  domains: any[] = [];
  selectedDomains: Set<number> = new Set();
  resultCount = 0;

  searchMode: 'classic' | 'semantic' | 'qa' = 'classic';
  semanticResults: any[] = [];
  qaResults: any[] = [];
  loadingAi = false;

  get isLoggedIn(): boolean {
    return this.auth.isLoggedIn();
  }

  ngOnInit(): void {
    this.api.get<any[]>('domains').subscribe(r => this.domains = r || []);
    this.api.get<any[]>('publications').subscribe(r => {
      this.allPublications = r || [];
      this.publications = this.allPublications;
      this.resultCount = this.publications.length;
    });
    this.route.queryParams.subscribe(p => {
      if (p['q']) { this.searchQuery = p['q']; this.performSearch(); }
    });
  }

  setMode(mode: 'classic' | 'semantic' | 'qa'): void {
    this.searchMode = mode;
    this.semanticResults = [];
    this.qaResults = [];
    if (this.searchQuery.trim()) this.performSearch();
  }

  performSearch(): void {
    if (this.searchMode === 'semantic' && this.isLoggedIn) {
      this.semanticSearch();
      return;
    }
    if (this.searchMode === 'qa' && this.isLoggedIn) {
      this.askQuestion();
      return;
    }

    let results = this.allPublications;
    if (this.searchQuery.trim()) {
      const q = this.searchQuery.toLowerCase();
      results = results.filter(p =>
        p.title?.toLowerCase().includes(q) ||
        (p.abstractText || p.abstract_ || '').toLowerCase().includes(q) ||
        p.researcherName?.toLowerCase().includes(q)
      );
    }
    if (this.selectedDomains.size > 0) {
      results = results.filter(p => this.selectedDomains.has(p.domainId));
    }
    this.publications = results;
    this.resultCount = results.length;
  }

  semanticSearch(): void {
    if (!this.searchQuery.trim()) return;
    this.loadingAi = true;
    this.semanticResults = [];
    this.http.post<any>(`${this.BASE}/ai/semantic-search?query=${encodeURIComponent(this.searchQuery)}&topN=10`, {}).subscribe({
      next: (res) => {
        const resultIds = (res?.results || []).map((r: any) => ({ id: r.id, score: r.score }));
        this.semanticResults = resultIds.map((r: any) => {
          const pub = this.allPublications.find(p => p.id === r.id);
          return pub ? { ...pub, similarityScore: r.score } : null;
        }).filter((r: any) => r !== null);
        this.resultCount = this.semanticResults.length;
        this.loadingAi = false;
      },
      error: () => { this.loadingAi = false; }
    });
  }

  askQuestion(): void {
    if (!this.searchQuery.trim()) return;
    this.loadingAi = true;
    this.qaResults = [];
    this.http.post<any>(`${this.BASE}/ai/ask?question=${encodeURIComponent(this.searchQuery)}&topN=5`, {}).subscribe({
      next: (res) => {
        const results = res?.results || [];
        this.qaResults = results.map((r: any) => {
          const pub = this.allPublications.find(p => p.id === r.id);
          return pub ? { ...pub, answer: r.answer, score: r.score } : null;
        }).filter((r: any) => r !== null);
        this.resultCount = this.qaResults.length;
        this.loadingAi = false;
      },
      error: () => { this.loadingAi = false; }
    });
  }

  toggleDomain(id: number): void {
    if (this.selectedDomains.has(id)) this.selectedDomains.delete(id);
    else this.selectedDomains.add(id);
    this.performSearch();
  }

  getSimilarityPercent(score: number): string {
    return Math.round(score * 100) + '%';
  }
}
