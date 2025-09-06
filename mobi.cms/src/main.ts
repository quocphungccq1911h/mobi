import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { bootstrapApplication } from '@angular/platform-browser';
import { importProvidersFrom } from '@angular/core';
import { App } from './app/app';
import { authInterceptor } from './app/core/auth/auth.interceptor';

bootstrapApplication(App, {
  providers: [
    provideHttpClient(withInterceptors([authInterceptor])),
  ],
});
