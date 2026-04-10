import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-publications',
  standalone: true,
  imports: [CommonModule, TableModule, ButtonModule, ToastModule],
  providers: [MessageService],
  templateUrl: './publications.component.html'
})
export class PublicationsComponent implements OnInit {
  private apiService = inject(ApiService);
  private messageService = inject(MessageService);

  publications: any[] = [];

  ngOnInit(): void {
    this.loadPublications();
  }

  loadPublications(): void {
    this.apiService.get<any>('publications').subscribe({
      next: (res: any) => this.publications = res || [],
      error: () => this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Impossible de charger' })
    });
  }

  deletePublication(id: number): void {
    if (confirm('Êtes-vous sûr ?')) {
      this.apiService.delete(`publications/${id}`).subscribe({
        next: () => {
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Publication supprimée' });
          this.loadPublications();
        },
        error: () => this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Impossible de supprimer' })
      });
    }
  }
}
