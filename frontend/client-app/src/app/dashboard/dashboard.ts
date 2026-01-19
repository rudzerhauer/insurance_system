import { Component, OnInit } from '@angular/core';
import { PolicyService } from '../services/policy-service';
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.html',
  standalone : false,
  styleUrls: ['./dashboard.css']
})
export class Dashboard implements OnInit {
  policies: any[] = [];
  constructor(private policyService: PolicyService) {}
  ngOnInit(): void {
    this.policyService.getAllPolicies().subscribe(data => {
      this.policies = data;
    });
  }
  buyPolicy(id: number) {
    this.policyService.buyPolicy(id).subscribe(() => {
      alert("Policy bought!");
    });
  }
}
