package com.cynozer.reminder_premium.contentproviders;


import com.cyno.reminder.constants.GlobalConstants;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;

public class TasksContentProvider extends ContentProvider{
	
	private TasksDbHelper mDatabase;
	
	private static final int ALL_TASKS = 1;
	private static final int SINGLE_TASK = 2;
	private static final int ALL_CATEGORIES = 3;
	private static final int SINGLE_CATEGORY = 4;
	
	 public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
		      + "/todos";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
		      + "/todo";
	
	private static final UriMatcher mURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static
	{
		mURIMatcher.addURI(GlobalConstants.AUTHORITY, TasksTable.TABLE_TASKS, ALL_TASKS);
		mURIMatcher.addURI(GlobalConstants.AUTHORITY, TasksTable.TABLE_TASKS+"/#", SINGLE_TASK);
		mURIMatcher.addURI(GlobalConstants.AUTHORITY, CategoriesTable.TABLE_CATEGORIES, ALL_CATEGORIES);
		mURIMatcher.addURI(GlobalConstants.AUTHORITY, CategoriesTable.TABLE_CATEGORIES+"/#", SINGLE_CATEGORY);
	}

	

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		
		    String sTable = "";
		    String sColumns = null;
		    switch ( mURIMatcher.match(uri)) {
		    case ALL_TASKS:
			     sTable = TasksTable.TABLE_TASKS;
		      break;
		    case SINGLE_TASK:
			     sTable = TasksTable.TABLE_TASKS;
			     sColumns = TasksTable.COL_TASK_ID;		  
		    	break;
		    case ALL_CATEGORIES:
			     sTable = CategoriesTable.TABLE_CATEGORIES;
		      break;
		    case SINGLE_CATEGORY:
			     sTable = CategoriesTable.TABLE_CATEGORIES;
			     sColumns = CategoriesTable.COL_CATEGORY_ID;		  
		    	break;
		    	
		    default:
		      throw new IllegalArgumentException("Unknown URI: " + uri);
		    }if (sColumns != null) {
				selection = sColumns + "=" + uri.getPathSegments().get(1)
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
			}

			try {
				SQLiteDatabase db = mDatabase.getWritableDatabase();
				int count = db.delete(sTable, selection, selectionArgs);
				getContext().getContentResolver().notifyChange(uri, null);
				return count;
			} catch (SQLException e) {
			}
			return -1 ;	
		}


	@Override
	public String getType(Uri uri) {
		switch(mURIMatcher.match(uri)){
		case ALL_TASKS:
			return CONTENT_TYPE;
		case SINGLE_TASK:
			return CONTENT_ITEM_TYPE;
		case ALL_CATEGORIES:
			return CONTENT_TYPE;
		case SINGLE_CATEGORY:
			return CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown Uri "+uri);
		}
	}


	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		String sTable = "";
		String sColumn = null;
		Uri mContentUri = null;

		switch(mURIMatcher.match(uri)){
		case ALL_TASKS :
			sTable = TasksTable.TABLE_TASKS;
			mContentUri = TasksTable.CONTENT_URI;
			break;
		case SINGLE_TASK:
			sTable =  TasksTable.TABLE_TASKS;
			sColumn = TasksTable.COL_TASK_ID;
			mContentUri = TasksTable.CONTENT_URI;
			break;
		case ALL_CATEGORIES:
			sTable = CategoriesTable.TABLE_CATEGORIES;
			mContentUri = CategoriesTable.CONTENT_URI;
			break;
		case SINGLE_CATEGORY:
			sTable =  CategoriesTable.TABLE_CATEGORIES;
			sColumn = CategoriesTable.COL_CATEGORY_ID;
			mContentUri = CategoriesTable.CONTENT_URI;
			break;
		default:
			throw new IllegalArgumentException("Unknown Uri "+uri);
		}


		long rowid;
		try {
			
			SQLiteDatabase db = mDatabase.getWritableDatabase();
			if (values == null) {
				values = new ContentValues();
			}
			
			rowid = db.insert(sTable, sColumn, values);
			if (rowid > 0){
				Uri oUri = ContentUris.withAppendedId(mContentUri, rowid);
				getContext().getContentResolver().notifyChange(oUri, null);
				return oUri;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		throw new SQLException("Failed to insert row into " + uri);

	}

	@Override
	public boolean onCreate() {
		mDatabase = new TasksDbHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
			String sTable = "";
			String sColumn = null;
			String sSort = "";
			switch(mURIMatcher.match(uri)){
			case ALL_TASKS:
				sTable = TasksTable.TABLE_TASKS;
				sSort = TasksTable.COL_TASK_DATE;
				break;
			case SINGLE_TASK:
				sTable = TasksTable.TABLE_TASKS;
				sColumn = TasksTable.COL_TASK_ID;
				break;
			case ALL_CATEGORIES:
				sTable = CategoriesTable.TABLE_CATEGORIES;
				sSort = CategoriesTable.COL_CATEGORY_ID;
				break;
			case SINGLE_CATEGORY:
				sTable = CategoriesTable.TABLE_CATEGORIES;
				sColumn = CategoriesTable.COL_CATEGORY_ID;
				break;

			default:
				throw new IllegalArgumentException("Unknown Uri "+uri);
			}

			SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
			builder.setTables(sTable);
			String orderBy;
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = sSort;
			} else {
				orderBy = sortOrder;
			}
			if (sColumn != null) {
				selection = sColumn + "=" + uri.getPathSegments().get(1)
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
			}

			SQLiteDatabase db;
			try {
				db = mDatabase.getReadableDatabase();
				Cursor cursor = builder.query(db, projection, selection,
						selectionArgs, null, null, orderBy);

				if (cursor != null) {
					cursor.setNotificationUri(getContext().getContentResolver(), uri);
				}

				return cursor;
			} catch (SQLException e) {
			}
			return null ;	
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
			String sTable = "";
			String sColumn = null;
			Uri mContentUri = null;
			switch(mURIMatcher.match(uri)){
			case ALL_TASKS :
				sTable = TasksTable.TABLE_TASKS;
				mContentUri  = TasksTable.CONTENT_URI;
				break;
			case SINGLE_TASK:
				sTable =  TasksTable.TABLE_TASKS;
				sColumn = TasksTable.COL_TASK_ID;
				mContentUri = TasksTable.CONTENT_URI;
				break;
			case ALL_CATEGORIES:
				sTable = CategoriesTable.TABLE_CATEGORIES;
				mContentUri  = CategoriesTable.CONTENT_URI;
				break;
			case SINGLE_CATEGORY:
				sTable =  CategoriesTable.TABLE_CATEGORIES;
				sColumn = CategoriesTable.COL_CATEGORY_ID;
				mContentUri = CategoriesTable.CONTENT_URI;
				break;
			default:
				throw new IllegalArgumentException("Unknown Uri "+uri);
			}
			try {
				if (sColumn != null) {
					selection = sColumn	+ "=" + uri.getPathSegments().get(1)
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection	+ ')' : "");
				}

			
				SQLiteDatabase db = mDatabase.getWritableDatabase();
				int count = db.update(sTable, values, selection, selectionArgs);
			//	getContext().getContentResolver().notifyChange(uri, null);
				return count;
			} catch (SQLException e) {
			}
			return 0 ;
	}
	

	private static class TasksDbHelper extends SQLiteOpenHelper
	{
		
		

		public TasksDbHelper(Context context) {
			super(context, GlobalConstants.DATABASE_NAME, null, GlobalConstants.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			TasksTable.onCreate(db);
			CategoriesTable.onCreate(db);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			TasksTable.onUpdate(db);
			CategoriesTable.onUpdate(db);
			
		}
		
	}



}
