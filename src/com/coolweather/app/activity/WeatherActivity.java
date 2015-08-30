package com.coolweather.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.LogUtil;
import com.coolweather.app.util.Utility;

public class WeatherActivity extends Activity {
	
	private static final String COUNTY_CODE_KEY 		= "county_code";
	private static final String QUERY_TYPE_COUNTY_CODE 	= "county_code";
	private static final String QUERY_TYPE_WEATHER_CODE = "weather_code";
	
	private LinearLayout 	mWeatherLayout;
	private TextView		mtvCityName;
	private TextView 		mtvPublishTime;
	private TextView 		mtvCurDate;
	private TextView 		mtvWeatherDesp;
	private TextView 		mtvTemp1;
	private TextView 		mtvTemp2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		initView();
		getCountyWeatherInfo();
	}
	
	/**
	 * 本活动启动方法
	 */
	public static void start(Context ctx, String strCountyCode){
		Intent intent = new Intent(ctx, WeatherActivity.class);
		intent.putExtra(COUNTY_CODE_KEY, strCountyCode);
		ctx.startActivity(intent);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		
		this.mWeatherLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		this.mtvCityName		= (TextView) findViewById(R.id.tvCityName);
		this.mtvPublishTime	= (TextView) findViewById(R.id.tvPublish);
		this.mtvCurDate		= (TextView) findViewById(R.id.tvCurDate);
		this.mtvWeatherDesp	= (TextView) findViewById(R.id.tvWeatherDesp);
		this.mtvTemp1		= (TextView) findViewById(R.id.tvTemp1);
		this.mtvTemp2		= (TextView) findViewById(R.id.tvTemp2);	
	}
	
	/**
	 * 获取county天气信息
	 */
	private void getCountyWeatherInfo() {
		
		Intent intent = getIntent();
		String strCountyCode = intent.getStringExtra(COUNTY_CODE_KEY);
		
		if( !TextUtils.isEmpty(strCountyCode)){
			//根据countycode去查询天气
			this.mtvPublishTime.setText("同步中...");
			this.mWeatherLayout.setVisibility(View.INVISIBLE);
			queryWeather(strCountyCode);
		}
		else{
			//没有county代号时就直接显示本地存储的天气
			showWeather();
		}
	}

	/**
	 * 根据countyCode查询county级天气信息
	 * @param strCountyCode
	 */
	private void queryWeather(String strCountyCode) {
		final String strAddress = "http://www.weather.com.cn/data/list3/city" + strCountyCode + ".xml";
		queryFromServer(strAddress, QUERY_TYPE_COUNTY_CODE);
	}

	/**
	 * 
	 * @param strAddress
	 */
	private void queryFromServer(final String strAddress, final String strQueryCode) {
		
		LogUtil.d("WeatherActivity", "queryFromServer --- strAddress: " + strAddress + ", strQueryCode: " + strQueryCode);
		
		HttpUtil.sendHttpRequest(strAddress, new HttpCallbackListener() {
			
			@Override
			public void onFinish(final String response) {
				
				LogUtil.d("WeatherActivity", "queryFromServer --- response: " + response);
				
				if(QUERY_TYPE_COUNTY_CODE.equals(strQueryCode)){
					if(!TextUtils.isEmpty(response)){
						//从服务器返回的数据中解析出天气的代号
						String [] array = response.split("\\|");
						if( (null != array) && (2 == array.length) ){
							String strWeatherCode = array[1];
							queryWeatherInfo(strWeatherCode);
						}
					}
				}
				else if(QUERY_TYPE_WEATHER_CODE.equals(strQueryCode)){
					//处理服务器返回的天气信息
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					
					//回到主线程显示天气信息
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						mtvPublishTime.setText("同步失败！！！");
					}
				});
			}
		});
	}
	
	/**
	 * 根据天气代号查询对应的天气信息
	 * @param strWeatherCode
	 */
	protected void queryWeatherInfo(String strWeatherCode) {
		final String strAddress = "http://www.weather.com.cn/data/cityinfo/" + strWeatherCode + ".html";
		queryFromServer(strAddress, QUERY_TYPE_WEATHER_CODE);
	}

	/**
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面
	 */
	private void showWeather() {
		
		SharedPreferences sprefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		this.mtvCityName.setText(sprefs.getString("city_name", ""));
		this.mtvPublishTime.setText("今天" + sprefs.getString("publish_time", "") + "发布");
		this.mtvCurDate.setText(sprefs.getString("current_date", ""));
		this.mtvTemp1.setText(sprefs.getString("temp1", ""));
		this.mtvTemp2.setText(sprefs.getString("temp2", ""));
		this.mtvWeatherDesp.setText(sprefs.getString("weather_desp", ""));
		this.mWeatherLayout.setVisibility(View.VISIBLE);
	}
}























