package com.example.customizecalendar;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class AddEventActivity extends Activity implements OnClickListener {
	
	static String EXTRA_ADD_EVENT = "com.example.customizecalendar.AddEvent";
	
	public static final String[] EVENT_PROJECTION = new String [] {
         Calendars._ID,                           // 0
         Calendars.CALENDAR_DISPLAY_NAME,         // 1
    };
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 1;
	
	private String[] mAlarmTimeWayList = {"toast", "vibrator", "sound"};
	private String mAlarmTimeWay = "toast";
	private int[] mAlarmTimeList = {0, 1, 5, 10};
	private int mAlarmTime = 0;
	
	private Button btnOK;
	private Button btnCancel;
	private EditText editTitle;
	private EditText editDesc;
	private TextView start_date;
	private TextView start_time;
	private TextView end_date;
	private TextView end_time;
	private Spinner calendarName;
	private Spinner spinner_alarm;
	private Spinner mSpinnerAlarmWay;
	
	// 紀錄事件開始與結束時間
	private int mStartYear;
	private int mStartMonth;
	private int mStartDay;
	private int mStartHour;
	private int mStartMinute;
	private int mEndYear;
	private int mEndMonth;
	private int mEndDay;
	private int mEndHour;
	private int mEndMinute;
	
	private int mCalendarId;
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_add_event);
		
		editTitle = (EditText) findViewById(R.id.title);
		editDesc = (EditText) findViewById(R.id.edit_desc);
		calendarName = (Spinner) findViewById(R.id.calendar_name);	
		findCalendarList();
		
		// alarm提醒方式
		mSpinnerAlarmWay= (Spinner) findViewById(R.id.spinner_alarm_way);
		mSpinnerAlarmWay.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView adapterView, View view, int position, long id){
            	mAlarmTimeWay = mAlarmTimeWayList[position];
            }
            public void onNothingSelected(AdapterView arg0) {                
            }
		});
		setAlarmWayList();
		// alarm提醒時間
		spinner_alarm = (Spinner) findViewById(R.id.spinner_alarm);
		spinner_alarm.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView adapterView, View view, int position, long id){
            	mAlarmTime = mAlarmTimeList[position];
            }
            public void onNothingSelected(AdapterView arg0) {                
            }
		});
		setAlarmTimeList();
		
		Bundle bundle = getIntent().getExtras();
		start_date = (TextView) findViewById(R.id.start_date);
		start_date.setOnClickListener(this);
		start_time = (TextView) findViewById(R.id.start_time);
		start_time.setOnClickListener(this);
		setStartDate(bundle);
		end_date = (TextView) findViewById(R.id.end_date);
		end_date.setOnClickListener(this);
		end_time = (TextView) findViewById(R.id.end_time);
		end_time.setOnClickListener(this);
		setEndDate();
		
		btnOK = (Button) findViewById(R.id.btn_ok);
		btnOK.setOnClickListener(this);
		
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// button click event
		if (v == btnOK) {
			saveAndBack();
			return;
		} else if (v == btnCancel) {
			AddEventActivity.this.setResult(RESULT_CANCELED);
			AddEventActivity.this.finish();
			return;
		}
		
		// TextView click event
		if (v == start_date) {
			new DatePickerDialog(this,
					callback_startDatePick,
					mStartYear,
					mStartMonth,
					mStartDay).show();
		} else if (v == start_time) {
			// 執行TimePick
			new TimePickerDialog(this,
					callback_startTimePick,  // callback
                    mStartHour,  		// default time
                    mStartMinute,
                    true).show();  		// 24 hour                    
		} else if (v == end_date) {
			new DatePickerDialog(this,
					callback_endDatePick,
					mEndYear,
					mEndMonth,
					mEndDay).show();
		} else if (v == end_time) {
			new TimePickerDialog(this,
					callback_endTimePick,
					mEndHour,  		
					mEndMinute,
                    true).show();  
		}
	}
	
	// TimePick選擇好時間後會執行此callback
	TimePickerDialog.OnTimeSetListener callback_startTimePick = new TimePickerDialog.OnTimeSetListener() {
	    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	    	mStartHour = hourOfDay;
	    	mStartMinute = minute;
	    	start_time.setText(mStartHour + ":" + mStartMinute);
	    }
	  };  

	DatePickerDialog.OnDateSetListener callback_startDatePick = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mStartYear= year;
			mStartMonth = monthOfYear;
			mStartDay = dayOfMonth;
			start_date.setText(mStartYear + "/" + (mStartMonth+1) + "/" + mStartDay);
		}
	};  
	
	TimePickerDialog.OnTimeSetListener callback_endTimePick = new TimePickerDialog.OnTimeSetListener() {
	    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	    	mEndHour = hourOfDay;
	    	mEndMinute = minute;
	    	end_time.setText(mEndHour + ":" + mEndMinute);
	    }
	  };  

	DatePickerDialog.OnDateSetListener callback_endDatePick = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mEndYear= year;
			mEndMonth = monthOfYear;
			mEndDay = dayOfMonth;
			end_date.setText(mEndYear + "/" + (mEndMonth+1) + "/" + mEndDay);
		}
	};  
		  
	public void saveAndBack() {
		long eventID = addNewEvent();
		Intent intent = getIntent();
		Bundle bundle = new Bundle();
		bundle.putLong("eventID", eventID);
		
		intent.putExtra(EXTRA_ADD_EVENT, bundle);
		AddEventActivity.this.setResult(RESULT_OK, intent);
		AddEventActivity.this.finish();
	}
	
	private long addNewEvent() {
		long startMillis = 0; 
		long endMillis = 0;     
		Calendar beginTime = Calendar.getInstance();
		beginTime.set(mStartYear, mStartMonth, mStartDay, mStartHour, mStartMinute);
		startMillis = beginTime.getTimeInMillis();
		Calendar endTime = Calendar.getInstance();
		endTime.set(mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute);
		endMillis = endTime.getTimeInMillis();
			
		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		values.put(Events.DTSTART, startMillis);
		values.put(Events.DTEND, endMillis);
		values.put(Events.TITLE, editTitle.getText().toString());
		values.put(Events.DESCRIPTION, editDesc.getText().toString());
		values.put(Events.EVENT_TIMEZONE, "Taiwan");
		values.put(Events.CALENDAR_ID, mCalendarId);
		
		Uri uri = cr.insert(Events.CONTENT_URI, values);		
		long eventID = Long.parseLong(uri.getLastPathSegment());
		//鬧鐘設置
		setAlarm(eventID);
		
		return eventID;
	}
	
	/* 月曆列表 */
	private void findCalendarList() {
		Cursor cur = null;
        ContentResolver cr = getContentResolver() ;
        Uri uri = Calendars.CONTENT_URI;
        String selection = "(" + Calendars.ACCOUNT_NAME + " = ?)";
        String[] selectionArgs = {"account_name_local"}; //只用本地月曆
        cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
        
        ArrayList<String> mStr = new ArrayList<String>();
        while (cur.moveToNext()) {
		    String displayName = null;
		    
		    mCalendarId = (int) cur.getLong(PROJECTION_ID_INDEX) ;
		    displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);		    
		    mStr.add(displayName);
		    //TODO: 這裡應該再加一段處理calendar ID的code,讓User可以選擇event加到哪個calendar
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this,
        		android.R.layout.simple_spinner_item, mStr);
        calendarName.setAdapter(adapter);
	}
	
	// 設置鬧鐘, eventID拿來當鬧鐘的unique ID
	private void setAlarm(long eventID) {
		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		
		values.put(Reminders.MINUTES, mAlarmTime);  // 事件發生前多久提醒 (分)
		values.put(Reminders.EVENT_ID, eventID);
		values.put(Reminders.METHOD, Reminders.METHOD_ALERT);  //提醒方式
		cr.insert(Reminders.CONTENT_URI, values);
	}
	
	
	/* 鬧鐘時間選項 */
	private void setAlarmTimeList() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.reminder_time,
				android.R.layout.simple_spinner_item);
        spinner_alarm.setAdapter(adapter);
	}
	
	private void setAlarmWayList() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.reminder_way,
				android.R.layout.simple_spinner_item);
		mSpinnerAlarmWay.setAdapter(adapter);
	}
	
	private void setStartDate(Bundle bundle) {		
		mStartYear = bundle.getInt("year");
		mStartMonth = bundle.getInt("month");
		mStartDay = bundle.getInt("day");
		start_date.setText(mStartYear + "/" + (mStartMonth+1) + "/" + mStartDay);
		
		Calendar calendar = Calendar.getInstance();
		mStartHour = calendar.get(Calendar.HOUR_OF_DAY);
		mStartMinute = calendar.get(Calendar.MINUTE);
		start_time.setText(mStartHour + ":" + mStartMinute);
	}
	
	private void setEndDate() {
		mEndYear = mStartYear;
		mEndMonth = mStartMonth;
		mEndDay = mStartDay;	
		end_date.setText(mEndYear + "/" + (mEndMonth+1) + "/" + mEndDay);
		
		Calendar calendar = Calendar.getInstance();
		mEndHour = mStartHour + 1 ; // 預設多1小時
		mEndMinute = mStartMinute;
		end_time.setText(mEndHour + ":" + mEndMinute);
	}
}
