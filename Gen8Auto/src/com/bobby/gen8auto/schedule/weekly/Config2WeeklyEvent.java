package com.bobby.gen8auto.schedule.weekly;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.bobby.gen8auto.schedule.AutoEvent;
import com.bobby.gen8auto.schedule.AutoEvent.EVENT_TYPE;

public class Config2WeeklyEvent {

	public Config2WeeklyEvent() {
	}
	
	public static List<AutoEvent> config2Event(String config){
		ArrayList<AutoEvent> r = new ArrayList<AutoEvent>();
		String[] arr = config.split("\\s");
		EVENT_TYPE et = EVENT_TYPE.valueOf(arr[0]);
		
		String weekstr = arr[1];
		String timestr = arr[2]; 
		arr = timestr.split(":");
		int hh = Integer.parseInt(arr[0]);
		int mm = Integer.parseInt(arr[1]);
		int ss = Integer.parseInt(arr[2]);
		 
		Calendar calendar = Calendar.getInstance();
		for(int i = 0;i<weekstr.length();i++) {
			int week = weekstr.charAt(i) - '1';
			int currweekday = calendar.get(Calendar.WEDNESDAY);
			
			int distDay = week - currweekday;
			if(distDay < 0) {
				distDay += 7;
			}
			
			Calendar firstc = (Calendar) calendar.clone();
			firstc.setTimeInMillis(firstc.getTimeInMillis() + distDay * 24*60*60* 1000);
			firstc.set(Calendar.HOUR_OF_DAY, hh);
			firstc.set(Calendar.MINUTE, mm);
			firstc.set(Calendar.SECOND, ss);
			firstc.set(Calendar.MILLISECOND, 00);
			r.add(new AutoEvent(et, firstc.getTimeInMillis()));
		}		
		return r;
	}

}
