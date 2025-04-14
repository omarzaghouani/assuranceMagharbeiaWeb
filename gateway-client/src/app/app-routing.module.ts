import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RefundListComponent } from './components/refund-list/refund-list.component';

const routes: Routes = [
  { path: '', redirectTo: 'refunds', pathMatch: 'full' },
  { path: 'refunds', component: RefundListComponent },
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
