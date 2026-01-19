import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
@Injectable({ providedIn: 'root' })
export class AuthService {
  private baseUrl = "/api/auth";
  constructor(private http: HttpClient) {}
  login(username: string, password: string) {
    return this.http.post(`${this.baseUrl}/login`, { username, password }, { withCredentials: true });
  }
  verify(username: string, code: string) {
    return this.http.post(`${this.baseUrl}/verify`, { username, code }, { withCredentials: true });
  }
  register(username: string, email: string, password: string) {
  return this.http.post(
  "/api/auth/register",
    { username, email, password },
    { responseType: 'text', withCredentials: true }  
  );
}
  refresh() {
    const refreshToken = this.getRefreshToken();
    return this.http.post(`${this.baseUrl}/refresh`, { refreshToken }, { withCredentials: true });
  }
  logout() {
    const refreshToken = this.getRefreshToken();
    return this.http.post(`${this.baseUrl}/logout`, { refreshToken }, { withCredentials: true });
  }
  setAccessToken(token: string) {
    localStorage.setItem("accessToken", token);
  }
  setRefreshToken(token: string) {
    localStorage.setItem("refreshToken", token);
  }
  getAccessToken() {
    return localStorage.getItem("accessToken");
  }
  getRefreshToken() {
    return localStorage.getItem("refreshToken");
  }
  clearLocalTokens() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }
}
