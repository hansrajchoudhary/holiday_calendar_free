package com.apps.calendar;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CalendarEntry extends BaseAdapter
{
	private Context mContext;

	private java.util.Calendar month;

	public GregorianCalendar pmonth; // calendar instance for previous month

	/**
	 * calendar instance for previous month for getting complete view
	 */
	public GregorianCalendar pmonthmaxset;

	private GregorianCalendar selectedDate;

	int firstDay;

	int maxWeeknumber;

	int maxP;

	int calMaxP;

	int lastWeekDay;

	int leftDays;

	int mnthlength;

	String itemvalue, curentDateString;

	DateFormat df;

	private ArrayList<String> items;

	public static List<String> dayString;

	private View previousView;

	ArrayList<String> holidayOfTheMonth;

	ArrayList<String> holidayNumbers;

	int width;

	int height;

	public CalendarEntry(Context c, GregorianCalendar monthCalendar, ArrayList<String> holidayOfTheMonth, int width, int height)
	{
		CalendarAdapter.dayString = new ArrayList<String>();
		month = monthCalendar;
		selectedDate = (GregorianCalendar) monthCalendar.clone();
		mContext = c;
		month.set(GregorianCalendar.DAY_OF_MONTH, 1);
		this.items = new ArrayList<String>();
		df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		curentDateString = df.format(selectedDate.getTime());
		this.holidayOfTheMonth = holidayOfTheMonth;
		this.width = width;
		this.height = height;
		refreshDays();
	}

	public void setItems(ArrayList<String> items)
	{
		for (int i = 0; i != items.size(); i++)
		{
			if (items.get(i).length() == 1)
			{
				items.set(i, "0" + items.get(i));
			}
		}
		this.items = items;
	}

	public void setHolidayOfTheMonth(ArrayList<String> holidayOfTheMonth)
	{
		this.holidayOfTheMonth = holidayOfTheMonth;
	}

	public int getCount()
	{
		return dayString.size();
	}

	public Object getItem(int position)
	{
		return dayString.get(position);
	}

	public long getItemId(int position)
	{
		return 0;
	}

	// create a new view for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = convertView;

		TextView dayView;
		TextView holiView;
		if (convertView == null)
		{ // if it's not recycled, initialize some
			// attributes
			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.calendar_item, null);

		}
		LinearLayout itemx = (LinearLayout) v.findViewById(R.id.itemx);
		int w = width / 7;
		int h = height / 14;
		if (height > 650)
		{
			w = width / 7;
			h = height / 17;
		}
		LayoutParams params = new AbsListView.LayoutParams(w, h);
		itemx.setLayoutParams(params);

		dayView = (TextView) v.findViewById(R.id.date);
		holiView = (TextView) v.findViewById(R.id.holi);
		holiView.setText("");
		// separates daystring into parts.
		String[] separatedTime = dayString.get(position).split("-");
		// taking last part of date. ie; 2 from 2012-12-02
		String gridvalue = separatedTime[2].replaceFirst("^0*", "");
		// checking whether the day is in current month or not.
		boolean showHoliday = false;
		boolean isGray = false;
		if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay))
		{
			// setting offdays to white color.
			dayView.setTextColor(Color.GRAY);
			dayView.setClickable(false);
			dayView.setFocusable(false);
			isGray = true;
		}
		else if ((Integer.parseInt(gridvalue) < 7) && (position > 28))
		{
			dayView.setTextColor(Color.GRAY);
			dayView.setClickable(false);
			dayView.setFocusable(false);
			isGray = true;
		}
		else if (position % 7 == 0 || holidayNumbers.contains(gridvalue))
		{
			// setting offdays to white color.
			dayView.setTextColor(Color.RED);
			showHoliday = true;
		}
		else if (position % 7 == 6)
		{
			// setting offdays to white color.
			dayView.setTextColor(Color.MAGENTA);
		}
		else
		{
			// setting curent month's days in blue color.
			dayView.setTextColor(Color.BLUE);
		}

		if (dayString.get(position).equals(curentDateString))
		{
			setSelected(v);
			previousView = v;
		}
		else
		{
			v.setBackgroundResource(R.drawable.list_item_background);
		}

		dayView.setText(gridvalue);

		/***** Handle small screen device ******/
		int maxLen = 6;
		dayView.setTextSize(13);
		holiView.setTextSize(10);

		/** hack **/
		if (mContext.getResources().getBoolean(R.bool.isTablet))
		{
			dayView.setTextSize(14);
			holiView.setTextSize(14);
		}

		/**************/
		String fName = separatedTime[0] + "_" + separatedTime[1] + "_" + separatedTime[2];
		int x = holidayNumbers.indexOf(gridvalue);
		if (holidayNumbers.contains(gridvalue) && showHoliday)
		{
			String holi = holidayOfTheMonth.get(x).trim();
			String showStr = holi.split(":")[1].trim();
			if (showStr.length() > maxLen)
				showStr = showStr.substring(0, maxLen);

			holiView.setText(showStr.replace("^!", ""));
		}

		// create date string for comparison
		String date = dayString.get(position);
		String file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HolidaCal/" + fName + ".3gp";
		File f = new File(file);
		if (f.exists())
		{
			if (!isGray)
			{
				dayView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.recording, 0);
				dayView.setCompoundDrawablePadding(12);
			}
		}
		else
		{
			if (holidayNumbers.contains(gridvalue) && (holidayOfTheMonth.get(x) != null) && holidayOfTheMonth.get(x).contains("^!"))
			{
				if (!isGray)
				{
					dayView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sstt, 0);
					dayView.setCompoundDrawablePadding(12);
				}
			}
			else
				dayView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}

		if (date.length() == 1)
		{
			date = "0" + date;
		}
		String monthStr = "" + (month.get(GregorianCalendar.MONTH) + 1);
		if (monthStr.length() == 1)
		{
			monthStr = "0" + monthStr;
		}

		return v;
	}

	public View setSelected(View view)
	{
		if (previousView != null)
		{
			previousView.setBackgroundResource(R.drawable.list_item_background);
		}
		previousView = view;
		view.setBackgroundResource(R.drawable.calendar_cel_selectl);
		return view;
	}

	private void setHolidayNumbers()
	{
		holidayNumbers = new ArrayList<String>();
		for (String holiday : holidayOfTheMonth)
		{
			String[] tokens = holiday.split(" ");
			holidayNumbers.add(tokens[1].replaceFirst("^0*", ""));
		}
	}

	public void refreshDays()
	{
		// clear items
		items.clear();
		dayString.clear();
		// Locale.setDefault(Locale.US);
		pmonth = (GregorianCalendar) month.clone();
		// month start day. ie; sun, mon, etc
		firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
		int str = month.get(GregorianCalendar.MONTH);
		int yr = month.get(GregorianCalendar.YEAR);
		// finding number of weeks in current month.
		if (firstDay == 1)
			maxWeeknumber = 5;
		else if (yr == 2016 && (str == 7 || str == 10))
			maxWeeknumber = 6;
		else
			maxWeeknumber = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
		// allocating maximum row number for the gridview.
		mnthlength = maxWeeknumber * 7;
		maxP = getMaxP(); // previous month maximum day 31,30....
		calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...
		/**
		 * Calendar instance for getting a complete gridview including the three month's (previous,current,next) dates.
		 */
		pmonthmaxset = (GregorianCalendar) pmonth.clone();
		/**
		 * setting the start date as previous month's required date.
		 */
		pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);

		/**
		 * filling calendar gridview.
		 */
		for (int n = 0; n < mnthlength; n++)
		{
			itemvalue = df.format(pmonthmaxset.getTime());
			pmonthmaxset.add(GregorianCalendar.DATE, 1);
			dayString.add(itemvalue);
		}
		setHolidayNumbers();
	}

	private int getMaxP()
	{
		int maxP;
		if (month.get(GregorianCalendar.MONTH) == month.getActualMinimum(GregorianCalendar.MONTH))
		{
			pmonth.set((month.get(GregorianCalendar.YEAR) - 1), month.getActualMaximum(GregorianCalendar.MONTH), 1);
		}
		else
		{
			pmonth.set(GregorianCalendar.MONTH, month.get(GregorianCalendar.MONTH) - 1);
		}
		maxP = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

		return maxP;
	}

}
