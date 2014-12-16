package com.bobby.firstsms;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Db extends SQLiteOpenHelper {

	public Db(Context context) {
		super(context, "bobbydb", null, 1);
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table SMS_SEND_TO(" +
				"PHONE_NO TEXT" +
				")");
		db.execSQL("create table SMS_PARM(" +
				"LOCALE_RECEIVE TEXT" +
				")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
