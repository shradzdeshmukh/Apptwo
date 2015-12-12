package com.cyno.reminder.multiselect;

import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

public class MultiSelector {
    private SparseBooleanArray mSelections = new SparseBooleanArray();
    private WeakHolderTracker mTracker = new WeakHolderTracker();
    private ArrayList<String> mSelectedIds;

    private boolean mIsSelectable;
	private boolean isMultiSelect;

    public void setSelectable(boolean isSelectable) {
        mIsSelectable = isSelectable;
        refreshAllHolders();
    }

    public boolean isSelectable() {
        return mIsSelectable;
    }

    private void refreshAllHolders() {
        for (SelectableHolder holder : mTracker.getTrackedHolders()) {
            refreshHolder(holder);
        }
    }

    private void refreshHolder(SelectableHolder holder) {
        if (holder == null) {
            return;
        }
        holder.setSelectable(mIsSelectable);

        boolean isActivated = mSelections.get(holder.getPosition());
        holder.setActivated(isActivated);
    }

    public boolean isSelected(int position, long id) {
        return mSelections.get(position);
    }

    public void setSelected(int position, long id, boolean isSelected) {
        mSelections.put(position, isSelected);
        refreshHolder(mTracker.getHolder(position));
        
    }

    public void clearSelections() {
        mSelections.clear();
        refreshAllHolders();
    }

    public List<Integer> getSelectedPositions() {
        List<Integer> positions = new ArrayList<Integer>();

        for (int i = 0; i < mSelections.size(); i++) {
            if (mSelections.valueAt(i)) {
                positions.add(mSelections.keyAt(i));
            }
        }

        return positions;
    }

    public void bindHolder(SelectableHolder holder, int position, long id) {
        mTracker.bindHolder(holder, position);
        refreshHolder(holder);
    }

    public void setSelected(SelectableHolder holder, boolean isSelected , String id) {
        setSelected(holder.getPosition(), holder.getItemId(), isSelected);
        mSelectedIds.add(id);
    }

    public boolean tapSelection(SelectableHolder holder) {
        return tapSelection(holder.getPosition(), holder.getItemId());
    }

    private boolean tapSelection(int position, long itemId) {
        if (mIsSelectable) {
            boolean isSelected = isSelected(position, itemId);
            setSelected(position, itemId, !isSelected);
            return true;
        } else {
            return false;
        }

    }
    
    public void setMultiSelectMode(boolean flag){
    	this.isMultiSelect = flag;
    	if(flag)
    		mSelectedIds = new ArrayList<String>();
    	else{
    		if(mSelectedIds != null)
    			mSelectedIds.clear();
    	}
    		
    }
    
    public boolean isMultiselect(){
    	return isMultiSelect;
    }
    
    public ArrayList<String> getSlectedIds(){
    	return mSelectedIds;
    }
}
