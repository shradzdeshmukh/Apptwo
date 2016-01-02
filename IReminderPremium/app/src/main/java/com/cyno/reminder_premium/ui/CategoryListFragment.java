package com.cyno.reminder_premium.ui;

import java.util.ArrayList;

import com.cyno.reminder.models.CategoryItem;
import com.cyno.reminder_premium.R;
import com.cyno.reminder_premium.adapters.CategoryListAdapter;
import com.cynozer.reminder.utils.Task;
//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;






import com.cynozer.reminder_premium.contentproviders.CategoriesTable;
import com.cynozer.reminder_premium.contentproviders.TasksTable;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class CategoryListFragment extends DialogFragment implements OnItemClickListener {

	//	private ArrayList<String> mCatTitles;
	//	private ArrayList<String> mCatIcons;
	private ListView mCatList;
	private CategoryListAdapter adapter;
	private View rootView;
	private boolean isUpdate;
	private int taskId;
	private int listPosition;

	public CategoryListFragment(){

	}
	public CategoryListFragment(boolean isUpdate , int id){
		this.isUpdate = isUpdate;
		this.taskId = id;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Base_Theme_AppCompat_Dialog);
		//		GoogleAnalytics.getInstance(getActivity().getBaseContext()).dispatchLocalHits();
		//		Tracker t = ((IReminder)getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
		//		t.setScreenName("Category list fragment");
		//		t.send(new HitBuilders.AppViewBuilder().build());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		int catID = 0;
		ArrayList<CategoryItem> catList = new ArrayList<>();
		rootView = inflater.inflate(R.layout.fragment_category_dialog, null);
		//		mCatTitles = new ArrayList<>();
		//		mCatIcons = new ArrayList<>();
		Cursor mCursor = getActivity().getContentResolver().query(CategoriesTable.CONTENT_URI, null
				, CategoriesTable.COL_CATEGORY_TYPE + " = ? OR " + CategoriesTable.COL_CATEGORY_TYPE + " = ? OR " + CategoriesTable.COL_CATEGORY_TYPE + " = ?",
				new String[]{String.valueOf(TasksTable.TASK_TYPE_SHOPPING) , String.valueOf(TasksTable.TASK_TYPE_NORMAL),
						String.valueOf(TasksTable.TASK_TYPE_SCRIBBLE)},
				CategoriesTable.COL_CATEGORY_TYPE);

		if(isUpdate){
			Cursor cur = getActivity().getContentResolver().query(TasksTable.CONTENT_URI, null, 
					TasksTable.COL_TASK_ID + " = ? ",new String[]{String.valueOf(this.taskId)}, TasksTable.COL_TASK_CATEGORY_UID);

			if(cur.moveToNext())
				catID  = cur.getInt(cur.getColumnIndex(TasksTable.COL_TASK_CATEGORY_UID));
		}



		if(mCursor != null){
			while(mCursor.moveToNext()){
				int catType = mCursor.getInt(mCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_TYPE));
				//				mCatTitles.add(mCursor.getString(mCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_NAME)));
				//				mCatIcons.add(mCursor.getString(mCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_COLOR)));
				catList.add(new CategoryItem(mCursor.getString(mCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_NAME)),
						mCursor.getString(mCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_COLOR)),
						mCursor.getInt(mCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_ID)),
						mCursor.getInt(mCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_TYPE))));
				if(catID == mCursor.getInt(mCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_ID)))
					this.listPosition = mCursor.getPosition();

			}
			mCursor.close();

		}
		mCatList = (ListView) rootView.findViewById(R.id.list_task);
		mCatList.setOnItemClickListener(this);
		adapter = new CategoryListAdapter(getActivity(), catList);
		mCatList.setAdapter(adapter);
		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		// safety check
		if (getDialog() == null) {
			return;
		}
		getDialog().getWindow().setWindowAnimations(
				R.style.dialog_animation_fade);
	}

	//	private ArrayList<CategoryItem> getCatList(){
	//		ArrayList<CategoryItem> items = new ArrayList<CategoryItem>();
	//		Cursor mCursor = getActivity().getContentResolver().query(CategoriesTable.CONTENT_URI, null, CategoriesTable.COL_CATEGORY_ID +" > ? " , new String[]{String.valueOf("3")}, null);
	//		if(mCursor != null){
	//			while(mCursor.moveToNext()){
	//				items.add(new CategoryItem(mCursor.getString(mCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_NAME)), 
	//						mCursor.getString(mCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_COLOR)) ,
	//						Integer.valueOf(mCursor.getString(mCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_ID))))
	//						);
	//
	//			}
	//			mCursor.close();
	//		}
	//		return items ;
	//	}

	@Override
	public void onResume() {
		super.onResume();
		if(isUpdate)
			selectListPosition(listPosition);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

        CategoryItem item = (CategoryItem) view.getTag();
        selectListPosition(position);
        getDialog().dismiss();

        Task.getInstance().setActive(true);
        Task.getInstance().setCategoryLabel(item.getTitle());
        Task.getInstance().setiCategoryUID(item.getId());
        Task.getInstance().setiCategoryType(item.getType());

        if (item.getType() == TasksTable.TASK_TYPE_SCRIBBLE) {
            getFragmentManager().beginTransaction().replace(R.id.frame_container , new ScribbleFragment()).commit();
        } else {
            FragmentAddTask frag = new FragmentAddTask(isUpdate, taskId, item.getType());
            frag.show(getFragmentManager(), FragmentAddTask.class.getSimpleName());
        }
    }

	private void selectListPosition(int position) {
		mCatList.setItemChecked(position, true);
		mCatList.setSelection(position);
	}

}
