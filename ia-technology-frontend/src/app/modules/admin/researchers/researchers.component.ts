import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-researchers',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, DropdownModule, ToastModule],
  providers: [MessageService],
  templateUrl: './researchers.component.html',
  styleUrl: './researchers.component.scss'
})
export class ResearchersComponent implements OnInit {
  private apiService = inject(ApiService);
  private messageService = inject(MessageService);
  private fb = inject(FormBuilder);

  researchers: any[] = [];
  domains: any[] = [];
  displayDialog = false;
  form: FormGroup;
  loading = false;
  editingId: Long | null = null;

  constructor() {
    this.form = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      bio: [''],
      photoUrl: [''],
      domainId: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadResearchers();
    this.loadDomains();
  }

  loadResearchers(): void {
    this.apiService.get<any>('researchers').subscribe({
      next: (res: any) => this.researchers = res || [],
      error: () => this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Impossible de charger les chercheurs' })
    });
  }

  loadDomains(): void {
    this.apiService.get<any>('domains').subscribe({
      next: (res: any) => this.domains = res || []
    });
  }

  openDialog(): void {
    this.editingId = null;
    this.form.reset();
    this.displayDialog = true;
  }

  editResearcher(researcher: any): void {
    this.editingId = researcher.id;
    this.form.patchValue(researcher);
    this.displayDialog = true;
  }

  saveResearcher(): void {
    if (this.form.invalid) return;
    this.loading = true;

    const request = this.editingId
      ? this.apiService.put(`researchers/${this.editingId}`, this.form.value)
      : this.apiService.post('researchers', this.form.value);

    request.subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Chercheur sauvegardé' });
        this.displayDialog = false;
        this.loadResearchers();
        this.loading = false;
      },
      error: () => {
        this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Impossible de sauvegarder' });
        this.loading = false;
      }
    });
  }

  deleteResearcher(id: Long): void {
    if (confirm('Êtes-vous sûr ?')) {
      this.apiService.delete(`researchers/${id}`).subscribe({
        next: () => {
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Chercheur supprimé' });
          this.loadResearchers();
        },
        error: () => this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Impossible de supprimer' })
      });
    }
  }
}
