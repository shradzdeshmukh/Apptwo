package com.cynozer.reminder_premium.contentproviders;

import com.cyno.reminder.constants.GlobalConstants;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class CategoriesTable 
{
	public static final String TABLE_CATEGORIES = "Categories";

	public static final Uri CONTENT_URI = Uri.parse("content://" + GlobalConstants.AUTHORITY
			+ "/" + TABLE_CATEGORIES);


	public static final String COL_CATEGORY_ID = "_id";
	public static final String COL_CATEGORY_NAME= "name";
	public static final String COL_CATEGORY_COLOR = "color";
	public static final String COL_CATEGORY_TYPE = "type";


	private static final String DATABASE_CREATE = "create table " 
			+ TABLE_CATEGORIES
			+ "(" 
			+ COL_CATEGORY_ID + " integer primary key autoincrement, "
			+ COL_CATEGORY_NAME + " text , "
			+ COL_CATEGORY_TYPE + " integer , "
			+ COL_CATEGORY_COLOR + " text  "
			+ ");";

	public static void onCreate(SQLiteDatabase mDatabase)
	{
		mDatabase.execSQL(DATABASE_CREATE);
	}
	public static void onUpdate(SQLiteDatabase mDatabase)
	{
	}


}
