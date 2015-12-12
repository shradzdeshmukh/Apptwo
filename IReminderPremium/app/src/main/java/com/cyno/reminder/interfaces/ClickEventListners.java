package com.cyno.reminder.interfaces;

import android.view.View;

import com.cyno.reminder.multiselect.SwappingHolder;

public interface ClickEventListners {

	public void onLongClick(SwappingHolder holder , View view);
	public void onClick(SwappingHolder holder , View view);
}
