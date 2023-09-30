package com.springboot.expensetracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.springboot.expensetracker.entity.SavingsHistory;

public interface SavingsHistoryRepository extends JpaRepository<SavingsHistory, Integer> {
	@Query(value = "SELECT sh.* " +
            "FROM savings_history sh " +
            "INNER JOIN savings_goal s ON sh.savings_goal_s_id = s.s_id " +
            "WHERE s.user_id = :userId " +
            "AND sh.sh_id = ( " +
            "    SELECT MAX(sh2.sh_id) " +
            "    FROM savings_history sh2 " +
            "    WHERE sh2.savings_goal_s_id = s.s_id " +
            "    AND YEAR(sh2.date) <= YEAR(DATE(:month)) " +
            "    AND MONTH(sh2.date) <= MONTH(DATE(:month)) " +
            ")",
    nativeQuery = true)
	public SavingsHistory getSavingsForMonth(Integer userId, String month);
}
