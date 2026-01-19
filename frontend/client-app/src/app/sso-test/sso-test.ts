import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
@Component({
  selector: 'app-sso-test',
  standalone: false,
  templateUrl: './sso-test.html',
  styleUrls: ['./sso-test.css']
})
export class SsoTest {
  out = 'Not requested yet';
  constructor(private http: HttpClient, private router: Router) {}
  login() {
    window.location.href = '/oauth2/authorization/keycloak';
  }
  async me() {
    try {
      const res = await this.http.get('/api/auth/me', { withCredentials: true }).toPromise();
      this.out = JSON.stringify(res, null, 2);
    } catch (e: any) {
      this.out = 'Not authenticated: ' + (e?.status || e?.message || e);
    }
  }
  logout() {
    this.out = 'Use backend /api/auth/logout to fully clear cookies.';
  }
}
