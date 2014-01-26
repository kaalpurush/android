package com.codelixir.dseliveplus;

import android.net.Uri;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public class PortfolioActivity extends SherlockActivity {
	static Activity activity;
	WebView webview;
	ProgressDialog pDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTheme(R.style.Theme_Dselive);
		
		Log.d("@","PortfolioActivity.onCreate");
		
		activity=this;

		setContentView(R.layout.portfolio);
		
    	webview = (WebView)activity.findViewById(R.id.webView); 
    	webview.getSettings().setJavaScriptEnabled(true);
    	webview.loadUrl("http://dse-live.appspot.com/template/portfolio.html");
    	webview.addJavascriptInterface(new JavaScriptInterface(this), "jsInterface");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu subMenu1 = menu.addSubMenu("Menu");
		subMenu1.add("Buy");

		MenuItem subMenu1Item = subMenu1.getItem();
		subMenu1Item.setIcon(R.drawable.ic_actionbar_filter);
		subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
				| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("Buy")) {
			webview.loadUrl("javascript:show_buy_share();");
			return true;
		}
		return false;
	}
	
	public class JavaScriptInterface {
	    Context mContext;

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
	    public String getPrice(String code){
	    	return PriceFragment.prices.get(code);
	    }
	    
	    @JavascriptInterface
	    public String getSetting(String key){
	    	SharedPreferences settings = activity.getSharedPreferences("Settings", 0);
	    	return settings.getString(key, "[]"); 
	    }
	    
	    @JavascriptInterface
	    public Boolean putSetting(String key, String value){
	    	SharedPreferences settings = activity.getSharedPreferences("Settings", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(key, value);
            return editor.commit();
	    }
	    
	    @JavascriptInterface
	    public void showSpinner(String message,String title){
	    	pDialog = ProgressDialog.show(activity, title, 
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
	    public void exitApp(){
	    	finish();
	    }
	    
	}

}