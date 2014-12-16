package com.bobby.firstservice;

import com.bobby.firstservice.EchoService.EchoSericeBinder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract.Contacts;
import android.provider.SyncStateContract.Constants;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	private Button btnStart, btnStop,btnBind,btnUnBind,btnUpdate;
	private TextView serviceCountView;
	
	private Intent serviceIntent;
	private EchoSericeBinder serviceBinder = null;

	public ServiceConnection servConn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			System.out.println("unbind Service");
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			serviceBinder = (EchoSericeBinder)service;
			System.out.println("bind Service");
		}
	};

	class ClickListerer implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnStartService:
				startService(serviceIntent);
				
				break;
			case R.id.btnStopService:
				stopService(serviceIntent);
				break;
			case R.id.btnBand:
				
				bindService(serviceIntent, servConn , Context.BIND_AUTO_CREATE);
				break;
			case R.id.btnUnBind:				
				unbindService(servConn);
				serviceBinder = null;
				break;
			case R.id.btnUpdate:
				if(serviceBinder  != null){
					serviceCountView.setText("count:"+serviceBinder.getService().getTimeCount());
				}
				break;
			default:
				break;
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnStart = (Button) findViewById(R.id.btnStartService);
		btnStop = (Button) findViewById(R.id.btnStopService);
		btnBind = (Button) findViewById(R.id.btnBand);
		btnUnBind = (Button) findViewById(R.id.btnUnBind);
		btnUpdate = (Button) findViewById(R.id.btnUpdate);
		
		OnClickListener onClickListener = new ClickListerer();
		btnStart.setOnClickListener(onClickListener);
		btnStop.setOnClickListener(onClickListener);
		btnBind.setOnClickListener(onClickListener);
		btnUnBind.setOnClickListener(onClickListener);
		btnUpdate.setOnClickListener(onClickListener);
		
		
		serviceIntent = new Intent(this,EchoService.class);
		
		serviceCountView = (TextView) findViewById(R.id.txtServiceCount);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
