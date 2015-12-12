package com.cyno.reminder.constants;

import java.util.Arrays;
import java.util.List;



public class StringConstants {
	
	//tables constants
	public final static String BIRTHDAYS =  "Birthdays" ;
	public final static String EXAMS = "Exams" ;
	public final static String HOLIDAYS = "Holidays" ;
	public final static String APPOINTMENTS = "Appointments" ;
	public final static String REMIND = "Quick Remind" ;
	public static final String SHOPPING = "Shopping";
	
	public final static String [] TASKS_ARR = new String [] {REMIND,
																BIRTHDAYS,
																EXAMS,
																HOLIDAYS,
																SHOPPING,
																APPOINTMENTS,
																}; 
	
	public final static List<String> TASKS_LIST =  Arrays.asList(TASKS_ARR);


	public final static String KEY = "KEY";
	public final static String ALARM_SERVICE_KEY = "ALARM_KEY";
	
	
//shared pref constants
	public static final String USER_INFO = "user_info";
	public static final String USER_LATITUDE = "user_latitude";
	public static final String USER_LONGITUDE = "user_longitude";
	public static final String WEATHER_DESCRIPTION = "weather_description";
	public static final String WEATHER_TEMPERATURE = "weather_temp";
	public static final String USER_NAME = "user_name";
	public static final String COUNTRY_CODE = "country_code";
	
	public static final String PREF_BOOLEAN = "Boolean";
	
	
	
//	public static final String[] HOLIDAYS_COUNTRIES_NAMES = new String[]{ "India" ,
//																			"USA" ,
//																			"Australia" 
//																			,"Austria" ,
//																			"Belgium" ,
//																			"Canada" ,
//																			"China" , 
//																			"Croatia" ,
//																			"Czech Republic",
//																			"Denmark",
//																			"England",
//																			"Estonia",
//																			"Finland",
//																			"France",
//																			"Germany",
//																			"Hong Kong",
//																			"Hungary",
//																			"Iceland",
//																			"Ireland",
//																			"Israel",
//																			"Italy",
//																			"Japan",
//																			"Latvia",
//																			"Lithuania",
//																			"Luxembourg",
//																			"Netherlands",
//																			"New Zealand",
//																			"Northern Ireland",
//																			"Norway",
//																			"Poland",
//																			"Portugal",
//																			"Russia",
//																			"Serbia",
//																			"Slovakia",
//																			"Slovenia",
//																			"South Africa",
//																			"South Korea",
//																			"Sweden",
//																			"Wales",
//																			};
	public static final String[] HOLIDAYS_COUNTRIES_CODE = new String[]{ "INDIA" ,
		"usa" ,
		"aus" 
		,"aut" ,
		"bel" ,
		"can" ,
		"chn" , 
		"hrv" ,
		"cze",
		"dnk",
		"eng",
		"est",
		"fin",
		"fra",
		"ger",
		"hkg",
		"hun",
		"isl",
		"irl",
		"isr",
		"ita",
		"jpn",
		"lva",
		"ltu",
		"lux",
		"nld",
		"nzl",
		"nir",
		"nir",
		"pol",
		"prt",
		"rus",
		"srb",
		"svk",
		"svn",
		"zaf",
		"rok",
		"swe",
		"wal",};

//	public static final List<String> HOLIDAYS_COUNTRIES_LIST =  Arrays.asList(HOLIDAYS_COUNTRIES_NAMES);
	public static final List<String> HOLIDAYS_COUNTRIES_CODE_LIST =  Arrays.asList(HOLIDAYS_COUNTRIES_CODE);
	public static final String CHOOSE_YOUR_COUNTRY = "Choose your country";
	public static final String IS_PREMIUM = "is_premium";
	public static final String PREFERENCE_PROFILE = "profile";
	public static final String KEY_USER_NAME = "name";
	public static final String KEY_USER_BIRTHDAY = "birthday";
	public static final String KEY_USER_LOCATION = "loc";
	public static final String KEY_USER_LANGUAGE = "lc";
	public static final String KEY_USER_GENNDER = "gen";
	public static final String KEY_USER_COVER = "cvr";
	public static final String KEY_USER_IMAGE = "img";
	public static final String KEY_USER_SIGNED_GPLUS = "signed";
	public static final String SIGN_IN_SKIPPED = "skip";
	public static final String NOTIFICATION_TYPE = "TYPE";
	public static final String NOTIF_ACTION = "NOTIF";
	
	
}
