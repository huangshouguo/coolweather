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
	/******************************* 常量定义区   ******************************************************/  
	
	/**
	 * 显示等级常量定义
	 */
	private static enum Level {
		LEVEL_PROVINCE, LEVEL_CITY, LEVEL_COUNTY
	}
	
	/**
	 * 查询类型
	 */
	private static final String QUERY_TYPE_PROVINCE = "Province";
	private static final String QUERY_TYPE_CITY 	= "City";
	private static final String QUERY_TYPE_COUNTY 	= "County";
	
	/**
	 * 服务器地址
	 */
	private static final String SERVER_ADDR = "http://www.weather.com.cn/data/list3/";
	
	/********************************************************************************************/
	
	/********************************************************************************************/
	/******************************* 变量定义区   ******************************************************/
	private List<Province> 			mProvinceList; 	// 存储全国province的数据
	private List<City> 				mCityList; 		// 存储某个province下city的全部数据
	private List<County> 			mCountyList; 	// 存储某个city下county的全部数据
	private List<String> 			mDataList; 		// 当前显示的数据
	private ListView 				mListView; 		// 供选择的province/city/county数据显示列表控件
	private ArrayAdapter<String> 	mAdapter; 		// listview的适配器
	private Level 					mCurLevel; 		// 记录当前的显示等级province/city/county
	private Province 				mCurProvince; 	// 当前选择的province
	private City 					mCurCity; 		// 当前选择的city
	private County 					mCurConty; 		// 当前选择的county
	private CoolWeatherDB 			mCoolWeatherDB; // 数据库
	private TextView 				mTVTitle; 		// 显示标题控件
	private ProgressDialog          mProgressDlg;	// 进度对话框
	/********************************************************************************************/
	
	/********************************************************************************************/
	/******************************* 方法定义区   ******************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		initViews();
		setListView();
		queryProvinces(); // 加载province级数据
	}

	/**
	 * 初始化方法
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
	 * 对ListView的设置
	 */
	private void setListView() {
		this.mListView.setAdapter(this.mAdapter);
		this.mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (Level.LEVEL_PROVINCE == mCurLevel) {
					mCurProvince = mProvinceList.get(arg2); //记录当前选择的province
					queryCities(); // 加载city级数据
				} else if (Level.LEVEL_CITY == mCurLevel) {
					mCurCity = mCityList.get(arg2); //记录当前选择的city
					queryCounties(); // 加载county级数据
				}
			}
		});
	}

	/**
	 * 查询province数据，优先从数据中查询，如果没有相关数据再从服务器上查询
	 */
	private void queryProvinces() {
		
		this.mProvinceList = this.mCoolWeatherDB.loadProvinces();

		//如果数据库有数据，则从数据库加载，否则从服务器上加载
		if (this.mProvinceList.size() > 0) {
			
			this.mDataList.clear();
			
			for (int i = 0; i < mProvinceList.size(); i++) {
				Province province = mProvinceList.get(i);
				this.mDataList.add(province.getProvinceName());
			}
			
			this.mAdapter.notifyDataSetChanged(); //更新listview
			this.mListView.setSelection(0); //设置listview从0开始显示
			this.mTVTitle.setText("中国"); //设置标题
			this.mCurLevel = Level.LEVEL_PROVINCE; //设置当前显示等级
		}
		else {
			queryFromServer(null, QUERY_TYPE_PROVINCE);
		}
	}

	/**
	 * 查询city数据，优先从数据中查询，如果没有相关数据再从服务器上查询
	 */
	private void queryCities() {
		
		this.mCityList = this.mCoolWeatherDB.loadCities(this.mCurProvince.getProvinceId());
		
		//如果数据库中有数据就从数据库中加载，如果没有就从服务器获取
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
	 * 查询county数据，优先从数据中查询，如果没有相关数据再从服务器上查询
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
	 * 根据传入的代号和类型从服务器上加载个province/city/county数据
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
					//通过runOnUiThread()方法回到主线程处理
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
				//通过runOnUiThread()方法回到主线程处理
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog() {
	
		if(null == this.mProgressDlg){
			this.mProgressDlg = new ProgressDialog(this);
			this.mProgressDlg.setMessage("正在加载....");
			this.mProgressDlg.setCanceledOnTouchOutside(false);
		}
		
		this.mProgressDlg.show();
	}
	
	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog() {
		if(null != this.mProgressDlg){
			this.mProgressDlg.dismiss();
		}
	}
}
