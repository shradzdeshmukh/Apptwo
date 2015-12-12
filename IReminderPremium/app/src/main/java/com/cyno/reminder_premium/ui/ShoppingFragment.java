package com.cyno.reminder_premium.ui;

import java.util.ArrayList;

import com.cyno.reminder_premium.R;
import com.cyno.reminder_premium.adapters.ShoppingAdapter;
import com.cyno.reminder.edittext.MaterialEditText;
import com.cyno.reminder.fab.ButtonFloat;
import com.cyno.reminder.models.CategoryItem;
import com.cynozer.reminder.utils.Task;
import com.cynozer.reminder_premium.contentproviders.TasksTable;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ShoppingFragment extends Fragment implements OnClickListener{

	private View rootView;
	private ArrayList<String> alList;
	private ShoppingAdapter adapter;
	private int taskID;
	private String title;
	private ButtonFloat mFab;
	private boolean isUpdate;
	private MaterialEditText editText;
	private int catID;

	public ShoppingFragment(int taskId,String title,boolean isUpdate , int catID) {
		this.taskID = taskId;
		this.title = title;
		this.isUpdate = isUpdate;
		this.catID = catID;
	}

	public ShoppingFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//		if(getActivity() instanceof MainActivity)
		//			((MainActivity)getActivity()).hideFab();
		rootView = inflater.inflate(R.layout.shopping_layout, null);

		TextView tvTitle = (TextView) rootView.findViewById(R.id.tv_shopping_title);
		tvTitle.setText(title);
		tvTitle.setBackgroundColor(Color.parseColor(CategoryItem.getDbColod(getActivity(), catID)));
		((MainActivity)getActivity()).changeToolbarColor(TasksTable.TASK_TYPE_SHOPPING, catID);
		ImageView ivAddItem = (ImageView) rootView.findViewById(R.id.iv_shoppin_add_item);


		mFab = (ButtonFloat) rootView.findViewById(R.id.fab);
		mFab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Task.getInstance().setNote(alList.toString().replace("[","").replace("]",""));
				ArrayList<Boolean> mList  = new ArrayList<>();
				for(int i = 0 ; i < alList.size() ; ++i){
					mList.add(false);
				}
				Task.getInstance().setShopDone(mList.toString().replace("[","").replace("]",""));

				FragmentCalender cal = new FragmentCalender(isUpdate , taskID);
				cal.show(getFragmentManager(), FragmentCalender.class.getSimpleName());
				getFragmentManager().popBackStack();
			}
		});
		alList = new ArrayList<String>();

		if(isUpdate){
			Cursor cur = getActivity().getContentResolver().query(TasksTable.CONTENT_URI, null, TasksTable.COL_TASK_ID + " = ? ", new String[]{String.valueOf(taskID)}, null);
			if(cur != null){
				if(cur.moveToNext()){
					String str = cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_NOTE));
					if(str != null){
						String []arr = str.split(",");
						for(int i = 0 ; i < arr.length ; ++i){
							alList.add(arr[i]);
						}
					}
				}
				cur.close();
			}
		}


		adapter = new ShoppingAdapter(getActivity() , android.R.id.text1 , this , alList);
		final ListView lv = (ListView) rootView.findViewById(R.id.shop_list);
		lv.setAdapter(adapter);

		editText = (MaterialEditText) rootView.findViewById(R.id.shop_edittext);
		editText.requestFocus();
		editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(editText.getText().toString().length() == 0)
					return false;

				alList.add(v.getText().toString());
				adapter.notifyDataSetChanged();
				editText.setText("");
				lv.smoothScrollToPosition(alList.size());
				return true;
			}
		});

		ivAddItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(editText.getText().toString().length() == 0)
					return;
				alList.add(editText.getText().toString());
				adapter.notifyDataSetChanged();
				editText.setText("");
				lv.smoothScrollToPosition(alList.size());
			}
		});
		return rootView;
	}

	@Override
	public void onStop() {
		super.onStop();
		editText.setEnabled(false);
		//		((MainActivity)getActivity()).showFab();
	}

	@Override
	public void onClick(View v) {
		alList.remove((int)v.getTag());
		adapter.notifyDataSetChanged();
	}

}
