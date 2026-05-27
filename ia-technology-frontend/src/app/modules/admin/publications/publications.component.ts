import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-publications',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './publications.component.html',
  styleUrl: './publications.component.scss'
})
export class PublicationsComponent implements OnInit {
  private api = inject(ApiService);
  private http = inject(HttpClient);
  private readonly BASE = 'http://localhost:8080/api';

  publications: any[] = [];
  domains: any[] = [];
  researchers: any[] = [];
  searchQuery = '';
  filterResearcherId: number | null = null;
  filterDomainId: number | null = null;
  showDialog = false;
  editingId: number | null = null;

  form = { title: '', abstract_: '', doi: '', publishedDate: '', researcherId: null as number | null, domainId: null as number | null, filePath: '' };

  uploadingFile = false;
  uploadedFileName: string | null = null;

  classifying = false;
  aiPredictions: any[] = [];
  aiMetrics: any = null;

  ngOnInit(): void {
    this.load();
    this.api.get<any[]>('domains').subscribe(r => this.domains = r || []);
    this.api.get<any[]>('researchers').subscribe(r => this.researchers = r || []);
  }

  load(): void {
    this.api.get<any[]>('publications').subscribe(r => this.publications = r || []);
  }

  get withFileCount(): number {
    return this.publications.filter(p => p.filePath).length;
  }

  get filtered(): any[] {
    return this.publications.filter(p => {
      const matchQ = !this.searchQuery.trim() || p.title?.toLowerCase().includes(this.searchQuery.toLowerCase());
      const matchR = !this.filterResearcherId || p.researcherId === this.filterResearcherId;
      const matchD = !this.filterDomainId || p.domainId === this.filterDomainId;
      return matchQ && matchR && matchD;
    });
  }

  clearFilters(): void {
    this.searchQuery = '';
    this.filterResearcherId = null;
    this.filterDomainId = null;
  }

  get hasActiveFilters(): boolean {
    return !!this.searchQuery.trim() || !!this.filterResearcherId || !!this.filterDomainId;
  }

  openNew(): void {
    this.editingId = null;
    this.form = { title: '', abstract_: '', doi: '', publishedDate: '', researcherId: null, domainId: null, filePath: '' };
    this.uploadedFileName = null;
    this.aiPredictions = [];
    this.aiMetrics = null;
    this.showDialog = true;
  }

  edit(p: any): void {
    this.editingId = p.id;
    this.form = {
      title: p.title, abstract_: p.abstractText || p.abstract_ || '',
      doi: p.doi || '', publishedDate: p.publishedDate || '',
      researcherId: p.researcherId, domainId: p.domainId,
      filePath: p.filePath || ''
    };
    this.uploadedFileName = p.filePath ? p.filePath.split('/').pop() || null : null;
    this.showDialog = true;
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    const file = input.files[0];

    this.uploadingFile = true;
    this.uploadedFileName = file.name;
    const fd = new FormData();
    fd.append('file', file);
    this.api.upload<{ path: string }>('files/pdf', fd).subscribe({
      next: (res) => {
        this.form.filePath = res.path;
        this.uploadingFile = false;
      },
      error: () => {
        this.uploadingFile = false;
        this.uploadedFileName = null;
      }
    });
  }

  removeFile(): void {
    this.form.filePath = '';
    this.uploadedFileName = null;
  }

  save(): void {
    const req = this.editingId
      ? this.api.put(`publications/${this.editingId}`, this.form)
      : this.api.post('publications', this.form);
    req.subscribe({ next: () => { this.showDialog = false; this.load(); } });
  }

  toggleFeatured(p: any): void {
    p.featured = !p.featured;
    this.api.put(`publications/${p.id}`, {
      title: p.title,
      abstract_: p.abstractText || p.abstract_ || '',
      doi: p.doi || '',
      publishedDate: p.publishedDate || '',
      researcherId: p.researcherId,
      domainId: p.domainId,
      filePath: p.filePath || '',
      featured: p.featured
    }).subscribe();
  }

  classifyDomain(): void {
    const text = (this.form.title + ' ' + this.form.abstract_).trim();
    if (!text || text.length < 10) return;
    this.classifying = true;
    this.aiPredictions = [];
    this.aiMetrics = null;
    this.http.post<any>(`${this.BASE}/ai/classify?text=${encodeURIComponent(text)}&topN=3`, {}).subscribe({
      next: (res) => {
        this.aiPredictions = res?.predictions || [];
        this.aiMetrics = res?.metrics || null;
        this.classifying = false;
      },
      error: () => { this.classifying = false; }
    });
  }

  applyPrediction(pred: any): void {
    const domain = this.domains.find((d: any) => d.name === pred.domain);
    if (domain) this.form.domainId = domain.id;
  }

  delete(id: number): void {
    if (confirm('Supprimer cette publication ?')) {
      this.api.delete(`publications/${id}`).subscribe(() => this.load());
    }
  }
}
