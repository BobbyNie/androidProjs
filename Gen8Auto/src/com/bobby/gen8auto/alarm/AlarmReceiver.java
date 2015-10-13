package com.bobby.gen8auto.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bobby.gen8auto.conf.Gen8Config;
import com.bobby.gen8auto.http.Gen8RestFullPower;
import com.bobby.gen8auto.schedule.AutoEvent;
import com.bobby.gen8auto.schedule.AutoEvent.EVENT_TYPE;

public class AlarmReceiver extends BroadcastReceiver {

	public static final String GEN8_ON = "com.bobby.gen8.GEN8_START_SERVER";
	private static final String GEN8_OFF = "com.bobby.gen8.GEN8_START_SHUTDOWN";

	public AlarmReceiver() {
		
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if(GEN8_ON.equals(intent.getAction())){
			Gen8RestFullPower.startUp();
		}
		
		if(GEN8_OFF.equals(intent.getAction())){
			Gen8RestFullPower.ShutDown();
		}
		
//		Intent it=new Intent(context,MainActivity.class);
//		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		context.startActivity(it);
//		
	}
	
	
	public static void setAlarm(Context context) {		

		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent on = PendingIntent.getBroadcast(context, 0, new Intent(GEN8_ON), 0);
		PendingIntent off = PendingIntent.getBroadcast(context, 0, new Intent(GEN8_OFF), 0);
		for(AutoEvent autoEvent : Gen8Config.getAutoManageList()) {
			am.setRepeating(AlarmManager.RTC_WAKEUP, autoEvent.getFirstTime(), 7*24*60*60*1000, autoEvent.getType() == EVENT_TYPE.ON ? on:off);
		}
	}

}
