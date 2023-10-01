package com.springboot.expensetracker.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springboot.expensetracker.dao.SavingsGoalRepository;
import com.springboot.expensetracker.dao.UserRepository;
import com.springboot.expensetracker.entity.Expense;
import com.springboot.expensetracker.entity.SavingsGoal;
import com.springboot.expensetracker.entity.SavingsHistory;
import com.springboot.expensetracker.entity.User;
import com.springboot.expensetracker.helper.DateHelper;
import com.springboot.expensetracker.helper.Message;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/savings-goal/")
public class SavingsGoalController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SavingsGoalRepository savingsGoalRepository;

	@ModelAttribute
	public void addCommonData(Principal principal, Model model) {
		String name = principal.getName();
		User user = userRepository.getUserByUsername(name);
		model.addAttribute("username", user.getName());
		model.addAttribute("user", user);
		String fullCurrency = user.getCurrency();
		String currencySymbol = fullCurrency.substring(fullCurrency.indexOf('(') + 1, fullCurrency.length() - 1);
		System.out.println("Currency Symbol: " + currencySymbol);
		model.addAttribute("currencySymbol", currencySymbol);
	}

	@GetMapping("/set-savings-goal")
	public String setSavingsGoal(Model model, @ModelAttribute("user") User user) {
		try {
			SavingsGoal savingsGoal = user.getSavingsGoal();
			if (savingsGoal == null) {
				model.addAttribute("savingsGoal", new SavingsGoal());
			} else {
				model.addAttribute("savingsGoal", savingsGoal);
			}

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", new Message("Something went wrong", "alert-danger"));
		}
		return "user/savings-goal/set-savings-goal";
	}

	@PostMapping("/do-set-savings-goal")
	public String doSetSavingsGoal(@Valid @ModelAttribute("savingsGoal") SavingsGoal savingsGoal,
			BindingResult bindingResult, @ModelAttribute User user, RedirectAttributes redirectAttributes) {
		try {

			if (bindingResult.hasErrors()) {
				System.out.println(savingsGoal.getDate());
				System.out.println(bindingResult);
				return "user/savings-goal/set-savings-goal";
			}

			savingsGoal.setDate(DateHelper.getCurrentDate());

			// getOld
			if (user.getSavingsGoal() != null) {
				SavingsGoal oldSavingsGoal = savingsGoalRepository.findById(savingsGoal.getsId()).get();
				oldSavingsGoal.setUser(null);
				user.setSavingsGoal(null);
			}

			// setting savings history
			SavingsHistory history = new SavingsHistory();
			history.setAmount(savingsGoal.getAmount());
			history.setDate(savingsGoal.getDate());
			history.setGoal(savingsGoal.getGoal());
			history.setSavingsGoal(savingsGoal);
			List<SavingsHistory> savings = savingsGoal.getSavingsHistory();
			if (savings == null) {
				savings = new ArrayList<>();
			}
			savings.add(history);
			System.out.println("Savings History: " + history);
			savingsGoal.setSavingsHistory(savings);

			// setting new
			savingsGoal.setUser(user);
			System.out.println("SavingsGoal " + savingsGoal);
			user.setSavingsGoal(savingsGoal);

			User save = userRepository.save(user);
			System.out.println(save);

			if (user.getIncomeSources().isEmpty()) {
				redirectAttributes.addFlashAttribute("message",
						new Message("Now lets add an Income Source", "alert-warning"));
				return "redirect:/user/income-source/add-income-source";
			} else if (user.getExpenses().isEmpty()) {
				redirectAttributes.addFlashAttribute("message",
						new Message("Now lets add an Expense", "alert-warning"));
				return "redirect:/user/expense/add-expense";
			} else {
				redirectAttributes.addFlashAttribute("message", new Message("Savings Goal Set", "alert-success"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", new Message("Something Went Wrong", "alert-danger"));
		}
		return "redirect:set-savings-goal";
	}

}
