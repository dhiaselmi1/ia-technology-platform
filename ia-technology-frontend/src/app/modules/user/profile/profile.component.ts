import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, CardModule],
  template: `
    <div class="py-8">
      <h1 class="text-3xl font-bold mb-6">Mon Profil</h1>
      <p-card>
        <p class="text-gray-600">Contenu du profil utilisateur à venir...</p>
      </p-card>
    </div>
  `
})
export class ProfileComponent {}
