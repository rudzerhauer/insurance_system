import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
 export class Login {
  username = "";
  password = "";
  error = "";
  constructor(private auth: AuthService, private router: Router) {}
 submit() {
  this.auth.login(this.username, this.password).subscribe({
    next: (res: any) => {
      const access = res?.accessToken || res?.token || res?.access_token;
      const refresh = res?.refreshToken || res?.refresh_token;
      if (access) {
        this.auth.setAccessToken(access);
      }
      if (refresh) {
        this.auth.setRefreshToken(refresh);
      }
      if (res.verified === false) {
        localStorage.setItem("tempUsername", this.username);
        this.router.navigate(['/verify']);
        return;
      }
      if (res.verified === true) {
        try { localStorage.setItem('currentUsername', this.username); } catch(e) {  }
        this.router.navigate(['/policies']);
        return;
      }
    },
    error: () => this.error = "Invalid username or password"
  });
}
}
