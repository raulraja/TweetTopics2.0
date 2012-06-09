package com.javielinux.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.Path.Direction;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.twitter.ConnectionManager2;
import error_reporter.ErrorReporter;
import infos.CacheData;
import infos.InfoLink;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import preferences.ColorsApp;
import twitter4j.MediaEntity;
import twitter4j.TwitterAPIConfiguration;
import twitter4j.TwitterException;
import twitter4j.URLEntity;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	
	public static final String VERSION = "1.72";

    public static final String EXTRA_CRASHED = "param_crash";

	public static final int NETWORK_TWITTER = 0;
	public static final int NETWORK_FACEBOOK = 1;
	
	public static String ACTION_WIDGET_CONTROL = "com.javielinux.tweettopics2.WIDGET_CONTROL";
	public static final String URI_SCHEME = "tweettopics2_widget_tweets";
	
	//public static final String SEP_BLOCK = "--";
	//public static final String SEP_VALUES = ";;";
	public static final String SEP_BLOCK = "\n";
	public static final String SEP_VALUES = "\t";
	
	public static final String APPLICATION_PREFERENCES = "app_prefs";
	
	public static final String URL_QR = "http://chart.apis.google.com/chart?cht=qr&chs=300x300&chl=tweettopics%%qr";
	
	public static final String URL_SHARE_THEME_QR = "http://chart.apis.google.com/chart?cht=qr&chs=300x300&chl=tweettopics%%theme";
	
	public static final String TAG = "TweetTopics2";
	public static final String TAG_ALARM = "TweetTopics2Alarm";
	public static final String TAG_WIDGET = "TweetTopics2Widget";
	
	public static final String HASHTAG_SHARE = "#TweetTopicsQR";
	public static final String HASHTAG_SHARE_THEME = "#TweetTopicsTheme";
    
	public static final int NUMBERS_ZEROS_IN_LONG = 24;
	
	public static final int TYPE_ANIM_TOP = 0;
	public static final int TYPE_ANIM_BOTTOM = 1;
	public static final int TYPE_ANIM_LEFT = 2;
	public static final int TYPE_ANIM_RIGHT = 3;
	
	public static final int TYPE_CIRCLE = 0;
	public static final int TYPE_RECTANGLE = 1;
	public static final int TYPE_BUBBLE = 2;

	public static final int TYPE_LINK_IMAGE = 0;
	public static final int TYPE_LINK_VIDEO = 1;
	public static final int TYPE_LINK_GENERAL = 2;
	public static final int TYPE_LINK_TWEETOPICS_QR = 3;
	public static final int TYPE_LINK_TWEETOPICS_THEME = 4;
	public static final int TYPE_LINK_TWEET = 5;
	
	public static final int NOERROR = 1;
	public static final int UNKNOWN_ERROR = 2;
	public static final int LIMIT_ERROR = 3;
	
	public static final int MAX_NOTIFICATIONS = 50;
	public static final int MAX_NOTIFICATIONS_LITE = 1;
	
	public static final int HEIGHT_SEARCH_ICON = 48;
	public static final int HEIGHT_SEARCH_ICON_SMALL = 30;
	public static final int HEIGHT_IMAGELINK = 60;
	public static final int HEIGHT_THUMB = 45;
	public static final int HEIGHT_THUMB_NEWSTATUS = 60;
	public static final int HEIGHT_IMAGE = 250;
	public static final int HEIGHT_VIDEO = 150;
	
	public static final int HEIGHT_PHOTO_SIZE_SMALL = 480;
	public static final int HEIGHT_PHOTO_SIZE_MIDDLE = 768;
    public static final int HEIGHT_PHOTO_SIZE_LARGE = 1600;
	
	public static final int MAX_ROW_BYSEARCH = 500;
    public static final int MAX_ROW_BYSEARCH_FORCE = 1200;
	
	static public int AVATAR_SMALL = 30;
	static public int AVATAR_MEDIUM = 36;
	static public int AVATAR_LARGE = 48;
	
	static public Context context = null;
	
	static public SharedPreferences preference = null;
	
	static public String appDirectory = Environment.getExternalStorageDirectory().getAbsolutePath()+"/tweettopics2/";
	static public String appIconsDirectory = Environment.getExternalStorageDirectory().getAbsolutePath()+"/tweettopics2/icons/";
	static public String appUploadImageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath()+"/tweettopics2/uploadimage/";
	static public String packageName = "com.javielinux.tweettopics2";
	static public String packageNamePRO = "com.javielinux.tweettopics.pro";
	
	//static public String filesDirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/tweettopics/avatars/";
	
	static public ArrayList<String> hideUser = new ArrayList<String>();
	static public ArrayList<String> hideWord = new ArrayList<String>();
	static public ArrayList<String> hideSource = new ArrayList<String>();
	
	static public String KEY_MAPS = "0B8Rsd6rLL0nlCx9VT1moiWzNGYAosIa0lMNfSg";
	
	static public int dip2px(Context cnt, float dip) {
		final float scale = cnt.getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		return (int) (dip * scale + 0.5f);
	}
	
	static public void fillHide() {
		hideWord.clear();
		hideUser.clear();
		hideSource.clear();
		ArrayList<Entity> words = DataFramework.getInstance().getEntityList("quiet");
		for (Entity word : words) {
			if (word.getInt("type_id")==1) { // palabra
				hideWord.add(word.getString("word").toLowerCase());
			}
			if (word.getInt("type_id")==2) { // usuario
				hideUser.add(word.getString("word").toLowerCase());
			}
			if (word.getInt("type_id")==3) { // fuente
				hideSource.add(word.getString("word").toLowerCase());
			}
		}
	}
	
	static public boolean isHideWordInText(String text) {
		for (String word : hideWord) {
			if (text.toLowerCase().contains(word.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	static public boolean isHideSourceInText(String text) {
		for (String word : hideSource) {
			if (text.toLowerCase().contains(word.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	static public SharedPreferences getPreference(Context cnt) {
		PreferenceManager.setDefaultValues(cnt, R.xml.preferences, false);
		return PreferenceManager.getDefaultSharedPreferences(cnt);
	}

    static public boolean isOnline(Context context) {
    	try {
    		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    		return cm.getActiveNetworkInfo().isConnected();
    	} catch (Exception e) {
    		return false;
    	}
	}
    

    
    public static int getLenghtTweet(String text, int shortURLLength, int shortURLLengthHttps) {
    	int length = text.length();
		ArrayList<String> links = Utils.pullLinksHTTP(text); 
		for (String link : links) {
			if (link.contains("http:") && link.length()>shortURLLength) {
				length -= link.length();
				length += shortURLLength;
			}
			if (link.contains("https:") && link.length()>shortURLLengthHttps) {
				length -= link.length();
				length += shortURLLengthHttps;
			}
		}	
		return length;
    }
    

    
    public static String toCapitalize(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
    
    public static ArrayList<String> getDivide140(String text, String reply) {
		ArrayList<String> ar = new ArrayList<String>();
		
		int max = 134;
		if (!reply.equals("")) {
			max = 134 - reply.length();
			if (text.startsWith(reply)) {
				text = text.substring(text.indexOf(" ")).trim();
			}
			reply += " ";
		}
		
		boolean todo = true;
		while (todo) {
			if (text.length()>max) {
				String t1 = text.substring(0, max-1);
				int pos = t1.lastIndexOf(" ");
				ar.add(reply + text.substring(0, pos));
				text = text.substring(pos).trim();
			} else {
				ar.add(reply + text);
				todo = false;
			}
		}
		
		ArrayList<String> out = new ArrayList<String>();
		int count = 1;
		for (String a : ar) {
			out.add(a + " (" + count + "/" + ar.size() + ")");
			//Log.d(Utils.TAG, a + " (" + count + "/" + ar.size() + ")");
			count++;
		}
		
		return out;
	}
    
    static public String getIconGeneric(Context cnt, String name) {
    	if (name.startsWith("#")) {
    		return "drawable/letter_hash";
    	}
    	if (name.startsWith("@")) {
    		return "drawable/letter_user";
    	}
    	String c = name.toLowerCase().substring(0,1);
    	Log.d(Utils.TAG, "es: " + c);
		int id = cnt.getResources().getIdentifier(Utils.packageName+":drawable/letter_"+c, null, null);
		if (id>0) {
			return "drawable/letter_"+c;
		} else {
			return "drawable/letter_az";
		}
    }
    
    static public String createIconForSearch(Bitmap bmp) {
    	int count = 1;
		String tokenFile = "search_1";
		String file = Utils.appDirectory + tokenFile+".png";
		File f = new File(file);
		while (f.exists()) {
			count++;
			tokenFile = "search_"+count;
			file = Utils.appDirectory + tokenFile+".png";
			f = new File(file);
		}
		
		Bitmap avatarBig = Bitmap.createScaledBitmap(bmp, Utils.HEIGHT_SEARCH_ICON, Utils.HEIGHT_SEARCH_ICON, true);
		Bitmap avatarSmall = Bitmap.createScaledBitmap(bmp, Utils.HEIGHT_SEARCH_ICON_SMALL, Utils.HEIGHT_SEARCH_ICON_SMALL, true);
		
		FileOutputStream out;
		try {
			out = new FileOutputStream(file);
			avatarBig.compress(Bitmap.CompressFormat.PNG, 90, out);
			
			String fileSmall = Utils.appDirectory + tokenFile+"_small.png";
			FileOutputStream outSmall = new FileOutputStream(fileSmall);
			avatarSmall.compress(Bitmap.CompressFormat.PNG, 90, outSmall);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return tokenFile;
    }
	
	static public String fillZeros(String number) {
		int fill = NUMBERS_ZEROS_IN_LONG - number.length();
		String out = "";
		for (int i=0; i<fill;i++) {
			out +="0";
		}
		return out + number;
	}
	
	static public String getFileAvatar(long id) {
		return Utils.appDirectory + "avatar_" + id + ".jpg";
	}
	
	static public Bitmap getBitmapAvatar(long id, int size) {
		File f = new File(getFileAvatar(id));
		if (f.exists()) {
			try {
				return Bitmap.createScaledBitmap(getBitmapFromFile(getFileAvatar(id), size, true), size, size, true);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	static public Bitmap toGrayscale(Bitmap bmpOriginal)
	{        
		try {
		    int width, height;
		    height = bmpOriginal.getHeight();
		    width = bmpOriginal.getWidth();    
	
		    Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		    Canvas c = new Canvas(bmpGrayscale);
		    Paint paint = new Paint();
		    ColorMatrix cm = new ColorMatrix();
		    cm.setSaturation(0);
		    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		    paint.setColorFilter(f);
		    c.drawBitmap(bmpOriginal, 0, 0, paint);
		    return bmpGrayscale;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return bmpOriginal;
		} catch (Exception e) {
			e.printStackTrace();
			return bmpOriginal;
		}
	}
	
	static public Drawable colorDrawable(Context cnt, int d, int color) {
        return colorDrawable(cnt.getResources().getDrawable(d), color);
	}
	
	static public Drawable colorDrawable(Drawable d, int color) {
		ColorFilter c = new LightingColorFilter(color, color);
        d.setColorFilter(c);
        return d;
	}
	
	static public Bitmap getBitmapNumber(Context cnt, int number, int color, int type) {
        return getBitmapNumber(cnt, number, color, type, 13);
	}

    static public Bitmap getBitmapNumber(Context cnt, int number, int color, int type, int textSize) {
        String text = number+"";
        if (number>999) {
            text = "999+";
        }
        return getBitmapInBubble(cnt, text, color, type, textSize);
    }
	
	static public Bitmap getBitmapInBubble(Context cnt, String text, int color, int type, int textSize) {
		
		try {
			textSize = Utils.dip2px(cnt, textSize);
			Paint paintFill = new Paint();
			paintFill.setAntiAlias(true);
			
			if (color == Color.GREEN) {
				paintFill.setShader(new LinearGradient(0,0,0,22,0xff94c147,0xff658729,Shader.TileMode.CLAMP));
			}
			
			if (color == Color.RED) {
				paintFill.setShader(new LinearGradient(0,0,0,22,0xffb72121,0xffe82f2f,Shader.TileMode.CLAMP));
			}
			
			Paint paintStroke = new Paint();
			paintStroke.setAntiAlias(true);
			paintStroke.setColor(Color.WHITE);	
			
			Paint paintText = new Paint();
			paintText.setAntiAlias(true);
			paintText.setTextSize(textSize);
			paintText.setFakeBoldText(true);
			paintText.setTextAlign(Align.CENTER);
			paintText.setColor(Color.WHITE);
			
			if (type==TYPE_CIRCLE) {
				float width = paintText.measureText(text);
				float height = paintText.descent() - paintText.ascent();
				
				int size = (int)((width>height)?width:height) + 7;
				int radius = (size-2)/2;
				int center = size/2;
				int ytext = center + (int)paintText.descent() + 2;
				
				Bitmap bmp = Bitmap.createBitmap(size, size, Config.ARGB_8888);
				Canvas c = new Canvas(bmp);
				
				c.drawCircle(center, center, radius, paintStroke);
				c.drawCircle(center, center, radius-1, paintFill);
				c.drawText(text, center, ytext, paintText);
				return bmp;
				
			} else {
				
				float width = paintText.measureText(text);
				float height = paintText.descent() - paintText.ascent();
						
			    int wBox = (int)width  + 10;
			    int hBox = (int)height + 4;
			    int hBoxFinal = hBox;
			    
				int center = wBox/2;
				int ytext = (hBox/2) + (int)paintText.descent() + 2;
			    
			    RectF boxRect = new RectF(1, 1, wBox-1, hBox-1);
				
			    Path pathFill = new Path();
			    pathFill.addRoundRect(boxRect, 7, 7, Direction.CCW);
			    if (type==TYPE_BUBBLE) {
			    	pathFill.moveTo(7, hBox-2);
			    	pathFill.lineTo(7, hBox+4);
			    	pathFill.lineTo(12, hBox-2);
			    	hBoxFinal = hBox + 6;
			    }
			    
			    RectF boxRectStroke = new RectF(0, 0, wBox, hBox);
			    
			    Path pathStroke = new Path();
			    pathStroke.addRoundRect(boxRectStroke, 7, 7, Direction.CCW);
			    if (type==TYPE_BUBBLE) {
			    	pathStroke.moveTo(5, hBox-2);
			    	pathStroke.lineTo(5, hBox+6);
			    	pathStroke.lineTo(14, hBox);
			    }
				
				Bitmap bmp = Bitmap.createBitmap(wBox, hBoxFinal, Config.ARGB_4444);
				Canvas c = new Canvas(bmp);
				
				c.drawPath(pathStroke, paintStroke);
				c.drawPath(pathFill, paintFill);
				
				c.drawText(text, center, ytext, paintText);
				return bmp;
			}
		} catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
		}
        return null;
	}
	
	static public void setActivity(Context cnt)
    {
		context = cnt;
        try {
                File dir = new File(appDirectory);
                if (!dir.exists()) dir.mkdir();
                
                File fileNomedia = new File(appDirectory+".nomedia");
                if (!fileNomedia.exists()) fileNomedia.createNewFile();
                
                File dirIcons = new File(appIconsDirectory);
                if (!dirIcons.exists()) dirIcons.mkdir();
                
                File fileNomediaIcons = new File(appIconsDirectory+".nomedia");
                if (!fileNomediaIcons.exists()) fileNomediaIcons.createNewFile();
                
                File dirUploadImageDirectory = new File(appUploadImageDirectory);
                if (!dirUploadImageDirectory.exists()) dirUploadImageDirectory.mkdir();
                
                File fileUploadImageDirectory = new File(appUploadImageDirectory+".nomedia");
                if (!fileUploadImageDirectory.exists()) fileUploadImageDirectory.createNewFile();
                /*
                File dirAvatars = new File(filesDirPath);
                if (!dirAvatars.exists()) dirAvatars.mkdir();
                
                File avatarsNomedia = new File(filesDirPath+".nomedia");
                if (!avatarsNomedia.exists()) avatarsNomedia.createNewFile();
                deleteAvatars(cnt);
                */
                	//filesDirPath = cnt.getCacheDir().getPath();


                
        } catch (Exception ioe) {
                ioe.printStackTrace();
        }
        
        PreferenceManager.setDefaultValues(cnt, R.xml.preferences, false);
		preference = PreferenceManager.getDefaultSharedPreferences(cnt);

    }
	/*
	static public void deleteAvatars(Context cnt) {
		boolean todo = false;
		long time = getDateDeleteAvatars(cnt);
		if (time<0) {
			setDateDeleteAvatars(cnt, new Date().getTime());
		} else {
			Date dateToday = new Date();
			Date dateRefresh = new Date(time);
			if (dateToday.after(dateRefresh)) {
				todo = true;
				Calendar calToday = Calendar.getInstance();
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m");
				String tomorrow = Utils.getTomorrow(calToday.get(Calendar.YEAR), (calToday.get(Calendar.MONTH)+1), calToday.get(Calendar.DATE));
				try {
					Date nextDate = format.parse(tomorrow + " 10:00");
					setDateDeleteAvatars(cnt, nextDate.getTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
			}
		}
		
		if (todo) {

            File dir = new File(filesDirPath);
            File[] files = dir.listFiles();
			int max = 300;
			if (files.length>max) {
				Log.d(TAG, "Borramos " + (files.length-max) + " avatars");
				Arrays.sort(files, new Comparator<File>(){
					public int compare(File f1, File f2) {
						return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
					} 
				});
				for (int i=0; i<files.length-max; i++) {
					files[i].delete();
				}
			}
	        
		}
	}
	    */
	static public void saveApiConfiguration(Context cnt) {
		boolean todo = false;
		long time = PreferenceUtils.getDateApiConfiguration(cnt);
		if (time<0) {
            PreferenceUtils.setDateApiConfiguration(cnt, new Date().getTime());
			todo = true;
		} else {
			Date dateToday = new Date();
			Date dateRefresh = new Date(time);
			if (dateToday.after(dateRefresh)) {
				todo = true;
				Calendar calToday = Calendar.getInstance();
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m");
				String tomorrow = Utils.getTomorrow(calToday.get(Calendar.YEAR), (calToday.get(Calendar.MONTH)+1), calToday.get(Calendar.DATE));
				try {
					Date nextDate = format.parse(tomorrow + " 10:00");
                    PreferenceUtils.setDateApiConfiguration(cnt, nextDate.getTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
			}
		}
		
		if (todo) {

			Log.d(Utils.TAG, "Cargar configuración");
			
	        ConnectionManager2.getInstance().open(cnt);
	        
	        try {
	        	TwitterAPIConfiguration api = ConnectionManager2.getInstance().getAnonymousTwitter().getAPIConfiguration();
                PreferenceUtils.setShortURLLength(cnt, api.getShortURLLength());
                PreferenceUtils.setShortURLLengthHttps(cnt, api.getShortURLLengthHttps());
			} catch (TwitterException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
	        
		}
	}
	
	static public String now() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currentTime = new Date();
		return formatter.format(currentTime);
	}
	
    static public void showMessage(Context context, String msg) {
	    Toast.makeText(context, 
	    		msg, 
	            Toast.LENGTH_LONG).show();
    }

    static public void showMessage(Context context, int resmsg) {
	    Toast.makeText(context, 
	    		context.getString(resmsg), 
	            Toast.LENGTH_LONG).show();
    }
    
    static public void showShortMessage(Context context, String msg) {
	    Toast.makeText(context, 
	    		msg, 
	            Toast.LENGTH_SHORT).show();
    }
    
    static public void showShortMessage(Context context, int resmsg) {
	    Toast.makeText(context, 
	    		context.getString(resmsg), 
	            Toast.LENGTH_SHORT).show();
    }
    
    static public String seconds2Time(int seconds) {
    	return seconds2Time(seconds, true);
    }
    
    static public String seconds2Time(int seconds, boolean hasHours) {
    	int h = 0, m = 0, s = seconds;
    	if (s>60) {
    		m = s/60;
    		s = s%60;
    		if (m>60) {
    			h = m/60;
    		}
    	}
    	if (s==60) {
    		s = 0;
    		m++;
    	}
    	if (m==60) {
    		m = 0;
    		h++;
    	}
    	if (m==61) {
    		m = 1;
    		h++;
    	}
    	if (!hasHours) {
    		m += h*60;
    	}
    	String hs = ""+h, ms = ""+m, ss = ""+s;
    	if (h<10) {
    		hs = "0"+hs;
    	}
    	if (m<10) {
    		ms = "0"+ms;
    	}
    	if (s<10) {
    		ss = "0"+ss;
    	}
    	if (hasHours) {
    		return hs+":"+ms+":"+ss;
    	} else {
    		return ms+":"+ss;
    	}
    }
    
    static public String getTextURLs(twitter4j.Status st) {
		String out = "";
		URLEntity[] urls = st.getURLEntities();
				
		if (urls!=null && urls.length>0) {
            for (URLEntity url : urls) {
                if (url.getDisplayURL()!=null && !url.getDisplayURL().equals("")) {
                    out += url.getURL().toString() + SEP_VALUES + url.getDisplayURL()
                            + SEP_VALUES + url.getExpandedURL().toString() + SEP_BLOCK;
                }
            }
        }

        MediaEntity[] medias = st.getMediaEntities();

        if (medias!=null && medias.length>0) {
            for (MediaEntity media : medias) {
                if (media.getDisplayURL()!=null && !media.getDisplayURL().equals("")) {
                    out += media.getURL().toString() + SEP_VALUES + media.getDisplayURL()
                            + SEP_VALUES + media.getExpandedURL().toString()
                            + SEP_VALUES + media.getMediaURL().toString() + ":thumb"
                            + SEP_VALUES + media.getMediaURL().toString() + ":medium"
                            + SEP_BLOCK;
                }
            }
        }

		return out;
	}
    /*
    static public class InfoURLEntity {
    	String url;
    	String urlDisplay;
    	String urlExpanded;
    }
*/
    
    static public class URLContent {
    	public String normal;
    	public String display;
    	public String expanded;
        public String linkMediaThumb = null;
        public String linkMediaLarge = null;
    }
    
    static public URLContent searchContent(ArrayList<URLContent> urls, String url) {
    	for (URLContent u : urls) {
    		if (u.normal.equals(url) || u.display.equals(url) || u.expanded.equals(url)) {
    			return u;
    		}
    	}
    	return null;
    }
    
    static public ArrayList<URLContent> urls2content(String urls) {
    	/*Log.d(Utils.TAG, "urls: " + urls);
    	urls = urls.replace("--", SEP_BLOCK);
    	urls = urls.replace(";;", SEP_VALUES);
    	Log.d(Utils.TAG, "urls: " + urls);*/
    	ArrayList<URLContent> out = new ArrayList<URLContent>();
    	
    	StringTokenizer tokens = new StringTokenizer(urls, SEP_BLOCK);  
    	
    	while(tokens.hasMoreTokens()) {  
    		try {
    			String token = tokens.nextToken();
    			StringTokenizer hash = new StringTokenizer(token, SEP_VALUES);
    			if (hash.countTokens()==3 || hash.countTokens()==5) {
    				URLContent u = new URLContent();
					u.normal = hash.nextToken();
					u.display =	hash.nextToken();
					u.expanded = hash.nextToken();
                    if (hash.hasMoreTokens()) u.linkMediaThumb = hash.nextToken();
                    if (hash.hasMoreTokens()) u.linkMediaLarge = hash.nextToken();
                    if (u.linkMediaThumb!=null && !u.linkMediaThumb.equals("")) {
                        CacheData.putURLMedia(u.expanded, u);
                    }
					out.add(u);
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	
    	return out;
    }
    
    static public String[] toHTMLTyped(Context cnt, String text, String urls) {
    	return toHTMLTyped(cnt, text, urls, "");
    }
    
    static public String[] toHTMLTyped(Context cnt, String text, String urls, String underline) {
    	
    	// 1 normal
    	// 2 display
    	// 3 Expanded
    	int type = Integer.parseInt(Utils.getPreference(cnt).getString("prf_show_links", "2"));
    	
    	String[] out = new String[2];
    	
    	if (urls==null || urls.equals("") || type == 1) {
    		out[0] = text;
    		out[1] = Utils.toHTML(cnt, text);
    		return out;	
    	}
    	
    	//ArrayList<InfoURLEntity> ents = new ArrayList<InfoURLEntity>(); 
    	
    	String normalText = text;
    	String htmlText = Utils.toHTML(cnt, text, underline);
    	
    	StringTokenizer tokens = new StringTokenizer(urls, SEP_BLOCK);  
    	
    	while(tokens.hasMoreTokens()) {  
    		try {
    			String token = tokens.nextToken();
    			StringTokenizer hash = new StringTokenizer(token, SEP_VALUES);
    			if (hash.countTokens()==3 || hash.countTokens()==5) {
    				//InfoURLEntity e = new InfoURLEntity();
					String url = hash.nextToken();
					String urlDisplay = hash.nextToken();
					String urlExpanded = hash.nextToken();
					if (type==2) {
						normalText = normalText.replace(url, urlDisplay);
		    			htmlText = htmlText.replace(url, urlDisplay);
		    		} else {
		    			normalText = normalText.replace(url, urlExpanded);
		    			htmlText = htmlText.replace(url, urlExpanded);
		    		}
					//ents.add(e);
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	
    	out[0] = normalText;
		out[1] = htmlText;
    	
    	
    	return out;
	}
    
    static public String toHTML(Context context, String text) {
    	return toHTML(context, text, "");
    }
    
    static public String toHTML(Context context, String text, String underline) {
    	String out = text.replace("<", "&lt;");
    	//out = out.replace(">", "&gt;");
    	ArrayList<Integer> valStart = new ArrayList<Integer>();
    	ArrayList<Integer> valEnd = new ArrayList<Integer>();
    	
    	Comparator<Integer> comparator = Collections.reverseOrder();
    	
    	// enlaces
    	
    	String regex = "\\(?\\b(http://|https://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
    	Pattern p = Pattern.compile(regex);
    	Matcher m = p.matcher(out);
    	while(m.find()) {
    		valStart.add(m.start());
    		valEnd.add(m.end());
    	}
    	
    	// hashtag
    	
    	regex = "(#[\\w-]+)";
    	p = Pattern.compile(regex);
    	m = p.matcher(out);
    	while(m.find()) {
    		valStart.add(m.start());
    		valEnd.add(m.end());
    	}
    	
    	// usuarios twitter
    	
    	regex = "(@[\\w-]+)";
    	p = Pattern.compile(regex);
    	m = p.matcher(out);
    	while(m.find()) {
    		valStart.add(m.start());
    		valEnd.add(m.end());
    	}
    	
    	ThemeManager theme = new ThemeManager(context);
    	
    	String tweet_color_link = theme.getStringColor("tweet_color_link");
    	String tweet_color_hashtag = theme.getStringColor("tweet_color_hashtag");
    	String tweet_color_user = theme.getStringColor("tweet_color_user");
    	
    	Collections.sort(valStart,comparator);
    	Collections.sort(valEnd,comparator);
    	    	
    	for (int i=0; i<valStart.size(); i++) {
    		int s = valStart.get(i);
    		int e = valEnd.get(i);
    		String link = out.substring(s, e);
    		if (link.equals(underline)) {
    			link = "<u>"+out.substring(s, e)+"</u>";
    		}
    		if (out.substring(s, s+1).equals("#")) {
    			out = out.substring(0, s) + "<font color=\"#"+tweet_color_hashtag+"\">" + link + "</font>" + out.substring(e, out.length());
    		} else if (out.substring(s, s+1).equals("@")) {
    			out = out.substring(0, s) + "<font color=\"#"+tweet_color_user+"\">" + link + "</font>" + out.substring(e, out.length());
    		} else {
    			out = out.substring(0, s) + "<font color=\"#"+tweet_color_link+"\">" + link + "</font>" + out.substring(e, out.length());
    		}
    	}
    	
    	return out;
    }
    
    static public String toExportHTML(Context context, String text) {
    	String out = text.replace("<", "&lt;");
    	//out = out.replace(">", "&gt;");
    	ArrayList<Integer> valStart = new ArrayList<Integer>();
    	ArrayList<Integer> valEnd = new ArrayList<Integer>();
    	
    	Comparator<Integer> comparator = Collections.reverseOrder();
    	
    	// enlaces
    	
    	String regex = "\\(?\\b(http://|https://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
    	Pattern p = Pattern.compile(regex);
    	Matcher m = p.matcher(out);
    	while(m.find()) {
    		valStart.add(m.start());
    		valEnd.add(m.end());
    	}
    	
    	// hashtag
    	
    	regex = "(#[\\w-]+)";
    	p = Pattern.compile(regex);
    	m = p.matcher(out);
    	while(m.find()) {
    		valStart.add(m.start());
    		valEnd.add(m.end());
    	}
    	
    	// usuarios twitter
    	
    	regex = "(@[\\w-]+)";
    	p = Pattern.compile(regex);
    	m = p.matcher(out);
    	while(m.find()) {
    		valStart.add(m.start());
    		valEnd.add(m.end());
    	}
    	
    	Collections.sort(valStart,comparator);
    	Collections.sort(valEnd,comparator);
    	    	
    	for (int i=0; i<valStart.size(); i++) {
    		int s = valStart.get(i);
    		int e = valEnd.get(i);
    		String link = out.substring(s, e);

    		if (out.substring(s, s+1).equals("#")) {
    			out = out.substring(0, s) + "<a href=\"http://twitter.com/#!/search/"+link+"\" class=\"hashtag\">" + link + "</a>" + out.substring(e, out.length());
    		} else if (out.substring(s, s+1).equals("@")) {
    			out = out.substring(0, s) + "<a href=\"http://twitter.com/#!/"+link+"\" class=\"user\">" + link + "</a>" + out.substring(e, out.length());
    		} else {
    			out = out.substring(0, s) + "<a href=\""+link+"\" class=\"link\">" + link + "</a>" + out.substring(e, out.length());
    		}
    	}
    	
    	return out;
    }
    
    static public ArrayList<String> pullLinksHTTP(String text) {
    	return pullLinksHTTP(text, null);
    }
    
    static public ArrayList<String> pullLinksHTTP(String text, ArrayList<URLContent> urls) {
    	ArrayList<String> links = new ArrayList<String>();
    	
    	// enlaces
    	
    	String regex = "\\(?\\b(http://|https://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
    	Pattern p = Pattern.compile(regex);
    	Matcher m = p.matcher(text);
    	while(m.find()) {
    		String urlStr = m.group();
    		if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
    			urlStr = urlStr.substring(1, urlStr.length() - 1);
    		}
    		if (urls==null) {
    			links.add(urlStr);
    		} else {
    			URLContent u = searchContent(urls, urlStr);
    			if (u!=null) {
    				links.add(u.expanded);
    			} else {
    				links.add(urlStr);
    			}
    		}
    	}
    	
    	return links;
    }
    
    static public ArrayList<String> pullLinksUsers(String text) {
    	ArrayList<String> links = new ArrayList<String>();

    	// usuarios twitter
    	
    	String regex = "(@[\\w-]+)";
    	Pattern p = Pattern.compile(regex);
    	Matcher m = p.matcher(text);
    	while(m.find()) {
    		String urlStr = m.group();
    		if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
    			urlStr = urlStr.substring(1, urlStr.length() - 1);
    		}
    		links.add(urlStr);
    	}
    	
    	return links;
    }

    static public ArrayList<String> pullLinksHashTags(String text) {
    	ArrayList<String> links = new ArrayList<String>();

    	// hashtags

    	String regex = "(#[\\w-]+)";
    	Pattern p = Pattern.compile(regex);
    	Matcher m = p.matcher(text);
    	while(m.find()) {
    		String urlStr = m.group();
    		if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
    			urlStr = urlStr.substring(1, urlStr.length() - 1);
    		}
    		links.add(urlStr);
    	}

    	return links;
    }
    
    static public ArrayList<String> pullLinks(String text) {
    	return pullLinks(text, null);
    }
    
    static public ArrayList<String> pullLinks(String text, ArrayList<URLContent> urls) {
    	ArrayList<String> links = pullLinksHTTP(text, urls);//= new ArrayList<String>();

    	// enlaces
    	/*
    	String regex = "\\(?\\b(http://|https://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
    	Pattern p = Pattern.compile(regex);
    	Matcher m = p.matcher(text);
    	while(m.find()) {
    		String urlStr = m.group();
    		if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
    			urlStr = urlStr.substring(1, urlStr.length() - 1);
    		}
    		links.add(urlStr);
    	}
    	*/
    	// hashtag
    	
    	String regex = "(#[\\w-]+)";
    	Pattern p = Pattern.compile(regex);
    	Matcher m = p.matcher(text);
    	while(m.find()) {
    		String urlStr = m.group();
    		if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
    			urlStr = urlStr.substring(1, urlStr.length() - 1);
    		}
    		links.add(urlStr);
    	}
    	
    	// usuarios twitter
    	
    	regex = "(@[\\w-]+)";
    	p = Pattern.compile(regex);
    	m = p.matcher(text);
    	while(m.find()) {
    		String urlStr = m.group();
    		if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
    			urlStr = urlStr.substring(1, urlStr.length() - 1);
    		}
    		links.add(urlStr);
    	}
    	
    	return links;
    }
    
    static public String getQuotedText (String text, String operator, boolean startWithOperator) {
    	String query = "";
    	if (startWithOperator) query+=" " + operator;
		Matcher matcher = Pattern.compile("\\s*\"([^\"]+)\"").matcher(text);
		boolean sw = true;
		while (matcher.find()) {
			String f = matcher.group().trim();
			if (sw) {
				sw = false;
				if (!startWithOperator) query += " ";
			} else {
				query += " "+operator;	
			}
			query += f;
			text = text.replace(f, "");
		}
		text = text.trim().replace("  ", " ");
		text = text.replace("  ", " ");
		
		if (!text.equals("")) { // si queda texto sin estar entre comillas lo ponemos
			if (sw) { // no tiene textos entre comillas
				if (!startWithOperator) query += " ";
			} else { // si tiene textos entre comillas
				query += " "+operator;
			}
			query += text.replace(" ", " "+operator);
		}
		
		return query;
    }
    
    static public CharSequence[] splitLinks(String text) {
    	ArrayList<String> links = pullLinks(text);
    	CharSequence[] out = new CharSequence[links.size()];
    	for (int i=0; i<links.size(); i++) {
    		out[i] = links.get(i);
    	}
    	return out;
    	
    }
    
    static public String shortURL(String link) {
    	
    	link = largeLink(link);
    	
    	int s = (Integer.parseInt(Utils.preference.getString("prf_service_shorter", "1")));
    	if ( s == 1 ) { // bit.ly
    		String user = "tweettopics";
    		String key = "R_ba0652e93e7c9c527c016447d2e29091";
    		if (!PreferenceUtils.getUsernameBitly(context).equals("") && !PreferenceUtils.getKeyBitly(context).equals("")) {
    			user = PreferenceUtils.getUsernameBitly(context);
        		key = PreferenceUtils.getKeyBitly(context);
    		}
    		String url = "http://api.bit.ly/v3/shorten?login="+user+"&apiKey="+key+"&format=json&longUrl=" + URLEncoder.encode(link);
    		
    		HttpGet request = new HttpGet(url);
    		HttpClient client = new DefaultHttpClient();
    		HttpResponse httpResponse;
    		try {
    			httpResponse = client.execute(request);
    			String xml = EntityUtils.toString(httpResponse.getEntity());
    			JSONObject jsonObject = new JSONObject(xml);
    			if (jsonObject.getString("status_txt").equals("OK")) {
    				return jsonObject.getJSONObject("data").getString("url");
    			}				    
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		
    	} else { // karmacracy
    		// http://kcy.me/api/?u=javielinux&key=nyk1tjr20x&format=json&url=http://www.javielinux.com
    		String user = PreferenceUtils.getUsernameKarmacracy(context);
    		String key = PreferenceUtils.getKeyKarmacracy(context);
    		
    		String url = "http://kcy.me/api/?u="+user+"&key="+key+"&format=json&url=" + URLEncoder.encode(link);
    		
    		HttpGet request = new HttpGet(url);
    		HttpClient client = new DefaultHttpClient();
    		HttpResponse httpResponse;
    		try {
    			httpResponse = client.execute(request);
    			String xml = EntityUtils.toString(httpResponse.getEntity());
    			JSONObject jsonObject = new JSONObject(xml);
    			if (jsonObject.getString("status_txt").equals("OK")) {
    				return jsonObject.getJSONObject("data").getString("url");
    			}				    
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		
    	}
    	

		return null;
    }
    
    static public String shortLinks(String text, ArrayList<String> noImages) {
    	ArrayList<String> links = pullLinksHTTP(text);
    	String out = text;
    	for (int i=0; i<links.size(); i++) {
    		if ( (!links.get(i).contains("bit.ly")) && (!links.get(i).contains("goo.gl"))
    				&& (!links.get(i).contains("twitpic.com")) && (!links.get(i).contains("yfrog.com"))
    				&& (!links.get(i).contains("lockerz.com")) && (!links.get(i).contains("kcy.me"))
    				&& (!links.get(i).contains("t.co")) && (!links.get(i).contains("tinyurl")) ) {
    			String link = links.get(i);
    			String newUrl = shortURL(links.get(i));
    			if (newUrl!=null) {
    				if (!newUrl.equals("")) out = out.replace(link, newUrl);
    			}
    		}
    	}
    	
    	return out;
    	
    }
    
    static public String largeLink(String link) {
    	/*
		if ( (link.contains("bit.ly")) || (link.contains("short.ie")) || (link.contains("tinyurl.com"))
				|| (link.contains("ow.ly")) || (link.contains("ff.im")) || (link.contains("post.ly"))
				|| (link.contains("j.mp")) || (link.contains("t.co")) ) {
			
			String url = "http://www.longurlplease.com/api/v1.1?q=" + link;
			
			HttpGet request = new HttpGet(url);
			HttpClient client = new DefaultHttpClient();
			HttpResponse httpResponse;
			try {
				httpResponse = client.execute(request);
				String xml = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(xml);
			    String t = jsonObject.getString(link);
			    if ( (t!=null) && t!="") link = t;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
    	
    	boolean done = false;
    	
		if (link.contains("goo.gl")) {
			try {
				String url = "https://www.googleapis.com/urlshortener/v1/url?shortUrl=" + link;
				
				HttpGet request = new HttpGet(url);
				HttpClient client = new DefaultHttpClient();
				HttpResponse httpResponse;
				
				httpResponse = client.execute(request);
				String xml = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(xml);
			    String t = jsonObject.getString("longUrl");
			    if ( (t!=null) && t!="") {
			    	link = t;
			    	done = true;
			    }
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		if (link.contains("kcy.me")) {
			//http://karmacracy.com/api/v1/kcy/2e10?appkey=tweet!t0pic
			try {
				String id = link.substring(link.lastIndexOf("/")+1);
				String url = "http://karmacracy.com/api/v1/kcy/"+id+"?appkey=tweet!t0pic";
				
				HttpGet request = new HttpGet(url);
				HttpClient client = new DefaultHttpClient();
				HttpResponse httpResponse;
				
				httpResponse = client.execute(request);
				String xml = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(xml);
			    String t = jsonObject.getJSONObject("data").getJSONObject("kcy").getString("url");
			    //Log.d(Utils.TAG, "URL: " +t);
			    if ( (t!=null) && t!="") {
			    	link = t;
			    	done = true;
			    }
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (!done) {
    	
			try {
				String url = "http://www.longurlplease.com/api/v1.1?q=" + link;
				HttpGet request = new HttpGet(url);
				HttpClient client = new DefaultHttpClient();
				HttpResponse httpResponse;
				httpResponse = client.execute(request);
				
				String xml = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(xml);
			    String t = jsonObject.getString(link);
			    if ( (t!=null) && t!="" && !t.equals("null")) link = t;
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return link;
    }
    
    static public boolean hasLinksTweetForUser(String text) {
    	int p = (Integer.parseInt(Utils.preference.getString("prf_links", "3")));
    	if ( p == 1 ) {
    		return false;
    	} else if ( p == 2 ) {
    		return hasImagesTweet(text);
    	} else if ( p == 3 ) {
    		return hasLinksTweet(text);
    	}
    	return true;
    }
    
    static public boolean hasLinksTweet(String text) {
    	ArrayList<String> links = pullLinks(text);
    	for (int i=0; i<links.size(); i++) {
    		String link = links.get(i);
    		if ( (!link.startsWith("#")) && (!link.startsWith("@")) ) {
    			return true;
    		}
    	}
    	return false;
    }

    static public boolean isLinkImage(String link) {
        // mytubo.net
        if (link.contains("mytubo.net")) {
            return true;
        }

        // imgur.com
        if (link.contains("imgur.com")) {
            return true;
        }

        // instagr.am
        if (link.contains("instagr.am")) {
            return true;
        }

        // lightbox
        if (link.contains("lightbox")) {
            return true;
        }

        // vvcap
        if (link.contains("vvcap")) {
            return true;
        }

        // twitpic
        if (link.contains("twitpic")) {
            return true;
        }

        // picplz.com
        if (link.contains("picplz")) {
            return true;
        }

        // plixi
        if (link.contains("plixi")) {
            return true;
        }

        // yfrog

        if (link.contains("yfrog")) {
            return true;
        }

        // vimeo
        if (link.contains("vimeo")) {
            return true;
        }

        // twitgoo
        if (link.contains("twitgoo")) {
            return true;
        }

        // twitvid

        if (link.contains("twitvid")) {
            return true;
        }

        // youtube

        if (link.contains("youtube")) {
            return true;
        }

        if (link.contains("youtu.be")) {
            return true;
        }

        return false;
    }

    
    static public boolean hasImagesTweet(String text) {
    	ArrayList<String> links = pullLinks(text);
    	for (int i=0; i<links.size(); i++) {
    		String link = links.get(i);
    		if ( (!link.startsWith("#")) && (!link.startsWith("@")) ) {
                if (isLinkImage(link)) return true;
    		}
    		
    	}
    	return false;
    }
    
    static public String getTwitLoger(twitter4j.Status st) {
    	String out = "";
    	String link = "";
    	if (st.getText().contains("(cont) http://t.co/")) {
    		URLEntity[] urls = st.getURLEntities();
    		if (urls==null || urls.length<=0) return out;
    		for (URLEntity url : urls) {
    			if (url.getDisplayURL()!=null) {
    				if (url.getDisplayURL().contains("tl.gd")) {
    					link = url.getDisplayURL();
    				}
    			}
        	}
	    	if (!link.equals("")) {
	    		
    			String id = link.substring(link.lastIndexOf("/")+1);
    			String strURL = "http://www.twitlonger.com/api_read/" + id;
    			Document doc = null;
    			try {
    				URL url;
    				URLConnection urlConn = null;
    				url = new URL(strURL);
    				urlConn = url.openConnection();
    				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    				DocumentBuilder db = dbf.newDocumentBuilder();
    				doc = db.parse(urlConn.getInputStream());
    			} catch (IOException ioe) {
    			} catch (ParserConfigurationException pce) {
    			} catch (SAXException se) {
    			}
				if (doc!=null) {
					try {
						//String content = doc.getElementsByTagName("content").item(0).getChildNodes().getLength()+"";//.getFirstChild().getNodeValue();
						
						String content = "";
						NodeList nodes = doc.getElementsByTagName("content").item(0).getChildNodes();
						for (int i=0; i<nodes.getLength(); i++) {
							content += nodes.item(i).getNodeValue();
						}
				        if (!content.equals("")) {
				        	return content;
				        }
					} catch (Exception e) {
					}
				}
	    		
	    	}
    	}
    	return out;
    }
    
    static public ArrayList<InfoLink> getThumbsTweet(String text) {
    	ArrayList<String> links = pullLinks(text);
    	ArrayList<InfoLink> images = new ArrayList<InfoLink>();
    	for (int i=0; i<links.size(); i++) {
    		String link = links.get(i);
    		if ( (!link.startsWith("#")) && (!link.startsWith("@")) ) {
    			images.add(getThumbTweet(link));
    		}
    	}
    	return images;
    }
    
	private static final char[] BASE58_CHARS = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
			.toCharArray();

	public static String numberToAlpha(long number) {
		char[] buffer = new char[20];
		int index = 0;
		do {
			buffer[index++] = BASE58_CHARS[(int) (number % BASE58_CHARS.length)];
			number = number / BASE58_CHARS.length;
		} while (number > 0);
		return new String(buffer, 0, index);
	}

	public static long alphaToNumber(String text) {
		char[] chars = text.toCharArray();
		long result = 0;
		long multiplier = 1;
		for (int index = 0; index < chars.length; index++) {
			char c = chars[index];
			int digit;
			if (c >= '1' && c <= '9') {
				digit = c - '1';
			} else if (c >= 'A' && c < 'I') {
				digit = (c - 'A') + 9;
			} else if (c > 'I' && c < 'O') {
				digit = (c - 'J') + 17;
			} else if (c > 'O' && c <= 'Z') {
				digit = (c - 'P') + 22;
			} else if (c >= 'a' && c < 'l') {
				digit = (c - 'a') + 33;
			} else if (c > 'l' && c <= 'z') {
				digit = (c - 'l') + 43;
			} else {
				throw new IllegalArgumentException("Illegal character found: '"
						+ c + "'");
			}

			result += digit * multiplier;
			multiplier = multiplier * BASE58_CHARS.length;
		}
		return result;
	}
    
    static public int base58_decode( String snipcode )
    {
        String alphabet = "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
        int num = snipcode.length();
        int decoded = 0 ;
        int multi = 1 ;
        for ( int i = (num-1) ; i >= 0 ; i-- )
        {
            decoded = decoded + multi * alphabet.indexOf( snipcode.substring(i, i+1) ) ;
            multi = multi * alphabet.length();
        }
        return decoded;
    }
    
    static public InfoLink getThumbTweet(String link) {
    			
    	String originalLink = link;
		
		// acortadores
		
    	link = largeLink(link);

        // si es un url media
        if (CacheData.existURLMedia(link)) {
            URLContent content = CacheData.getURLMedia(link);
            String imgThumb = content.linkMediaThumb;
            String imgLarge = content.linkMediaLarge;
            Bitmap bmp = getBitmap(imgThumb, HEIGHT_THUMB);
            if (bmp!=null) {
                InfoLink il = new InfoLink();
                il.setBitmapThumb(bmp);
                il.setService("Twitter Pic");
                il.setType(0);
                il.setLink(link);
                il.setOriginalLink(originalLink);
                il.setLinkImageThumb(imgThumb);
                il.setLinkImageLarge(imgLarge);
                return il;
            }
        }

		// es una busqueda
		if (link.startsWith(Utils.URL_QR)) {
			InfoLink il = new InfoLink();
			il.setService("tweettopics-qr");
			il.setType(TYPE_LINK_TWEETOPICS_QR);
			il.setLink(link);
			il.setOriginalLink(originalLink);
			return il;
		}
		
		// es un tema
		
		if (link.startsWith(Utils.URL_SHARE_THEME_QR)) {
			InfoLink il = new InfoLink();
			il.setService("tweettopics-theme");
			il.setType(TYPE_LINK_TWEETOPICS_THEME);
			il.setLink(link);
			il.setOriginalLink(originalLink);
			return il;
		}
		/*
		if (link.startsWith(InfoTweet.START_URL_TWITTER) && link.contains(InfoTweet.PREFIX_URL_TWITTER)) {
			InfoLink il = new InfoLink();
			il.setService("tweet_twitter");
			il.setType(TYPE_LINK_TWEET);
			il.setLink(link);
			il.setOriginalLink(originalLink);
			try {
				String c = link.replace(InfoTweet.START_URL_TWITTER, "");
				String user = c.substring(0, c.indexOf("/"));
				il.setTweetUser(user);
				String id = c.replace(InfoTweet.PREFIX_URL_TWITTER, "");
				id = id.replace(user, "");
				il.setTweetId(Long.parseLong(id));
				return il;
			} catch (ClassCastException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		*/
		if ( (link.endsWith(".jpg")) || (link.endsWith(".png"))	|| (link.endsWith(".gif")) || (link.endsWith(".bmp")) ) {
			String imgThumb = link;
			String imgLarge = link;
			Bitmap bmp = getBitmap(imgThumb, HEIGHT_IMAGE);
			if (bmp!=null) {
				InfoLink il = new InfoLink();
				il.setBitmapLarge(bmp);
				Bitmap bmpthumb = getBitmap(bmp, Utils.HEIGHT_THUMB);
				il.setBitmapThumb(bmpthumb);
				il.setExtensiveInfo(true);
				il.setService("Web");
				il.setType(0);
				il.setLink(link);
				il.setOriginalLink(originalLink);
				il.setLinkImageThumb(imgThumb);
				il.setLinkImageLarge(imgLarge);
				return il;
			}
    	}
		/*
		if (link.contains("flic.kr")) {
			String idbase58 = link.substring(link.lastIndexOf("/")+1);
			String id = String.valueOf(alphaToNumber(idbase58));
			
			String urlApi = "http://api.flickr.com/services/rest/?method=flickr.photos.getInfo&api_key=6ce2af123df7dd2a7dab086f086e9824&photo_id="+id+"&format=json&nojsoncallback=1";
			
			Log.d(Utils.TAG, "urlApi: (" + link + ") " + urlApi);
			
			String farmId="";
			String serverId="";
			String secret="";
			
			HttpGet request = new HttpGet(urlApi);
			HttpClient client = new DefaultHttpClient();
			HttpResponse httpResponse;
			try {
				httpResponse = client.execute(request);
				String xml = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(xml);
				if (jsonObject!=null) {
					if (jsonObject.getJSONObject("photo")!=null) {
						farmId = jsonObject.getJSONObject("photo").getString("farm");
						serverId = jsonObject.getJSONObject("photo").getString("server");
						secret = jsonObject.getJSONObject("photo").getString("secret");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (farmId!="") {
				String imgThumb = "http://farm"+farmId+".static.flickr.com/"+serverId+"/"+id+"_"+secret+"_s.jpg";
				String imgLarge = "http://farm"+farmId+".static.flickr.com/"+serverId+"/"+id+"_"+secret+".jpg";
				Bitmap bmp = getBitmap(imgThumb, HEIGHT_THUMB);
				if (bmp!=null) {
					InfoLink il = new InfoLink();
					il.setBitmapThumb(bmp);
					il.setService("Flickr");
					il.setType(0);
					il.setLink(link);
					il.setOriginalLink(originalLink);
					il.setLinkImageThumb(imgThumb);
					il.setLinkImageLarge(imgLarge);
					return il;
				}
			}
			
		}
		*/
		if (link.contains("mytubo.net")) {
			String image = "";
			
			try {
				HtmlCleaner cleaner = new HtmlCleaner();
		        CleanerProperties props = cleaner.getProperties();
		        props.setAllowHtmlInsideAttributes(true);
		        props.setAllowMultiWordAttributes(true);
		        props.setRecognizeUnicodeChars(true);
		        props.setOmitComments(true);
			
				URL url = new URL(link);
				URLConnection conn;
				conn = url.openConnection();
				InputStreamReader isr = new InputStreamReader(conn.getInputStream());
				TagNode node = cleaner.clean(isr);

		        Object[] objMeta = node.evaluateXPath("//img[@id='originPic']");
		        if (objMeta.length > 0) {
		        	TagNode info_node = (TagNode) objMeta[0];
		        	image = URLDecoder.decode(info_node.getAttributeByName("src").toString().trim());
		        }
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XPatherException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String imgThumb = image;
			String imgLarge = image;
			Bitmap bmp = getBitmap(imgThumb, HEIGHT_IMAGE);
			if (bmp!=null) {
				InfoLink il = new InfoLink();
				il.setBitmapLarge(bmp);
				Bitmap bmpthumb = getBitmap(bmp, Utils.HEIGHT_THUMB);
				il.setBitmapThumb(bmpthumb);
				il.setExtensiveInfo(true);
				il.setService("Mytubo.net");
				il.setType(0);
				il.setLink(link);
				il.setOriginalLink(originalLink);
				il.setLinkImageThumb(imgThumb);
				il.setLinkImageLarge(imgLarge);
				return il;
			}
			
		}
		
		if (link.contains("imgur.com")) {
			String id = link.substring(link.lastIndexOf("/")+1);
			String imgThumb = "http://i.imgur.com/"+id+"b.jpg";
			String imgLarge = "http://i.imgur.com/"+id+".jpg";
			Bitmap bmp = getBitmap(imgThumb, HEIGHT_THUMB);
			if (bmp!=null) {
				InfoLink il = new InfoLink();
				il.setBitmapThumb(bmp);
				il.setService("Imgur");
				il.setType(0);
				il.setLink(link);
				il.setOriginalLink(originalLink);
				il.setLinkImageThumb(imgThumb);
				il.setLinkImageLarge(imgLarge);
				return il;
			}
		}
		
		// instagr.am	
		if (link.contains("instagr.am")) {
			
			String image = "";
			
			try {
				HtmlCleaner cleaner = new HtmlCleaner();
		        CleanerProperties props = cleaner.getProperties();
		        props.setAllowHtmlInsideAttributes(true);
		        props.setAllowMultiWordAttributes(true);
		        props.setRecognizeUnicodeChars(true);
		        props.setOmitComments(true);
			
				URL url = new URL(link);
				URLConnection conn;
				conn = url.openConnection();
				InputStreamReader isr = new InputStreamReader(conn.getInputStream());
				TagNode node = cleaner.clean(isr);

		        Object[] objMeta = node.evaluateXPath("//meta[@property='og:image']");
		        if (objMeta.length > 0) {
		        	TagNode info_node = (TagNode) objMeta[0];
		        	image = URLDecoder.decode(info_node.getAttributeByName("content").toString().trim());
		        }
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XPatherException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String imgThumb = image;
			String imgLarge = image;
			Bitmap bmp = getBitmap(imgThumb, Utils.HEIGHT_IMAGE);
			if (bmp!=null) {
				InfoLink il = new InfoLink();
				il.setBitmapLarge(bmp);
				Bitmap bmpthumb = getBitmap(bmp, Utils.HEIGHT_THUMB);
				il.setBitmapThumb(bmpthumb);
				il.setExtensiveInfo(true);
				il.setService("Instagr.am");
				il.setType(0);
				il.setLink(link);
				il.setOriginalLink(originalLink);
				il.setLinkImageThumb(imgThumb);
				il.setLinkImageLarge(imgLarge);
				return il;
			}
		}
		
		// lightbox	
		if (link.contains("lightbox")) {
			String id = link.substring(link.lastIndexOf("/")+1);
			String imgThumb = "http://lightbox.com/show/thumb/"+id;
			String imgLarge = "http://lightbox.com/show/large/"+id;
			Bitmap bmp = getBitmap(imgThumb, HEIGHT_THUMB);
			if (bmp!=null) {
				InfoLink il = new InfoLink();
				il.setBitmapThumb(bmp);
				il.setService("Lightbox");
				il.setType(0);
				il.setLink(link);
				il.setOriginalLink(originalLink);
				il.setLinkImageThumb(imgThumb);
				il.setLinkImageLarge(imgLarge);
				return il;
			}
		}
		
		// twitpic	
		if (link.contains("twitpic")) {
			String id = link.substring(link.lastIndexOf("/")+1);
			String imgThumb = "http://twitpic.com/show/mini/"+id;
			String imgLarge = "http://twitpic.com/show/large/"+id;
			Bitmap bmp = getBitmap(imgThumb, HEIGHT_THUMB);
			if (bmp!=null) {
				InfoLink il = new InfoLink();
				il.setBitmapThumb(bmp);
				il.setService("Twitpic");
				il.setType(0);
				il.setLink(link);
				il.setOriginalLink(originalLink);
				il.setLinkImageThumb(imgThumb);
				il.setLinkImageLarge(imgLarge);
				return il;
			}
		}
		
		// picplz	
		if (link.contains("picplz")) {
			String id = link.substring(link.lastIndexOf("/")+1);
			String imgThumb = "http://picplz.com/"+id+"/thumb/200";
			String imgLarge = "http://picplz.com/"+id+"/thumb/400";
			Bitmap bmp = getBitmap(imgThumb, HEIGHT_THUMB);
			if (bmp!=null) {
				InfoLink il = new InfoLink();
				il.setBitmapThumb(bmp);
				il.setService("Picplz");
				il.setType(0);
				il.setLink(link);
				il.setOriginalLink(originalLink);
				il.setLinkImageThumb(imgThumb);
				il.setLinkImageLarge(imgLarge);
				return il;
			}
		}
		
		// img.ly
		if (link.contains("img.ly")) {
			String id = link.substring(link.lastIndexOf("/")+1);
			String imgThumb = "http://img.ly/show/thumb/"+id;
			String imgLarge = "http://img.ly/show/medium/"+id;
			Bitmap bmp = getBitmap(imgThumb, HEIGHT_THUMB);
			if (bmp!=null) {
				InfoLink il = new InfoLink();
				il.setBitmapThumb(bmp);
				il.setService("Img.ly");
				il.setType(0);
				il.setLink(link);
				il.setOriginalLink(originalLink);
				il.setLinkImageThumb(imgThumb);
				il.setLinkImageLarge(imgLarge);
				return il;
			}
		}

        // vvcap

        if (link.contains("vvcap")) {
            String imgThumb = link.replace(".htp", ".png");
            String imgLarge = link.replace(".htp", ".png");
            Bitmap bmp = getBitmap(imgThumb, HEIGHT_IMAGE);
            if (bmp!=null) {
                InfoLink il = new InfoLink();
                il.setBitmapLarge(bmp);
                Bitmap bmpthumb = getBitmap(bmp, Utils.HEIGHT_THUMB);
                il.setBitmapThumb(bmpthumb);
                il.setExtensiveInfo(true);
                il.setService("Vvcap.net");
                il.setType(0);
                il.setLink(link);
                il.setOriginalLink(originalLink);
                il.setLinkImageThumb(imgThumb);
                il.setLinkImageLarge(imgLarge);
                return il;
            }
        }
		
		// yfrog
		
		if (link.contains("yfrog")) {
			String imgThumb = link+".th.jpg";
			String imgLarge = link+":android";
			Bitmap bmp = getBitmap(imgThumb, HEIGHT_THUMB);
			if (bmp!=null) {
				InfoLink il = new InfoLink();
				il.setBitmapThumb(bmp);
				il.setService("Yfrog");
				il.setType(0);
				il.setLink(link);
				il.setOriginalLink(originalLink);
				il.setLinkImageThumb(imgThumb);
				il.setLinkImageLarge(imgLarge);
				return il;
			}
		}
		
		// plixi o lockerz
		
		if (link.contains("plixi") || link.contains("lockerz")) {
			    				
			String strURL = "http://api.plixi.com/api/tpapi.svc/metadatafromurl?url=" + link;
			try {
				Document doc = null;
				try {
					URL url;
					URLConnection urlConn = null;
					url = new URL(strURL);
					urlConn = url.openConnection();
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					doc = db.parse(urlConn.getInputStream());
				} catch (IOException ioe) {
				} catch (ParserConfigurationException pce) {
				} catch (SAXException se) {
				}
				
				if (doc!=null) {
					try {
						String imgThumb = doc.getElementsByTagName("ThumbnailUrl").item(0).getFirstChild().getNodeValue();
	    				String imgLarge = doc.getElementsByTagName("MediumImageUrl").item(0).getFirstChild().getNodeValue();;
										        
				        if (!imgThumb.equals("")) {
					        Bitmap bmp = getBitmap(imgThumb, HEIGHT_THUMB);
		    				if (bmp!=null) {
			        			InfoLink il = new InfoLink();
		    					il.setBitmapThumb(bmp);
		    					il.setService("Lockerz");
		    					il.setType(0);
		    					il.setLink(link);
		    					il.setOriginalLink(originalLink);
		    					il.setLinkImageThumb(imgThumb);
		    					il.setLinkImageLarge(imgLarge);
		    					return il;
		    				}
				        }
					} catch (Exception e) {
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		// twitgoo
		
		if (link.contains("twitgoo")) {
			String id = link.substring(link.lastIndexOf("/")+1);
			String strURL = "http://twitgoo.com/api/message/info/" + id;
			Document doc = null;
			try {
				URL url;
				URLConnection urlConn = null;
				url = new URL(strURL);
				urlConn = url.openConnection();
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				doc = db.parse(urlConn.getInputStream());
			} catch (IOException ioe) {
			} catch (ParserConfigurationException pce) {
			} catch (SAXException se) {
			}
			
			if (doc!=null) {
				try {
					String imgThumb = doc.getElementsByTagName("thumburl").item(0).getFirstChild().getNodeValue();
    				String imgLarge = doc.getElementsByTagName("imageurl").item(0).getFirstChild().getNodeValue();
    				if (!imgThumb.equals("")) {
				        Bitmap bmp = getBitmap(imgThumb, HEIGHT_THUMB);
	    				if (bmp!=null) {
		        			InfoLink il = new InfoLink();
	    					il.setBitmapThumb(bmp);
	    					il.setService("Twitgoo");
	    					il.setType(0);
	    					il.setLink(link);
	    					il.setOriginalLink(originalLink);
	    					il.setLinkImageThumb(imgThumb);
	    					il.setLinkImageLarge(imgLarge);
	    					return il;
	    				}
			        }
				} catch (Exception e) {
				}
			}
			
		}
		
		// twitvid	
		if (link.contains("twitvid")) {
			String id = link.substring(link.lastIndexOf("/")+1);
			String imgThumb = "http://images2.twitvid.com/"+id+".jpg";
			String imgLarge = "http://images2.twitvid.com/"+id+".jpg";
			Bitmap bmp = getBitmap(imgThumb, HEIGHT_THUMB);
			if (bmp!=null) {
				InfoLink il = new InfoLink();
				il.setBitmapThumb(bmp);
				il.setService("twitvid");
				il.setType(1);
				il.setLink(link);
				il.setOriginalLink(originalLink);
				il.setTitle("Twitvid");
				il.setDurationVideo(0);
				il.setLinkImageThumb(imgThumb);
				il.setLinkImageLarge(imgLarge);
				return il;
			}
		}
		
		// vimeo
		
		if (link.contains("vimeo")) {
			String id = link.substring(link.lastIndexOf("/")+1);
			String strURL = "http://vimeo.com/api/v2/video/"+id+".xml";
			
			Document doc = null;
			try {
				URL url;
				URLConnection urlConn = null;
				url = new URL(strURL);
				urlConn = url.openConnection();
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				doc = db.parse(urlConn.getInputStream());
			} catch (IOException ioe) {
			} catch (ParserConfigurationException pce) {
			} catch (SAXException se) {
			}
			
			if (doc!=null) {
				try {
					String imgThumb = doc.getElementsByTagName("thumbnail_small").item(0).getFirstChild().getNodeValue();
    				String imgLarge = doc.getElementsByTagName("thumbnail_large").item(0).getFirstChild().getNodeValue();
    				String title = doc.getElementsByTagName("title").item(0).getFirstChild().getNodeValue();
    				int duration = Integer.parseInt(doc.getElementsByTagName("duration").item(0).getFirstChild().getNodeValue());
    				if (!imgThumb.equals("")) {
				        Bitmap bmp = getBitmap(imgThumb, HEIGHT_THUMB);
	    				if (bmp!=null) {
		        			InfoLink il = new InfoLink();
	    					il.setBitmapThumb(bmp);
	    					il.setService("Vimeo");
	    					il.setType(1);
	    					il.setLink(link);
	    					il.setOriginalLink(originalLink);
	    					il.setTitle(title);
	    					il.setDurationVideo(duration);
	    					il.setLinkImageThumb(imgThumb);
	    					il.setLinkImageLarge(imgLarge);
	    					return il;
	    				}
			        }
				} catch (Exception e) {
				}
			}
			
		}
		
		// youtube
		
		if ( (link.contains("youtube")) || (link.contains("youtu.be")) ) {
			String id = "";
			if (link.contains("youtube")) {
				id = link.substring(link.lastIndexOf("v=")+2);
				if (id.contains("&")) {
					id = id.substring(0,id.indexOf("&"));
				}
			}
			if (link.contains("youtu.be")) {
				id = link.substring(link.lastIndexOf("/")+1);
				if (id.contains("?")) {
					id = id.substring(0,id.indexOf("?"));
				}
			}
			String imgThumb = "http://img.youtube.com/vi/"+id+"/2.jpg";
			String imgLarge = "http://img.youtube.com/vi/"+id+"/0.jpg";
			Bitmap bmp = getBitmap(imgThumb, HEIGHT_THUMB);
			if (bmp!=null) {
				    					
				String strURL = "http://gdata.youtube.com/feeds/api/videos/"+id;
				
				Document doc = null;
				try {
					URL url;
					URLConnection urlConn = null;
					url = new URL(strURL);
					urlConn = url.openConnection();
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					doc = db.parse(urlConn.getInputStream());
				} catch (IOException ioe) {
				} catch (ParserConfigurationException pce) {
				} catch (SAXException se) {
				}
				
				String title = "Youtube";
				int duration = 0;
				
				try {
					if (doc!=null) {
						title = doc.getElementsByTagName("title").item(0).getFirstChild().getNodeValue();
						duration = Integer.parseInt(doc.getElementsByTagName("yt:duration").item(0).getAttributes().getNamedItem("seconds").getNodeValue());
					}
				} catch (Exception e) {
					
				}
				
				InfoLink il = new InfoLink();
				il.setBitmapThumb(bmp);
				il.setService("Youtube");
				il.setType(1);
				il.setLink(link);
				il.setOriginalLink(originalLink);
				il.setTitle(title);
				il.setDurationVideo(duration);
				il.setLinkImageThumb(imgThumb);
				il.setLinkImageLarge(imgLarge);
				return il;
				
			}
		}
		
		if ( (Integer.parseInt(Utils.preference.getString("prf_links", "3"))) == 3 ) {			
			InfoLink il = new InfoLink();
			il.setService("web");
			il.setType(2);
			il.setLink(link);
			il.setOriginalLink(originalLink);
			il.setTitle(originalLink);
			return il;
		}
    			
    	return null;
    }
    
    static public String getFileFromURL(String url) {
    	String[] c = url.split("/");
    	return c[c.length-1];
    }
    
	static public Bitmap getBitmap(Bitmap bitmapOrg, int newHeight) {
		try {
			
			int width = bitmapOrg.getWidth();
	        int height = bitmapOrg.getHeight();
	        int newWidth = 0;
	        
			if (width>height) {
				newWidth = (newHeight * width) / height;

			} else {
				newWidth = newHeight;
				newHeight = (newWidth*height) / width;
			}
			
			if (height>newHeight) {
		        float scaleWidth = ((float) newWidth) / width;
				float scaleHeight = ((float) newHeight) / height;
				
		        Matrix matrix = new Matrix();
		        matrix.postScale(scaleWidth, scaleHeight);	        
				
		        return Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, false);
			} else {
				return bitmapOrg;
			}
			/*
			Options opt = new Options();
			opt.inSampleSize = 2;
			Bitmap bm = BitmapFactory.decodeStream(new Utils.FlushedInputStream(bis), null, opt);
			bis.close();
			is.close();
			
			int width = (height * bm.getWidth()) / bm.getHeight();
			
			return Bitmap.createScaledBitmap(bm, width, height, true);*/
			
		} catch (OutOfMemoryError e) { 
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
    }
    
	static public Bitmap getBitmap(String url, int newHeight) {
		try {
			URL urlImage = new URL(url);
			URLConnection conn = urlImage.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			
			Bitmap bitmapOrg = BitmapFactory.decodeStream(new Utils.FlushedInputStream(bis));
			
			int width = bitmapOrg.getWidth();
	        int height = bitmapOrg.getHeight();
	        int newWidth = 0;
	        
			if (width>height) {
				newWidth = (newHeight * width) / height;

			} else {
				newWidth = newHeight;
				newHeight = (newWidth*height) / width;
			}

			if (height>newHeight) {
		        float scaleWidth = ((float) newWidth) / width;
				float scaleHeight = ((float) newHeight) / height;
				
		        Matrix matrix = new Matrix();
		        matrix.postScale(scaleWidth, scaleHeight);	        
				
		        return Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, false);
			} else {
				return bitmapOrg;
			}
			/*
			Options opt = new Options();
			opt.inSampleSize = 2;
			Bitmap bm = BitmapFactory.decodeStream(new Utils.FlushedInputStream(bis), null, opt);
			bis.close();
			is.close();
			
			int width = (height * bm.getWidth()) / bm.getHeight();
			
			return Bitmap.createScaledBitmap(bm, width, height, true);*/
			
		} catch (OutOfMemoryError e) { 
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
    }

	static public Bitmap getResizeBitmapFromFile(String file, int newHeight) {
		try {

	        Bitmap bitmapOrg = getBitmapFromFile(file, newHeight, false);
	         
	        int width = bitmapOrg.getWidth();
	        int height = bitmapOrg.getHeight();
	        int newWidth = 0;
	        
			if (width>height) {
				newWidth = (newHeight * width) / height;

			} else {
				newWidth = newHeight;
				newHeight = (newWidth*height) / width;
			}
 
			if (height>newHeight) {
		        float scaleWidth = ((float) newWidth) / width;
				float scaleHeight = ((float) newHeight) / height;
				
		        Matrix matrix = new Matrix();
		        matrix.postScale(scaleWidth, scaleHeight);	 
		        
		        ExifInterface exif = new ExifInterface(file);
		        
		        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
		        
		        if (orientation==3) {
		        	matrix.postRotate(180);
		        } else if (orientation==6) {
		        	matrix.postRotate(90);
		        } else if (orientation==8) {
		        	matrix.postRotate(270);
		        }
				
		        return Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);
			} else {
				return bitmapOrg;
			}

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
    }
	
	static public Bitmap getBitmapFromFile(String file, int height, boolean crop) {
		try {

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file),null,o);

            //Find the correct scale value. It should be the power of 2.
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;

            while(true){
                if(width_tmp/2<height || height_tmp/2<height)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }

            //decode with inSampleSize
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inSampleSize=scale;

			Bitmap bm = BitmapFactory.decodeFile(file, opt);
			
			if (crop) {
				
				Bitmap b = null;
				
				if (bm.getWidth()> bm.getHeight()) {
					int x = (bm.getWidth() - bm.getHeight())/2;
					b = Bitmap.createBitmap(bm, x, 0, bm.getHeight(), bm.getHeight());	
				} else {
					int y = (bm.getHeight() - bm.getWidth())/2;
					b = Bitmap.createBitmap(bm, 0, y, bm.getWidth(), bm.getWidth());	
				}
								
				return Bitmap.createScaledBitmap(b, height, height, true);
			} else {
				int width = (height * bm.getWidth()) / bm.getHeight();
				return Bitmap.createScaledBitmap(bm, width, height, true);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
    }
	
	public static Bitmap saveAvatar(String u, File file) {
		URL url;
		try {
			url = new URL(u);
			Bitmap bmp = BitmapFactory.decodeStream(new Utils.FlushedInputStream(url.openStream()));	
			if (bmp!=null) {
				bmp = getRoundedCornerBitmap(bmp, 5);
				FileOutputStream out = new FileOutputStream(file);
				bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
				return bmp;
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    public static Bitmap savePhotoInScale(Context context, String image) {

        Log.d(Utils.TAG, "Image " + image);

        int s = Integer.parseInt(Utils.getPreference(context).getString("prf_size_photo", "2"));

        Bitmap bmp = null;

        int size = 0;

        if (s == 1) {
            size = HEIGHT_PHOTO_SIZE_SMALL;
        } else if (s == 2) {
            size = HEIGHT_PHOTO_SIZE_MIDDLE;
        } else {
            size = HEIGHT_PHOTO_SIZE_LARGE;
        }

        bmp = Utils.getResizeBitmapFromFile(image, size);

        if (bmp!=null) {
            Matrix matrix = new Matrix();

            float aux = bmp.getWidth();
            if (bmp.getHeight()>bmp.getWidth()) aux = bmp.getHeight();

            float scale = size/aux;
            matrix.setScale(scale, scale);
            Log.d(Utils.TAG, "Scale: "+scale);

            try {
                ExifInterface exif = new ExifInterface(image);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

                if (orientation==3) {
                    matrix.postRotate(180);
                } else if (orientation==6) {
                    matrix.postRotate(90);
                } else if (orientation==8) {
                    matrix.postRotate(270);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bmp!=null) {
                int w = bmp.getWidth();
                int h = bmp.getHeight();
                Log.d(Utils.TAG, "Original size w="+w + " h=" + h);
                try {
                    bmp = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, false);
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (bmp!=null) {
                Log.d(Utils.TAG, "Image scale to: w="+bmp.getWidth() + " h=" + bmp.getHeight());

                try {
                    FileOutputStream out = new FileOutputStream(image);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.d(Utils.TAG, "bmp == null");
        }
        
        return bmp;

    }
	
	public static Bitmap getAvatar(String u) {
		URL url;
		try {
			url = new URL(u);
			return getRoundedCornerBitmap(BitmapFactory.decodeStream(new Utils.FlushedInputStream(url.openStream())), 5);	
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
	
    public static String diffDate (Date first, Date second){

    	if (second.after(first)) {
       		return "0s";
    	}
    	
        long diff= ((first.getTime() - second.getTime()))/1000;
        
        if (diff<60) {
        	return String.valueOf(diff) + "s";
        } else {
        	// minutos
        	long sec = diff%60;
        	diff = diff/60;
        	if (diff<60) {
        		return String.valueOf(diff) + "m " + sec + "s";
        	} else {
        		// horas
        		long min = diff%60;
        		diff = diff/60;
            	if (diff<24) {
            		return String.valueOf(diff) + "h " + min + "m";
            	} else {
            		// dias
            		long hours = diff%24;
            		diff = diff/24;
                	return String.valueOf(diff) + "d " + hours + "h";
            	}
        	}
        }

    }

    public static String timeFromTweet (Context cnt, Date timeTweet){
    	
    	if ( Integer.parseInt(Utils.getPreference(cnt).getString("prf_date_format", "1")) == 1 ) {	
    		return diffDate(new Date(), timeTweet);
    	} else {
    	   	Date now = new Date();
	    	if (now.getDay()==timeTweet.getDay() && now.getMonth()==timeTweet.getMonth()
	    			&& now.getYear()==timeTweet.getYear()) {
	    		return DateFormat.getTimeInstance().format(timeTweet);
	    	} else {
	    		return DateFormat.getDateInstance().format(timeTweet);
	    	}
    	}

    }
    
    public static String timeFromTweetExtended (Context cnt, Date timeTweet){
    	
    	if ( Integer.parseInt(Utils.getPreference(cnt).getString("prf_date_format", "1")) == 1 ) {	
	    	if (timeTweet!=null) {
		    	Date now = new Date();
		    	
		        long diff= ((now.getTime() - timeTweet.getTime()))/1000;
		        String out = "";
		        
		        if (diff<60) {
		        	out = String.valueOf(diff) + " " + cnt.getString(R.string.seconds);
		        } else {
		        	// minutos
		        	long sec = diff%60;
		        	diff = diff/60;
		        	if (diff<60) {
		        		out = String.valueOf(diff) + " " + cnt.getString(R.string.minutes) + " " + cnt.getString(R.string.and) + " " + sec + " " + cnt.getString(R.string.seconds);
		        	} else {
		        		// horas
		        		long min = diff%60;
		        		diff = diff/60;
		            	if (diff<24) {
		            		out = String.valueOf(diff) + " " + cnt.getString(R.string.hours) + " " + cnt.getString(R.string.and) + " " + min + " " + cnt.getString(R.string.minutes);
		            	} else {
		            		// dias
		            		long hours = diff%24;
		            		diff = diff/24;
		            		out = String.valueOf(diff) + " " + cnt.getString(R.string.days) + " " + cnt.getString(R.string.and) + " " + hours + " " + cnt.getString(R.string.hours);
		            	}
		        	}
		        }
		        
		        return cnt.getString(R.string.date_long, out);
	    	}
    	} else {
    		return DateFormat.getDateTimeInstance().format(timeTweet);
    	}
    	return "";
    }
    
	public static String getTomorrow(int year, int month, int day) {
		int lastDayMonth = 0;
		if ( (month==1) && (month==3) && (month==5) && (month==7) && (month==8) && (month==10) && (month==12) ) {
			lastDayMonth = 31;
		} else if (month==2) {
			if ((year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0)))
				lastDayMonth = 29;
			else
				lastDayMonth = 28;
		} else {
			lastDayMonth = 30;
		}
		String out = "";
		if (day+1>lastDayMonth) {
			if (month+1>12) {
				out = (year+1) + "-1-1";
			} else {
				out = year + "-" + (month+1) + "-1";
			}
		} else {
			out = year + "-" + month + "-" + (day+1);
		}

		return out;
	}
	
	public static Drawable getDrawable(Context ctx, String text) {
		if (text.startsWith("drawable")) {
			return ctx.getResources().getDrawable(ctx.getResources().getIdentifier(Utils.packageName+":"+text, null, null));
		}
		if (text.startsWith("file")) {
			return Drawable.createFromPath(Utils.appDirectory+text.substring(5));
		}
		return null;
	}
    
    public static String getHashFromFile(String file) {
        return String.valueOf(Math.abs(file.hashCode()));
    }

    public static File getFileForSaveURL(Context cnt, String file) {
        return new File(cnt.getCacheDir(), getHashFromFile(file));
    }
    
	/*
	public static String extractFileFromAvatar(String url) {
		String[] s = url.split("/");
		if (s.length>1) {
			return s[s.length-2] + "_" + s[s.length-1];
		}
		return "";
	}
    */
	public static String getAsset(Context cnt, String file) {
    	AssetManager assetManager = cnt.getAssets();
    	InputStream inputStream = null;
    	try {
    		inputStream = assetManager.open(file);
    	} catch (IOException e) {
    		Log.d(TAG, "No se ha podido cargar el fichero " + file);
    	}

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int len;
		try {
			while ((len = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
		}
		return outputStream.toString();
	}
	
	public static boolean importTheme(Context cnt, String text) {
		
		try {			
			text = text.replace(Utils.URL_SHARE_THEME_QR, "");
			
			Log.d(Utils.TAG, text);
			
			ColorsApp.loadTheme(cnt, text);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean importSearch(Context cnt, String text) {
		
		try {
			if (text.startsWith("http://bit.ly")) {
				String url = "http://www.longurlplease.com/api/v1.1?q=" + text;
				
				HttpGet request = new HttpGet(url);
				HttpClient client = new DefaultHttpClient();
				HttpResponse httpResponse;
				try {
					httpResponse = client.execute(request);
					String xml = EntityUtils.toString(httpResponse.getEntity());
					JSONObject jsonObject = new JSONObject(xml);
				    String t = jsonObject.getString(text);
				    if ( (t!=null) && t!="") text = t;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			text = text.replace(Utils.URL_QR, "");
			
			StringTokenizer tokens = new StringTokenizer(text, "||");  
			
			Entity ent = new Entity("search"); 
			ent.setValue("date_create", Utils.now());
    		ent.setValue("last_modified", Utils.now());
        	ent.setValue("use_count", 0);
			
			long icon_id = -1;
			
			while(tokens.hasMoreTokens()) {  

				StringTokenizer hash = new StringTokenizer(tokens.nextToken(), "%%");  
				
				if (hash.countTokens()==2) {
					String key = hash.nextToken();
					String value = hash.nextToken();
					if (key.equals("name")) {
						ent.setValue("name", value);
					} else if (key.equals("and")) {
						ent.setValue("words_and", value);
					} else if (key.equals("or")) {
						ent.setValue("words_or", value);
					} else if (key.equals("not")) {
						ent.setValue("words_not", value);
					} else if (key.equals("from")) {
						ent.setValue("from_user", value);
					} else if (key.equals("to")) {
						ent.setValue("to_user", value);
					} else if (key.equals("lang")) {
						ent.setValue("lang", value);
					} else if (key.equals("att")) {
						ent.setValue("attitude", value);
					} else if (key.equals("filt")) {
						ent.setValue("filter", value);
					} else if (key.equals("rt")) {
						ent.setValue("no_retweet", value);
					} else if (key.equals("geo")) {
						ent.setValue("use_geo", value);
					} else if (key.equals("tgeo")) {
						ent.setValue("type_geo", value);
					} else if (key.equals("lat")) {
						ent.setValue("latitude", value);
					} else if (key.equals("long")) {
						ent.setValue("longitude", value);
					} else if (key.equals("tdist")) {
						ent.setValue("type_distance", value);
					} else if (key.equals("dist")) {
						ent.setValue("distance", value);
					} else if (key.equals("icon")) {
						icon_id = Long.parseLong(value);
					}
				}
			}
			
			if (icon_id>1) {
	    		Entity icon = new Entity("icons", icon_id);
	    		ent.setValue("icon_big", "drawable/"+icon.getValue("icon"));
	    		ent.setValue("icon_small", "drawable/"+icon.getValue("icon_small"));
	    	} else {
	    		String c = ent.getString("name").substring(0, 1).toLowerCase();
	    		int id = cnt.getResources().getIdentifier(Utils.packageName+":drawable/letter_"+c, null, null);
	    		if (id>0) {
	    			ent.setValue("icon_big", "drawable/letter_"+c);
		    		ent.setValue("icon_small", "drawable/letter_"+c+"_small");
	    		} else {
	    			ent.setValue("icon_big", "drawable/letter_az");
		    		ent.setValue("icon_small", "drawable/letter_az_small");
	    		}
	    	}
			
			ent.save();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static String exportSearch(long id) {
		
		String url = URL_QR;
		
		Entity search = new Entity("search", id);
		
		url += "||name%%" + search.getString("name");
		url += "||icon%%" + search.getString("icon_id");
		url += "||and%%" + search.getString("words_and");
		url += "||or%%" + search.getString("words_or");
		url += "||not%%" + search.getString("words_not");
		url += "||from%%" + search.getString("from_user");
		url += "||to%%" + search.getString("to_user");
		url += "||lang%%" + search.getString("lang");
		url += "||att%%" + search.getString("attitude");
		url += "||filt%%" + search.getString("filter");
		url += "||rt%%" + search.getString("no_retweet");
		url += "||geo%%" + search.getString("use_geo");
		url += "||tgeo%%" + search.getString("type_geo");
		url += "||lat%%" + search.getString("latitude");
		url += "||long%%" + search.getString("longitude");
		url += "||tdist%%" + search.getString("type_distance");
		url += "||dist%%" + search.getString("distance");
		
		return shortURL(url);
		
	}

    public static void sendLastCrash(Activity cnt) {
        /*Intent msg=new Intent(Intent.ACTION_SEND);
        msg.setType("text/plain");
        msg.putExtra(Intent.EXTRA_EMAIL, new String[] {"tweettopics.issues@gmail.com"});
        msg.putExtra(Intent.EXTRA_SUBJECT, "TweetTopics crash");
        msg.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(ErrorReporter.getErrors(cnt)));
        cnt.startActivity(msg);*/
        Intent gmail = new Intent(Intent.ACTION_VIEW);
        gmail.setClassName("com.google.android.gm","com.google.android.gm.ComposeActivityGmail");
        gmail.putExtra(Intent.EXTRA_EMAIL, new String[] { "tweettopics.issues@gmail.com" });
        gmail.setData(Uri.parse("tweettopics.issues@gmail.com"));
        gmail.putExtra(Intent.EXTRA_SUBJECT, "TweetTopics crash");
        gmail.setType("plain/text");
        gmail.putExtra(Intent.EXTRA_TEXT, ErrorReporter.getErrors(cnt));
        cnt.startActivity(gmail);
    }
	
	public static boolean isLite(Context context) {
		if (packageName.equals("com.javielinux.tweettopics.lite")) {
			return true;
		} else {
			PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
			SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
			return preference.getBoolean("prf_force_lite", false);
		}
	}
	
	public static boolean isDev(Context context) {
		return packageName.equals("com.javielinux.tweettopics");
	}

	
	public static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
	
	public static class SlideDrawable extends ShapeDrawable {
        private Paint mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Paint mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private int mColorStroke;
        
        public SlideDrawable(Shape s, int c) {
        	super(s);
        	init(s, c, 255);
        }
        
        public SlideDrawable(Shape s, int c, int alpha) {
            super(s);
            init(s, c, alpha);

        }
        
        private void init(Shape s, int c, int alpha) {
            float[] hsv = new float[3];
	        Color.colorToHSV(c, hsv);
	        if (hsv[2]-.08f>0) hsv[2]=hsv[2]-.08f;
	        mColorStroke=Color.HSVToColor(hsv);
            
            mStrokePaint.setStyle(Paint.Style.STROKE);
            mStrokePaint.setStrokeWidth(3);
            mStrokePaint.setColor(mColorStroke);
            mStrokePaint.setAlpha(alpha);
            
            mFillPaint.setStyle(Paint.Style.FILL);
            mFillPaint.setColor(c);
            mFillPaint.setAlpha(alpha);
        }
        
        public int getColorStroke() {
        	return mColorStroke;
        }
        
                
        @Override protected void onDraw(Shape s, Canvas c, Paint p) {
            s.draw(c, p);
            s.draw(c, mFillPaint);
            s.draw(c, mStrokePaint);
        }
    }


	
}