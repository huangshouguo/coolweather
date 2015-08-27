package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public class CoolWeatherDB {
	
	/*
	 * ���ݿ������
	 */
	private static final String DB_NAME = "cool_weather";
	
	/*
	 * ���ݿ�İ汾
	 */
	private static final int DB_VERSION = 1;
	
	private static CoolWeatherDB coolWeatherDB;
	
	private SQLiteDatabase db;
	
	/*
	 * �����췽��˽�л�����ֹ�̳�
	 */
	private CoolWeatherDB(Context ctx) {
		
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(
				ctx, DB_NAME, null, DB_VERSION);
		
		this.db = dbHelper.getWritableDatabase();
	}
	
	/*
	 * ��ȡCoolWeatherDB��ʵ��
	 */
	public synchronized static CoolWeatherDB getInstance(Context ctx){
		
		if(null == coolWeatherDB){
			coolWeatherDB = new CoolWeatherDB(ctx);
		}
		
		return coolWeatherDB;
	}
	
	/*
	 * ��Province������Ϣ�洢�����ݿ�
	 */
	public void saveProvince(Province province){
		if(null != province){
			ContentValues values = new ContentValues();
			
			values.put("prince_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			
			db.insert("Province", null, values);
		}
	}

	/*
	 * �����ݿ��ȡȫ����ʡ��������Ϣ
	 */
	public List<Province> loadProvinces(){
		List <Province> lists = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				Province province = new Province();
				province.setProvinceId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				lists.add(province);
			}while(cursor.moveToNext());
		}
		
		if(null != cursor){
			cursor.close();
		}
		return lists;
	}
	
	/*
	 * ��city��Ϣ�洢������
	 */
	public void saveCity(City city) {
		if(null != city){
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	
	/*
	 * �����ݿ��ȡĳ��Province������city��Ϣ
	 */
	public List<City> loadCities(int provinceId){
		List<City> lists = new ArrayList<City>();
		
		Cursor cursor = db.query(	"City", 
									null, 
									"province_id = ?", 
									new String [] {String.valueOf(provinceId)}, 
									null, 
									null,
									null);
		if(cursor.moveToFirst()){
			do{
				City city = new City();
				city.setCityId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
				lists.add(city);
			}while(cursor.moveToNext());
		}
		
		if(null != cursor){
			cursor.close();
		}
		
		return lists;
	}
	
	/*
	 * ��county��Ϣ�洢�����ݿ���
	 */
	public void saveCounty(County county){
		
		if(null != county){
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values );
		}
		
	}
	
	/*
	 * �����ݿ��ȡĳ��city������county��Ϣ
	 */
	public List<County> loadCounties (int cityId){
		List<County> lists = new ArrayList<County>();
		
		Cursor cursor = db.query(	"County", 
									null, 
									"city_id = ?", 
									new String[]{String.valueOf(cityId)},
									null, 
									null,
									null);
		if(cursor.moveToFirst()){
			do{
				County county = new County();
				county.setCountyId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
				lists.add(county);
			}while(cursor.moveToNext());
		}
		
		if(null != cursor){
			cursor.close();
		}
		
		return lists;
	}
}
