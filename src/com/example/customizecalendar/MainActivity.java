package com.example.customizecalendar;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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

	public class NoteAdapter extends SimpleAdapter{
		Context context = null;
 
		/* 重設GridView的外觀
		 * 修改TextView的大小,增加點選範圍
		 * 設定TextView背景顏色,與GridView的background不同顏色產生格線效果
		 * */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 取得TextView
			convertView = LayoutInflater.from(context).inflate(R.layout.gridcell, null);
			TextView txtNote = (TextView)convertView.findViewById(R.id.num);
			
			HashMap<String, String> theMap = (HashMap<String, String>)getItem(position);
			setTextColor(txtNote, theMap.get("color").toString() , position);
			txtNote.setText(theMap.get("dayNum").toString());
			txtNote.setTextSize(24);			
			txtNote.setBackgroundColor(Color.WHITE);// textView背景白色
 
			/* 根据列数计算TextView宽度，以使总宽度尽量填充屏幕
			 * 會影響到background與點擊範圍
			 * 每格寬度 = (總寬度 - 格線數*線數間距) / 數
			 */
			int itemWidth = (int)(getResources().getDisplayMetrics().widthPixels -  6 * 2)  / 7;
			int itemHeight = itemWidth;
		    AbsListView.LayoutParams param = new AbsListView.LayoutParams(
		    		itemWidth,
		    		itemHeight);
		    txtNote.setLayoutParams(param);
 
		    return txtNote;
		}
		
		/* 設定TextView字體顏色 */
		private void setTextColor(TextView view, String color, int position) {
			Boolean hoilday = false;
			if ((position % 7 == 0) || (position % 7 == 6))
				hoilday = true;
			
			if (color.equals("Black")) {
				if (hoilday)
					view.setTextColor(getResources().getColor(R.color.red));
				else
					view.setTextColor(getResources().getColor(R.color.black));
			} else if (color.equals("Gray")) {
				view.setTextColor(getResources().getColor(R.color.lightgray));
			} else if (color.equals("Blue")) {
				view.setTextColor(getResources().getColor(R.color.blue));
			}
		}
		 
		public NoteAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			this.context = context;
		}
 
	}
}
