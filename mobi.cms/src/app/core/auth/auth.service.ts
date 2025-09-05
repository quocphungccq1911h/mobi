import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../../shared/models/User';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  // Lưu trạng thái đăng nhập bằng signal:
  user = signal<User | null>(null);

  login(userReq: User) {
    return this.http.post<{ accessToken: string; user: User }>(
      `${environment.apiBaseUrl}/auth/login`,
      userReq
    );
  }

  setSession(token: string, user: User) {
    localStorage.setItem('access_token', token);
    this.user.set(user);
  }

  logout() {
    localStorage.removeItem('access_token');
    this.user.set(null);
  }

  hasRole(role: string) {
    return (
      this.user()
        ?.roles.map((r) => r.toString())
        .includes(role) ?? false
    );
  }
}
