package com.springboot.expensetracker.helper;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class GetMapsForCharts {
	public static Map<String, Double> getMapForPieChart(Map<String, Double> map, double totalItems) {
		Map<String, Double> outputMap = new HashMap<>();

		// looping over the map
		for (Map.Entry<String, Double> entry : map.entrySet()) {
			// getting the name of the expense category
			String key = entry.getKey();

			// getting the count of expense category
			Double value = entry.getValue();
			// applying percentage formula
			value = (value / totalItems) * 100;
			// rounding the output percentage to 2 decimals
			value = Math.round(value * 100.0) / 100.0;

			// adding the final key and value to the map ie. String(name of category),
			// Double(percentage)
			outputMap.put(key, value);
		}
		return outputMap;
	}

	public static Map<String, Double> getExpAmtByMonth(Map<String, Double> incomeByMonth,
			Map<Integer, Double> expAmtByMonth) {
		Map<String, Double> expByMonthLine = new HashMap<>();
		for (Map.Entry<Integer, Double> entry : expAmtByMonth.entrySet()) {
			if (incomeByMonth.containsKey(entry.getKey().toString())) {
				expByMonthLine.put(entry.getKey().toString(), entry.getValue());
			}
		}

		for (Map.Entry<String, Double> entry : incomeByMonth.entrySet()) {
			if (!expByMonthLine.containsKey(entry.getKey())) {
				expByMonthLine.put(entry.getKey(), 0.0);
			}
		}
		return expByMonthLine;
	}

}
