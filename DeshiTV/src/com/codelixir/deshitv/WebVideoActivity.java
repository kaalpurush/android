package com.codelixir.deshitv;

import java.util.Calendar;
import java.util.List;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;

import android.os.Bundle;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

public class WebVideoActivity extends Activity implements AdListener{
	WebView webview;
	private AdView mAdView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.webvideo);
		
		if(!shouldHideAd()){
			
			AdMan adman=new AdMan(this,getApplicationContext().getPackageName());
			
			String ad_id=adman.getID();
			
			if(ad_id!=""){
				
				LinearLayout ad_container = (LinearLayout)findViewById(R.id.ad_container);
				
				AdView mAdView = new AdView(this, AdSize.BANNER, ad_id);
				
				mAdView.setAdListener(this);
				
				ad_container.addView(mAdView);
				
				AdRequest mAdRequest = new AdRequest();
				
				//mAdRequest.addTestDevice("SH15NTR29817");
				
				mAdView.loadAd(mAdRequest);
				
			}
		}
		
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.flash_required), Toast.LENGTH_SHORT).show();
		
		Intent intent = new Intent();

	    intent.setComponent(new ComponentName("com.adobe.flashplayer", "com.adobe.flashplayer.FlashExpandableFileChooser"));
	    PackageManager pm = getPackageManager();
	    List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
	    if (activities != null && activities.size() > 0) {
	        Toast.makeText(this, "Loading...Please Wait!", Toast.LENGTH_LONG).show();
	    }
	    else {
	        Toast.makeText(this, "Flash Player not installed! Please install Flash Player from Play Store.", Toast.LENGTH_LONG).show();	        
	    }
		
		String url = getIntent().getDataString();
		String title=getIntent().getStringExtra("displayName");
		
    	webview = (WebView)findViewById(R.id.webView); 
    	webview.getSettings().setJavaScriptEnabled(true);
    	webview.getSettings().setPluginsEnabled(true);
    	webview.setWebViewClient(new WebViewClient() {            
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				//webview.loadUrl(url);
			    return true;
			}
    	});
    	webview.setWebChromeClient(new WebChromeClient());
    	webview.loadUrl(url);
	
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		webview.destroy();
	}
	
	public void hideAd(){
		mAdView.removeAllViews();
		Calendar cal = Calendar.getInstance(); 		
		putSetting("ad_day", String.valueOf(cal.get(Calendar.DATE)));
	}
	
	public boolean shouldHideAd(){
		Calendar cal = Calendar.getInstance(); 		
		return getSetting("ad_day").equals(String.valueOf(cal.get(Calendar.DATE)));
	}
	
    public String getSetting(String key){
    	SharedPreferences settings = getSharedPreferences("Settings", 0);
    	return settings.getString(key, ""); 
    }
    
    public Boolean putSetting(String key, String value){
    	SharedPreferences settings = getSharedPreferences("Settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }
    
	@Override
	public void onDismissScreen(Ad arg0) {
		try{
			hideAd();
		}catch(Exception e){}
		//toast(getResources().getString(R.string.ad_disabled));
	};
	
	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLeaveApplication(Ad arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPresentScreen(Ad arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onReceiveAd(Ad arg0) {
		// TODO Auto-generated method stub
		
	} 

}