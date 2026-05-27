import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-researchers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './researchers.component.html',
  styleUrl: './researchers.component.scss'
})
export class ResearchersComponent implements OnInit {
  private api = inject(ApiService);

  researchers: any[] = [];
  domains: any[] = [];
  searchQuery = '';
  showDialog = false;
  editingId: number | null = null;

  form = { firstName: '', lastName: '', email: '', bio: '', photoUrl: '', domainId: null as number | null };

  ngOnInit(): void {
    this.load();
    this.api.get<any[]>('domains').subscribe(r => this.domains = r || []);
  }

  load(): void {
    this.api.get<any[]>('researchers').subscribe(r => this.researchers = r || []);
  }

  get filtered(): any[] {
    if (!this.searchQuery.trim()) return this.researchers;
    const q = this.searchQuery.toLowerCase();
    return this.researchers.filter(r =>
      `${r.firstName} ${r.lastName}`.toLowerCase().includes(q) || (r.domainName||'').toLowerCase().includes(q)
    );
  }

  openNew(): void {
    this.editingId = null;
    this.form = { firstName: '', lastName: '', email: '', bio: '', photoUrl: '', domainId: null };
    this.showDialog = true;
  }

  edit(r: any): void {
    this.editingId = r.id;
    this.form = { firstName: r.firstName, lastName: r.lastName, email: r.email, bio: r.bio || '', photoUrl: r.photoUrl || '', domainId: r.domainId };
    this.showDialog = true;
  }

  save(): void {
    const req = this.editingId
      ? this.api.put(`researchers/${this.editingId}`, this.form)
      : this.api.post('researchers', this.form);
    req.subscribe({ next: () => { this.showDialog = false; this.load(); } });
  }

  delete(id: number): void {
    if (confirm('Supprimer ce chercheur ?')) {
      this.api.delete(`researchers/${id}`).subscribe(() => this.load());
    }
  }

  getInitials(r: any): string {
    return ((r.firstName?.[0] || '') + (r.lastName?.[0] || '')).toUpperCase();
  }
}
