import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
export interface UserDto {
  id: number;
  email: string;
  role?: string;
}
@Injectable({ providedIn: 'root' })
export class UsersService {
  constructor(private http: HttpClient) {}
  list(): Observable<UserDto[]> {
    return this.http.get<UserDto[]>('/api/admin/users', { withCredentials: true });
  }
  get(id: number) {
    return this.http.get<UserDto>(`/api/admin/users/${id}`, { withCredentials: true });
  }
  update(id: number, payload: Partial<UserDto>) {
    return this.http.put<UserDto>(`/api/admin/users/${id}`, payload, { withCredentials: true });
  }
  delete(id: number) {
    return this.http.delete(`/api/admin/users/${id}`, { withCredentials: true });
  }
}
