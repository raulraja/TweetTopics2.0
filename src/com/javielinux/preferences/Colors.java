package com.javielinux.preferences;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.BaseActivity;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class Colors extends BaseActivity {

	private static final int ADD_ID = Menu.FIRST;
	private static final int BACK_ID = Menu.FIRST+1;
	
	private static final int DIALOG_ITEM = 0;
		
	private ListView mListView;
	private TextView mNoQuietWords;
	
	private static View dialogColor;
	
	private long mCurrentId = 0;
	
	private ColorsAdapter mAdapter;
	
	private ArrayList<String> colors = new ArrayList<String>();
	
	private int mColorSelected = 0;
	
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_ITEM:
            return new AlertDialog.Builder(this)
            .setTitle(R.string.actions)
            .setItems(R.array.items_colors, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which==0) {
                    	newEditItem(mCurrentId);
                    } else {
                       	deleteItem();
                    }
                }
            })
            .create();     
        }
        return null;
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
        	DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        ThemeManager mThemeManager = new ThemeManager(this);
        
        colors = mThemeManager.getColors();
        
        setContentView(R.layout.tweetquick_list);
        
        mListView = (ListView) this.findViewById(R.id.list_tweetquick);
        
        mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mCurrentId = mAdapter.getItem(position).getId();
				showDialog(DIALOG_ITEM);				
			}
        });
        
        mNoQuietWords = (TextView) this.findViewById(R.id.empty);
        
        refresh();
        
    }
    
    private void selectedColor(int color) {
    	ImageButton c1 = (ImageButton) dialogColor.findViewById(R.id.color1);
    	c1.setBackgroundResource((color==0)?R.drawable.btn_default_selected:R.drawable.btn_default_normal);
    	ImageButton c2 = (ImageButton) dialogColor.findViewById(R.id.color2);
    	c2.setBackgroundResource((color==1)?R.drawable.btn_default_selected:R.drawable.btn_default_normal);
    	ImageButton c3 = (ImageButton) dialogColor.findViewById(R.id.color3);
    	c3.setBackgroundResource((color==2)?R.drawable.btn_default_selected:R.drawable.btn_default_normal);
    	ImageButton c4 = (ImageButton) dialogColor.findViewById(R.id.color4);
    	c4.setBackgroundResource((color==3)?R.drawable.btn_default_selected:R.drawable.btn_default_normal);
    	ImageButton c5 = (ImageButton) dialogColor.findViewById(R.id.color5);
    	c5.setBackgroundResource((color==4)?R.drawable.btn_default_selected:R.drawable.btn_default_normal);
    	ImageButton c6 = (ImageButton) dialogColor.findViewById(R.id.color6);
    	c6.setBackgroundResource((color==5)?R.drawable.btn_default_selected:R.drawable.btn_default_normal);
    	ImageButton c7 = (ImageButton) dialogColor.findViewById(R.id.color7);
    	c7.setBackgroundResource((color==6)?R.drawable.btn_default_selected:R.drawable.btn_default_normal);
    	ImageButton c8 = (ImageButton) dialogColor.findViewById(R.id.color8);
    	c8.setBackgroundResource((color==7)?R.drawable.btn_default_selected:R.drawable.btn_default_normal);
    	mColorSelected = color;	
    }
    /*
    private String getColorSelected() {
    	try {
    		return colors.get(mColorSelected);
    	} catch (Exception e) {
    	}
    	return colors.get(0);
    }
    */
    private void loadColors() {
    	ImageButton c1 = (ImageButton) dialogColor.findViewById(R.id.color1);
		Bitmap bmp =  Bitmap.createBitmap(30, 30, Bitmap.Config.RGB_565);
		Canvas cv = new Canvas(bmp);
		cv.drawColor(Color.parseColor(colors.get(0)));
    	c1.setImageBitmap(bmp);
    	c1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedColor(0);
			}

    	});
    	
    	ImageButton c2 = (ImageButton) dialogColor.findViewById(R.id.color2);
		bmp =  Bitmap.createBitmap(30, 30, Bitmap.Config.RGB_565);
		cv = new Canvas(bmp);
		cv.drawColor(Color.parseColor(colors.get(1)));
    	c2.setImageBitmap(bmp);
    	c2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedColor(1);
			}
    		
    	});
    	
    	ImageButton c3 = (ImageButton) dialogColor.findViewById(R.id.color3);
		bmp =  Bitmap.createBitmap(30, 30, Bitmap.Config.RGB_565);
		cv = new Canvas(bmp);
		cv.drawColor(Color.parseColor(colors.get(2)));
    	c3.setImageBitmap(bmp);
    	c3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedColor(2);
			}
    		
    	});
    	ImageButton c4 = (ImageButton) dialogColor.findViewById(R.id.color4);
		bmp =  Bitmap.createBitmap(30, 30, Bitmap.Config.RGB_565);
		cv = new Canvas(bmp);
		cv.drawColor(Color.parseColor(colors.get(3)));
    	c4.setImageBitmap(bmp);
    	c4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedColor(3);
			}
    		
    	});
    	ImageButton c5 = (ImageButton) dialogColor.findViewById(R.id.color5);
		bmp =  Bitmap.createBitmap(30, 30, Bitmap.Config.RGB_565);
		cv = new Canvas(bmp);
		cv.drawColor(Color.parseColor(colors.get(4)));
    	c5.setImageBitmap(bmp);
    	c5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedColor(4);
			}
    		
    	});
    	ImageButton c6 = (ImageButton) dialogColor.findViewById(R.id.color6);
		bmp =  Bitmap.createBitmap(30, 30, Bitmap.Config.RGB_565);
		cv = new Canvas(bmp);
		cv.drawColor(Color.parseColor(colors.get(5)));
    	c6.setImageBitmap(bmp);
    	c6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedColor(5);
			}
    		
    	});
    	ImageButton c7 = (ImageButton) dialogColor.findViewById(R.id.color7);
		bmp =  Bitmap.createBitmap(30, 30, Bitmap.Config.RGB_565);
		cv = new Canvas(bmp);
		cv.drawColor(Color.parseColor(colors.get(6)));
    	c7.setImageBitmap(bmp);
    	c7.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedColor(6);
			}
    		
    	});
    	ImageButton c8 = (ImageButton) dialogColor.findViewById(R.id.color8);
		bmp =  Bitmap.createBitmap(30, 30, Bitmap.Config.RGB_565);
		cv = new Canvas(bmp);
		cv.drawColor(Color.parseColor(colors.get(7)));
    	c8.setImageBitmap(bmp);
    	c8.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedColor(7);
			}
    		
    	});
    }
    
    private void newItem() {
    	newEditItem(-1);
    }
    
    private void newEditItem(long id) {
    	final long idColor =  id;
    	dialogColor = LayoutInflater.from(this).inflate(R.layout.alert_dialog_color, null);
    	
    	loadColors();
    	
    	if (id>0) {
    		Entity ent = new Entity("type_colors", id);
    		TextView t = (TextView) dialogColor.findViewById(R.id.type_color_name);
    		t.setText(ent.getString("name"));
    		selectedColor(ent.getInt("pos"));
    	} else {
    		selectedColor(0);    		
    	}
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.title_dialog_color);
		builder.setMessage(R.string.desc_dialog_color);
		builder.setView(dialogColor);
		builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				TextView t = (TextView) dialogColor.findViewById(R.id.type_color_name);
				Entity ent = new Entity("type_colors", idColor);
				ent.setValue("name", t.getText().toString());
				ent.setValue("pos", mColorSelected);
		    	ent.save();
		    	refresh ();
	
			}
			
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {	
			}
			
		});
		AlertDialog alert = builder.create();
		alert.show();
    }
    

    private void deleteItem() {
    	
    	for (Entity ent : DataFramework.getInstance().getEntityList("colors", "type_color_id="+mCurrentId)) {
    		ent.delete();
    	}
    	Entity ent = new Entity("type_colors", mCurrentId);
    	ent.delete();
    	refresh ();
    }
    
    
    private void refresh () {

    	mAdapter = new ColorsAdapter(this, DataFramework.getInstance().getEntityList("type_colors"));
    	
    	if (mAdapter.getCount()<=0) {
    		mNoQuietWords.setVisibility(View.VISIBLE);
    	} else {
    		mNoQuietWords.setVisibility(View.GONE);
    		mListView.setAdapter(mAdapter);
    	}

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, ADD_ID, 0,  R.string.add)
			.setIcon(android.R.drawable.ic_menu_add);
        menu.add(0, BACK_ID, 0,  R.string.back)
			.setIcon(android.R.drawable.ic_menu_directions);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case ADD_ID:
        	newItem();
            return true;
        case BACK_ID:
        	setResult(RESULT_OK);
			finish();
            return true;
        }
       
        return super.onOptionsItemSelected(item);
	}
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }
    
}