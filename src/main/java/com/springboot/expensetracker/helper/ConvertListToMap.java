package com.springboot.expensetracker.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvertListToMap {
	public static Map<String, Double> convertToStrDouble(List<Object[]> list) {
		Map<String, Double> map = new HashMap<>();
		for (Object[] objects : list) {
			String key = objects[0].toString();
			Double value = Double.parseDouble(String.valueOf(objects[1]));
			map.put(key, value);
		}
		return map;
	}
	
	public static Map<Integer, Double> convertToIntDouble(List<Object[]> list){
		Map<Integer, Double> map = new HashMap<>();
		for (Object[] objects : list) {
			Integer key = Integer.parseInt(String.valueOf(objects[0]));
			Double value = Double.parseDouble(String.valueOf(objects[1]));
			map.put(key, value);
		}
		return map;
	}
}
