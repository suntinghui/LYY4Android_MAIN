package com.people.lyy.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class mySQLiteHelpter extends SQLiteOpenHelper {
	private static String name = "Data.db";
	private static int version = 1;
	private String sql = "CREATE TABLE PERSON ("
			+ "_ID INTEGER PRIMARY KEY AUTOINCREMENT," + "NAME VARCHAR(20),"
			+ "NUMBER VARCHAR(20)" + ")";

	public mySQLiteHelpter(Context context) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
