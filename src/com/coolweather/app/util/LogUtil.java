package com.coolweather.app.util;

import android.util.Log;

public class LogUtil {
	
	public static final int LOG_VERBOSE = 1;
	public static final int LOG_DEBUG 	= 2;
	public static final int LOG_INFO 	= 3;
	public static final int LOG_WARN 	= 4;
	public static final int LOG_ERROR	= 5;
	public static final int LOG_NOTHING = 6;
	
	public static final int LEVEL = LOG_VERBOSE;
	
	public static void v(String tag, String msg){
		if(LEVEL <= LOG_VERBOSE){
			Log.v(tag, msg);
		}
	}
	
	public static void d(String tag, String msg){
		if(LEVEL <= LOG_DEBUG){
			Log.d(tag, msg);
		}
	}
	
	public static void i(String tag, String msg){
		if(LEVEL <= LOG_INFO){
			Log.i(tag, msg);
		}
	}
	
	public static void w(String tag, String msg){
		if(LEVEL <= LOG_WARN){
			Log.w(tag, msg);
		}
	}
	
	public static void e(String tag, String msg){
		if(LEVEL <= LOG_ERROR){
			Log.e(tag, msg);
		}
	}

}
