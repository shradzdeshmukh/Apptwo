package com.cyno.reminder.swipelist;

import android.content.Context;
import android.database.Cursor;

import java.util.List;

import com.cyno.reminder_premium.adapters.CursorRecyclerViewAdapter;
import com.cyno.reminder_premium.adapters.TaskListAdapter;


public abstract class RecyclerSwipeAdapter extends CursorRecyclerViewAdapter<TaskListAdapter.ViewHolder> implements SwipeItemMangerInterface, SwipeAdapterInterface {

    public RecyclerSwipeAdapter(Context context, Cursor cursor) {
		super(context, cursor);
	}

	public SwipeItemMangerImpl mItemManger = new SwipeItemMangerImpl(this);


    @Override
    public void notifyDatasetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void openItem(int position) {
        mItemManger.openItem(position);
    }

    @Override
    public void closeItem(int position) {
        mItemManger.closeItem(position);
    }

    @Override
    public void closeAllExcept(SwipeLayout layout) {
        mItemManger.closeAllExcept(layout);
    }

    @Override
    public void closeAllItems() {
        mItemManger.closeAllItems();
    }

    @Override
    public List<Integer> getOpenItems() {
        return mItemManger.getOpenItems();
    }

    @Override
    public List<SwipeLayout> getOpenLayouts() {
        return mItemManger.getOpenLayouts();
    }

    @Override
    public void removeShownLayouts(SwipeLayout layout) {
        mItemManger.removeShownLayouts(layout);
    }

    @Override
    public boolean isOpen(int position) {
        return mItemManger.isOpen(position);
    }

    @Override
    public Attributes.Mode getMode() {
        return mItemManger.getMode();
    }

    @Override
    public void setMode(Attributes.Mode mode) {
        mItemManger.setMode(mode);
    }
}
