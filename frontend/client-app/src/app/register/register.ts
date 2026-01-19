import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  username = '';
  password = '';
  email = '';
  error = '';
  constructor(private auth : AuthService, private router : Router) {}
  onRegister() {
    this.auth.register(this.username, this.email, this.password).subscribe({
        next : () => {
          localStorage.setItem('tempUsername', this.username);
          this.router.navigate(['/verify']);
        },
        error: () => this.error = "Registration failed"
    });
  }
}
