package com.springboot.expensetracker.entity;

import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.SourceType;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class SavingsGoal {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int sId;

	@Column(length = 10, nullable = false)
	@DecimalMin(value = "1", message = "Amount must be atleast 1")
	@DecimalMax(value = "100000000", message = "Amount can be upto 1000000000 only")
	private double amount;

	@Column(length = 100, nullable = false)
	@NotBlank(message = "Savings goal description cannot be blank")
	@Size(min = 2, max = 10000, message = "The savings goal description should be between 2 to 10000 characters")
	private String goal;

	@Column(nullable = false)
	private String date;

	@OneToOne
	private User user;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "savingsGoal")
	private List<SavingsHistory> savingsHistory;
	
	
	@Override
	public String toString() {
		return "SavingsGoal [sId=" + sId + ", amount=" + amount + ", goal=" + goal + ", date=" + date + ", user=" + user
				+ "]";
	}

	public List<SavingsHistory> getSavingsHistory() {
		return savingsHistory;
	}

	public void setSavingsHistory(List<SavingsHistory> savingsHistory) {
		this.savingsHistory = savingsHistory;
	}

	public int getsId() {
		return sId;
	}

	public void setsId(int sId) {
		this.sId = sId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
