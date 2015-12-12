package com.cyno.reminder_premium.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cyno.reminder_premium.R;
import com.cynozer.reminder.utils.Tools;

import java.util.ArrayList;


public class ToolsGridAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<Tools> gridItems;
	
	public ToolsGridAdapter(Context context, ArrayList<Tools> navDrawerItems){
		this.context = context;
		this.gridItems = navDrawerItems;
	}

	@Override
	public int getCount() {
		return gridItems.size();
	}

	@Override
	public Object getItem(int position) {		
		return gridItems.get(position);
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
            convertView = mInflater.inflate(R.layout.grid_text_item, null);
        }
         
        TextView txtTitle = (TextView) convertView.findViewById(R.id.grid_item_text_title);
        txtTitle.setText(gridItems.get(position).getTitle());
        txtTitle.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(gridItems.get(position).getImage()), null, null);
        
        return convertView;
	}

}
