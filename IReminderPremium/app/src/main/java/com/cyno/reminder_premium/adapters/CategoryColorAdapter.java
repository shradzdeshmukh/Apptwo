package com.cyno.reminder_premium.adapters;


import com.cyno.reminder_premium.R;
import com.cynozer.reminder_premium.contentproviders.CategoriesTable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CategoryColorAdapter extends CursorAdapter {
	Context mContext;

	@SuppressLint("NewApi")
	public CategoryColorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		this.mContext = context;
	}

	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {
		TextView mCategoryTitle = (TextView) view.findViewById(R.id.tv_category_color_title);
		View mView = view.findViewById(R.id.color);
		mCategoryTitle.setText(cursor.getString(cursor.getColumnIndex(CategoriesTable.COL_CATEGORY_NAME)));
		changeColor(mView, cursor.getString(cursor.getColumnIndex(CategoriesTable.COL_CATEGORY_COLOR)));
		view.setTag(cursor.getString(cursor.getColumnIndex(CategoriesTable.COL_CATEGORY_ID)));
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		return View.inflate(mContext, R.layout.item_category_color, null);
	}
	
	private void changeColor(View mView , String color){
		int radius = 150;
		 ShapeDrawable biggerCircle= new ShapeDrawable( new OvalShape());
	        biggerCircle.setIntrinsicHeight( radius );
	        biggerCircle.setIntrinsicWidth( radius);
	        biggerCircle.setBounds(new Rect(0, 0, radius, radius));
	        biggerCircle.getPaint().setColor(Color.BLUE);
	        int padding = 100;

	        ShapeDrawable smallerCircle= new ShapeDrawable( new OvalShape());
	        smallerCircle.setIntrinsicHeight( 50 );
	        smallerCircle.setIntrinsicWidth( 50);
	        smallerCircle.setBounds(new Rect(0, 0, 10, 10));
	        smallerCircle.getPaint().setColor(Color.parseColor(color));
	        smallerCircle.setPadding(padding,padding,padding,padding);
	        Drawable[] d = {smallerCircle,biggerCircle};

	        LayerDrawable composite1 = new LayerDrawable(d);

	        mView.setBackgroundDrawable(composite1);  
	}
	
}
