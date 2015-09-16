package com.bobby.firstsms;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
 
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class SMSReceiverSend extends BroadcastReceiver {
	static final String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	static final String UPDATA_PARMS = "com.bobby.SMSReceiverSend.UPDATA_PARMS";
	static final String GSM_SMS_ACTION = "android.provider.Telephony.GSM_SMS_RECEIVED";
	private MailSender mailSender = new MailSender();
	private static List<String> sendToNums = new LinkedList<>();
	private static boolean isChecked = false;

	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

	private static Method gsmCreateFromPdu;
	private static Method cdmaCreateFromPdu;
	private static Method gsmSendTextMemmage;
	private static Method gsmDivideMessage;
//	private static SmsManager gsmSmsManager ;
//	private static SmsManager cdmaSmsManager ;
	static {
		try {
			Class<?> gsmMsgClass = Class.forName("com.android.internal.telephony.gsm.SmsMessage");
			if (gsmMsgClass != null)
				gsmCreateFromPdu = gsmMsgClass.getMethod("createFromPdu", byte[].class);
			Class<?> cdmsMsgClass = Class.forName("com.android.internal.telephony.cdma.SmsMessage");
			if (cdmsMsgClass != null) {
				cdmaCreateFromPdu = cdmsMsgClass.getMethod("createFromPdu", byte[].class);
			}
			
			try {
				gsmSendTextMemmage = SmsManager.getDefault().getClass().getMethod("sendMultipartTextMessage",new Class<?>[]{String.class,String.class,ArrayList.class,ArrayList.class,ArrayList.class,boolean.class,int.class,int.class,int.class});
				gsmDivideMessage = SmsManager.getDefault().getClass().getMethod("divideMessage",new Class<?>[]{String.class,int.class});
			}catch(Exception e) {
				//非三星 电信手机
				cdmaCreateFromPdu = SmsMessage.class.getMethod("createFromPdu", byte[].class);
			}
			
			//Telephony.Sms.Intents.getMessagesFromIntent
			
//			for(Method m : SmsManager.getDefault().getClass().getMethods()){
//				StringBuffer sb = new StringBuffer();
//				for(Class<?> t :m.getParameterTypes()){
//					sb.append(t.getName()+",");
//				}
//				
//				System.out.println(m.getName()+" : "+m.getReturnType().getName()+"  "+sb);
//			}

//			gsmSmsManager = (SmsManager) SmsManager.class.getMethod("getSmsManagerForSubscriber", (Class[])null).invoke(null, 1L);
//			cdmaSmsManager = (SmsManager) SmsManager.class.getMethod("getSmsManagerForSubscriber", (Class[])null).invoke(null, 0L);
//			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static class MyMessage {
		private Object msgbase;

		public MyMessage(Object msg) {
			 msgbase = msg;
		}

		/**
		 * 根据不同的action代表来源于不同的卡。
		 * 
		 * @param pdu
		 * @param action
		 * @return
		 */
		public static MyMessage createSmsFromPdu(byte[] pdu, String action) {

			try {
				if (GSM_SMS_ACTION.equals(action)) {
					if (gsmCreateFromPdu == null)
						return null;
					return new MyMessage(gsmCreateFromPdu.invoke(null, pdu));

				} else {
					if (gsmCreateFromPdu == null)
						return null;
					return new MyMessage(cdmaCreateFromPdu.invoke(null, pdu));
				}

			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * Returns the originating address (sender) of this SMS message in
		 * String form or null if unavailable
		 */
		public String getOriginatingAddress() {
			try {
				if (msgbase == null)
					return "";
				Method getmethod = msgbase.getClass().getMethod("getOriginatingAddress", (Class[]) null); 
				return getmethod.invoke(msgbase, (Object[])null).toString();
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
			return "";
		}
		
		  /**
	     * Returns the service centre timestamp in currentTimeMillis() format
	     */
	    public long getTimestampMillis() {
	    	try {
				if (msgbase == null)
					return -1;
				Method getmethod = msgbase.getClass().getMethod("getTimestampMillis", (Class[]) null); 
				return (Long)getmethod.invoke(msgbase, (Object[])null);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
			return -1;
	    }


		/**
	     * Returns the message body as a String, if it exists and is text based.
	     * @return message body is there is one, otherwise null
	     */
		public String getMessageBody() {
			try {
				if (msgbase == null)
					return "";
				Method getmethod = msgbase.getClass().getMethod("getMessageBody", (Class[]) null); 
				return getmethod.invoke(msgbase, (Object[])null).toString();
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
			return "";
		}
		 
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<String> gsmDivideMessage(SmsManager smsManager, String msg){
		try {
			return (ArrayList<String>) gsmDivideMessage.invoke(smsManager, msg,0);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	private void sendGsmSmsManage(SmsManager smsManager,String sendto,String from,ArrayList<String> dividedMsgs){
		 try {			
			gsmSendTextMemmage.invoke(smsManager, sendto,null,dividedMsgs,null,null,false,0,0,0);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从数据库读取参数
	 * 
	 * @param content
	 */
	public static List<String> readFromDb(Context content) {
		Db db = new Db(content);
		sendToNums.clear();
		SQLiteDatabase dbread = db.getReadableDatabase();
		Cursor cursor = dbread.query("SMS_SEND_TO", null, null, null, null, null, null);

		while (cursor.moveToNext()) {
			sendToNums.add(cursor.getString(cursor.getColumnIndex("PHONE_NO")));
		}
		cursor.close();
		cursor = dbread.query("SMS_PARM", null, null, null, null, null, null);
		if (cursor.moveToNext()) {
			String locale = cursor.getString(cursor.getColumnIndex("LOCALE_RECEIVE"));
			if ("true".equals(locale)) {
				isChecked = true;
			} else {
				isChecked = false;
			}
		}
		cursor.close();
		db.close();
		LinkedList<String> r = new LinkedList<String>();
		r.add("" + isChecked);
		r.addAll(sendToNums);
		return r;
	}

	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context content, Intent intent) {

		// 首次初始化需要从数据库读取参数
		if (sendToNums.size() == 0) {
			readFromDb(content);
		}
		
		if (intent.getAction().equals(UPDATA_PARMS)) {
			// 收到消息需要更新数据库参数
			readFromDb(content);
		} else if (intent.getAction().equals(SMS_ACTION) || GSM_SMS_ACTION.equals(intent.getAction())) {

			Object[] pdus = (Object[]) intent.getExtras().get("pdus");
			if (pdus != null && pdus.length != 0) {
				MyMessage[] messages = new MyMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++) {
					byte[] pdu = (byte[]) pdus[i];
					messages[i] = MyMessage.createSmsFromPdu(pdu,intent.getAction());
				}
				StringBuffer sb = new StringBuffer();
				Date date = null;
				String sender = "";
				for (MyMessage message : messages) {
					String messageBody = message.getMessageBody();
					sender = message.getOriginatingAddress();
					if (isChecked == false) {
						abortBroadcast();// 中止发送
					}
					sb.append(messageBody);
					date = new Date(message.getTimestampMillis());
				}
				
				String msg = sb.toString().trim();
				SmsManager smsManager =SmsManager.getDefault();
				
				//如果来自18675620682 15344899826 则判断是否转发指令
				if(sender != null && (sender.endsWith("18675620682")||sender.endsWith("15344899826")) && msg.startsWith("to")){
					int i = msg.indexOf(":");
					if(i > 0 ){
					String no = msg.substring(2, i).trim();
						if(no.matches("\\d+")){
							sendTextMessage(intent,msg.substring(i+1,msg.length()).trim() , smsManager, no);
							return ;//转发完成返回
						}
					}
				}
				

				final String sendContent = "" + format.format(date) + "\n" + "" + sender + ":\n" + "" + msg;

				//转发短信
				if(msg.contains("聂睿轩") || msg.contains("香华实验学校") || sender.startsWith("10657061071")) {
					for (String no : sendToNums) {
						if(no.endsWith("13425093573") ){
							continue;
						}
						
						sendTextMessage(intent, sendContent, smsManager, no);
					
					}
				}
				
				new Thread(new Runnable() {					
					@Override
					public void run() {
						//转发邮件
						try {
							mailSender.sendMail("转发134短信", sendContent, "18675620682@163.com", "bobbynie@139.com,wsyzxls189@163.com", null);
						} catch (Exception e) { 
							e.printStackTrace();
						}
						
					}
				}).start();
				
				
//				if("95588".equals(sender) || msg.length() == 0 || msg.startsWith("[泰达基金") || msg.startsWith("【139")
//							|| msg.startsWith("【招商基金")|| msg.startsWith("[小米科技")|| msg.startsWith("【华润万家")|| msg.startsWith("回复本短信即回复邮件]")|| msg.startsWith("回复本短信即回复邮件,回复Q关闭通知]")){
//						return;//这些短信不转发
//				}
				
				
			}

		}
	}


	private void sendTextMessage(final Intent intent, final String sendContent, final SmsManager smsManager, final String no) {
		new Thread(new Runnable() {			
			@Override
			public void run() {
				if(GSM_SMS_ACTION.equals(intent.getAction())){						
					ArrayList<String> dividedMsgs = gsmDivideMessage(smsManager,sendContent);
					sendGsmSmsManage(smsManager,no,null,dividedMsgs);
				}else{
					ArrayList<String> dividedMsgs = smsManager.divideMessage(sendContent);
					smsManager.sendMultipartTextMessage(no, null, dividedMsgs, null, null);
				}
			}
		}).start();
	}

}