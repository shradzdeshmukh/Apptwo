package com.cyno.reminder_premium.ui;

import com.cyno.reminder_premium.R;
import com.cyno.reminder_premium.adapters.CategoryColorAdapter;
import com.cyno.reminder.fab.ButtonFloat;
import com.cynozer.reminder_premium.contentproviders.CategoriesTable;
import com.cynozer.reminder_premium.contentproviders.TasksTable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FragmentChangeCatColor extends Fragment implements LoaderCallbacks<Cursor>{

	private static final int LOADER_ID = 213;
	public static final String ACTION_REFRESH_LIST = "REF";
	private CategoryColorAdapter adapter;
	private ListView mListView;
	private ColorChangeReciever reciever;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_category_color, null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		reciever = new ColorChangeReciever();
		getLoaderManager().initLoader(LOADER_ID, null, this);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		getActivity().registerReceiver(reciever, new IntentFilter(ACTION_REFRESH_LIST));
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		((MainActivity)getActivity()).getToolbar().setBackgroundColor(getResources().getColor(R.color.primary));
		mListView = (ListView) view.findViewById(R.id.list_categories_color);
		ButtonFloat fab = (ButtonFloat)view.findViewById(R.id.fab);
		fab.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentChangeCatDialog frag = new FragmentChangeCatDialog();
				frag.show(getFragmentManager(), FragmentChangeCatDialog.class.getSimpleName());
			}
		});
		Cursor mCur = getActivity().getContentResolver().query(CategoriesTable.CONTENT_URI, null,null, null, null);
		adapter = new CategoryColorAdapter(getActivity(), mCur, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FragmentChangeCatDialog frag = new FragmentChangeCatDialog(view.getTag().toString());
				frag.show(getFragmentManager(), FragmentChangeCatDialog.class.getSimpleName());
			}
		});
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity() , CategoriesTable.CONTENT_URI , null , CategoriesTable.COL_CATEGORY_TYPE + " = ? " ,
				new String[]{String.valueOf(TasksTable.TASK_TYPE_NORMAL)}, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}
	
	
	private class ColorChangeReciever extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			getLoaderManager().restartLoader(LOADER_ID, null, FragmentChangeCatColor.this);
		}
		
	}
	
	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(reciever);
	}

}
