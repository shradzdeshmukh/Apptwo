package com.cynozer.reminder_premium.contentproviders;

import com.cyno.reminder.constants.GlobalConstants;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class TasksTable 
{

	public static final int TASK_TYPE_COMMING_UP = 10;

	public static final int TASK_TYPE_NEXT_SEVEN_DAYS = 20;

	public static final int TASK_TYPE_ALL_TASKS = 40;

	public static final int TASK_TYPE_SHOPPING = 50;

	public static final int TASK_TYPE_SCRIBBLE = 51;

	public static final int TASK_TYPE_NORMAL = 60;


	public static final String TABLE_TASKS = "Tasks";

	public static final Uri CONTENT_URI = Uri.parse("content://" + GlobalConstants.AUTHORITY
			+ "/" + TABLE_TASKS);


	public static final String COL_TASK_ID = "_id";
	public static final String COL_TASK_UNIQUE_KEY = "_UID";
	public static final String COL_TASK_LABEL = "Label";
	public static final String COL_TASK_NAME = "Name";
	public static final String COL_TASK_DATE = "Date";
	public static final String COL_TASK_EMAIL = "Email";
	public static final String COL_TASK_PHONE = "Phone";
	public static final String COL_TASK_STATUS = "Status";
	public static final String COL_TASK_NOTE = "Note";
	public static final String COL_TASK_CATEGORY_UID = "Cat";
	public static final String COL_TASK_CATEGORY_TYPE = "Type";
	public static final String COL_TASK_REPEAT_TYPE = "Rep";
	public static final String COL_SHOPPING_COMPLETED = "ShopCompleted";
	public static final String COL_CAT_COLOR = "Color";
    public static final String COL_SCRIBBLE = "scribble";


    private static final String DATABASE_CREATE = "create table "
			+ TABLE_TASKS
			+ "(" 
			+ COL_TASK_ID + " integer primary key autoincrement, "
			+ COL_TASK_UNIQUE_KEY + " integer , " 
			+ COL_TASK_LABEL + " text not null, " 
			+ COL_TASK_CATEGORY_UID + " integer , "
			+ COL_TASK_CATEGORY_TYPE + " integer , "
			+ COL_TASK_NAME + " text not null, " 
			+ COL_TASK_REPEAT_TYPE + " integer default (-1) , " 
			+ COL_TASK_DATE + " integer not null , "
			+ COL_TASK_STATUS + " integer ," 
			+ COL_TASK_EMAIL + " text , " 
			+ COL_TASK_PHONE + " text , " 
			+ COL_TASK_NOTE + " text ,  "
			+ COL_SHOPPING_COMPLETED + " text, " 
			+ COL_CAT_COLOR + " text, "
            + COL_SCRIBBLE + " blob  "
            + ");";


	public static void onCreate(SQLiteDatabase mDatabase)
	{
		mDatabase.execSQL(DATABASE_CREATE);
	}

	private static final java.lang.String DATABASE_UPGRADE_V2 = " ALTER TABLE "+TABLE_TASKS+
			" ADD COLUMN "+COL_SCRIBBLE+" blob default '' ;";

	public static void onUpdate(SQLiteDatabase mDatabase){
		mDatabase.execSQL(DATABASE_UPGRADE_V2);
	}


}
