package com.cyno.reminder_premium.adapters;

import java.util.ArrayList;
import java.util.TreeSet;

import com.cyno.reminder_premium.R;
import com.cynozer.reminder.utils.TodayTaskModel;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlanDayAdapter extends BaseAdapter {

	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SEPARATOR = 1;
	private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

	private ArrayList<TodayTaskModel> mData = new ArrayList<TodayTaskModel>();
	private LayoutInflater mInflater;

	private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();

	public PlanDayAdapter(Context context) {
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addItem(final TodayTaskModel item) {
		mData.add(item);
		notifyDataSetChanged();
	}

	public void addSeparatorItem(final TodayTaskModel item) {
		mData.add(item);
		// save separator position
		mSeparatorsSet.add(mData.size() - 1);
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	public int getCount() {
		return mData.size();
	}

	public TodayTaskModel getItem(int position) {
		return mData.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int type = getItemViewType(position);
		System.out.println("getView " + position + " " + convertView + " type = " + type);
		if (convertView == null) {
			holder = new ViewHolder();
			switch (type) {
			case TYPE_ITEM:
				convertView = mInflater.inflate(R.layout.plan_today_item, null);
				holder.textView = (TextView)convertView.findViewById(R.id.plan_today_title);
				break;
			case TYPE_SEPARATOR:
				convertView = mInflater.inflate(R.layout.plan_today_sep, null);
				holder.textView = (TextView)convertView.findViewById(R.id.plan_today_sep);
				break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		CharSequence date = DateUtils.getRelativeTimeSpanString(mData.get(position).getTime());
		if(mData.get(position).isSeperator())
			date = "";
		holder.textView.setText(mData.get(position).getTitle() + " " + date);

		return convertView;
	}


	public static class ViewHolder {
		public TextView textView;
	}

	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
}
