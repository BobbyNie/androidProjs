package com.bobby.firstservice;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class EchoService extends Service {

	public EchoService() {
	}

	final EchoSericeBinder binder = new EchoSericeBinder();
	
	public class EchoSericeBinder extends Binder{
		public EchoService getService(){
			return EchoService.this;
		}
	}
	
	
	
	private Timer timer;
	private TimerTask task;
	private int timeCount = 0;
	public void startTimer(){
		
		if(timer == null){
			timer = new Timer();			
			task = new TimerTask() {
				@Override
				public void run() {
					timeCount+= 1;
					System.out.println("timer: "+getTimeCount());
				}
			};
			
			timer.schedule(task, 1000,1000);
		}
	}
	
	public void stopTimer(){
		if(timer !=null){
			task.cancel();
			timer.cancel();
			timer = null;
			task = null;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		System.out.println("Echo bind");
		return binder;
	}
	
	@Override
	public void onCreate() {
		System.out.println("createed");
		
		startTimer();
		
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		System.out.println("destroied");
		stopTimer();
		super.onDestroy();
	}

	public int getTimeCount() {
		return timeCount;
	}
 
}
