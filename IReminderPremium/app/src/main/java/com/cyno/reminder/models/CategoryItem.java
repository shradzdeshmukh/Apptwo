package com.cyno.reminder.models;

import com.cynozer.reminder_premium.contentproviders.CategoriesTable;
import com.cynozer.reminder_premium.contentproviders.TasksTable;

import android.content.Context;
import android.database.Cursor;

public class CategoryItem {

	private String title;
	private String icon;
	private int id;
	private boolean isShopping;
	private int type;


	public CategoryItem(){}


	public CategoryItem(String title, String icon , int id ,  int type,boolean isShopping){
		this.title = title;
		this.icon = icon;
		this.id = id;
		this.isShopping = isShopping;
		this.type = type;
	}




	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public boolean isShopping() {
		return isShopping;
	}


	public void setShopping(boolean isShopping) {
		this.isShopping = isShopping;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getTitle(){
		return this.title;
	}

	public String getIcon(){
		return this.icon;
	}



	public void setTitle(String title){
		this.title = title;
	}

	public void setIcon(String icon){
		this.icon = icon;
	}

	public static String getDbColod(Context mContext , int id){
		Cursor mCursor = mContext.getContentResolver().query(CategoriesTable.CONTENT_URI,
				new String[]{CategoriesTable.COL_CATEGORY_COLOR}, CategoriesTable.COL_CATEGORY_ID + " = ? ", 
				new String[]{String.valueOf(id)}
		, null);
		if(mCursor != null){
			if(mCursor.moveToNext())
				return mCursor.getString(mCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_COLOR));
			mCursor.close();
		}
		return null;
	}

	public static String getCatName(Context mContext ,int type, int id){
		Cursor mCursor = null;
		switch (type) {
		case TasksTable.TASK_TYPE_NORMAL:
			mCursor = mContext.getContentResolver().query(CategoriesTable.CONTENT_URI,
					new String[]{CategoriesTable.COL_CATEGORY_COLOR,CategoriesTable.COL_CATEGORY_NAME},
					CategoriesTable.COL_CATEGORY_ID + " = ? ", new String[]{String.valueOf(id)}
			, null);
			break;
		default:
			mCursor = mContext.getContentResolver().query(CategoriesTable.CONTENT_URI,
					new String[]{CategoriesTable.COL_CATEGORY_COLOR,CategoriesTable.COL_CATEGORY_NAME},
					CategoriesTable.COL_CATEGORY_TYPE + " = ? ", new String[]{String.valueOf(type)}
			, null);
			break;
		}
		if(mCursor != null){
			if(mCursor.moveToNext())
				return mCursor.getString(mCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_NAME));
			mCursor.close();
		}
		return null;
	}

}
