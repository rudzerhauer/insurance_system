import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsersService, UserDto } from './users.service';
@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {
  users: UserDto[] = [];
  loading = false;
  error: string | null = null;
  constructor(private svc: UsersService) {}
  ngOnInit(): void {
    this.reload();
  }
  reload() {
    this.loading = true;
    this.error = null;
    this.svc.list().subscribe({
      next: (u) => { this.users = u; this.loading = false; },
      error: (e) => { this.error = e?.message || 'Failed'; this.loading = false; }
    });
  }
  deleteUser(id: number) {
    if (!confirm('Delete user?')) return;
    this.svc.delete(id).subscribe({ next: () => this.reload(), error: (e) => alert('Delete failed') });
  }
}
