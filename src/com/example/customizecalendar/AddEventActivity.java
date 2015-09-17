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
	
	public static final String[] EVENT_PROJECTION = new String [] {
         Calendars._ID,                           // 0
         Calendars.CALENDAR_DISPLAY_NAME,         // 1
    };
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 1;
	
	private int[] alarmTimeList = {0, 60, 60*5, 60*10};
	private int alarmTime = 0;
	
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
	
	// 紀錄事件開始與結束時間
	private int start_year;
	private int start_month;
	private int start_day;
	private int start_hour;
	private int start_minute;
	private int end_year;
	private int end_month;
	private int end_day;
	private int end_hour;
	private int end_minute;
	
	private int calendarID;
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_add_event);
		
		editTitle = (EditText) findViewById(R.id.title);
		editDesc = (EditText) findViewById(R.id.edit_desc);
		calendarName = (Spinner) findViewById(R.id.calendar_name);	
		findCalendarList();
		
		spinner_alarm = (Spinner) findViewById(R.id.spinner_alarm);
		spinner_alarm.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView adapterView, View view, int position, long id){
            	alarmTime = alarmTimeList[position];
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
					start_year,
					start_month,
					start_day).show();
		} else if (v == start_time) {
			// 執行TimePick
			new TimePickerDialog(this,
					callback_startTimePick,  // callback
                    start_hour,  		// default time
                    start_minute,
                    true).show();  		// 24 hour                    
		} else if (v == end_date) {
			new DatePickerDialog(this,
					callback_endDatePick,
					end_year,
					end_month,
					end_day).show();
		} else if (v == end_time) {
			new TimePickerDialog(this,
					callback_endTimePick,
					end_hour,  		
					end_minute,
                    true).show();  
		}
	}
	
	// TimePick選擇好時間後會執行此callback
	TimePickerDialog.OnTimeSetListener callback_startTimePick = new TimePickerDialog.OnTimeSetListener() {
	    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	    	start_hour = hourOfDay;
	    	start_minute = minute;
	    	start_time.setText(start_hour + ":" + start_minute);
	    }
	  };  

	DatePickerDialog.OnDateSetListener callback_startDatePick = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			start_year= year;
			start_month = monthOfYear;
			start_day = dayOfMonth;
			start_date.setText(start_year + "/" + (start_month+1) + "/" + start_day);
		}
	};  
	
	TimePickerDialog.OnTimeSetListener callback_endTimePick = new TimePickerDialog.OnTimeSetListener() {
	    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	    	end_hour = hourOfDay;
	    	end_minute = minute;
	    	end_time.setText(end_hour + ":" + end_minute);
	    }
	  };  

	DatePickerDialog.OnDateSetListener callback_endDatePick = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			end_year= year;
			end_month = monthOfYear;
			end_day = dayOfMonth;
			end_date.setText(end_year + "/" + (end_month+1) + "/" + end_day);
		}
	};  
		  
	public void saveAndBack() {
		long eventID = addNewEvent();
		Intent intent = getIntent();
		Bundle bundle = new Bundle();
		bundle.putLong("eventID", eventID);
		
		intent.putExtra("com.example.customizecalendar.AddEvent", bundle);
		AddEventActivity.this.setResult(RESULT_OK, intent);
		AddEventActivity.this.finish();
	}
	
	private long addNewEvent() {
		long startMillis = 0; 
		long endMillis = 0;     
		Calendar beginTime = Calendar.getInstance();
		beginTime.set(start_year, start_month, start_day, start_hour, start_minute);
		startMillis = beginTime.getTimeInMillis();
		Calendar endTime = Calendar.getInstance();
		endTime.set(end_year, end_month, end_day, end_hour, end_minute);
		endMillis = endTime.getTimeInMillis();
			
		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		values.put(Events.DTSTART, startMillis);
		values.put(Events.DTEND, endMillis);
		values.put(Events.TITLE, editTitle.getText().toString());
		values.put(Events.DESCRIPTION, editDesc.getText().toString());
		values.put(Events.EVENT_TIMEZONE, "Taiwan");
		values.put(Events.CALENDAR_ID, calendarID);
		
		Uri uri = cr.insert(Events.CONTENT_URI, values);		
		long eventID = Long.parseLong(uri.getLastPathSegment());
		//鬧鐘設置
		setAlarm(startMillis, eventID);
		
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
		    
		    calendarID = (int) cur.getLong(PROJECTION_ID_INDEX) ;
		    displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);		    
		    mStr.add(displayName);
		    //TODO: 這裡應該再加一段處理calendar ID的code,讓User可以選擇event加到哪個calendar
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this,
        		android.R.layout.simple_spinner_item, mStr);
        calendarName.setAdapter(adapter);
	}
	
	// 設置鬧鐘, eventID拿來當鬧鐘的unique ID
	private void setAlarm(long startTime, long eventID) {
		Intent intent = new Intent(this, AlarmReceiver.class);
		intent.setData(Uri.parse("custom://customizeCalendar/" + eventID));
		intent.setAction(String.valueOf(eventID));
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
		
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, startTime - (alarmTime * 1000), pendingIntent);
		
	}
	
	private void cancelAlarm(long eventID) {
		Intent intent = new Intent(this, AlarmReceiver.class);
		intent.setData(Uri.parse("custom://customizeCalendar/" + eventID));
		intent.setAction(String.valueOf(eventID));
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
		
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(pendingIntent);
	}
	
	/* 鬧鐘時間選項 */
	private void setAlarmTimeList() {
		String[] mTimeList = {"不提醒", "1分鐘前", "5分鐘前", "10分鐘前"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String> (this,
        		android.R.layout.simple_spinner_item, mTimeList);
        spinner_alarm.setAdapter(adapter);
	}
	
	private void setStartDate(Bundle bundle) {		
		start_year = bundle.getInt("year");
		start_month = bundle.getInt("month");
		start_day = bundle.getInt("day");
		start_date.setText(start_year + "/" + (start_month+1) + "/" + start_day);
		
		Calendar calendar = Calendar.getInstance();
		start_hour = calendar.get(Calendar.HOUR_OF_DAY);
		start_minute = calendar.get(Calendar.MINUTE);
		start_time.setText(start_hour + ":" + start_minute);
	}
	
	private void setEndDate() {
		end_year = start_year;
		end_month = start_month;
		end_day = start_day;	
		end_date.setText(end_year + "/" + (end_month+1) + "/" + end_day);
		
		Calendar calendar = Calendar.getInstance();
		end_hour = start_hour + 1 ; // 預設多1小時
		end_minute = start_minute;
		end_time.setText(end_hour + ":" + end_minute);
	}
}
