package com.apps.calendar;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.widget.PopupWindow;

public class MyPopupWindow extends PopupWindow {
	CalendarView view;

	MyPopupWindow(Context context) {
		super(context);
	}

	MyPopupWindow(Context context, AttributeSet attrSet) {
		super(context, attrSet);
	}

	MyPopupWindow(Context context, CalendarView view) {
		super(context);
		this.view = view;
	}

	@Override
	public void dismiss() {
		if(view != null)
		{
			try {
				if (view.mRecorder != null) {
					view.mRecorder.stop();
					view.mRecorder.release();
					String file = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/HolidaCal/" + view.fName + ".3gp";
					view.addRecordingToMediaLibrary(file);
					view.mRecorder = null;
				}
			} catch (Exception e) {
			}
			view.stopPlaying();
		}
		super.dismiss();
	}		
}
