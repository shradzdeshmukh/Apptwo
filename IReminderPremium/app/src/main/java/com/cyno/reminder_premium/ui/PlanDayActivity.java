package com.cyno.reminder_premium.ui;

import com.cyno.reminder_premium.R;

import android.support.v7.app.ActionBarActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class PlanDayActivity extends ActionBarActivity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(Build.VERSION.SDK_INT >= 21){
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(getResources().getColor(R.color.primary_dark));
			// inside your activity (if you did not enable transitions in your theme)
			window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
			window.setExitTransition(new Explode());
			window.setEnterTransition(new Fade());
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan_day);
		
//		getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new FragmentPlanDay()).commit();
		getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new PlanDayTodayTaskFrag()).commit();

	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		startActivity(new Intent(this , MainActivity.class));
		finish();
	}


}
