package com.cyno.reminder_premium.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.util.TimeUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.cyno.reminder_premium.R;
import com.cyno.reminder.interfaces.ClickEventListners;
import com.cyno.reminder.multiselect.MultiSelector;
import com.cyno.reminder.multiselect.SwappingHolder;
import com.cyno.reminder.swipelist.RecyclerSwipeAdapter;
import com.cyno.reminder.swipelist.SwipeLayout;
import com.cynozer.reminder.utils.Task;
import com.cynozer.reminder_premium.contentproviders.TasksTable;


public class TaskListAdapter extends RecyclerSwipeAdapter{

	private final String dateformat = "dd MMM yyyy , HH:mm";
	private static Context context;
	private ViewHolder mViewHolder;
	private ClickEventListners clickListners;
	private static MultiSelector multiSelector;

	public TaskListAdapter(Context context,Cursor cursor , ClickEventListners listners, MultiSelector multi){
		super(context,cursor);
		clickListners = listners;
		TaskListAdapter.context = context;
		TaskListAdapter.multiSelector = multi;
	}

	public  class ViewHolder extends SwappingHolder implements OnClickListener, OnLongClickListener {
		public TextView tvTitle;
		public TextView tvDate;
		public TextView tvDaysLeft;
		public TextView tvDaysLeftText;
		public View ivImage;
		public SwipeLayout swipeLayout;
		private View rootView;
		private ImageView ivDelete;
		private ImageView ivEdit;
		private ImageView ivDone;
		private ImageView ivSync;

		private boolean isHour;

		public ViewHolder(View view ,ClickEventListners clickListners) {
			super(view , multiSelector);
			tvTitle = (TextView) view.findViewById(R.id.tv_item_task_list_title);
			tvDate = (TextView) view.findViewById(R.id.tv_item_task_list_date);
			tvDaysLeft = (TextView) view.findViewById(R.id.tv_item_task_list_days_left_number);
			tvDaysLeftText= (TextView) view.findViewById(R.id.tv_item_task_list_daysleft);
			ivImage= view.findViewById(R.id.task_item_image);
			ivDelete = (ImageView) view.findViewById(R.id.iv_delete);
			ivEdit= (ImageView) view.findViewById(R.id.iv_edt);
			ivDone = (ImageView) view.findViewById(R.id.iv_done);
			ivSync= (ImageView) view.findViewById(R.id.iv_sync);

			swipeLayout = (SwipeLayout) view.findViewById(R.id.swipe);
			rootView = view.findViewById(R.id.rootview);
			ivDelete.setOnClickListener(this);
			ivDone.setOnClickListener(this);
			ivEdit.setOnClickListener(this);
			if(ivSync != null)
				ivSync.setOnClickListener(this);
			if(Build.VERSION.SDK_INT >= 11)
				rootView.setOnLongClickListener(this);
			rootView.setOnClickListener(this);
		}



		@Override
		public boolean onLongClick(View v) {
			clickListners.onLongClick(this, v);
			return true;
		}

		@Override
		public void onClick(View v) {
			clickListners.onClick(this, v);
		}
	}

	public ViewHolder getViewHolder(){
		return mViewHolder;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_list, parent, false);
		View itemView = View.inflate(TaskListAdapter.context, R.layout.item_task_list, null);
		mViewHolder = new ViewHolder(itemView ,clickListners);
		return mViewHolder;
	}

	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor) {
		viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

		//        MyListItem myListItem = MyListItem.fromCursor(cursor);
		final long date = Long.valueOf(cursor.getString(cursor.getColumnIndex(TasksTable.COL_TASK_DATE)));
		final long diff = date - System.currentTimeMillis();


		viewHolder.tvTitle.setText(cursor.getString(cursor.getColumnIndex(TasksTable.COL_TASK_NAME)));
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(Long.valueOf(cursor.getString(cursor.getColumnIndex(TasksTable.COL_TASK_DATE))));
		SimpleDateFormat format  = new SimpleDateFormat(dateformat, Locale.getDefault());
		viewHolder.tvDate.setText(format.format(cal.getTime()));
		int status = Integer.valueOf(cursor.getString(cursor.getColumnIndex(TasksTable.COL_TASK_STATUS)));
		if(status == Task.STATUS_DONE){
			viewHolder.tvDaysLeft.setTextColor(context.getResources().getColor(R.color.green));
			viewHolder.tvDaysLeftText.setTextColor(context.getResources().getColor(R.color.green));
			viewHolder.tvDaysLeft.setVisibility(View.GONE);
			viewHolder.tvDaysLeftText.setText(R.string.done);
			//			viewHolder.ivImage.setBackgroundColor((R.drawable.label_light_green));
			changeColor(viewHolder.ivImage, context.getString(R.color.green));
			viewHolder.ivDone.setImageResource(R.drawable.ic_content_clear);
		}else if(diff < 0){
			viewHolder.tvDaysLeft.setTextColor(context.getResources().getColor(R.color.warn));
			viewHolder.tvDaysLeftText.setTextColor(context.getResources().getColor(R.color.warn));
			viewHolder.tvDaysLeft.setVisibility(View.GONE);
			viewHolder.tvDaysLeftText.setText(R.string.overdue);
			changeColor(viewHolder.ivImage, context.getString(R.color.warn));
			viewHolder.ivDone.setImageResource(R.drawable.ic_action_done);

		}else{
			viewHolder.tvDaysLeft.setTextColor(context.getResources().getColor(R.color.primary_dark));
			viewHolder.tvDaysLeftText.setTextColor(context.getResources().getColor(R.color.primary_dark));
			viewHolder.tvDaysLeft.setVisibility(View.VISIBLE);
			if(getDaysLeft(diff, viewHolder.tvDaysLeftText , viewHolder)){
				CountDownTimer Count = new CountDownTimer(diff, 1000) {
					@SuppressLint("NewApi")
					public void onTick(long milliseconds) {
						//						if(viewHolder.tvDaysLeftText.getText().equals(context.getString(R.string.Days_left)))
						//							return;
						
						Log.d("tick", viewHolder.tvTitle.getText().toString() + " date = "+ viewHolder.tvDaysLeft.getText().toString());
						if(!viewHolder.isHour)
							return;
						if(cursor.isClosed() || cursor.getInt(cursor.getColumnIndex(TasksTable.COL_TASK_STATUS)) == Task.STATUS_DONE || 
								diff < 0 )
							return;
						long time = System.currentTimeMillis() - milliseconds;
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(time);
						DecimalFormat df = new DecimalFormat("00");
						viewHolder.tvDaysLeft.setText(""+String.format("%s:%s:%s", 
								df.format(TimeUnit.MILLISECONDS.toHours( milliseconds)),
								df.format(TimeUnit.MILLISECONDS.toMinutes( milliseconds)%60),
								df.format(TimeUnit.MILLISECONDS.toSeconds(milliseconds) - 
										TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)))));

					}
					public void onFinish() {
						notifyDatasetChanged();
					}
				};
				Count.start();
			}else{
				viewHolder.tvDaysLeft.setText(getDaysLeft(diff)); 
			}
			String color = cursor.getString(cursor.getColumnIndex(TasksTable.COL_CAT_COLOR));
			if(color != null)
				changeColor(viewHolder.ivImage, color);
			viewHolder.ivDone.setImageResource(R.drawable.ic_action_done);
		}
		viewHolder.ivDelete.setTag(cursor.getString(cursor.getColumnIndex(TasksTable.COL_TASK_ID )));
		viewHolder.ivEdit.setTag(cursor.getString(cursor.getColumnIndex(TasksTable.COL_TASK_ID )));
		viewHolder.ivDone.setTag(cursor.getString(cursor.getColumnIndex(TasksTable.COL_TASK_ID )));
		viewHolder.rootView.setTag(cursor.getString(cursor.getColumnIndex(TasksTable.COL_TASK_ID )));
		viewHolder.rootView.setTag(R.string.cat_id , cursor.getString(cursor.getColumnIndex(TasksTable.COL_TASK_CATEGORY_UID )));
		viewHolder.rootView.setTag(R.string.cat_type , cursor.getString(cursor.getColumnIndex(TasksTable.COL_TASK_CATEGORY_TYPE)));

		if(viewHolder.ivSync != null)
			viewHolder.ivSync.setTag(cursor.getString(cursor.getColumnIndex(TasksTable.COL_TASK_ID )));
		mItemManger.bind(viewHolder.itemView, cursor.getPosition());



	}

	private String getDaysLeft(long date) {
		return String.valueOf(date/(24*3600*1000));
	}

	private boolean getDaysLeft(long diff , TextView tv , ViewHolder holder) {
		long diffr = diff/(1000*3600*24);
		if(diffr != 0){
			holder.isHour = false;
			tv.setText(R.string.Days_left);
			return false;
		}else{
			holder.isHour = true;
			tv.setText(R.string.hours_left);
			return true;
		}
	}

	@Override
	public int getSwipeLayoutResourceId(int position) {
		return R.id.swipe;
	}

	private void changeColor(View mView , String color){
		int radius = 150;
		ShapeDrawable biggerCircle= new ShapeDrawable( new OvalShape());
		biggerCircle.setIntrinsicHeight( radius );
		biggerCircle.setIntrinsicWidth( radius);
		biggerCircle.setBounds(new Rect(0, 0, radius, radius));
		biggerCircle.getPaint().setColor(Color.BLUE);
		int padding = 100;

		ShapeDrawable smallerCircle= new ShapeDrawable( new OvalShape());
		smallerCircle.setIntrinsicHeight( 50 );
		smallerCircle.setIntrinsicWidth( 50);
		smallerCircle.setBounds(new Rect(0, 0, 10, 10));
		smallerCircle.getPaint().setColor(Color.parseColor(color));
		smallerCircle.setPadding(padding,padding,padding,padding);
		Drawable[] d = {smallerCircle,biggerCircle};

		LayerDrawable composite1 = new LayerDrawable(d);

		mView.setBackgroundDrawable(composite1);  
	}
}