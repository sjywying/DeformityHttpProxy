package com.cfun.proxy;

import android.content.Context;
import android.content.SharedPreferences;


public class Config 
{
	public static boolean isProxyServer;
	public static boolean isBeforeURL;
	public static boolean isReplaceURL;
	public static boolean isSuffix;
	public static boolean isReplaceProtectVersion;
	public static boolean isInsertHost;
	public static boolean isReplaceHost;
	public static boolean isReplaceAccept;
	public static boolean isInsertX;
	public static boolean isXOnlineHost;
	public static boolean isCustom;
	public static boolean isReplaceConnection;
	
	public static String proxyServer;
	public static String beforeURL;
	public static String replaceURL;
	public static String suffix;
	public static String replaceProtectVersion;
	public static String insertHost;
	public static String replaceHost;
	public static String replaceAccept;
	public static String insertX;
	public static String xOnlineHost;
	public static String custom;
	public static String replaceConnection;
	public static void refresh(Context context)
	{
		SharedPreferences pres = context.getSharedPreferences("com.cfun.proxy_preferences",android.content.Context.MODE_PRIVATE);
		isProxyServer 					= pres.getBoolean("isProxyServer", false);
		isBeforeURL 						= pres.getBoolean("isBeforeURL", false);
		isReplaceURL 					= pres.getBoolean("isReplaceURL", false);
		isSuffix 							= pres.getBoolean("isSuffix", false);
		isReplaceProtectVersion 	= pres.getBoolean("isReplaceProtectVersion", false);
		isInsertHost 						= pres.getBoolean("isInsertHost", false);
		isReplaceHost 					= pres.getBoolean("isReplaceHost", false);
		isReplaceAccept				= pres.getBoolean("isReplaceAccept", false);
		isInsertX 							= pres.getBoolean("isInsertX", false);
		isXOnlineHost 					= pres.getBoolean("isXOnlineHost", false);
		isCustom 							= pres.getBoolean("isCustom", false);
		isReplaceConnection 		= pres.getBoolean("isReplaceConnection", false);
		
		proxyServer						=pres.getString("proxyServer", "");
		beforeURL						=pres.getString("beforeURL", "");
		replaceURL						=pres.getString("replaceURL", "");
		suffix								=pres.getString("suffix", "");
		replaceProtectVersion		=pres.getString("replaceProtectVersion", "");
		insertHost							=pres.getString("insertHost", "");
		replaceHost						=pres.getString("replaceHost", "");
		replaceAccept					=pres.getString("replaceAccept", "");
		insertX								=pres.getString("insertX", "");
		xOnlineHost						=pres.getString("xOnlineHost", "");
		custom								=pres.getString("custom", "");
		replaceConnection			=pres.getString("replaceConnection", "close");
	}
}
