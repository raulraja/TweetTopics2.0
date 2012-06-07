package com.javielinux.twitter;


import android.app.Activity;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.*;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.PreferenceUtils;
import com.javielinux.utils.Utils;
import twitter4j.TwitterException;

public class AuthorizationActivity extends Activity {

	public static String TWITTER_HOST = "www.tweet-topics.com";
	private WebView webView;
  
	private String network = "twitter.com";
	
	private WebViewClient webViewClient = new WebViewClient() {
		@Override
		public void onLoadResource(WebView view, String url) {
			Uri uri = Uri.parse(url);
			if (uri!=null && uri.getHost().equals(TWITTER_HOST)) {
				String token = uri.getQueryParameter("oauth_token");
				if (null != token) {
					webView.setVisibility(View.INVISIBLE);
					try {
						ConnectionManager2.getInstance().finalizeOAuthentication(uri);
					} catch (TwitterException e) {
						e.printStackTrace();
					}
					finish();
				} else {
					//Toast.makeText(AuthorizationActivity.this, "No se ha podido acceder a Twitter", Toast.LENGTH_LONG).show();
				}
			} else {
				super.onLoadResource(view, url);
			}
		}
		@Override
		public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("network")) network = savedInstanceState.getString("network");
		} else {
			Bundle extras = getIntent().getExtras();  
       		if (extras != null) {
       			if (extras.containsKey("network")) network = extras.getString("network");
       		}
		}
		
		CookieSyncManager.createInstance(this);
    
		webView = new WebView(this);
		webView.setWebViewClient(webViewClient);
    
		setContentView(webView);
		
		CookieManager.getInstance().removeAllCookie();
		CookieManager.getInstance().removeExpiredCookie();
		CookieManager.getInstance().removeSessionCookie();
		
		CookieSyncManager.getInstance().sync();
		
	}
  
	@Override
	protected void onResume() {
		super.onResume();
        PreferenceUtils.saveStatusWorkApp(this, true);
		
		ConnectionManager2.destroyInstance();
		
		ConnectionManager2.getInstance().open(this);
    	
        ConnectionManager2.getInstance().setNetworkConfig(network);
        
        String authUrl = null;
		try {
			authUrl = ConnectionManager2.getInstance().getAuthenticationURL();
		} catch (TwitterException e) {
			e.printStackTrace();
		}		
		
		
		if (authUrl!=null) {
			webView.loadUrl(authUrl);
		} else {
			Utils.showMessage(this, getString(R.string.problem_twitter_auth));
			finish();
		}
	}
    
    @Override
    protected void onPause() {
        super.onPause();
        PreferenceUtils.saveStatusWorkApp(this, false);
    }
  
}
