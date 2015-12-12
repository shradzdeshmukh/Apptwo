package com.cyno.reminder_premium.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.cyno.reminder.fab.ButtonFloat;
import com.cyno.reminder_premium.R;
import com.cyno.reminder_premium.ui.FragmentCalender.CustomListener;
import com.cyno.reminder_premium.ui.IReminder.TrackerName;
import com.cynozer.reminder.utils.Task;
//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;






import com.cynozer.reminder_premium.contentproviders.TasksTable;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentAlarm extends DialogFragment implements OnClickListener, android.view.View.OnClickListener{

	private static final long TIME_OF_SNOOZE = 10*60*1000;
	private int taskId;
	private View rootView;
	private final String dateformat = "dd MMMM ";
	private ButtonFloat mFab;

	public FragmentAlarm() {
	}

	public FragmentAlarm(String id){
		this.taskId = Integer.valueOf(id);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new Builder(getActivity());
		rootView = View.inflate(getActivity(), R.layout.fragment_alarm, null);
		populateFields();
		mFab = (ButtonFloat) rootView.findViewById(R.id.fab);
		mFab.setOnClickListener(this);
		builder.setView(rootView);
		builder.setPositiveButton(getActivity().getString(R.string.snooze), this);
		builder.setNegativeButton(getActivity().getString(R.string.cancel), null);
		//		GoogleAnalytics.getInstance(getActivity().getBaseContext()).dispatchLocalHits();
		//		Tracker t = ((IReminder)getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
		//		t.setScreenName("Alarm fragment");
		//		t.send(new HitBuilders.AppViewBuilder().build());

		return builder.show();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		if(getActivity() != null)
			getActivity().finish();
	}

	private void populateFields() {
		TextView tvCategory = (TextView) rootView.findViewById(R.id.details_tv_category);
		tvCategory.setBackgroundColor(Color.parseColor(getColor()));
		TextView tvTitle = (TextView) rootView.findViewById(R.id.details_tv_title);
		TextView tvDate = (TextView) rootView.findViewById(R.id.details_tv_date);
		TextView tvDescKey = (TextView) rootView.findViewById(R.id.details_tv_description_key);
		TextView tvDescValue = (TextView) rootView.findViewById(R.id.details_tv_description_value);

		Cursor cur = getActivity().getContentResolver().query(TasksTable.CONTENT_URI, null, TasksTable.COL_TASK_ID + " = ?"
				, new String[]{String.valueOf(this.taskId)}, null);

		if(cur != null){
			if(cur.moveToNext()){
				tvCategory.setText(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_LABEL)));
				tvTitle.setText(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_NAME)));
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(Long.valueOf(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_DATE))));
				SimpleDateFormat format  = new SimpleDateFormat(dateformat, Locale.getDefault());
				//				long date = Long.valueOf(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_DATE)));

				//				if(Integer.valueOf(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_STATUS))) == Task.STATUS_DONE){
				//					tvDaysLeftValue.setTextColor(getActivity().getResources().getColor(R.color.green));
				//					tvDaysLeftKey.setTextColor(getActivity().getResources().getColor(R.color.green));
				//					tvDaysLeftValue.setVisibility(View.GONE);
				//					tvDaysLeftKey.setText(R.string.done);
				//					tvDaysLeftKey.setGravity(Gravity.CENTER_HORIZONTAL);
				//				}else if(diff < 0){
				//					tvDaysLeftValue.setTextColor(getActivity().getResources().getColor(R.color.warn));
				//					tvDaysLeftKey.setTextColor(getActivity().getResources().getColor(R.color.warn));
				//					tvDaysLeftValue.setVisibility(View.GONE);
				//					tvDaysLeftKey.setText(R.string.overdue);
				//					tvDaysLeftKey.setGravity(Gravity.CENTER_HORIZONTAL);
				//				}else{
				tvDate.setText(format.format(cal.getTime()));
				//				}

				String desc = cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_NOTE));
				if(desc != null){
					if(desc.length() > 0){
						tvDescKey.setVisibility(View.GONE);
						tvDescValue.setVisibility(View.GONE);
					}else{
						tvDescValue.setText(desc);
					}
				}else{
					tvDescKey.setVisibility(View.GONE);
					tvDescValue.setVisibility(View.GONE);
				}
			}
			cur.close();
		}

	}

	private String getColor() {
		Cursor cur = getActivity().getContentResolver().query(TasksTable.CONTENT_URI, new String[]{TasksTable.COL_TASK_CATEGORY_UID},
				TasksTable.COL_TASK_ID + " = ? ", new String[]{taskId+""}, null);
		if(cur != null){
			if(cur.moveToNext())
				return Task.getInstance().getTaskColor(Integer.valueOf(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_CATEGORY_UID))), getActivity());
			cur.close();
		}
		return null;
	}

	private long getDaysLeft(long diff , TextView tv) {
		long diffr = diff/(1000*3600*24);
		if(diffr != 0){
			tv.setText(R.string.days_left);
			return diffr;
		}else{
			tv.setText(R.string.hours_left_details);
			return diff/(1000*3600);
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case AlertDialog.BUTTON_POSITIVE:
			ContentValues values = new ContentValues();
			values.put(TasksTable.COL_TASK_DATE, String.valueOf(System.currentTimeMillis() + getSnoozeTime()));
			Task.getInstance().updateTask(taskId, values, getActivity());
			Toast.makeText(getActivity(), R.string.toast_snooze, Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
	}

	private long getSnoozeTime() {
		return TIME_OF_SNOOZE;
	}

	@Override
	public void onClick(View v) {
		ContentValues values = new ContentValues();
		values.put(TasksTable.COL_TASK_STATUS, Task.STATUS_DONE);
		getActivity().getContentResolver().
		update(TasksTable.CONTENT_URI, values, 
				TasksTable.COL_TASK_ID + " = ? ", new String[]{String.valueOf(this.taskId)});
		Toast.makeText(getActivity(), getActivity().getString(R.string.task_completed),Toast.LENGTH_LONG).show();

		Task.getInstance().setAlarm(getActivity());

		getDialog().dismiss();
		getActivity().sendBroadcast(new Intent(TaskListFragment.ACTION_REFRESH));
	}

	@Override
	public void onResume() {
		super.onResume();
		((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.accent));
		((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.accent));
		((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.WHITE);
		((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.WHITE);

	}

}
