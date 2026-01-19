package com.example.model;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
@Entity
@Table(name="policies")
public class Policy {
	public Policy() {}
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public PolicyType getType() {
		return type;
	}
	public void setType(PolicyType type) {
		this.type = type;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public LocalDate getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}
	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
	private User user;
	@ManyToOne
	@JoinColumn(name = "type_id", nullable=false)
	private PolicyType type;
	@Column(name="start_date", nullable = false)
	private LocalDate startDate;
	@Column(name="end_date", nullable=false)
	private LocalDate endDate;
	@Column(nullable=false)
	private Double price;
	@Column(name= "created_at", nullable=false)
	private LocalDate createdAt = LocalDate.now();
}
