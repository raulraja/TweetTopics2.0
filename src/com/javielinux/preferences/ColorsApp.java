package com.javielinux.preferences;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.components.ColorDialog;
import com.javielinux.tweettopics2.NewStatusActivity;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.LinksUtils;
import com.javielinux.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class ColorsApp extends ListActivity implements ColorDialog.OnChangeColor {
	
	protected static final int RESTART_COLOR_ID = Menu.FIRST;
	protected static final int SAVE_THEME_ID = Menu.FIRST+1;
	protected static final int LOAD_THEME_ID = Menu.FIRST+2;
	protected static final int SHARE_THEME_ID = Menu.FIRST+3;
	protected static final int DELETE_THEME_ID = Menu.FIRST+4;
	
	public static final String SEP_BLOCK = "--";
	public static final String SEP_VALUES = "//";
	
	private ColorsAppAdapter adapter;
	private int mCurrentPosition = -1;
	private int mPositionBeforeRefresh = -1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        try {
            DataFramework.getInstance().open(ColorsApp.this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setContentView(R.layout.pref_colors_app);
        
        setTitle(R.string.title_prf_colors_app);
        
        refresh();
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, RESTART_COLOR_ID, 0,  R.string.restarts_colors)
			.setIcon(android.R.drawable.ic_menu_revert);
        menu.add(0, SAVE_THEME_ID, 0,  R.string.save)
			.setIcon(android.R.drawable.ic_menu_save);
        menu.add(0, LOAD_THEME_ID, 0,  R.string.load_theme)
			.setIcon(android.R.drawable.ic_menu_agenda);
        menu.add(0, SHARE_THEME_ID, 0,  R.string.share)
			.setIcon(android.R.drawable.ic_menu_share);
        menu.add(0, DELETE_THEME_ID, 0,  R.string.delete_theme)
			.setIcon(android.R.drawable.ic_menu_delete);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case RESTART_COLOR_ID:
        	restartColors();
            return true;
        case SAVE_THEME_ID:
        	dialogSaveTheme();
            return true;
        case LOAD_THEME_ID:
        	dialogLoadTheme();
            return true;
        case SHARE_THEME_ID:
        	exportTheme(this);
            return true;
        case DELETE_THEME_ID:
        	dialogDeleteTheme();
            return true;
        }
        return false;
    }
    
    private void dialogSaveTheme() {
    	final ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("themes");

		if (ents.size()>0) {
			CharSequence[] c = new CharSequence[ents.size()];
			for (int i=0; i<ents.size(); i++) {
				c[i] = ents.get(i).getString("name");
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.overwrite_theme);
			builder.setItems(c, new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Entity ent = ents.get(which);
					ent.setValue("theme", getTheme(ColorsApp.this));
					ent.save();
					Utils.showMessage(ColorsApp.this, ColorsApp.this.getString(R.string.correct_new_theme));
				}
	
				
			});
			builder.setNeutralButton(R.string.new_theme, new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int whichButton) {
	        		saveNewTheme();
	        	}
	        });
			builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int whichButton) {
	        	}
	        });
	        builder.create();
	        builder.show();	 
		} else {
			saveNewTheme();
		}
    }
    
    private void dialogLoadTheme() {
    	final ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("themes");

		if (ents.size()>0) {
			CharSequence[] c = new CharSequence[ents.size()];
			for (int i=0; i<ents.size(); i++) {
				c[i] = ents.get(i).getString("name");
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.load_theme);
			builder.setItems(c, new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					loadTheme(ColorsApp.this, ents.get(which).getString("theme"));
					refresh ();
				}
	
				
			});
	        builder.create();
	        builder.show();	 
		} else {
			Utils.showMessage(ColorsApp.this, ColorsApp.this.getString(R.string.no_themes));
		}
    }
    
    private void dialogDeleteTheme() {
    	final ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("themes");

		if (ents.size()>0) {
			CharSequence[] c = new CharSequence[ents.size()];
			for (int i=0; i<ents.size(); i++) {
				c[i] = ents.get(i).getString("name");
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.delete_theme);
			builder.setItems(c, new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ents.get(which).delete();
					Utils.showMessage(ColorsApp.this, ColorsApp.this.getString(R.string.correct_delete_theme));
				}
	
				
			});
	        builder.create();
	        builder.show();	 
		} else {
			Utils.showMessage(ColorsApp.this, ColorsApp.this.getString(R.string.no_themes));
		}
    }
    
    public static void loadTheme(Context cnt, String theme) {
    	
    	HashMap<String,String> t = new HashMap<String,String>();
    	
    	StringTokenizer tokens = new StringTokenizer(theme, SEP_BLOCK);  
    	
    	while(tokens.hasMoreTokens()) {  
    		try {
    			String token = tokens.nextToken();
    			StringTokenizer hash = new StringTokenizer(token, SEP_VALUES);
    			if (hash.countTokens()==2) {
					String key = hash.nextToken();
					String value = hash.nextToken();
					t.put(key, value);
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	
    	SharedPreferences preferences = Utils.getPreference(cnt);
    	Editor editor = preferences.edit();
    	
    	if (t.containsKey("theme")) {
        	editor.putString("prf_theme", t.get("theme"));
    	}
    	
//    	if (t.containsKey("positions_links")) {
//        	editor.putString("prf_positions_links", t.get("positions_links"));
//    	}
    	
    	if (t.containsKey("use_gradient")) {
        	editor.putBoolean("prf_use_gradient", t.get("use_gradient").equals("1"));
    	}
    	
    	if (t.containsKey("use_divider_tweet")) {
        	editor.putBoolean("prf_use_divider_tweet", t.get("use_divider_tweet").equals("1"));
    	}
    	
    	editor.commit();
    	
    	// cargamos primero el tema por defecto
    	if (t.containsKey("theme")) {
    		restartColors(cnt);
    	}    	
    	
    	if (t.containsKey("color_tweet_text")) new InfoColorsApp(cnt, "color_tweet_text").setColor(cnt, t.get("color_tweet_text"));
    	
    	if (t.containsKey("color_tweet_usename")) new InfoColorsApp(cnt, "color_tweet_usename").setColor(cnt, t.get("color_tweet_usename"));
    	if (t.containsKey("color_tweet_source")) new InfoColorsApp(cnt, "color_tweet_source").setColor(cnt, t.get("color_tweet_source"));
    	if (t.containsKey("color_tweet_date")) new InfoColorsApp(cnt, "color_tweet_date").setColor(cnt, t.get("color_tweet_date"));
    	if (t.containsKey("color_tweet_retweet")) new InfoColorsApp(cnt, "color_tweet_retweet").setColor(cnt, t.get("color_tweet_retweet"));

    	if (t.containsKey("color_top_bar")) new InfoColorsApp(cnt, "color_top_bar").setColor(cnt, t.get("color_top_bar"));
    	if (t.containsKey("color_bottom_bar")) new InfoColorsApp(cnt, "color_bottom_bar").setColor(cnt, t.get("color_bottom_bar"));
    	
    	if (t.containsKey("list_background_row_color")) new InfoColorsApp(cnt, "list_background_row_color").setColor(cnt, t.get("list_background_row_color"));
    	if (t.containsKey("color_shadow_listview")) new InfoColorsApp(cnt, "color_shadow_listview").setColor(cnt, t.get("color_shadow_listview"));
    	if (t.containsKey("color_divider_tweet")) new InfoColorsApp(cnt, "color_divider_tweet").setColor(cnt, t.get("color_divider_tweet"));
    	if (t.containsKey("color_tweet_no_read")) new InfoColorsApp(cnt, "color_tweet_no_read").setColor(cnt, t.get("color_tweet_no_read"));
    	if (t.containsKey("color_load_more_break")) new InfoColorsApp(cnt, "color_load_more_break").setColor(cnt, t.get("color_load_more_break"));
    	
    	if (t.containsKey("tweet_color_selected")) new InfoColorsApp(cnt, "tweet_color_selected").setColor(cnt, t.get("tweet_color_selected"));
    	if (t.containsKey("tweet_color_link")) new InfoColorsApp(cnt, "tweet_color_link").setColor(cnt, t.get("tweet_color_link"));
    	if (t.containsKey("tweet_color_hashtag")) new InfoColorsApp(cnt, "tweet_color_hashtag").setColor(cnt, t.get("tweet_color_hashtag"));
    	if (t.containsKey("tweet_color_user")) new InfoColorsApp(cnt, "tweet_color_user").setColor(cnt, t.get("tweet_color_user"));
    	
    	if (t.containsKey("color_main_button_normal")) new InfoColorsApp(cnt, "color_main_button_normal").setColor(cnt, t.get("color_main_button_normal"));
    	if (t.containsKey("color_main_button_selected")) new InfoColorsApp(cnt, "color_main_button_selected").setColor(cnt, t.get("color_main_button_selected"));
    	if (t.containsKey("color_tweet_buttons_normal")) new InfoColorsApp(cnt, "color_tweet_buttons_normal").setColor(cnt, t.get("color_tweet_buttons_normal"));
    	if (t.containsKey("color_tweet_buttons_press")) new InfoColorsApp(cnt, "color_tweet_buttons_press").setColor(cnt, t.get("color_tweet_buttons_press"));
    	
    	if (t.containsKey("color_1")) new InfoColorsApp(cnt, "color_1").setColor(cnt, t.get("color_1"));
    	if (t.containsKey("color_2")) new InfoColorsApp(cnt, "color_2").setColor(cnt, t.get("color_2"));
    	if (t.containsKey("color_3")) new InfoColorsApp(cnt, "color_3").setColor(cnt, t.get("color_3"));
    	if (t.containsKey("color_4")) new InfoColorsApp(cnt, "color_4").setColor(cnt, t.get("color_4"));
    	if (t.containsKey("color_5")) new InfoColorsApp(cnt, "color_5").setColor(cnt, t.get("color_5"));
    	if (t.containsKey("color_6")) new InfoColorsApp(cnt, "color_6").setColor(cnt, t.get("color_6"));
    	if (t.containsKey("color_7")) new InfoColorsApp(cnt, "color_7").setColor(cnt, t.get("color_7"));
    	if (t.containsKey("color_8")) new InfoColorsApp(cnt, "color_8").setColor(cnt, t.get("color_8"));
    	
    }
    
    private void saveNewTheme() {
    	final EditText et = new EditText(this);
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.getString(R.string.new_theme));
		builder.setMessage(this.getString(R.string.desc_new_theme));
		builder.setView(et);
		builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = et.getText().toString();
				if (name.equals("")) {
					Utils.showMessage(ColorsApp.this, ColorsApp.this.getString(R.string.no_name_new_theme));
				} else {
					Entity ent = new Entity("themes");
					ent.setValue("name", name);
					ent.setValue("theme", getTheme(ColorsApp.this));
					ent.save();
					Utils.showMessage(ColorsApp.this, ColorsApp.this.getString(R.string.correct_new_theme));
				}
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
    
    public static String getTheme(Context cnt) {
    	String theme = "";
    	theme += "color_tweet_text"+SEP_VALUES+new InfoColorsApp(cnt, "color_tweet_text").getRGB();
    	theme += SEP_BLOCK+"color_tweet_usename"+SEP_VALUES+new InfoColorsApp(cnt, "color_tweet_usename").getRGB();
    	theme += SEP_BLOCK+"color_tweet_source"+SEP_VALUES+new InfoColorsApp(cnt, "color_tweet_source").getRGB();
    	theme += SEP_BLOCK+"color_tweet_date"+SEP_VALUES+new InfoColorsApp(cnt, "color_tweet_date").getRGB();
    	theme += SEP_BLOCK+"color_tweet_retwee"+SEP_VALUES+new InfoColorsApp(cnt, "color_tweet_retweet").getRGB();
    	
    	theme += SEP_BLOCK+"color_top_bar"+SEP_VALUES+new InfoColorsApp(cnt, "color_top_bar").getRGB();
    	theme += SEP_BLOCK+"color_bottom_bar"+SEP_VALUES+new InfoColorsApp(cnt, "color_bottom_bar").getRGB();
    	
    	theme += SEP_BLOCK+"list_background_row_color"+SEP_VALUES+new InfoColorsApp(cnt, "list_background_row_color").getRGB();
    	theme += SEP_BLOCK+"color_shadow_listview"+SEP_VALUES+new InfoColorsApp(cnt, "color_shadow_listview").getRGB();
    	theme += SEP_BLOCK+"color_divider_tweet"+SEP_VALUES+new InfoColorsApp(cnt, "color_divider_tweet").getRGB();
    	theme += SEP_BLOCK+"color_tweet_no_read"+SEP_VALUES+new InfoColorsApp(cnt, "color_tweet_no_read").getRGB();
    	theme += SEP_BLOCK+"color_load_more_break"+SEP_VALUES+new InfoColorsApp(cnt, "color_load_more_break").getRGB();
    	
    	theme += SEP_BLOCK+"tweet_color_selected"+SEP_VALUES+new InfoColorsApp(cnt, "tweet_color_selected").getRGB();
    	theme += SEP_BLOCK+"tweet_color_link"+SEP_VALUES+new InfoColorsApp(cnt, "tweet_color_link").getRGB();
    	theme += SEP_BLOCK+"tweet_color_hashtag"+SEP_VALUES+new InfoColorsApp(cnt, "tweet_color_hashtag").getRGB();
    	theme += SEP_BLOCK+"tweet_color_user"+SEP_VALUES+new InfoColorsApp(cnt, "tweet_color_user").getRGB();
    	
    	theme += SEP_BLOCK+"color_main_button_normal"+SEP_VALUES+new InfoColorsApp(cnt, "color_main_button_normal").getRGB();
    	theme += SEP_BLOCK+"color_main_button_selected"+SEP_VALUES+new InfoColorsApp(cnt, "color_main_button_selected").getRGB();
    	theme += SEP_BLOCK+"color_tweet_buttons_normal"+SEP_VALUES+new InfoColorsApp(cnt, "color_tweet_buttons_normal").getRGB();
    	theme += SEP_BLOCK+"color_tweet_buttons_press"+SEP_VALUES+new InfoColorsApp(cnt, "color_tweet_buttons_press").getRGB();

    	theme += SEP_BLOCK+"color_1"+SEP_VALUES+new InfoColorsApp(cnt, "color_1").getRGB();
    	theme += SEP_BLOCK+"color_2"+SEP_VALUES+new InfoColorsApp(cnt, "color_2").getRGB();
    	theme += SEP_BLOCK+"color_3"+SEP_VALUES+new InfoColorsApp(cnt, "color_3").getRGB();
    	theme += SEP_BLOCK+"color_4"+SEP_VALUES+new InfoColorsApp(cnt, "color_4").getRGB();
    	theme += SEP_BLOCK+"color_5"+SEP_VALUES+new InfoColorsApp(cnt, "color_5").getRGB();
    	theme += SEP_BLOCK+"color_6"+SEP_VALUES+new InfoColorsApp(cnt, "color_6").getRGB();
    	theme += SEP_BLOCK+"color_7"+SEP_VALUES+new InfoColorsApp(cnt, "color_7").getRGB();
    	theme += SEP_BLOCK+"color_8"+SEP_VALUES+new InfoColorsApp(cnt, "color_8").getRGB();
    	
    	SharedPreferences preferences = Utils.getPreference(cnt);
    	    	    	
    	theme += SEP_BLOCK+"theme"+SEP_VALUES+preferences.getString("prf_theme", "1");
//    	theme += SEP_BLOCK+"positions_links"+SEP_VALUES+preferences.getString("prf_positions_links", "1");
    	theme += SEP_BLOCK+"use_gradient"+SEP_VALUES + (preferences.getBoolean("prf_use_gradient", true)?"1":"0");
    	theme += SEP_BLOCK+"use_divider_tweet"+SEP_VALUES + (preferences.getBoolean("prf_use_divider_tweet", true)?"1":"0");

    	return theme;
    }
    
    public static void restartColors(Context cnt) {
    	new InfoColorsApp(cnt, "color_tweet_text").restartColor(cnt);
    	new InfoColorsApp(cnt, "color_tweet_usename").restartColor(cnt);
    	new InfoColorsApp(cnt, "color_tweet_source").restartColor(cnt);
    	new InfoColorsApp(cnt, "color_tweet_date").restartColor(cnt);
    	new InfoColorsApp(cnt, "color_tweet_retweet").restartColor(cnt);
    	
    	new InfoColorsApp(cnt, "color_top_bar").restartColor(cnt);
    	new InfoColorsApp(cnt, "color_bottom_bar").restartColor(cnt);
    	
    	new InfoColorsApp(cnt, "list_background_row_color").restartColor(cnt);
    	new InfoColorsApp(cnt, "color_shadow_listview").restartColor(cnt);
    	new InfoColorsApp(cnt, "color_divider_tweet").restartColor(cnt);
    	new InfoColorsApp(cnt, "color_tweet_no_read").restartColor(cnt);
    	new InfoColorsApp(cnt, "color_load_more_break").restartColor(cnt);
    	
    	new InfoColorsApp(cnt, "tweet_color_selected").restartColor(cnt);
    	new InfoColorsApp(cnt, "tweet_color_link").restartColor(cnt);
    	new InfoColorsApp(cnt, "tweet_color_hashtag").restartColor(cnt);
    	new InfoColorsApp(cnt, "tweet_color_user").restartColor(cnt);
    	
    	new InfoColorsApp(cnt, "color_main_button_normal").restartColor(cnt);
    	new InfoColorsApp(cnt, "color_main_button_selected").restartColor(cnt);
    	new InfoColorsApp(cnt, "color_tweet_buttons_normal").restartColor(cnt);
    	new InfoColorsApp(cnt, "color_tweet_buttons_press").restartColor(cnt);
    	
    	new InfoColorsApp(cnt, "color_1").restartColor(cnt);
    	new InfoColorsApp(cnt, "color_2").restartColor(cnt);
    	new InfoColorsApp(cnt, "color_3").restartColor(cnt);
    	new InfoColorsApp(cnt, "color_4").restartColor(cnt);
    	new InfoColorsApp(cnt, "color_5").restartColor(cnt);
    	new InfoColorsApp(cnt, "color_6").restartColor(cnt);
    	new InfoColorsApp(cnt, "color_7").restartColor(cnt);
    	new InfoColorsApp(cnt, "color_8").restartColor(cnt);
    	
    	SharedPreferences preferences = Utils.getPreference(cnt);
    	Editor editor = preferences.edit();
    	
       	editor.putBoolean("prf_use_gradient", true);
       	editor.putBoolean("prf_use_divider_tweet", true);
    	
    	editor.commit();
    	
    }
    
    private void restartColors() {
    	
    	restartColors(this);
    	
    	refresh ();
    	
    }
    
    public static void exportTheme(Context cnt) {
    	Intent newstatus = new Intent(cnt, NewStatusActivity.class);
    	newstatus.putExtra("text", Utils.HASHTAG_SHARE_THEME + " " + URLExportTheme(cnt));
    	newstatus.putExtra("type", NewStatusActivity.TYPE_NORMAL);
    	cnt.startActivity(newstatus);
    }
    
    public static String URLExportTheme(Context cnt) {
		
		String url = Utils.URL_SHARE_THEME_QR;

		url += SEP_BLOCK+getTheme(cnt);
		
		return LinksUtils.shortURL(cnt, url);
		
	}
    
    private void refresh () {
        ArrayList<InfoColorsApp> statii = new ArrayList<InfoColorsApp>();
        
        statii.add(new InfoColorsApp(getString(R.string.section_texts)));
        
        statii.add(new InfoColorsApp(this, "color_tweet_text"));
        statii.add(new InfoColorsApp(this, "color_tweet_usename"));
        statii.add(new InfoColorsApp(this, "color_tweet_source"));
        statii.add(new InfoColorsApp(this, "color_tweet_date"));
        statii.add(new InfoColorsApp(this, "color_tweet_retweet"));
        
        statii.add(new InfoColorsApp(getString(R.string.section_bars)));
        
        statii.add(new InfoColorsApp(this, "color_top_bar"));
        statii.add(new InfoColorsApp(this, "color_bottom_bar"));
        
        statii.add(new InfoColorsApp(getString(R.string.section_bgs)));
        
        statii.add(new InfoColorsApp(this, "list_background_row_color"));
        statii.add(new InfoColorsApp(this, "tweet_color_selected"));
        statii.add(new InfoColorsApp(this, "color_shadow_listview"));
        statii.add(new InfoColorsApp(this, "color_divider_tweet"));
        statii.add(new InfoColorsApp(this, "color_tweet_no_read"));
        statii.add(new InfoColorsApp(this, "color_load_more_break"));
        
        statii.add(new InfoColorsApp(getString(R.string.section_links)));
        
        statii.add(new InfoColorsApp(this, "tweet_color_link"));
        statii.add(new InfoColorsApp(this, "tweet_color_hashtag"));
        statii.add(new InfoColorsApp(this, "tweet_color_user"));
        
        statii.add(new InfoColorsApp(getString(R.string.section_buttons)));
        
        statii.add(new InfoColorsApp(this, "color_main_button_normal"));
        statii.add(new InfoColorsApp(this, "color_main_button_selected"));
        statii.add(new InfoColorsApp(this, "color_tweet_buttons_normal"));
        statii.add(new InfoColorsApp(this, "color_tweet_buttons_press"));
        
        statii.add(new InfoColorsApp(getString(R.string.section_8colors)));
        
        statii.add(new InfoColorsApp(this, "color_1"));
        statii.add(new InfoColorsApp(this, "color_2"));
        statii.add(new InfoColorsApp(this, "color_3"));
        statii.add(new InfoColorsApp(this, "color_4"));
        statii.add(new InfoColorsApp(this, "color_5"));
        statii.add(new InfoColorsApp(this, "color_6"));
        statii.add(new InfoColorsApp(this, "color_7"));
        statii.add(new InfoColorsApp(this, "color_8"));
        
        
        adapter = new ColorsAppAdapter(this, statii);
        
        this.setListAdapter(adapter);
        
        if (mPositionBeforeRefresh>=0) {
        	this.getListView().setSelection(mPositionBeforeRefresh);
        }
        
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		InfoColorsApp info = adapter.getItem(position);
		
		if (info.getType()==InfoColorsApp.TYPE_COLOR) {
			mCurrentPosition = position; 
			
			ColorDialog color = new ColorDialog(this, this.getString(R.string.select_color), info.getRGB());
			color.setOnchangeColor(this);
			color.show();
		}
		
	}

	@Override
	public void changeColor(String rgb) {
		adapter.getItem(mCurrentPosition).setColor(this, rgb);
		mPositionBeforeRefresh = this.getListView().getFirstVisiblePosition();
		refresh ();
	}
	
    @Override
    protected void onDestroy() {
    	super.onDestroy();
        DataFramework.getInstance().close();
    }

    
}