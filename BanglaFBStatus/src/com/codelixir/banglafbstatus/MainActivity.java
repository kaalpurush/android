package com.codelixir.banglafbstatus;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import android.util.Log;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
	static Activity activity;
	WebView webview;
	static Dialog dlg;
	static boolean dismissed=false;
	ProgressDialog pDialog;
	
	static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message hmsg) {
			if (hmsg.what == 1) {
				try{
					if(!dismissed && dlg!=null)
						dlg.show();
				}
		    	catch(Exception e){
		    		e.printStackTrace();
		    	}
			}
		}		
	};
	
	protected void onDestroy() {
		super.onDestroy();
    	try{
    		if(pDialog!=null && pDialog.isShowing())
    			pDialog.dismiss();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}		
	};
	
	@Override
	public void onBackPressed()
	{
	    if(webview.canGoBack())
	    	webview.goBack();
	    else{
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Confirmation")
			.setMessage("Do you want to exit?")
			.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							finish();
						}
					}).setNegativeButton("Cancel", null).show();
	    }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("@","MainActivity.onCreate");
		
		if(!isOnline()) toast("This application requires an active internet connection.");
		
		activity=this;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.main);
		
		AdMan adman=new AdMan(this,getApplicationContext().getPackageName());
		
		String ad_id=adman.getID();
		
		if(ad_id!=""){
			
			LinearLayout ad_container = (LinearLayout)findViewById(R.id.ad_container);
			
			AdView mAdView = new AdView(this, AdSize.BANNER, ad_id);
			
			ad_container.addView(mAdView);
			
			AdRequest mAdRequest = new AdRequest();
			
			//mAdRequest.addTestDevice("SH15NTR29817");
			
			mAdView.loadAd(mAdRequest);
			
		}		
		
    	webview = (WebView)activity.findViewById(R.id.webView); 
    	webview.getSettings().setJavaScriptEnabled(true);
    	webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    	webview.getSettings().setSupportMultipleWindows(true);
    	
    	webview.loadUrl("http://bangla-fb.appspot.com");
    	
    	webview.addJavascriptInterface(new JavaScriptInterface(this), "jsInterface");
    	
    	webview.setWebChromeClient(new WebChromeClient(){
		   @Override
		    public boolean onCreateWindow(WebView view, boolean dialog,
		            boolean userGesture, Message resultMsg) {
			   
			   if(isFinishing()) return true;
			   
			   	dismissed=false;
				dlg=new Dialog(activity);
				dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dlg.setContentView(R.layout.dialog);
				WebView childView=(WebView)dlg.findViewById(R.id.webView1); 
		        childView.getSettings().setJavaScriptEnabled(true);
		        childView.setWebChromeClient(this);
		        childView.setWebViewClient(new WebViewClient());
		        childView.requestFocusFromTouch();	
		        		        
		        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
		        transport.setWebView(childView);
		        resultMsg.sendToTarget();
		        
		        toast("Logging in...");
	 	        
		        mHandler.sendEmptyMessageDelayed(1, 5000);
		        return true;
		    }
    		
    		@Override
    		public void onCloseWindow(WebView window) {
    			try{
	    			if(dlg!=null && dlg.isShowing())
	    				dlg.dismiss(); 
	    		}
	        	catch(Exception e){
	        		e.printStackTrace();
	        	}
    			dismissed=true;
    		}
    	});
    	
    	webview.setWebViewClient(new WebViewClient() {
            
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
			    return true;
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				if(pDialog!=null && pDialog.isShowing())
					pDialog.dismiss();
			}
			
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		        super.onReceivedError(view, errorCode, description, failingUrl);	
		        view.loadUrl("file:///android_asset/www/error.html");
		    }
    	
    	});    	
    	
    	if(savedInstanceState==null){
	    	pDialog = ProgressDialog.show(activity, "Bangla FB Status", 
	                "Loading... Please wait...", true);
	    	pDialog.setIcon(R.drawable.ic_launcher);
    	}
	}
	
	public class JavaScriptInterface {
	    Context mContext;

	    /** Instantiate the interface and set the context */
	    JavaScriptInterface(Context c) {
	        mContext = c;
	    }

	    /** Show a toast from the web page */
	    public String showToast(String toast) {
	        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	        return toast;
	    }
	    
	    public void showSpinner(String message,String title){
	    	pDialog = ProgressDialog.show(activity, title, 
	    			message, true);
	    }
	    
	    public void hideSpinner(){
	    	try{
	    		if(pDialog!=null && pDialog.isShowing())
	    			pDialog.dismiss();
	    	}
	    	catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }
	    
	    public boolean viewUrl(String url){
	    	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	    	return false;
	    }
	    
	    public boolean showApps(){
	    	startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("market://search?q=pub:Codelixir+Lab")));
	    	return false;
	    }
	    
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
	    
	    public void exitApp(){
	    	finish();
	    }
	    
	}
	
	public void toast(final String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}	
	
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}

}