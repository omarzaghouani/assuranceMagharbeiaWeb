import { Component } from '@angular/core';
import { PartnerComponent } from "./partner/partner.component";

@Component({
  selector: 'app-root',
  standalone: true, // Indicates this is a standalone component
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  imports: [PartnerComponent]
})
export class AppComponent {
  title = 'ng-front';
}
