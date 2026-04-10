import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CardModule } from 'primeng/card';
import { ChartModule } from 'primeng/chart';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, CardModule, ChartModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  private apiService = inject(ApiService);

  stats = {
    researchers: 0,
    publications: 0,
    domains: 0,
    users: 0
  };

  chartData: any;
  chartOptions: any;

  ngOnInit(): void {
    this.loadStats();
    this.initChart();
  }

  loadStats(): void {
    this.apiService.get<any>('researchers').subscribe({
      next: (res: any) => this.stats.researchers = (res || []).length
    });
    this.apiService.get<any>('publications').subscribe({
      next: (res: any) => this.stats.publications = (res || []).length
    });
    this.apiService.get<any>('domains').subscribe({
      next: (res: any) => this.stats.domains = (res || []).length
    });
    this.apiService.get<any>('users').subscribe({
      next: (res: any) => this.stats.users = (res || []).length
    });
  }

  initChart(): void {
    this.chartData = {
      labels: ['Chercheurs', 'Publications', 'Domaines', 'Utilisateurs'],
      datasets: [{
        data: [this.stats.researchers, this.stats.publications, this.stats.domains, this.stats.users],
        backgroundColor: ['#3b82f6', '#8b5cf6', '#ec4899', '#f59e0b']
      }]
    };
    this.chartOptions = {
      responsive: true,
      maintainAspectRatio: true
    };
  }
}
