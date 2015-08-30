package com.coolweather.app.util;

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

}
