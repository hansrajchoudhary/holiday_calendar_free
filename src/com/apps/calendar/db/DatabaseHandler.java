package com.apps.calendar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper
{

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 8;

	// Database Name
	private static final String DATABASE_NAME = "holidaysManager";

	// DbEntrys table name
	private static final String TABLE_HOLIDAYS14 = "holiday2014";

	private static final String TABLE_HOLIDAYS15 = "holiday2015";

	private static final String TABLE_HOLIDAYS16 = "holiday2016";

	// DbEntrys Table Columns names
	private static final String COUNTRY = "country";

	private static final String CODE = "code";

	private static final String DAY = "day";

	private static final String HOLIDAY = "holiday";

	private Context context;

	public DatabaseHandler(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		try
		{
			String CREATE_CONTACTS_TABLE15 = "CREATE TABLE " + TABLE_HOLIDAYS15 + "(" + COUNTRY + " TEXT," + CODE + " TEXT," + DAY + " TEXT," + HOLIDAY + " TEXT"
					+ ", CONSTRAINT uc_country_day UNIQUE (" + COUNTRY + "," + DAY + "," + HOLIDAY + "))";
			db.execSQL(CREATE_CONTACTS_TABLE15);
			populateDBWithFile("h2015.txt", db, TABLE_HOLIDAYS15);
			upgradeTask(db);
		}
		catch (Exception e)
		{
		}
	}

	private boolean isInteger(String s)
	{
		try
		{
			Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

	private void populateDBWithFile(String holidayFile, SQLiteDatabase db, String db_table)
	{
		BufferedReader reader;
		String zeile = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(context.getAssets().open(holidayFile)));
			zeile = reader.readLine();

			String country = "";
			String code = "";

			while (zeile != null)
			{
				try
				{
					if (zeile.startsWith("###"))
					{
						String country_code = zeile.substring(3);
						country = country_code.split(":")[0];
						code = country_code.split(":")[1];
					}
					else
					{
						String[] holiday_entry = zeile.split(" ");
						String day = "";

						if (isInteger(holiday_entry[1]))
							day = holiday_entry[0].substring(0, 3) + " " + ((holiday_entry[1].trim().length() == 1) ? "0" + holiday_entry[1].trim() : holiday_entry[1].trim());
						else
							day = holiday_entry[1].substring(0, 3) + " " + ((holiday_entry[2].trim().length() == 1) ? "0" + holiday_entry[2].trim() : holiday_entry[2].trim());

						String holiday = "";
						for (int i = 3; i < holiday_entry.length; i++)
							holiday = holiday + holiday_entry[i] + " ";
						DbEntry entry = new DbEntry();
						entry.setCountry(country);
						entry.setCode(code);
						entry.setDay(day);
						entry.setHoliday(holiday.trim());

						addEntry(db, entry, db_table);
					}
				}
				catch (Exception e)
				{
				}
				zeile = reader.readLine();
			}

			reader.close();

		}
		catch (Exception e)
		{
		}
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		try
		{
			fixDayValue(db, TABLE_HOLIDAYS15);
			fixDayValue(db, TABLE_HOLIDAYS16);
		}
		catch (Exception e)
		{
		}
	}

	private void fixDayValue(SQLiteDatabase db, String table)
	{
		if (!db.isOpen())
		{
			SQLiteDatabase.openDatabase(db.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
		}
		List<DbEntry> list = new ArrayList<DbEntry>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + table;

		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst())
		{
			do
			{
				String day = cursor.getString(2);
				String[] dayarr = day.split(" ");
				if(dayarr[1].trim().length() == 1)
				{
					String ctr = cursor.getString(0);
					String cd = cursor.getString(1);
					String ho = cursor.getString(3);
					deleteDbEntry(ctr, day, table, ho);
					
					day = dayarr[0]+" 0"+dayarr[1].trim();
					addEntry(new DbEntry(ctr,day,cd,ho), table);
				}
			}
			while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
	}

	private void upgradeTask(SQLiteDatabase db)
	{
		// version 6
		if(!isTableExists(db, TABLE_HOLIDAYS16))
		{
			addEntry(db, new DbEntry("Oman", "Jan 01", "OM", "New Year's Day"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Oman", "Jan 03", "OM", "Milad Un Nabi (The Prophet's Birthday)"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Oman", "May 16", "OM", "Lailat Al Miraj (The Prophet's Ascension)"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Oman", "Jun 18", "OM", "Eid Al Fitr"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Oman", "Jul 13", "OM", "Laylat al-Qadr"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Oman", "Jul 18", "OM", "Start of Ramadan"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Oman", "Jul 18", "OM", "Renaissance Day"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Oman", "Jul 23", "OM", "Renaissance Day"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Oman", "Sep 23", "OM", "Eid Al Adha"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Oman", "Oct 13", "OM", "Al Hijra (Islamic New Year)"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Oman", "Oct 23", "OM", "Ashura"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Oman", "Nov 18", "OM", "Birthday of HM Sultan Qaboos"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Oman", "Nov 18", "OM", "Oman National Day"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Oman", "Dec 24", "OM", "Prophet's Birthday"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Oman", "Dec 25", "OM", "Christmas Day"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Oman", "Dec 26", "OM", "Boxing Day Celebrated on 26th December"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Saudi Arabia", "Jan 01", "SA", "New Year's Day"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Saudi Arabia", "Jan 03", "SA", "Milad Un Nabi (The Prophet's Birthday)"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Saudi Arabia", "May 16", "SA", "Lailat Al Miraj (The Prophet's Ascension)"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Saudi Arabia", "Jun 18", "SA", "Eid Al Fitr"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Saudi Arabia", "Jul 13", "SA", "Laylat al-Qadr"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Saudi Arabia", "Jul 18", "SA", "Start of Ramadan"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Saudi Arabia", "Jul 18", "SA", "Renaissance Day"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Saudi Arabia", "Jul 23", "SA", "Saudi National Day"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Saudi Arabia", "Sep 23", "SA", "Eid Al Adha"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Saudi Arabia", "Oct 13", "SA", "Al Hijra (Islamic New Year)"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Saudi Arabia", "Oct 23", "SA", "Ashura"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Saudi Arabia", "Nov 18", "SA", "Birthday of HM Sultan Qaboos"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Saudi Arabia", "Nov 18", "SA", "Saudi Arabia National Day"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Saudi Arabia", "Dec 24", "SA", "Prophet's Birthday"), TABLE_HOLIDAYS15);

			addEntry(db, new DbEntry("Saudi Arabia", "Dec 25", "SA", "Christmas Day"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Saudi Arabia", "Dec 26", "SA", "Boxing Day     Celebrated on 26th December"), TABLE_HOLIDAYS15);

			addEntry(db, new DbEntry("Kenya", "Jan 01", "KE", "New Years Day"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Kenya", "Apr 03", "KE", "Good Friday  International Catholic holiday"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Kenya", "Apr 06", "KE", " Easter Monday Monday after Easter Sunday"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Kenya", "May 01", "KE", "Labour Day"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Kenya", "Jun 01", "KE", "Madaraka Day, commemorates the day that Kenya attained internal self-rule in 1963"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Kenya", "Jul 17", "KE", "Eid Al Fitr, End of Ramadan"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Kenya", "Sep 23", "KE", "Feast of the Sacrifice "), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Kenya", "Oct 20", "KE", "Mashujaa Day, Honours all those who contributed towards the struggle for independence"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Kenya", "Dec 12", "KE", "Jamhuri Day, Marks the date of Kenya's establishment as a republic on 12 December 1964"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Kenya", "Dec 25", "KE", "Christmas Day"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Kenya", "Dec 26", "KE", "Boxing Day, Celebrated on 26th December"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Tanzania", "Jan 01", "TZ", "New Years Day"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Tanzania", "Jan 03", "TZ", "Milad un Nabi (Birth of the Prophet Muhammad)"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Tanzania", "Jan 12", "TZ", "Zanzibar Revolution Day"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Tanzania", "Apr 03", "TZ", "Good Friday  International Catholic holiday"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Tanzania", "Apr 06", "TZ", " Easter Monday Monday after Easter Sunday"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Tanzania", "May 01", "TZ", "Labour Day"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Tanzania", "Jul 07", "TZ", "Saba Saba (Dar es salaam International Trade Fair Day)"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Tanzania", "Jul 17", "TZ", "Eid Al Fitr, End of Ramadan"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Tanzania", "Aug 08", "TZ", "Nane Nane (Farmers’ Day)"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Tanzania", "Sep 23", "TZ", "Feast of the Sacrifice "), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Tanzania", "Oct 14", "TZ", "Nyerere Day"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Tanzania", "Oct 20", "TZ", "Mashujaa Day, Honours all those who contributed towards the struggle for independence"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Tanzania", "Dec 09", "TZ", "Independence and Republic Day Tanzania"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Tanzania", "Dec 25", "TZ", "Christmas Day"), TABLE_HOLIDAYS15);
			addEntry(db, new DbEntry("Tanzania", "Dec 26", "TZ", "Boxing Day,Celebrated on 26th December"), TABLE_HOLIDAYS15);

			
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOLIDAYS16);
			String CREATE_CONTACTS_TABLE16 = "CREATE TABLE " + TABLE_HOLIDAYS16 + "(" + COUNTRY + " TEXT," + CODE + " TEXT," + DAY + " TEXT," + HOLIDAY + " TEXT"
					+ ", CONSTRAINT uc_country_day UNIQUE (" + COUNTRY + "," + DAY + "," + HOLIDAY + "))";
			db.execSQL(CREATE_CONTACTS_TABLE16);
	
			populateDBWithFile("h2016.txt", db, TABLE_HOLIDAYS16);
			populateDBWithFile("h2015_extra.txt", db, TABLE_HOLIDAYS15);
		}
	}

	boolean isTableExists(SQLiteDatabase db, String tableName)
	{
	    Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
	    if(cursor!=null) {
	        if(cursor.getCount()>0) {
	                            cursor.close();
	            return true;
	        }
	                    cursor.close();
	    }
	    return false;
	}
	
	void addEntry(SQLiteDatabase db, DbEntry entry, String table)
	{
		try
		{
			ContentValues values = new ContentValues();
			values.put(COUNTRY, entry.getCountry());
			values.put(CODE, entry.getCode());
			values.put(DAY, entry.getDay());
			values.put(HOLIDAY, entry.getHoliday());

			// Inserting Row
			db.insert(table, null, values);
		}
		catch (Exception e)
		{

		}
	}

	// Getting All DbEntrys
	public List<DbEntry> getAllDbEntrys(String table)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		if (!db.isOpen())
		{
			SQLiteDatabase.openDatabase(db.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
		}
		List<DbEntry> list = new ArrayList<DbEntry>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + table;

		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst())
		{
			do
			{
				DbEntry dbEntry = new DbEntry();
				dbEntry.setCountry(cursor.getString(0));
				dbEntry.setCode(cursor.getString(1));
				dbEntry.setDay(cursor.getString(2));
				dbEntry.setHoliday(cursor.getString(3));
				// Adding contact to list
				list.add(dbEntry);
			}
			while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		// return contact list
		return list;
	}

	public List<DbEntry> getAllDbEntrysForDay(String country, String day, String table)
	{
		List<DbEntry> list = new ArrayList<DbEntry>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + table + " where " + COUNTRY + " = " + country + " and " + DAY + " = " + day;

		SQLiteDatabase db = this.getWritableDatabase();
		if (!db.isOpen())
		{
			SQLiteDatabase.openDatabase(db.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
		}
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst())
		{
			do
			{
				DbEntry dbEntry = new DbEntry();
				dbEntry.setCountry(cursor.getString(0));
				dbEntry.setCode(cursor.getString(1));
				dbEntry.setDay(cursor.getString(2));
				dbEntry.setHoliday(cursor.getString(3));
				// Adding contact to list
				list.add(dbEntry);
			}
			while (cursor.moveToNext());
		}

		// return contact list
		db.close();
		return list;
	}

	// Updating single contact
	public int updateDbEntry(DbEntry entry, String oldHoliday, String table)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		if (!db.isOpen())
		{
			SQLiteDatabase.openDatabase(db.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
		}
		ContentValues values = new ContentValues();
		values.put(COUNTRY, entry.getCountry());
		values.put(CODE, entry.getCode());
		values.put(DAY, entry.getDay());
		values.put(HOLIDAY, entry.getHoliday());
		// updating row
		return db.update(table, values, COUNTRY + " = ? and " + CODE + " = ? and " + DAY + " = ? and " + HOLIDAY + " = ?", new String[] { entry.getCountry(), entry.getCode(),
				entry.getDay(), oldHoliday });
	}

	// Deleting single contact
	public int deleteDbEntry(String country, String day, String table)
	{
		int value = 0;
		try
		{
			SQLiteDatabase db = this.getWritableDatabase();
			if (!db.isOpen())
			{
				SQLiteDatabase.openDatabase(db.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
			}

			value = db.delete(table, COUNTRY + " = ? and " + DAY + " = ?", new String[] { country, day });
			db.close();
		}
		catch (Exception e)
		{
		}
		return value;
	}

	// Deleting single contact
	public int deleteDbEntry(String country, String day, String table, String holi)
	{
		int value = 0;
		try
		{
			SQLiteDatabase db = this.getWritableDatabase();
			if (!db.isOpen())
			{
				SQLiteDatabase.openDatabase(db.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
			}

			value = db.delete(table, COUNTRY + " = ? and " + DAY + " = ? and " + HOLIDAY + " = ?", new String[] { country, day, holi });
			db.close();
		}
		catch (Exception e)
		{
		}

		return value;
	}

	// Adding new contact
	void addEntry(DbEntry entry, String table)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		if (!db.isOpen())
		{
			SQLiteDatabase.openDatabase(db.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
		}

		ContentValues values = new ContentValues();
		values.put(COUNTRY, entry.getCountry());
		values.put(CODE, entry.getCode());
		values.put(DAY, entry.getDay());
		values.put(HOLIDAY, entry.getHoliday());

		// Inserting Row
		db.insert(table, null, values);
		db.close(); // Closing database connection
	}

	// Getting contacts Count
	public int getDbEntrysCount(String table)
	{
		String countQuery = "SELECT  * FROM " + table;
		SQLiteDatabase db = this.getWritableDatabase();
		if (!db.isOpen())
		{
			SQLiteDatabase.openDatabase(db.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
		}
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		db.close();

		// return count
		return cursor.getCount();
	}
}
