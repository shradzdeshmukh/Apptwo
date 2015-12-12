package com.cyno.reminder.recievers;


import com.cyno.reminder.constants.IntentKeyConstants;
import com.cyno.reminder.constants.StringConstants;
import com.cyno.reminder_premium.R;
import com.cyno.reminder_premium.service.PremiumAlarmService;
import com.cyno.reminder_premium.ui.AlarmActivity;
import com.cyno.reminder_premium.ui.MainActivity;
import com.cyno.reminder_premium.ui.PlanDayActivity;
import com.cynozer.reminder_premium.contentproviders.TasksTable;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlarmReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(StringConstants.NOTIF_ACTION)){
			showNormalNotif(context, intent);
		}else{
			showDailyNotif(context, intent);
		}
	}


	private void showDailyNotif(Context context, Intent intent) {
		int id = intent.getIntExtra(StringConstants.ALARM_SERVICE_KEY, 0);
		Intent oIntent = new Intent(context,PlanDayActivity.class);
		oIntent.setAction(intent.getAction());
		PendingIntent mPendingIntent = PendingIntent.getActivity(context, id, oIntent, 0);
		Notification noti = new NotificationCompat.Builder(context)
		.setContentTitle(context.getString(R.string.plan_day_title))
		.setContentText(context.getString(R.string.plan_day_msg))
		.setSmallIcon(R.drawable.ic_notification)
		.setAutoCancel(true)
		.setSound(this.getAlarmTone(context))
		.setContentIntent(mPendingIntent)
		.build();

		if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_sound), true)){
			noti.defaults |= Notification.DEFAULT_SOUND;
		}
		if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_vibrate), true))
			noti.defaults |= Notification.DEFAULT_VIBRATE;
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_alarm), true)
				&& PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_notification), true)
				&& PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_morning_alarm), true)

				)
			notificationManager.notify(id, noti); 
	}


	private void showNormalNotif(Context context, Intent intent){

		int id = intent.getIntExtra(StringConstants.ALARM_SERVICE_KEY, 0);
		Intent oIntent = new Intent(context,MainActivity.class);
		oIntent.setAction(StringConstants.NOTIF_ACTION);
		oIntent.putExtra(IntentKeyConstants.KEY_TYPE_OF_ALARM, intent.getIntExtra(IntentKeyConstants.KEY_TYPE_OF_ALARM, 0));

		PendingIntent mPendingIntent = PendingIntent.getActivity(context, id, oIntent, 0);


		Cursor mCursor = context.getContentResolver().query(TasksTable.CONTENT_URI, null, TasksTable.COL_TASK_ID + " = ?", new String[]{String.valueOf(id)}, null);
		Uri alarm = getAlarmTone(context);
		if(mCursor != null){
			if(mCursor.moveToNext()){
				Notification noti = new NotificationCompat.Builder(context)
				.setContentTitle(context.getString(R.string.notification_title))
				.setContentText(mCursor.getString(mCursor.getColumnIndex(TasksTable.COL_TASK_NAME)))
				.setSmallIcon(R.drawable.ic_notification)
				.setAutoCancel(true)
				.setSound(alarm)
				.setContentIntent(mPendingIntent)
				.build();

				if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_sound), true)){
					if(alarm == null)			
						noti.defaults |= Notification.DEFAULT_SOUND;
				}
				if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_vibrate), true))
					noti.defaults |= Notification.DEFAULT_VIBRATE;
				NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

				if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_alarm), true)
						&& PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_notification), true))
					notificationManager.notify((int)System.currentTimeMillis(), noti); 
			}
			mCursor.close();
		}

		Intent service = new Intent(context, PremiumAlarmService.class);
		service.putExtra(IntentKeyConstants.KEY_TYPE_OF_ALARM, 1);//TODO change later
		service.setAction(PremiumAlarmService.CREATE);
		context.startService(service);
		Intent mIntent =  new Intent(context , AlarmActivity.class) ;
		mIntent.putExtra(IntentKeyConstants.KEY_TASK_ID, id);
		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(mIntent);
	}

	private Uri getAlarmTone(Context context){
		String alarms = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.key_ringtone),null);
		if(alarms != null)
			return Uri.parse(alarms);
		return null;
	}

}
