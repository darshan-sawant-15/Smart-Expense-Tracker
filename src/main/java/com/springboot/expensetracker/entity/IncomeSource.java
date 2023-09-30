package com.springboot.expensetracker.entity;

import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class IncomeSource {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int isId;

	@Column(length = 100, nullable = false)
	@NotBlank(message = "Income source description cannot be blank")
	@Size(min = 2, max = 100, message = "Description should be between 2 to 100 characters")
	private String description;

	@Column(length = 45, nullable = false)
	@NotBlank(message = "Please select the appropriate income type")
	private String type;

	@Column(length = 10, nullable = false)
	@DecimalMin(value = "1", message = "Earnings must be atleast ₹1")
	@DecimalMax(value = "100000000", message = "Earnings can be upto ₹1000000000 only")
	private double earnings;

	@Size(max = 10000, message = "Notes should be a maximum of 10,000 characters")
	@Column(length = 10000)
	private String note;

	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private String createdAt;

	@JoinColumn(nullable = false, updatable = false)
	@ManyToOne()
	private User user;
	
	public List<IncomeChange> getIncomeChanges() {
		return incomeChanges;
	}

	public void setIncomeChanges(List<IncomeChange> incomeChanges) {
		this.incomeChanges = incomeChanges;
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "incomeSource")
	private List<IncomeChange> incomeChanges;

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public int getIsId() {
		return isId;
	}

	public void setIsId(int isId) {
		this.isId = isId;
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

	public double getEarnings() {
		return earnings;
	}

	public void setEarnings(double earnings) {
		this.earnings = earnings;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
