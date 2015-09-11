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
		int currentMonth = mMonth; //�o�Ӥ�
		daysInMonth = getNumberOfDaysOfMonth(currentMonth);
		Log.d(TAG, "���릳" + daysInMonth + "��");
		//����Ĥ@�Ѫ����
		GregorianCalendar cal = new GregorianCalendar(mYear, mMonth, 1);
		
		//���o�W�Ӥ몺�ѼƸ�T
		if (currentMonth == 11) { //���p�{�b�O12��
			prevMonth = currentMonth - 1;
			daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
		} else if (currentMonth == 0) {  //���p�{�b�O1��
			prevMonth = 11;
			daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
		} else {
			prevMonth = currentMonth - 1;
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
			if (mMonth == 2)
				++daysInMonth;
			else if (mMonth == 3)
				++daysInPrevMonth;

		// list����W�Ӥ몺��ƨ�trailingSpaces
		for (int i = 0; i < trailingSpaces; i++) {
			HashMap<String, String> item = new HashMap<String ,String >() ;
			item.put("dayNum", String.valueOf(
					(daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i));
			list.add (item);
		}

		// ���U�ө񥻤���
		for (int i = 1; i <= daysInMonth; i++) {
			HashMap<String, String> item = new HashMap<String ,String >() ;
			item.put("dayNum", String.valueOf(i));
			list.add (item);
		}

		// �U�Ӥ���,�q1���}�l����l��
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
