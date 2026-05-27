import { Component, inject, OnInit, AfterViewInit, ViewChild, ElementRef, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser, CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit, AfterViewInit {
  private api = inject(ApiService);
  private platformId = inject(PLATFORM_ID);
  private isBrowser = isPlatformBrowser(this.platformId);

  stats = { researchers: 0, publications: 0, domains: 0, users: 0, news: 0 };
  recentActivity: any[] = [];
  trends: any[] = [];

  @ViewChild('domainChart') domainChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('monthChart') monthChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('roleChart') roleChartRef!: ElementRef<HTMLCanvasElement>;

  private domainChart: Chart | null = null;
  private monthChart: Chart | null = null;
  private roleChart: Chart | null = null;
  private viewReady = false;

  domainData: any[] = [];
  monthData: any[] = [];
  roleData: any[] = [];

  ngOnInit(): void {
    this.api.get<any>('stats/overview').subscribe({
      next: (res) => this.stats = { ...this.stats, ...res },
      error: () => {
        this.api.get<any[]>('researchers').subscribe(r => this.stats.researchers = (r || []).length);
        this.api.get<any[]>('publications').subscribe(r => this.stats.publications = (r || []).length);
        this.api.get<any[]>('domains').subscribe(r => this.stats.domains = (r || []).length);
        this.api.get<any[]>('users').subscribe(r => this.stats.users = (r || []).length);
      }
    });
    this.api.get<any[]>('stats/recent-activity').subscribe({
      next: (res) => this.recentActivity = (res || []).slice(0, 8),
      error: () => {}
    });

    this.loadChartData();
    this.loadTrends();
  }

  private loadTrends(): void {
    this.api.get<any>('ai/predict-trends').subscribe({
      next: (res) => this.trends = res?.trends || [],
      error: () => {}
    });
  }

  ngAfterViewInit(): void {
    if (this.isBrowser) {
      this.viewReady = true;
      setTimeout(() => this.renderAllCharts(), 100);
    }
  }

  private loadChartData(): void {
    this.api.get<any[]>('stats/publications-by-domain').subscribe({
      next: (data) => {
        this.domainData = data || [];
        if (this.isBrowser) setTimeout(() => this.renderAllCharts(), 100);
      },
      error: () => {}
    });

    this.api.get<any[]>('stats/publications-by-month').subscribe({
      next: (data) => {
        this.monthData = data || [];
        if (this.isBrowser) setTimeout(() => this.renderAllCharts(), 100);
      },
      error: () => {}
    });

    this.api.get<any[]>('stats/users-by-role').subscribe({
      next: (data) => {
        this.roleData = data || [];
        if (this.isBrowser) setTimeout(() => this.renderAllCharts(), 100);
      },
      error: () => {}
    });
  }

  private renderAllCharts(): void {
    if (!this.viewReady) return;

    if (this.domainData.length && !this.domainChart && this.domainChartRef?.nativeElement) {
      const labels = this.domainData.map(d => d.domain);
      const values = this.domainData.map(d => d.count);
      const colors = ['#2563eb', '#7c3aed', '#06b6d4', '#10b981', '#f59e0b', '#ef4444', '#ec4899', '#8b5cf6'];

      this.domainChart = new Chart(this.domainChartRef.nativeElement, {
        type: 'doughnut',
        data: {
          labels,
          datasets: [{ data: values, backgroundColor: colors.slice(0, labels.length), borderWidth: 2, borderColor: '#ffffff' }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { position: 'bottom', labels: { padding: 16, usePointStyle: true, pointStyle: 'circle', font: { size: 11 } } } },
          cutout: '65%'
        }
      });
    }

    if (this.monthData.length && !this.monthChart && this.monthChartRef?.nativeElement) {
      const labels = this.monthData.map(d => {
        const [y, m] = d.month.split('-');
        const mNames = ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Jun', 'Jul', 'Aoû', 'Sep', 'Oct', 'Nov', 'Déc'];
        return mNames[parseInt(m) - 1] + ' ' + y.slice(2);
      });
      const values = this.monthData.map(d => d.count);

      this.monthChart = new Chart(this.monthChartRef.nativeElement, {
        type: 'bar',
        data: {
          labels,
          datasets: [{ label: 'Publications', data: values, backgroundColor: '#2563eb', borderRadius: 6, borderSkipped: false, barPercentage: 0.6 }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { display: false } },
          scales: {
            y: { beginAtZero: true, ticks: { stepSize: 1, font: { size: 11 } }, grid: { color: '#f1f5f9' } },
            x: { ticks: { font: { size: 11 } }, grid: { display: false } }
          }
        }
      });
    }

    if (this.roleData.length && !this.roleChart && this.roleChartRef?.nativeElement) {
      const labels = this.roleData.map(d => d.role);
      const values = this.roleData.map(d => d.count);
      const roleColors: Record<string, string> = { ADMIN: '#ef4444', MODERATEUR: '#f59e0b', UTILISATEUR: '#2563eb' };
      const colors = labels.map(l => roleColors[l] || '#94a3b8');

      this.roleChart = new Chart(this.roleChartRef.nativeElement, {
        type: 'pie',
        data: {
          labels,
          datasets: [{ data: values, backgroundColor: colors, borderWidth: 2, borderColor: '#ffffff' }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { position: 'bottom', labels: { padding: 16, usePointStyle: true, pointStyle: 'circle', font: { size: 11 } } } }
        }
      });
    }
  }
}
