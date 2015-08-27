package com.coolweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
	
	/*
	 * Province 表建表语句
	 */
	private static final String CREATE_DB_PROVINCE = "create table Province (" 
				+ "id integer primary key autoincrement, "
				+ "province_name text,"
				+ "province_code text)";
	
	/*
	 * City 表建表语句
	 */
	private static final String CREATE_DB_CITY = "create table City ("
				+ "id integer primary key autoincrement, "
				+ "city_name text, "
				+ "city_code text, "
				+ "province_id integer)";

	/*
	 * County 表建表语句
	 */
	private static final String CREATE_DB_COUNTY = "create table County ("
				+ "id integer primary key autoincrement, "
				+ "county_name text, "
				+ "county_code text, "
				+ "city_id integer)";
	
	
	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_DB_PROVINCE); //建立province数据库
		db.execSQL(CREATE_DB_CITY); //建立city数据库
		db.execSQL(CREATE_DB_COUNTY); //建立country数据库
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
