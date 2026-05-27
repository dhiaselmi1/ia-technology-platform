import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss'
})
export class UsersComponent implements OnInit {
  private api = inject(ApiService);

  users: any[] = [];
  searchQuery = '';

  ngOnInit(): void { this.load(); }

  load(): void {
    this.api.get<any[]>('users').subscribe(r => this.users = r || []);
  }

  get filtered(): any[] {
    if (!this.searchQuery.trim()) return this.users;
    const q = this.searchQuery.toLowerCase();
    return this.users.filter(u => u.username?.toLowerCase().includes(q) || u.email?.toLowerCase().includes(q));
  }

  get activeCount(): number { return this.users.filter(u => u.active).length; }

  toggleActive(user: any): void {
    const newStatus = !user.active;
    this.api.put(`users/${user.id}/active?active=${newStatus}`, {}).subscribe({
      next: () => { user.active = newStatus; },
      error: () => {}
    });
  }

  changeRole(user: any, role: string): void {
    this.api.put(`users/${user.id}/role?role=${role}`, {}).subscribe({
      next: () => { user.role = role; },
      error: () => {}
    });
  }

  deleteUser(id: number): void {
    if (confirm('Supprimer cet utilisateur ?')) {
      this.api.delete(`users/${id}`).subscribe(() => this.load());
    }
  }

  getRoleBadgeClass(role: string): string {
    switch (role) {
      case 'ADMIN': return 'badge badge-danger';
      case 'MODERATEUR': return 'badge badge-warning';
      default: return 'badge badge-primary';
    }
  }

  getInitials(u: any): string {
    const parts = (u.username || '').split(' ');
    return parts.length > 1 ? (parts[0][0] + parts[1][0]).toUpperCase() : (u.username || '??').substring(0, 2).toUpperCase();
  }
}
