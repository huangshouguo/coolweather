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
	 * �����������
	 */
	public static void start(Context ctx, String strCountyCode){
		Intent intent = new Intent(ctx, WeatherActivity.class);
		intent.putExtra(COUNTY_CODE_KEY, strCountyCode);
		ctx.startActivity(intent);
	}

	/**
	 * ��ʼ���ؼ�
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
	 * ��ȡcounty������Ϣ
	 */
	private void getCountyWeatherInfo() {
		
		Intent intent = getIntent();
		String strCountyCode = intent.getStringExtra(COUNTY_CODE_KEY);
		
		if( !TextUtils.isEmpty(strCountyCode)){
			//����countycodeȥ��ѯ����
			this.mtvPublishTime.setText("ͬ����...");
			this.mWeatherLayout.setVisibility(View.INVISIBLE);
			queryWeather(strCountyCode);
		}
		else{
			//û��county����ʱ��ֱ����ʾ���ش洢������
			showWeather();
		}
	}

	/**
	 * ����countyCode��ѯcounty��������Ϣ
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
						//�ӷ��������ص������н����������Ĵ���
						String [] array = response.split("\\|");
						if( (null != array) && (2 == array.length) ){
							String strWeatherCode = array[1];
							queryWeatherInfo(strWeatherCode);
						}
					}
				}
				else if(QUERY_TYPE_WEATHER_CODE.equals(strQueryCode)){
					//������������ص�������Ϣ
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					
					//�ص����߳���ʾ������Ϣ
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
						mtvPublishTime.setText("ͬ��ʧ�ܣ�����");
					}
				});
			}
		});
	}
	
	/**
	 * �����������Ų�ѯ��Ӧ��������Ϣ
	 * @param strWeatherCode
	 */
	protected void queryWeatherInfo(String strWeatherCode) {
		final String strAddress = "http://www.weather.com.cn/data/cityinfo/" + strWeatherCode + ".html";
		queryFromServer(strAddress, QUERY_TYPE_WEATHER_CODE);
	}

	/**
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ������
	 */
	private void showWeather() {
		
		SharedPreferences sprefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		this.mtvCityName.setText(sprefs.getString("city_name", ""));
		this.mtvPublishTime.setText("����" + sprefs.getString("publish_time", "") + "����");
		this.mtvCurDate.setText(sprefs.getString("current_date", ""));
		this.mtvTemp1.setText(sprefs.getString("temp1", ""));
		this.mtvTemp2.setText(sprefs.getString("temp2", ""));
		this.mtvWeatherDesp.setText(sprefs.getString("weather_desp", ""));
		this.mWeatherLayout.setVisibility(View.VISIBLE);
	}
}























