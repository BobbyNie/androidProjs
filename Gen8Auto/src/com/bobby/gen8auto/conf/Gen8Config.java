package com.bobby.gen8auto.conf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bobby.gen8auto.schedule.AutoEvent;
import com.bobby.gen8auto.schedule.weekly.Config2WeeklyEvent;

public class Gen8Config {

	private static String ip = "10.11.18.130";
	private static String port = "443";
	private static String username = "Administrator";
	private static String passwd = "1234_dcba";

	private static String mailUser = "18675620682@163.com";
	private static String mailPassword = "1234_dcba";
	private static String mailhost = "smtp.163.com";
	private static String mailToUsers = "bobbynie@139.com,wsyzxls189@163.com";

	private static List<String> autoManageList = Arrays.asList(new String[] {
			"ON 12345 18:00:00",
			"OFF 12345 23:00:00", 
			"ON 6 05:00:00", 
			"OFF 7 23:59:00" });

	public static String getMailUser() {
		return mailUser;
	}

	public static String getMailPassword() {
		return mailPassword;
	}

	public static String getMailhost() {
		return mailhost;
	}

	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		Gen8Config.username = username;
	}

	public static String getIp() {
		return ip;
	}

	public static void setIp(String ip) {
		Gen8Config.ip = ip;
	}

	public static String getPasswd() {
		return passwd;
	}

	public static void setPasswd(String passwd) {
		Gen8Config.passwd = passwd;
	}
 

	public static String getPort() {
		return port;
	}

	public static String getMailToUsers() {
		return mailToUsers;
	}

	private static  List<AutoEvent>  autoEvents = null;
	public static List<AutoEvent> getAutoManageList() {
		if(autoEvents == null) {
			autoEvents = new ArrayList<>();
			for(String config:autoManageList) {
				autoEvents.addAll(Config2WeeklyEvent.config2Event(config));
			};
		}
		return autoEvents;
	}
 
}
