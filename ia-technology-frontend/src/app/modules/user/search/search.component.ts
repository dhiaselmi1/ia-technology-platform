import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { InputGroupModule } from 'primeng/inputgroup';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, FormsModule, CardModule, ButtonModule, InputGroupModule, InputTextModule, DropdownModule],
  templateUrl: './search.component.html'
})
export class SearchComponent implements OnInit {
  private apiService = inject(ApiService);
  private route = inject(ActivatedRoute);

  searchQuery = '';
  publications: any[] = [];
  researchers: any[] = [];
  domains: any[] = [];
  selectedDomain: number | null = null;

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['q']) {
        this.searchQuery = params['q'];
        this.performSearch();
      }
    });
    this.loadDomains();
  }

  loadDomains(): void {
    this.apiService.get<any>('domains').subscribe({
      next: (res: any) => this.domains = res || []
    });
  }

  performSearch(): void {
    if (!this.searchQuery.trim() && !this.selectedDomain) return;

    const params: any = {};
    if (this.searchQuery.trim()) params['q'] = this.searchQuery;
    if (this.selectedDomain) params['domain'] = this.selectedDomain;

    this.apiService.get<any>('search', params).subscribe({
      next: (res: any) => {
        this.publications = res.publications || [];
        this.researchers = res.researchers || [];
      }
    });
  }
}
