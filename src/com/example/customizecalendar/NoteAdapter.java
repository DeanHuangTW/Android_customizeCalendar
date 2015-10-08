package com.example.customizecalendar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class NoteAdapter extends SimpleAdapter{
	Context context = null;

	/* 重設GridView的外觀
	 * 設定TextView大小,背景顏色,與GridView的background不同顏色產生格線效果
	 * 設定layout大小增加點選範圍
	 * */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 取得TextView
		convertView = LayoutInflater.from(context).inflate(R.layout.gridcell, null);
		// 設置日期格式
		TextView txtNote = (TextView)convertView.findViewById(R.id.num);		
		HashMap<String, String> theMap = (HashMap<String, String>)getItem(position);
		setTextColor(txtNote, theMap.get("color").toString() , position);
		txtNote.setText(theMap.get("dayNum").toString());
		txtNote.setTextSize(20);			
		txtNote.setBackgroundColor(Color.WHITE);// textView背景白色
		//設置Event圖示
		TextView icon_event = (TextView)convertView.findViewById(R.id.icon_event);
		setEventIcon(icon_event, theMap);

		/* 根据列数计算宽度，以使总宽度尽量填充屏幕
		 * 會影響到GridView點擊範圍
		 * 每格寬度 = (總寬度 - 格線數*線數間距) / 數
		 */
		int itemWidth = (int)(context.getResources().getDisplayMetrics().widthPixels -  6 * 2)  / 7;
		int itemHeight = itemWidth;
	    AbsListView.LayoutParams param = new AbsListView.LayoutParams(
	    		itemWidth,
	    		itemHeight);
	    LinearLayout ll = (LinearLayout)convertView.findViewById(R.id.grid_fill);
	    ll.setLayoutParams(param);   
	    
	    return ll;
	}
	
	// 月曆上如果今天有Event顯示'E'
	private void setEventIcon(TextView view, HashMap<String, String> theMap) {
		view.setTextSize(20);
		view.setTextColor(Color.BLUE);
		view.setBackgroundColor(Color.WHITE);
		
		//搜尋今天是否有event
		int year = Integer.valueOf(theMap.get("year"));
		int month = Integer.valueOf(theMap.get("month"));
		int day = Integer.valueOf(theMap.get("dayNum"));
		DayEvent checkDate = new DayEvent(year, month, day);
		if (checkDate.hasEventToday(context.getContentResolver())) {
			view.setText("E");
		}
	}
	
	/* 設定TextView字體顏色 */
	private void setTextColor(TextView view, String color, int position) {
		Boolean hoilday = false;
		if ((position % 7 == 0) || (position % 7 == 6))
			hoilday = true;
		
		if (color.equals("Black")) {
			if (hoilday)
				view.setTextColor(context.getResources().getColor(R.color.red));
			else
				view.setTextColor(context.getResources().getColor(R.color.black));
		} else if (color.equals("Gray")) {
			view.setTextColor(context.getResources().getColor(R.color.lightgray));
		} else if (color.equals("Blue")) {
			view.setTextColor(context.getResources().getColor(R.color.blue));
		}
	}
	 
	public NoteAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource,
			String[] from, int[] to) {
		super(context, data, resource, from, to);
		this.context = context;
	}

}
