package com.apps.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Favorite extends Activity
{
	private HorizontalPager mPager;
	private RadioGroup mRadioGroup;
	private RadioButton rb0;
	private RadioButton rb1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorite);
		
		mPager = (HorizontalPager) findViewById(R.id.horizontal_pager);
		mRadioGroup = (RadioGroup) findViewById(R.id.tabs);
		mPager.setOnScreenSwitchListener(onScreenSwitchListener);
		mRadioGroup.setOnCheckedChangeListener(onCheckedChangedListener);
		rb0 = (RadioButton) findViewById(R.id.radio_btn_0);
		rb1 = (RadioButton) findViewById(R.id.radio_btn_1);
		rb0.setText(Html.fromHtml("<b><u><font color='#00FF00'> Favorites </font></u><b>"));
		rb1.setText(Html.fromHtml("<b><u><font color='#FFFFFF'> Holidays </font></u><b>"));

		TableLayout tv = (TableLayout) findViewById(R.id.fav_content);
		Bundle b = getIntent().getExtras();
		
		String value = b.getString("key");
		String[] entries = value.split("###");
		String mText = "";

		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		Map<Date, String> map = new TreeMap<Date, String>();
		for (String entry : entries)
		{			
			String[] s1 = entry.split(":::");
			if (s1.length == 2)
			{
				try
				{
					Date x = formatter.parse(s1[0]);
					map.put(x, s1[1]);
				}
				catch (ParseException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// mText = mText + "<b><font color='white'>" + (s1[0])
				// + ": </font></b> &nbsp;" + s1[1].replace("^!", "")+"<br />";
			}
		}
		int xx = 0;
		for (Entry<Date, String> e : map.entrySet())
		{
			TableRow row1 = new TableRow(this);
			TableRow row2 = new TableRow(this);

			TextView t1 = new TextView(this);
			TextView t2 = new TextView(this);
			
			String val = e.getValue();
		    Calendar cal = Calendar.getInstance();
		    cal.setTime(e.getKey());
		    int year = cal.get(Calendar.YEAR);
		    String month = Constants.getMonthName(cal.get(Calendar.MONTH) + 1);
		    int day = cal.get(Calendar.DAY_OF_MONTH);
			t1.setText(Html.fromHtml("<font color='#00FF00'>" + day+" "+month+" "+year + "</font>"));
			t2.setText(Html.fromHtml("<font color='#FFFFFF'>" + val.replace("^!", "").replace("#", "<br />") + "</font>"));
			t2.setPadding(10, 0, 0, 0);
			row1.addView(t1);
			row2.addView(t2);
			tv.addView(row1, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));	
			tv.addView(row2, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));		
			xx++;
		}
		
		/**
		 * List of holidays
		 */
		StringBuilder allHolidays = new StringBuilder();
		for (Entry<String, String> entry : CalendarView.activity.holidayMap2015.entrySet())
		{
			String[] s = entry.getKey().split(" ");
			allHolidays = allHolidays.append(s[1]).append("-").append(Constants.getMonthInt(s[0])).append("-2015:::").append(entry.getValue()).append("###");
		}
		for (Entry<String, String> entry : CalendarView.activity.holidayMap.entrySet())
		{
			String[] s = entry.getKey().split(" ");
			allHolidays = allHolidays.append(s[1]).append("-").append(Constants.getMonthInt(s[0])).append("-2016:::").append(entry.getValue()).append("###");
		}
		
		String[] listOfHolidays = allHolidays.toString().split("###");
		ListView holidayContent = (ListView) findViewById(R.id.holiday_content);
		List<String> planetList = new ArrayList<String>(); 
	    final SwipeViewDetector swipeDetector = new SwipeViewDetector();
	    holidayContent.setOnTouchListener(swipeDetector);		
	    final ArrayAdapter<String> listAdapter =  new ArrayAdapter<String>(this, R.layout.simplerow, planetList)
	    {
			@Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	            View view = super.getView(position, convertView, parent);
	            TextView subTitleView = (TextView) view.findViewById(R.id.rowTextView);
	            String txt = subTitleView.getText().toString();
	            String day = txt.split("<:>")[0];
	            String val = txt.split("<:>")[1];
			    String str = "<font color='#00FF00'>" + day + "<br/></font><font color='#FFFFFF'>" + val + "</font>";
	            subTitleView.setText(Html.fromHtml(str));

	            return view;
	        }
	    };
		holidayContent.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				if (swipeDetector.swipeDetected()){
					String holi = ((TextView) view).getText().toString();
					deleteHolidayFromList(listAdapter,position,holi);
                }
			}
		});
	    
	    holidayContent.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
			{
				//final Context inst = CalendarView.activity;

				if (swipeDetector.swipeDetected()){
					String holi = ((TextView) view).getText().toString();
					deleteHolidayFromList(listAdapter,position,holi);
                }
				return true;
			}
		});

	    Map<Date, String> holiday_map = new TreeMap<Date, String>();
		for (String entry : listOfHolidays)
		{			
			String[] s1 = entry.split(":::");
			if (s1.length == 2)
			{
				try
				{
					Date x = formatter.parse(s1[0]);
					map.put(x, s1[1]);
				}
				catch (ParseException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		for (Entry<Date, String> e : map.entrySet())
		{
			try
			{
				String val = e.getValue().replace("^!", "");
			    Calendar cal = Calendar.getInstance();
			    cal.setTime(e.getKey());
			    int year = cal.get(Calendar.YEAR);
			    String month = Constants.getMonthName(cal.get(Calendar.MONTH) + 1);
			    int day = cal.get(Calendar.DAY_OF_MONTH);
			    for(String newVal : val.split("#"))
			    {
			    	if(newVal.length() > 0)
			    		listAdapter.add(day+" "+month+" "+year + "<:>" + newVal);
			    }
			}
			catch (Exception e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		holidayContent.setAdapter( listAdapter );
	}
	
	private void deleteHolidayFromList(final ArrayAdapter<String> listAdapter, final int position, final String holi)
	{
		final CalendarView inst = CalendarView.activity;
		String tarikh = holi.split(" ")[0];
		if(tarikh.length() == 1)
			tarikh = "0"+tarikh;
		final String day = holi.split(" ")[1]+" "+tarikh;
		final String holidayValue = holi.split("\\r?\\n")[1];
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				if (which == DialogInterface.BUTTON_POSITIVE)
				{
					String country = inst._country;
					String toRemove = listAdapter.getItem(position);
					listAdapter.remove(toRemove);
					if (inst._year.equals("2015"))
					{
						String temp = inst.holidayMap2015.get(day).replace(holidayValue, "");
						if(temp.contains("^!") || temp.contains("#"))
						{
							if(temp.length() <=3)
							{
								inst.holidayMap2015.remove(day);
								inst.dbHandler.deleteDbEntry(country, day, "holiday2015");
							}
							else
							{
								temp = temp.replace("# ^!", "").replace("#^!", "").replace("##", "#").replace("# #","#").replace("^!^!","^!");
								if(temp.startsWith("#"))
									temp = temp.replaceFirst("#", "");
								if(temp.endsWith("#"))
									temp = temp.substring(0, temp.length()-1);
								inst.holidayMap2015.put(day, temp);
								int val = inst.dbHandler.deleteDbEntry(country, day, "holiday2015", holidayValue);
								if(val == 0)
									inst.dbHandler.deleteDbEntry(country, day, "holiday2015", holidayValue+"^!");
							}
						}
						else
						{
							inst.holidayMap2015.remove(day);
							inst.dbHandler.deleteDbEntry(country, day, "holiday2015");
						}
						
						boolean result = inst.globalHolidayMap2015.get(country).remove(new DayEntry(day, holidayValue));
						if(!result)
							inst.globalHolidayMap2015.get(country).remove(new DayEntry(day, holidayValue+"^!"));
						inst.showHolidays(day);
					}
					else if(inst._year.equals("2016"))
					{
						String temp = inst.holidayMap.get(day).replace(holidayValue, "");
						if(temp.contains("^!") || temp.contains("#"))
						{
							if(temp.length() <=3)
							{
								inst.holidayMap.remove(day);
								inst.dbHandler.deleteDbEntry(country, day, "holiday2016");
							}
							else
							{
								temp = temp.replace("# ^!", "").replace("#^!", "").replace("##", "#").replace("# #","#").replace("^!^!","^!");
								inst.holidayMap.put(day, temp);
								int val = inst.dbHandler.deleteDbEntry(country, day, "holiday2016", holidayValue);
								if(val == 0)
									inst.dbHandler.deleteDbEntry(country, day, "holiday2016", holidayValue+"^!");
							}
						}
						else
						{
							inst.holidayMap.remove(day);
							inst.dbHandler.deleteDbEntry(country, day, "holiday2016");
						}
						
						boolean result = inst.globalHolidayMap.get(country).remove(new DayEntry(day, holidayValue));
						if(!result)
							inst.globalHolidayMap.get(country).remove(new DayEntry(day, holidayValue+"^!"));
						inst.showHolidays(day);
					}
					inst.refreshCalendar();
				}
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure to delete entry on "+day +"?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
	
	}
	public void onClickBtn(View v)
	{
		finish();
	}
	
	private final RadioGroup.OnCheckedChangeListener onCheckedChangedListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(final RadioGroup group, final int checkedId) {
			//Reset the animations if screen changed
			int previousScreen = mPager.getCurrentScreen();
			
			// Slide to the appropriate screen when the user checks a button.
			switch (checkedId) {
			case R.id.radio_btn_0:
				if(previousScreen != 0)
					stopAnimation();
				mPager.setCurrentScreen(0, true);
				rb0.setText(Html.fromHtml("<b><u><font color='#00FF00'> Favorites </font></u><b>"));
				rb1.setText(Html.fromHtml("<b><u><font color='#FFFFFF'> Holidays </font></u><b>"));
				break;
			case R.id.radio_btn_1:
				if(previousScreen != 1)
					stopAnimation();
				mPager.setCurrentScreen(1, true);
				rb0.setText(Html.fromHtml("<b><u><font color='#FFFFFF'> Favorites </font></u><b>"));
				rb1.setText(Html.fromHtml("<b><u><font color='#00FF00'> Holidays </font></u><b>"));
				break;
			default:
				break;
			}
		}

		private void stopAnimation()
		{
			// TODO Auto-generated method stub
			
		}
	};
	private final HorizontalPager.OnScreenSwitchListener onScreenSwitchListener = new HorizontalPager.OnScreenSwitchListener() {
		@Override
		public void onScreenSwitched(final int screen) {
			//Reset the animations if screen changed
			int previousScreen = mPager.getCurrentScreen();
			
			// Check the appropriate button when the user swipes screens.
			switch (screen) {
			case 0:
				mRadioGroup.check(R.id.radio_btn_0);
				break;
			case 1:
				mRadioGroup.check(R.id.radio_btn_1);
				break;
			default:
				break;
			}
		}
	};
}

class DateCompare implements Comparator<Date>
{
	public int compare(Date one, Date two)
	{
		return one.compareTo(two);
	}
}
