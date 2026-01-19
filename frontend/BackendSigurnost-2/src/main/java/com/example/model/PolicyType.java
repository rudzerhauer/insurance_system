package com.example.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name="policy_types")
public class PolicyType {
	public PolicyType() {}
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	public PolicyType(Long id, String name, String description, Double basePrice) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.basePrice = basePrice;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Double getBasePrice() {
		return basePrice;
	}
	public void setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
	}
	@Column(unique=true, nullable = false)
	private String name;
	private String description;
	@Column(name="base_price", nullable=false)
	private Double basePrice;
}
