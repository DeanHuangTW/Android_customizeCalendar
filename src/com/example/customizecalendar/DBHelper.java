package com.example.customizecalendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

	private final static int _DBVersion = 1;
	public static final String _DBName = "AlarmList.db"; 
	public static final String _TableName = "AlarmTable";
	
	public DBHelper(Context context) {
		super(context, _DBName, null, _DBVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String DATABASE_CREATE_TABLE =
				"CREATE TABLE IF NOT EXISTS " + _TableName + "(" +
				"_ID INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL," + 
				"_EventID INTEGER," +
				"_NotifyWay VARCHAR," +
				"_NotifyTime INTEGER" +
				")";
		db.execSQL(DATABASE_CREATE_TABLE);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		final String SQL = "DROP TABLE " + _TableName;
		db.execSQL(SQL); //刪除舊有的資料表
		onCreate(db);		
	}

	
}
