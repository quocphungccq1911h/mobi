import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../../shared/models/User';
import { environment } from '../../../environments/environment';
import { tap, map } from 'rxjs/operators';

const TOKEN_KEY = 'access_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  user = signal<User | null>(null);

  login(userReq: User) {
    return this.http.post<{ accessToken: string; user: User }>(
      `${environment.apiBaseUrl}/auth/login`,
      userReq
    ).pipe(
      tap(({ accessToken, user }) => this.setSession(accessToken, user)),
      map(() => true)
    );
  }

  setSession(token: string, user: User) {
    localStorage.setItem(TOKEN_KEY, token);
    this.user.set(user);
  }

  logout() {
    localStorage.removeItem(TOKEN_KEY);
    this.user.set(null);
  }

 hasRole(role: string) { return ( this.user() ?.roles.map((r) => r.toString()) .includes(role) ?? false ); }

  isAuthenticated() {
    return !!localStorage.getItem(TOKEN_KEY);
  }
}
