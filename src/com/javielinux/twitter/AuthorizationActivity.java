package com.javielinux.twitter;


import android.app.Activity;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.*;
import android.widget.LinearLayout;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.PreferenceUtils;
import twitter4j.TwitterException;

public class AuthorizationActivity extends Activity {

	public static String TWITTER_HOST = "www.tweet-topics.com";
	private WebView webView;
    private LinearLayout llRedirecting;
  
	private String network = "twitter.com";
	
	private WebViewClient webViewClient = new WebViewClient() {
		@Override
		public void onLoadResource(WebView view, String url) {
			Uri uri = Uri.parse(url);
			if (uri!=null && uri.getHost().equals(TWITTER_HOST)) {
				String token = uri.getQueryParameter("oauth_token");
				if (null != token) {
                    showRedirecting();
				    ConnectionManager.getInstance().finalizeOAuthentication(uri, new ConnectionManager.TwitterAuthentication() {
                        @Override
                        public void onFinalizeOAuthentication() {
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
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

        setContentView(R.layout.twitter_oauth);

		webView = (WebView)findViewById(R.id.twitter_oauth_webview);
		webView.setWebViewClient(webViewClient);

        llRedirecting = (LinearLayout)findViewById(R.id.twitter_oauth_redirecting);
        llRedirecting.setVisibility(View.GONE);
		
		CookieManager.getInstance().removeAllCookie();
		CookieManager.getInstance().removeExpiredCookie();
		CookieManager.getInstance().removeSessionCookie();
		
		CookieSyncManager.getInstance().sync();
		
	}

    private void showRedirecting() {
        llRedirecting.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
    }
  
	@Override
	protected void onResume() {
		super.onResume();
        PreferenceUtils.saveStatusWorkApp(this, true);
		
		ConnectionManager.destroyInstance();
		
		ConnectionManager.getInstance().open(this);
    	
//        ConnectionManager.getInstance().setNetworkConfig(network);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String authUrl = null;
                try {
                    authUrl = ConnectionManager.getInstance().getAuthenticationURL();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }


                if (authUrl!=null) {
                    webView.loadUrl(authUrl);
                } else {
                    // TODO Mostrar un mensaje, un toast aquí casca
                    finish();
                }
            }
        }).start();

	}
    
    @Override
    protected void onPause() {
        super.onPause();
        PreferenceUtils.saveStatusWorkApp(this, false);
    }
  
}
