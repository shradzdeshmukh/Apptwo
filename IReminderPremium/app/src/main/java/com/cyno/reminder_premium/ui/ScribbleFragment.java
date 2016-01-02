package com.cyno.reminder_premium.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cyno.reminder_premium.R;
import com.cyno.reminder_premium.adapters.ToolsGridAdapter;
import com.cynozer.drawer.colorpicker.ColorPickerClickListener;
import com.cynozer.drawer.colorpicker.ColorPickerDialogBuilder;
import com.cynozer.drawer.colorpicker.ColorPickerView;
import com.cynozer.drawer.colorpicker.LineColorPicker;
import com.cynozer.drawer.colorpicker.OnColorChangedListener;
import com.cynozer.drawer.colorpicker.OnColorSelectedListener;
import com.cynozer.reminder.utils.Task;
import com.cynozer.reminder.utils.Tools;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import me.panavtec.drawableview.DrawableView;
import me.panavtec.drawableview.DrawableViewConfig;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ScribbleFragment extends Fragment implements OnSeekBarChangeListener, OnClickListener,
        OnItemClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    private DrawableView mDrawableView;
    private DrawableViewConfig config;
    private int currentColor = Color.parseColor("#3F51B5");
    private LinearLayout mGridLayout;
    private int mGridSpace ;
    private String fname;
    private MediaScannerConnection msConn;
    private SeekBar mSeekbar;
    private float mStrokeSize = 20f;
    private ImageView mMenuImage;
    private View mToolsView ;
    private String[] toolsTitles;
    private TypedArray toolsIcons;
    private GridView mtoolsGrid;
    private boolean hasGrid;
    private SeekBar mGridSeekbar;
    private View gridSeekLayout;
    private AlertDialog dialog;

    private final String appPackageName = "com.cyno.drawme";
    private Calendar cal;
    private int backGroundColor;

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        TimePickerDialog dialog = new TimePickerDialog(getActivity(), this, cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE), false);
        dialog.show();
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        if(cal.getTimeInMillis() < System.currentTimeMillis())
            Toast.makeText(getActivity(), getActivity().getString(R.string.time_back_selected),Toast.LENGTH_LONG).show();
        else{
            Task.getInstance().setTime(cal.getTimeInMillis());
            Task.getInstance().setStatus(Task.STATUS_PENDING);



//            RepeatTaskFragment frag  = new RepeatTaskFragment();
//            frag.show(getFragmentManager(), RepeatTaskFragment.class.getSimpleName());


//			((MainActivity)getActivity()).refreshNavDrawer();
//			((MainActivity)getActivity()).displayView(Task.getInstance().getiCategory());
//			Task.getInstance().clearObject();
            if(dialog != null)
                dialog.dismiss();
        }
    }

    public enum navItems  {BRUSH,ERASER,GRID,COLOR,SAVE,CLEAR,UNDO,BACKGROUND}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scribble, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toast.makeText(getActivity(),getString(R.string.start_drawing),Toast.LENGTH_LONG).show();

        mDrawableView = (DrawableView) view.findViewById(R.id.paintView_mainactivity);
        mGridLayout = (LinearLayout) view.findViewById(R.id.linear_grid_mainactivity);
        mGridSpace = (int) getResources().getDimension(R.dimen.grid_default_height);
        mToolsView = view.findViewById(R.id.tools_layout);
        mToolsView.setTag(false);
        mSeekbar = (SeekBar) view.findViewById(R.id.seekbar_size);
        mSeekbar.setProgress((int)mStrokeSize);
        mMenuImage = (ImageView) view.findViewById(R.id.bottom_drawer);
        mMenuImage.setOnClickListener(this);
        setupDrawerView();

        toolsTitles = getResources().getStringArray(R.array.tools_grid_titles);
        toolsIcons = getResources().obtainTypedArray(R.array.tools_grid_icons);
        mtoolsGrid = (GridView) view.findViewById(R.id.mainactivity_tools_grid);
        ToolsGridAdapter adapter = new ToolsGridAdapter(getActivity(),getToolsList());
        toolsIcons.recycle();
        mtoolsGrid.setOnItemClickListener(this);
        mtoolsGrid.setAdapter(adapter);

        mGridSeekbar = (SeekBar) view.findViewById(R.id.seekbar_grid);
        gridSeekLayout = view.findViewById(R.id.grid_seekbar_layout);

        mGridSeekbar.setOnSeekBarChangeListener(this);

        ImageView ivNavUp = (ImageView)view. findViewById(R.id.nav_up);
        ivNavUp.setOnClickListener(this);
        backGroundColor = getActivity().getResources().getColor(R.color.notes_yellow);

    }

    @SuppressLint("NewApi")
    private void setupDrawerView() {
        int width = 0;
        int height = 0;
        config = new DrawableViewConfig();
        config.setStrokeColor(currentColor);
        config.setMaxZoom(1.0f);
        config.setMinZoom(1.0f);
        config.setStrokeWidth(mStrokeSize);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        if(Build.VERSION.SDK_INT >= 13){
            display.getSize(size);
            width = size.x;
            height = size.y;
        }else{
            width = display.getWidth();
            height = display.getHeight();
        }
        config.setCanvasHeight(height);
        config.setCanvasWidth(width);
        mDrawableView.setConfig(config);
    }

    @Override
    public void onStart() {
        super.onStart();
//        ((MainActivity)getActivity()).hideFab();

        mSeekbar.setOnSeekBarChangeListener(this);
        if(hasGrid)
            showGrid(mGridSpace, false);
    }




    private ArrayList<Tools> getToolsList(){
        ArrayList<Tools> itemList = new ArrayList<>();
        for(int i = 0 ; i < toolsTitles.length ; ++i){
            itemList.add(new Tools(toolsTitles[i], toolsIcons.getResourceId(i, -1) ));
        }
        return itemList;
    }




    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        navItems item = navItems.values()[position];
        switch (item) {
            case BRUSH:
                config.setStrokeColor(currentColor);
                break;
            case ERASER:
                config.setStrokeColor(backGroundColor);
                break;
            case UNDO:
//                premiumFeature();
                mDrawableView.undo();
                break;
            case GRID:
//                premiumFeature();
                showGrid(mGridSpace,true );
                break;
            case SAVE:
                if(saveLocally()) {
                    animateUp();
                    showTitleDialog();
                }else
                    Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_LONG).show();

                break;

            case CLEAR:
                mDrawableView.clear();
                mGridLayout.setVisibility(View.GONE);
                hasGrid = false;
                break;
            case COLOR:
//                premiumFeature();
                if(Build.VERSION.SDK_INT >= 16)
                    selectColorModern(true,false);
                else
                    selectColorOld(true,false);
                break;

            case BACKGROUND:
                if(Build.VERSION.SDK_INT >= 16)
                    selectColorModern(false , true);
                else
                    selectColorOld(false,true);
                break;

            default:
                break;
        }
        if(!hasGrid)
            animateUp();
    }

    private void showTitleDialog(){
        Builder mBuilder = new Builder(getActivity());
        View dialogView = View.inflate(getActivity(), R.layout.fragment_gettitle, null);
        final EditText mTitle = (EditText) dialogView.findViewById(R.id.et_task_title);
        mBuilder.setView(dialogView);
        mBuilder.setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!TextUtils.isEmpty(mTitle.getText())) {
                    Task.getInstance().setTitle(mTitle.getText().toString());
                    openNextFrag();
                }else
                    mTitle.setError(getString(R.string.enter_title_error));
            }
        });
        mBuilder.setNegativeButton(getString(R.string.cancel) , null);
        mBuilder.setTitle(getString(R.string.enter_title) );
        mBuilder.show();
    }

    private void openNextFrag() {
        if(Build.VERSION.SDK_INT >= 14) {
            FragmentCalender mFragment = new FragmentCalender(false, -1);
            mFragment.show(getFragmentManager(), FragmentCalender.class.getSimpleName());
        }else{
            showNativeCal();
        }
    }

    private void showNativeCal() {
        cal = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(getActivity(), ScribbleFragment.this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        picker.show();
    }


    private void selectColorOld(final boolean setcolor , final boolean isBackground) {
        Builder builder = new Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.color_picker_old, null);
        builder.setView(view).setTitle(R.string.pick_color_title);
        dialog = builder.create();
        dialog.show();

        LineColorPicker colorpicker = (LineColorPicker) view.findViewById(R.id.picker);
        colorpicker.setOnColorChangedListener(new OnColorChangedListener() {

            @Override
            public void onColorChanged(int c) {
                if(!isBackground)
                    currentColor = c;
                if(setcolor)
                    config.setStrokeColor(currentColor);
                else if(isBackground){
                    backGroundColor = c;
                    mDrawableView.setBackgroundColor(c);
                }
                dialog.cancel();
//                premiumFeature();
            }
        });


    }

    @SuppressLint("NewApi")
    private void showGrid(int space , boolean flag){
        if(flag)
            hasGrid = !hasGrid;

        if(hasGrid){
            this.gridSeekLayout.setVisibility(View.VISIBLE);
        }else{
            this.gridSeekLayout.setVisibility(View.GONE);
        }

        if(hasGrid)
            mGridLayout.setVisibility(View.VISIBLE);
        else
            mGridLayout.setVisibility(View.GONE);

        Display mDisplay = getActivity().getWindowManager().getDefaultDisplay();
        Point mPoint = new Point();
        int height;
        if(Build.VERSION.SDK_INT >= 13){
            mDisplay.getSize(mPoint);
            height = mPoint.y;
        }else{
            height = mDisplay.getHeight();
        }
        int numOfLines = height / space;
        if(mGridLayout.getChildCount() != 0)
            mGridLayout.removeAllViewsInLayout();
        for (int i = 0; i <= numOfLines; ++i) {
            View mView = new View(getActivity());
            mView.setBackgroundColor(Color.GRAY);
            LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(MATCH_PARENT, 1);
            mParams.setMargins(0, space, 0, 0);
            mView.setLayoutParams(mParams);
            mView.invalidate();
            mGridLayout.addView(mView);
        }
    }

	/*public void saveLocally(View v){
		saveLocally(null);
		animateUp();
		Toast.makeText(this, R.string.saved, Toast.LENGTH_LONG).show();
	}*/

//	private Bitmap saveLocally(){
//		String root = Environment.getExternalStorageDirectory().toString();
//		File myDir = new File(root + "/Pictures/Drawer");
//		myDir.mkdirs();
//		fname = "/picture"+System.currentTimeMillis()+".png";
//		File file = new File(myDir, fname);
//		if (file.exists()) file.delete();
//		Bitmap bp = null;
//		try {
//			FileOutputStream out = new FileOutputStream(file);
//			bp  = mDrawableView.obtainBitmap();
//			bp.compress(Bitmap.CompressFormat.PNG, 100, out);
//			out.flush();
//			out.close();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			scanPhoto(file.toString());
//		}
//		return bp;
//	}

    private boolean saveLocally(){

        Bitmap mBitmap = mDrawableView.obtainBitmap(backGroundColor);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG , 100 , outStream);
        byte[] data = outStream.toByteArray();
        Task.getInstance().setScribbleData(data);
        return true;
    }


    /*public void scanPhoto(final String imageFileName) {
        msConn = new MediaScannerConnection(this,new MediaScannerConnectionClient()
        {
            public void onMediaScannerConnected()
            {
                msConn.scanFile(imageFileName, null);
                Log.i("msClient obj  in Photo Utility","connection established");
            }

            @Override
            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
                Log.i("msClient obj in Photo Utility","scan completed");
            }
        });
        msConn.connect();
    }
*/
    private void shareBitmap(Bitmap bp , String title){
        String pathofBmp = Images.Media.insertImage(getActivity().getContentResolver(), bp,title, null);
        if(pathofBmp == null){
            Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_LONG).show();
            return;
        }
        Uri bmpUri = Uri.parse(pathofBmp);
        final Intent emailIntent1 = new Intent(   Intent.ACTION_SEND);
        emailIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri);
        emailIntent1.putExtra(Intent.EXTRA_TEXT, getString(R.string.caption_string)+" http://bit.ly/1KRYeKf");

        emailIntent1.setType("image/png");

        startActivity(emailIntent1);
    }

    public void selectColorModern(final boolean setBrush , final boolean isBackground){
        ColorPickerDialogBuilder
                .with(getActivity())
                .setTitle(getString(R.string.pick_color_title))
                .initialColor(currentColor )
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
//                        currentColor  = selectedColor;

                    }
                })
                .setPositiveButton(getString(android.R.string.ok), new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        if(setBrush)
                            config.setStrokeColor(selectedColor);
                        else if(isBackground){
                            backGroundColor = selectedColor;
                            mDrawableView.setBackgroundColor(selectedColor);
                        }

//                        premiumFeature();
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekbar_grid:
                this.mGridSpace = progress+(int) getResources().getDimension(R.dimen.grid_default_height);
                showGrid(mGridSpace , false);
                break;

            default:
                this.mStrokeSize = progress;
                this.config.setStrokeWidth(mStrokeSize);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_drawer:
                if((boolean) mToolsView.getTag()){
                    animateUp();

                }else{
                    animateDown();

                }
                break;
            case R.id.nav_up:
                animateUp();
                break;

            default:
                break;
        }

    }


    private void animateUp(){
        mToolsView.setVisibility(View.GONE);
        Animation slideAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        mToolsView.startAnimation(slideAnimation);
        mToolsView.setTag(false);

        slideAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });
    }

    private void animateDown(){
        mToolsView.setVisibility(View.VISIBLE);
        Animation slideAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        mToolsView.startAnimation(slideAnimation);
        mToolsView.setTag(true);
    }

    public void onBackPressed() {
        if((boolean) mToolsView.getTag())
            animateUp();
    }

//    private void premiumFeature(){
//        Builder mBuilder = new Builder(getActivity());
//        mBuilder.setTitle(getString(R.string.premium_title))
//                .setMessage(getString(R.string.premium_msg))
//                .setPositiveButton(getString(R.string.premium_btn), new DialogInterface.OnClickListener() {
//
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        try {
//                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//                        } catch (android.content.ActivityNotFoundException anfe) {
//                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
//                        }
//
//                    }
//                }).setNegativeButton(android.R.string.cancel, null).show();
//
//    }


    @Override
    public void onStop() {
        super.onStop();
//        ((MainActivity)getActivity()).showFab();
    }
}
