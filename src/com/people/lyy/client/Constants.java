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

	public static final String ip = "http://211.147.87.20:8092/Vpm/";
	public static final String ipCash = "111.198.29.38:8891";
	public static final String DOWNLOADURL = "http://220.194.46.46:8080/zfb/mpos/transProcess.do?operationId=getVersion";
	// public static final String DOWNLOADURL =
	// "http://192.168.1.46:8080/zfb/mpos/transProcess.do?operationId=getVersion";

	public static final int OVERTIME = 20;// 超时时间

	public static boolean HASSETBLUETOOTH = false;
	public static int DEVICEMODEL = 0; // 蓝牙 0: POSManage.BLUETOOTHMODE, 音频 1:  POSManage.XAUDIOHMODE    

	// 上次签到日期 MMdd。一天签到一次
	public static String kPRESIGNDATE = "kPRESIGNDATE";

	public static final String kLOCKKEY = "LockKey";
	public static final String kFIRSTLOGIN = "FirstLogin";
	public static final String kGESTRUECLOSE = "GestureClose";

	public static final String ACTION_QPOS_CANCEL = "ACTION_QPOS_CANCEL";
	public static final String ACTION_QPOS_STARTSWIPE = "ACTION_QPOS_STARTSWIPE";
	public static final String ACTION_QPOS_SWIPEDONE = "ACTION_QPOS_SWIPEDONE";

	public static final String SIGNIMAGESPATH = APPFILEPATH + "/signImages/";
	public static final String kTRACEAUDITNUM = "kTRACEAUDITNUM";

	public static final String kPAGESIZE = "5";
	public static final String NEWAPP = "newApp";

	// BPUSH
	public static final String kBPUSH_APPID = "appid";
	public static final String kBPUSH_USERID = "userid";
	public static final String kBPUSH_CHANNELID = "channelid";

	public static final String MAC = "5391ea1af354f4fa9e8a084baa1a7bc9";
	public static final String PIN = "efe3d80d7dd858266e4fbb247333e884";
	public static String TCK = "00112233445566778899aabbccddeeff";
	
	public static final String TCKPreferences = "TCKPreferences";
	
	public static String APPTOKEN = "";
}
