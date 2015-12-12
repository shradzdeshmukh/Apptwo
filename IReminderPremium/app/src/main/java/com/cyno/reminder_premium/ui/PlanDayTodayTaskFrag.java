package com.cyno.reminder_premium.ui;

import com.cyno.reminder_premium.R;
import com.cyno.reminder_premium.adapters.PlanDayAdapter;
import com.cynozer.reminder.utils.Task;
import com.cynozer.reminder.utils.TodayTaskModel;
import com.cynozer.reminder_premium.contentproviders.CategoriesTable;
import com.cynozer.reminder_premium.contentproviders.TasksTable;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class PlanDayTodayTaskFrag extends Fragment{

	private final long lOneDay = 1000*3600*24;
	private PlanDayAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_today_task, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		adapter = new PlanDayAdapter(getActivity());

		ListView mList = (ListView) view.findViewById(R.id.list_today_task);
		Cursor cur = getActivity().getContentResolver().query(CategoriesTable.CONTENT_URI,
				new String[]{CategoriesTable.COL_CATEGORY_NAME , CategoriesTable.COL_CATEGORY_ID},
				CategoriesTable.COL_CATEGORY_TYPE + " = ? ", new String[]{String.valueOf(TasksTable.TASK_TYPE_NORMAL)}, null);
		if(cur != null){
			while(cur.moveToNext()){
				TodayTaskModel model = new TodayTaskModel();
				model.setSeperator(true);
				model.setTitle(cur.getString(cur.getColumnIndex(CategoriesTable.COL_CATEGORY_NAME)));
				Cursor mCursor = getActivity().getContentResolver().query(TasksTable.CONTENT_URI,
						new String[]{TasksTable.COL_TASK_NAME , 
						TasksTable.COL_TASK_DATE},
						TasksTable.COL_TASK_CATEGORY_UID + " = ? AND "+ 
								TasksTable.COL_TASK_DATE + " > ? AND " + 
								TasksTable.COL_TASK_DATE + " < ? ", 

								new String[]{String.valueOf(cur.getInt(cur.getColumnIndex(CategoriesTable.COL_CATEGORY_ID)))
						,String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()+lOneDay)},TasksTable.COL_TASK_DATE);
				if(mCursor != null){
					if(mCursor.moveToNext()){
						if(DateUtils.isToday(Long.valueOf(mCursor.getString(mCursor.getColumnIndex(TasksTable.COL_TASK_DATE))))){
							adapter.addSeparatorItem(model);
							addItems(cur.getInt(cur.getColumnIndex(CategoriesTable.COL_CATEGORY_ID)));
						}
					}
					mCursor.close();
				}
			}
			cur.close();
		}


		if(adapter.getCount() == 0)
			openNextFrag();
		mList.setDividerHeight(0);
		mList.setAdapter(adapter);

		Button btNext = (Button) view.findViewById(R.id.button_today_task_next);
		btNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openNextFrag();
			}
		});
	}

	private void addItems(int catID){
		Cursor mCursor = getActivity().getContentResolver().query(TasksTable.CONTENT_URI,
				new String[]{TasksTable.COL_TASK_NAME , TasksTable.COL_TASK_DATE},
				TasksTable.COL_TASK_CATEGORY_UID + " = ? AND "+ 	TasksTable.COL_TASK_DATE + " > ? AND " + 
						TasksTable.COL_TASK_DATE + " < ? ", 
						new String[]{String.valueOf(catID),String.valueOf(System.currentTimeMillis()),
				String.valueOf(System.currentTimeMillis()+lOneDay)},TasksTable.COL_TASK_DATE);
		if(mCursor != null){
			while(mCursor.moveToNext()){
				if(DateUtils.isToday(Long.valueOf(mCursor.getString(mCursor.getColumnIndex(TasksTable.COL_TASK_DATE))))){
					TodayTaskModel model = new TodayTaskModel();
					model.setTitle(mCursor.getString(mCursor.getColumnIndex(TasksTable.COL_TASK_NAME)));
					model.setTime(Long.valueOf(mCursor.getString(mCursor.getColumnIndex(TasksTable.COL_TASK_DATE))));
					adapter.addItem(model);
				}
			}
			mCursor.close();
		}

	}



	private void openNextFrag(){
		getFragmentManager().beginTransaction().replace(R.id.frame_container, new FragmentPlanDay() ).commit();
	}

}
