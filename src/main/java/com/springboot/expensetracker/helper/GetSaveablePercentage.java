package com.springboot.expensetracker.helper;

import org.springframework.beans.factory.annotation.Autowired;

import com.springboot.expensetracker.dao.ExpenseRepository;
import com.springboot.expensetracker.dao.IncomeChangeRepository;
import com.springboot.expensetracker.dao.IncomeSourceRepository;
import com.springboot.expensetracker.dao.SavingsHistoryRepository;
import com.springboot.expensetracker.entity.SavingsHistory;
import com.springboot.expensetracker.entity.User;

public class GetSaveablePercentage {
	public double getSaveablePercentageForCurrentMonth(User user, ExpenseRepository expenseRepository,
			IncomeChangeRepository incomeChangeRepository, SavingsHistoryRepository savingsHistoryRepository) {
		String selectedMonth = DateHelper.getCurrentDate();
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
		return saveablePercent;
	}
}
