import { Routes } from '@angular/router';

export const MODERATOR_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./news/news.component').then(m => m.NewsComponent)
  }
];
