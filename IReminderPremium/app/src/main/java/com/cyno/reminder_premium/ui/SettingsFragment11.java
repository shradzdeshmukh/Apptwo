package com.cyno.reminder_premium.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.RingtonePreference;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cyno.reminder.preference.PreferenceFragment;
import com.cyno.reminder_premium.R;

@SuppressLint("NewApi")
public class SettingsFragment11 extends android.preference.PreferenceFragment implements OnTimeSetListener {

	private Preference mPreference;
	private int hour;
	private int min;

	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		//		GoogleAnalytics.getInstance(getActivity().getBaseContext()).dispatchLocalHits();
		//		Tracker t = ((IReminder)getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
		//		t.setScreenName("Settings fragment");
		//		t.send(new HitBuilders.AppViewBuilder().build());

		addPreferencesFromResource(R.xml.settings);

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		RingtonePreference pref = (RingtonePreference) findPreference(getString(R.string.key_ringtone));
		pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Editor mEdit = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
				mEdit.putString(getString(R.string.key_ringtone), newValue.toString());
				mEdit.commit();
				return false;
			}
		});

		mPreference = (Preference) findPreference(getString(R.string.key_morning_alarm_time));
		setTime();
		mPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				TimePickerDialog mDialog = new TimePickerDialog(getActivity(), SettingsFragment11.this, hour, min, false);
				mDialog.show();
				return false;
			}

		});

	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
		editor.putInt(getString(R.string.key_daily_alarm_hour),hourOfDay);
		editor.putInt(getString(R.string.key_daily_alarm_min),minute);
		editor.commit();
		setTime();
		MainActivity.setDailyAlarm(getActivity());
	}

	private void setTime(){
		hour = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(getString(R.string.key_daily_alarm_hour),9);
		min = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(getString(R.string.key_daily_alarm_min),0);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, min);
		SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm" , Locale.getDefault());

		mPreference.setTitle(getString(R.string.daily_alarm_title));
		mPreference.setSummary(mFormat.format(cal.getTime()));
	}

	@Override
	public void onStop() {
		super.onStop();
		//		((MainActivity)getActivity()).showFab();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
}
