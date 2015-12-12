package com.cyno.reminder_premium.service;


import com.cyno.reminder.constants.IntentKeyConstants;
import com.cyno.reminder.constants.StringConstants;
import com.cyno.reminder.recievers.AlarmReciever;
import com.cynozer.reminder.utils.Task;
import com.cynozer.reminder_premium.contentproviders.TasksTable;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.util.Log;

public class PremiumAlarmService extends IntentService {

	public static final String CREATE = "create";
	public static final String CANCEL = "cancel";
	public static final String ACTION_DAILY_MORNING_ALARM = "daily";
	public static final String DAILY_ALARM_KEY = "key";
	public static final String KEY_TIME = "time";
	public static final int KEY_ID = 100000;
	public IntentFilter matcher;


	public PremiumAlarmService() {
		super("some name");
		matcher = new IntentFilter();
		matcher.addAction(CREATE);
		matcher.addAction(CANCEL);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("alarm", "alarm service called");
		String action = intent.getAction();
		if(action.equals(ACTION_DAILY_MORNING_ALARM)){
			showDailyAlarmNotif(intent.getLongExtra(KEY_TIME, 0) );
		}else if(matcher.matchAction(action)){
			execute(action);
		}
	}

	private void showDailyAlarmNotif(long time) {

		Intent mIntent = new Intent(this,AlarmReciever.class);
		mIntent .setAction(ACTION_DAILY_MORNING_ALARM);
		mIntent.putExtra(StringConstants.ALARM_SERVICE_KEY, KEY_ID);
		PendingIntent mPendingIntent = PendingIntent.getBroadcast(this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager mManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Log.d("alaarm","from ser "+String.valueOf(time));
		mManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+2000, mPendingIntent);
	}

	private void execute(String action) {
		AlarmManager mManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		String sSelection = TasksTable.COL_TASK_DATE  +" > ? AND "+TasksTable.COL_TASK_STATUS + " = ? ";
		String []arSelectionArgs = new String[]{String.valueOf(System.currentTimeMillis()) , String.valueOf(Task.STATUS_PENDING)};
		Cursor mCursor = getContentResolver().query(TasksTable.CONTENT_URI, null , sSelection	, arSelectionArgs, TasksTable.COL_TASK_DATE );
		if(mCursor != null){
			if(mCursor.moveToFirst()){
				Intent mIntent = new Intent(this,AlarmReciever.class);
				mIntent.setAction(StringConstants.NOTIF_ACTION);
				mIntent.putExtra(StringConstants.ALARM_SERVICE_KEY, mCursor.getInt(mCursor.getColumnIndex(TasksTable.COL_TASK_ID)));
				mIntent.putExtra(IntentKeyConstants.KEY_TYPE_OF_ALARM, mCursor.getInt(mCursor.getColumnIndex(TasksTable.COL_TASK_CATEGORY_UID)));
				mIntent.putExtra(IntentKeyConstants.KEY_TASK_ID, mCursor.getInt(mCursor.getColumnIndex(TasksTable.COL_TASK_ID)));
				PendingIntent mPendingIntent = PendingIntent.getBroadcast(this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				Long lTime = Long.parseLong(mCursor.getString( mCursor.getColumnIndex(TasksTable.COL_TASK_DATE)));

				if(action.equals(CREATE)){
					mManager.set(AlarmManager.RTC_WAKEUP, lTime, mPendingIntent);
				}else if(action.equals(CANCEL)){
					mManager.cancel(mPendingIntent);
				}
			}
			mCursor.close();
		}
	}
}
