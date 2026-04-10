import { Routes } from '@angular/router';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
  {
    path: 'researchers',
    loadComponent: () => import('./researchers/researchers.component').then(m => m.ResearchersComponent)
  },
  {
    path: 'domains',
    loadComponent: () => import('./domains/domains.component').then(m => m.DomainsComponent)
  },
  {
    path: 'publications',
    loadComponent: () => import('./publications/publications.component').then(m => m.PublicationsComponent)
  },
  {
    path: 'users',
    loadComponent: () => import('./users/users.component').then(m => m.UsersComponent)
  }
];
