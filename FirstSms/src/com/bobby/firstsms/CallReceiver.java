package com.bobby.firstsms;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class CallReceiver extends BroadcastReceiver {
	private static boolean incomingFlag = false;

	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

	// private String incomingNumber;
	@Override
	public void onReceive(Context context, Intent intent) {

		// System.out.println( intent.get());
		// 拨打电话
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			incomingFlag = false;
			// final String phoneNum =
			// intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			// Log.d("PhoneReceiver", "phoneNum: " + phoneNum);
		} else {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
			// 获取卡来源 -- 天语手机
			final int subscription = intent.getIntExtra("subscription", 0);
			final String incomingNumber = intent.getStringExtra("incoming_number");
			switch (tm.getCallState()) {
			case TelephonyManager.CALL_STATE_RINGING:
				incomingFlag = true;// 标识当前是来电
				pool.execute(new Runnable() {
					@Override
					public void run() {
						try {
							// 134手机插入卡槽1 186手机插入插槽2 微信企业号 134手机id为 5
							// 186手机id为 6
							int appId = (subscription == 0 ? 5 : 6);
							WeChartSender.sendWeChart(appId, format.format(new Timestamp(System.currentTimeMillis())) + ":\n" + incomingNumber + "  正在拨打"+(subscription == 0? "186":"134"));
						} catch (Exception e) {
							// do nothing
						}
					}
				});
				break;

			case TelephonyManager.CALL_STATE_OFFHOOK:
				if (incomingFlag) {
					pool.execute(new Runnable() {
						@Override
						public void run() {
							try {
								// 134手机插入卡槽1 186手机插入插槽2 微信企业号 134手机id为 5
								// 186手机id为 6
								int appId = (subscription == 0 ? 5 : 6);
								WeChartSender.sendWeChart(appId, format.format(new Timestamp(System.currentTimeMillis())) + ":\n" + incomingNumber + "  接通电话");
							} catch (Exception e) {
								// do nothing
							}
						}
					});

				}
				break;

			case TelephonyManager.CALL_STATE_IDLE:
				if (incomingFlag) {
					pool.execute(new Runnable() {
						@Override
						public void run() {
							try {
								// 134手机插入卡槽1 186手机插入插槽2 微信企业号 134手机id为 5
								// 186手机id为 6
								int appId = (subscription == 0 ? 5 : 6);
								WeChartSender.sendWeChart(appId, format.format(new Timestamp(System.currentTimeMillis())) + ":\n" + incomingNumber + "  挂机");
							} catch (Exception e) {
								// do nothing
							}
						}
					});

				}

				break;

			}

		}
	}
	ExecutorService pool = Executors.newSingleThreadExecutor();
}