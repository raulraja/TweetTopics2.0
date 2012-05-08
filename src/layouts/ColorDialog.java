/*
 * Copyright (C) 2011  Javier Perez Pacheco
 *
 * Clase para crear un diálogo en Android y poder seleccionar un color RGB 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Javier Perez Pacheco
 * Cadiz (Spain)
 * javi.pacheco@gmail.com
 * 
 *
 */

package layouts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.javielinux.tweettopics.Utils;

public class ColorDialog {
	
	public interface OnChangeColor {
		public void changeColor(String rgb);
	}
	
	private OnChangeColor OnchangeColor;
	
	public String rgb = "";
	
	public String title = "";
	
	public Context context;
	
	private TextView tvR, tvG, tvB;
	private SeekBar seekBarR, seekBarG, seekBarB;
	private ImageView imgColor;
	
	public ColorDialog(Context context, String title, String defaultRGB ) {
		rgb = defaultRGB;
		this.title = title;
		this.context = context;
	}
	
	public void setOnchangeColor(OnChangeColor cc) {
		OnchangeColor = cc;
	}
	
	public void show( ) {

		int[] values = getRGB(rgb);
		
		LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		ll.weight = 1;
		
		String aboutTitle = String.format(title);

		LinearLayout mainView=new LinearLayout(context);
		mainView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		mainView.setOrientation(LinearLayout.VERTICAL);

		LinearLayout llR=new LinearLayout(context);
		llR.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		llR.setOrientation(LinearLayout.HORIZONTAL);
		
		tvR = new TextView(context);
		tvR.setText("R ("+values[0]+")");
		tvR.setMinWidth(80);
		tvR.setPadding(4, 4, 4, 4);
		llR.addView(tvR);
		seekBarR = new SeekBar(context);
		seekBarR.setLayoutParams(ll);
		seekBarR.setMax(255);
		seekBarR.setProgress(values[0]);
		seekBarR.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				tvR.setText("R ("+progress+")");
				refreshImageColor();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
			
		});
		llR.addView(seekBarR);
		mainView.addView(llR);
		
		LinearLayout llG=new LinearLayout(context);
		llG.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		llG.setOrientation(LinearLayout.HORIZONTAL);
		
		tvG = new TextView(context);
		tvG.setText("G ("+values[1]+")");
		tvG.setMinWidth(80);
		tvG.setPadding(4, 4, 4, 4);
		llG.addView(tvG);
		seekBarG = new SeekBar(context);
		seekBarG.setLayoutParams(ll);
		seekBarG.setMax(255);
		seekBarG.setProgress(values[1]);
		seekBarG.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				tvG.setText("G ("+progress+")");
				refreshImageColor();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
			
		});
		llG.addView(seekBarG);
		mainView.addView(llG);
		
		LinearLayout llB=new LinearLayout(context);
		llB.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		llB.setOrientation(LinearLayout.HORIZONTAL);
		
		tvB = new TextView(context);
		tvB.setText("B ("+values[2]+")");
		tvB.setMinWidth(80);
		tvB.setPadding(4, 4, 4, 4);
		llB.addView(tvB);
		seekBarB = new SeekBar(context);
		seekBarB.setLayoutParams(ll);
		seekBarB.setMax(255);
		seekBarB.setProgress(values[2]);
		seekBarB.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				tvB.setText("B ("+progress+")");
				refreshImageColor();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
			
		});
		llB.addView(seekBarB);
		mainView.addView(llB);
		
		imgColor = new ImageView(context);	
		imgColor.setAdjustViewBounds(true);
		refreshImageColor();

		mainView.addView(imgColor, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(aboutTitle);
		alert.setCancelable(true);
		alert.setPositiveButton(context.getString(android.R.string.ok), new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (OnchangeColor!=null) OnchangeColor.changeColor(intToHex());
			}

			
		});
		alert.setView(mainView);
		alert.create();

		alert.show();
		
	}
	
	public void refreshImageColor() {
		String color = "#"+intToHex();

		try {
			Bitmap bmp = Bitmap.createBitmap(200, 50, Config.RGB_565);
			Canvas c = new Canvas(bmp);
			c.drawColor(Color.parseColor(color));
			imgColor.setImageBitmap(bmp);
		} catch (Exception e) {
			Log.d(Utils.TAG, "problema al establecer color: " + color);
		}
	}
	
	public int[] getRGB(String rgb){
	    int[] val = new int[3];
	    for(int i=0; i<3; i++){
	    	val[i] = Integer.parseInt(rgb.substring(i*2, i*2+2), 16);
	    }
	    return val;
	}
	
	public String intToHex(){
		String r = Integer.toHexString(seekBarR.getProgress());
		String g = Integer.toHexString(seekBarG.getProgress());
		String b = Integer.toHexString(seekBarB.getProgress());
		if (r.length()==1) r = "0"+r;
		if (g.length()==1) g = "0"+g;
		if (b.length()==1) b = "0"+b;
		return r + g + b;  
	}
	
}