package com.apps.calendar;

public class DayEntry{
	private String dayStr;
	private String nameStr;

	public DayEntry() {
	}
	public DayEntry(String dayStr) {
		this.dayStr = dayStr;
	}
	public DayEntry(String dayStr, String nameStr) {
		this.dayStr = dayStr;
		this.nameStr = nameStr;
	}
	public String getDayStr() {
		return dayStr;
	}

	public void setDayStr(String dayStr) {
		this.dayStr = dayStr;
	}

	public String getNameStr() {
		return nameStr;
	}

	public void setNameStr(String nameStr) {
		this.nameStr = nameStr;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(((DayEntry) o).nameStr != null && this.nameStr != null)
			return this.dayStr.equals(((DayEntry) o).dayStr) && this.nameStr.equals(((DayEntry) o).nameStr);
		
		return this.dayStr.equals(((DayEntry) o).dayStr);
	}
}