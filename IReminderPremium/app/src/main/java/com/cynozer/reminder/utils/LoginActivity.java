//package com.cynozer.reminder.utils;
//
//import com.cyno.reminder.R;
//import com.cynozer.reminder.constants.StringConstants;
//import com.cynozer.reminder.ui.IReminder;
//import com.cynozer.reminder.ui.MainActivity;
//import com.cynozer.reminder.ui.SplashActivity;
//import com.cynozer.reminder.ui.IReminder.TrackerName;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.plus.Plus;
//import com.google.android.gms.plus.model.people.Person;
//import com.nostra13.universalimageloader.core.ImageLoader;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.content.IntentSender;
//import android.content.SharedPreferences;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.transition.Explode;
//import android.transition.Fade;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.view.View.OnClickListener;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.view.animation.Animation.AnimationListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//@SuppressLint("NewApi")
//public class LoginActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
//
//	private static final String PROFILE_PIC_SIZE = "200";
//	private static final int REQUEST_CODE_SIGN_IN = 1;
//	private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;
//	public static final String ACTION_SIGNIN = "signin";
//	public static final String ACTION_SIGNOUT = "logout";
//	public static final String LOGOUT_BROADCAST = "log_out_broadcast";
//
//	private GoogleApiClient mGoogleApiClient;
//	private ConnectionResult mConnectionResult;
//	private boolean isLogout;
//	private ProgressDialog mProgressDialog;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		if(Build.VERSION.SDK_INT >= 21){
//			Window window = getWindow();
//			// inside your activity (if you did not enable transitions in your theme)
//			window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//			window.setExitTransition(new Fade());
//			window.setEnterTransition(new Explode());
//		}
//
//		super.onCreate(savedInstanceState);
//		if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)!=ConnectionResult.SUCCESS){
//			startActivity(new Intent (this , MainActivity.class));
//			Toast.makeText(this, getString(R.string.no_play_services), Toast.LENGTH_LONG).show();
//			finishAfterTransition();
//		}
//
//		mProgressDialog = new ProgressDialog(this);
//		mProgressDialog.setCancelable(false);
//		mGoogleApiClient = new GoogleApiClient.Builder(this)
//		.addApi(Plus.API, Plus.PlusOptions.builder()
//				.addActivityTypes(MomentUtil.ACTIONS).build())
//				.addScope(Plus.SCOPE_PLUS_LOGIN)
//				.addConnectionCallbacks(this)
//				.addOnConnectionFailedListener(this)
//				.build();
//
//		if(getIntent().getAction().equals(ACTION_SIGNIN))
//			signIn();
//		else
//			signOut();
//
//		// Get tracker.
//		Tracker t = ((IReminder)getApplication()).getTracker(TrackerName.APP_TRACKER);
//		t.setScreenName(LoginActivity.class.getSimpleName());
//		t.send(new HitBuilders.AppViewBuilder().build());
//	}
//
//
//	@Override
//	public void onStart() {
//		super.onStart();
//		if(mGoogleApiClient != null)
//			mGoogleApiClient.connect();
//	}
//
//	@Override
//	public void onStop() {
//		Toast.makeText(this, "on stop", Toast.LENGTH_LONG).show();
//		if(mGoogleApiClient != null)
//			mGoogleApiClient.disconnect();
//		super.onStop();
//		mProgressDialog.dismiss();
//	}
//
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		Toast.makeText(this, "onactivity result", Toast.LENGTH_LONG).show();
//		if (requestCode == REQUEST_CODE_SIGN_IN
//				|| requestCode == REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES) {
//			if (resultCode == RESULT_CANCELED) {
//				mProgressDialog.cancel();
//				finishAfterTransition();
//			} else if (resultCode == RESULT_OK && !mGoogleApiClient.isConnected()
//					&& !mGoogleApiClient.isConnecting()) {
//				// This time, connect should succeed.
//				mGoogleApiClient.connect();
//			}
//		}
//	}
//
//	@Override
//	public void onConnectionFailed(ConnectionResult result) {
//		Toast.makeText(this, "connection failed", Toast.LENGTH_LONG).show();
//		mConnectionResult = result;		
//		try {
//			mConnectionResult.startResolutionForResult(this, REQUEST_CODE_SIGN_IN);
//		} catch (IntentSender.SendIntentException e) {
//			// Fetch a new result to start.
//			mGoogleApiClient.connect();
//		} 
//	}
//
//	@Override
//	public void onConnected(Bundle arg0) {
//		if(isLogout){
//			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//			mGoogleApiClient.disconnect();
//			SharedPreferences.Editor edit = getSharedPreferences(StringConstants.PREFERENCE_PROFILE, MODE_PRIVATE).edit();
//			edit.putBoolean(StringConstants.KEY_USER_SIGNED_GPLUS, false);
//			edit.clear();
//			edit.commit();
//
//			new Handler().postDelayed(new Runnable() {
//				public void run() {
//					runOnUiThread(new Runnable() {
//
//						@Override
//						public void run() {
//							finishAfterTransition();
//							sendLogoutBroadcast();
//							ImageLoader.getInstance().clearDiskCache();
//							ImageLoader.getInstance().clearMemoryCache();
//						}
//					});
//				}
//			}, 1000);
//			return;
//		}
//
//		Toast.makeText(this, "connected", Toast.LENGTH_LONG).show();
//		Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
//		if(person == null)
//			return;
//
//		SharedPreferences.Editor edit = getSharedPreferences(StringConstants.PREFERENCE_PROFILE, MODE_PRIVATE).edit();	
//		String personPhotoUrl = person.getImage().getUrl();
//		personPhotoUrl = personPhotoUrl.substring(0,personPhotoUrl.length() - 2);
//		personPhotoUrl = personPhotoUrl + PROFILE_PIC_SIZE;
//		edit.putString(StringConstants.KEY_USER_NAME, person.getDisplayName());
//		edit.putString(StringConstants.KEY_USER_BIRTHDAY, person.getBirthday());
//		edit.putString(StringConstants.KEY_USER_LOCATION, person.getCurrentLocation());
//		edit.putString(StringConstants.KEY_USER_LANGUAGE, person.getLanguage());
//		edit.putInt(StringConstants.KEY_USER_GENNDER, person.getGender());
//		edit.putString(StringConstants.KEY_USER_IMAGE, personPhotoUrl);
//		edit.putBoolean(StringConstants.KEY_USER_SIGNED_GPLUS, true);
//
//		edit.commit();
//
//		startActivity(new Intent (this , MainActivity.class));
//		finishAfterTransition();
//	}
//
//
//	@Override
//	public void onConnectionSuspended(int cause) {
//		mGoogleApiClient.connect();
//		Toast.makeText(this, "connection suspended", Toast.LENGTH_LONG).show();
//	}
//
//
//	private void signIn(){
//		mProgressDialog.setMessage(getString(R.string.signing_in));
//		mProgressDialog.show();
//		int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//		if (available != ConnectionResult.SUCCESS) {
//			//TODO
//			return;
//		}
//	}
//
//	private void signOut(){
//		mProgressDialog.setMessage(getString(R.string.signing_out));
//		mProgressDialog.show();
//		isLogout = true;
//		if (mGoogleApiClient.isConnected()) {
//			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//			mGoogleApiClient.disconnect();
//			SharedPreferences.Editor edit = getSharedPreferences(StringConstants.PREFERENCE_PROFILE, MODE_PRIVATE).edit();
//			edit.putBoolean(StringConstants.KEY_USER_SIGNED_GPLUS, false);
//			edit.clear();
//			edit.commit();
//			finishAfterTransition();
//			sendLogoutBroadcast();
//		}else{
//			mGoogleApiClient.connect();
//		}
//	}
//
//	private void sendLogoutBroadcast(){
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				sendBroadcast(new Intent(LOGOUT_BROADCAST));
//			}
//		}).start();
//	}
//
//}
