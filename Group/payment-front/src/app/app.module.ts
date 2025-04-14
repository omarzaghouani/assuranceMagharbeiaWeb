import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms'; // ✅ Import this

import { AppComponent } from './app.component';
import { PaymentComponent } from './components/payment/payment.component';
import { PaymentService } from './services/payment.service';


@NgModule({
    declarations: [
    ],
    imports: [
      BrowserModule,
      FormsModule,
      PaymentComponent // ✅ You import standalone component here
    ],
    providers: [PaymentService],
  })
export class AppModule { }
