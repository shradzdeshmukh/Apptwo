package com.cyno.reminder_premium.ui;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.cyno.reminder.constants.IntentKeyConstants;
import com.cyno.reminder.constants.StringConstants;
import com.cyno.reminder.models.CategoryItem;
import com.cyno.reminder_premium.R;
import com.cyno.reminder_premium.adapters.CategoryCursorListAdapter;
import com.cyno.reminder_premium.calender.CalendarDay;
import com.cyno.reminder_premium.calender.MaterialCalendarView;
import com.cyno.reminder_premium.service.PremiumAlarmService;
import com.cynozer.reminder.utils.AppUtils;
import com.cynozer.reminder_premium.contentproviders.CategoriesTable;
import com.cynozer.reminder_premium.contentproviders.TasksTable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements OnClickListener , LoaderCallbacks<Cursor> {

	protected static final int SETTINGS = 10;
	protected static final int EMPTY_FRAGMENT = 200;
	protected static final int ADD_TASK = 230;
	private static final int NAV_LIST_COUNT = 9;
	private static final String ALARM_SET = null;
	private static final String KEY_CAT_POPULATED = "populate";
	public static final int LOADER_ID = 129;
	public static final String HAS_CATEGORIES = "has_cate";

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	//	private ButtonFloat mFab;
	private ArrayList<Object> alCount;

	// slide menu items
	private String[] navMenuTitles;
	//	private TypedArray navMenuIcons;

	private CategoryCursorListAdapter adapter;
	//	private ImageView ivProfilePic;
	private boolean showMainOnBack;
	private Toolbar toolbar;
	private ColorChangeReciever reciever;
	private boolean isSettings;
	private boolean isCatCustomise;
	private boolean isNotif;
	private ArrayList<Object> mCatList;


	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mCatList = new ArrayList<>();
		Calendar cala = Calendar.getInstance();
		cala.setTimeInMillis(System.currentTimeMillis());
		//		setDailyAlarm(this);
//		if(Build.VERSION.SDK_INT >= 21){
//			Window window = getWindow();
//			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			window.setStatusBarColor(getResources().getColor(R.color.primary_dark));
//			// inside your activity (if you did not enable transitions in your theme)
//			//			window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//			//			window.setExitTransition(new Explode());
//			//			window.setEnterTransition(new Fade());
//		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSupportLoaderManager().initLoader(LOADER_ID, null, this);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		checknCallDailyAlarm();
		//		mFab =  (ButtonFloat) findViewById(R.id.fab);
		//		mFab.setOnClickListener(this);

		//		getCountList();
		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		//		populateCategoryDb(navMenuTitles);
		//		populateCategoryDb(navMenuTitles);
		// nav drawer icons from resources
		//		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
		adapter = new CategoryCursorListAdapter(this, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		//		navMenuIcons.recycle();
		mDrawerList.setAdapter(adapter);



		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				toolbar, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
				) {
			public void onDrawerClosed(View view) {
			}

			public void onDrawerOpened(View drawerView) {
			}
		};

		mDrawerToggle.syncState();


		mDrawerLayout.setDrawerListener(mDrawerToggle);

		//		ivProfilePic = (ImageView) findViewById(R.id.profile_img);

		mDrawerList.postDelayed(new Runnable() {

			@Override
			public void run() {
				displayView(TasksTable.TASK_TYPE_COMMING_UP , 1);
			}
		}, 100);


		TextView tvDate = (TextView) findViewById(R.id.tv_date);
		TextView tvMonth = (TextView) findViewById(R.id.tv_month);

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat mFormatDate = new SimpleDateFormat("dd" , Locale.getDefault());
		SimpleDateFormat mFormatMonth = new SimpleDateFormat("MMMM" , Locale.getDefault());

		tvDate.setText(mFormatDate.format(cal.getTime()));
		tvMonth.setText(mFormatMonth.format(cal.getTime()));

		Intent mIntent = getIntent();
		if(mIntent != null){
			if(mIntent.getAction() != null){
				if(mIntent.getAction().equals(StringConstants.NOTIF_ACTION)){
					int cat = Integer.valueOf(mIntent.getIntExtra(IntentKeyConstants.KEY_TYPE_OF_ALARM ,
							TasksTable.TASK_TYPE_ALL_TASKS));
					displayView(TasksTable.TASK_TYPE_NORMAL, cat);
					isNotif = true;
				}
			}
		}

		MaterialCalendarView mCal = (MaterialCalendarView) findViewById(R.id.calendarView);
		if(mCal != null)
			mCal.setSelectedDate(new CalendarDay(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH)));

		//		GoogleAnalytics.getInstance(getBaseContext()).dispatchLocalHits();
		//		Tracker t = ((IReminder)getApplication()).getTracker(TrackerName.APP_TRACKER);
		//		t.setScreenName("Main activity");
		//		t.send(new HitBuilders.AppViewBuilder().build());
		reciever = new ColorChangeReciever();
	}


	//	private void populateCategoryDb(String[] titles) {
	//		Intent mIntent = getIntent();
	//		if(mIntent == null){
	//			return;
	//		}
	//			String []mColors = getResources().getStringArray(R.array.core_colors);
	//			for(int index = 0 ; index < titles.length; ++index){
	//				ContentValues values = new ContentValues();
	//				values.put(CategoriesTable.COL_CATEGORY_NAME, titles[index]);
	//				values.put(CategoriesTable.COL_CATEGORY_COLOR, mColors[index]);
	//				switch (index) {
	//				case 0:
	//					values.put(CategoriesTable.COL_CATEGORY_TYPE, TasksTable.TASK_TYPE_COMMING_UP);
	//					break;
	//				case 1:
	//					values.put(CategoriesTable.COL_CATEGORY_TYPE, TasksTable.TASK_TYPE_NEXT_SEVEN_DAYS);
	//					break;
	//				case 2:
	//					values.put(CategoriesTable.COL_CATEGORY_TYPE, TasksTable.TASK_TYPE_ALL_TASKS);
	//					break;
	//				case 3:
	//					values.put(CategoriesTable.COL_CATEGORY_TYPE, TasksTable.TASK_TYPE_SHOPPING);
	//					break;
	//				default:
	//					values.put(CategoriesTable.COL_CATEGORY_TYPE, TasksTable.TASK_TYPE_NORMAL);
	//					break;
	//				}
	//				getContentResolver().insert(CategoriesTable.CONTENT_URI, values);
	//			}
	//
	//			SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
	//			edit.putBoolean(KEY_CAT_POPULATED, true);
	//			edit.commit();
	//	}


	private void checknCallDailyAlarm() {
		if(!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(ALARM_SET, false)){
			SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
			setDailyAlarm(this);
			edit.putBoolean(ALARM_SET, true);
			edit.commit();
		}
	}


	@Override
	public void onStart() {
		super.onStart();
		registerReceiver(reciever, new IntentFilter(FragmentChangeCatColor.ACTION_REFRESH_LIST));
		//		registerReceiver(LogoutReciever, new IntentFilter(LoginActivity.LOGOUT_BROADCAST));
		//		SharedPreferences pref = getSharedPreferences(StringConstants.PREFERENCE_PROFILE, MODE_PRIVATE);
		//		ImageLoader.getInstance().displayImage(pref.getString(StringConstants.KEY_USER_IMAGE, null), ivProfilePic);
	}

	@Override
	public void onStop() {
		super.onStop();
		unregisterReceiver(reciever);
		//		unregisterReceiver(LogoutReciever);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	@SuppressLint("NewApi")
	protected void displayView(int type , int catId) {
		if(isNotif){
			isNotif = false;
			return;
		}
		if(isSettings){
			isSettings = false;
			getFragmentManager().popBackStack();
		}
		String title = CategoryItem.getCatName(this,type, catId);
		Fragment fragment = null;
		FragmentManager fragmentManager = getSupportFragmentManager();
		switch (type) {
		case ADD_TASK:
			CategoryListFragment list = new CategoryListFragment();
			list.show(fragmentManager, CategoryListFragment.class.getSimpleName());
			return;

		default:

			fragment =	new TaskListFragment(type,catId ,title );
			fragmentManager.beginTransaction()
			.replace(R.id.frame_container, fragment, TaskListFragment.class.getSimpleName()).commit();

			break;
		}

		selectNavPosition(type , catId , title);
		changeToolbarColor(type , catId);
	}

	@SuppressLint("NewApi")
	protected void changeToolbarColor(int type , int catID) {	

		//		switch (type) {
		//		case TasksTable.TASK_TYPE_COMMING_UP:
		//			catID = 0;
		//			break;
		//		case TasksTable.TASK_TYPE_NEXT_SEVEN_DAYS:
		//			catID = 1;
		//			break;
		//		case TasksTable.TASK_TYPE_ALL_TASKS:
		//			catID = 2;
		//			break;
		//		default:
		//			break;
		//		}
		String clr =  CategoryItem.getDbColod(this, catID);
		if(clr == null)
			return;
		int color =Color.parseColor(clr);
		toolbar.setBackgroundColor(color);
		changeStatusBarColor(color);
	}

	@SuppressLint("NewApi")
	private void changeStatusBarColor(int color){
		float[] hsv = new float[3];
		//				int color = Color.parseColor(mCursor.getString(mCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_COLOR)));
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.8f; // value component
		color = Color.HSVToColor(hsv);
		Window mWindow = getWindow();
		if(Build.VERSION.SDK_INT>=21)
			mWindow.setStatusBarColor(color);

	}



	private void selectNavPosition(int type , int catID , String title) {
		//		switch (type) {
		//		case TasksTable.TASK_TYPE_COMMING_UP:
		//			mDrawerList.setItemChecked(mCatList.indexOf(type), true);
		//			mDrawerList.setSelection(mCatList.indexOf(type));
		//			break;
		//		case TasksTable.TASK_TYPE_NEXT_SEVEN_DAYS:
		//			mDrawerList.setItemChecked(mCatList.indexOf(type), true);
		//			mDrawerList.setSelection(mCatList.indexOf(type));
		//			break;
		//		case TasksTable.TASK_TYPE_ALL_TASKS:
		//			mDrawerList.setItemChecked(mCatList.indexOf(type), true);
		//			mDrawerList.setSelection(mCatList.indexOf(type));
		//			break;
		//		case TasksTable.TASK_TYPE_SHOPPING:
		//			mDrawerList.setItemChecked(mCatList.indexOf(type), true);
		//			mDrawerList.setSelection(mCatList.indexOf(type));
		//			break;
		//
		//		default:
		//			mDrawerList.setItemChecked(mCatList.indexOf(catID), true);
		//			mDrawerList.setSelection(mCatList.indexOf(catID));
		//			break;
		//		}

		mDrawerList.setItemChecked(mCatList.indexOf(catID), true);
		mDrawerList.setSelection(mCatList.indexOf(catID));

		setTitle(title);
		mDrawerLayout.closeDrawer(Gravity.START);
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
	ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(Integer.valueOf((String)view.getTag(R.string.cat_type)) , 
					Integer.valueOf((String)view.getTag(R.string.cat_id)));
		}
	}



	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//		startActivity(new Intent(this , PlanDayActivity.class));
		//		Task.getInstance().setDummyList(this);
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		//		case R.id.action_login:
		//			Intent mIntentLogin = new Intent(this , SplashActivity.class);
		////			mIntentLogin.setAction(LoginActivity.ACTION_SIGNIN);
		//			startActivity(mIntentLogin);
		//			break;
		//		case R.id.action_logout:
		////			Intent mIntentLogout = new Intent(this , LoginActivity.class);
		////			mIntentLogout.setAction(LoginActivity.ACTION_SIGNOUT);
		////			startActivity(mIntentLogout);
		//			break;
		case R.id.action_settings:
//			if(Build.VERSION.SDK_INT >= 11){
//				toolbar.setBackgroundColor(getResources().getColor(R.color.primary));
//				changeStatusBarColor(getResources().getColor(R.color.primary));
//				isSettings = true;
//				showMainOnBack = true;
//				Fragment frag = getSupportFragmentManager().findFragmentById(R.id.frame_container);
//				getSupportFragmentManager().beginTransaction().remove(frag).commit();
//				getFragmentManager().beginTransaction().addToBackStack(SettingsFragment.class.getSimpleName()).replace(R.id.frame_container, new SettingsFragment11()).commit();
//				//				startActivity(new Intent(this , SettingsActivity.class));
//			}else		
//				getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new SettingsFragment()).commit();
//			//			hideFab();
			startActivity(new Intent(this , SettingsPrefActivity.class));
			break;

		case R.id.action_customise_category:
			isCatCustomise = true;
			getSupportFragmentManager().beginTransaction().addToBackStack(FragmentChangeCatColor.class.getSimpleName())
			.replace(R.id.frame_container, new FragmentChangeCatColor()).commit();
			break;

		default:
			break;
		}

		//TODO support
		invalidateOptionsMenu();
		return true;

	}



	@SuppressLint("NewApi")
	@Override
	protected void onRestart() {
		super.onRestart();
		invalidateOptionsMenu();
	}

	//	private ArrayList<NavDrawerItem> getNavList(){
	//		ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();
	//		for(int i = 0 ; i <= navMenuTitles.length -1 ; ++i){
	//			navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], 
	//					navMenuIcons.getResourceId(i, -1) ,  !String.valueOf(alCount.get(i)).equals("0") , String.valueOf(alCount.get(i))));
	//		}
	//		return navDrawerItems;
	//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(AppUtils.isSignedIn(this))
			getMenuInflater().inflate(R.menu.main_signed_in, menu);
		else
			getMenuInflater().inflate(R.menu.main_signed_out, menu);
		return true;
	}




	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(Gravity.START))
			mDrawerLayout.closeDrawer(Gravity.START);
		else if(showMainOnBack){
			getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.frame_container)).commit();
			showMainOnBack = false;
			displayView(TasksTable.TASK_TYPE_COMMING_UP , -1);
		}else if(isCatCustomise){
			//			getFragmentManager().beginTransaction().repla).commit();
			isCatCustomise = false;
			displayView(TasksTable.TASK_TYPE_COMMING_UP , -1);
		}else{
			super.onBackPressed();
		}
	}


	public void refreshNavDrawer(int type, int catId ){
		//		getCountList();
		//		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
		//		adapter = new  CategoryCursorListAdapter(this, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		//		mDrawerList.setAdapter(adapter);
		getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
		adapter.notifyDataSetChanged();
		mDrawerList.setAdapter(adapter);
		displayView(type, catId);
	}


	//	@SuppressLint("NewApi")
	//	BroadcastReceiver LogoutReciever = new  BroadcastReceiver(){
	//
	//		@Override
	//		public void onReceive(Context context, Intent intent) {
	//			if(intent.getAction().equals(LoginActivity.LOGOUT_BROADCAST)){
	////				ivProfilePic.setImageDrawable(getDrawable(R.drawable.ic_profile_pic));
	//				invalidateOptionsMenu();
	//			}
	//		}
	//	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fab:
			displayView(ADD_TASK , -1);
			break;

		default:
			break;
		}
	}

	//	private void getCountList(){
	//		alCount = new ArrayList<>();
	//		Cursor cursor = null;
	//		Cursor cursor1 = null;
	//		Cursor cur = null;
	//		for(int i = 0 ; i <= NAV_LIST_COUNT  ; ++i){
	//
	//			switch (i) {
	//			case 0:
	//				cursor = getContentResolver().query(TasksTable.CONTENT_URI, new String[]{TasksTable.COL_TASK_ID},
	//						TasksTable.COL_TASK_DATE +  " > ? AND "+TasksTable.COL_TASK_DATE +  " < ? ", new String[]{String.valueOf(System.currentTimeMillis()) , String.valueOf(System.currentTimeMillis() + 2*24*3600*1000)}, null);
	//				if(cursor != null)
	//					alCount.add(cursor.getCount());
	//
	//				break;
	//			case 1:
	//				cursor1 = getContentResolver().query(TasksTable.CONTENT_URI, new String[]{TasksTable.COL_TASK_ID},
	//						TasksTable.COL_TASK_DATE +  " > ? AND "+TasksTable.COL_TASK_DATE +  " < ? ", new String[]{String.valueOf(System.currentTimeMillis()) , String.valueOf(System.currentTimeMillis() + 7*24*3600*1000)}, null);
	//				if(cursor1 != null)
	//					alCount.add(cursor1.getCount());
	//
	//				break;
	//			case 2:
	//				cursor = getContentResolver().query(TasksTable.CONTENT_URI,new String[]{TasksTable.COL_TASK_ID}, null,null, null);
	//				if(cursor != null)
	//					alCount.add(cursor.getCount());
	//
	//				break;
	//
	//			default:
	//				cur = getContentResolver().query(TasksTable.CONTENT_URI, new String[]{TasksTable.COL_TASK_ID},
	//						TasksTable.COL_TASK_CATEGORY_UID +  " = ? ", new String[]{String.valueOf(i)}, null);
	//				if(cur != null)
	//					alCount.add(cur.getCount());
	//
	//				break;
	//			}
	//
	//		}
	//		if(cursor != null)
	//			cursor.close();
	//		if(cursor1 != null)
	//			cursor1.close();
	//		if(cur != null)
	//			cur.close();
	//	}

	//	public void hideFab(){
	//		this.mFab.setVisibility(View.GONE);
	//	}
	//	public void showFab(){
	//		this.mFab.setVisibility(View.VISIBLE);
	//	}

	public static void setDailyAlarm(Context context){

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		int hour =0;
		long timeBeforeNext =24*3600*1000;


		if(isPassedThresholdTime(PreferenceManager.
				getDefaultSharedPreferences(context).getInt(context.getString(R.string.key_daily_alarm_hour),9))){
			calendar.add(Calendar.DAY_OF_MONTH, 1);

		}
		hour = PreferenceManager.
				getDefaultSharedPreferences(context).getInt(context.getString(R.string.key_daily_alarm_hour),9);

		//		calendar.set(Calendar.HOUR_OF_DAY, 23);
		int min = PreferenceManager.
				getDefaultSharedPreferences(context).getInt(context.getString(R.string.key_daily_alarm_min),0);

		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, min);
		calendar.set(Calendar.SECOND, 0);

		Intent myIntent = new Intent(context , com.cyno.reminder_premium.service.PremiumAlarmService.class);   
		myIntent.putExtra(PremiumAlarmService.KEY_TIME, calendar.getTimeInMillis() );
		myIntent.setAction(PremiumAlarmService.ACTION_DAILY_MORNING_ALARM);
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent, 0);
		Log.d("alaarm","from act "+ String.valueOf(calendar.getTimeInMillis()));
//		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),timeBeforeNext  , pendingIntent);  //set repeating every 24 hours
		alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
	}

	/*
	 * if current time has already passed time to alarm,
	 * it doesnot trigger 1st alarm... so add 1 day.. and trigger alarm for next day
	 */

	private static boolean isPassedThresholdTime(int i) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		return cal.get(Calendar.HOUR_OF_DAY )> i;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		return new CursorLoader(this,CategoriesTable.CONTENT_URI, null,null,null,null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
		if(cursor != null){
			while(cursor.moveToNext())
				mCatList.add(cursor.getInt(cursor.getColumnIndex(CategoriesTable.COL_CATEGORY_ID)));
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursor) {
		adapter.swapCursor(null);
	}

	private class ColorChangeReciever extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			getSupportLoaderManager().restartLoader(LOADER_ID, null,MainActivity.this);
		}

	}

	public Toolbar getToolbar(){
		return toolbar;
	}


	public void refreshNavDrawer() {
		getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
	}


}
