import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    // Remove provideZoneChangeDetection or implement it properly
    provideRouter(routes),
    provideHttpClient()
  ]
};