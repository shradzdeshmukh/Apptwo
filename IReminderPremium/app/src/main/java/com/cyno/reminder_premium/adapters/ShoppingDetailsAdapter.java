package com.cyno.reminder_premium.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.cyno.reminder_premium.R;

/**
 * Created by kdinesh on 17-06-2015.
 */
public class ShoppingDetailsAdapter extends ArrayAdapter {
	private final Context context;
	private final ArrayList list;
	private OnCheckedChangeListener onCheck;
	private ArrayList<Boolean> alCheckedList;

	public ShoppingDetailsAdapter(Context context, int resource, OnCheckedChangeListener listner , ArrayList mList, ArrayList<Boolean> alCheckedList) {
		super(context, resource);
		this.onCheck = listner;
		this.context = context;
		this.list = mList;
		this.alCheckedList = alCheckedList;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tvItem = null;
		CheckBox cbItem = null;
		if(convertView == null) {
			convertView = View.inflate(context, R.layout.shop_details_item, null);
		}
		tvItem = (TextView) convertView.findViewById(R.id.shop_item_text);
		cbItem = (CheckBox) convertView.findViewById(R.id.shop_check_box);
		cbItem.setTag(position);

		tvItem.setText(list.get(position).toString());
		cbItem.setOnCheckedChangeListener(onCheck);
		if((boolean)alCheckedList.get(position)){
			tvItem.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			cbItem.setChecked(true);
		}else{
			tvItem.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
			cbItem.setChecked(false);
		}
		return convertView;
	}
}
