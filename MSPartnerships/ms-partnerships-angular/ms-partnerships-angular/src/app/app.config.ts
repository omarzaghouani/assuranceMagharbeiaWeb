import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { routes } from './app.routes';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';

export const appConfig: ApplicationConfig = {
  providers: [
    // Router configuration with application routes
    provideRouter(routes),

    // HTTP Client configuration for making HTTP requests
    provideHttpClient(),

    // Enables client-side hydration for better rendering performance
    provideClientHydration(withEventReplay()),

    // Zone configuration to improve performance and handle change detection
    provideZoneChangeDetection({ eventCoalescing: true })
  ]
};

// Implement `provideZoneChangeDetection` function
function provideZoneChangeDetection(arg0: { eventCoalescing: boolean; }) {
  // Example implementation: adapt this to your needs
  return {
    provide: 'ZoneChangeDetection',
    useValue: arg0.eventCoalescing
  };
}
