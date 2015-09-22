package com.apps.calendar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class CalendarView extends Activity
{
	public GregorianCalendar month, itemmonth;// calendar instances.
	public static CalendarView activity;

	public CalendarAdapter adapter;// adapter instance

	public Handler handler;// for grabbing some event values for showing the dot
							// marker.

	public ArrayList<String> items; // container to store calendar items which
									// needs showing the event marker

	Map<String, List<DayEntry>> globalHolidayMap;// 2016

	Map<String, List<DayEntry>> globalHolidayMap2015;// 2014

	Map<String, String> holidayMap;

	Map<String, String> holidayMap2015;

	Map<String, String> codeToCountryMap;

	ArrayList<String> holidayOfTheMonth;

	private TableLayout holidayView = null;

	private AdView adView;

	ArrayList<String> event;

	int screen_width;

	int screen_height;

	private MediaPlayer mPlayer = null;

	MediaRecorder mRecorder = null;

	ScrollView scrollView = null;

	Point p;

	String fName;

	String _country;

	String _year;

	DatabaseHandler dbHandler;

	private Dialog progressDialog;

	private class LoadViewTask extends AsyncTask<Void, Integer, Void>
	{
		// Before running code in separate thread
		@Override
		protected void onPreExecute()
		{
			progressDialog = new Dialog(CalendarView.this);

			progressDialog.setContentView(R.layout.splashscreen);
			progressDialog.setTitle("Loading...");
			SurfaceView v = (SurfaceView) progressDialog.findViewById(R.id.image);
			TextView tv = (TextView) progressDialog.findViewById(R.id.txt11);
			TextView tv2 = (TextView) progressDialog.findViewById(R.id.txt12);
			tv2.setText(Html.fromHtml(" US, India, Russia, UK, China, Malaysia, Canada, Singapore, Thailand, HK, Brazil, UAE, Pakistan, Indonesia, SriLanka, and more ...  "));

			tv.setText(Html.fromHtml("<h2 align center> * * * Enjoy * * * !! </h2>"));

			GifRun w = new GifRun();
			w.LoadGiff(v, progressDialog.getContext(), R.drawable.holiday);

			progressDialog.show();
		}

		// The code to be executed in a background thread.
		@Override
		protected Void doInBackground(Void... params)
		{
			initializeMaps();
			return null;
		}

		// after executing the code in the thread
		@Override
		protected void onPostExecute(Void result)
		{
			try
			{
				progressDialog.dismiss();
			}
			catch (Exception e)
			{
			}
			// initialize the View
			initializeAll();
		}
	}

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		new LoadViewTask().execute();
	}
	
	private void initializeMaps()
	{
		dbHandler = new DatabaseHandler(this);
		activity = this;
		boolean myFileFirstTime15 = false,myFileFirstTime16 = false;
		try{
			myFileFirstTime15 = createEmptyFileIfNotExists("my_list_15.txt");
			myFileFirstTime16 = createEmptyFileIfNotExists("my_list_16.txt");
		}
		catch(Exception e) {}
		codeToCountryMap = new HashMap<String, String>();
		populateHoliday2015(dbHandler,myFileFirstTime15);
		populateHoliday2016(dbHandler,myFileFirstTime16);
	}

	private void populateHoliday2016(DatabaseHandler dbHandler, boolean myFileFirstTime)
	{
		globalHolidayMap = new HashMap<String, List<DayEntry>>();
		try
		{
			List<DbEntry> list = dbHandler.getAllDbEntrys("holiday2016");
			for (DbEntry de : list)
			{
				String country = de.getCountry();
				String code = de.getCode();
				
				if (!codeToCountryMap.containsKey(country))
					codeToCountryMap.put(country, code);

				if (!globalHolidayMap.containsKey(country))
					globalHolidayMap.put(country, new ArrayList<DayEntry>());
				
				globalHolidayMap.get(country).add(dbEntryToDayEntry(de));
				
				if(myFileFirstTime && de.getHoliday().contains("^!"))
				{
					StringBuilder sb = new StringBuilder();
					sb.append(de.getCountry()).append(";;;").append(de.getCode()).append(";;;").append(de.getDay()).append(";;;").append(de.getHoliday());
					writeLineToFile("my_list_16.txt",sb.toString(),null);
				}
			}
		}
		catch (Exception e)
		{
		}
	}

	private void populateHoliday2015(DatabaseHandler dbHandler, boolean myFileFirstTime)
	{
		globalHolidayMap2015 = new HashMap<String, List<DayEntry>>();
		try
		{
			List<DbEntry> list = dbHandler.getAllDbEntrys("holiday2015");
			for (DbEntry de : list)
			{
				String country = de.getCountry();
				String code = de.getCode();
				if (!codeToCountryMap.containsKey(country))
					codeToCountryMap.put(country, code);
				
				if (!globalHolidayMap2015.containsKey(country))
					globalHolidayMap2015.put(country, new ArrayList<DayEntry>());
				globalHolidayMap2015.get(country).add(dbEntryToDayEntry(de));
				
				if(myFileFirstTime && de.getHoliday().contains("^!"))
				{
					StringBuilder sb = new StringBuilder();
					sb.append(de.getCountry()).append(";;;").append(de.getCode()).append(";;;").append(de.getDay()).append(";;;").append(de.getHoliday());
					writeLineToFile("my_list_15.txt",sb.toString(),null);
				}
			}
		}
		catch (Exception e)
		{
		}
	}

	public DayEntry dbEntryToDayEntry(DbEntry de)
	{
		DayEntry dayEntry = new DayEntry();
		dayEntry.setDayStr(de.getDay());
		dayEntry.setNameStr(de.getHoliday());
		return dayEntry;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		/***** Handle small screen device ******/
		if (adapter != null)
		{
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);

			screen_height = metrics.heightPixels;
			screen_width = metrics.widthPixels;
			adapter.width = screen_width;
			adapter.height = screen_height;
		}
	}

	private void initializeAll()
	{
		setContentView(R.layout.calendar);
		month = (GregorianCalendar) GregorianCalendar.getInstance();
		itemmonth = (GregorianCalendar) month.clone();

		/***** Handle small screen device ******/
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		if (screen_height == 0)
		{
			screen_height = metrics.heightPixels;
			screen_width = metrics.widthPixels;
		}
		/**************/
		scrollView = (ScrollView) findViewById(R.id.scrollView1);
		holidayView = (TableLayout) findViewById(R.id.holiholi);
		holidayView.setOnTouchListener(new OnSwipeTouchListener()
		{

			public void onSwipeLeft()
			{
				setNextMonth();
				refreshCalendar();
			}

			public void onSwipeRight()
			{
				setPreviousMonth();
				refreshCalendar();
			}

		});
		initializeHolidays();

		items = new ArrayList<String>();
		adapter = new CalendarAdapter(this, month, holidayOfTheMonth, screen_width, screen_height);

		MyGridView gridview = (MyGridView) findViewById(R.id.gridview);
		gridview.setAdapter(adapter);

		handler = new Handler();
		handler.post(calendarUpdater);

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
		title.setTextColor(Color.WHITE);
		title.setTextSize(getTextSize());

		RelativeLayout previous = (RelativeLayout) findViewById(R.id.previous);

		previous.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setPreviousMonth();
				refreshCalendar();
			}
		});

		RelativeLayout next = (RelativeLayout) findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				setNextMonth();
				refreshCalendar();

			}
		});

		gridview.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{

				((CalendarAdapter) parent.getAdapter()).setSelected(v);

				String selectedGridDate = CalendarAdapter.dayString.get(position);
				String[] separatedTime = selectedGridDate.split("-");
				String gridvalueString = separatedTime[2].replaceFirst("^0*", "");// taking last part of date. ie; 2 from 2012-12-02.
				int month = Integer.parseInt(separatedTime[1]);
				int gridvalue = Integer.parseInt(gridvalueString);

				((CalendarAdapter) parent.getAdapter()).setSelected(v);

				String selectedDay = Constants.getMonthName(month) + " " + separatedTime[2];
				showHolidays(selectedDay);
				// navigate to next or previous month on clicking offdays.
				if ((gridvalue > 10) && (position < 8))
				{
					setPreviousMonth();
					refreshCalendar();
				}
				else if ((gridvalue < 7) && (position > 28))
				{
					setNextMonth();
					refreshCalendar();
				}
			}
		});

		gridview.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id)
			{

				((CalendarAdapter) parent.getAdapter()).setSelected(v);
				int firstDay = ((CalendarAdapter) parent.getAdapter()).firstDay;

				String selectedGridDate = CalendarAdapter.dayString.get(position);
				String[] separatedTime = selectedGridDate.split("-");
				String gridvalueString = separatedTime[2].replaceFirst("^0*", "");// taking last part of date. ie; 2 from 2012-12-02.
				int month = Integer.parseInt(separatedTime[1]);
				int gridvalue = Integer.parseInt(gridvalueString);

				((CalendarAdapter) parent.getAdapter()).setSelected(v);

				String selectedDay = Constants.getMonthName(month) + " " + separatedTime[2];
				boolean showPopUp = true;
				if ((gridvalue > 1 && position < firstDay) || (gridvalue < 7 && position > 28))
					showPopUp = false;

				int[] location = new int[2];
				v.getLocationOnScreen(location);
				p = new Point();
				p.x = location[0];
				p.y = location[1];
				// Open popup window
				if (p != null && showPopUp)
				{
					fName = separatedTime[0] + "_" + separatedTime[1] + "_" + separatedTime[2];

					String vaar = "Sunday";
					if (position % 7 == 1)
						vaar = "Monday";
					if (position % 7 == 2)
						vaar = "Tuesday";
					if (position % 7 == 3)
						vaar = "Wednesday";
					if (position % 7 == 4)
						vaar = "Thursday";
					if (position % 7 == 5)
						vaar = "Friday";
					if (position % 7 == 6)
						vaar = "Saturday";
					showPopup(CalendarView.this, p, fName, selectedDay, vaar, position, v);
				}
				return true;
			}
		});

		setOnTouchListener();

		AdView adView = (AdView)this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
	    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	    .build();

		adView.loadAd(adRequest);
	}

	private void initializeHolidays()
	{
		Spinner spinner = (Spinner) findViewById(R.id.countries);
		spinner.setOnItemSelectedListener(new MyItemSelectedListener(this));

		String countryId = Locale.getDefault().getCountry();
		List<String> list = new ArrayList<String>();

		// get selected country ----------//
		String selectedCountry = null;
		try
		{
			selectedCountry = readUsersLastValueOfLocale();
		}
		catch (Exception e)
		{
		}
		if (selectedCountry == null)
			selectedCountry = codeToCountryMap.get(countryId);
		if (selectedCountry == null)
			selectedCountry = "USA";
		// finished selected country --------//
		for (String cntr : globalHolidayMap2015.keySet())
		{
			list.add(cntr);
		}
		Collections.sort(list);

		int index = 0;
		int i = -1;
		List<CharSequence> list1 = new ArrayList<CharSequence>();
		for (String str : list)
		{
			if (getResources().getBoolean(R.bool.isTablet))
				list1.add(Html.fromHtml("<u><big>" + str + "</h4></big>"));
			else
				list1.add(Html.fromHtml("<u>" + str + "</u>"));
			
			if (selectedCountry.equals(str))
			{
				i = index;
			}
			index++;
		}

		if (i == -1)
		{
			selectedCountry = "USA";
			i = list.indexOf("USA");
		}

		ArrayAdapter<CharSequence> dataAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, list1);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		spinner.setSelection(i);
		refreshCountryHoliday(selectedCountry);
	}

	public void refreshCountryHoliday(String selectedCountry)
	{
		_country = selectedCountry;
		holidayMap = new HashMap<String, String>();
		holidayMap2015 = new HashMap<String, String>();

		List<DayEntry> entries = globalHolidayMap.get(selectedCountry);
		if(entries != null)
		{
			for (DayEntry dayEntry : entries)
			{
				String nameStr = dayEntry.getNameStr();
				if (holidayMap.containsKey(dayEntry.getDayStr()))
				{
					String tmp = holidayMap.get(dayEntry.getDayStr());
					if (!nameStr.contains(tmp))
					{
						nameStr = tmp + "# " + nameStr;
					}
				}
				holidayMap.put(dayEntry.getDayStr(), nameStr);
			}
		}
		List<DayEntry> entries1 = globalHolidayMap2015.get(selectedCountry);
		if(entries1 != null)
		{
			for (DayEntry dayEntry : entries1)
			{
				String nameStr = dayEntry.getNameStr();
				if (holidayMap2015.containsKey(dayEntry.getDayStr()))
				{
					String tmp = holidayMap2015.get(dayEntry.getDayStr());
					if (!nameStr.contains(tmp))
					{
						nameStr = tmp + "# " + nameStr;
					}
				}
				holidayMap2015.put(dayEntry.getDayStr(), nameStr);
			}
		}
		refreshHolidayOfTheMonth();
		showHolidays();
	}

	private void setOnTouchListener()
	{
		RelativeLayout calItems = (RelativeLayout) findViewById(R.id.header);
		calItems.setOnTouchListener(new OnSwipeTouchListener()
		{

			public void onSwipeLeft()
			{
				setNextMonth();
				refreshCalendar();
			}

			public void onSwipeRight()
			{
				setPreviousMonth();
				refreshCalendar();
			}

		});
		TableLayout tableHeader = (TableLayout) findViewById(R.id.table_header);
		tableHeader.setOnTouchListener(new OnSwipeTouchListener()
		{

			public void onSwipeLeft()
			{
				setNextMonth();
				refreshCalendar();
			}

			public void onSwipeRight()
			{
				setPreviousMonth();
				refreshCalendar();
			}

		});
		
		RelativeLayout calItems1 = (RelativeLayout) findViewById(R.id.calItems);
		calItems1.setOnTouchListener(new OnSwipeTouchListener()
		{

			public void onSwipeLeft()
			{
				setNextMonth();
				refreshCalendar();
			}

			public void onSwipeRight()
			{
				setPreviousMonth();
				refreshCalendar();
			}

		});
	}

	protected void setNextMonth()
	{
		if (month.get(GregorianCalendar.MONTH) == month.getActualMaximum(GregorianCalendar.MONTH))
		{
			month.set((month.get(GregorianCalendar.YEAR) + 1), month.getActualMinimum(GregorianCalendar.MONTH), 1);
		}
		else
		{
			month.set(GregorianCalendar.MONTH, month.get(GregorianCalendar.MONTH) + 1);
		}

	}

	protected void setPreviousMonth()
	{
		if (month.get(GregorianCalendar.MONTH) == month.getActualMinimum(GregorianCalendar.MONTH))
		{
			month.set((month.get(GregorianCalendar.YEAR) - 1), month.getActualMaximum(GregorianCalendar.MONTH), 1);
		}
		else
		{
			month.set(GregorianCalendar.MONTH, month.get(GregorianCalendar.MONTH) - 1);
		}

	}

	private String readUsersLastValueOfLocale() throws IOException
	{
		String sampleDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HolidaCal";
		File file = new File(sampleDir + "/mylocale.txt");

		if (!file.exists())
		{
			return null;
		}
		String ret = null;

		FileInputStream inputStream = openFileInput(file.getName());
		if (inputStream != null)
		{
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String receiveString = "";
			StringBuilder stringBuilder = new StringBuilder();

			while ((receiveString = bufferedReader.readLine()) != null)
			{
				stringBuilder.append(receiveString);
			}

			inputStream.close();
			ret = stringBuilder.toString().trim();
		}

		return ret;
	}

	public void refreshCalendar()
	{
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
		refreshHolidayOfTheMonth();

		adapter.setHolidayOfTheMonth(holidayOfTheMonth);
		adapter.refreshDays();
		adapter.notifyDataSetChanged();
		handler.post(calendarUpdater); // generate some calendar items
		showHolidays();
	}

	private void refreshHolidayOfTheMonth()
	{
		holidayOfTheMonth = new ArrayList<String>();
		Map<String, String> map = new HashMap<String, String>();
		if (month.get(GregorianCalendar.YEAR) == 2016)
		{
			_year = "2016";
			map = holidayMap;
		}
		if (month.get(GregorianCalendar.YEAR) == 2015)
		{
			_year = "2015";
			map = holidayMap2015;
		}

		for (Entry<String, String> entry : map.entrySet())
		{
			String monthStr = Constants.getMonthName(month.get(GregorianCalendar.MONTH) + 1);
			if (entry.getKey().contains(monthStr))
			{
				holidayOfTheMonth.add(entry.getKey() + " : " + entry.getValue() + "\n");
			}
		}

		Collections.sort(holidayOfTheMonth);
	}

	private void showHolidays()
	{
		showHolidays(null);
	}

	private int getTextSize()
	{
		int textSize = 14;
		try
		{
			if (getResources().getBoolean(R.bool.isTablet))
			{
				textSize = 18;
			}
		}
		catch(Exception e){}
		
		return textSize;
	}

	protected String showHolidays(String selected)
	{
		holidayView.removeAllViews();
		String selectedHolidayStr = "";
		int i = 0;
		boolean end = false;
		for (String str : holidayOfTheMonth)
		{
			TableRow row = new TableRow(this);
			TextView t1 = new TextView(this);
			TextView t2 = new TextView(this);
			i++;
			String str1 = str.replace("^!", "");
			String day = str1.split(":")[0].trim();
			String hol = str1.split(":")[1];
			boolean showHol = false;
			if (_year.equals("2015") && holidayMap2015.containsKey(day))
				showHol = true;
			else if (_year.equals("2016") && holidayMap.containsKey(day))
				showHol = true;

			if (showHol)
			{
				t1.setText(Html.fromHtml("<font color='#FF0000'>" + day + ": </font>"));
				if(hol.startsWith("#"))
					hol = hol.replaceFirst("#", "");
				if(hol.endsWith("#"))
					hol = hol.substring(0, hol.length()-1);
				t2.setText(Html.fromHtml("<font color='#000000'>" + hol.replace("#", "<br />") + "</font>"));
				if (selected != null && str1.contains(selected))
				{
					t1.setText(Html.fromHtml("<b><font color='#FF0000'>" + day + ": </font></b>"));
					t2.setText(Html.fromHtml("<b><font color='#0000FF'>" + hol.replace("#", "<br />") + "</font></b>"));
					selectedHolidayStr = day + " : " + hol;
					if (i > 7)
						end = true;
				}
				t1.setTextSize(getTextSize());
				t2.setTextSize(getTextSize());
				t2.setSingleLine(false);

				row.addView(t1);
				row.addView(t2);
				if(i%2 == 0)
					row.setBackgroundColor(Color.parseColor("#E8E8E8"));
				holidayView.addView(row, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
			}
		}

		if (end)
			scrollView.fullScroll(ScrollView.FOCUS_DOWN);
		else
			scrollView.fullScroll(ScrollView.FOCUS_UP);

		return selectedHolidayStr;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_menu, menu);
		return true;
	}

	@Override
	public void onDestroy()
	{
		if (adView != null)
		{
			adView.destroy();
		}
		if (mPlayer != null)
		{
			try
			{
				mPlayer.stop();
				mPlayer.release();
			}
			catch (Exception e)
			{
				// TODO: handle exception
			}
			mPlayer = null;
		}
		super.onDestroy();
	}

	public void exitApp()
	{
		finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch (item.getItemId())
		{
		case R.id.about:
			startActivity(new Intent(this, About.class));
			return true;
		case R.id.favorite:
			Intent intent = new Intent(this, Favorite.class);
			Bundle b = new Bundle();
			StringBuilder str = new StringBuilder();
			for (Entry<String, String> entry : holidayMap.entrySet())
			{
				String[] s = entry.getKey().split(" ");
				if(entry.getValue().contains("^!"))
					str = str.append(s[1]).append("-").append(Constants.getMonthInt(s[0])).append("-2016:::").append(entry.getValue()).append("###");
			}
			for (Entry<String, String> entry : holidayMap2015.entrySet())
			{
				String[] s = entry.getKey().split(" ");
				if(entry.getValue().contains("^!"))
					str = str.append(s[1]).append("-").append(Constants.getMonthInt(s[0])).append("-2015:::").append(entry.getValue()).append("###");
			}
			b.putString("key", str.toString()); // Your id

			intent.putExtras(b); // Put your id to your next Intent
			startActivity(intent);
			return true;
		case R.id.like:
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.apps.calendar")));
			return true;
		case R.id.ad_free:
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.apps.calendarnew")));
			return true;
		case R.id.exit:
			exitApp();
			return true;
		default:
			return true;
		}
	}
	public TreeMap<String, String> getMyFavList(){
		TreeMap<String, String> myHolidayMap = new TreeMap<String, String>();
		String myHolidays = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HolidaCal/my_holidays.txt";
		File file = new File(myHolidays);
	
		try
		{
			FileInputStream inputStream = openFileInput(file.getName());
			if (inputStream != null)
			{
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
	
				while ((receiveString = bufferedReader.readLine()) != null)
				{
					String[] hh = receiveString.split("###");
					for (String ss : hh)
						if (ss.split(":::").length == 2)
							myHolidayMap.put(ss.split(":::")[0], ss.split(":::")[1]);
				}
	
				inputStream.close();
			}
		}
		catch (Exception e)
		{
		}
		return myHolidayMap;
	}

	public void alertMessage(final TextView puTextView, final String puText, final String day, final View adapterView, final String vaar, final TextView fav)
	{
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				if (which == DialogInterface.BUTTON_POSITIVE)
				{
					holidayMap.remove(day);

					if (_year.equals("2015"))
					{
						dbHandler.deleteDbEntry(_country, day, "holiday2015");
						holidayMap2015.remove(day);
						showHolidays(day);
						globalHolidayMap2015.get(_country).remove(new DayEntry(day));
					}
					else
					{
						dbHandler.deleteDbEntry(_country, day, "holiday2016");
						holidayMap.remove(day);
						showHolidays(day);
						globalHolidayMap.get(_country).remove(new DayEntry(day));
					}
					fav.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.fav, 0);
					puTextView.setText(Html.fromHtml(vaar));
					TextView dayView = (TextView) adapterView.findViewById(R.id.date);
					dayView.setTextSize(getTextSize());
					dayView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
					((TextView) adapterView.findViewById(R.id.holi)).setText("");
				}
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to delete the holiday?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
	}
	// The method that displays the popup.
	private void showPopup(final Activity context, Point p, final String fileName, final String selectedDay, final String vaar, int position, final View adapterView)
	{
		int popupWidth = (screen_width * 10) / 12;
		int popupHeight = (screen_height * 10) / 23;
		try
		{
			if(getResources().getBoolean(R.bool.isTablet))
			{
				popupWidth = (screen_width * 10) / 15;
				popupHeight = (screen_height * 10) / 35;
			}
		}
		catch(Exception e){}
		// Inflate the popup_layout.xml
		LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = layoutInflater.inflate(R.layout.popup_layout, viewGroup);

		// Creating the PopupWindow
		final MyPopupWindow popup = new MyPopupWindow(context, this);
		popup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		popup.setContentView(layout);
		popup.setWidth(popupWidth);
		popup.setHeight(popupHeight);
		popup.setFocusable(true);

		// Some offset to align the popup a bit to the right, and a bit down,
		// relative to button's position.
		int OFFSET_X = 10;
		int OFFSET_Y = 20;

		// Clear the default translucent background
		popup.setBackgroundDrawable(new BitmapDrawable());

		// Displaying the popup at the specified location, + offsets.
		popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

		// Getting a reference to Close button, and close the popup when
		// clicked.
		final TextView puTextView = (TextView) layout.findViewById(R.id.puTitle);
		final TextView fav = (TextView) layout.findViewById(R.id.fav);
		final TextView delete = (TextView) layout.findViewById(R.id.del);

		String tmpTxt = showHolidays(selectedDay);
		puTextView.setTextSize(getTextSize());
		if(tmpTxt.startsWith("#"))
			tmpTxt = tmpTxt.replaceFirst("#", "");
		if(tmpTxt.endsWith("#"))
			tmpTxt = tmpTxt.substring(0, tmpTxt.length()-1);
		if (tmpTxt != "")
			puTextView.setText(Html.fromHtml(tmpTxt.replace("#", "<br />")));
		else
		{
			tmpTxt = "<font color='#f0020c'>" + selectedDay + "</font> : " + vaar;
			puTextView.setText(Html.fromHtml(tmpTxt.replace("#", "<br />")));
		}

		final String puText = tmpTxt;

		String[] separatedTime = CalendarAdapter.dayString.get(position).split("-");
		final String fName = separatedTime[0] + "_" + separatedTime[1] + "_" + separatedTime[2];

		final TextView close = (TextView) layout.findViewById(R.id.cancelPop);
		final TextView save = (TextView) layout.findViewById(R.id.savePop);
		final TextView recordNew = (TextView) layout.findViewById(R.id.recordNew);
		final TextView playRecording = (TextView) layout.findViewById(R.id.playSaved);
		final TextView deleteRecording = (TextView) layout.findViewById(R.id.deleteRec);
		final Button addButton = (Button) layout.findViewById(R.id.titleButon);
		final CalendarView inst = this;
		close.setTextSize(getTextSize());
		save.setTextSize(getTextSize());
		recordNew.setTextSize(getTextSize());
		playRecording.setTextSize(getTextSize());
		deleteRecording.setTextSize(getTextSize());

		if(Constants.DAYS.contains(puText.split(":")[1].trim().toLowerCase()))
			fav.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.fav, 0);
		else
			fav.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.text_edit, 0);

		delete.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete, 0);
			
		delete.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String day = puText.split(":")[1].trim();
				if (!Constants.DAYS.contains(day.toLowerCase()))
				{
					String vaarHoliday = puText.split(":")[0] + ":" + vaar;
					alertMessage(puTextView, puText, selectedDay, adapterView, vaarHoliday, fav);
				}
			}
		});

		fav.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				try
				{
					LinearLayout rl = (LinearLayout) layout.findViewById(R.id.box1);
					if (rl.getVisibility() == View.VISIBLE)
						rl.setVisibility(View.GONE);
					else
					{
						EditText et = (EditText) layout.findViewById(R.id.titleEditText);
						et.setBackgroundColor(Color.parseColor("#FFFFFF"));
						String hh = puText.split(":")[1];
						if(!(Constants.DAYS.contains(hh.trim().toLowerCase())))
							et.setHint("Optional Text !");
						else
							et.setHint("Add Here !");

						rl.setVisibility(View.VISIBLE);
					}
				}
				catch (Exception e)
				{
				}
			}
		});

		addButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				try
				{
					String myStr = ((EditText) layout.findViewById(R.id.titleEditText)).getText().toString();
					String holidayText = "";
					String hh = puText.split(":")[1];
					if(Constants.DAYS.contains(hh.trim().toLowerCase()) && (myStr == null || myStr.length() ==0))
					{
						Toast.makeText(getApplicationContext(), "Please write some text...!!!", Toast.LENGTH_SHORT).show();
						return;
					}
					if (_year == "2015")
					{
						if (holidayMap2015.containsKey(selectedDay))
						{
							if(myStr == null || myStr.length() ==0)
							{
								holidayText = holidayMap2015.get(selectedDay) +"^!";
								if(!holidayMap2015.get(selectedDay).contains("^!"))
								{
									String old1 = holidayMap2015.get(selectedDay);
									if(old1.startsWith("#"))
										old1 = old1.replaceFirst("#", "");
									if(old1.endsWith("#"))
										old1 = old1.substring(0, old1.length()-1);
									
									String old = old1.split("#")[0];
									dbHandler.updateDbEntry(new DbEntry(_country, selectedDay, codeToCountryMap.get(_country),old+"^!"), old, "holiday2015");
									writeLineToFile("my_list_15.txt", old+"^!",selectedDay);
								}							
							}
							else
							{
								holidayText = myStr+"#"+holidayMap2015.get(selectedDay) +"^!";
								dbHandler.addEntry(new DbEntry(_country, selectedDay, codeToCountryMap.get(_country), myStr+"^!"), "holiday2015");
								writeLineToFile("my_list_15.txt", myStr+"^!",selectedDay);
							}
						}
						else
						{
							holidayText = myStr +"^!";
							dbHandler.addEntry(new DbEntry(_country, selectedDay, codeToCountryMap.get(_country), holidayText), "holiday2015");
							writeLineToFile("my_list_15.txt", myStr+"^!",selectedDay);
						}

						holidayMap2015.put(selectedDay, holidayText);
						globalHolidayMap2015.get(_country).remove(new DayEntry(selectedDay, holidayText));
						globalHolidayMap2015.get(_country).add(new DayEntry(selectedDay, holidayText));
					}
							
					if (_year == "2016")
					{
						if (holidayMap.containsKey(selectedDay))
						{
							if(myStr == null || myStr.length() ==0)
							{
								holidayText = holidayMap.get(selectedDay) +"^!";
								if(!holidayMap.get(selectedDay).contains("^!"))
								{
									String old1 = holidayMap.get(selectedDay);
									if(old1.startsWith("#"))
										old1 = old1.replaceFirst("#", "");
									if(old1.endsWith("#"))
										old1 = old1.substring(0, old1.length()-1);
									
									String old = old1.split("#")[0];
									dbHandler.updateDbEntry(new DbEntry(_country, selectedDay, codeToCountryMap.get(_country),old+"^!"), old, "holiday2016");
									writeLineToFile("my_list_16.txt", old+"^!",selectedDay);
								}							
							}
							else
							{
								holidayText = myStr+"#"+holidayMap.get(selectedDay) +"^!";
								dbHandler.addEntry(new DbEntry(_country, selectedDay, codeToCountryMap.get(_country), myStr+"^!"), "holiday2016");
								writeLineToFile("my_list_16.txt", myStr+"^!",selectedDay);
							}
						}
						else
						{
							holidayText = myStr +"^!";
							dbHandler.addEntry(new DbEntry(_country, selectedDay, codeToCountryMap.get(_country), holidayText), "holiday2016");
							writeLineToFile("my_list_16.txt", myStr+"^!",selectedDay);
						}
						holidayMap.put(selectedDay, holidayText);
						globalHolidayMap.get(_country).remove(new DayEntry(selectedDay, holidayText));
						globalHolidayMap.get(_country).add(new DayEntry(selectedDay, holidayText));
					}
					
					puTextView.setText(Html.fromHtml("<font color='#f0020c'>" + selectedDay + "</font> : " + holidayText.replace("^!", "")));
					delete.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete, 0);
					fav.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.text_edit, 0);
					((LinearLayout) layout.findViewById(R.id.box1)).setVisibility(View.GONE);
					refreshHolidayOfTheMonth();
					refreshCalendar();
					showHolidays(selectedDay);
					//Toast.makeText(getApplicationContext(), "/fully Added to Favorite List!", Toast.LENGTH_SHORT).show();
				}
				catch (Exception e)
				{
					//Toast.makeText(getApplicationContext(), "Error while adding to Favorite List! "+e.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
		});

		recordNew.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				onRecordLinkClick(recordNew, playRecording, save, deleteRecording, fileName, adapterView);
			}
		});
		playRecording.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				startPlaying(playRecording, recordNew, fileName);
			}
		});
		save.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				onSaveButtonClick((TextView) v, recordNew, deleteRecording, fileName);
			}
		});
		deleteRecording.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				onDeleteRecordingClick((TextView) v, playRecording, fileName, adapterView);
			}
		});
		close.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				popup.dismiss();
			}
		});
	}

	public void onPlayFinished(TextView playRecording)
	{
		playRecording.setText(Html.fromHtml("<u>Play Msg</u>"));
	}

	public void onDeleteRecordingClick(TextView deleteRec, TextView playView, String fileName, View adapterView)
	{
		String file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HolidaCal/" + fileName + ".3gp";
		File f = new File(file);

		if (f.exists())
		{
			if (playView.getText().toString().contains("Playing"))
			{
				stopPlaying();
				playView.setText(Html.fromHtml("<u>Play Msg</u>"));
			}

			f.delete();
			deleteRec.setText("Deleted");
			TextView dayView = (TextView) adapterView.findViewById(R.id.date);
			dayView.setTextSize(getTextSize());
			dayView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}
	}

	public void onRecordLinkClick(TextView recordView, TextView playView, TextView save, TextView deleteView, String fileName, View adapterView)
	{
		if (playView.getText().toString().contains("Playing"))
		{
			stopPlaying();
			playView.setText(Html.fromHtml("<u>Play Msg</u>"));
		}

		String text = recordView.getText().toString();
		if (text.contains("New"))
		{
			String sampleDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HolidaCal";
			File holiDir = new File(sampleDir);
			if (!holiDir.exists())
				holiDir.mkdir();

			try
			{
				createEmptyFileIfNotExists(fileName + ".3gp");
				startRecording(sampleDir + "/" + fileName + ".3gp");
			}
			catch (Exception e)
			{
				recordView.setText("Error:No-SD");
				return;
			}
			save.setText(Html.fromHtml("<u>Save</u>"));
			save.setTextColor(-65536);
			recordView.setText(Html.fromHtml("<u>Recording...</u>"));
			TextView dayView = (TextView) adapterView.findViewById(R.id.date);
			dayView.setTextSize(getTextSize());
			dayView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.recording, 0);
			dayView.setCompoundDrawablePadding(12);
		}
		else
			onSaveButtonClick(save, recordView, deleteView, fileName);
	}

	public void onSaveButtonClick(TextView v, TextView text, TextView deleteView, String fileName)
	{
		if (!v.getText().toString().equals("Save"))
			return;

		v.setTextColor(-16777216);
		text.setText(Html.fromHtml("<u>Record New</u>"));
		deleteView.setText(Html.fromHtml("<u>Delete</u>"));
		v.setText("Saved");
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
		String file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HolidaCal/" + fileName + ".3gp";
		addRecordingToMediaLibrary(file);
	}

	protected void addRecordingToMediaLibrary(String file)
	{
		ContentValues values = new ContentValues(4);
		long current = System.currentTimeMillis();
		values.put(MediaStore.Audio.Media.TITLE, "audio" + file);
		values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
		values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp");
		values.put(MediaStore.Audio.Media.DATA, file);
		ContentResolver contentResolver = getContentResolver();

		Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Uri newUri = contentResolver.insert(base, values);

		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
	}

	private void startRecording(String fileName) throws Exception
	{
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mRecorder.setOutputFile(fileName);
		try
		{
			mRecorder.prepare();
		}
		catch (IOException e)
		{
			throw e;
		}

		mRecorder.start();
	}

	private void startPlaying(final TextView playView, TextView text, String fileName)
	{
		String file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HolidaCal/" + fileName + ".3gp";

		File f = new File(file);
		if (!f.exists())
			return;

		if (text.getText().toString().contains("Recording"))
		{
			return;
		}

		if (playView.getText().toString().contains("Playing"))
		{
			try
			{
				if (mPlayer != null && mPlayer.isPlaying())
				{
					mPlayer.stop();
					playView.setText(Html.fromHtml("<u>Play Msg</u>"));
					return;
				}
			}
			catch (Exception e)
			{
			}
		}

		mPlayer = new MediaPlayer();
		try
		{
			mPlayer.setDataSource(file);
			mPlayer.prepare();
			mPlayer.start();
			playView.setText(Html.fromHtml("<u>Playing...</u>"));
			mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
			{
				@Override
				public void onCompletion(MediaPlayer mp)
				{
					onPlayFinished(playView);
				}
			});
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void stopPlaying()
	{
		try
		{
			if (mPlayer != null && mPlayer.isPlaying())
			{
				mPlayer.stop();
				mPlayer.release();
			}
		}
		catch (Exception e)
		{
		}
		mPlayer = null;
	}

	public Runnable calendarUpdater = new Runnable()
	{

		@Override
		public void run()
		{
			items.clear();

			// Print dates of the current week
			Locale locale = Locale.getDefault();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd", locale);
			String itemvalue;

			// event = Utility.readCalendarEvent(CalendarView.this);

			for (int i = 0; i < Utility.startDates.size(); i++)
			{
				itemvalue = df.format(itemmonth.getTime());
				itemmonth.add(GregorianCalendar.DATE, 1);
				items.add(Utility.startDates.get(i).toString());
			}

			adapter.setItems(items);
			adapter.notifyDataSetChanged();
		}
	};

	private boolean createEmptyFileIfNotExists(String fileName) throws IOException
	{
		String sampleDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HolidaCal";
		File holiDir = new File(sampleDir);
		if (!holiDir.exists())
			holiDir.mkdir();

		File file = new File(sampleDir + "/" + fileName);

		if (!file.exists())
		{
			file.createNewFile();
			FileOutputStream writer = this.openFileOutput(file.getName(), Context.MODE_APPEND | Context.MODE_WORLD_READABLE);

			writer.write("".getBytes());
			writer.flush();
			writer.close();
			return true;
		}
	    file.setReadable(true, false);

		return false;
	}
	
	private void writeLineToFile(String fileName, String line, String day) throws IOException
	{
		String sampleDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HolidaCal";
		File file = new File(sampleDir + "/" + fileName);
		
		if (file.exists())
		{
			try
			{
		    file.setReadable(true, false);
			FileOutputStream writer = this.openFileOutput(file.getName(), Context.MODE_APPEND | Context.MODE_WORLD_READABLE);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(writer));
			if(day != null)
				line = _country+";;;"+day+";;;"+codeToCountryMap.get(_country)+";;;"+line;
			bw.append(line);
			bw.newLine();
			bw.close();
			}
			catch(Exception e)
			{
			}
		}
	}
}

class MyItemSelectedListener implements OnItemSelectedListener
{
	CalendarView instance;

	public MyItemSelectedListener(CalendarView instance)
	{
		this.instance = instance;
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
	{
		String selected = parent.getItemAtPosition(pos).toString();
		try
		{
			writeSelectedValueToFile(selected);
		}
		catch (Exception e)
		{
			// ignore
		}
		instance.refreshCountryHoliday(selected);
		instance.refreshCalendar();
	}

	private void writeSelectedValueToFile(String selected) throws IOException
	{
		String sampleDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HolidaCal";
		File holiDir = new File(sampleDir);
		if (!holiDir.exists())
			holiDir.mkdir();

		File file = new File(sampleDir + "/mylocale.txt");
		file.setWritable(true, false);
		if (!file.exists())
		{
			file.createNewFile();
		}

		FileOutputStream writer = instance.openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);

		writer.write(selected.getBytes());
		writer.flush();
		writer.close();
	}

	public void onNothingSelected(AdapterView parent)
	{
		// Do nothing.
	}
}
