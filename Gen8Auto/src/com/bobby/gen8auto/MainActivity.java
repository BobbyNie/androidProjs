package com.bobby.gen8auto;
 
import android.app.Activity;
import android.os.Bundle;
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

				new Thread(new Runnable() {
					@Override
					public void run() {
						showMsg( "停止中...");
						boolean btnR = Gen8RestFullPower.ShutDown();
						showMsg(btnR ? "停止服务器成功！":"停止服务器失败！");
					}

				}).start();
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
