package com.codelixir.dseliveplus;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

public class PortfolioActivity extends SherlockActivity {
	static Activity activity;
	WebView webview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("@","PortfolioActivity.onCreate");
		
		activity=this;

		setContentView(R.layout.portfolio);
		
    	webview = (WebView)activity.findViewById(R.id.webView); 
    	webview.getSettings().setJavaScriptEnabled(true);
    	webview.loadUrl("file:///android_asset/www/portfolio.html");
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
	    public String showToast(String toast) {
	        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	        return toast;
	    }
	    
	    public String getPrice(String code){
	    	return PriceFragment.prices.get(code);
	    }
	    
	    public String getSetting(String key){
	    	SharedPreferences settings = activity.getSharedPreferences("Settings", 0);
	    	//Toast.makeText(getApplicationContext(),settings.getString(key, ""), Toast.LENGTH_SHORT).show();
	    	return settings.getString(key, "[]"); 
	    }
	    
	    public Boolean putSetting(String key, String value){
	    	SharedPreferences settings = activity.getSharedPreferences("Settings", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(key, value);
            return editor.commit();
	    }
	    
	}

}