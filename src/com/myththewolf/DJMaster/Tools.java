package com.myththewolf.DJMaster;

import org.json.JSONArray;

public class Tools {
	private static boolean arrayFound = false;
	public static boolean JSONArray_Contains(JSONArray haystack, String needle) {
		arrayFound = false;
		haystack.forEach(val -> {
			if(val.equals(needle)) {
				arrayFound = true;
			}
		});
		return arrayFound;
	}
}
