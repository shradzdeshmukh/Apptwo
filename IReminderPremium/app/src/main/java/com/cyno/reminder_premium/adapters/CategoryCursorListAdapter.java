package com.cyno.reminder_premium.adapters;


import com.cyno.reminder_premium.R;
import com.cynozer.reminder.utils.Task;
import com.cynozer.reminder_premium.contentproviders.CategoriesTable;
import com.cynozer.reminder_premium.contentproviders.TasksTable;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryCursorListAdapter extends CursorAdapter {
	Context mContext;

	public CategoryCursorListAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		this.mContext = context;
	}
	


	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {
		Cursor mCursor =null;
		TextView mTextTitle = (TextView) view.findViewById(R.id.title);
		TextView mCounter = (TextView) view.findViewById(R.id.counter);
		View mView = view.findViewById(R.id.icon);
		mTextTitle.setText(cursor.getString(cursor.getColumnIndex(CategoriesTable.COL_CATEGORY_NAME)));
		mView.setBackgroundColor(Color.parseColor(cursor.getString(cursor.getColumnIndex(CategoriesTable.COL_CATEGORY_COLOR))));
		
		switch (Integer.valueOf(cursor.getString(cursor.getColumnIndex(CategoriesTable.COL_CATEGORY_TYPE)))) {
		case TasksTable.TASK_TYPE_COMMING_UP:
			mCursor =  mContext.getContentResolver().query(TasksTable.CONTENT_URI, new String[]{TasksTable.COL_TASK_CATEGORY_TYPE},
					TasksTable.COL_TASK_DATE +  " > ? AND "+TasksTable.COL_TASK_DATE +  " < ? ", new String[]
							{String.valueOf(System.currentTimeMillis()) , String.valueOf(System.currentTimeMillis() + 2*24*3600*1000)},
							null);
			break;
		case TasksTable.TASK_TYPE_NEXT_SEVEN_DAYS:
			mCursor = mContext.getContentResolver().query(TasksTable.CONTENT_URI, new String[]{TasksTable.COL_TASK_CATEGORY_TYPE},
					TasksTable.COL_TASK_DATE +  " > ? AND "+TasksTable.COL_TASK_DATE +  " < ? ", 
					new String[]{String.valueOf(System.currentTimeMillis()) ,
					String.valueOf(System.currentTimeMillis() + 7*24*3600*1000)}, null);
			break;
		case TasksTable.TASK_TYPE_ALL_TASKS:
			mCursor = mContext.getContentResolver().query(TasksTable.CONTENT_URI,new String[]{TasksTable.COL_TASK_CATEGORY_TYPE}, null,null, null);
			break;


		default:
			mCursor = mContext.getContentResolver().query(TasksTable.CONTENT_URI,new String[]{TasksTable.COL_TASK_ID},
					TasksTable.COL_TASK_CATEGORY_UID+ " = ? ",
					new String[]{cursor.getString(cursor.getColumnIndex(CategoriesTable.COL_CATEGORY_ID))}, null);
			break;
		}
		
		Log.d("count", cursor.getString(cursor.getColumnIndex(CategoriesTable.COL_CATEGORY_NAME)) + " " + mCursor.getCount()+"");
		
		if(mCursor  != null){
			if(mCursor.getCount() > 0){
				mCounter.setText(mCursor.getCount()+"");
				mCounter.setVisibility(View.VISIBLE);
			}else
				mCounter.setVisibility(View.GONE);
			mCursor.close();
		}else
			mCounter.setVisibility(View.GONE);
		
		view.setTag(R.string.cat_id , cursor.getString(cursor.getColumnIndex(CategoriesTable.COL_CATEGORY_ID)));
		view.setTag(R.string.cat_type, cursor.getString(cursor.getColumnIndex(CategoriesTable.COL_CATEGORY_TYPE)));

	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		return View.inflate(mContext, R.layout.drawer_list_item, null);
	}
	
	
}
