package com.springboot.expensetracker.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.springboot.expensetracker.entity.Expense;
import com.springboot.expensetracker.entity.IncomeSource;
import com.springboot.expensetracker.entity.User;

@Repository
public interface IncomeSourceRepository extends JpaRepository<IncomeSource, Integer> {
	@Query("select i from IncomeSource i where i.user=:u order by i.earnings desc")
	public Page<IncomeSource> getIncomeSourcesByUser(User u, Pageable pageable);

	@Query(value = "SELECT SUM(income_source.earnings) FROM income_source WHERE income_source.user_id =:user_id AND YEAR(income_source.created_at) <= YEAR(DATE(:month)) AND MONTH(income_source.created_at) <= MONTH(DATE(:month))", nativeQuery = true)
	public Double getTotalEarnings(int user_id, String month);

	// to get month and year for pie chart
	@Query("select DATE_FORMAT(i.createdAt, '%Y-%m') from IncomeSource i where i.user=:u order by i.createdAt asc limit 1")
	public String getIncomeStartMonth(User u);

	// to group total expense amount by months in a particular year
//	@Query(value = "SELECT MONTH(income_source.createdAt) AS month, SUM(income_source.earnings) FROM income_source WHERE YEAR(income_source.date) =:year AND income_source.user_id=:user GROUP BY MONTH(income_expense.createdAt)", nativeQuery = true)
//	public List<Object[]> getIncomeSourceAmountsByMonthByYear(User u, String year);
}
