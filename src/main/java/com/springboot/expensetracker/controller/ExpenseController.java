package com.springboot.expensetracker.controller;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.springboot.expensetracker.dao.ExpenseRepository;
import com.springboot.expensetracker.dao.UserRepository;
import com.springboot.expensetracker.entity.Expense;
import com.springboot.expensetracker.entity.User;
import com.springboot.expensetracker.helper.DateHelper;
import com.springboot.expensetracker.helper.Message;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/expense")
public class ExpenseController {
	@Autowired
	UserRepository userRepository;
	@Autowired
	ExpenseRepository expenseRepository;

	@ModelAttribute
	public void addCommonData(Principal principal, Model model) {
		String name = principal.getName();
		User user = userRepository.getUserByUsername(name);
		model.addAttribute("user", user);
		model.addAttribute("username", user.getName());
		String fullCurrency = user.getCurrency();
		String currencySymbol = fullCurrency.substring(fullCurrency.indexOf('(') + 1, fullCurrency.length() - 1);
		System.out.println("Currency Symbol: " + currencySymbol);
		model.addAttribute("currencySymbol", currencySymbol);
	}

	@GetMapping("/search-expense/{query}/{month}")
	public ResponseEntity<List<Expense>> searchExpense(@ModelAttribute("user") User user,
			@PathVariable("query") String query, @PathVariable("month") String month) {
		List<Expense> expenses;
		System.out.println(month);
		if (month.equals("Any")) {
			System.out.println("Reaching");
			expenses = expenseRepository.searchExpensesByDesc(query, user);
		} else {
			expenses = expenseRepository.searchExpensesByDescAndMonth(query, user, month);
		}
		return ResponseEntity.ok(expenses);
	}

	@GetMapping("/add-expense")
	public String addExpense(Model model, @ModelAttribute("expense") Expense expense) {
		try {
			if (expense == null) {
				model.addAttribute("expense", new Expense());
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", new Message("Something went wrong", "alert-danger"));
		}
		return "user/expense/add-expense";
	}

	@PostMapping("/do-add-expense")
	public String doAddExpense(@Valid @ModelAttribute("expense") Expense expense, BindingResult bindingResult,
			@ModelAttribute User user, RedirectAttributes redirectAttributes) {
		try {

			if (bindingResult.hasErrors()) {
				System.out.println(expense.getDate());
				System.out.println(bindingResult);
				return "user/expense/add-expense";
			}

			if (DateHelper.isAfterToday(expense.getDate())) {
				redirectAttributes.addFlashAttribute("errorMessage",
						new Message("Expense date cannot be in the future", "dateError"));
				redirectAttributes.addFlashAttribute("expense", expense);
				return "redirect:add-expense";
			}

			expense.setUser(user);
			user.getExpenses().add(expense);
			User save = userRepository.save(user);
			System.out.println(save);

			if (user.getIncomeSources().isEmpty()) {
				redirectAttributes.addFlashAttribute("message",
						new Message("Now lets add an Income Source", "alert-warning"));
				return "redirect:/user/income-source/add-income-source";
			} else if (user.getSavingsGoal() == null) {
				redirectAttributes.addFlashAttribute("message",
						new Message("Now set a Savings Goal For Yourself", "alert-warning"));
				return "redirect:/user/savings-goal/set-savings-goal";
			} else {
				redirectAttributes.addFlashAttribute("message", new Message("Expense Added", "alert-success"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", new Message("Something Went Wrong", "alert-danger"));
		}
		return "redirect:add-expense";
	}

	// to view expenses
	@GetMapping("/view-expenses/{page}")
	public String viewExpensesGet(@PathVariable("page") Integer currentPage, Model model,
			@ModelAttribute("user") User user, @RequestParam(value = "month", required = false) String selectedMonth) {
		try {
			Integer itemsPerPage = 10;
			Pageable pageable = PageRequest.of(currentPage, itemsPerPage);
			Page<Expense> expenses;
			System.out.println("Selected month: " + selectedMonth);
			if (selectedMonth == null || selectedMonth.isEmpty() || selectedMonth.equals("Any")) {
				expenses = expenseRepository.getExpensesByUser(user, pageable);
			} else {
				expenses = expenseRepository.getExpensesByUserAndMonth(user, selectedMonth, pageable);
			}
			if (expenses.isEmpty()) {
				if (expenses.getTotalPages() != 0 && currentPage >= expenses.getTotalPages()) {
					currentPage = expenses.getTotalPages() - 1;
					return "redirect:" + currentPage;
				} else {
					model.addAttribute("message",
							new Message("You haven't added any Expense items as of yet", "alert-danger"));
				}
			} else {
				List<String> monthYearList = expenseRepository.getMonthYearList(user);
				model.addAttribute("monthYearList", monthYearList);
				model.addAttribute("month", selectedMonth);
				model.addAttribute("currentPage", currentPage);
				model.addAttribute("totalPages", expenses.getTotalPages());
				model.addAttribute("expenses", expenses);
				model.addAttribute("itemsPerPage", itemsPerPage);
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", new Message("Something went wrong", "alert-danger"));
		}
		return "user/expense/view-expenses";
	}

	// detail-expense-view
	@GetMapping("/view-expense/{expenseId}/{currentPage}")
	public String viewExpense(@PathVariable("expenseId") Integer expenseId,
			@PathVariable("currentPage") Integer currentPage, Model model, @ModelAttribute("user") User user) {
		try {

			System.out.println(expenseId);
			Expense expense = expenseRepository.findById(expenseId).get();
			if (user.getId() == expense.getUser().getId()) {
				model.addAttribute("expense", expense);
				model.addAttribute("currentPage", currentPage);
			} else {
				model.addAttribute("message",
						new Message("You don't have permissions to View this Expense item", "alert-danger"));
			}
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			model.addAttribute("message", new Message("No such Expense item", "alert-danger"));
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", new Message("Something went wrong", "alert-danger"));
		}
		return "user/expense/view-expense";
	}

	// delete expense
	@GetMapping("/delete-expense/{expenseId}/{currentPage}")
	public RedirectView deleteExpense(@PathVariable("expenseId") Integer expenseId,
			@PathVariable("currentPage") Integer currentPage, Model model, @ModelAttribute("user") User user,
			RedirectAttributes attributes) {
		try {
			System.out.println(user);
			System.out.println(expenseId);
			Expense expense = expenseRepository.findById(expenseId).get();
			if (user.getId() == expense.getUser().getId()) {
				System.out.println("Expense to be deleted: " + expense);
				user.getExpenses().remove(expense);// removing expense from user
				expenseRepository.deleteById(expenseId);
				attributes.addFlashAttribute("message", new Message("Expense Deleted Successfully", "alert-success"));
			} else {
				attributes.addFlashAttribute("message",
						new Message("You don't have permissions to Delete this Expense item", "alert-danger"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			attributes.addFlashAttribute("message", new Message("Something went wrong", "alert-danger"));
		}
		return new RedirectView("/user/expense/view-expenses/" + currentPage);
	}

	// update expense
	@GetMapping("/update-expense/{expenseId}")
	public String updateExpense(@PathVariable("expenseId") Integer expenseId, Model model,
			@ModelAttribute("user") User user) {
		System.out.println("Reaching");
		try {
			Expense expense = expenseRepository.findById(expenseId).get();
			System.out.println(expense);
			if (user.getId() == expense.getUser().getId()) {
				model.addAttribute(expense);
			} else {
				model.addAttribute("message",
						new Message("You don't have permissions to Update this Expense item", "alert-danger"));
			}
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			model.addAttribute("message", new Message("No such Expense item", "alert-danger"));
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", new Message("Something went wrong", "alert-danger"));
		}
		return "user/expense/update-expense";
	}

	@PostMapping("/do-update-expense")
	public String doUpdateExpense(@Valid @ModelAttribute("expense") Expense newExpense, BindingResult bindingResult,
			@ModelAttribute User user, RedirectAttributes redirectAttributes) {
		try {
			System.out.println("Reachingggggg");
			System.out.println("Expense from form: " + newExpense);
			if (bindingResult.hasErrors()) {
				System.out.println(newExpense.getDate());
				System.out.println(bindingResult);
				return "user/expense/update-expense";
			}
			// getting old expense
			Expense oldExpense = expenseRepository.findById(newExpense.getExpId()).get();
			System.out.println("Old Expense: " + oldExpense);
			oldExpense.setUser(null);
			user.getExpenses().remove(oldExpense);

			// setting new expense
			newExpense.setUser(user);
			user.getExpenses().add(newExpense);
			User save = userRepository.save(user);
			System.out.println(save);
			Message updateMessage = new Message("Expense Updated", "alert-success");
			redirectAttributes.addFlashAttribute("updateMessage", updateMessage);
		} catch (Exception e) {
			e.printStackTrace();
			Message updateMessage = new Message("Something Went Wrong", "alert-danger");
			redirectAttributes.addFlashAttribute("updateMessage", updateMessage);
		}
		return "redirect:view-expense/" + newExpense.getExpId() + "/0";
	}
}
