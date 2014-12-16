package com.bobby.firstandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Aty1 extends Activity {

	public Aty1() {

		
	}

	private Button btnClose;
	
	private TextView tvAty1Edit1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layoutaty1);
		btnClose = (Button) findViewById(R.id.btnCloseAty1);
		btnClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = Aty1.this.getIntent();				
				i.putExtra("txt", tvAty1Edit1.getText().toString());
				Aty1.this.setResult(456, i);
				
				Aty1.this.finish();
			}
		});
		
		tvAty1Edit1 = (TextView) findViewById(R.id.aty1TextEdit1);
		
		String str = getIntent().getStringExtra("txt");
		
		tvAty1Edit1.getEditableText().append(str);
		
	}
}
