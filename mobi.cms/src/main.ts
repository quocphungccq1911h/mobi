// import { appConfig } from './app/app.config';

import { bootstrapApplication } from '@angular/platform-browser';
import { App } from './app/app';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './app/core/auth/auth.interceptor';

// bootstrapApplication(App, appConfig)
//   .catch((err) => console.error(err));

bootstrapApplication(App, {
  providers: [provideHttpClient(withInterceptors([authInterceptor]))],
});
