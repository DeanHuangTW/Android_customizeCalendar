package com.example.customizecalendar;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class AddEvent extends Activity implements OnClickListener {
	
	private Button btnOK;
	private Button btnCancel;
	private EditText editTitle;
	private TextView start_date;
	private TextView start_time;
	private TextView end_date;
	private TextView end_time;
	
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
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.event_dialog);
		
		editTitle = (EditText) findViewById(R.id.title);		
		
		start_date = (TextView) findViewById(R.id.start_date);
		start_date.setOnClickListener(this);
		start_time = (TextView) findViewById(R.id.start_time);
		start_time.setOnClickListener(this);
		setStartDate();
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
			AddEvent.this.setResult(RESULT_CANCELED);
			AddEvent.this.finish();
			return;
		}
		
		// TextView click event
		if (v == start_date) {
			new DatePickerDialog(this,
					callback_StartDatePick,
					start_year,
					start_month,
					start_day).show();
		} else if (v == start_time) {
			// 執行TimePick
			new TimePickerDialog(this,
					callback_timePick,  // callback
                    start_hour,  		// default time
                    start_minute,
                    true).show();  		// 24 hour                    
		}
	}
	
	// TimePick選擇好時間後會執行此callback
	TimePickerDialog.OnTimeSetListener callback_timePick = new TimePickerDialog.OnTimeSetListener() {
	    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	    	start_hour = hourOfDay;
	    	start_minute = minute;
	    	start_time.setText(start_hour + ":" + start_minute);
	    }
	  };  

	DatePickerDialog.OnDateSetListener callback_StartDatePick = new DatePickerDialog.OnDateSetListener() {
		  public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			  start_year= year;
			  start_month = monthOfYear;
			  start_day = dayOfMonth;
			  start_date.setText(start_year + "/" + (start_month+1) + "/" + start_day);
		  }
	};  
		  
	public void saveAndBack() {
		Intent intent = getIntent();
		Bundle bundle = new Bundle();
		String title = editTitle.getText().toString();
		bundle.putString("title", title);
		
		String[] start = { String.valueOf(start_year), 
				String.valueOf(start_month), 
				String.valueOf(start_day),
				String.valueOf(start_hour), 
				String.valueOf(start_minute)};
		bundle.putStringArray("startTime", start);
		
		
		intent.putExtra("com.example.customizecalendar.AddEvent", bundle);
		AddEvent.this.setResult(RESULT_OK, intent);
		AddEvent.this.finish();
	}
	
	private void setStartDate() {
		Calendar calendar = Calendar.getInstance();
		start_year = calendar.get(Calendar.YEAR);
		start_month = calendar.get(Calendar.MONTH);
		start_day = calendar.get(Calendar.DAY_OF_MONTH);	
		start_date.setText(start_year + "/" + (start_month+1) + "/" + start_day);
		
		start_hour = calendar.get(Calendar.HOUR_OF_DAY);
		start_minute = calendar.get(Calendar.MINUTE);
		start_time.setText(start_hour + ":" + start_minute);
	}
	
	private void setEndDate() {
		end_year = start_year;
		end_month = start_month;
		end_day = start_day;	
		end_date.setText(start_year + "/" + (start_month+1) + "/" + start_day);
		
		end_hour = start_hour + 1 ;
		end_minute = start_minute;
		end_time.setText(start_hour + ":" + start_minute);
	}
}
