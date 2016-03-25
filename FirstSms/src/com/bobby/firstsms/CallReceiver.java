package com.bobby.firstsms;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class CallReceiver extends BroadcastReceiver {
	//private static boolean incomingFlag = false;

	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

	// private String incomingNumber;
	@Override
	public void onReceive(Context context, Intent intent) {
			// 获取卡来源 -- 天语手机
		final int subscription = intent.getIntExtra("subscription", 0);
		final int appId = (subscription == 0 ? 6 : 5);
		final String phone = (subscription == 0 ? "186":"134");
		//String telephonyServiceName = (subscription == 0 ? "phone":"phone_msim");
		
		//TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
		final String incomingNumber = intent.getStringExtra("incoming_number");
		
		 // 如果是来电
        Bundle bundler = intent.getExtras();
        // 可以Debug看下这个bundler里面的数据，就明白为什么我找到了
        final String ring_state = bundler.getString("state");
        //String number = bundler.getString("incoming_number");
        
    	pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					// 134手机插入卡槽1 186手机插入插槽2 微信企业号 134手机id为 5
					// 186手机id为 6
					WeChartSender.sendWeChart(appId, format.format(new Timestamp(System.currentTimeMillis())) + ":\n" + incomingNumber+" " +ring_state + " on "+phone);
				} catch (Exception e) {
					// do nothing
				}
			}
		}); 

	}
	ExecutorService pool = Executors.newSingleThreadExecutor();
}