import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ResearchersComponent } from './researchers/researchers.component';
import { DomainsComponent } from './domains/domains.component';
import { PublicationsComponent } from './publications/publications.component';
import { UsersComponent } from './users/users.component';

export const ADMIN_ROUTES: Routes = [
  { path: '', component: DashboardComponent },
  { path: 'researchers', component: ResearchersComponent },
  { path: 'domains', component: DomainsComponent },
  { path: 'publications', component: PublicationsComponent },
  { path: 'users', component: UsersComponent }
];
