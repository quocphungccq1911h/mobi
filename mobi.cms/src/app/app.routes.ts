import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { adminLayoutRoutes } from './core/layout/admin-layout.routes';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./core/auth/login/login').then((m) => m.Login) },
  {
    path: '',
    canActivate: [authGuard],
    children: adminLayoutRoutes,
  },
];
