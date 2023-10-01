package com.springboot.expensetracker.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springboot.expensetracker.dao.ExpenseRepository;
import com.springboot.expensetracker.dao.IncomeChangeRepository;
import com.springboot.expensetracker.dao.IncomeSourceRepository;
import com.springboot.expensetracker.dao.SavingsHistoryRepository;
import com.springboot.expensetracker.dao.UserRepository;
import com.springboot.expensetracker.entity.Expense;
import com.springboot.expensetracker.entity.SavingsHistory;
import com.springboot.expensetracker.entity.User;
import com.springboot.expensetracker.helper.ConvertListToMap;
import com.springboot.expensetracker.helper.DateHelper;
import com.springboot.expensetracker.helper.GetMapsForCharts;
import com.springboot.expensetracker.helper.GetSaveablePercentage;
import com.springboot.expensetracker.helper.Message;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ExpenseRepository expenseRepository;
	@Autowired
	private IncomeSourceRepository incomeRepository;
	@Autowired
	private IncomeChangeRepository incomeChangeRepository;
	@Autowired
	private SavingsHistoryRepository savingsHistoryRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

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

	@GetMapping("/dashboard")
	public String getUserDashboard(Model model, @ModelAttribute("user") User user, RedirectAttributes attributes) {
		try {
			if (user.getIncomeSources().isEmpty()) {
				attributes.addFlashAttribute("message",
						new Message("First lets add an Income Source", "alert-warning"));
				return "redirect:income-source/add-income-source";
			} else if (user.getExpenses().isEmpty()) {
				attributes.addFlashAttribute("message", new Message("First lets add an Expense", "alert-warning"));
				return "redirect:expense/add-expense";
			} else if (user.getSavingsGoal() == null) {
				attributes.addFlashAttribute("message",
						new Message("First set a Savings Goal For Yourself", "alert-warning"));
				return "redirect:savings-goal/set-savings-goal";
			} else {

				model.addAttribute("showSideBar", true);

				handleExpenseReportSection(user, model, DateHelper.getCurrentMonth());
				handleSavingsMeterSection(user, model, DateHelper.getCurrentMonth());

				GetSaveablePercentage percentage = new GetSaveablePercentage();
				Double saveablePercent = percentage.getSaveablePercentageForCurrentMonth(user, expenseRepository,
						incomeChangeRepository, savingsHistoryRepository);

			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", new Message("Something went wrong", "alert-danger"));
		}
		return "user/dashboard";

	}

	@GetMapping("/edit-profile")
	public String getEditProfile(@RequestParam(value = "tab", required = false) Integer tab,
			@ModelAttribute("user") User user, Model model) {
		if (tab == null) {
			tab = 1;
		}

		model.addAttribute("user", user);

		System.out.println(tab);

		model.addAttribute("oldPhone", user.getPhone());
		model.addAttribute("oldEmail", user.getEmail());
		model.addAttribute("tab", tab);
		return "user/edit-profile";
	}

	@PostMapping("/do-edit-gen-info")
	public String doEditGenInfo(@Valid @ModelAttribute("user") User updatedUser, BindingResult bindingResult,
			Model model, RedirectAttributes attributes, @RequestParam("oldPhone") String oldPhone,
			@RequestParam("oldEmail") String oldEmail, Principal principal) {
		try {

			if (bindingResult.hasErrors()) {
				System.out.println(bindingResult);
				model.addAttribute("tab", 1);
				return "user/edit-profile";
			}
			String newPhone = updatedUser.getPhone();
			String newEmail = updatedUser.getEmail();
			if (userRepository.checkIfPhoneExists(newPhone) == 1 && !newPhone.equals(oldPhone)) {
				attributes.addFlashAttribute("errorMessage",
						new Message("Phone no. has been used already", "phoneError"));
				attributes.addFlashAttribute("user", updatedUser);
				return "redirect:edit-profile";
			}

			if (userRepository.checkIfEmailExists(newEmail) == 1 && !newEmail.equals(oldEmail)) {
				attributes.addFlashAttribute("errorMessage", new Message("Email has been used already", "emailError"));
				attributes.addFlashAttribute("user", updatedUser);
				return "redirect:edit-profile";
			}

			System.out.println(updatedUser);
			User user = userRepository.save(updatedUser);
			if (!oldEmail.equals(newEmail)) {
				attributes.addFlashAttribute("message",
						new Message("Email updated, login with your new email", "alert-success"));
				return "redirect:/logout";
			}

			attributes.addAttribute("user", user);
			attributes.addFlashAttribute("message", new Message("Profile Updated Successfully", "alert-success"));
			return "redirect:dashboard";

		} catch (Exception e) {
			e.printStackTrace();
			attributes.addFlashAttribute("genMessage", new Message("Something went wrong", "alert-danger"));
			attributes.addAttribute("tab", 1);
			return "redirect:edit-profile";
		}
	}

	@PostMapping("/do-edit-password")
	public String doEditPassword(@RequestParam("npassword") String npassword,
			@RequestParam("cpassword") String cpassword, @RequestParam("ccpassword") String ccpassword,
			@ModelAttribute("user") User user, Model model, RedirectAttributes attributes) {
		try {

			String oldPassEncoded = userRepository.findById(user.getId()).get().getPassword();
			System.out.println(oldPassEncoded);

			if (cpassword.isEmpty()) {
				attributes.addFlashAttribute("errorMessage",
						new Message("Current Password Cannot Be Empty", "cpasswordError"));
				attributes.addAttribute("tab", 2);
				return "redirect:edit-profile";
			}

			if (npassword.isEmpty()) {
				attributes.addFlashAttribute("errorMessage",
						new Message("New Password Cannot Be Empty", "npasswordError"));
				attributes.addAttribute("tab", 2);
				return "redirect:edit-profile";
			}

			String passRegex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$";

			if (!npassword.matches(passRegex)) {
				attributes.addFlashAttribute("errorMessage", new Message(
						"New Password must have minimum eight characters, at least one upper case English letter, one lower case English letter, one number and one special character",
						"npasswordError"));
				attributes.addAttribute("tab", 2);
				return "redirect:edit-profile";
			}

			if (ccpassword.isEmpty()) {
				attributes.addFlashAttribute("errorMessage",
						new Message("You Must Enter The New Password Twice", "ccpasswordError"));
				attributes.addAttribute("tab", 2);
				return "redirect:edit-profile";
			}

			if (!bCryptPasswordEncoder.matches(cpassword, oldPassEncoded)) {
				attributes.addFlashAttribute("errorMessage",
						new Message("Incorrect Current Password", "cpasswordError"));
				attributes.addAttribute("tab", 2);
				return "redirect:edit-profile";
			}

			if (npassword.equals(cpassword)) {
				attributes.addFlashAttribute("errorMessage",
						new Message("New Password cannot be same as the Current Password", "npasswordError"));
				attributes.addAttribute("tab", 2);
				return "redirect:edit-profile";
			}

			if (!npassword.equals(ccpassword)) {
				attributes.addFlashAttribute("errorMessage", new Message("Passwords don't match", "ccpasswordError"));
				attributes.addAttribute("tab", 2);
				return "redirect:edit-profile";
			}

			user.setPassword(bCryptPasswordEncoder.encode(npassword));
			userRepository.save(user);
			return "redirect:/logout";
		} catch (Exception e) {
			e.printStackTrace();
			attributes.addFlashAttribute("passMessage", new Message("Something went wrong", "alert-danger"));
			attributes.addAttribute("tab", 2);
			return "redirect:edit-profile";
		}

	}

	@GetMapping("/savings-history")
	public String savingsHistory(@ModelAttribute("user") User user, Model model,
			@RequestParam(value = "monthSM", required = false) String selectedMonth, RedirectAttributes attributes) {
		try {
			if (user.getIncomeSources().isEmpty()) {
				attributes.addFlashAttribute("message",
						new Message("First lets add an Income Source", "alert-warning"));
				return "redirect:income-source/add-income-source";
			} else if (user.getExpenses().isEmpty()) {
				attributes.addFlashAttribute("message", new Message("First lets add an Expense", "alert-warning"));
				return "redirect:expense/add-expense";
			} else if (user.getSavingsGoal() == null) {
				attributes.addFlashAttribute("message",
						new Message("First set a Savings Goal For Yourself", "alert-warning"));
				return "redirect:savings-goal/set-savings-goal";
			} else {
				String firstIncomeMonthStr = incomeRepository.getIncomeStartMonth(user);
				List<String> monthYearList = expenseRepository.getMonthYearList(user);
				List<String> filteredMonths = DateHelper.filterMonths(monthYearList, firstIncomeMonthStr);

				if (selectedMonth == null || selectedMonth.isEmpty() || !filteredMonths.contains(selectedMonth)) {
					if (!filteredMonths.isEmpty()) {
						selectedMonth = filteredMonths.get(0);
					} else {
						selectedMonth = DateHelper.getCurrentMonth();
						System.out.println("Hey reaching here" + selectedMonth);

						System.out.println(filteredMonths + "skdjsdj");
						filteredMonths.add(selectedMonth);
					}
				}
				handleSavingsMeterSection(user, model, selectedMonth);
				model.addAttribute("monthYearList", filteredMonths);
				model.addAttribute("currentMonth", DateHelper.getCurrentMonth());
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", new Message("Something went wrong", "alert-danger"));
		}
		return "user/savings-history";
	}

	@GetMapping("/view-analysis")
	public String viewAnalysisGet(@ModelAttribute("user") User user, Model model, RedirectAttributes attributes,
			@RequestParam(value = "monthPie", required = false) String selectedMonthPie,
			@RequestParam(value = "monthER", required = false) String selectedMonthER,
			@RequestParam(value = "yearBar", required = false) String selectedYearBar,
			@RequestParam(value = "yearLine", required = false) String selectedYearLine,
			@RequestParam(value = "tab", required = false) Integer tab) {
		try {
			if (user.getIncomeSources().isEmpty()) {
				attributes.addFlashAttribute("message",
						new Message("First lets add an Income Source", "alert-warning"));
				return "redirect:income-source/add-income-source";
			} else if (user.getExpenses().isEmpty()) {
				attributes.addFlashAttribute("message", new Message("First lets add an Expense", "alert-warning"));
				return "redirect:expense/add-expense";
			} else if (user.getSavingsGoal() == null) {
				attributes.addFlashAttribute("message",
						new Message("First set a Savings Goal For Yourself", "alert-warning"));
				return "redirect:savings-goal/set-savings-goal";
			} else {
				setTab(model, tab);
				handlePieChartSection(user, model, selectedMonthPie);
				handleBarChartSection(user, model, selectedYearBar);
				handleLineChartSection(user, model, selectedYearLine);
				handleExpenseReportSection(user, model, selectedMonthER);
				handleSavingsMeterSection(user, model, DateHelper.getCurrentMonth());
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", new Message("Something went wrong", "alert-danger"));
		}
		return "user/view-analysis";
	}

	private void setTab(Model model, Integer tab) {
		// Implementation for setting tab number
		if (tab == null || tab < 0 || tab > 3) {
			tab = 0;
		}
		model.addAttribute("tab", tab);
	}

	private void handlePieChartSection(User user, Model model, String selectedMonthPie) {
		// Implementation for pie chart section
		// list to store the amounts by category of a user
		List<Object[]> list;
		Double totalExpenseAmount;
		// list containing all available months for the user in the database
		List<String> monthYearList = expenseRepository.getMonthYearList(user);
		// if month is null we show latest
		if (selectedMonthPie == null
				|| (!monthYearList.contains(selectedMonthPie) && !selectedMonthPie.equals("All"))) {
			selectedMonthPie = monthYearList.get(0);
		}

		// if empty or all we give the pie chart of all months
		if (selectedMonthPie.isEmpty() || selectedMonthPie.equals("All")) {
			list = expenseRepository.getExpenseAmountsByCategory(user);
			totalExpenseAmount = expenseRepository.getTotalExpenseAmount(user);
			System.out.println("Total Expense Amount");

		}
		// if not empty and has a month we give piechart of the particular month
		else {

			list = expenseRepository.getExpenseAmountsByCategoryByMonth(user, selectedMonthPie);
			// getting total expense amount of user
			totalExpenseAmount = expenseRepository.getTotalExpenseAmountByMonth(user, selectedMonthPie);
		}

		// converting list to map
		Map<String, Double> exAmMap = ConvertListToMap.convertToStrDouble(list);
		System.out.println("Exammap: " + exAmMap);
		// converting map to map for chart
		Map<String, Double> exPieMap = GetMapsForCharts.getMapForPieChart(exAmMap, totalExpenseAmount);

		// sending amount category map to display amounts on pie chart
		model.addAttribute("exAmMap", exAmMap);
		model.addAttribute("exPieMap", exPieMap);
		model.addAttribute("monthYearList", monthYearList);
		model.addAttribute("month", selectedMonthPie);
	}

	private void handleBarChartSection(User user, Model model, String selectedYearBar) {
		// Implementation for bar chart section

		// yearList with all available years from the database of the user
		List<String> yearList = expenseRepository.getYearList(user);
		// list to get all expenses according to months
		List<Object[]> list2;
		if (selectedYearBar == null || (!yearList.contains(selectedYearBar) && !selectedYearBar.equals("All"))) {
			selectedYearBar = yearList.get(0);
		}
		if (selectedYearBar.isEmpty() || selectedYearBar.equals("All")) {
			list2 = expenseRepository.getExpenseAmountsForAllYears(user.getId());
			Map<Integer, Double> expAmtByYear = ConvertListToMap.convertToIntDouble(list2);
			model.addAttribute("expAmtByYear", expAmtByYear);
		} else {
			list2 = expenseRepository.getExpenseAmountsByMonthByYear(user.getId(), selectedYearBar);
			Map<Integer, Double> expAmtByMonth = ConvertListToMap.convertToIntDouble(list2);
			model.addAttribute("expAmtByMonth", expAmtByMonth);
		}

		// converting list to a map
		model.addAttribute("yearList", yearList);
		model.addAttribute("yearBar", selectedYearBar);

	}

	private void handleLineChartSection(User user, Model model, String selectedYearLine) {
		// Implementation for line chart section

		String firstIncomeMonthStr = incomeRepository.getIncomeStartMonth(user);
		List<String> yearList = expenseRepository.getYearList(user);
		// Use Java 8 Stream to filter months
		String firstIncomeYearStr = firstIncomeMonthStr.substring(0, 4);
		List<String> filteredYears = DateHelper.filterYears(yearList, firstIncomeYearStr);

		// Use Java 8 Stream to filter months
		List<String> monthYearList = expenseRepository.getMonthYearList(user);
		List<String> filteredMonths = DateHelper.filterMonths(monthYearList, firstIncomeMonthStr);

		if (selectedYearLine == null || selectedYearLine.isEmpty() || !filteredYears.contains(selectedYearLine)) {
			selectedYearLine = filteredYears.get(0);
		}

		if (!filteredMonths.contains(DateHelper.getCurrentMonth())) {
			filteredMonths.add(0, DateHelper.getCurrentMonth());
		}

		Map<String, Double> incomeByMonth = new HashMap<>();
		for (String month : filteredMonths) {
			if (month.startsWith(selectedYearLine)) {
				if (month.charAt(5) != '0') {
					incomeByMonth.put(month.substring(5),
							incomeChangeRepository.getTotalEarnings(user.getId(), month + "-01"));
				} else {
					incomeByMonth.put(month.substring(6),
							incomeChangeRepository.getTotalEarnings(user.getId(), month + "-01"));
				}
			}
		}
		List<Object[]> list2 = expenseRepository.getExpenseAmountsByMonthByYear(user.getId(), selectedYearLine);
		Map<Integer, Double> expAmtByMonth = ConvertListToMap.convertToIntDouble(list2);
		Map<String, Double> expByMonthLine = GetMapsForCharts.getExpAmtByMonth(incomeByMonth, expAmtByMonth);

		System.out.println("Line expenses: " + expByMonthLine);
		System.out.println("Line expenses: " + incomeByMonth);

		System.out.println("incomeBymonths for line: " + incomeByMonth);
		model.addAttribute("incomeByMonth", incomeByMonth);
		model.addAttribute("expByMonthLine", expByMonthLine);
		model.addAttribute("yearLine", selectedYearLine);
		model.addAttribute("yearListEI", filteredYears);
		model.addAttribute("firstIncomeMonth", firstIncomeMonthStr);

	}

	private void handleExpenseReportSection(User user, Model model, String selectedMonthER) {
		// Implementation for expense report section
		String firstIncomeMonthStr = incomeRepository.getIncomeStartMonth(user);
		List<String> monthYearList = expenseRepository.getMonthYearList(user);
		List<String> filteredMonths = DateHelper.filterMonths(monthYearList, firstIncomeMonthStr);

		if (selectedMonthER == null || selectedMonthER.isEmpty() || (!filteredMonths.contains(selectedMonthER)
				&& !selectedMonthER.equals(DateHelper.getCurrentMonth()))) {

			selectedMonthER = DateHelper.getCurrentMonth();
			System.out.println("Hey reaching here" + selectedMonthER);

			System.out.println(filteredMonths + "skdjsdj");
			filteredMonths.add(0, selectedMonthER);

		}

		System.out.println("Filtered Months after add: " + filteredMonths);
		System.out.println("months: " + filteredMonths);

		Double totalExpenseAmtER = expenseRepository.getTotalExpenseAmountByMonth(user, selectedMonthER);
		if (totalExpenseAmtER == null) {
			totalExpenseAmtER = 0.0;
		}
		Double totalEarningsER = incomeChangeRepository.getTotalEarnings(user.getId(), selectedMonthER + "-01");
		if (totalEarningsER == null) {
			totalEarningsER = 0.0;
		}
		System.out.println(selectedMonthER + " jd");
		// getting map of highest expense category and lowest expense category for the
		// current month
		Map<String, Double> highestCategory = ConvertListToMap
				.convertToStrDouble(expenseRepository.getHighestExpenseCategory(user, selectedMonthER));
		Map<String, Double> lowestCategory = ConvertListToMap
				.convertToStrDouble(expenseRepository.getLowestExpenseCategory(user, selectedMonthER));
		// getting highest and lowest expense for the current month
		Expense highestExp = expenseRepository.getHighestExpense(user, selectedMonthER);
		Expense lowestExp = expenseRepository.getLowestExpense(user, selectedMonthER);

		model.addAttribute("filteredMonths", filteredMonths);
		model.addAttribute("totalEarningsER", totalEarningsER);
		model.addAttribute("totalExpenseAmtER", totalExpenseAmtER);
		model.addAttribute("highestCategory", highestCategory);
		model.addAttribute("lowestCategory", lowestCategory);
		model.addAttribute("highestExpense", highestExp);
		model.addAttribute("lowestExpense", lowestExp);
		model.addAttribute("monthER", selectedMonthER);
	}

	private void handleSavingsMeterSection(User user, Model model, String selectedMonth) {
		// Implementation for savings meter section
		SavingsHistory savingsHistory = savingsHistoryRepository.getSavingsForMonth(user.getId(),
				selectedMonth + "-01");
		Double savingsAmount = savingsHistory.getAmount();
		// getting total expense amount and total income amount for the current month
		Double totalExpenseAmtCM = expenseRepository.getTotalExpenseAmountByMonth(user, selectedMonth);
		if (totalExpenseAmtCM == null) {
			totalExpenseAmtCM = 0.0;
		}
		Double totalEarningsCM = incomeChangeRepository.getTotalEarnings(user.getId(), selectedMonth + "-01");
		if (totalEarningsCM == null) {
			totalEarningsCM = 0.0;
		}
		Double saveable = totalEarningsCM - totalExpenseAmtCM;
		Integer saveablePercent;
		if (saveable > savingsAmount) {
			saveablePercent = 100;
			saveable = savingsAmount;
		} else {
			saveablePercent = (int) ((saveable / savingsAmount) * 100);
		}
		model.addAttribute("saveableAmt", saveable);
		System.out.println(saveablePercent);
		model.addAttribute("monthSM", selectedMonth);
		model.addAttribute("savingsAmount", savingsAmount);
		model.addAttribute("goal", savingsHistory.getGoal());
		model.addAttribute("saveablePercent", saveablePercent);
		model.addAttribute("totalEarningsCM", totalEarningsCM);
		model.addAttribute("totalExpenseAmtCM", totalExpenseAmtCM);
	}

}
