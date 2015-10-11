package com.bobby.gen8auto;
 
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bobby.gen8auto.alarm.AlarmReceiver;
import com.bobby.gen8auto.http.Gen8RestFullPower;

public class MainActivity extends Activity {
	private static final ThreadPoolExecutor pool =new ThreadPoolExecutor(0, 20, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	private Button shutDownBtn;
	
	private Button startUpBtn;
	private Button saveAutoBtn;
	private Button flashBtn;
	private TextView stateText ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		stateText = (TextView) findViewById(R.id.stateView);
		stateText.setText("UNKNOWN");
		
		shutDownBtn = (Button) findViewById(R.id.shutDownBtn);
		shutDownBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shutDownBtn.setClickable(false);
				startUpBtn.setClickable(false);
				pool.submit(new Runnable() {
					@Override
					public void run() {
						try {
							showMsg( "停止中...");
							boolean btnR = Gen8RestFullPower.ShutDown();
							setStateText(btnR ?"Off":"On");	
							showMsg(btnR ? "停止服务器成功！":"停止服务器失败！");
						}finally {
							shutDownBtn.setClickable(true);
							startUpBtn.setClickable(true);
						}
					}

				});
			}
		});
		
		startUpBtn  = (Button) findViewById(R.id.startUpBtn);
		startUpBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shutDownBtn.setClickable(false);
				startUpBtn.setClickable(false);
				pool.submit(new Runnable() {
					@Override
					public void run() {
						try {
							showMsg( "停止中...");
							boolean btnR = Gen8RestFullPower.startUp();
							showMsg(btnR ? "启动服务器成功！":"启动服务器失败！");
							setStateText(btnR ?"On":"Off");	
						}finally {
							shutDownBtn.setClickable(true);
							startUpBtn.setClickable(true);
						}
					}

				});
			}
		});
		
		flashBtn  = (Button) findViewById(R.id.flashBtn);
		flashBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				flashBtn.setClickable(false);
				pool.submit(new Runnable() {
					@Override
					public void run() {
						try {
							showMsg("开始更新状态");
							final String state = Gen8RestFullPower.getPowerState();
							setStateText(state);	
							showMsg((state !=null && state.startsWith("error")) ? "更新状态失败！":"更新状态成功！");
						}finally {
							flashBtn.setClickable(true);
						}
					}


				});
			}
		});
		
		saveAutoBtn = (Button) findViewById(R.id.saveAuto);
		saveAutoBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveAutoBtn.setClickable(false);
				pool.submit(new Runnable() {
					@Override
					public void run() {
						try {
							showMsg("开始更新自动启停信息");
							AlarmReceiver.setAlarm(MainActivity.this);
							showMsg("自动启停信息成功！");
						}finally {
							saveAutoBtn.setClickable(true);
						}
					}


				});
			}
		});
	}
	

	private void setStateText(final String state) {
		runOnUiThread(new Runnable() {							
			@Override
			public void run() {
				stateText.setText(state);								
			}
		});
	}

	/**
	 * 发送提示消息
	 * @param string
	 */
	private void showMsg(final String msg) {
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
			}
		});
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
