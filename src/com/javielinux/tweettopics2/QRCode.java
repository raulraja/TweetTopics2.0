package com.javielinux.tweettopics2;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

public class QRCode extends Activity {

	private ImageView imgQR;
	private TextView mTVTitleQR, mTVURLQR;
	
	private String mTitleQR = "";
	private String mURLQR = "";
	private String urlPage = "";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
       	setContentView(R.layout.qrcode);
       	
       	if (savedInstanceState != null) {
       		mTitleQR = savedInstanceState.getString("title_qr");
       		mURLQR = savedInstanceState.getString("url_qr");
       	} else {
       		Bundle extras = getIntent().getExtras();  
       		if (extras != null) {
       			mTitleQR = extras.getString("title_qr");
       			mURLQR = extras.getString("url_qr");
       		}
       	}
       	
       	urlPage = "http://chart.apis.google.com/chart?cht=qr&chs=250x250&chl=" + mURLQR;
       	
       	imgQR = (ImageView) this.findViewById(R.id.img_qr);
       	mTVTitleQR = (TextView) this.findViewById(R.id.title_qr);
       	mTVURLQR = (TextView) this.findViewById(R.id.url_qr);
       	
       	mTVTitleQR.setText(mTitleQR);
       	mTVURLQR.setText(mURLQR);
    	
    	try {
    		URL img = new URL(urlPage); 
			imgQR.setImageBitmap(BitmapFactory.decodeStream(img.openStream()));
		} catch (IOException e) {
			e.printStackTrace();
		} 

    }
	
}
