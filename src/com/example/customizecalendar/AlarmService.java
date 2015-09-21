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
	
	// 設置鬧鐘, eventID拿來當鬧鐘的unique ID
	public void setAlarm(ContentResolver cr, int reminderTime) {
		ContentValues values = new ContentValues();
		
		values.put(Reminders.MINUTES, reminderTime);  // 事件發生前多久提醒 (分)
		values.put(Reminders.EVENT_ID, mEventId);
		values.put(Reminders.METHOD, Reminders.METHOD_ALERT);  //提醒方式
		cr.insert(Reminders.CONTENT_URI, values);
	}
	
	/* 根據eventID修改reminder的時間與方式*/
	public void updateAlarm(ContentResolver cr, int reminderTime) {
		//取得reminder Uri
		ContentValues values = new ContentValues();
		int reminderId = queryRemindersId(cr);
		Uri updateUri = ContentUris.withAppendedId(Reminders.CONTENT_URI, reminderId);
		// update
		values.put(Reminders.MINUTES, reminderTime);  // 事件發生前多久提醒 (分)
		values.put(Reminders.EVENT_ID, mEventId);
		values.put(Reminders.METHOD, Reminders.METHOD_ALERT);  //提醒方式
		cr.update(updateUri, values, null, null);
	}
	
	public void cancelAlarm(ContentResolver cr) {
		//取得reminder Uri
		int reminderId = queryRemindersId(cr);
		Uri updateUri = ContentUris.withAppendedId(Reminders.CONTENT_URI, reminderId);
		
		cr.delete(updateUri, null, null);
	}
	
	/* 根據eventID搜尋reminderID */
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
	
	/* 拿來測試reminder是否真的新增,刪除 */
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
