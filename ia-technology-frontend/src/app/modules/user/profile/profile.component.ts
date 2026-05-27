import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  private api = inject(ApiService);
  private authService = inject(AuthService);

  profile: any = null;
  loading = true;

  editMode = false;
  editForm = { username: '', email: '' };
  editError = '';
  editSuccess = '';
  saving = false;

  showPasswordForm = false;
  passwordForm = { currentPassword: '', newPassword: '', confirmPassword: '' };
  passwordError = '';
  passwordSuccess = '';
  savingPassword = false;

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.loading = true;
    this.api.get<any>('profile').subscribe({
      next: (res) => {
        this.profile = res;
        this.loading = false;
      },
      error: () => {
        this.profile = {
          username: this.authService.getUsername(),
          email: '',
          role: this.authService.getUserRole(),
          createdAt: ''
        };
        this.loading = false;
      }
    });
  }

  getInitials(): string {
    const name = this.profile?.username || '';
    if (!name) return '?';
    const parts = name.split(' ');
    return parts.length > 1 ? (parts[0][0] + parts[1][0]).toUpperCase() : name.substring(0, 2).toUpperCase();
  }

  getRoleBadgeClass(): string {
    switch (this.profile?.role) {
      case 'ADMIN': return 'badge badge-danger';
      case 'MODERATEUR': return 'badge badge-warning';
      default: return 'badge badge-primary';
    }
  }

  getRoleLabel(): string {
    switch (this.profile?.role) {
      case 'ADMIN': return 'Administrateur';
      case 'MODERATEUR': return 'Modérateur';
      default: return 'Utilisateur';
    }
  }

  startEdit(): void {
    this.editForm = { username: this.profile.username, email: this.profile.email };
    this.editError = '';
    this.editSuccess = '';
    this.editMode = true;
  }

  cancelEdit(): void {
    this.editMode = false;
    this.editError = '';
    this.editSuccess = '';
  }

  saveProfile(): void {
    this.saving = true;
    this.editError = '';
    this.api.put<any>('profile', this.editForm).subscribe({
      next: (res) => {
        if (res.message && !res.id) {
          this.editError = res.message;
          this.saving = false;
          return;
        }
        this.profile = res;
        this.editMode = false;
        this.editSuccess = 'Profil mis à jour avec succès';
        this.saving = false;
        const stored = localStorage.getItem('iat_user');
        if (stored) {
          const userData = JSON.parse(stored);
          userData.username = res.username;
          localStorage.setItem('iat_user', JSON.stringify(userData));
        }
        setTimeout(() => this.editSuccess = '', 3000);
      },
      error: (err) => {
        this.editError = err.error?.message || 'Erreur lors de la mise à jour';
        this.saving = false;
      }
    });
  }

  togglePasswordForm(): void {
    this.showPasswordForm = !this.showPasswordForm;
    this.passwordForm = { currentPassword: '', newPassword: '', confirmPassword: '' };
    this.passwordError = '';
    this.passwordSuccess = '';
  }

  changePassword(): void {
    if (!this.passwordForm.currentPassword || !this.passwordForm.newPassword) {
      this.passwordError = 'Tous les champs sont obligatoires';
      return;
    }
    if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
      this.passwordError = 'Les mots de passe ne correspondent pas';
      return;
    }
    if (this.passwordForm.newPassword.length < 6) {
      this.passwordError = 'Le mot de passe doit contenir au moins 6 caractères';
      return;
    }
    this.savingPassword = true;
    this.passwordError = '';
    this.api.put<any>('profile/password', {
      currentPassword: this.passwordForm.currentPassword,
      newPassword: this.passwordForm.newPassword
    }).subscribe({
      next: (res) => {
        this.passwordSuccess = res.message || 'Mot de passe modifié';
        this.savingPassword = false;
        this.passwordForm = { currentPassword: '', newPassword: '', confirmPassword: '' };
        setTimeout(() => { this.passwordSuccess = ''; this.showPasswordForm = false; }, 3000);
      },
      error: (err) => {
        this.passwordError = err.error?.message || 'Erreur lors du changement';
        this.savingPassword = false;
      }
    });
  }
}
