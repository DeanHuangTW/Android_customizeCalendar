package com.example.customizecalendar;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;


public class ModifyEventActivity extends Activity implements OnClickListener {
	static String EXTRA_MODIFY_EVENT = "com.example.customizecalendar.ModifyEventActivity";
	
	private int mEventId;
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
	
	private Button mBtnModify;
	private Button mBtnCancel;
	private Button mBtnDelete;
	private EditText mEditTitle;
	private EditText mEditDesc;
	private TextView mTextStartDate;
	private TextView mTextStartTime;
	private TextView mTextEndDate;
	private TextView mTextEndTime;
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_modify_event);
		
		mEditTitle = (EditText) findViewById(R.id.title);
		mEditDesc = (EditText) findViewById(R.id.edit_desc);
		
		mTextStartDate = (TextView) findViewById(R.id.start_date);
		mTextStartDate.setOnClickListener(this);
		mTextStartTime = (TextView) findViewById(R.id.start_time);
		mTextStartTime.setOnClickListener(this);
		mTextEndDate = (TextView) findViewById(R.id.end_date);
		mTextEndDate.setOnClickListener(this);
		mTextEndTime = (TextView) findViewById(R.id.end_time);
		mTextEndTime.setOnClickListener(this);
		
		mBtnModify = (Button) findViewById(R.id.button_modify);
		mBtnModify.setOnClickListener(this);
		mBtnDelete = (Button) findViewById(R.id.button_delete);
		mBtnDelete.setOnClickListener(this);
		mBtnCancel = (Button) findViewById(R.id.btn_cancel);
		mBtnCancel.setOnClickListener(this);
		
		Bundle bundle = getIntent().getExtras();
		loadDataFromIntent(bundle);
	}
	
	@Override
	public void onClick(View v) {
		if (v == mBtnCancel) {
			ModifyEventActivity.this.setResult(RESULT_CANCELED);
			ModifyEventActivity.this.finish();
			return;
		} else if (v == mBtnModify) {
			saveData();
			return;
		} else if (v == mBtnDelete) {
			deleteEvent();
		}
		
		// TextView click event
		if (v == mTextStartDate) {
			new DatePickerDialog(this,
					callback_startDatePick,
					mStartYear,
					mStartMonth,
					mStartDay).show();
		} else if (v == mTextStartTime) {
			// 執行TimePick
			new TimePickerDialog(this,
					callback_startTimePick,  // callback
                    mStartHour,  		// default time
                    mStartMinute,
                    true).show();  		// 24 hour                    
		} else if (v == mTextEndDate) {
			new DatePickerDialog(this,
					callback_endDatePick,
					mEndYear,
					mEndMonth,
					mEndDay).show();
		} else if (v == mTextEndTime) {
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
	    	setTextTime(mTextStartTime, mStartHour, mStartMinute);
	    }
	  };  

	DatePickerDialog.OnDateSetListener callback_startDatePick = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mStartYear= year;
			mStartMonth = monthOfYear;
			mStartDay = dayOfMonth;
			setTextDate(mTextStartDate, mStartYear, mStartMonth, mStartDay);
		}
	};  
	
	TimePickerDialog.OnTimeSetListener callback_endTimePick = new TimePickerDialog.OnTimeSetListener() {
	    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	    	mEndHour = hourOfDay;
	    	mEndMinute = minute;
	    	setTextTime(mTextEndTime, mEndHour, mEndMinute);
	    }
	  };  

	DatePickerDialog.OnDateSetListener callback_endDatePick = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mEndYear= year;
			mEndMonth = monthOfYear;
			mEndDay = dayOfMonth;
			setTextDate(mTextEndDate, mEndYear, mEndMonth, mEndDay);
		}
	};  
	
	private void deleteEvent() {
		ContentResolver cr = getContentResolver();
		Uri deleteUri = null;
		deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, mEventId);
		cr.delete(deleteUri, null, null);
		
		// 更新結束,回主頁面
		Intent intent = getIntent();
		Bundle bundle = new Bundle();		
		intent.putExtra(EXTRA_MODIFY_EVENT, bundle);
		ModifyEventActivity.this.setResult(RESULT_OK, intent);
		ModifyEventActivity.this.finish();
	}
	
	private void saveData() {
		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		Uri updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, mEventId);
		long startMillis = 0; 
		long endMillis = 0;
		//event開始時間
		Calendar beginTime = Calendar.getInstance();
		beginTime.set(mStartYear, mStartMonth, mStartDay, mStartHour, mStartMinute);
		startMillis = beginTime.getTimeInMillis();
		values.put(Events.DTSTART, startMillis);
		//event結束時間
		Calendar endTime = Calendar.getInstance();
		endTime.set(mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute);
		endMillis = endTime.getTimeInMillis();		
		values.put(Events.DTEND, endMillis);
		// title, description
		values.put(Events.TITLE, mEditTitle.getText().toString()); 
		values.put(Events.DESCRIPTION, mEditDesc.getText().toString()); 
		
		cr.update(updateUri, values, null, null);		
		
		// 更新結束,回主頁面
		Intent intent = getIntent();
		Bundle bundle = new Bundle();		
		intent.putExtra(EXTRA_MODIFY_EVENT, bundle);
		ModifyEventActivity.this.setResult(RESULT_OK, intent);
		ModifyEventActivity.this.finish();
	}
	
	private void loadDataFromIntent(Bundle bundle) {
		/* 讀取資料 */
		Long beginTimeInMills = bundle.getLong("beginTime");
		Long endTimeInMills = bundle.getLong("endTime");
		String eventTitle = bundle.getString("title");
		String eventDesc = bundle.getString("desc");
		mEventId = bundle.getInt("eventID");
		
		/* 設定資料 */
		//start time
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(beginTimeInMills);
		mStartYear = cal.get(Calendar.YEAR);
		mStartMonth = cal.get(Calendar.MONTH);
		mStartDay = cal.get(Calendar.DATE);
		mStartHour = cal.get(Calendar.HOUR_OF_DAY);
		mStartMinute = cal.get(Calendar.MINUTE);
		setTextDate(mTextStartDate, mStartYear, mStartMonth, mStartDay);
		setTextTime(mTextStartTime, mStartHour, mStartMinute);
		// end time
		cal.setTimeInMillis(endTimeInMills);
		mEndYear = cal.get(Calendar.YEAR);
		mEndMonth = cal.get(Calendar.MONTH);
		mEndDay = cal.get(Calendar.DATE);
		mEndHour = cal.get(Calendar.HOUR_OF_DAY);
		mEndMinute = cal.get(Calendar.MINUTE);
		setTextDate(mTextEndDate, mEndYear, mEndMonth, mEndDay);
		setTextTime(mTextEndTime, mEndHour, mEndMinute);
		// title and description
		mEditTitle.setText(eventTitle);
		mEditDesc.setText(eventDesc);
	}
	
	private void setTextDate(TextView view, int year, int month, int day) {
		view.setText(String.valueOf(year) + "/" +
				String.valueOf(month+1) + "/" +
				String.valueOf(day));
	}
	
	private void setTextTime(TextView view, int hour, int minute) {
		view.setText(String.valueOf(hour) + ":" +
				String.valueOf(minute));
	}
}
