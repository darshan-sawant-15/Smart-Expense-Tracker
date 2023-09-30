package com.springboot.expensetracker.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.springboot.expensetracker.dao.IncomeSourceRepository;
import com.springboot.expensetracker.dao.UserRepository;
import com.springboot.expensetracker.entity.IncomeChange;
import com.springboot.expensetracker.entity.IncomeSource;
import com.springboot.expensetracker.entity.User;
import com.springboot.expensetracker.helper.Message;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/income-source")
public class IncomeSourceController {
	@Autowired
	UserRepository userRepository;
	@Autowired
	IncomeSourceRepository incomeSourceRepository;

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

	@GetMapping("/add-income-source")
	public String addIncomeSource(Model model, @ModelAttribute("user") User user) {
		try {
			model.addAttribute("incomeSource", new IncomeSource());
			if (user.getIncomeSources().size() == 10) {
				model.addAttribute("fullMessage",
						new Message("You can only have upto 10 Income Sources", "alert-danger"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", new Message("Something went wrong", "alert-danger"));
		}
		return "user/income-source/add-income-source";
	}

	@PostMapping("/do-add-income-source")
	public String doAddIncomeSource(@Valid @ModelAttribute("incomeSource") IncomeSource incomeSource,
			BindingResult bindingResult, @ModelAttribute User user, RedirectAttributes redirectAttributes) {
		try {

			if (bindingResult.hasErrors()) {
				System.out.println(bindingResult);
				return "user/income-source/add-income-source";
			}
			IncomeChange ic = new IncomeChange();
			ic.setEarnings(incomeSource.getEarnings());
			ic.setIncomeSource(incomeSource);
			incomeSource.setUser(user);
			List<IncomeChange> changes = new ArrayList<>();
			changes.add(ic);
			incomeSource.setIncomeChanges(changes);

			System.out.println(incomeSource);
			user.getIncomeSources().add(incomeSource);
			userRepository.save(user);

			if (user.getExpenses().isEmpty()) {
				redirectAttributes.addFlashAttribute("message",
						new Message("Now lets add an Expense", "alert-warning"));
				return "redirect:/user/expense/add-expense";
			} else if (user.getSavingsGoal() == null) {
				redirectAttributes.addFlashAttribute("message",
						new Message("Now set Savings Goal For Yourself", "alert-warning"));
				return "redirect:/user/savings-goal/set-savings-goal";
			} else {
				redirectAttributes.addFlashAttribute("message", new Message("Income Source Added", "alert-success"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", new Message("Something Went Wrong", "alert-danger"));
		}
		return "redirect:add-income-source";
	}

	@GetMapping("/view-income-sources/{page}")
	public String viewIncomeSources(@PathVariable("page") Integer currentPage, Model model,
			@ModelAttribute("user") User user) {
		try {
			Integer itemsPerPage = 10;
			Pageable pageable = PageRequest.of(currentPage, itemsPerPage);
			Page<IncomeSource> sources = incomeSourceRepository.getIncomeSourcesByUser(user, pageable);
			if (sources.isEmpty()) {
				System.out.println("Sources: " + sources.isEmpty() + " " + sources);
				model.addAttribute("message",
						new Message("You haven't added any Income Sources as of yet", "alert-danger"));
			} else {
				model.addAttribute("currentPage", currentPage);
				model.addAttribute("totalPages", sources.getTotalPages());
				model.addAttribute("sources", sources);
				model.addAttribute("itemsPerPage", itemsPerPage);
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", new Message("Something went wrong", "alert-danger"));
		}
		return "user/income-source/view-income-sources";
	}

	// detail-income-source-view
	@GetMapping("/view-income-source/{sourceId}")
	public String viewIncomeSource(@PathVariable("sourceId") Integer sourceId, Model model,
			@ModelAttribute("user") User user) {
		try {
			System.out.println(sourceId);
			IncomeSource source = incomeSourceRepository.findById(sourceId).get();
			if (source.getUser().getId() == user.getId()) {
				model.addAttribute("source", source);
			} else {
				model.addAttribute("message",
						new Message("You don't have permissions to View this Income Source", "alert-danger"));
			}
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			model.addAttribute("message", new Message("No such Income Source", "alert-danger"));
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", new Message("Something went wrong", "alert-danger"));
		}
		return "user/income-source/view-income-source";
	}

	@GetMapping("/delete-income-source/{sourceId}")
	public RedirectView deleteIncomeSource(@PathVariable("sourceId") Integer sourceId, Model model,
			@ModelAttribute("user") User user, RedirectAttributes attributes) {
		try {
			System.out.println(user);
			System.out.println(sourceId);
			IncomeSource source = incomeSourceRepository.findById(sourceId).get();
			if (user.getId() == source.getUser().getId()) {
				user.getIncomeSources().remove(source); // removing source from user
				incomeSourceRepository.deleteById(sourceId);
				attributes.addFlashAttribute("message",
						new Message("Income Source Deleted Successfully", "alert-success"));
			} else {
				attributes.addFlashAttribute("message",
						new Message("You don't have permissions to Delete this Income Source", "alert-danger"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			attributes.addFlashAttribute("message", new Message("Something went wrong", "alert-danger"));
		}
		return new RedirectView("/user/income-source/view-income-sources/0");
	}

	// update income source
	@GetMapping("/update-income-source/{sourceId}")
	public String updateIncomeSource(@PathVariable("sourceId") Integer sourceId, Model model,
			@ModelAttribute("user") User user) {
		System.out.println("Reaching");
		try {
			IncomeSource source = incomeSourceRepository.findById(sourceId).get();
			System.out.println(source);
			if (user.getId() == source.getUser().getId()) {
				model.addAttribute("source", source);

			} else {
				model.addAttribute("message",
						new Message("You don't have permissions to Update this Income Source", "alert-danger"));
			}
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			model.addAttribute("message", new Message("No such Income Source", "alert-danger"));
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", new Message("Something went wrong", "alert-danger"));
		}
		return "user/income-source/update-income-source";
	}

	@PostMapping("/do-update-income-source")
	public String doUpdateIncomeSource(@Valid @ModelAttribute("source") IncomeSource newSource,
			BindingResult bindingResult, @ModelAttribute User user, RedirectAttributes redirectAttributes) {
		try {
			System.out.println("Reachingggggg");
			System.out.println("Income from form: " + newSource);
			if (bindingResult.hasErrors()) {
				System.out.println(bindingResult);
				return "user/income-source/update-income-source";
			}
			// getting old IS
			IncomeSource oldSource = incomeSourceRepository.findById(newSource.getIsId()).get();
			System.out.println("Old Income: " + oldSource);
			oldSource.setUser(null);
			user.getIncomeSources().remove(oldSource);

			// setting new source
			newSource.setUser(user);
			if (oldSource.getEarnings() != newSource.getEarnings()) {
				IncomeChange ic = new IncomeChange();
				ic.setEarnings(newSource.getEarnings());
				ic.setIncomeSource(newSource);
				List<IncomeChange> changes = newSource.getIncomeChanges();
				if (changes == null) {
					changes = new ArrayList<>();
				}
				changes.add(ic);
				newSource.setIncomeChanges(changes);
			}
			user.getIncomeSources().add(newSource);
			User save = userRepository.save(user);
			System.out.println(save);
			Message updateMessage = new Message("Income Source Updated", "alert-success");
			redirectAttributes.addFlashAttribute("updateMessage", updateMessage);
		} catch (Exception e) {
			e.printStackTrace();
			Message updateMessage = new Message("Something Went Wrong", "alert-danger");
			redirectAttributes.addFlashAttribute("updateMessage", updateMessage);
		}
		return "redirect:view-income-source/" + newSource.getIsId();
	}
}
