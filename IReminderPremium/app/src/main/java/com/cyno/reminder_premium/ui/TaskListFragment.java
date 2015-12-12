package com.cyno.reminder_premium.ui;

import java.util.ArrayList;

import com.cyno.reminder.constants.GlobalConstants;
import com.cyno.reminder.fab.ButtonFloat;
import com.cyno.reminder.interfaces.ClickEventListners;
import com.cyno.reminder.multiselect.MultiSelector;
import com.cyno.reminder.multiselect.SwappingHolder;
import com.cyno.reminder_premium.R;
import com.cyno.reminder_premium.adapters.TaskListAdapter;
import com.cynozer.reminder.utils.DividerHelper;
//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;





import com.cynozer.reminder.utils.Task;
import com.cynozer.reminder_premium.contentproviders.TasksTable;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class TaskListFragment extends Fragment implements LoaderCallbacks<Cursor> , ClickEventListners{

	protected static final int SHOW_EMPTY_VIEW = 0;
	protected static final String ACTION_REFRESH = "REFRESH";
	private final long lTwoDays = 1000*3600*24*2;
	private final long lOneWeek = 1000*3600*24*7;

	private RecyclerView mRecyclerView;
	private LinearLayoutManager mLayoutManager;
	private TaskListAdapter mAdapter;
	private int LOADER_ID = 10;
	private int type;
	private TextView mSubHeader;
	private String tite;
	private Handler mEmptyListHandler;
	private RefreshLisner listner;
	private MultiSelector multiSelector;
	private Toolbar mToolbar;
	private ActionMode mActionMode;
	private int catID;


	public TaskListFragment(){
		super();
	}


	public TaskListFragment(int type , int catID , String title) {
		this.type = type;
		this.tite = title;
		this.catID = catID;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEmptyListHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				EmptListFragment fragment;
				switch (msg.what) {
				case SHOW_EMPTY_VIEW:
					//					if(getActivity() != null)
					//						((MainActivity)getActivity()).displayView(MainActivity.EMPTY_FRAGMENT);
					fragment =	new EmptListFragment();
					getFragmentManager().beginTransaction()
					.replace(R.id.frame_container, fragment, EmptListFragment.class.getSimpleName()).commit();
					break;

				default:
					break;
				}
			}
		};

		//		GoogleAnalytics.getInstance(getActivity().getBaseContext()).dispatchLocalHits();
		//		Tracker t = ((IReminder)getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
		//		t.setScreenName("Task list fragment");
		//		t.send(new HitBuilders.AppViewBuilder().build());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_task_list, null);
		mSubHeader =(TextView) rootView.findViewById(R.id.fragment_task_list_subheader);


		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_tasks);
		final RecyclerView.ItemDecoration itemDecoration = new DividerHelper(getActivity());
		mRecyclerView.addItemDecoration(itemDecoration);
		// use this setting to improve performance if you know that changes
		// in content do not change the layout size of the RecyclerView
		mRecyclerView.setHasFixedSize(true);
		// use a linear layout manager
		mLayoutManager = new LinearLayoutManager(getActivity());
		mRecyclerView.setLayoutManager(mLayoutManager);
		// specify an adapter (see also next example)
		multiSelector = new MultiSelector();
		mAdapter = new TaskListAdapter(getActivity(), null , this, multiSelector);
		mRecyclerView.setAdapter(mAdapter);

		mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		getLoaderManager().initLoader(LOADER_ID , null, this);

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButtonFloat mButton = (ButtonFloat) view.findViewById(R.id.fab);
		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((MainActivity)getActivity()).displayView(MainActivity.ADD_TASK , -1);
			}
		});
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		switch (type) {
		case TasksTable.TASK_TYPE_COMMING_UP:
			return new CursorLoader(getActivity(),TasksTable.CONTENT_URI,null,  TasksTable.COL_TASK_DATE + " > ? AND " + TasksTable.COL_TASK_DATE + " < ? ", 
					new String[]{String.valueOf(System.currentTimeMillis()),String.valueOf(System.currentTimeMillis()+lTwoDays)}, null);
		case TasksTable.TASK_TYPE_NEXT_SEVEN_DAYS:
			return new CursorLoader(getActivity(),TasksTable.CONTENT_URI,null,  
					TasksTable.COL_TASK_DATE + " > ? AND " + TasksTable.COL_TASK_DATE + " < ? ", 
					new String[]{String.valueOf(System.currentTimeMillis()),String.valueOf(System.currentTimeMillis()+lOneWeek)}, null);
		case TasksTable.TASK_TYPE_ALL_TASKS:
			return new CursorLoader(getActivity(),TasksTable.CONTENT_URI,null,  null,null, null);

		case TasksTable.TASK_TYPE_SHOPPING:
			//		case GlobalConstants.TYPE_PERSONAL:
			//		case GlobalConstants.TYPE_BILLS:
			//		case GlobalConstants.TYPE_BIRTHDAY:
			//		case GlobalConstants.TYPE_SHOPPING:
			//		case GlobalConstants.TYPE_HOLIDAYS:
			//		case GlobalConstants.TYPE_APPOINTMENTS:
			//		case GlobalConstants.TYPE_OTHERS:

		default:
			return new CursorLoader(getActivity(),TasksTable.CONTENT_URI,null,  TasksTable.COL_TASK_CATEGORY_UID + " = ? ", new String[]{String.valueOf(catID)}, null);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		mAdapter.swapCursor(cursor);
		if(cursor.getCount() == 0)
			this.mEmptyListHandler.sendEmptyMessage(SHOW_EMPTY_VIEW);
		else
			mSubHeader.setText(this.tite);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}


	private void closeItem(){
		int post = mAdapter.mItemManger.getOpenItems().get(0);
		mAdapter.mItemManger.closeItem(post);
	}

	@Override
	public void onStart() {
		super.onStart();
		listner = new RefreshLisner();
		getActivity().registerReceiver(listner, new IntentFilter(ACTION_REFRESH));
	}

	@Override
	public void onResume() {
		super.onResume();
		//		((MainActivity)getActivity()).showFab();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(listner);
	}

	private class RefreshLisner extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			int pos = mLayoutManager.findFirstCompletelyVisibleItemPosition();
			getLoaderManager().restartLoader(LOADER_ID, null, TaskListFragment.this);
			mRecyclerView.setAdapter(mAdapter);
			mRecyclerView.scrollToPosition(pos);
		}
	}



	@SuppressLint("NewApi")
	@Override
	public void onLongClick(SwappingHolder holder, View view) {

		if(multiSelector.isMultiselect()){
			mActionMode.finish();
			return;
		}
		multiSelector.setMultiSelectMode(true);
		multiSelector.setSelected(holder , true , String.valueOf(view.getTag()));
		mToolbar.startActionMode(new Callback() {
			@Override
			public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public void onDestroyActionMode(android.view.ActionMode mode) {
				multiSelector.clearSelections();
				multiSelector.setMultiSelectMode(false);
				mToolbar.setVisibility(View.VISIBLE);

			}

			@Override
			public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
				mActionMode = mode;
				getActivity().getMenuInflater().inflate(R.menu.menu_multiselect, menu);
				mToolbar.setVisibility(View.GONE);
				mActionMode.setTitle(multiSelector.getSelectedPositions().size()+"");
				return true;
			}

			@SuppressLint("NewApi")
			@Override
			public boolean onActionItemClicked(android.view.ActionMode mode,
					MenuItem item) {
				int pos = mLayoutManager.findFirstVisibleItemPosition();
				switch (item.getItemId()) {
				case R.id.menu_done:
					Task.getInstance().doneInBulk(multiSelector.getSlectedIds() , getActivity().getContentResolver()  ,getActivity());
					mAdapter.notifyDatasetChanged();
					break;
				case R.id.menu_undone:
					Task.getInstance().undoneInBulk(multiSelector.getSlectedIds() , getActivity().getContentResolver(),getActivity());
					mAdapter.notifyDatasetChanged();
					break;
				case R.id.menu_delete:
					Task.getInstance().deleteInBulk(multiSelector.getSlectedIds() , getActivity().getContentResolver(),getActivity());

					break;
				case R.id.menu_sync:
					for(String id : multiSelector.getSlectedIds()){
						Task.addToGoogleCal(Task.getInstance().getTask(Integer.valueOf(id), getActivity()), getActivity());
					}
					Toast.makeText(getActivity(), getString(R.string.sync_msg), Toast.LENGTH_LONG).show();

					break;

				default:
					break;
				}
				multiSelector.clearSelections();
				multiSelector.setMultiSelectMode(false);
				mode.finish();

				//				((MainActivity)getActivity()).refreshNavDrawer(type , catID );
				getLoaderManager().restartLoader(LOADER_ID, null, TaskListFragment.this);
				mRecyclerView.setAdapter(mAdapter);

				Task.getInstance().setAlarm(getActivity());
				//				getActivity().sendBroadcast(new Intent(TaskListFragment.ACTION_REFRESH));
				mRecyclerView.scrollToPosition(pos);
				return true;
			}
		});
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(SwappingHolder holder, View v) {

		if(multiSelector.isMultiselect() && !(v.getId() == R.id.iv_delete || v.getId() == R.id.iv_edt || v.getId() == R.id.iv_done)){
			multiSelector.setSelected(holder, !holder.isActivated() ,(String)v.getTag());
			if(multiSelector.getSelectedPositions().size() == 0){
				multiSelector.setMultiSelectMode(false);
				mActionMode.finish();
			}else
				mActionMode.setTitle(multiSelector.getSelectedPositions().size()+"");

		}else{

			Log.d("tag", v.getTag().toString()+" tag");
			switch (v.getId()) {
			case R.id.iv_delete:
				getActivity().getContentResolver().
				delete(TasksTable.CONTENT_URI,TasksTable.COL_TASK_ID + " = ? ", 
						new String[]{String.valueOf(v.getTag())});
				Toast.makeText(getActivity(), getActivity().getString(R.string.task_delete),Toast.LENGTH_LONG).show();
				Task.getInstance().setAlarm(getActivity());
				getLoaderManager().restartLoader(LOADER_ID, null, this);
				//				getActivity().sendBroadcast(new Intent(TaskListFragment.ACTION_REFRESH));
				((MainActivity)getActivity()).refreshNavDrawer();
				break;
			case R.id.iv_edt:
				CategoryListFragment fragment = new CategoryListFragment(true, Integer.valueOf(v.getTag().toString()));
				fragment.show(getFragmentManager(), CategoryListFragment.class.getSimpleName());
				break;
			case R.id.iv_done:
				boolean isDone = false;
				Cursor cur = getActivity().getContentResolver().query(TasksTable.CONTENT_URI, new String[]{TasksTable.COL_TASK_STATUS},
						TasksTable.COL_TASK_ID + " = ? ", new String[]{v.getTag().toString()}, null);
				if(cur != null){
					if(cur.moveToNext()){
						isDone = Integer.valueOf(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_STATUS))) == Task.STATUS_DONE;
					}
					cur.close();
				}
				ContentValues values = new ContentValues();
				if(!isDone)
					values.put(TasksTable.COL_TASK_STATUS, Task.STATUS_DONE);
				else
					values.put(TasksTable.COL_TASK_STATUS, Task.STATUS_PENDING);

				Log.d("update", String.valueOf(v.getTag().toString()));
				getActivity().getContentResolver().
				update(TasksTable.CONTENT_URI, values, 
						TasksTable.COL_TASK_ID + " = ? ", new String[]{String.valueOf(v.getTag().toString())});
				if(!isDone)
					Toast.makeText(getActivity(), getActivity().getString(R.string.task_completed),Toast.LENGTH_LONG).show();
				else
					Toast.makeText(getActivity(), getActivity().getString(R.string.task_pending),Toast.LENGTH_LONG).show();

				Task.getInstance().setAlarm(getActivity());
				mAdapter.notifyDatasetChanged();
				getLoaderManager().restartLoader(LOADER_ID, null, this);
				//				getActivity().sendBroadcast(new Intent(TaskListFragment.ACTION_REFRESH));

				break;

			case R.id.iv_sync:
				if(Task.addToGoogleCal(Task.getInstance().getTask(Integer.valueOf(v.getTag().toString()), getActivity()), getActivity()))	
					Toast.makeText(getActivity(), getString(R.string.sync_msg), Toast.LENGTH_LONG).show();
				//				getActivity().sendBroadcast(new Intent(TaskListFragment.ACTION_REFRESH));
				break;

			default:
				int cat = -1;
				//				Cursor mCursor = getActivity().getContentResolver().query(TasksTable.CONTENT_URI,
				//						new String[]{TasksTable.COL_TASK_CATEGORY_UID}, TasksTable.COL_TASK_ID + " = ? ",
				//						new String[]{String.valueOf(v.getTag())}, null);
				//				if(mCursor != null){
				//					if(mCursor.moveToNext()){
				//						cat = mCursor.getInt(mCursor.getColumnIndex(TasksTable.COL_TASK_CATEGORY_UID));
				//					}
				//					mCursor.close();
				//				}
				cat = Integer.valueOf(v.getTag(R.string.cat_type).toString());
				if(cat == TasksTable.TASK_TYPE_SHOPPING){
					FragmentShoppingDetails frag = new FragmentShoppingDetails(Integer.valueOf(v.getTag().toString()));
					getFragmentManager().beginTransaction().replace(R.id.frame_container,frag, 
							FragmentShoppingDetails.class.getSimpleName()).addToBackStack(FragmentShoppingDetails.class.getSimpleName()).commit();
				}else{
					FragmentDetails frag = new FragmentDetails((String)v.getTag());
					frag.show(getFragmentManager(), FragmentDetails.class.getSimpleName());
				}
				break;
			}

		}
		closeItem();

	}



}
