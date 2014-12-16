package com.bobby.firstsms;

import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	private EditText et;
	private Button saveBt;
	private ToggleButton toBt;
	private boolean isChecked = false;
 	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		et = (EditText) findViewById(R.id.sendToId);
		toBt = (ToggleButton) findViewById(R.id.togId); 
		
		toBt.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				MainActivity.this.isChecked = isChecked;
			}
		});
		saveBt = (Button) findViewById(R.id.saveBtId);
		saveBt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(SMSReceiverSend.UPDATA_PARMS);
				String sendToNum = et.getText().toString();
				
				if(sendToNum != null){
					Db db = new Db(MainActivity.this);
					SQLiteDatabase dbWrite = db.getWritableDatabase();
					dbWrite.delete("SMS_SEND_TO", null, null);
					dbWrite.delete("SMS_PARM", null, null);
					
					ContentValues cv;
					String[] tmpArr = sendToNum.split(",");
					for(String no :tmpArr){
						if(no != null && !no.equals("")){
							cv = new ContentValues();
							cv.put("PHONE_NO", no);
							dbWrite.insert("SMS_SEND_TO", "", cv);
						}
					}
					cv = new ContentValues();
					cv.put("LOCALE_RECEIVE", isChecked?"true":"false");
					dbWrite.insert("SMS_PARM", "", cv);
					db.close();
				}		
				Toast.makeText(MainActivity.this, "已成功绑定", Toast.LENGTH_SHORT).show();
				sendBroadcast(intent);
				
			}
		});
		List<String> parms = SMSReceiverSend.readFromDb(this);
		String checkedStr = parms.remove(0);
		StringBuffer sb = new StringBuffer();
		for(String no: parms){
			sb.append(no);
			sb.append(",");
		}
		et.setText(sb.toString());
		toBt.setChecked("true".equals(checkedStr));

	}

}