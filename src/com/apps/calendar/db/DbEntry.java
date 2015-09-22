package com.apps.calendar;

public class DbEntry
{
	// private variables
	String _country;

	String _day;
	
	String _code;

	String _holiday;

	DbEntry(){
		
	}
	DbEntry(String country, String day, String code, String holiday){
		this._code = code;
		this._country = country;
		this._day = day;
		this._holiday = holiday;
	}
	
	public String getCountry()
	{
		return _country;
	}

	public void setCountry(String _country)
	{
		this._country = _country;
	}

	public String getCode()
	{
		return _code;
	}

	public void setCode(String _code)
	{
		this._code = _code;
	}
	
	public String getDay()
	{
		return _day;
	}

	public void setDay(String _day)
	{
		this._day = _day;
	}

	public String getHoliday()
	{
		return _holiday;
	}

	public void setHoliday(String _holiday)
	{
		this._holiday = _holiday;
	}

	
}
