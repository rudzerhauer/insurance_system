import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { AuthService } from './auth.service';
import { DebugService } from './debug.service';
import { Router } from '@angular/router';
import { NotificationService } from './notification.service';
import { catchError, Observable, throwError, switchMap, tap } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class AuthInterceptor implements HttpInterceptor {
  constructor(private auth: AuthService, private debug: DebugService, private router: Router, private notification: NotificationService) {}
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.auth.getAccessToken();
    let authReq = req;
    try {
      const hasToken = !!token;
      const masked = hasToken ? `${token?.toString().slice(0,6)}...` : 'none';
      console.debug(`[AuthInterceptor] attaching token: ${masked} for ${req.url}`);
    } catch (e) {
    }
    if (token) {
      authReq = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
    }
    return next.handle(authReq).pipe(
      catchError((err: HttpErrorResponse) => {
        if (err.status === 401 && !req.url.includes('/api/auth/refresh')) {
          return this.auth.refresh().pipe(
            switchMap(() => {
              return next.handle(req);
            }),
            catchError((refreshErr) => {
              try { this.auth.clearLocalTokens(); } catch(e) {}
              try { this.debug.setLastError(refreshErr); } catch(e) {}
              try { this.notification.notify('Session expired — please log in again'); } catch(e) {}
              try { this.router.navigate(['/login']); } catch(e) {}
              return throwError(() => refreshErr);
            })
          );
        }
        try { this.debug.setLastError(err); } catch(e) {}
        return throwError(() => err);
      })
    );
  }
}
