package com.bobby.gen8auto.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.bobby.gen8auto.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {

	public static final String GEN8_START_SERVER = "com.bobby.gen8.GEN8_START_SERVER";


	public AlarmReceiver() {
		
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("收到广播"+intent.getAction());
		Intent it=new Intent(context,MainActivity.class);
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(it);
	}
	
	
	public static void setAlarm(Context context) {		
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent =new Intent(GEN8_START_SERVER);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		long firstime=SystemClock.elapsedRealtime();
		am.setRepeating(AlarmManager.RTC_WAKEUP, firstime, 6*1000, pendingIntent);
	}

}
