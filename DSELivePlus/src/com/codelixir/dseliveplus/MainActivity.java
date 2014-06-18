package com.codelixir.dseliveplus;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.view.Window;

import com.google.ads.*;
import com.google.ads.AdRequest.ErrorCode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends SherlockFragmentActivity implements AdListener{

	ViewPager mPager;
	static AlertDialog dlg;
	
	static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message hmsg) {
			if (hmsg.what == 1) {
				dlg.dismiss();
			}
		}		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final ActionBar mActionBar;
		
		Log.d("@","MainActivity.onCreate");
		
		if(!isOnline()) toast("This application requires an active internet connection.");

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

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
		
		/** Getting a reference to action bar of this activity */
		mActionBar = getSupportActionBar();

		/** Set tab navigation mode */
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		/** Getting a reference to ViewPager from the layout */
		mPager = (ViewPager) findViewById(R.id.pager);

		/** Getting a reference to FragmentManager */
		FragmentManager fm = getSupportFragmentManager();

		/** Defining a listener for pageChange */
		ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				mActionBar.setSelectedNavigationItem(position);
			}

		};

		/** Setting the pageChange listner to the viewPager */
		mPager.setOnPageChangeListener(pageChangeListener);

		/** Creating an instance of FragmentPagerAdapter */
		MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(fm);

		/** Setting the FragmentPagerAdapter object to the viewPager object */
		mPager.setAdapter(fragmentPagerAdapter);

		mActionBar.setDisplayShowTitleEnabled(true);

		/** Defining tab listener */
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				mPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
			}
		};

		/** Creating Indexes Tab */
		Tab tab = mActionBar.newTab().setText("Index")
		// .setIcon(R.drawable.android)
				.setTabListener(tabListener);

		mActionBar.addTab(tab);

		/** Creating Prices Tab */
		tab = mActionBar.newTab().setText("Price")
		// .setIcon(R.drawable.apple)
				.setTabListener(tabListener);
		
		mActionBar.addTab(tab);
		
		/** Creating Watch Tab */
		tab = mActionBar.newTab().setText("Watchlist")
				.setTabListener(tabListener);

		mActionBar.addTab(tab);
		
		//Calendar cal = Calendar.getInstance(); 		
		
		//if(savedInstanceState==null && cal.get(Calendar.MONTH)==3)
			//showSplash();
	}

	public void progress(boolean state) {
		//Log.d("progress",String.valueOf(state));
		setSupportProgressBarIndeterminateVisibility(state);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Refresh").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		SubMenu subMenu1 = menu.addSubMenu("Menu");
		subMenu1.add("Portfolio");
		subMenu1.add("About");
		subMenu1.add("Exit");

		MenuItem subMenu1Item = subMenu1.getItem();
		subMenu1Item.setIcon(R.drawable.ic_actionbar_filter);
		subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
				| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("About")) {
			showAbout();
			return true;
		} else if (item.getTitle().equals("Exit")) {
			finish();
			return true;
		} else if (item.getTitle().equals("Portfolio")) {
			Intent myIntent = new Intent(getApplicationContext(), PortfolioActivity.class);
	        startActivity(myIntent);
			return true;			
		} else if (item.getTitle().equals("Refresh")) {
			switch (mPager.getCurrentItem()) {
			case 0:
				MyFragmentPagerAdapter.indexFragment.update();
				break;
			case 1:
			case 2:
				MyFragmentPagerAdapter.priceFragment.update(false);
				break;
			}
			return true;
		}
		return false;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Ask the user if they want to quit
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
		return false;
	}

	public void toast(final String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}

	/**
	 * Show an about dialog that cites data sources.
	 */
	protected void showAbout() {
		// Inflate the about message contents
		View aboutView = getLayoutInflater().inflate(R.layout.about, null,
				false);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(R.string.app_name);
		builder.setView(aboutView);
		builder.show();
	}
	
    public void showApps(View view){
    	startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("market://search?q=pub:Codelixir+Lab")));
    }	
	
	protected void showSplash() {
		// Inflate the about message contents
		View splashView = getLayoutInflater().inflate(R.layout.splash, null,
				false);
		AdView adview=(AdView)splashView.findViewById(R.id.ad);
		adview.setAdListener(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(R.string.app_name);
		builder.setView(splashView);
		builder.setCancelable(false);			
		dlg = builder.create();
		dlg.show();
		mHandler.sendEmptyMessageDelayed(1, 20000);
	}
	
	public void showCompanyInfo(String company) {
		Toast.makeText(this, "Loading..", Toast.LENGTH_LONG).show();
		// Inflate the about message contents
		View companyView = getLayoutInflater().inflate(R.layout.company, null,
				false);
		WebView webview = (WebView)companyView.findViewById(R.id.webView2);
    	webview.loadUrl("http://www.dsebd.org/print_Company.php?name="+company);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(company);
		builder.setView(companyView);
		builder.show();
	}

	public static String match(String nail, String hay) {
		Pattern p = Pattern.compile(nail);
		Matcher m = p.matcher(hay);

		if (m.find())
			return m.group(1).trim();
		return "";
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
	
	@Override
	public void onDismissScreen(Ad arg0) {
		if(dlg!=null && dlg.isShowing())
			dlg.dismiss();
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