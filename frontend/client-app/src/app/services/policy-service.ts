import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
@Injectable({ providedIn: 'root' })
export class PolicyService {
  private apiUrl = '/api/policies';
  constructor(private http: HttpClient) {}
  getAllPolicies(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}`, { withCredentials: true });
  }
  getMyPolicies(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/my`, { withCredentials: true });
  }
  getPolicyTypes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/types`, { withCredentials: true });
  }
  buyPolicy(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/buy/${id}`, {}, { withCredentials: true });
  }
  cancelPolicy(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`, { withCredentials: true });
  }
}
