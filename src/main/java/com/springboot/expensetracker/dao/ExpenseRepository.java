package com.springboot.expensetracker.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.springboot.expensetracker.entity.Expense;
import com.springboot.expensetracker.entity.User;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
	@Query("select e from Expense e where e.user=:u order by e.date desc")
	public Page<Expense> getExpensesByUser(User u, Pageable pageable);

	@Query("select e from Expense e where e.user=:u and e.date like CONCAT(:month, '%')")
	public Page<Expense> getExpensesByUserAndMonth(User u, String month, Pageable pageable);

	@Query("select e from Expense e where e.user=:u and e.description like %:desc% and e.date like CONCAT(:month, '%')")
	public List<Expense> searchExpensesByDescAndMonth(String desc, User u, String month);
	
	@Query("select e from Expense e where e.user=:u and e.description like %:desc%")
	public List<Expense> searchExpensesByDesc(String desc, User u);

	@Query("select e.type, sum(e.amount) from Expense e where e.user=:u group by e.type")
	public List<Object[]> getExpenseAmountsByCategory(User u);

	@Query("select e.type, sum(e.amount) from Expense e where e.user=:u  and e.date like CONCAT(:month, '%') group by e.type")
	public List<Object[]> getExpenseAmountsByCategoryByMonth(User u, String month);

	@Query("select sum(e.amount) from Expense e where e.user=:u")
	public Double getTotalExpenseAmount(User u);

	@Query("select sum(e.amount) from Expense e where e.user=:u and e.date like CONCAT(:month, '%')")
	public Double getTotalExpenseAmountByMonth(User u, String month);

	// to get month and year for pie chart
	@Query("select DISTINCT DATE_FORMAT(e.date, '%Y-%m') as formatted_date from Expense e where e.user=:u order by formatted_date desc")
	public List<String> getMonthYearList(User u);

	// to get year list for bar chart
	@Query("select DISTINCT DATE_FORMAT(e.date, '%Y') as formatted_date from Expense e where e.user=:u order by formatted_date desc")
	public List<String> getYearList(User u);

	// to group total expense amount by months in a particular year
	@Query(value = "SELECT MONTH(expense.date) AS month, SUM(expense.amount) FROM expense WHERE YEAR(expense.date) =:year AND expense.user_id =:userId GROUP BY MONTH(expense.date)", nativeQuery = true)
	public List<Object[]> getExpenseAmountsByMonthByYear(int userId, String year);

	@Query(value = "SELECT YEAR(expense.date) AS month, SUM(expense.amount) FROM expense GROUP BY YEAR(expense.date)", nativeQuery = true)
	public List<Object[]> getExpenseAmountsForAllYears(int userId);

	@Query("select e.type, sum(e.amount) as totalAmt from Expense e where e.user=:u  and e.date like CONCAT(:month, '%') group by e.type order by totalAmt desc limit 1")
	public List<Object[]> getHighestExpenseCategory(User u, String month);

	@Query("select e.type, sum(e.amount) as totalAmt from Expense e where e.user=:u  and e.date like CONCAT(:month, '%') group by e.type order by totalAmt asc limit 1")
	public List<Object[]> getLowestExpenseCategory(User u, String month);

	@Query("SELECT e FROM Expense e WHERE e.user = :u AND e.date LIKE CONCAT(:month, '%') ORDER BY e.amount DESC limit 1")
	public Expense getHighestExpense(User u, String month);

	@Query("SELECT e FROM Expense e WHERE e.user = :u AND e.date LIKE CONCAT(:month, '%') ORDER BY e.amount ASC limit 1")
	public Expense getLowestExpense(User u, String month);

}
