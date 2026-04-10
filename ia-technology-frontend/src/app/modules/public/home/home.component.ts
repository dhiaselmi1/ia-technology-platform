import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { InputGroupModule } from 'primeng/inputgroup';
import { InputTextModule } from 'primeng/inputtext';
import { TagModule } from 'primeng/tag';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, CardModule, ButtonModule, InputGroupModule, InputTextModule, TagModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  private apiService = inject(ApiService);

  publications: any[] = [];
  news: any[] = [];
  searchQuery = '';

  ngOnInit(): void {
    this.loadPublications();
    this.loadNews();
  }

  loadPublications(): void {
    this.apiService.get<any>('publications').subscribe({
      next: (res: any) => this.publications = (res || []).slice(0, 6),
      error: () => this.publications = []
    });
  }

  loadNews(): void {
    this.apiService.get<any>('news').subscribe({
      next: (res: any) => this.news = (res || []).slice(0, 3),
      error: () => this.news = []
    });
  }

  onSearch(): void {
    if (this.searchQuery.trim()) {
      // Redirect to search page with query
      window.location.href = `/user/search?q=${encodeURIComponent(this.searchQuery)}`;
    }
  }
}
