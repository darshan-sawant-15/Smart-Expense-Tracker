package com.springboot.expensetracker.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(length = 30, nullable = false)
	@Size(min = 2, max = 30, message = "Length of the name should be between 2-30 characters")
	@NotBlank(message = "Name must not be blank")
	private String name;

	@Column(unique = true, nullable = false)
	@Email(regexp = "^(?=.{1,254}$)(?![.-])(?!.*[.-]{2})[a-zA-Z0-9.-]+(?<![.-])@(?![.-])[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Enter valid email id")
	@NotBlank(message = "Email must not be blank")
	private String email;

	@Column(unique = true, length = 10, nullable = false)
	@Size(min = 10, max = 10, message = "Enter valid Indian phone number")
	@NotBlank(message = "Phone no. must not be blank")
	private String phone;

	@Column(length = 256, nullable = false)
	@Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$", message = "Password must be minimum eight characters, at least one upper case English letter, one lower case English letter, one number and one special character")
	@NotBlank(message = "Password must not be blank")
	private String password;

	@Column(length = 500, nullable = false)
	@Size(max = 500, message = "Length of the goals field should not be more than 500 characters")
	@NotBlank(message = "Goals field must not be blank")
	private String goals;

	@Column(length = 2, nullable = false)
	@Size(max = 2, min = 1, message = "Enter valid age number")
	@NotBlank(message = "Age must not be blank")
	private String age;

	@Column(length = 45, nullable = false)
	@NotBlank(message = "Please select your preferred currency")
	private String currency;

	private String imageUrl;

	private boolean enabled;
	
	private String role;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private List<Expense> expenses;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private List<IncomeSource> incomeSources;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
	private SavingsGoal savingsGoal;

	public List<Expense> getExpenses() {
		return expenses;
	}

	public void setExpenses(List<Expense> expenses) {
		this.expenses = expenses;
	}

	public List<IncomeSource> getIncomeSources() {
		return incomeSources;
	}

	public void setIncomeSources(List<IncomeSource> incomeSources) {
		this.incomeSources = incomeSources;
	}

	public SavingsGoal getSavingsGoal() {
		return savingsGoal;
	}

	public void setSavingsGoal(SavingsGoal savingsGoal) {
		this.savingsGoal = savingsGoal;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGoals() {
		return goals;
	}

	public void setGoals(String goals) {
		this.goals = goals;
	}

	public String getAge() {
		return age;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public void setAge(String age) {
		this.age = age;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", phone=" + phone + ", password=" + password
				+ ", goals=" + goals + ", age=" + age + ", currency=" + currency + ", imageUrl=" + imageUrl
				+ ", enabled=" + enabled + ", role=" + role + "]";
	}

	
}
