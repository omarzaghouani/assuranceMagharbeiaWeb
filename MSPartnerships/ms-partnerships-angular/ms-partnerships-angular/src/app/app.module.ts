import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms'; // Import FormsModule
import { HttpClientModule } from '@angular/common/http';
import { AppComponent } from './app.component';
import { PartnerComponent } from './partner/partner.component';
import { RouterModule } from '@angular/router';
import { routes } from './app.routes';

@NgModule({
    imports: [
      BrowserModule,
      FormsModule,
      HttpClientModule,
      RouterModule.forRoot(routes) // Add this line if not standalone
    ],
  })
  
export class AppModule {}
