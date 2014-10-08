package com.people.lyy.client;

public class Constants {

	// 当前系统的版本号
	public static final int VERSION = 1;

	public static final String AESKEY = "dynamicode";

	public static final String APPFILEPATH = "/data/data/"
			+ ApplicationEnvironment.getInstance().getApplication()
					.getPackageName();

	// assets下的文件保存路径
	public static final String ASSETSPATH = APPFILEPATH + "/assets/";

	public static final String kUSERNAME = "kUSERNAME";
	public static final String kPASSWORD = "kPASSWORD";

	public static String kVERSION = "VERSION";

	public static final String IP = "http://111.198.29.38:6443";

	public static final int OVERTIME = 20;// 超时时间

	public static boolean HASSETBLUETOOTH = false;

	public static final String kLOCKKEY = "LockKey";
	public static final String kGESTRUECLOSE = "GestureClose";

	public static String LOGGED = "Logged";

	public static String SOTPPACKET = "com.people.sotp.service";

}
