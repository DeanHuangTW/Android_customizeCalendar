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
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
		int itemWidth = (int)(context.getResources().getDisplayMetrics().widthPixels -  6 * 2)  / 7;
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
