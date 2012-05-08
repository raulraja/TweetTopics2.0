package com.javielinux.tweettopics;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import preferences.Preferences;
import zoom.WrapMotionEvent;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.android.dataframework.DataFramework;
import com.cyrilmottier.android.greendroid.R;

public class AdjustImage extends GDActivity implements OnTouchListener
{
	
	private static final int OK_ID = Menu.FIRST;
	
    private int screen_w = 320;
    private int screen_h = 480;
        
    private Bitmap mBitmap = null;
    
    private float imageWidth = 0;
    private float imageHeight = 0;
     
    private ImageView mImage;  
    private Resources res;
        
    private String file = "";
    private String url = "";
    
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    
    private float mScale = 1f;
    
    static final int TYPE_ZOOM_NORMAL = 0;
    static final int TYPE_ZOOM_VERTICAL = 1;
    static final int TYPE_ZOOM_HORIZONTAL = 2;

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    static final int ZOOM_ONEFINGER = 3;
    int mode = NONE;

    PointF start = new PointF();
    PointF mid = new PointF();
    PointF startOneFinger = new PointF();
    float oldDist = 1f;

    private GestureDetector gd;
    
    private ActionBar mActionBar;

    public void refreshTheme() {		
    	mActionBar.setBackgroundColor(Color.parseColor("#"+new ThemeManager(this).getStringColor("color_top_bar")));
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState); 
        
        setTitle(R.string.title_prf_image_background);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("file")) file = savedInstanceState.getString("file");
			if (savedInstanceState.containsKey("url")) url = savedInstanceState.getString("url");
		} else {
			Bundle extras = getIntent().getExtras();  
			if (extras != null) {
				file = (extras.containsKey("file")) ? extras.getString("file") : "";
				url = (extras.containsKey("url")) ? extras.getString("url") : "";
			} else {
				file = "";
				url = "";
			}
		}
        
        
        try {
			DataFramework.getInstance().open(this, Utils.packageName);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Utils.setActivity(this);
		
		gd = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
			@Override
			public void onLongPress(MotionEvent e) {
				if (Utils.preference.getBoolean("prf_zoom_one_finger", false)) {
					((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100); 
					mode = ZOOM_ONEFINGER;
					startOneFinger.set(e.getX(), e.getY());
				}
			}			
		});
        
        res = getResources();
        
        mid.x = 0f;
        mid.y = 0f;
        
        DisplayMetrics dm = res.getDisplayMetrics();
        screen_w = dm.widthPixels;
        screen_h = dm.heightPixels;
        
        if (!file.equals("")) {
        	mBitmap = BitmapFactory.decodeFile(file);
        } else {
        	Log.d(Utils.TAG, url);
        	mBitmap = Utils.getBitmap(url, screen_h);
        }
        
        if (mBitmap==null) {
        	Utils.showMessage(this, getString(R.string.problem_image));
        	finish();
        }

       	imageWidth = mBitmap.getWidth();
        imageHeight = mBitmap.getHeight();
        
        FrameLayout fr = new FrameLayout(this);
                                
        mImage = new ImageView(this);
        mImage.setImageBitmap(mBitmap);
        
        float scrollX = (-(imageWidth/2))+(screen_w/2);
        float scrollY = (-(imageHeight/2))+(screen_h/2);

        mImage.setScaleType(ScaleType.MATRIX);
        mImage.setOnTouchListener(this);
        matrix.setTranslate(scrollX, scrollY);
        
        if (!file.equals("")) {
        	try {
		        ExifInterface exif = new ExifInterface(file);
		        
		        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
		        
		        if (orientation==3) {
		        	matrix.postRotate(180);
		        } else if (orientation==6) {
		        	matrix.postRotate(90);
		        } else if (orientation==8) {
		        	matrix.postRotate(270);
		        }
        	} catch (IOException e1) {
				e1.printStackTrace();
			}
        }
        
        mImage.setImageMatrix(matrix);
        
        fr.addView(mImage);

        setActionBarContentView(fr, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        
        mActionBar = getGreenDroidActionBar();
        mActionBar.addItem(Type.Add);
        

        ImageView mIconActivity = (ImageView) mActionBar.findViewById(R.id.gd_action_bar_home_item);
        mIconActivity.setVisibility(View.GONE);
        
        refreshTheme();
        
        Utils.showMessage(this, getString(R.string.adjust_background));
        
    }
    
	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (position) {
		case 0:
			save();
			break;
		default:
			break;
		}
		return super.onHandleActionBarItemClick(item, position);
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, OK_ID, 0,  R.string.save)
			.setIcon(android.R.drawable.ic_menu_save);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case OK_ID:
        	save();
        	return true;
        }
       
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void save() {
    	Bitmap outBitmap = null;
    	
    	try {
    		outBitmap = Bitmap.createBitmap(screen_w, screen_h, Bitmap.Config.RGB_565);
    	} catch (OutOfMemoryError e) {
    		try {
    			outBitmap = Bitmap.createBitmap(screen_w/2, screen_h/2, Bitmap.Config.RGB_565);
    		} catch (OutOfMemoryError e2) {
    			e2.printStackTrace();
    		}
    	}
    	if (outBitmap!=null) {
	    	Canvas c = new Canvas(outBitmap);
	    	c.drawColor(Color.BLACK);
	    	c.drawBitmap(mBitmap, matrix, null);
	
	    	FileOutputStream out;
			try {
				out = new FileOutputStream(Preferences.IMAGE_WALLPAPER);
				outBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			outBitmap.recycle();
			outBitmap = null;
			System.gc();
	
			Utils.showMessage(this, getString(R.string.correct_background));
	
			setResult(RESULT_OK);
	        finish();
    	} else {
    		Utils.showMessage(this, getString(R.string.error_general));
    	}
    }
        
    @Override
    public boolean onTouch(View v, MotionEvent rawEvent) {

    	WrapMotionEvent event = WrapMotionEvent.wrap(rawEvent);
    	ImageView view = (ImageView) v;

    	switch (event.getAction() & MotionEvent.ACTION_MASK) {
    	case MotionEvent.ACTION_DOWN:
    		savedMatrix.set(matrix);
    		start.set(event.getX(), event.getY());
    		mode = DRAG;
    		break;
    	case MotionEvent.ACTION_POINTER_DOWN:
    		oldDist = spacing(event);
    		if (oldDist > 10f) {
    			savedMatrix.set(matrix);
    			midPoint(mid, event);
    			mode = ZOOM;
    		}
    		break;
    	case MotionEvent.ACTION_UP:
    	case MotionEvent.ACTION_POINTER_UP:
    		mode = NONE;
    		break;
    	case MotionEvent.ACTION_MOVE:
    		if (mode == DRAG) {
    			matrix.set(savedMatrix);
    			matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
    		} else if (mode == ZOOM) {
    			float newDist = spacing(event);
    			if (newDist > 10f) {
    				matrix.set(savedMatrix);
    				mScale = newDist / oldDist;
    				float scale_x = mScale;
    				float scale_y = mScale;
    				matrix.postScale(scale_x, scale_y, mid.x, mid.y);
    			}
    		} else if (mode == ZOOM_ONEFINGER) {
    			float y = startOneFinger.y - event.getY(0);
    			if ( (y > 10f) || (y < -10f) ) {
    				matrix.set(savedMatrix);
    				mScale = 1.0f + (y / (screen_h/2));
    				float scale_x = mScale;
    				float scale_y = mScale;
    				matrix.postScale(scale_x, scale_y, startOneFinger.x, startOneFinger.y);
    			}
    		}
    		break;
    	}
    	
    	view.setImageMatrix(matrix);
    	gd.onTouchEvent(rawEvent);

    	return true;
    }
	   
    private float spacing(WrapMotionEvent event) {
    	float x = event.getX(0) - event.getX(1);
    	float y = event.getY(0) - event.getY(1);
    	return FloatMath.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, WrapMotionEvent event) {
    	float x = event.getX(0) + event.getX(1);
    	float y = event.getY(0) + event.getY(1);
    	point.set(x / 2, y / 2);
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBitmap.recycle();
		mBitmap = null;	
		DataFramework.getInstance().close();
	}
	
    @Override
    protected void onResume() {
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("file", file);
		outState.putString("url", url);
		super.onSaveInstanceState(outState);
	}
    
}