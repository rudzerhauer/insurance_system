package com.example.model;
public class PaymentRequest {
	private Long userId;
	private Long policyId;
	private Long amount;
	private String currency;
	public Long getUserId() { return userId; }
	public void setUserId(Long userId) { this.userId = userId; }
	public Long getPolicyId() { return policyId; }
	public void setPolicyId(Long policyId) { this.policyId = policyId; }
	public Long getAmount() {
		return this.amount;
	}
	public String getCurrency() {
		return this.currency;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
