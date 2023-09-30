package com.springboot.expensetracker.entity;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Expense {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int expId;

	@Column(length = 100, nullable = false)
	@NotBlank(message = "Expense description cannot be blank")
	@Size(min = 2, max = 100, message = "Description should be between 2 to 100 characters")
	private String description;

	@Column(length = 45, nullable = false)
	@NotBlank(message = "Please select the appropriate expense type")
	private String type;

	@Column(length = 10, nullable = false)
	@DecimalMin(value = "1", message = "Amount must be atleast ₹1")
	@DecimalMax(value = "100000000", message = "Amount can be upto ₹1000000000 only")
	private double amount;

	@Column(nullable = false)
	@NotBlank(message = "Expense date cannot be empty")
	private String date;

	@Size(max = 10000, message = "Notes should be a maximum of 10,000 characters")
	@Column(length = 10000)
	private String note;

	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private String createdAt;

	@JoinColumn(nullable = false, updatable = false)
	@ManyToOne()
	private User user;

	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return date;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public int getExpId() {
		return expId;
	}

	public void setExpId(int expId) {
		this.expId = expId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String toString() {
		return "Expense [expId=" + expId + ", description=" + description + ", type=" + type + ", amount=" + amount
				+ ", date=" + date + ", user=" + user + "]";
	}
}
