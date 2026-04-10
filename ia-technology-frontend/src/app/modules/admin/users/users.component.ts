import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, TableModule, ButtonModule, ToastModule],
  providers: [MessageService],
  templateUrl: './users.component.html'
})
export class UsersComponent implements OnInit {
  private apiService = inject(ApiService);
  private messageService = inject(MessageService);

  users: any[] = [];

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.apiService.get<any>('users').subscribe({
      next: (res: any) => this.users = res || [],
      error: () => this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Impossible de charger' })
    });
  }

  deleteUser(id: Long): void {
    if (confirm('Êtes-vous sûr ?')) {
      this.apiService.delete(`users/${id}`).subscribe({
        next: () => {
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Utilisateur supprimé' });
          this.loadUsers();
        },
        error: () => this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Impossible de supprimer' })
      });
    }
  }
}
