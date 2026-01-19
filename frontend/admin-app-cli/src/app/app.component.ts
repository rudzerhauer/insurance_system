import { Component } from '@angular/core';
import { UsersComponent } from '../../../admin-app/src/app/users/users.component';
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [UsersComponent],
  template: `
    <div style="padding:16px">
      <h1>Admin - Users (CLI)</h1>
      <main>
        <app-users></app-users>
      </main>
    </div>
  `
})
export class AppComponent {}
