import { Routes } from '@angular/router';
import { ProfileComponent } from './profile/profile.component';
import { SearchComponent } from './search/search.component';

export const USER_ROUTES: Routes = [
  { path: '', component: ProfileComponent },
  { path: 'search', component: SearchComponent }
];
