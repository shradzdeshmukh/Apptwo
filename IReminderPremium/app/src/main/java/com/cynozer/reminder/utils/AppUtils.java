package com.cynozer.reminder.utils;

import com.cyno.reminder.constants.StringConstants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AppUtils {
	
	public static boolean isSignedIn(Activity activity){
		SharedPreferences pref = activity.getSharedPreferences(StringConstants.PREFERENCE_PROFILE, activity.MODE_PRIVATE);
		return pref.getBoolean(StringConstants.KEY_USER_SIGNED_GPLUS, false);
	}

}
