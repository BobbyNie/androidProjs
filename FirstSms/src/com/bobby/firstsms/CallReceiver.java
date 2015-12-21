package com.bobby.firstsms;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class CallReceiver extends BroadcastReceiver {
    private static boolean incomingFlag = false;
    
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
//    private String incomingNumber;
    @Override
    public void onReceive(Context context, Intent intent) {
        //拨打电话
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            incomingFlag = false;
            //final String phoneNum = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
           // Log.d("PhoneReceiver", "phoneNum: " + phoneNum);
        } else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);
        }
    }
    final PhoneStateListener listener=new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state,final String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch(state){
                //电话等待接听
                case TelephonyManager.CALL_STATE_RINGING:
                    incomingFlag = true;
                    Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
		                    WeChartSender.sendWeChart(5, format.format(new Timestamp(System.currentTimeMillis()))+":\n"+ incomingNumber+ "  正在拨打134手机");							
						}
					});
                    t.start();
                    
                   // Log.i("PhoneReceiver", "CALL IN RINGING :" + incomingNumber);
                    break;
                //电话接听
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (incomingFlag) {
                    	t = new Thread(new Runnable() {
    						@Override
    						public void run() {
    							WeChartSender.sendWeChart(5, format.format(new Timestamp(System.currentTimeMillis()))+":\n"+incomingNumber+ "  接通电话");							
    						}
    					});
                        t.start();
                    	//Log.i("PhoneReceiver", "CALL IN ACCEPT :" + incomingNumber);
                    }
                    break;
                //电话挂机
                case TelephonyManager.CALL_STATE_IDLE:
                    if (incomingFlag) {
                    	t = new Thread(new Runnable() {
    						@Override
    						public void run() {
    							WeChartSender.sendWeChart(5, format.format(new Timestamp(System.currentTimeMillis()))+":\n"+ "  挂机");							
    						}
    					});
                        t.start();
                    	//Log.i("PhoneReceiver", "CALL IDLE");
                    }
                    break;
            }
        }
    };
}