package com.cyno.reminder_premium.ui;

import com.cyno.reminder_premium.R;
import com.cynozer.reminder.utils.Task;
import com.cynozer.reminder_premium.contentproviders.CategoriesTable;
import com.cynozer.reminder_premium.contentproviders.TasksTable;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentPlanDay extends Fragment implements OnClickListener, AnimationListener{

	private static final int ANIM_TYPE_NEXT_VIEW_FADE_OUT = 10;
	private static final int ANIM_TYPE_CHANGE_QUE_FADE_OUT = 20;
	private static final int ANIM_TYPE_CHANGE_QUE_FADE_IN = 30;
	private View rootView;
	private ImageView ivPositive;
	private ImageView ivNegative;
	private Animation fadeOutAnimation;
	private View topView;
	private View bottomView;
	private Animation fadeInAnimation;
	private TextView tvQuestion;
	private int iCurrentAnimType;
	private int iCurrentQuestion;
	private MyObserver mObserver;
	private final long lOneDay = 1000*3600*24;

	private final long duration = 1000*3600*24;
	private Cursor mCategoriesCursor;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_plan_day,null);

		ivPositive = (ImageView) rootView.findViewById(R.id.plan_day_positive_actn);
		ivNegative = (ImageView) rootView.findViewById(R.id.plan_day_negative_actn);
		topView = rootView.findViewById(R.id.view_top);
		bottomView = rootView.findViewById(R.id.view_bottom);
		tvQuestion = (TextView)rootView.findViewById(R.id.question);

		ivPositive.setOnClickListener(this);
		ivNegative.setOnClickListener(this);

		fadeOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out_small);
		fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_small);

		fadeOutAnimation.setAnimationListener(this);

		fadeInAnimation.setAnimationListener(this);

		return rootView;
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mCategoriesCursor = getActivity().getContentResolver().query(CategoriesTable.CONTENT_URI,
				new String[]{CategoriesTable.COL_CATEGORY_ID, CategoriesTable.COL_CATEGORY_NAME},
				CategoriesTable.COL_CATEGORY_TYPE + " = ? ",new String[]{String.valueOf(TasksTable.TASK_TYPE_NORMAL)},null);



		iCurrentAnimType = ANIM_TYPE_CHANGE_QUE_FADE_IN;
		tvQuestion.startAnimation(fadeInAnimation);
		//		iCurrentQuestion++;
		mCategoriesCursor.moveToPosition(iCurrentQuestion);
		changeQuestion();


		if(mCategoriesCursor != null)
			mCategoriesCursor.moveToPosition(iCurrentQuestion);

		//		if(isSingleTask(cur))
		//			tvQuestion.setText(getString(R.string.plan_day_birthday));
		//		else
		//			tvQuestion.setText(getString(R.string.plan_day_birthday_more));

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mObserver = new MyObserver(null);
		getActivity().getContentResolver().
		registerContentObserver(
				TasksTable.CONTENT_URI,
				true,
				mObserver);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.plan_day_negative_actn:
			iCurrentAnimType = ANIM_TYPE_CHANGE_QUE_FADE_OUT;
			tvQuestion.startAnimation(fadeOutAnimation);
			iCurrentQuestion++;

			if(iCurrentQuestion >= mCategoriesCursor.getCount()){
				getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
				getActivity().finish();
				return;
			}


			if(mCategoriesCursor != null)
				mCategoriesCursor.moveToPosition(iCurrentQuestion);

			break;
		case R.id.plan_day_positive_actn:
			//			iCurrentAnimType= ANIM_TYPE_NEXT_VIEW_FADE_OUT;
			//			rootView.startAnimation(fadeOutAnimation);

			if(mCategoriesCursor != null){
				mCategoriesCursor.moveToPosition(iCurrentQuestion);

				Task.getInstance().setCategoryLabel(mCategoriesCursor.getString
						(mCategoriesCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_NAME)));
				Task.getInstance().setiCategoryUID(mCategoriesCursor.getInt
						(mCategoriesCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_ID)));
			}


			FragmentAddTask frag = new FragmentAddTask(false , 0 , -1);
			frag.show(getFragmentManager(), FragmentAddTask.class.getSimpleName());
			break;

		default:
			break;
		}
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationEnd(Animation animation) {

		switch (iCurrentAnimType) {
		case ANIM_TYPE_NEXT_VIEW_FADE_OUT:
			topView.setVisibility(View.GONE);
			bottomView.setVisibility(View.VISIBLE);
			bottomView.startAnimation(fadeInAnimation);
			ivPositive.setVisibility(View.GONE);
			ivNegative.setVisibility(View.GONE);

			iCurrentAnimType = ANIM_TYPE_CHANGE_QUE_FADE_IN;
			break;
		case ANIM_TYPE_CHANGE_QUE_FADE_OUT:

			tvQuestion.startAnimation(fadeInAnimation);
			changeQuestion();
			ivPositive.setVisibility(View.GONE);
			ivNegative.setVisibility(View.GONE);

			break;
		case ANIM_TYPE_CHANGE_QUE_FADE_IN:
			ivPositive.setVisibility(View.VISIBLE);
			ivNegative.setVisibility(View.VISIBLE);
			break;

		default:
			getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
			break;
		}


	}

	@Override
	public void onStop() {
		super.onStop();
		getActivity().getContentResolver().
		unregisterContentObserver(mObserver);
	}

	private boolean isSingleTask(Cursor taskCursor) {
		Cursor mCursor = getActivity().getContentResolver().query(TasksTable.CONTENT_URI,
				new String[]{TasksTable.COL_TASK_NAME , 
				TasksTable.COL_TASK_DATE},
				TasksTable.COL_TASK_CATEGORY_UID + " = ? AND "+ 
						TasksTable.COL_TASK_DATE + " > ? AND " + 
						TasksTable.COL_TASK_DATE + " < ? ", 

						new String[]{String.valueOf(taskCursor.getInt(taskCursor.getColumnIndex(TasksTable.COL_TASK_CATEGORY_UID)))
				,String.valueOf(System.currentTimeMillis()),
				String.valueOf(System.currentTimeMillis()+lOneDay)},TasksTable.COL_TASK_DATE);
		if(mCursor != null){
			if(mCursor.moveToNext()){
				if(DateUtils.isToday(Long.valueOf(mCursor.getString(mCursor.getColumnIndex(TasksTable.COL_TASK_DATE))))){
					return false;
				}
			}
			mCursor.close();
		}
		return true;
	}

	private class MyObserver extends ContentObserver {
		public MyObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			Log.d("observer","onchange");
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					changeQuestion();
				}
			});
		}		

		@Override
		public void onChange(boolean selfChange, Uri uri) {
			Log.d("observer","onchange");
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					changeQuestion();
				}
			});
		}
	}

	private void changeQuestion(){
		Log.d("plan_day", "pos = "+mCategoriesCursor.getPosition() + " " +  mCategoriesCursor.getString(
				mCategoriesCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_NAME)));
		if(iCurrentQuestion >= mCategoriesCursor.getCount()){
			getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
			getActivity().finish();
			return;
		}
		iCurrentAnimType = ANIM_TYPE_CHANGE_QUE_FADE_IN;
		Cursor taskCursor = getActivity().getContentResolver().query(TasksTable.CONTENT_URI, new String[]
				{TasksTable.COL_TASK_ID ,TasksTable.COL_TASK_CATEGORY_UID },
				TasksTable.COL_TASK_CATEGORY_UID + " = ? ", new String[]
						{mCategoriesCursor.getString(mCategoriesCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_ID))},null);
		String preWords = getString(R.string.any);
		if(taskCursor != null){
			if(taskCursor.moveToNext()){
				if(!isSingleTask(taskCursor))
					preWords = getString(R.string.any_more);
			}
		}
		tvQuestion.setText(preWords +" "+ mCategoriesCursor.getString(
				mCategoriesCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_NAME))+" " + getString(R.string.today)) ;


	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mCategoriesCursor != null){
			mCategoriesCursor.close();
		}
	}

}
