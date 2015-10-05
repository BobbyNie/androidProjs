package com.bobby.gen8auto;
 
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.bobby.gen8auto.http.Gen8RestFullPower;

public class MainActivity extends Activity {
	private Button shutDownBt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		shutDownBt = (Button) findViewById(R.id.shutDownBtn);
		shutDownBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Toast.makeText(MainActivity.this, "停止中...", Toast.LENGTH_LONG).show();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						boolean btnR = Gen8RestFullPower.ShutDown();
						sendMsg(btnR ? "停止服务器成功！":"停止服务器失败！");
					}

				}).start();
			}
		});
	}

	/**
	 * 发送提示消息
	 * @param string
	 */
	private void sendMsg(String msgStr) {
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("msg", msgStr);
		msg.setData(data );
		msgHandler.sendMessage(msg);
	}
	
	private Handler msgHandler = new MsgHander();
	
	private class MsgHander extends Handler{
		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(MainActivity.this.getApplicationContext(), msg.getData().getString("msg"), Toast.LENGTH_LONG).show();
		}
	};

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
