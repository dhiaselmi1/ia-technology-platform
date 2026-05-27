import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    loadChildren: () => import('./modules/public/public.routes').then(m => m.PUBLIC_ROUTES)
  },
  {
    path: 'search',
    loadComponent: () => import('./modules/user/search/search.component').then(m => m.SearchComponent)
  },
  {
    path: 'researchers',
    loadComponent: () => import('./modules/public/researchers-list/researchers-list.component').then(m => m.ResearchersListComponent)
  },
  {
    path: 'publications/:id',
    loadComponent: () => import('./modules/public/publication-detail/publication-detail.component').then(m => m.PublicationDetailComponent)
  },
  {
    path: 'auth',
    loadChildren: () => import('./modules/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: 'admin',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN'] },
    loadChildren: () => import('./modules/admin/admin.routes').then(m => m.ADMIN_ROUTES)
  },
  {
    path: 'moderator',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['MODERATEUR'] },
    loadChildren: () => import('./modules/moderator/moderator.routes').then(m => m.MODERATOR_ROUTES)
  },
  {
    path: 'user',
    canActivate: [authGuard],
    loadChildren: () => import('./modules/user/user.routes').then(m => m.USER_ROUTES)
  },
  { path: '**', redirectTo: '' }
];
