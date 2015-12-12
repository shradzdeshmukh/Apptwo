//package com.cynozer.reminder.utils;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.SharedPreferences;
//
//public class PreferenceHelper {
//
//	public static final int PREFERENCE_PROFILE = 1;
//	private final static String PREFERENCE_PROFILE_NAME = "PROFILE";
//	public static final String KEY_NAME="name";
//	public static final String KEY_AGE="age";
//	public static final String KEY_LOCATION="loc";
//	public static final String KEY_LOCALE="lc";
//	public static final String KEY_PROFILE_IMG="prf_img";
//	public static final String KEY_COVER_IMG="cvr_img";
//	public static final String KEY_GENDER="gndr";
//
//	public static void putPreference(Context context , int Preference , Object value , String key){
//
//		switch (Preference) {
//		case PREFERENCE_PROFILE:
//			SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_PROFILE_NAME, Activity.MODE_PRIVATE).edit();
//			if(value instanceof String)
//				editor.putString(key, (String)value);
//			else if(value instanceof Integer)
//				editor.putInt(key, (Integer)value);
//			else if(value instanceof Boolean)
//				editor.putBoolean(key, (Boolean)value);
//			
//			editor.commit();
//			break;
//
//		default:
//			break;
//		}
//	}
//	
//	public static Boolean getPreference(Context context , int Preference, String key){
//		switch (Preference) {
//		case PREFERENCE_PROFILE:
//			SharedPreferences pref = context.getSharedPreferences(PREFERENCE_PROFILE_NAME, Activity.MODE_PRIVATE);
//			return pref.getBoolean(key, false);
//		}
//		return false;
//	}
//	
//	public static int getPreference(Context context , int Preference, String key){
//		switch (Preference) {
//		case PREFERENCE_PROFILE:
//			SharedPreferences pref = context.getSharedPreferences(PREFERENCE_PROFILE_NAME, Activity.MODE_PRIVATE);
//			return pref.getBoolean(key, false);
//		}
//		return false;
//	}
//
//	public static String getPreference(Context context , int Preference, String key){
//		switch (Preference) {
//		case PREFERENCE_PROFILE:
//			SharedPreferences pref = context.getSharedPreferences(PREFERENCE_PROFILE_NAME, Activity.MODE_PRIVATE);
//			return pref.getString(key, "");
//		}
//		return "";
//	}
//
//
//}
