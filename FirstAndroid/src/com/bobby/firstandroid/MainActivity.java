package com.bobby.firstandroid;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	private TextView titleView ;
	private Button btnStartAty1 ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnStartAty1 = (Button)findViewById(R.id.btnStartAty1); 
		
		btnStartAty1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.out.println(v.getDrawingTime());
				
				Intent intent = new Intent(MainActivity.this, Aty1.class);
				
				intent.putExtra("txt", "test value from main active");
				
				startActivityForResult(intent, 123);
			}
		});
		
		
		titleView = (TextView) findViewById(R.id.textView1);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 123){
			String txt = data.getStringExtra("txt");
			
			titleView.setText(txt);
		}
		
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
