package com.springboot.expensetracker.helper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.servlet.tags.form.InputTag;

public class DateHelper {
	public static String getCurrentMonth() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
		LocalDate date = LocalDate.now();
		return date.format(formatter);
	}
	
	public static String getCurrentDate() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date = LocalDate.now();
		return date.format(formatter);
	}

	public static List<String> filterMonths(List<String> months, String firstIncomeMonthStr) {
		System.out.println("Before filetering: "+months);
		LocalDate firstIncomeMonth = LocalDate.parse(firstIncomeMonthStr + "-01");
		List<String> filteredMonths = months.stream().filter(monthStr -> {
			LocalDate month = LocalDate.parse(monthStr + "-01");
			return month.isAfter(firstIncomeMonth) || month.isEqual(firstIncomeMonth);
		}).collect(Collectors.toList());
		System.out.println("After filetering: "+filteredMonths);
		return filteredMonths;
	}

	public static List<String> filterYears(List<String> years, String firstIncomeYearStr) {
		LocalDate firstIncomeYear = LocalDate.parse(firstIncomeYearStr + "-01-01");
		List<String> filteredYears = years.stream().filter(yearStr -> {
			LocalDate year = LocalDate.parse(yearStr + "-01-01");
			return year.isAfter(firstIncomeYear) || year.isEqual(firstIncomeYear);
		}).collect(Collectors.toList());
		return filteredYears;
	}

	public static boolean isAfterToday(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate today = LocalDate.now();
		LocalDate inputDate = LocalDate.parse(date);
		return (inputDate.isAfter(today)) ? true : false;
	}
}
