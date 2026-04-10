import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { EditorModule } from 'primeng/editor';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-news',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, EditorModule, ToastModule],
  providers: [MessageService],
  templateUrl: './news.component.html'
})
export class NewsComponent implements OnInit {
  private apiService = inject(ApiService);
  private messageService = inject(MessageService);
  private fb = inject(FormBuilder);

  news: any[] = [];
  displayDialog = false;
  form: FormGroup;
  loading = false;
  editingId: Long | null = null;

  constructor() {
    this.form = this.fb.group({
      title: ['', Validators.required],
      content: ['', Validators.required],
      imageUrl: [''],
      featured: [false]
    });
  }

  ngOnInit(): void {
    this.loadNews();
  }

  loadNews(): void {
    this.apiService.get<any>('news').subscribe({
      next: (res: any) => this.news = res || [],
      error: () => this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Impossible de charger' })
    });
  }

  openDialog(): void {
    this.editingId = null;
    this.form.reset();
    this.displayDialog = true;
  }

  editNews(item: any): void {
    this.editingId = item.id;
    this.form.patchValue(item);
    this.displayDialog = true;
  }

  saveNews(): void {
    if (this.form.invalid) return;
    this.loading = true;

    const request = this.editingId
      ? this.apiService.put(`news/${this.editingId}`, this.form.value)
      : this.apiService.post('news', this.form.value);

    request.subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Actualité sauvegardée' });
        this.displayDialog = false;
        this.loadNews();
        this.loading = false;
      },
      error: () => {
        this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Impossible de sauvegarder' });
        this.loading = false;
      }
    });
  }

  deleteNews(id: Long): void {
    if (confirm('Êtes-vous sûr ?')) {
      this.apiService.delete(`news/${id}`).subscribe({
        next: () => {
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Actualité supprimée' });
          this.loadNews();
        },
        error: () => this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Impossible de supprimer' })
      });
    }
  }
}
