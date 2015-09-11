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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
		NoteAdapter adapter = new NoteAdapter(this, 
		    gridCell.getGridList(), R.layout.gridcell, new String[]{"dayNum"},
		    new int[]{R.id.num});
		myCalendarView.setAdapter(adapter);
		
		myCalendarView.setOnItemClickListener(new GridView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView adapterView,View view,int position,long id) {
				Log.i(TAG, "press position:" + position);
			}
		});
		
	}
	
	
	private Calendar getTodayCalendar() {
		Calendar calendar = Calendar.getInstance();
		mYearOfToday = calendar.get(Calendar.YEAR);
		mMonthOfToday = calendar.get(Calendar.MONTH);
		mDayOfToday = calendar.get(Calendar.DAY_OF_MONTH);
		
		Log.i(TAG, "今天日期是:" + mYearOfToday + "/" + (mMonthOfToday+1) + "/" + mDayOfToday);
		return calendar;
	}

	public class NoteAdapter extends SimpleAdapter{
		Context context = null;
 
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 重新設計TextView,然後回傳
			convertView = LayoutInflater.from(context).inflate(R.layout.gridcell, null);
			TextView txtNote = (TextView)convertView.findViewById(R.id.num);
			
			HashMap<String, String> theMap = (HashMap<String, String>)getItem(position);
			txtNote.setText(theMap.get("dayNum").toString());
			txtNote.setTextSize(24);  
			// textView背景白色,與GridView的background不同顏色產生格線效果
			txtNote.setBackgroundColor(Color.WHITE);
 
			/* 根据列数计算TextView宽度，以使总宽度尽量填充屏幕
			 * 會影響到background與點擊範圍
			 */
			int itemWidth = (int)(getResources().getDisplayMetrics().widthPixels -  7 * 10)  / 7;
			int itemHeight = itemWidth;
		    AbsListView.LayoutParams param = new AbsListView.LayoutParams(
		    		itemWidth,
		    		itemHeight);
		    txtNote.setLayoutParams(param);
 
		    return txtNote;
		}
 
		public NoteAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			this.context = context;
		}
 
	}
}
