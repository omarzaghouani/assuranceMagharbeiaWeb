import { Routes } from '@angular/router';
import { PartnerComponent } from './partner/partner.component'; // Import your PartnerComponent

export const routes: Routes = [
  { path: '', redirectTo: 'partners', pathMatch: 'full' }, // Default route
  { path: 'partners', component: PartnerComponent }, // Route for PartnerComponent
];