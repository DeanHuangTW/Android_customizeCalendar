package com.example.customizecalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import android.util.Log;

/* �o��class�O�Ψӳ]�m�n�e�bGridView�W�����
 * �ھڳ]�m���~�P��,�ӨM�w�C��n�񤰻�Ʀr */
public class FillGridCell {
	private String TAG = "Dean";
	
	private int mYear;
	private int mMonth;
	private static final int DAY_OFFSET = 1;	
	private int daysInMonth;
	
	/* �w�]���Ѫ��~,�� */
	public FillGridCell() {
		Calendar calendar = Calendar.getInstance();
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH);
	}
	/* �ۭq�~,�� */
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
		int trailingSpaces = 0;    //�ݭn��X���W�Ӥ몺���
		int daysInPrevMonth = 0;   //�W�Ӥ릳�X��
		int prevMonth = 0;         //�W�Ӥ�O�X��
		int prevYear = 0;
		int nextMonth = 0;
		int nextYear = 0;
		int currentMonth = mMonth; //�o�Ӥ�
		daysInMonth = getNumberOfDaysOfMonth(currentMonth);
		Log.d(TAG, "���릳" + daysInMonth + "��");
		//����Ĥ@�Ѫ����
		GregorianCalendar cal = new GregorianCalendar(mYear, mMonth, 1);
		
		//���o�W�Ӥ몺�ѼƸ�T
		if (currentMonth == 11) { //���p�{�b�O12��
			prevMonth = currentMonth - 1;
			prevYear = mYear;
			daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
			nextMonth = 0;
			nextYear = mYear + 1;
		} else if (currentMonth == 0) {  //���p�{�b�O1��
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
		//���o����@���O§���X(1�N��P����),�ھڳo�ӨM�w�n��X���W�Ӥ몺���
		int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
		trailingSpaces = currentWeekDay;
		Log.d(TAG, "����1��:" + currentWeekDay + " �O " + getWeekDayAsString(currentWeekDay));
		Log.d(TAG, "�ݭn�d" + trailingSpaces + "�ӪŮ浹�W�Ӥ�");
		Log.d(TAG, "�W�Ӥ몺�Ѽ�: " + daysInPrevMonth);

		//�|�~2��h�@��
		if (cal.isLeapYear(cal.get(Calendar.YEAR)))
			if (mMonth == 1)
				++daysInMonth;
			else if (mMonth == 2)
				++daysInPrevMonth;

		// list����W�Ӥ몺��ƨ�trailingSpaces
		for (int i = 0; i < trailingSpaces; i++) {
			HashMap<String, String> item = new HashMap<String ,String >() ;
			item.put("dayNum", String.valueOf(
					(daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i));
			item.put("year", String.valueOf(prevYear));
			item.put("month", String.valueOf(prevMonth));
			item.put("color", "Gray");
			list.add (item);
		}

		// ���U�ө񥻤���
		int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		for (int i = 1; i <= daysInMonth; i++) {
			HashMap<String, String> item = new HashMap<String ,String >() ;
			item.put("dayNum", String.valueOf(i));
			item.put("year", String.valueOf(mYear));
			item.put("month", String.valueOf(mMonth));
			if (i == today) {
				item.put("color", "Blue");  //���Ѫ��r��O�Ŧ�
			} else {
				item.put("color", "Black");  //����䥦�ѬO�¦�r��
			}
			list.add (item);
		}

		// �U�Ӥ���,�q1���}�l����l��
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
