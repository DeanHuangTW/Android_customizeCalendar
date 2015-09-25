package com.example.customizecalendar;

import com.example.customizecalendar.DBHelper;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public final class AlarmReceiver extends BroadcastReceiver {
    private String[] mAlarmTimeWayList = {"Toast" , "Vibrator", "Voice"};
    private DBHelper DH = null; 
    @Override
    public void onReceive(Context context,Intent intent){
    	Long eventID = intent .getLongExtra("EventID" , 0) ;    	
    	String notifyWay = getNotifyWayFromDatabase(context, eventID);    	    	
    	Log.v("AlarmReceiver", "事件" + String.valueOf(eventID) + ",通知方式:" + notifyWay);
    	
    	if (notifyWay.equals(mAlarmTimeWayList[0])) {
    		Toast. makeText(context , "Alarm work!", Toast.LENGTH_LONG).show ();
    	} else if (notifyWay.equals(mAlarmTimeWayList[1])){
    		Vibrator myVibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
    		myVibrator.vibrate(1500);
    	} else {
    		
    	}
    }
    
    /* 根據eventID取的Database中的Notify way*/
    private String getNotifyWayFromDatabase(Context context, Long eventId) {
    	DH = new DBHelper(context);
        SQLiteDatabase db = DH.getReadableDatabase();
        String[] columns = {"_EventID", "_NotifyWay"};
        String Select = "(_EventID=" + String.valueOf(eventId) +")";
        
        Cursor cursor = db.query("AlarmTable", columns, Select, null, null, null, null);
        
        String notifyWay = "";
        while (cursor.moveToNext()) {
        	notifyWay = cursor.getString(1);  // index 1 是notifyWay
        }
        
        DH.close();
        return notifyWay;
    }
}
