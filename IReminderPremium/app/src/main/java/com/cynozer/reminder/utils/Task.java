package com.cynozer.reminder.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import com.cyno.reminder.constants.GlobalConstants;
import com.cyno.reminder.constants.IntentKeyConstants;
import com.cyno.reminder_premium.R;
import com.cyno.reminder_premium.service.PremiumAlarmService;
import com.cynozer.reminder_premium.contentproviders.CategoriesTable;
import com.cynozer.reminder_premium.contentproviders.TasksTable;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

public class Task {

	public static final int STATUS_PENDING = 100;
	public static final int STATUS_DONE = 200;

	public static enum RepeatTypes{REPEAT_EVERYDAY,REPEAT_WEEKLY,REPEAT_MONTHLY,REPEAT_YEARLY};

	private String title;
	private int status;
	private String categoryLabel;
	private String note;
	private long time;
	private int iCategoryUID;
	private int iCategoryType;
	private int iRepeatType=-1;
	private boolean isRepeat;
	private int uniqueKey;
	private String shopDone;
	private boolean active;
	private String color;
	private byte[] scribbleData;

	public static int NOT_IS_UPDATE = -1;

	private static Task instance = new Task();



	private Task(){

	}

	public static Task getInstance(){
		return instance;
	}





	public int getUniqueKey() {
		return uniqueKey;
	}

	public void setUniqueKey(int uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	public boolean isRepeat() {
		return isRepeat;
	}

	public void setRepeat(boolean isRepeat) {
		this.isRepeat = isRepeat;
	}

	public String getTitle() {
		return title;
	}



	public int getiCategoryType() {
		return iCategoryType;
	}

	public void setiCategoryType(int iCategoryType) {
		this.iCategoryType = iCategoryType;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getiRepeatType() {
		return iRepeatType;
	}

	public void setiRepeatType(int iRepeatType) {
		this.iRepeatType = iRepeatType;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCategoryLabel() {
		return categoryLabel;
	}

	public void setCategoryLabel(String category) {
		this.categoryLabel = category;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getiCategoryUID() {
		return iCategoryUID;
	}

	public void setiCategoryUID(int iCategory) {
		this.iCategoryUID = iCategory;
	}

	public byte[] getScribbleData() {
		return scribbleData;
	}

	public void setScribbleData(byte[] scribbleData) {
		this.scribbleData = scribbleData;
	}

	public void storeLocally(final Context context , int id){
		if(this.title == null)
			return;

		Log.d("rep", "id = " +id);
		getTaskColor(this.iCategoryUID, context);

		if(isRepeat){
			int repCount = 0;
			int daysToAdd = 0;
			int monthsToAdd = 0; 
			int yearToAdd = 0;
			RepeatTypes types = RepeatTypes.values()[iRepeatType];
			ArrayList<Task> alTasks = new ArrayList<>(); 
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(this.time);

			switch (types) {
			case REPEAT_EVERYDAY:
				repCount = 365;
				daysToAdd = 1;
				break;
			case REPEAT_WEEKLY:
				repCount = 52;
				daysToAdd = 7;
				break;
			case REPEAT_MONTHLY:
				repCount = 12;
				monthsToAdd = 7;
				break;
			case REPEAT_YEARLY:
				repCount = 10;
				yearToAdd = 1;
				break;

			default:
				break;
			}

			for(int i = 1 ; i <= repCount ; ++i){
				Task mTask = new Task();
				mTask.setTitle(this.getTitle());
				mTask.setNote(this.getNote());
				mTask.setCategoryLabel(this.getCategoryLabel());
				mTask.setiCategoryUID(this.getiCategoryUID());
				mTask.setUniqueKey(id);
				mTask.setStatus(this.getStatus());
				mTask.setColor(this.color);
				mTask.setiCategoryType(this.iCategoryType);
				mTask.setShopDone(this.getShopDone());
				mTask.setiRepeatType(RepeatTypes.REPEAT_EVERYDAY.ordinal());
				if(yearToAdd != 0)
					cal.add(Calendar.YEAR, 1);
				else if(monthsToAdd != 0)
					cal.add(Calendar.MONTH, 1);
				else 
					cal.add(Calendar.DAY_OF_MONTH, daysToAdd);
				mTask.setTime(cal.getTimeInMillis());
				mTask.setScribbleData(this.getScribbleData());
				alTasks.add(mTask);

			}

			final ArrayList<ContentProviderOperation> ops =new ArrayList<ContentProviderOperation>();
			for(Task mTask : alTasks){
				ops.add(ContentProviderOperation.newInsert(TasksTable.CONTENT_URI)
						.withValue(TasksTable.COL_TASK_STATUS, mTask.getStatus())
						.withValue(TasksTable.COL_TASK_CATEGORY_UID, mTask.getiCategoryUID())
						.withValue(TasksTable.COL_TASK_LABEL, mTask.getCategoryLabel())
						.withValue(TasksTable.COL_TASK_NAME, mTask.getTitle())
						.withValue(TasksTable.COL_TASK_NOTE, mTask.getNote())
						.withValue(TasksTable.COL_TASK_DATE, mTask.getTime())
						.withValue(TasksTable.COL_CAT_COLOR,mTask.getColor())
						.withValue(TasksTable.COL_TASK_CATEGORY_TYPE,mTask.getiCategoryType())
						.withValue(TasksTable.COL_SHOPPING_COMPLETED,mTask.getShopDone())
						.withValue(TasksTable.COL_TASK_REPEAT_TYPE, id)
						.withValue(TasksTable.COL_TASK_UNIQUE_KEY, mTask.getUniqueKey())
						.withValue(TasksTable.COL_SCRIBBLE, mTask.getScribbleData())
						.build());
			}
			try {

				context.getContentResolver().applyBatch(GlobalConstants.AUTHORITY, ops);
			} catch (RemoteException e) {
			} catch (OperationApplicationException e) {
			}

		}


		ContentValues values = new ContentValues();
		values.put(TasksTable.COL_TASK_NAME, this.title);
		values.put(TasksTable.COL_TASK_DATE, this.time);
		values.put(TasksTable.COL_TASK_STATUS, this.status);
		values.put(TasksTable.COL_TASK_NOTE, this.note);
		values.put(TasksTable.COL_TASK_LABEL, this.categoryLabel);
		values.put(TasksTable.COL_TASK_CATEGORY_TYPE, this.iCategoryType);
		values.put(TasksTable.COL_TASK_CATEGORY_UID, this.iCategoryUID);
		values.put(TasksTable.COL_SHOPPING_COMPLETED, this.shopDone);
		values.put(TasksTable.COL_CAT_COLOR, this.color);
		values.put(TasksTable.COL_TASK_UNIQUE_KEY, id);
		values.put(TasksTable.COL_SCRIBBLE, this.scribbleData);

		if(id == NOT_IS_UPDATE)
			context.getContentResolver().insert(TasksTable.CONTENT_URI, values);
		else
			context.getContentResolver().update(TasksTable.CONTENT_URI, values, TasksTable.COL_TASK_ID + " = ?", 
					new String[]{String.valueOf(id)});

		setAlarm(context);

		/*
		 * for testing
		 */
		Cursor cur = context.getContentResolver().query(TasksTable.CONTENT_URI, null, null, null, null);
		while(cur.moveToNext()){
			Log.d("test",cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_STATUS)) + "");
		}
		cur.close();
	}

	public void clearObject() {
		this.title  = null;
		this.categoryLabel = null;
		this.status = 0;
		this.time = 0;
		this.note = null;
		this.isRepeat = false;
		this.iRepeatType = -1;
		this.scribbleData = null;
	}

	public void updateTask(int taskId , ContentValues values , Context context){
		context.getContentResolver().update(TasksTable.CONTENT_URI, values, 
				TasksTable.COL_TASK_ID + " = ? ", new String[]{String.valueOf(taskId)});
		setAlarm(context);
	}

	public  void setAlarm(Context context){
		Intent service = new Intent(context, PremiumAlarmService.class);
		service.putExtra(IntentKeyConstants.KEY_TYPE_OF_ALARM, categoryLabel);
		service.setAction(PremiumAlarmService.CREATE);
		context.startService(service);
	}


	//	public void setDummyList(Context context){
	//		for(int i = 0 ; i <= 100 ; ++i){
	//			ContentValues values = new ContentValues();
	//			values.put(TasksTable.COL_TASK_NAME, "Test");
	//			values.put(TasksTable.COL_TASK_DATE, System.currentTimeMillis()+(60*1000*60));
	//			values.put(TasksTable.COL_TASK_STATUS, Task.STATUS_DONE);
	//			values.put(TasksTable.COL_TASK_NOTE,"Test");
	//			values.put(TasksTable.COL_TASK_LABEL, StringConstants.APPOINTMENTS);
	//			values.put(TasksTable.COL_TASK_CATEGORY, 3);
	//			context.getContentResolver().insert(TasksTable.CONTENT_URI, values);
	//		}
	//	}

	public  void doneInBulk(ArrayList<String> slectedIds,
			ContentResolver contentResolver , Context mContext) {
		ArrayList<ContentProviderOperation> ops =new ArrayList<ContentProviderOperation>();
		for(String id : slectedIds){
			ops.add(ContentProviderOperation.newUpdate(TasksTable.CONTENT_URI)
					.withValue(TasksTable.COL_TASK_STATUS, Task.STATUS_DONE)
					.withSelection(TasksTable.COL_TASK_ID + " = ? ", new String[]{id})
					.build());
		}
		try {
			contentResolver.applyBatch(GlobalConstants.AUTHORITY, ops);
		} catch (RemoteException e) {
		} catch (OperationApplicationException e) {
		}
		setAlarm(mContext);
	}

	public  void undoneInBulk(ArrayList<String> slectedIds,
			ContentResolver contentResolver , Context mContext) {
		ArrayList<ContentProviderOperation> ops =new ArrayList<ContentProviderOperation>();
		for(String id : slectedIds){
			ops.add(ContentProviderOperation.newUpdate(TasksTable.CONTENT_URI)
					.withValue(TasksTable.COL_TASK_STATUS, Task.STATUS_PENDING)
					.withSelection(TasksTable.COL_TASK_ID + " = ? ", new String[]{id})
					.build());
		}
		try {
			contentResolver.applyBatch(GlobalConstants.AUTHORITY, ops);
		} catch (RemoteException e) {
		} catch (OperationApplicationException e) {
		}
		setAlarm(mContext);
	}

	public  void deleteInBulk(ArrayList<String> slectedIds,ContentResolver contentResolver , Context mContext) {
		ArrayList<ContentProviderOperation> ops =new ArrayList<ContentProviderOperation>();
		for(String id : slectedIds){
			ops.add(ContentProviderOperation.newDelete(TasksTable.CONTENT_URI)
					.withSelection(TasksTable.COL_TASK_ID + " = ? ", new String[]{id})
					.build());
		}
		try {
			contentResolver.applyBatch(GlobalConstants.AUTHORITY, ops);
		} catch (RemoteException e) {
		} catch (OperationApplicationException e) {
		}

		setAlarm(mContext);
	}

	public Task getTask(int id , Context mContext){
		Task mTask = new Task();
		Cursor cur = mContext.getContentResolver().query(TasksTable.CONTENT_URI, null, TasksTable.COL_TASK_ID + " = ? ",new String[]{String.valueOf(id)}, null);
		if(cur != null){
			if(cur.moveToNext()){
				mTask.setTitle(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_NAME)));
				mTask.setTime(Long.valueOf(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_DATE))));
				mTask.setCategoryLabel(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_CATEGORY_UID)));
				mTask.setStatus(Integer.valueOf(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_STATUS))));
				mTask.setNote(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_NOTE)));
			}
			cur.close();
		}
		return mTask;
	}


	@SuppressLint("InlinedApi")
	public static boolean addToGoogleCal(Task mTask , Context mContext){
		try{
		ContentValues cv = new ContentValues();
		cv.put("calendar_id", 1);
		cv.put("title", mTask.getTitle());
		cv.put("dtstart", mTask.getTime());
		cv.put("hasAlarm", 1);
		cv.put("dtend", mTask.getTime() + (1000*5*60));
		cv.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getAvailableIDs()[0]);
		Uri newEvent ;
		newEvent = mContext.getContentResolver().insert(Uri.parse("content://com.android.calendar/events"), cv);

		if (newEvent != null) {
			long id = Long.parseLong( newEvent.getLastPathSegment() );
			ContentValues values = new ContentValues();
			values.put( "event_id", id );
			values.put( "method", 1 );
			values.put( "minutes", 15 ); // 15 minutes
			mContext.getContentResolver().insert( Uri.parse( "content://com.android.calendar/reminders" ), values );
		}
		}catch(Exception ex){
			Toast.makeText(mContext, mContext.getString(R.string.error), Toast.LENGTH_LONG).show();
			return false;
		}
		
		return true;
	}


	public String getShopDone() {
		return shopDone;
	}

	public void setShopDone(String shopDone) {
		this.shopDone = shopDone;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}


	public String getTaskColor(int catId, Context mContext){
		Cursor mCursor = mContext.getContentResolver().query(CategoriesTable.CONTENT_URI, 
				new String[]{CategoriesTable.COL_CATEGORY_COLOR},
				CategoriesTable.COL_CATEGORY_ID + " = ? ",
				new String[]{String.valueOf(catId)},null);

		if(mCursor != null){
			if(mCursor.moveToNext()){
				String color = mCursor.getString(mCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_COLOR));
				this.setColor(color);
				Log.d("color", color);
				return color;
			}
		}
		return null;
	}


}
