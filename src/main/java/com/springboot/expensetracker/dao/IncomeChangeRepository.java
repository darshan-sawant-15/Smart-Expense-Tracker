package com.springboot.expensetracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.springboot.expensetracker.entity.IncomeChange;

public interface IncomeChangeRepository extends JpaRepository<IncomeChange, Integer> {

	@Query(value = "SELECT SUM(ic.earnings) as total_earnings FROM income_change ic INNER JOIN income_source isrc ON ic.income_source_is_id = isrc.is_id WHERE isrc.user_id=:userId AND ic.ic_id = (SELECT MAX(ic2.ic_id) FROM income_change ic2 WHERE ic2.income_source_is_id = ic.income_source_is_id AND YEAR(ic2.created_at) <= YEAR(DATE(:month)) AND MONTH(ic2.created_at) <= MONTH(DATE(:month)))", nativeQuery = true)
	public Double getTotalEarnings(int userId, String month);
	
//	@Query(value = "SELECT  as total_earnings FROM income_change ic INNER JOIN income_source isrc ON ic.income_source_is_id = isrc.is_id WHERE isrc.user_id=:userId AND ic.ic_id = (SELECT MAX(ic2.ic_id) FROM income_change ic2 WHERE ic2.income_source_is_id = ic.income_source_is_id AND YEAR(ic2.created_at) <= YEAR(DATE(:month)) AND MONTH(ic2.created_at) <= MONTH(DATE(:month)))", nativeQuery = true)
//	public Double getTotalEarnings(int userId, String year);
}
