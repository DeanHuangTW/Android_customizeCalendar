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
		int prevYear = 0;
		int nextMonth = 0;
		int nextYear = 0;
		int currentMonth = mMonth; //這個月
		daysInMonth = getNumberOfDaysOfMonth(currentMonth);
		Log.d(TAG, "本月有" + daysInMonth + "天");
		//本月第一天的月曆
		GregorianCalendar cal = new GregorianCalendar(mYear, mMonth, 1);
		
		//取得上個月的天數資訊
		if (currentMonth == 11) { //假如現在是12月
			prevMonth = currentMonth - 1;
			prevYear = mYear;
			daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
			nextMonth = 0;
			nextYear = mYear + 1;
		} else if (currentMonth == 0) {  //假如現在是1月
			prevMonth = 11;
			prevYear = mYear - 1;
			daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
			nextMonth = currentMonth + 1;
			nextYear = mYear;
		} else {
			prevMonth = currentMonth - 1;
			nextMonth = currentMonth + 1;
			nextYear = mYear;
			prevYear = mYear;
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
			if (mMonth == 1)
				++daysInMonth;
			else if (mMonth == 2)
				++daysInPrevMonth;

		// list先放上個月的資料到trailingSpaces
		for (int i = 0; i < trailingSpaces; i++) {
			HashMap<String, String> item = new HashMap<String ,String >() ;
			item.put("dayNum", String.valueOf(
					(daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i));
			item.put("year", String.valueOf(prevYear));
			item.put("month", String.valueOf(prevMonth));
			item.put("color", "Gray");
			list.add (item);
		}

		// 接下來放本月資料
		int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		for (int i = 1; i <= daysInMonth; i++) {
			HashMap<String, String> item = new HashMap<String ,String >() ;
			item.put("dayNum", String.valueOf(i));
			item.put("year", String.valueOf(mYear));
			item.put("month", String.valueOf(mMonth));
			if (i == today) {
				item.put("color", "Blue");  //今天的字體是藍色
			} else {
				item.put("color", "Black");  //本月其它天是黑色字體
			}
			list.add (item);
		}

		// 下個月資料,從1號開始放到格子滿
		for (int i = 0; i < list.size() % 7; i++) {
			HashMap<String, String> item = new HashMap<String ,String >() ;
			item.put("dayNum", String.valueOf(i + 1));
			item.put("year", String.valueOf(nextYear));
			item.put("month", String.valueOf(nextMonth));
			item.put("color", "Gray");
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
