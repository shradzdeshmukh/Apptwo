package com.cyno.reminder_premium.ui;
import android.app.Application;


public class IReminder extends Application {
	
	/*
	 * Premium features:-
	 * 1. swipe to delete done edit
	 * 2. multiselect
	 * 3. sync with google cal
	 * 4. change alarm tone
	 * 5. share intent
	 * --------------------------------------------
	 * 6. shopping list
	 * 7. sns for edit texts
	 * 8. text to speech
	 * 9. dialogs make seperate layout for api below 11... there is color diff between 2 views
	 * 10. repeat alarm
	 * 11. holidays implementation for countries with max downloads
	 */

	private static final int RADIUS = 300;
	private static final String PROPERTY_ID = "UA-59966368-1"; // stage
//	private static final String PROPERTY_ID = "UA-59966368-1"; // live
	/**
	 * Enum used to identify the tracker that needs to be used for tracking.
	 *
	 * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
	 * storing them all in Application object helps ensure that they are created only once per
	 * application instance.
	 */
	public enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
		ECOMMERCE_TRACKER,;
	}

//	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	@Override
	public void onCreate() {
		super.onCreate();
//		DisplayImageOptions options = new DisplayImageOptions.Builder()
//		.showImageOnLoading(R.drawable.ic_profile_pic) // resource or drawable
//		.showImageForEmptyUri(R.drawable.ic_profile_pic) // resource or drawable
//		.showImageOnFail(R.drawable.ic_profile_pic) // resource or drawable
//		.resetViewBeforeLoading(false)  // default
//		.cacheInMemory(true) // default
//		.cacheOnDisk(true) // default
//		.considerExifParams(false) // default
//		.displayer(new RoundedBitmapDisplayer(RADIUS)) // default
//		.build();
//
//		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
//		.defaultDisplayImageOptions(options)															
//		.build();
//		ImageLoader.getInstance().init(config);


	}

//	public synchronized Tracker getTracker(TrackerName trackerId) {
//		if (!mTrackers.containsKey(trackerId)) {
//			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//			analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
//			Tracker t = analytics.newTracker(PROPERTY_ID);
//			mTrackers.put(trackerId, t);
//		}
//		return mTrackers.get(trackerId);
//	}
}
