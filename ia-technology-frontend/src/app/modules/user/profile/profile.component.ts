import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="py-8">
      <h1 class="text-3xl font-bold mb-6">Mon Profil</h1>
      <div class="border rounded-lg p-6">
        <p class="text-gray-600">Contenu du profil utilisateur à venir...</p>
      </div>
    </div>
  `
})
export class ProfileComponent {}
