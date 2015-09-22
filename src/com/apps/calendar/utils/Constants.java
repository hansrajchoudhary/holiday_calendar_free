package com.apps.calendar;

import java.util.Arrays;
import java.util.List;


public class Constants {
	private static final String January = "Jan";
	private static final String February = "Feb";
	private static final String March = "Mar";
	private static final String April = "Apr";
	private static final String May = "May";
	private static final String June = "Jun";
	private static final String July = "Jul";
	private static final String August = "Aug";
	private static final String September = "Sep";
	private static final String October = "Oct";
	private static final String November = "Nov";
	private static final String December = "Dec";

	static final List<String> DAYS = Arrays.asList("sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday");
	
	public static String getMonthInt(String month) {
		if (month.equals("Jan"))
			return "01";
		if (month.equals("Feb"))
			return "02";
		if (month.equals("Mar"))
			return "03";
		if (month.equals("Apr"))
			return "04";
		if (month.equals("May"))
			return "05";
		if (month.equals("Jun"))
			return "06";
		if (month.equals("Jul"))
			return "07";
		if (month.equals("Aug"))
			return "08";
		if (month.equals("Sep"))
			return "09";
		if (month.equals("Oct"))
			return "10";
		if (month.equals("Nov"))
			return "11";
		return "12";
	}
	
	public static String getMonthName(int month) {
		if (month == 1)
			return January;
		if (month == 2)
			return February;

		if (month == 3)
			return March;

		if (month == 4)
			return April;

		if (month == 5)
			return May;

		if (month == 6)
			return June;

		if (month == 7)
			return July;

		if (month == 8)
			return August;

		if (month == 9)
			return September;

		if (month == 10)
			return October;

		if (month == 11)
			return November;

		if (month == 12)
			return December;

		return January;
	}
}
