package com.cyno.reminder_premium.ui;

import java.util.Calendar;

import com.cyno.reminder_premium.R;
import com.cynozer.reminder.utils.Task;
import com.cynozer.reminder_premium.contentproviders.TasksTable;

//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;


public class FragmentAddTask extends DialogFragment implements OnClickListener , OnDateSetListener, OnTimeSetListener{


	private EditText etTask;;
	private EditText etDescription;
	private int taskId;
	private boolean isUpdate;
	private Calendar cal;
	private Calendar taskCal;
	private  Dialog dialog;
	private boolean isShopping;

	public FragmentAddTask() {
	}

	public FragmentAddTask(boolean isUpdate, int taskId, boolean isShopping) {
		this.isUpdate = isUpdate;
		this.taskId = taskId;
		cal = Calendar.getInstance();
		taskCal = Calendar.getInstance();
		this.isShopping=isShopping;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		GoogleAnalytics.getInstance(getActivity().getBaseContext()).dispatchLocalHits();
		//		Tracker t = ((IReminder)getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
		//		t.setScreenName("Task create fragment");
		//		t.send(new HitBuilders.AppViewBuilder().build());

	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new Builder(getActivity());
		View rootView = View.inflate(getActivity(), R.layout.fragment_add_task , null);
		etTask = (EditText)rootView.findViewById(R.id.et_task_title);
		etDescription = (EditText)rootView.findViewById(R.id.et_task_description);


		if(isUpdate){
			Cursor cur = getActivity().getContentResolver().query(TasksTable.CONTENT_URI, null, TasksTable.COL_TASK_ID + " = ? ", new String[]{String.valueOf(taskId)}, null);
			if(cur != null){
				if(cur.moveToNext()){
					etTask.setText(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_NAME)));
					etDescription.setText(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_NOTE)));
				}
				cur.close();
			}
		}

		if(isShopping && Build.VERSION.SDK_INT >= 14){
			etDescription.setVisibility(View.GONE);
		}else{
			etDescription.setVisibility(View.VISIBLE);
		}

		builder.setView(rootView)
		.setTitle(getString(R.string.add_task_title))
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
	public void onClick(DialogInterface dialog, int which) {

	}

	private boolean hasTitle(){
		if(etTask != null){
			if(etTask.getText().length() > 0)
				return true;
		}
		showError(etTask, getString(R.string.enter_title_error));
		return false;
	}

	private boolean hasDescription(){
		if(etDescription != null){
			if(etDescription.getText().length() > 0)
				return true;
		}
		return false;
	}


	private void showError(EditText et , String error){
		et.setError(error);
	}

	class CustomListener implements View.OnClickListener {

		public CustomListener(Dialog dialog) {
			FragmentAddTask.this.dialog = dialog;
		}


		@Override
		public void onClick(View v) {
			if(hasTitle()){
				Task.getInstance().setTitle(etTask.getText().toString());
				if(hasDescription())
					Task.getInstance().setNote(etDescription.getText().toString());



				if(Build.VERSION.SDK_INT >= 14){
					if(isShopping){
						getFragmentManager().beginTransaction().replace(R.id.frame_container, new ShoppingFragment(taskId , etTask.getText().toString(),
								isUpdate , Task.getInstance().getiCategoryUID())).addToBackStack(ShoppingFragment.class.getSimpleName()).commit();
						getDialog().dismiss();
					}else{
						FragmentCalender cal = new FragmentCalender(isUpdate , taskId);
						cal.show(getFragmentManager(), FragmentCalender.class.getSimpleName());
						dialog.dismiss();
					}
				}else{
					DatePickerDialog picker = new DatePickerDialog(getActivity(), FragmentAddTask.this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
					picker.show();
				}
			}

		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		taskCal.set(Calendar.YEAR, year);
		taskCal.set(Calendar.MONTH, monthOfYear);
		taskCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		TimePickerDialog dialog = new TimePickerDialog(getActivity(), FragmentAddTask.this, cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE), false);
		dialog.show();
	}


	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		taskCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		taskCal.set(Calendar.MINUTE, minute);
		if(taskCal.getTimeInMillis() < System.currentTimeMillis())
			Toast.makeText(getActivity(), getActivity().getString(R.string.time_back_selected),Toast.LENGTH_LONG).show();
		else{
			Task.getInstance().setTime(taskCal.getTimeInMillis());
			Task.getInstance().setStatus(Task.STATUS_PENDING);
			if(isUpdate)
				Task.getInstance().storeLocally(getActivity(), taskId);
			else
			Task.getInstance().storeLocally(getActivity(), Task.NOT_IS_UPDATE);
			if(getActivity() instanceof MainActivity){
				((MainActivity)getActivity()).refreshNavDrawer(Task.getInstance().getiCategoryType() ,
						Task.getInstance().getiCategoryUID());
				((MainActivity)getActivity()).displayView(TasksTable.TASK_TYPE_NORMAL , Task.getInstance().getiCategoryUID());
			}
			Task.getInstance().clearObject();
			dialog.dismiss();
		}
	}
}
