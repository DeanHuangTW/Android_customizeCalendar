package com.example.customizecalendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Reminders;
import android.util.Log;

public class AlarmService {
	public static final String[] REMINDERS_PROJECTION = new String[] {
		Reminders.EVENT_ID,      // 0
		Reminders._ID,
		Reminders.MINUTES
	};
	
	private long mEventId;
	
	public AlarmService() {
		this.mEventId = 0;
	}
	
	public AlarmService(long Id) {
		this.mEventId = Id;
	}
	
	// �]�m�x��, eventID���ӷ�x����unique ID
	public void setAlarm(ContentResolver cr, int reminderTime) {
		ContentValues values = new ContentValues();
		
		values.put(Reminders.MINUTES, reminderTime);  // �ƥ�o�ͫe�h�[���� (��)
		values.put(Reminders.EVENT_ID, mEventId);
		values.put(Reminders.METHOD, Reminders.METHOD_ALERT);  //�����覡
		cr.insert(Reminders.CONTENT_URI, values);
	}
	
	/* �ھ�eventID�ק�reminder���ɶ��P�覡*/
	public void updateAlarm(ContentResolver cr, int reminderTime) {
		//���oreminder Uri
		ContentValues values = new ContentValues();
		int reminderId = queryRemindersId(cr);
		Uri updateUri = ContentUris.withAppendedId(Reminders.CONTENT_URI, reminderId);
		// update
		values.put(Reminders.MINUTES, reminderTime);  // �ƥ�o�ͫe�h�[���� (��)
		values.put(Reminders.EVENT_ID, mEventId);
		values.put(Reminders.METHOD, Reminders.METHOD_ALERT);  //�����覡
		cr.update(updateUri, values, null, null);
	}
	
	public void cancelAlarm(ContentResolver cr) {
		//���oreminder Uri
		int reminderId = queryRemindersId(cr);
		Uri updateUri = ContentUris.withAppendedId(Reminders.CONTENT_URI, reminderId);
		
		cr.delete(updateUri, null, null);
	}
	
	/* �ھ�eventID�j�MreminderID */
	public int queryRemindersId(ContentResolver cr) {
		String[] selectionArgs = new String[] { Long.toString(mEventId) };
		String selection = Reminders.EVENT_ID + "=?";		
		int reminderId = 0;
		
		Cursor cursor = cr.query(
				Reminders.CONTENT_URI, REMINDERS_PROJECTION, selection, selectionArgs, null);
		while (cursor.moveToNext()) {
			reminderId = cursor.getInt(1);  // Reminders._ID
		}
		
		return reminderId;
	}
	
	public Cursor queryReminders(ContentResolver cr) {
		String[] selectionArgs = new String[] { Long.toString(mEventId) };
		String selection = Reminders.EVENT_ID + "=?";		
		Cursor cursor = cr.query(
				Reminders.CONTENT_URI, REMINDERS_PROJECTION, selection, selectionArgs, null);
		
		return cursor;
	}
	
	/* ���Ӵ���reminder�O�_�u���s�W,�R�� */
	public void showAllReminder(ContentResolver cr) {
		Log.i("alarmService", "showAllReminder");
		
		Cursor cursor = cr.query(
				Reminders.CONTENT_URI, REMINDERS_PROJECTION, null, null, null);
		while (cursor.moveToNext()) {
			Log.i("alarmService", "    reminder ID:" + String.valueOf(cursor.getInt(1)));
			Log.i("alarmService", "    reminder time:" + String.valueOf(cursor.getInt(2)));
		}
	}
}
