package com.bobby.gen8auto.boot;

import com.bobby.gen8auto.alarm.AlarmReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
	private static final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";

	public void onReceive(Context context, Intent i) {
		System.out.println("boot:"+i.getAction());
		
		if (i.getAction().equals(BOOT_ACTION)) {
			AlarmReceiver.setAlarm(context);
		}
	}
}