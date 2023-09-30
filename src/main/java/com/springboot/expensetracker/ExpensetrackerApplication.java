package com.springboot.expensetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ExpensetrackerApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ExpensetrackerApplication.class, args);
//		UserRepository uR = context.getBean(UserRepository.class);
//		User u  = new User();
//		u.setName("Darshan");
//		u.setEmail("darsham@mail.com");
//		u.setPhone("83782");
//		u.setGoals("Yessir");
//		Expense e = new Expense();
//		e.setDescription("This is grocery");
//		e.setType("Home");
//		e.setAmount(983);
//		u.setExpenses(List.of(e));
//		uR.save(u);
		
	}

}
