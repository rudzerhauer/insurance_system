import { Component, OnInit, AfterViewInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { PolicyService } from '../services/policy-service';
import { Payment } from '../services/payment';
import { Router } from '@angular/router';
import { loadStripe, Stripe, StripeElements, StripeCardElement } from '@stripe/stripe-js';
import { timeout as rxTimeout } from 'rxjs/operators';
import { of } from 'rxjs';
@Component({
  selector: 'app-policies',
  standalone: false,
  templateUrl: './policies.html',
  styleUrl: './policies.css'
})
export class Policies implements OnInit, AfterViewInit {
  policies: any[] = [];
  loading = false;
  showPaymentModal = false;
  currentPolicy: any = null;
  clientSecret: string | null = null;
  stripe: Stripe | null = null;
  elements: StripeElements | null = null;
  cardElement: StripeCardElement | null = null;
  paymentError: string | null = null;
  isConfirming = false; 
  constructor(private policyService: PolicyService, private paymentService: Payment, private cd: ChangeDetectorRef, private router: Router) {}
  ngOnInit() {
    this.loading = true;
    this.policyService.getPolicyTypes().subscribe({
      next: res => {
        this.policies = res;
        this.policyService.getMyPolicies().subscribe({
          next: myPolicies => {
            try {
              const ownedIds = new Set((myPolicies || []).map((p: any) => p.id));
              const ownedTypeIds = new Set<number>();
              (myPolicies || []).forEach((mp: any) => {
                const typeId = mp?.type?.id ?? mp?.policyType?.id ?? mp?.policyTypeId ?? mp?.typeId ?? mp?.type;
                if (typeId !== undefined && typeId !== null && !isNaN(Number(typeId))) {
                  ownedTypeIds.add(Number(typeId));
                }
              });
              this.policies = this.policies.map(p => {
                const byId = ownedIds.has(p.id);
                const byType = ownedTypeIds.has(p.id);
                return { ...p, owned: byId || byType };
              });
            } catch (e) { }
            try { this.cd.detectChanges(); } catch(e) {}
            this.loading = false;
          },
          error: err => {
            if (err?.status && err.status !== 401) {
              console.error('Failed to fetch my policies for ownership mapping', err);
            }
            try { this.cd.detectChanges(); } catch(e) { }
            this.loading = false;
          }
        });
      },
      error: err => { console.error("ERROR LOADING POLICIES", err); this.loading = false; }
    });
  }
  ngAfterViewInit() {
  }
  buy(policy: any) {
    this.openPaymentModal(policy);
  }
  async openPaymentModal(policy: any) {
    this.currentPolicy = policy;
    this.paymentError = null;
    this.showPaymentModal = true;
    this.clientSecret = null;
    const unitPriceEur = (policy?.basePrice ?? policy?.price ?? 0);
    if (!unitPriceEur || isNaN(unitPriceEur) || unitPriceEur <= 0) {
      this.paymentError = 'Invalid policy price';
      return;
    }
    const amountCents = Math.round(unitPriceEur * 100);
    this.paymentService.createPayment(amountCents).subscribe(async (res: any) => {
      if (res && res.clientSecret) {
        this.clientSecret = res.clientSecret;
        try {
          this.stripe = await loadStripe('pk_test_51S8QMuE70mAEAtJgvxD2R7vx4ZzmNEsL9oftuiBZn9Q8D1vlyxzn1bZD9knca47kt558XhXGpKBWDwzTS8nZjICK004rD0Rwy6');
          if (!this.stripe) { this.paymentError = 'Failed to load Stripe'; return; }
          this.elements = this.stripe.elements();
          const card = this.elements.create('card');
          card.mount('#card-element');
          this.cardElement = card as unknown as StripeCardElement;
        } catch (e: any) {
          console.error('Stripe Elements mount error', e);
          this.paymentError = 'Failed to initialize card form';
        }
      } else {
        console.error('createPayment response missing clientSecret', res);
        this.paymentError = 'Payment initialization failed';
      }
    }, err => {
      console.error('createPayment failed', err);
      this.paymentError = 'Could not create payment';
    });
  }
  async confirmCardPayment() {
    this.paymentError = null;
    if (this.isConfirming) return; 
    if (!this.stripe || !this.cardElement || !this.clientSecret || !this.currentPolicy) {
      this.paymentError = 'Payment not ready';
      return;
    }
    this.isConfirming = true;
    try {
      const confirmPromise = this.stripe.confirmCardPayment(this.clientSecret, {
        payment_method: { card: this.cardElement }
      });
      const withTimeout = (p: Promise<any>, ms: number) => {
        return new Promise((resolve, reject) => {
          const t = setTimeout(() => reject(new Error('confirmCardPayment timeout')), ms);
          p.then(r => { clearTimeout(t); resolve(r); }).catch(e => { clearTimeout(t); reject(e); });
        });
      };
  const result: any = await withTimeout(confirmPromise, 30000);
      if (result.error) {
        console.error('confirmCardPayment error', result.error);
        this.paymentError = result.error.message || 'Payment failed';
        return;
      }
      if (result.paymentIntent && result.paymentIntent.status === 'succeeded') {
        const confirmAmountEur = (this.currentPolicy?.basePrice ?? this.currentPolicy?.price ?? 0);
        this.paymentService.confirmPayment(this.currentPolicy.id, confirmAmountEur)
          .pipe(rxTimeout(30000))
          .subscribe((resp: any) => {
           const assignedPolicy = resp?.policy || resp?.assignedPolicy || resp?.data?.policy;
           if (assignedPolicy) {
             const exists = this.policies.some(p => p.id === assignedPolicy.id);
             assignedPolicy.owned = true;
             if (!exists) {
               this.policies.push(assignedPolicy);
             } else {
               this.policies = this.policies.map(p => p.id === assignedPolicy.id ? assignedPolicy : p);
             }
           } else if (this.currentPolicy) {
             this.policies = this.policies.map(p => p.id === this.currentPolicy.id ? { ...p, owned: true } : p);
           }
           try { this.cd.detectChanges(); } catch(e) { }
           this.showPaymentModal = false;
           this.isConfirming = false;
           try { this.router.navigate(['/my-policies']); } catch(e) { console.warn('Navigation failed', e); }
         }, err => {
           console.error('Backend confirmPayment failed', err);
           this.paymentError = 'Payment succeeded but server-side confirmation failed';
           this.isConfirming = false;
         });
      } else {
        this.paymentError = 'Payment not completed';
      }
    } catch (e: any) {
      console.error('confirmCardPayment exception', e);
      this.paymentError = e?.message || 'Payment confirmation error';
    } finally {
      if (this.showPaymentModal) this.isConfirming = false;
    }
  }
  closePaymentModal() {
    try {
      if (this.cardElement) { this.cardElement.unmount(); }
    } catch (e) { }
    this.showPaymentModal = false;
    this.currentPolicy = null;
    this.clientSecret = null;
    this.paymentError = null;
  }
}
