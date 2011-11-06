package com.myroid.status;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StatusData {

	private static final String TAG = 
			StatusData.class.getSimpleName();
	
	static final int VERSION = 3;
	static final String DATABASE = "timeline.db";
	static final String TABLE = "timeline";
	
	public static final String C_ID = "_id";
	public static final String C_CREATED_AT = "created_at";
	public static final String C_TEXT = "txt";
	public static final String C_USER = "user";
	public static final String C_USER_IMG = "user_prof_img";
	
	private static final String GET_ALL_ORDER_BY = C_CREATED_AT + " DESC";
	
	private static final String[] DB_TEXT_COLUMNS = { C_TEXT };
	
	private static final String[] MAX_CREATED_AT_COLUMNS = {		
		"max(" + StatusData.C_CREATED_AT + ")" };
	
	class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, DATABASE, null, VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG, "create table " + TABLE);
			db.execSQL("CREATE TABLE " + TABLE + " (" + 
					C_ID + " int primary key, " +
					C_CREATED_AT + " int, " +
					C_USER + " text, " +
					C_TEXT + " text, " +
					C_USER_IMG + " text" +
					");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table " + TABLE);			
			this.onCreate(db);
		}
	}
	
	public final DBHelper dbHelper;
	
	public StatusData(Context context) {
		dbHelper = new DBHelper(context);
		Log.i(TAG, "Initialized data");
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public void insertOrIgnore(ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			db.insertWithOnConflict(TABLE, 
					null, values, SQLiteDatabase.CONFLICT_IGNORE);
		} finally {
			db.close();
		}
	}
	
	/**
	 * 
	 * @return cursor 컬럼은 _id, created_at, user, txt
	 */
	public Cursor getStatusUpdates() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		return db.query(TABLE, null, null, null, null, null, GET_ALL_ORDER_BY);
	}
	
	/**
	 * 가장 최근에 받은 timeline의 시간을 return하는 메소드
	 * @return max time
	 */
	public long getLatestStatusCreatedAtTime() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			// select MAX(created_at) from timeline;
			Cursor cursor = db.query(TABLE, 
					MAX_CREATED_AT_COLUMNS, null, null, null, null, null);
			try {
				return cursor.moveToNext() ? cursor.getLong(0) : Long.MAX_VALUE;
			} finally {
				cursor.close();
			}
		} finally {
			db.close();
		}
	}
	
	public String getStatusTextById(long id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			// select txt from timeline
			// where _id = 18678990;
			Cursor cursor = db.query(TABLE, 
					DB_TEXT_COLUMNS, 
					C_ID + "=" + id, 
					null, null, null, null);
			try {
				return cursor.moveToNext() ? cursor.getString(0) : null;
			} finally {
				cursor.close();
			}
		} finally {
			db.close();
		}
	}
}
