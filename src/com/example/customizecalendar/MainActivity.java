package com.example.customizecalendar;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {
	private String TAG = "Dean";
	
	// 今天的年,月,日
	private int mYearOfToday;
	private int mMonthOfToday;
	private int mDayOfToday;
	
	private GridView myCalendarView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		myCalendarView = (GridView) findViewById(R.id.myCalendar);
		
		Calendar calendar = getTodayCalendar();			
		
		FillGridCell gridCell = new FillGridCell();
		SimpleAdapter adapter = new SimpleAdapter(this, 
		    gridCell.getGridList(), R.layout.gridcell, new String[]{"dayNum"},
		    new int[]{R.id.num});
		 
		
		myCalendarView.setAdapter(adapter);
		
	}
	
	
	private Calendar getTodayCalendar() {
		Calendar calendar = Calendar.getInstance();
		mYearOfToday = calendar.get(Calendar.YEAR);
		mMonthOfToday = calendar.get(Calendar.MONTH);
		mDayOfToday = calendar.get(Calendar.DAY_OF_MONTH);
		
		Log.i(TAG, "今天日期是:" + mYearOfToday + "/" + (mMonthOfToday+1) + "/" + mDayOfToday);
		return calendar;
	}

}
