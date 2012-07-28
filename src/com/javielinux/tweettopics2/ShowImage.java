package com.javielinux.tweettopics2;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.javielinux.components.ImageViewZoomTouch;
import com.javielinux.utils.Utils;

public class ShowImage extends Activity
{
        
         
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState); 
                
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                                WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        
        ImageViewZoomTouch image = new ImageViewZoomTouch(this);
        image.setImageBitmap(BitmapFactory.decodeFile(Utils.appDirectory + "image_large.jpg"));

        FrameLayout fr = new FrameLayout(this);
        ImageView bg = new ImageView(this);
        bg.setBackgroundColor(Color.parseColor("#cc000000"));
        
        fr.addView(bg, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        fr.addView(image, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        
        setContentView(fr, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

    }
  
     
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
    
}