package com.example.customizecalendar;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	private String TAG = "Dean";
	
	// 今天的年,月,日
	private int mYearOfToday;
	private int mMonthOfToday;
	private int mDayOfToday;
	
	private GridView myCalendarView;
	private TextView mWeekDays;
	private TextView prevMon;
	private TextView nextMon;
	private TextView dateView;
	private Button btn_AddEvent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btn_AddEvent = (Button) findViewById(R.id.btn_addEvent);
		btn_AddEvent.setOnClickListener(this);
		myCalendarView = (GridView) findViewById(R.id.myCalendar);
		mWeekDays = (TextView) findViewById(R.id.weekDay);
		mWeekDays.setText("Sun        Mon        Tue        Wed        Thu        Fri        Sat");
		prevMon = (TextView) findViewById(R.id.prevMonth);
		prevMon.setOnClickListener(this);
		nextMon = (TextView) findViewById(R.id.nextMonth);
		nextMon.setOnClickListener(this);
		dateView = (TextView) findViewById(R.id.date);
		
		Calendar calendar = getTodayCalendar();		
		
		FillGridCell gridCell = new FillGridCell();
		NoteAdapter adapter = new NoteAdapter(this, 
		    gridCell.getGridList(), R.layout.gridcell, new String[]{"dayNum"},
		    new int[]{R.id.num});
		myCalendarView.setAdapter(adapter);
		
		myCalendarView.setOnItemClickListener(new GridView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView adapterView,View view,int position,long id) {
				Log.i(TAG, "press position:" + position);
				gridClick(view, position);
			}
		});
		
	}
	
	@Override
	public void onClick(View v) {
		if (v == btn_AddEvent) {
			Log.i(TAG, "addEvent");
			showAddEventInterface();
			return;
		}
		if (v == prevMon) {
			Log.i(TAG, "Chnage to previous month");
			if (mMonthOfToday == 0) {
				mMonthOfToday = 11;
				mYearOfToday--;
			} else {
				mMonthOfToday--;
			}
		} else if (v == nextMon){
			Log.i(TAG, "Chnage to next month");
			if (mMonthOfToday == 11) {
				mMonthOfToday = 0;
				mYearOfToday++;
			} else {
				mMonthOfToday++;
			}
		}
		Log.i(TAG, "  ===>" + mYearOfToday + "-" + (mMonthOfToday+1));
		updateGridView(mYearOfToday, mMonthOfToday);
		setDateView(mYearOfToday, mMonthOfToday, mDayOfToday);
	}
	
	private void updateGridView(int year, int month) {
		FillGridCell gridCell = new FillGridCell(year, month);
		NoteAdapter adapter = new NoteAdapter(this, 
		    gridCell.getGridList(), R.layout.gridcell, new String[]{"dayNum"},
		    new int[]{R.id.num});
		myCalendarView.setAdapter(adapter);
	}
	
	private void gridClick(View view, int position) {
		TextView txtNote = (TextView)view.findViewById(R.id.num);
		
	}
	
	private Calendar getTodayCalendar() {
		Calendar calendar = Calendar.getInstance();
		mYearOfToday = calendar.get(Calendar.YEAR);
		mMonthOfToday = calendar.get(Calendar.MONTH);
		mDayOfToday = calendar.get(Calendar.DAY_OF_MONTH);
		
		Log.i(TAG, "今天日期是:" + mYearOfToday + "/" + (mMonthOfToday+1) + "/" + mDayOfToday);
		setDateView(mYearOfToday, mMonthOfToday, mDayOfToday);
		return calendar;
	}
	
	private void setDateView(int year, int month, int day) {
		dateView.setText(String.valueOf(year) + "-"
				+ String.valueOf(month + 1) + "-"
				+ String.valueOf(day));
	}
	
	private void showAddEventInterface() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, AddEvent.class);
		
		startActivityForResult(intent, 0);
	}
	
	/* 接收回傳的值 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
			Log.v(TAG, "Give up to add a new event");
		} else if (resultCode == RESULT_OK) {
			Bundle bundle = data.getBundleExtra("com.example.customizecalendar.AddEvent");
			String event_title = bundle.getString("title");
			
			Log.v(TAG, "Title " + event_title);
		}
	}
}
