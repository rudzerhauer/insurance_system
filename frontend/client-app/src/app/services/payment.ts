import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { loadStripe } from "@stripe/stripe-js";
@Injectable({ providedIn: 'root' })
export class Payment {
  constructor(private http: HttpClient) {}
  createPayment(amount: number) {
  return this.http.post("/api/payments/create", {
      amount,
      currency: "eur"
    }, { withCredentials: true });
  }
  confirmPayment(policyId: number, amount: number) {
  return this.http.post("/api/payments/confirm", {
      amount,
      currency: "eur",
      policyId
    }, { withCredentials: true });
  }
  async startStripePayment(policy: any) {
    console.warn('startStripePayment: prefer using createPayment + Elements flow from the component');
  }
}
