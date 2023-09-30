package com.springboot.expensetracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class SavingsHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int shId;

	@Column(length = 10, nullable = false)
	private double amount;

	@Column(length = 100, nullable = false)
	private String goal;

	@Column(nullable = false)
	private String date;

	@ManyToOne
	SavingsGoal savingsGoal;

	@Override
	public String toString() {
		return "SavingsGoal [sId=" + shId + ", amount=" + amount + ", goal=" + goal + ", date=" + date + "]";
	}

	public int getShId() {
		return shId;
	}

	public void setShId(int shId) {
		this.shId = shId;
	}

	public SavingsGoal getSavingsGoal() {
		return savingsGoal;
	}

	public void setSavingsGoal(SavingsGoal savingsGoal) {
		this.savingsGoal = savingsGoal;
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



}
