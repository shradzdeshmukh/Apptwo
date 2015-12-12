package com.cyno.reminder_premium.adapters;


import java.util.ArrayList;

import com.cyno.reminder.models.CategoryItem;
import com.cyno.reminder_premium.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryListAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<CategoryItem> mItems;
	
	public CategoryListAdapter(Context context, ArrayList<CategoryItem> items){
		this.context = context;
		this.mItems = items;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {		
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_category_list, null);
        }
         
        View imgIcon =  convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.tv_tasklist_title);
         
        imgIcon.setBackgroundColor(Color.parseColor(mItems.get(position).getIcon()));        
        txtTitle.setText(mItems.get(position).getTitle());
        
        convertView.setTag(mItems.get(position));
        return convertView;
	}

}
