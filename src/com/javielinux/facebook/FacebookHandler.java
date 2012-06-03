package com.javielinux.facebook;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.javielinux.tweettopics2.Users;
import com.javielinux.utils.Utils;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class FacebookHandler {

	public static final String FB_APP_ID = "213237322060541";
	
	private WebView mWebView;
	boolean isWebViewShown;
	
	private Users mUsersActivity = null;
	
    private Activity activity;
    LinearLayout layout;
	
    private static String[] PERMISSIONS = 
        new String[] { "offline_access", "read_stream", "publish_stream" };

    public  FacebookHandler(Activity activity) {
    	if (activity!=null) {
	    	this.activity = activity;
	
	        layout = new LinearLayout(activity);
	        activity.addContentView(
	                layout, new LayoutParams(
	                        LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	        isWebViewShown = false;
    	}
    }
    
	public void setUsersActivity(Users mUsersActivity) {
		this.mUsersActivity = mUsersActivity;
	}
	
    private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				if (mUsersActivity!=null) {
					mUsersActivity.fillData();	
				}
			}
		}
    };
    
    public void newUser() {
        showWebView();
        new JsHandler().login();
    }
    
    public Facebook loadUser(long id) {
    	
    	try {
        	DataFramework.getInstance().open(getActivity(), Utils.packageName);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        Entity ent = DataFramework.getInstance().getTopEntity("users", DataFramework.KEY_ID + " = " + id, "");
        
        if (ent!=null) {
            Facebook fb = new Facebook(FB_APP_ID);
            fb.setAccessToken(ent.getString("token_facebook"));
            fb.setAccessExpires(ent.getLong("expires_facebook"));

            if (fb.isSessionValid()) {
                return fb;
            } else {
            	return null;
            }
        }
        
        DataFramework.getInstance().close();
        return null;
    }
    
    public void showWebView() {
        if (isWebViewShown) {
            return;
        }
        
        CookieSyncManager.createInstance(activity);
        
        mWebView = new WebView(activity);

        layout.addView(mWebView,
                new LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        
		CookieManager.getInstance().removeAllCookie();
		CookieManager.getInstance().removeExpiredCookie();
		CookieManager.getInstance().removeSessionCookie();
		
		CookieSyncManager.getInstance().sync();
        
        isWebViewShown = true;
    }

    public void hideWebView() {
        layout.removeView(mWebView);
        isWebViewShown = false;
    }

    public Activity getActivity() {
        return activity;
    }
    
    public boolean isWebViewShown() {
        return isWebViewShown;
    }

	private class JsHandler {

        public void login() {
            final Activity activity = FacebookHandler.this.getActivity();
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    hideWebView();
                    try {
                    	final Facebook fb = new Facebook(FB_APP_ID);
                    	fb.authorize(getActivity(), PERMISSIONS,
                                 new AppLoginListener(fb));
                    } catch (Exception e) {
                    	Log.d(Utils.TAG, "values: " + e.getMessage());
                    }
                }
            });
        }

        private class AppLoginListener implements DialogListener {

            private Facebook fb;

            public AppLoginListener(Facebook fb) {
                this.fb = fb;
            }
            
            public void saveUser(String uid, String name, String username) {
            	try {
                	DataFramework.getInstance().open(getActivity(), Utils.packageName);
                } catch (Exception e) {
                	e.printStackTrace();
                }
                
            	name = name.replace("'", "");
            	
                if (DataFramework.getInstance().getTopEntity("users", "name = '" + name + "'", "")==null) {
                	Entity ent = new Entity("users");
                	ent.setValue("name", name);
                	ent.setValue("service", "facebook");
                	ent.setValue("uid_facebook", uid);
                	ent.setValue("username_facebook", username);
                	ent.setValue("expires_facebook", Utils.fillZeros(fb.getAccessExpires()+""));
                	ent.setValue("token_facebook", fb.getAccessToken());
                	ent.save();

					try {
						URL url = new URL("http://graph.facebook.com/"+username+"/picture");
	    				Bitmap avatar = BitmapFactory.decodeStream(new Utils.FlushedInputStream(url.openStream()));
	    				
	    				if (avatar!=null) {
	    					String file = Utils.getFileAvatar(ent.getId());
	    					FileOutputStream out = new FileOutputStream(file);
	    					avatar.compress(Bitmap.CompressFormat.JPEG, 90, out);
	    					avatar.recycle();
	    				}
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
                	
                }
                
                DataFramework.getInstance().close();
                
                handler.sendEmptyMessage(0);
            }

            public void onCancel() {
                Log.d(Utils.TAG, "login canceled");
            }

            public void onComplete(Bundle values) {
            	
                new AsyncFacebookRunner(fb).request("/me", 
                        new AsyncRequestListener() {
                    public void onComplete(JSONObject obj, final Object state) {
                    	Log.d(Utils.TAG, "obj: " + obj.toString());
                        String uid = obj.optString("id");
                        String name = obj.optString("name");
                        String username = obj.optString("username");
                        saveUser(uid, name, username);

                    }
                }, null);
            }

            public void onError(DialogError e) {
                Log.d(Utils.TAG, "dialog error: " + e);               
            }

            public void onFacebookError(FacebookError e) {
                Log.d(Utils.TAG, "facebook error: " + e);
            }
        }
    }
    /*
    private class AppWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("app://")) {
                String handlerName = url.substring(6);
                Log.d("app", "ejecutar " + handlerName);
                //runHandler(handlerName);
                return true;	
            }
            return false;
        }        
    }
*/
}
