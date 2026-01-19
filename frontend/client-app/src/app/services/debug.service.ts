import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
@Injectable({ providedIn: 'root' })
export class DebugService {
  private lastErrorSub = new BehaviorSubject<any>(null);
  lastError$ = this.lastErrorSub.asObservable();
  setLastError(err: any) {
    try { this.lastErrorSub.next(err); } catch(e) {  }
  }
}
