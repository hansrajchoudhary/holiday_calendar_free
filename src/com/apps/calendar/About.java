package com.apps.calendar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class About extends Activity implements OnClickListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		View okButton = findViewById(R.id.about_ok);
		okButton.setOnClickListener(this);
		View upButton = findViewById(R.id.about_up);
		upButton.setOnClickListener(this);
		View projectUrl = findViewById(R.id.project_url);
		projectUrl.setOnClickListener(this);

		TextView about_content = (TextView) findViewById(R.id.about_content);
		String about_text = "<font color='#3300FF'> This is the simplest holiday calendar like the physical one, which we usually put in our house. This is a global holiday calendar, where user can choose a country and see what are the coming holidays. This calendar provide below features, </font><br><font color='#FF0000'> => Country wise holiday list (2015-2016), <br> => Select a country to see holidays, <br> => User\'s selected country will be remembered, <br> => Record and save a voice note after long pressing (press and hold for 2-3 second) a day, <br>  => Play the voice note clicking on the day, <br>  => Add/Delete your own holiday or personal event. </font><br><font color='#3300FF'>While every effort has been made to ensure the accuracy of the dates we publish, dates of holidays do change from time to time.We therefore encourage you to cross-check your travel dates with other public holidays information sources before making flight/hotel/travel plans. Happy Holidays !!</font>";
		about_content.setText(Html.fromHtml(about_text));
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.about_ok:
			finish();
			break;
		case R.id.about_up:
			openUpInBrowser();
			break;
		case R.id.project_url:
			openProjectUrlInBrowser();
			break;
		}
	}
	private void openUpInBrowser()
	{
		Uri uri = Uri.parse(getString(R.string.up_url));
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
	private void openProjectUrlInBrowser()
	{
		Uri uri = Uri.parse(getString(R.string.about_project_url));
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
}
