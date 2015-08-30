package com.coolweather.app.util;

import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public class Utility {

	/**
	 * �����ʹ�����������ص�province�����ݲ����������ݿ�
	 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB db,
			String response) {

		LogUtil.d("Utility", "hanleProvincesResponse --- response:" + response);

		if (!TextUtils.isEmpty(response)) {

			String[] allProvinces = response.split(",");// �ԡ�,��Ϊ�ֽ��߷ָ��ַ���

			if (null != allProvinces && allProvinces.length > 0) {

				for (int i = 0; i < allProvinces.length; i++) {
					String[] array = allProvinces[i].split("\\|");// �ԡ�|��Ϊ�ֽ��߷ָ��ַ���

					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);

					// ���������������ݴ洢�����ݿ��е�Province����
					db.saveProvince(province);
				}
				return true;
			}
		}

		LogUtil.e("Utility", "hanleProvincesResponse --- response is empty");

		return false;
	}

	/**
	 * �������������ص�city�����ݲ��洢�����ݿ�
	 */

	public synchronized static boolean handleCitiesResponse(CoolWeatherDB db,
			String response, int provinceId) {

		LogUtil.d("Utility", "handleCitiesResponse --- response:" + response);

		if (!TextUtils.isEmpty(response)) {

			String[] allCities = response.split(",");// �ԡ������ָ�����

			if (null != allCities && allCities.length > 0) {
				for (int i = 0; i < allCities.length; i++) {
					String[] array = allCities[i].split("\\|");

					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);

					// ������������city���ݱ��浽���ݿ�
					db.saveCity(city);
				}
				return true;
			}
		}

		LogUtil.e("Utility", "handleCitiesResponse --- response is empty");

		return false;
	}

	/**
	 * �������������ص�County�����ݲ����浽���ݿ�
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

					// ���������������ݱ��浽���ݿ�
					db.saveCounty(county);
				}
				return true;
			}
		}

		LogUtil.e("Utility", "handleCountiesResponse --- response is empty");

		return false;
	}

}
