package com.bobby.gen8auto.schedule;

public class AutoEvent extends Object {
	public static enum EVENT_TYPE{
		ON,
		OFF
	}
	
	private final EVENT_TYPE type;
	private final long firstTime;

	public AutoEvent( EVENT_TYPE type,long firstTime) {
		this.type = type;
		this.firstTime = firstTime;
	}

	public EVENT_TYPE getType() {
		return type;
	}

	public long getFirstTime() {
		return firstTime;
	}

}
