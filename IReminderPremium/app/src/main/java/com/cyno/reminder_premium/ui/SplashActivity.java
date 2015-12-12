package com.cyno.reminder_premium.ui;

import com.cyno.reminder.constants.StringConstants;
import com.cyno.reminder_premium.R;



import com.cyno.reminder_premium.ui.IReminder.TrackerName;



import com.cynozer.reminder_premium.contentproviders.CategoriesTable;
import com.cynozer.reminder_premium.contentproviders.TasksTable;

//import com.cynozer.reminder.utils.LoginActivity;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class SplashActivity extends Activity implements OnClickListener, AnimationListener {

	private Animation fadeInAnimation ;
	private Animation fadeOutAnimation;
	private View rootView;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences pref = getSharedPreferences(StringConstants.PREFERENCE_PROFILE, MODE_PRIVATE);
		setContentView(R.layout.activity_splash);
		rootView = findViewById(R.id.root_splash);
		TextView tv = (TextView) findViewById(R.id.splashscreen_text);
		fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
		fadeInAnimation.setAnimationListener(this);
		fadeOutAnimation.setAnimationListener(this);
		tv.startAnimation(fadeInAnimation);
		//		ImageView iv = (ImageView) findViewById(R.id.splash_imageview);
		//		iv.setBackgroundResource(R.anim.splash);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_signed_out, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/*	@Override
    protected Dialog onCreateDialog(int id) {
        if (id != DIALOG_GET_GOOGLE_PLAY_SERVICES) {
            return super.onCreateDialog(id);
        }

        int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (available == ConnectionResult.SUCCESS) {
            return null;
        }
        if (GooglePlayServicesUtil.isUserRecoverableError(available)) {
            return GooglePlayServicesUtil.getErrorDialog(
                    available, this, REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES);
        }
        return new AlertDialog.Builder(this)
                .setMessage(R.string.plus_generic_error)
                .setCancelable(true)
                .create();
    }*///TODO


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		//		case R.id.bt_splash_loginbutton:
		//
		//			Intent mIntent = new Intent(this , LoginActivity.class);
		//			mIntent.setAction(LoginActivity.ACTION_SIGNIN);
		//			startActivity(mIntent);
		//			finish();
		//			break;

		//		case R.id.bt_splash_skip:
		//			SharedPreferences.Editor edit = getSharedPreferences(StringConstants.PREFERENCE_PROFILE , MODE_PRIVATE).edit();
		//			edit.putBoolean(StringConstants.SIGN_IN_SKIPPED, true);
		//			edit.commit();
		//			startActivity(new Intent(this , MainActivity.class));
		//			finish();
		//			break;

		default:
			break;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}


	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onAnimationEnd(Animation animation) {
		if(animation == fadeInAnimation){
			rootView.startAnimation(fadeOutAnimation);
		}else{
			Cursor mCursor = getContentResolver().query(CategoriesTable.CONTENT_URI,
					new String[]{CategoriesTable.COL_CATEGORY_ID},null, null, null);
			if(mCursor != null){
				if(mCursor.getCount() == 0){
					String[] navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
					String []mColors = getResources().getStringArray(R.array.core_colors);
					for(int index = 0 ; index < navMenuTitles.length; ++index){
						ContentValues values = new ContentValues();
						values.put(CategoriesTable.COL_CATEGORY_NAME, navMenuTitles[index]);
						values.put(CategoriesTable.COL_CATEGORY_COLOR, mColors[index]);
						switch (index) {
						case 0:
							values.put(CategoriesTable.COL_CATEGORY_TYPE, TasksTable.TASK_TYPE_COMMING_UP);
							break;
						case 1:
							values.put(CategoriesTable.COL_CATEGORY_TYPE, TasksTable.TASK_TYPE_NEXT_SEVEN_DAYS);
							break;
						case 2:
							values.put(CategoriesTable.COL_CATEGORY_TYPE, TasksTable.TASK_TYPE_ALL_TASKS);
							break;
						case 3:
							values.put(CategoriesTable.COL_CATEGORY_TYPE, TasksTable.TASK_TYPE_SHOPPING);
							break;
						default:
							values.put(CategoriesTable.COL_CATEGORY_TYPE, TasksTable.TASK_TYPE_NORMAL);
							break;
						}
						getContentResolver().insert(CategoriesTable.CONTENT_URI, values);
					}
				}
				mCursor.close();
			}
			startActivity(new Intent(this , MainActivity.class));
			finish();
		}
	}


	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

}
