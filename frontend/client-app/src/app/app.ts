import { Component, signal } from '@angular/core';
import { DebugService } from './services/debug.service';
import { Observable } from 'rxjs';
import { NotificationService } from './services/notification.service';
@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  standalone: false,
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('my-sso-frontend');
  lastError$: Observable<any> | null = null;
  lastNotification$: Observable<string | null> | null = null;
  constructor(private debug: DebugService, private notification: NotificationService) {
    this.lastError$ = this.debug.lastError$;
    this.lastNotification$ = this.notification.notification$;
  }
  getLocalAccessToken(): string | null { return localStorage.getItem('accessToken'); }
  getLocalRefreshToken(): string | null { return localStorage.getItem('refreshToken'); }
  getDocumentCookie(): string { return document.cookie || ''; }
}
