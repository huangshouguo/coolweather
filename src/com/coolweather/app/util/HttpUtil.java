package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	
	/**
	 * ���������������Get����
	 * @param address ��������ַ
	 * @param listener �ص�����
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
						//�ص�onFinish()����
						listener.onFinish(response.toString());
					}
					
				} catch (Exception e) {
					if(null != listener){
						//�ص�onError()����
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
