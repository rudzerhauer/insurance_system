import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { PolicyService } from '../services/policy-service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-my-policies',
  standalone: false,
  templateUrl: './my-policies.html',
  styleUrl: './my-policies.css'
})
export class MyPolicies implements OnInit {
  myPolicies : any[] = [];
  loading = false;
  policyTypes: any[] = [];
  constructor(private policyService : PolicyService, private router: Router, private cd: ChangeDetectorRef) {}
  ngOnInit(): void {
      this.loading = true;
      this.policyService.getMyPolicies().subscribe({
        next: data => {
          const currentUser = localStorage.getItem('currentUsername');
          if (currentUser) {
            try {
              this.myPolicies = (data || []).filter((p: any) => {
                const ownerName = p?.user?.username || p?.owner?.username || p?.username;
                return !!ownerName && ownerName === currentUser;
              });
            } catch (e) {
              this.myPolicies = data;
            }
          } else {
            this.myPolicies = data;
          }
          this.loading = false;
          try { this.cd.detectChanges(); } catch(e) {  }
        },
        error: err => {
          console.error('Failed to load my policies', err);
          this.loading = false;
          if (err?.status === 401) {
            this.router.navigate(['/login']);
          }
        }
      });
      this.policyService.getPolicyTypes()?.subscribe?.({
        next: types => {
          this.policyTypes = types || [];
          try { this.cd.detectChanges(); } catch(e) {  }
        },
        error: _ => {  }
      });
  }
  cancelPolicy(id:number) {
    this.policyService.cancelPolicy(id).subscribe(() => {
      this.myPolicies = this.myPolicies.filter(p => p.id !==id);
    })
  }
}
