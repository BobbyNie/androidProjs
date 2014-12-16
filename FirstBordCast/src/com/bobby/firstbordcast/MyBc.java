package com.bobby.firstbordcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBc extends BroadcastReceiver {

	public static final String ACTION = "com.bobby.firstbordcast.action.MyBC";
	public MyBc() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("onreceive");
	}

}
