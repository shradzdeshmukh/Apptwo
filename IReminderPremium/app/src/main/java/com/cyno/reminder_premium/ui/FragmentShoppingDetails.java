package com.cyno.reminder_premium.ui;

import java.util.ArrayList;

import com.cyno.reminder_premium.R;
import com.cyno.reminder_premium.adapters.ShoppingDetailsAdapter;
import com.cyno.reminder.fab.ButtonFloat;
import com.cynozer.reminder.utils.Task;
import com.cynozer.reminder_premium.contentproviders.TasksTable;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

public class FragmentShoppingDetails extends Fragment implements OnCheckedChangeListener{

	private int taskId;
	private View rootView;
	private ArrayList<String> alList;
	private ArrayList<Boolean> alCheckedList;
	private ShoppingDetailsAdapter adapter;

	public FragmentShoppingDetails() {}

	public FragmentShoppingDetails(int taskId) {
		this.taskId = taskId;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.shopping_details,null);
		alList = new ArrayList<String>();
		alCheckedList= new ArrayList<Boolean>();
		TextView mText = (TextView) rootView.findViewById(R.id.tv_shopping_title);
		Cursor cur = getActivity().getContentResolver().query(TasksTable.CONTENT_URI, null, 
				TasksTable.COL_TASK_ID + " = ? ", new String[]{String.valueOf(taskId)}, null);
		if(cur != null){
			if(cur.moveToNext()){

				((MainActivity)getActivity()).changeToolbarColor(TasksTable.TASK_TYPE_SHOPPING, 
						cur.getInt(cur.getColumnIndex(TasksTable.COL_TASK_CATEGORY_UID)));
				mText.setText(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_NAME)));
				mText.setBackgroundColor(Color.parseColor(cur.getString(cur.getColumnIndex(TasksTable.COL_CAT_COLOR))));
				String []arr = cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_NOTE)).split(",");
				for(int i = 0 ; i < arr.length ; ++i){
					alList.add(arr[i]);
				}
				String []doneList = cur.getString(cur.getColumnIndex(TasksTable.COL_SHOPPING_COMPLETED)).split(",");
				for(int i = 0 ; i < doneList.length ; ++i){
					String val = doneList[i];
					if(val.trim().equals("false"))
						alCheckedList.add(false);
					else
						alCheckedList.add(true);
				}
			}
			cur.close();
		}



		adapter = new ShoppingDetailsAdapter(getActivity() , android.R.id.text1 , this , alList , alCheckedList);
		final ListView lv = (ListView) rootView.findViewById(R.id.shop_list);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				boolean temp = alCheckedList.get(position);
				alCheckedList.remove(position);
				alCheckedList.add(position,!temp);
				adapter.notifyDataSetChanged();

			}
		});

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButtonFloat mFab = (ButtonFloat)view.findViewById(R.id.fab);
		mFab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int size = alCheckedList.size();
				alCheckedList.removeAll(alCheckedList);
				for(int i = 0 ; i <= size ; ++i){
					alCheckedList.add(true);
				}
				ContentValues values = new ContentValues();
				values.put(TasksTable.COL_TASK_STATUS, Task.STATUS_DONE);
				values.put(TasksTable.COL_SHOPPING_COMPLETED, alCheckedList.toString().replace("[","").replace("]",""));

				getActivity().getContentResolver().
				update(TasksTable.CONTENT_URI, values, 
						TasksTable.COL_TASK_ID+ " = ? ", new String[]{String.valueOf(taskId)});
				Task.getInstance().setAlarm(getActivity());
				getActivity().sendBroadcast(new Intent(TaskListFragment.ACTION_REFRESH));
				getFragmentManager().popBackStack();
			}
		});
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if(getActivity() instanceof MainActivity)
//			((MainActivity)getActivity()).hideFab();
	}

	@Override
	public void onStop() {
		super.onStop();
		ContentValues values = new ContentValues();
		values.put(TasksTable.COL_SHOPPING_COMPLETED, alCheckedList.toString().replace("[","").replace("]",""));
		getActivity().getContentResolver().update(TasksTable.CONTENT_URI, values, TasksTable.COL_TASK_ID + " = ? ",
				new String[]{String.valueOf(taskId)});
//		((MainActivity)getActivity()).showFab();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		alCheckedList.remove((int)buttonView.getTag());
		alCheckedList.add((int)buttonView.getTag(),isChecked);
		adapter.notifyDataSetChanged();

	}
}
