import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { ApiService } from '../../../core/services/api.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-researchers-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './researchers-list.component.html',
  styleUrl: './researchers-list.component.scss'
})
export class ResearchersListComponent implements OnInit {
  private api = inject(ApiService);
  private http = inject(HttpClient);
  private auth = inject(AuthService);

  private readonly BASE = 'http://localhost:8080/api';

  researchers: any[] = [];
  domains: any[] = [];
  searchQuery = '';
  selectedDomainId: number | null = null;

  selectedResearcher: any = null;
  aiProfile: any = null;
  loadingProfile = false;

  get isLoggedIn(): boolean {
    return this.auth.isLoggedIn();
  }

  ngOnInit(): void {
    this.api.get<any[]>('researchers').subscribe(r => this.researchers = r || []);
    this.api.get<any[]>('domains').subscribe(r => this.domains = r || []);
  }

  get filtered(): any[] {
    return this.researchers.filter(r => {
      const matchQ = !this.searchQuery.trim() ||
        `${r.firstName} ${r.lastName}`.toLowerCase().includes(this.searchQuery.toLowerCase());
      const matchD = !this.selectedDomainId || r.domainId === this.selectedDomainId;
      return matchQ && matchD;
    });
  }

  getInitials(r: any): string {
    return ((r.firstName?.[0] || '') + (r.lastName?.[0] || '')).toUpperCase();
  }

  openAiProfile(r: any): void {
    this.selectedResearcher = r;
    this.aiProfile = null;
    this.loadingProfile = true;
    this.api.get<any>(`ai/researcher-profile/${r.id}`, { topN: '3' }).subscribe({
      next: (res) => {
        this.aiProfile = res;
        this.loadingProfile = false;
      },
      error: () => {
        this.aiProfile = { keywords: [], similar: [] };
        this.loadingProfile = false;
      }
    });
  }

  closeProfile(): void {
    this.selectedResearcher = null;
    this.aiProfile = null;
  }
}
