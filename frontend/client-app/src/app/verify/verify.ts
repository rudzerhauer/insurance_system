import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-verify',
  standalone: false,
  templateUrl: './verify.html',
  styleUrl: './verify.css'
})
export class Verify {
  code = "";
  error = "";
  username = localStorage.getItem("tempUsername") || "";
  constructor(private auth: AuthService, private router: Router) {}
  submit() {
    this.auth.verify(this.username, this.code).subscribe({
      next: (res: any) => {
        const access = res?.accessToken || res?.token || res?.access_token;
        const refresh = res?.refreshToken || res?.refresh_token;
        if (access) this.auth.setAccessToken(access);
        if (refresh) this.auth.setRefreshToken(refresh);
        try { localStorage.setItem('currentUsername', this.username); } catch(e) {  }
        this.router.navigate(['/policies']);
      },
      error: () => this.error = "Invalid verification code"
    });
  }
}
