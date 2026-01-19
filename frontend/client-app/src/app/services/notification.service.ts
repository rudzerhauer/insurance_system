import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
@Injectable({ providedIn: 'root' })
export class NotificationService {
  private subject = new BehaviorSubject<string | null>(null);
  notification$: Observable<string | null> = this.subject.asObservable();
  notify(message: string) {
    try { this.subject.next(message); } catch (e) {  }
    setTimeout(() => { try { this.clear(); } catch(e) {} }, 5000);
  }
  clear() { try { this.subject.next(null); } catch(e) {} }
}
