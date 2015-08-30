package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.LogUtil;
import com.coolweather.app.util.Utility;

public class ChooseAreaActivity extends Activity {

	/*********************************************************************************************/
	/******************************* ����������   ******************************************************/  
	
	/**
	 * ��ʾ�ȼ���������
	 */
	private static enum Level {
		LEVEL_PROVINCE, LEVEL_CITY, LEVEL_COUNTY
	}
	
	/**
	 * ��ѯ����
	 */
	private static final String QUERY_TYPE_PROVINCE = "Province";
	private static final String QUERY_TYPE_CITY 	= "City";
	private static final String QUERY_TYPE_COUNTY 	= "County";
	
	/**
	 * ��������ַ
	 */
	private static final String SERVER_ADDR = "http://www.weather.com.cn/data/list3/";
	
	/********************************************************************************************/
	
	/********************************************************************************************/
	/******************************* ����������   ******************************************************/
	private List<Province> 			mProvinceList; 	// �洢ȫ��province������
	private List<City> 				mCityList; 		// �洢ĳ��province��city��ȫ������
	private List<County> 			mCountyList; 	// �洢ĳ��city��county��ȫ������
	private List<String> 			mDataList; 		// ��ǰ��ʾ������
	private ListView 				mListView; 		// ��ѡ���province/city/county������ʾ�б�ؼ�
	private ArrayAdapter<String> 	mAdapter; 		// listview��������
	private Level 					mCurLevel; 		// ��¼��ǰ����ʾ�ȼ�province/city/county
	private Province 				mCurProvince; 	// ��ǰѡ���province
	private City 					mCurCity; 		// ��ǰѡ���city
	private County 					mCurConty; 		// ��ǰѡ���county
	private CoolWeatherDB 			mCoolWeatherDB; // ���ݿ�
	private TextView 				mTVTitle; 		// ��ʾ����ؼ�
	private ProgressDialog          mProgressDlg;	// ���ȶԻ���
	/********************************************************************************************/
	
	/********************************************************************************************/
	/******************************* ����������   ******************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		initViews();
		setListView();
		queryProvinces(); // ����province������
	}

	/**
	 * ��ʼ������
	 */
	private void initViews() {

		this.mProvinceList 	= new ArrayList<Province>();
		this.mCityList 		= new ArrayList<City>();
		this.mCountyList 	= new ArrayList<County>();
		this.mDataList 		= new ArrayList<String>();
		this.mListView 		= (ListView) findViewById(R.id.list_view);
		this.mAdapter 		= new ArrayAdapter<String>(this,
								android.R.layout.simple_expandable_list_item_1, this.mDataList);
		this.mCurLevel 		= Level.LEVEL_PROVINCE;
		this.mCoolWeatherDB = CoolWeatherDB.getInstance(this);
		this.mTVTitle		= (TextView) findViewById(R.id.tvTitle);
	}

	/**
	 * ��ListView������
	 */
	private void setListView() {
		this.mListView.setAdapter(this.mAdapter);
		this.mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (Level.LEVEL_PROVINCE == mCurLevel) {
					mCurProvince = mProvinceList.get(arg2); //��¼��ǰѡ���province
					queryCities(); // ����city������
				} else if (Level.LEVEL_CITY == mCurLevel) {
					mCurCity = mCityList.get(arg2); //��¼��ǰѡ���city
					queryCounties(); // ����county������
				}
			}
		});
	}

	/**
	 * ��ѯprovince���ݣ����ȴ������в�ѯ�����û����������ٴӷ������ϲ�ѯ
	 */
	private void queryProvinces() {
		
		this.mProvinceList = this.mCoolWeatherDB.loadProvinces();

		//������ݿ������ݣ�������ݿ���أ�����ӷ������ϼ���
		if (this.mProvinceList.size() > 0) {
			
			this.mDataList.clear();
			
			for (int i = 0; i < mProvinceList.size(); i++) {
				Province province = mProvinceList.get(i);
				this.mDataList.add(province.getProvinceName());
			}
			
			this.mAdapter.notifyDataSetChanged(); //����listview
			this.mListView.setSelection(0); //����listview��0��ʼ��ʾ
			this.mTVTitle.setText("�й�"); //���ñ���
			this.mCurLevel = Level.LEVEL_PROVINCE; //���õ�ǰ��ʾ�ȼ�
		}
		else {
			queryFromServer(null, QUERY_TYPE_PROVINCE);
		}
	}

	/**
	 * ��ѯcity���ݣ����ȴ������в�ѯ�����û����������ٴӷ������ϲ�ѯ
	 */
	private void queryCities() {
		
		this.mCityList = this.mCoolWeatherDB.loadCities(this.mCurProvince.getProvinceId());
		
		//������ݿ��������ݾʹ����ݿ��м��أ����û�оʹӷ�������ȡ
		if(this.mCityList.size() > 0){
			
			this.mDataList.clear();
			
			for(int i = 0; i < this.mCityList.size(); i++){
				City city = this.mCityList.get(i);
				this.mDataList.add(city.getCityName());
			}
			
			this.mAdapter.notifyDataSetChanged();
			this.mListView.setSelection(0);
			this.mTVTitle.setText(this.mCurProvince.getProvinceName());
			this.mCurLevel = Level.LEVEL_CITY;
		}
		else{
			queryFromServer(this.mCurProvince.getProvinceCode(), QUERY_TYPE_CITY);
		}
	}

	/**
	 * ��ѯcounty���ݣ����ȴ������в�ѯ�����û����������ٴӷ������ϲ�ѯ
	 */
	private void queryCounties() {
		
		this.mCountyList = this.mCoolWeatherDB.loadCounties(this.mCurCity.getCityId());
		
		if(this.mCountyList.size() > 0){
			
			this.mDataList.clear();
			
			for(int i = 0; i < this.mCountyList.size(); i++){
				County county = this.mCountyList.get(i);
				this.mDataList.add(county.getCountyName());
			}
			
			this.mAdapter.notifyDataSetChanged();
			this.mListView.setSelection(0);
			this.mTVTitle.setText(this.mCurCity.getCityName());
			this.mCurLevel = Level.LEVEL_COUNTY;
		}
		else {
			queryFromServer(this.mCurCity.getCityCode(), QUERY_TYPE_COUNTY);
		}
	}
	
	
	/**
	 * ���ݴ���Ĵ��ź����ʹӷ������ϼ��ظ�province/city/county����
	 * @param strCode provinceCode/cityCode
	 * @param strType 
	 */
	private void queryFromServer(final String strCode, final String strType) {
		
		LogUtil.d("ChooseAreaActiviy", "code: " + strCode + ", type: " + strType);
		
		String address = null;
		
		if(!TextUtils.isEmpty(strCode)){
			//address = SERVER_ADDR + "city" + strCode + ".xml";
			address = "http://www.weather.com.cn/data/list3/city" + strCode + ".xml";
		} else {
			//address = SERVER_ADDR + "city.xml";
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		
		showProgressDialog();
		
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean bResult = false;
				
				if(QUERY_TYPE_PROVINCE.equals(strType)){
					bResult = Utility.handleProvincesResponse(mCoolWeatherDB, response);
				}
				else if(QUERY_TYPE_CITY.equals(strType)){
					bResult = Utility.handleCitiesResponse(mCoolWeatherDB, response, mCurProvince.getProvinceId());
				} 
				else if(QUERY_TYPE_COUNTY.equals(strType)){
					bResult = Utility.handleCountiesResponse(mCoolWeatherDB, response, mCurCity.getCityId());
				}
				
				if(bResult){
					//ͨ��runOnUiThread()�����ص����̴߳���
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog();
							if(QUERY_TYPE_PROVINCE.equals(strType)){
								queryProvinces();
							}
							else if(QUERY_TYPE_CITY.equals(strType)){
								queryCities();
							} 
							else if(QUERY_TYPE_COUNTY.equals(strType)){
								queryCounties();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				//ͨ��runOnUiThread()�����ص����̴߳���
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	/**
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog() {
	
		if(null == this.mProgressDlg){
			this.mProgressDlg = new ProgressDialog(this);
			this.mProgressDlg.setMessage("���ڼ���....");
			this.mProgressDlg.setCanceledOnTouchOutside(false);
		}
		
		this.mProgressDlg.show();
	}
	
	/**
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog() {
		if(null != this.mProgressDlg){
			this.mProgressDlg.dismiss();
		}
	}
}
