package com.example.customizecalendar;

import com.example.customizecalendar.AlarmReceiver;
import com.example.customizecalendar.DBHelper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class AlarmService {
	private String TAG = "Dean";
	
	private long mEventId;
	private Context mContext;
	
	private DBHelper DH = null;
	public AlarmService() {
		this.mEventId = 0;
	}
	
	public AlarmService(Context ctx, long Id) {
		this.mEventId = Id;
		mContext = ctx;
	}
	
	public void setAlarm(long start, int reminderTime, String alarmWay) {
		Log.v(TAG, "setAlarm. ID:" + mEventId);
		Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.setData(Uri.parse("custom://customizeCalendar/" + mEventId));
        intent.putExtra("EventID", mEventId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
          
        AlarmManager am = (AlarmManager ) mContext.getSystemService(Context.ALARM_SERVICE );
        am.set(AlarmManager .RTC_WAKEUP, start - (reminderTime * 1000 ), pendingIntent);
        
        // write database
        DH = new DBHelper(mContext);
        SQLiteDatabase db = DH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("_EventID", mEventId);
        values.put("_NotifyWay", alarmWay);
        values.put("_NotifyTime", reminderTime);
        db.insert("AlarmTable", null, values);
        DH.close();
	}
	
	public void cancelAlarm() {
		Log.v(TAG, "cancelAlarm. ID:" + mEventId);
		Intent intent = new Intent(mContext, AlarmReceiver.class);
		intent.setData(Uri.parse("custom://customizeCalendar/" + mEventId));
		intent.setAction(String.valueOf(mEventId));
		PendingIntent pendingIntent = 
				PendingIntent.getBroadcast(mContext.getApplicationContext(), 0, intent, 0);

		AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pendingIntent);
		
		DH = new DBHelper(mContext);
		SQLiteDatabase db = DH.getWritableDatabase();
	    db.delete("AlarmTable", "_EventID=" + mEventId, null);
	    DH.close();
	}
	
	public void updateAlarm(long start, int reminderTime, String alarmWay) {
		Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.setData(Uri.parse("custom://customizeCalendar/" + mEventId));
        intent.putExtra("EventID", mEventId);
        PendingIntent pendingIntent = 				//使用FLAG_CANCEL_CURRENT才能更新intent
        		PendingIntent.getBroadcast(mContext, 0, intent,  PendingIntent.FLAG_CANCEL_CURRENT);
          
        AlarmManager am = (AlarmManager ) mContext.getSystemService(Context.ALARM_SERVICE );
        am.set(AlarmManager .RTC_WAKEUP, start - (reminderTime * 1000 ), pendingIntent);
        
		DH = new DBHelper(mContext);
        SQLiteDatabase db = DH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("_EventID", mEventId);
        values.put("_NotifyWay", alarmWay);
        values.put("_NotifyTime", reminderTime);
        db.update("AlarmTable", values, "_EventID=" + mEventId, null);
        DH.close();
	}
	
	public Cursor queryAlarmNotify() {
		Log.v(TAG, "queryAlarmNotify");
		
		DH = new DBHelper(mContext);
        SQLiteDatabase db = DH.getReadableDatabase();
        String[] columns = {"_EventID", "_NotifyWay", "_NotifyTime"};
        String select = "(_EventID=" + mEventId + ")";
        Cursor cursor = db.query("AlarmTable", columns, select, null, null, null, null);
        
        return cursor;
	}
	
	public String showAllAlarm() {
		DH = new DBHelper(mContext);
        SQLiteDatabase db = DH.getReadableDatabase();
        String[] columns = {"_ID", "_EventID", "_NotifyWay", "_NotifyTime"};
        Cursor cursor = db.query("AlarmTable", columns, null, null, null, null, null);
        
        String alarmList = "";
        while (cursor.moveToNext()) {
        	alarmList += "ID:" + cursor.getString(0) +
        			"  EventID:" + cursor.getString(1) +
        			"  NotifyWay:" + cursor.getString(2) +
        			"  NotifyTime:" + cursor.getString(3) + "\n";
        }
        
        return alarmList;
	}
	
}
