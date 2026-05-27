import { Routes } from '@angular/router';
import { NewsComponent } from './news/news.component';

export const MODERATOR_ROUTES: Routes = [
  { path: '', component: NewsComponent },
  {
    path: 'publications',
    loadComponent: () => import('../admin/publications/publications.component').then(m => m.PublicationsComponent)
  }
];
