package com.cyno.reminder_premium.ui;

import com.cyno.reminder.edittext.MaterialEditText;
import com.cyno.reminder_premium.R;
import com.cynozer.drawer.colorpicker.ColorPickerClickListener;
import com.cynozer.drawer.colorpicker.ColorPickerDialogBuilder;
import com.cynozer.drawer.colorpicker.ColorPickerView;
import com.cynozer.drawer.colorpicker.OnColorSelectedListener;
import com.cynozer.reminder_premium.contentproviders.CategoriesTable;
import com.cynozer.reminder_premium.contentproviders.TasksTable;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

public class FragmentChangeCatDialog extends DialogFragment implements OnClickListener, android.view.View.OnClickListener{

	private int catId = -100;
	private View rootView;
	private String color ;
	private View mCircleView;
	private MaterialEditText mTitle;

	public FragmentChangeCatDialog() {
	}

	public FragmentChangeCatDialog(String id){
		this.catId = Integer.valueOf(id);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		color = getString(R.color.primary);
		AlertDialog.Builder builder = new Builder(getActivity());
		rootView = View.inflate(getActivity(), R.layout.change_color_dialog, null);
		builder.setView(rootView);
		builder.setTitle(getActivity().getString(R.string.customize_dialot_title));
		builder.setPositiveButton(getActivity().getString(R.string.done), this);
		builder.setNegativeButton(getActivity().getString(R.string.delete), this);
		builder.setNeutralButton(getActivity().getString(R.string.cancel), null);
		return builder.show();
	}

	@Override
	public void onStart() {
		super.onStart();
		Cursor mCursor = getActivity().getContentResolver().query(CategoriesTable.CONTENT_URI, null, CategoriesTable.COL_CATEGORY_ID + " = ? ",
				new String[]{String.valueOf(catId)},null);
		mTitle = (MaterialEditText) rootView.findViewById(R.id.et_task_title);
		mCircleView = rootView.findViewById(R.id.color);
		mCircleView.setOnClickListener(this);

		if(mCursor != null){
			if(mCursor.moveToNext()){
				this.color = mCursor.getString(mCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_COLOR));
				changeColor(mCircleView,color);
				mTitle.setText(mCursor.getString(mCursor.getColumnIndex(CategoriesTable.COL_CATEGORY_NAME)));
			}
			mCursor.close();
		}
	}


	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
	}




	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case AlertDialog.BUTTON_POSITIVE:
			ContentValues values = new ContentValues();
			values.put(CategoriesTable.COL_CATEGORY_COLOR, color);
			values.put(CategoriesTable.COL_CATEGORY_NAME, this.mTitle.getText().toString());
			values.put(CategoriesTable.COL_CATEGORY_TYPE, TasksTable.TASK_TYPE_NORMAL);

			if(catId != -100){
				getActivity().getContentResolver().update(CategoriesTable.CONTENT_URI, 
						values, CategoriesTable.COL_CATEGORY_ID + " = ? ",new String[]{String.valueOf(catId)});

				values = new ContentValues();
				values.put(TasksTable.COL_TASK_LABEL, this.mTitle.getText().toString());
				values.put(TasksTable.COL_CAT_COLOR, color);
				getActivity().getContentResolver().update(TasksTable.CONTENT_URI, values, 
						TasksTable.COL_TASK_CATEGORY_UID + " = ? ", new String[]{String.valueOf(catId)});
			}else{
				getActivity().getContentResolver().insert(CategoriesTable.CONTENT_URI, values);
			}
			getActivity().sendBroadcast(new Intent(FragmentChangeCatColor.ACTION_REFRESH_LIST));
			break;

		case AlertDialog.BUTTON_NEGATIVE:
			getActivity().getContentResolver().delete(CategoriesTable.CONTENT_URI, 
					CategoriesTable.COL_CATEGORY_ID + " = ? ",new String[]{String.valueOf(catId)});	
			getActivity().getContentResolver().delete(TasksTable.CONTENT_URI, 
					TasksTable.COL_TASK_CATEGORY_UID + " = ? ",new String[]{String.valueOf(catId)});	
			getActivity().sendBroadcast(new Intent(FragmentChangeCatColor.ACTION_REFRESH_LIST));
			
			break;
		default:
			break;
		}
	}


	@Override
	public void onClick(View v) {
		selectColorModern(v);

	}

	@Override
	public void onResume() {
		super.onResume();
		((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.accent));
		((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.accent));
		((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.accent));

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

	public void selectColorModern(View v){
		ColorPickerDialogBuilder
		.with(getActivity())
		.setTitle(getString(R.string.color_category))
		.initialColor(Color.parseColor(color) )
		.wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
		.density(12)
		.setOnColorSelectedListener(new OnColorSelectedListener() {
			@Override
			public void onColorSelected(int selectedColor) {
			}
		})
		.setPositiveButton(getString(android.R.string.ok), new ColorPickerClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
				color = "#"+Integer.toHexString(selectedColor);
				changeColor(mCircleView, color);

			}
		})
		.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.build()
		.show();
	}

}
