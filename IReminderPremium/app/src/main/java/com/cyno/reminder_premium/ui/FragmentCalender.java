package com.cyno.reminder_premium.ui;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.cyno.reminder_premium.R;
import com.cyno.reminder_premium.calender.CalendarDay;
import com.cyno.reminder_premium.calender.MaterialCalendarView;
import com.cyno.reminder_premium.calender.OnDateChangedListener;
import com.cynozer.reminder.utils.Task;
//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;




















import com.cynozer.reminder_premium.contentproviders.TasksTable;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;


public class FragmentCalender extends DialogFragment implements OnDateChangedListener, OnTimeSetListener {

	private View rootView;
	private MaterialCalendarView mCal;
	private int taskId;
	private boolean isUpdate;
	private AlertDialog repeatDialog;
	private int uniqueId;
	private int repeatType;
	private boolean timeSet;

	public FragmentCalender() {
	}

	public FragmentCalender(boolean isUpdate, int taskId) {
		this.isUpdate = isUpdate;
		this.taskId = taskId;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		GoogleAnalytics.getInstance(getActivity().getBaseContext()).dispatchLocalHits();
		//		Tracker t = ((IReminder)getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
		//		t.setScreenName("Calender fragment");
		//		t.send(new HitBuilders.AppViewBuilder().build());

	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		rootView = View.inflate(getActivity(), R.layout.fragment_calender, null);
		Calendar cal = Calendar.getInstance();
		if(isUpdate){
			Cursor cur = getActivity().getContentResolver().query(TasksTable.CONTENT_URI, null, TasksTable.COL_TASK_ID + " = ? ", new String[]{String.valueOf(taskId)},null);
			if(cur != null){
				if(cur.moveToLast()){
					cal.setTimeInMillis(Long.valueOf(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_DATE))));
					uniqueId = cur.getInt(cur.getColumnIndex(TasksTable.COL_TASK_UNIQUE_KEY));
					repeatType = cur.getInt(cur.getColumnIndex(TasksTable.COL_TASK_REPEAT_TYPE));

				}
				cur.close();
			}
		}else
			cal.setTimeInMillis(System.currentTimeMillis());
		mCal = (MaterialCalendarView) rootView.findViewById(R.id.calendarView);
		mCal.setSelectedDate(new CalendarDay(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH)));
		mCal.setOnDateChangedListener(this);
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setView(rootView)
		.setPositiveButton(getString(R.string.create), null)
		.setNegativeButton(getString(android.R.string.cancel),null);
		return builder.create();
	}

	@Override
	public void onStart() {
		super.onStart();
		if (getDialog() == null) {
			return;
		}
		getDialog().getWindow().setWindowAnimations(R.style.dialog_animation_fade);
	}

	@Override
	public void onResume() {
		super.onResume();
		((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.accent));
		((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.accent));
		Button mPosButton = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
		mPosButton.setOnClickListener(new CustomListener(getDialog()));

	}

	@Override
	public void onDateChanged(MaterialCalendarView widget, CalendarDay date) {
	}


	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if(timeSet)
			return;
		else
			timeSet = true;
		if(getActivity()== null){
			return;
		}

		CalendarDay cal = mCal.getSelectedDate();
		Calendar mCalender = cal.getCalendar();
		mCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
		mCalender.set(Calendar.MINUTE, minute);

		if(mCalender.getTimeInMillis() < System.currentTimeMillis()){
			timeSet = false;
			Toast.makeText(getActivity(), R.string.time_back_selected, Toast.LENGTH_LONG).show();
			return;
		}

		Task.getInstance().setTime(mCalender.getTimeInMillis());

		AlertDialog.Builder mBuilder = new Builder(getActivity());
		mBuilder.setMessage(getString(R.string.repeat_dialog_msg)).
		setPositiveButton(R.string.Yes, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(isUpdate && repeatType != -1){
					getActivity().getContentResolver().delete(TasksTable.CONTENT_URI, TasksTable.COL_TASK_UNIQUE_KEY + " = ? AND " + TasksTable.COL_TASK_ID + " != ? ", new String[]{String.valueOf(uniqueId),String.valueOf(taskId) });
				}
				repeatTask();
			}
		}).setNegativeButton(R.string.No, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(isUpdate && repeatType != -1){
					Task.getInstance().setRepeat(false);
					int i =  getActivity().getContentResolver().delete(TasksTable.CONTENT_URI, TasksTable.COL_TASK_UNIQUE_KEY + " = ? AND " + TasksTable.COL_TASK_ID + " != ? ", new String[]{String.valueOf(uniqueId),String.valueOf(taskId) });

					Toast.makeText(getActivity(), i + "" , Toast.LENGTH_LONG).show();
				}
				storeLocally();
			}
		}).setCancelable(false).show();
		getDialog().hide();

	}

	private void repeatTask(){

		View rootView = View.inflate(getActivity(), R.layout.list_repeat_layout, null);
		ListView lv = (ListView) rootView.findViewById(R.id.list_repeat);
		String [] items = getResources().getStringArray(R.array.repeat_items);
		List<String> mList = Arrays.asList(items);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1, mList);
		lv.setAdapter(adapter);
		lv.setDividerHeight(0);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Task.getInstance().setRepeat(true);
				Task.RepeatTypes types = Task.RepeatTypes.values()[position];
				Task.getInstance().setiRepeatType(types.ordinal());
				storeLocally();
				repeatDialog.dismiss();
			}
		});


		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setView(rootView);
		repeatDialog = builder.create();
		repeatDialog.show();

	}

	private void storeLocally(){
//		if(getActivity() instanceof MainActivity)
//			((MainActivity)getActivity()).displayView(TasksTable.TASK_TYPE_NORMAL , Task.getInstance().getiCategoryUID());
		new StoreLocallyTask().execute();
	}


	class CustomListener implements View.OnClickListener {
		private final Dialog dialog;
		public CustomListener(Dialog dialog) {
			this.dialog = dialog;
		}


		@Override
		public void onClick(View v) {
			Calendar cal  = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			TimePickerDialog dialogg = new TimePickerDialog(getActivity(), FragmentCalender.this, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
			dialogg.show();			
		}
	}


	private class StoreLocallyTask extends AsyncTask<Void, Void, Void>{
		
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			this.dialog = ProgressDialog.show(getActivity(), null,getString(R.string.progress));
		}

		@Override
		protected Void doInBackground(Void... params) {
			Task.getInstance().setStatus(Task.STATUS_PENDING);
			if(!isUpdate)
				Task.getInstance().storeLocally(getActivity(), Task.NOT_IS_UPDATE);
			else
				Task.getInstance().storeLocally(getActivity(), taskId);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dialog.cancel();
			Toast.makeText(getActivity(), R.string.task_added, Toast.LENGTH_LONG).show();

			if(getActivity() instanceof MainActivity)
				((MainActivity)getActivity()).refreshNavDrawer(Task.getInstance().getiCategoryType() ,
						Task.getInstance().getiCategoryUID());
		

			Task.getInstance().clearObject();
			getDialog().dismiss();
		}

	}
	
	@Override
	public void onStop() {
		super.onStop();
		Task.getInstance().clearObject();
	}
}
