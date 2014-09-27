package com.people.lyy.sqlite;



import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataDao {
	private mySQLiteHelpter helper;
	public DataDao(Context context){
		helper = new mySQLiteHelpter(context);
	}
	public void add(String name,int number){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("INSERT INTO PERSON(NAME,NUMBER)VALUES(?,?)",new Object[]{name,number});
		db.close();
	}
	public boolean find(String name){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM PERSON WHERE NAME = ?", new String[]{name});
		boolean result = cursor.moveToNext();
		cursor.close();
		db.close();
		return result;
	} 
	public void update(String name , int number){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("UPDATE PERSON SET NUMBER = ? WHERE NAME = ? ",new Object[]{number,name});
		db.close();
	}
	public void delete(String name){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("DELETE FROM PERSON WHERE NAME = ?" , new Object[]{name});
		db.close();
	}
}
