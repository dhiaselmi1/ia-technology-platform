import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-domains',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './domains.component.html',
  styleUrl: './domains.component.scss'
})
export class DomainsComponent implements OnInit {
  private api = inject(ApiService);

  domains: any[] = [];
  searchQuery = '';
  showDialog = false;
  editingId: number | null = null;
  form = { name: '', description: '', category: '' };

  ngOnInit(): void { this.load(); }

  load(): void {
    this.api.get<any[]>('domains').subscribe(r => this.domains = r || []);
  }

  get filtered(): any[] {
    if (!this.searchQuery.trim()) return this.domains;
    const q = this.searchQuery.toLowerCase();
    return this.domains.filter(d => d.name?.toLowerCase().includes(q));
  }

  openNew(): void {
    this.editingId = null;
    this.form = { name: '', description: '', category: '' };
    this.showDialog = true;
  }

  edit(d: any): void {
    this.editingId = d.id;
    this.form = { name: d.name, description: d.description || '', category: d.category || '' };
    this.showDialog = true;
  }

  save(): void {
    const req = this.editingId
      ? this.api.put(`domains/${this.editingId}`, this.form)
      : this.api.post('domains', this.form);
    req.subscribe({ next: () => { this.showDialog = false; this.load(); } });
  }

  delete(id: number): void {
    if (confirm('Supprimer ce domaine ?')) {
      this.api.delete(`domains/${id}`).subscribe(() => this.load());
    }
  }
}
