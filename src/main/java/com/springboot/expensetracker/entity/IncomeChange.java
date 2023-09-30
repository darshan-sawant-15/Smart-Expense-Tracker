package com.springboot.expensetracker.entity;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class IncomeChange {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int ic_id;

	@Column(length = 10, nullable = false)
	private double earnings;

	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private String createdAt;

	@JoinColumn(nullable = false, updatable = false)
	@ManyToOne()
	private IncomeSource incomeSource;

	public int getId() {
		return ic_id;
	}

	public void setId(int id) {
		this.ic_id = id;
	}

	public double getEarnings() {
		return earnings;
	}

	public void setEarnings(double earnings) {
		this.earnings = earnings;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public IncomeSource getIncomeSource() {
		return incomeSource;
	}

	public void setIncomeSource(IncomeSource incomeSource) {
		this.incomeSource = incomeSource;
	}
}
