package com.bobby.firstbordcast;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;



public class MainActivity extends ActionBarActivity {

	private final MyBc myBC = new MyBc();
	private Button btnSendBroadCast,btnBcg,btnUnBcg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		btnSendBroadCast = (Button) findViewById(R.id.btnSendBroadCast);
		btnSendBroadCast.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//			sendBroadcast(new Intent(MainActivity.this,MyBc.class));				
				sendBroadcast(new Intent(MyBc.ACTION));				
			}
		});
		
		btnBcg = (Button)findViewById(R.id.btnRegBcg);
		
		btnBcg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				registerReceiver(myBC, new IntentFilter(MyBc.ACTION));
			}
		});
		btnUnBcg = (Button)findViewById(R.id.btnUnBcg);
		btnUnBcg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				unregisterReceiver(myBC);
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
