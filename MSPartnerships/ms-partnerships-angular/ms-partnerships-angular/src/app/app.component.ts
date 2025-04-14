import { Component } from '@angular/core';
import { PartnerComponent } from './partner/partner.component';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [PartnerComponent], // Include RouterOutlet if using routing
  template: `
    <main>
      <app-partner></app-partner> <!-- Render the PartnerComponent -->
    </main>
  `,
})
export class AppComponent {
  title = 'ng-front';
}