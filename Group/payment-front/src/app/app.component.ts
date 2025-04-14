import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root', // ✅ Changed from app-payment
  standalone: true,
  imports: [RouterOutlet], // ✅ RouterOutlet instead of PaymentComponent
  template: `
    <router-outlet></router-outlet>
    <!-- OR if you want payment always visible -->
    <!-- <app-payment></app-payment> -->
  `,
  styles: []
})
export class AppComponent {
  title = 'payment-front';
}