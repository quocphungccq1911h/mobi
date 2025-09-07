import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { UserRequest } from '../../../shared/request/UserReq';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  loading = signal(false);
  error = signal('');
  username = '';
  password = '';

  onSubmit() {
    this.loading.set(true);
    this.error.set('');

    const userReq: UserRequest = {
      username: this.username,
      password: this.password,
    };

    this.auth.login(userReq).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigateByUrl('/products');
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set('Sai tài khoản hoặc mật khẩu');
        console.log(err);
      },
    });
  }
}
