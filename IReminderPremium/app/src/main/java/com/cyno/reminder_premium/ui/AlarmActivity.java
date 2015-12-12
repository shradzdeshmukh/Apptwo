package com.cyno.reminder_premium.ui;

import com.cyno.reminder.constants.IntentKeyConstants;
import com.cyno.reminder_premium.R;
//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;



import com.cyno.reminder_premium.ui.IReminder.TrackerName;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public class AlarmActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Window window = getWindow();
		window.requestFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		if(!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_alarm), true)){
			finish();
			return;
		}
		
		Intent mIntent = getIntent();
		FragmentAlarm frag = new FragmentAlarm (String.valueOf(mIntent.getIntExtra(IntentKeyConstants.KEY_TASK_ID, 0)));
		frag.show(getSupportFragmentManager(), FragmentAlarm.class.getSimpleName());
		
//		GoogleAnalytics.getInstance(getBaseContext()).dispatchLocalHits();
//		Tracker t = ((IReminder)getApplication()).getTracker(TrackerName.APP_TRACKER);
//		t.setScreenName("Alarm activity");
//		t.send(new HitBuilders.AppViewBuilder().build());

	}
	
	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
}
