import { Component } from '@angular/core';

@Component({
  selector: 'app-home',
  standalone: true,
  template: `
    <div class="text-center py-8">
      <h1 class="text-4xl font-bold mb-4">Bienvenue sur IA-Technology</h1>
      <p class="text-xl text-gray-600 mb-8">Plateforme de gestion des travaux scientifiques</p>
      <p class="text-gray-500">Contenu de la page d'accueil à venir...</p>
    </div>
  `
})
export class HomeComponent {}
