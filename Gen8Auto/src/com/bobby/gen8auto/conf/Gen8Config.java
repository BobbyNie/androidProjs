package com.bobby.gen8auto.conf;

public class Gen8Config {

	private static String ip = "10.11.18.130";
	private static String port = "443";
	private static String username = "Administrator";
	private static String passwd = "1234_dcba";
	private static String autoConfig = "" + "on 12345 08:00:00" + "off 12345 08:00:00" + "on 12345 18:00:00" + "off 12345 23:00:00" + "on 6 05:00:00"
			+ "off 7 23:59:00";

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

	public static String getAutoConfig() {
		return autoConfig;
	}

	public static void setAutoConfig(String autoConfig) {
		Gen8Config.autoConfig = autoConfig;
	}

	public static String getPort() {
		return port;
	}
}
