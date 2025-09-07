import { inject } from '@angular/core';
import { CanActivateChildFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateChildFn = () => {
  console.log('co vo day');
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.isAuthenticated()) return true;
  router.navigateByUrl('/login');
  return false;
};
