package com.example.customizecalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.example.customizecalendar.DayEvent;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
	private ListView mEventList;
	private SimpleAdapter adapter;
	
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
		mEventList = (ListView) findViewById(R.id.eventList);
		
		// 預設的畫面
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
				gridClick(adapterView, view, position);
			}
		});
		
	}
	
	@Override
	public void onClick(View v) {
		// 新增event
		if (v == btn_AddEvent) {
			Log.i(TAG, "addEvent");
			showAddEventInterface();
			return;
		}
		
		//月份切換
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
	
	// 月份切換後,更新GridView
	private void updateGridView(int year, int month) {
		FillGridCell gridCell = new FillGridCell(year, month);
		NoteAdapter adapter = new NoteAdapter(this, 
		    gridCell.getGridList(), R.layout.gridcell, new String[]{"dayNum"},
		    new int[]{R.id.num});
		myCalendarView.setAdapter(adapter);
	}
	
	// 點選GridView上的日期
	private void gridClick(AdapterView adapter, View view, int position) {
		//TextView txtNote = (TextView)view.findViewById(R.id.num);
		HashMap<String, String> theMap = (HashMap<String, String>)adapter.getItemAtPosition(position);
		int year = Integer.valueOf(theMap.get("year"));
		int month = Integer.valueOf(theMap.get("month"));
		int day = Integer.valueOf(theMap.get("dayNum"));
		setDateView(year, month, day);
		
		DayEvent dm = new DayEvent(year, month, day);
    	Cursor cur = dm.queryTodayEvent(getContentResolver());
    	showDayEvents(cur);
	}
	
	// add events to ListView
	private void showDayEvents(Cursor cur) {
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		while (cur.moveToNext()) {
		    long eventID = cur.getLong(DayEvent.PROJ_ID_INDEX);
		    long beginVal = cur.getLong(DayEvent.PROJ_BEGIN_INDEX);
		    String title = cur.getString(DayEvent.PROJ_TITLE_INDEX);
		    
		    Calendar calendar = Calendar.getInstance();
		    calendar.setTimeInMillis(beginVal);
		    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");

		    HashMap<String,String> item = new HashMap<String,String>();
		    item.put("ID", String.valueOf(eventID)); // ID will not shown in ListView
		    item.put("Title", title);
		    item.put("startTime", formatter.format(calendar.getTime()));
		    list.add(item);
		}
		adapter = new SimpleAdapter(this, 
			list,
			android.R.layout.simple_list_item_2,
			new String[] { "Title","startTime" },
			new int[] {android.R.id.text1, android.R.id.text2});
				 
		//ListActivity設定adapter
		mEventList.setAdapter(adapter);
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
	
	// 跳到新增event窗口
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
			Long evenId = bundle.getLong("eventID");
			Log.i(TAG, "Add a new event. ID: " + evenId);
		}
	}
	
}
