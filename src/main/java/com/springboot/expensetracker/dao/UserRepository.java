package com.springboot.expensetracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.springboot.expensetracker.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	
	@Query("select u from User u where u.email =:email")
	public User getUserByUsername(@Param("email") String email);
	
	@Query("select count(u) from User u where u.email =:email")
	public Integer checkIfEmailExists(String email);
	
	@Query("select count(u) from User u where u.phone =:phone")
	public Integer checkIfPhoneExists(String phone);
}
