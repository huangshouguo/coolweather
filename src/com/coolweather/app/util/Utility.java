package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public class Utility {

	/**
	 * 解析和处理服务器返回的province级数据并保存至数据库
	 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB db,
			String response) {

		LogUtil.d("Utility", "hanleProvincesResponse --- response:" + response);

		if (!TextUtils.isEmpty(response)) {

			String[] allProvinces = response.split(",");// 以“,”为分界线分割字符串

			if (null != allProvinces && allProvinces.length > 0) {

				for (int i = 0; i < allProvinces.length; i++) {
					String[] array = allProvinces[i].split("\\|");// 以“|”为分界线分割字符串

					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);

					// 将解析出来的数据存储到数据库中的Province表里
					db.saveProvince(province);
				}
				return true;
			}
		}

		LogUtil.e("Utility", "hanleProvincesResponse --- response is empty");

		return false;
	}

	/**
	 * 解析服务器返回的city级数据并存储到数据库
	 */

	public synchronized static boolean handleCitiesResponse(CoolWeatherDB db,
			String response, int provinceId) {

		LogUtil.d("Utility", "handleCitiesResponse --- response:" + response);

		if (!TextUtils.isEmpty(response)) {

			String[] allCities = response.split(",");// 以“，”分割数据

			if (null != allCities && allCities.length > 0) {
				for (int i = 0; i < allCities.length; i++) {
					String[] array = allCities[i].split("\\|");

					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);

					// 将解析出来的city数据保存到数据库
					db.saveCity(city);
				}
				return true;
			}
		}

		LogUtil.e("Utility", "handleCitiesResponse --- response is empty");

		return false;
	}

	/**
	 * 解析服务器返回的County级数据并保存到数据库
	 */
	public synchronized static boolean handleCountiesResponse(CoolWeatherDB db,
			String response, int cityId) {

		LogUtil.d("Utility", "handleCountiesResponse --- response:" + response);

		if (!TextUtils.isEmpty(response)) {

			String[] allCounties = response.split(",");

			if (null != allCounties && allCounties.length > 0) {
				for (int i = 0; i < allCounties.length; i++) {
					String[] array = allCounties[i].split("\\|");

					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);

					// 将解析出来的数据保存到数据库
					db.saveCounty(county);
				}
				return true;
			}
		}

		LogUtil.e("Utility", "handleCountiesResponse --- response is empty");

		return false;
	}

	/**
	 * 解析服务器返回的JSON格式天气数据，并保存到本地
	 */
	public static void handleWeatherResponse(Context ctx, String strResponse){
		
			LogUtil.d("Utility", "handleWeatherResponse ---" + strResponse);
			
			try {
				JSONObject jsonObj 		= new JSONObject(strResponse);
				JSONObject weatherInfo 	= jsonObj.getJSONObject("weatherinfo");
				
				String strCityName 		= weatherInfo.getString("city");
				String strCityId		= weatherInfo.getString("cityid");
				String strTemp1			= weatherInfo.getString("temp1");
				String strTemp2			= weatherInfo.getString("temp2");
				String strWeatherDesp 	= weatherInfo.getString("weather");
				String strPublishTime	= weatherInfo.getString("ptime");
				
				saveWeatherInfo(ctx, strCityName, strCityId, strTemp1, strTemp2, strWeatherDesp, strPublishTime);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * 将服务器返回的天气信息存储到SharedPreference文件中
	 */
	public static void saveWeatherInfo(	Context ctx, 
										String strCityName,
										String strWeatherCode,
										String strTemp1,
										String strTemp2,
										String strWeatherDesp,
										String strPulishTime){
		
		LogUtil.d("Utility", "saveWeatherInfo --- cityName: " + strCityName +
				                                  "weatherCode: " + strWeatherCode +
				                                  "temp1: " + strTemp1 +
				                                  "temp2: " + strTemp2 +
				                                  "weatherDesp: " + strWeatherDesp +
				                                  "publishTime: " + strPulishTime);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
		
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", strCityName);
		editor.putString("weather_code", strWeatherCode);
		editor.putString("temp1", strTemp1);
		editor.putString("temp2", strTemp2);
		editor.putString("weather_desp", strWeatherDesp);
		editor.putString("publish_time", strPulishTime);
		editor.putString("current_date", sdf.format(new Date()));
		
		editor.commit();
	}
}












