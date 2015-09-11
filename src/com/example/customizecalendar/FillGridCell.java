package com.example.customizecalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import android.util.Log;

/* 這個class是用來設置要畫在GridView上的日期
 * 根據設置的年與月,來決定每格要填什麼數字 */
public class FillGridCell {
	private String TAG = "Dean";
	
	private int mYear;
	private int mMonth;
	private static final int DAY_OFFSET = 1;	
	private int daysInMonth;
	
	/* 預設今天的年,月 */
	public FillGridCell() {
		Calendar calendar = Calendar.getInstance();
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH);
	}
	/* 自訂年,月 */
	public FillGridCell(int year, int month) {
		this.mYear = year;
		this.mMonth = month;
	}
	
	public void setDate(int year, int month) {
		this.mYear = year;
		this.mMonth = month;
	}
	
	public ArrayList<HashMap<String, String>> getGridList() {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap <String ,String>>();
		int trailingSpaces = 0;    //需要填幾筆上個月的資料
		int daysInPrevMonth = 0;   //上個月有幾天
		int prevMonth = 0;         //上個月是幾月
		int currentMonth = mMonth; //這個月
		daysInMonth = getNumberOfDaysOfMonth(currentMonth);
		Log.d(TAG, "本月有" + daysInMonth + "天");
		//本月第一天的月曆
		GregorianCalendar cal = new GregorianCalendar(mYear, mMonth, 1);
		
		//取得上個月的天數資訊
		if (currentMonth == 11) { //假如現在是12月
			prevMonth = currentMonth - 1;
			daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
		} else if (currentMonth == 0) {  //假如現在是1月
			prevMonth = 11;
			daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
		} else {
			prevMonth = currentMonth - 1;
			daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
		}
		//取得本月一號是禮拜幾(1代表星期日),根據這個決定要填幾筆上個月的資料
		int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
		trailingSpaces = currentWeekDay;
		Log.d(TAG, "本月1號:" + currentWeekDay + " 是 " + getWeekDayAsString(currentWeekDay));
		Log.d(TAG, "需要留" + trailingSpaces + "個空格給上個月");
		Log.d(TAG, "上個月的天數: " + daysInPrevMonth);

		//閏年2月多一天
		if (cal.isLeapYear(cal.get(Calendar.YEAR)))
			if (mMonth == 2)
				++daysInMonth;
			else if (mMonth == 3)
				++daysInPrevMonth;

		// list先放上個月的資料到trailingSpaces
		for (int i = 0; i < trailingSpaces; i++) {
			HashMap<String, String> item = new HashMap<String ,String >() ;
			item.put("dayNum", String.valueOf(
					(daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i));
			list.add (item);
		}

		// 接下來放本月資料
		for (int i = 1; i <= daysInMonth; i++) {
			HashMap<String, String> item = new HashMap<String ,String >() ;
			item.put("dayNum", String.valueOf(i));
			list.add (item);
		}

		// 下個月資料,從1號開始放到格子滿
		for (int i = 0; i < list.size() % 7; i++) {
			HashMap<String, String> item = new HashMap<String ,String >() ;
			item.put("dayNum", String.valueOf(i + 1));
			list.add (item);
		}
		
		return list;
	}
	
	private int getNumberOfDaysOfMonth(int i) {
		int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
				31, 30, 31 };
		return daysOfMonth[i];
	}
	
	private String getMonthAsString(int i) {
		String[] months = { "January", "February", "March",
				"April", "May", "June", "July", "August", "September",
				"October", "November", "December" };
		return months[i];
	}

	private String getWeekDayAsString(int i) {
		String[] weekdays = new String[] { "Sun", "Mon", "Tue",
				"Wed", "Thu", "Fri", "Sat" };
		return weekdays[i];
	}
	
}
