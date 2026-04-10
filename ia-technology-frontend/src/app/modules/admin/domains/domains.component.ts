import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-domains',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, ToastModule],
  providers: [MessageService],
  templateUrl: './domains.component.html',
  styleUrl: './domains.component.scss'
})
export class DomainsComponent implements OnInit {
  private apiService = inject(ApiService);
  private messageService = inject(MessageService);
  private fb = inject(FormBuilder);

  domains: any[] = [];
  displayDialog = false;
  form: FormGroup;
  loading = false;
  editingId: Long | null = null;

  constructor() {
    this.form = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      category: ['']
    });
  }

  ngOnInit(): void {
    this.loadDomains();
  }

  loadDomains(): void {
    this.apiService.get<any>('domains').subscribe({
      next: (res: any) => this.domains = res || [],
      error: () => this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Impossible de charger les domaines' })
    });
  }

  openDialog(): void {
    this.editingId = null;
    this.form.reset();
    this.displayDialog = true;
  }

  editDomain(domain: any): void {
    this.editingId = domain.id;
    this.form.patchValue(domain);
    this.displayDialog = true;
  }

  saveDomain(): void {
    if (this.form.invalid) return;
    this.loading = true;

    const request = this.editingId
      ? this.apiService.put(`domains/${this.editingId}`, this.form.value)
      : this.apiService.post('domains', this.form.value);

    request.subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Domaine sauvegardé' });
        this.displayDialog = false;
        this.loadDomains();
        this.loading = false;
      },
      error: () => {
        this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Impossible de sauvegarder' });
        this.loading = false;
      }
    });
  }

  deleteDomain(id: Long): void {
    if (confirm('Êtes-vous sûr ?')) {
      this.apiService.delete(`domains/${id}`).subscribe({
        next: () => {
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Domaine supprimé' });
          this.loadDomains();
        },
        error: () => this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Impossible de supprimer' })
      });
    }
  }
}
