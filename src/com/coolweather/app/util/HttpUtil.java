package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	
	/**
	 * 向网络服务器发送Get请求
	 * @param address 服务器地址
	 * @param listener 回调函数
	 */
	public static void sendHttpRequest(final String address,
				final HttpCallbackListener listener){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection conn = null;
				
				URL url;
				try {
					url = new URL(address);
					conn = (HttpURLConnection) url.openConnection();
					
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(8000);
					conn.setReadTimeout(8000);
					
					InputStream in = conn.getInputStream();
					BufferedReader reader = new BufferedReader( new InputStreamReader(in));
					
					StringBuilder response = new StringBuilder();
					String line;
					
					while( null != (line = reader.readLine())){
						response.append(line);
					}
					
					if(null != listener){
						//回调onFinish()方法
						listener.onFinish(response.toString());
					}
					
				} catch (Exception e) {
					if(null != listener){
						//回调onError()方法
						listener.onError(e);
					}
					
				} finally {
					if(null != conn){
						conn.disconnect();
					}
				}
				
			}
		}).start();
	}

}
