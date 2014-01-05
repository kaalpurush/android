package com.codelixir.deshitv;

import java.util.Calendar;
import java.util.List;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

public class WebVideoActivity extends Activity implements AdListener{
	WebView webview;
	private AdView mAdView;
	private FullscreenWebChromeClient mFullscreenWebChromeClient = null;
	
	private long enqueue;
    private DownloadManager dm;
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message hmsg) {
			if (hmsg.what == 1) {
				webview.setVisibility(View.VISIBLE);
			}
		}		
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.webvideo);
		
		//if(!shouldHideAd()){
			
			if(Application.adUnitId!=""){
				
				LinearLayout ad_container = (LinearLayout)findViewById(R.id.ad_container);
				
				AdView mAdView = new AdView(this, AdSize.BANNER, Application.adUnitId);
				
				mAdView.setAdListener(this);
				
				ad_container.addView(mAdView);
				
				AdRequest mAdRequest = new AdRequest();
				
				mAdRequest.addTestDevice("5E5E95C252515979D9A831EAD134DAD7");
				
				mAdRequest.addTestDevice("BB94E3046543351C3331C90A53574828");
				
				mAdView.loadAd(mAdRequest);
				
			}
		//}
			
		
		
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.flash_required), Toast.LENGTH_SHORT).show();
		
		if(Build.VERSION.SDK_INT>18){
			Toast.makeText(this, "Flash Player not supported!", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		if(isFlashPlayerInstalled()){
			new JavaScriptInterface(this).showSpinner("Loading...Please Wait!", "Loading");
	    }
	    else {
	    	new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Flash Player")
			.setMessage("Flash Player not installed! You need to have it properly installed. Do you want to download Flash Player?")
			.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int which) {
							downloadFlashPlayer();
						}
					}).setNegativeButton("Cancel", null).show();
	        	        
	    }
	    
		String url = getIntent().getDataString();
		String title=getIntent().getStringExtra("displayName");
		
    	webview = (WebView)findViewById(R.id.webView); 
    	webview.setBackgroundColor(Color.BLACK);
    	webview.getSettings().setJavaScriptEnabled(true);
    	webview.getSettings().setPluginsEnabled(true);
    	webview.addJavascriptInterface(new JavaScriptInterface(this), "jsInterface");
    	webview.setWebViewClient(new WebViewClient() {            
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				//webview.loadUrl(url);
			    return true;
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				view.loadUrl("javascript:$('body').html($('.entry iframe').parent().html());" +
						"$('body').css('background','black');" +
						"var old_width=$('iframe').width();" +
						"var old_height=$('iframe').height();" +
						"var ratio=$('body').width()/$('iframe').width();" +
						"var new_width=Math.round(old_width*ratio);" +
						"var new_height=Math.round(old_height*ratio);" +
						"$('iframe').width(new_width);" +	
						"$('iframe').height(new_height);" +
						"var src=$('iframe').attr('src');" +
						"src=src.replace(old_width,new_width);" +
						"src=src.replace(old_height,new_height);" +
						"$('iframe').attr('src',src);jsInterface.hideSpinner();");
				
				mHandler.sendEmptyMessageDelayed(1, 4000);  
				//view.setVisibility(View.VISIBLE);
			}
			
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		        super.onReceivedError(view, errorCode, description, failingUrl);	
		        view.loadData("<a href='#'>Connection Error!</a>", "text/html; charset=UTF-8", "utf-8");
		    }
			
    	});
    	
    	if(Build.VERSION.SDK_INT<14) 	
    		webview.setWebChromeClient(new WebChromeClient());
    	else{
    		mFullscreenWebChromeClient=new FullscreenWebChromeClient(this);
    		webview.setWebChromeClient(mFullscreenWebChromeClient);
    	}

    	webview.loadUrl(url);
	
	}
	
	public class JavaScriptInterface {
	    Context mContext;
	    ProgressDialog pDialog;

	    /** Instantiate the interface and set the context */
	    JavaScriptInterface(Context c) {
	        mContext = c;
	    }
	    

	    /** Show a toast from the web page */
	    @JavascriptInterface
	    public String showToast(String toast) {
	        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	        return toast;
	    }
	    
	    @JavascriptInterface
	    public void showSpinner(String message,String title){
	    	pDialog = ProgressDialog.show(WebVideoActivity.this, title, 
	    			message, true);
	    }
	    
	    @JavascriptInterface
	    public void hideSpinner(){
	    	try{
	    		if(pDialog!=null && pDialog.isShowing())
	    			pDialog.dismiss();
	    	}
	    	catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }
	    
	    @JavascriptInterface
	    public boolean viewUrl(String url){
	    	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	    	return false;
	    }
	    
	    @JavascriptInterface
	    public boolean showApps(){
	    	startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("market://search?q=pub:Codelixir+Lab")));
	    	return false;
	    }
	    
	    @JavascriptInterface
	    public int versionCode(){
	    	PackageInfo pInfo;
			try {
				pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				return pInfo.versionCode;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			
			return -1;	    		    	
	    }
	    
	    @JavascriptInterface	    
	    public void finishActivity(){
	    	finish();
	    }
	    
	    @JavascriptInterface	    
	    public void finishApp(){
	    	System.exit(0);
	    }
	    
	}
	
	@Override
	public void onBackPressed() {
		if(mFullscreenWebChromeClient!=null && mFullscreenWebChromeClient.isFullscreen())
			mFullscreenWebChromeClient.onHideCustomView();
		else
	        super.onBackPressed();	       
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		webview.destroy();
		super.onDestroy();		
	}
	
	public void hideAd(){		
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
    
    public Boolean isFlashPlayerInstalled(){
		Intent intent = new Intent();
	    intent.setComponent(new ComponentName("com.adobe.flashplayer", "com.adobe.flashplayer.FlashExpandableFileChooser"));
	    PackageManager pm = getPackageManager();
	    List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
	    return activities != null && activities.size() > 0;
    }
    
    public void downloadFlashPlayer(){
    	String flash_url;
    	if(Build.VERSION.SDK_INT<Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    		flash_url="http://download.macromedia.com/pub/flashplayer/installers/archive/android/11.1.111.73/install_flash_player_pre_ics.apk";
    	else
    		flash_url="http://download.macromedia.com/pub/flashplayer/installers/archive/android/11.1.115.81/install_flash_player_ics.apk";
    	
    	dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
    	DownloadManager.Request request = new DownloadManager.Request(Uri.parse(flash_url));
    	request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "flash_player.apk");
    	request.setMimeType("application/vnd.android.package-archive");
        enqueue = dm.enqueue(request);
        Intent i = new Intent();
        i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(i);
        finish();
    }
    
	@Override
	public void onDismissScreen(Ad arg0) {
		// TODO Auto-generated method stub
		//hideAd();
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