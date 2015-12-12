package com.cyno.reminder_premium.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.cyno.reminder_premium.R;

/**
 * Created by kdinesh on 17-06-2015.
 */
public class ShoppingAdapter extends ArrayAdapter {
    private final Context context;
    private final ArrayList list;
    private View.OnClickListener onClick;

    public ShoppingAdapter(Context context, int resource, View.OnClickListener listner , ArrayList mList) {
        super(context, resource);
        this.onClick = listner;
        this.context = context;
        this.list = mList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tvItem = null;
        ImageView ivItem = null;
        if(convertView == null) {
            convertView = View.inflate(context, R.layout.shop_item, null);
        }
            tvItem = (TextView) convertView.findViewById(R.id.shop_item_text);
            ivItem = (ImageView) convertView.findViewById(R.id.shop_item_image);
            ivItem.setTag(position);

        tvItem.setText(list.get(position).toString());
        ivItem.setOnClickListener(onClick);
        return convertView;
    }
}
