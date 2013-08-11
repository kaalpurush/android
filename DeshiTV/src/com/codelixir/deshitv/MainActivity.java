package com.codelixir.deshitv;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
//import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends SherlockListActivity implements AdListener{
	static AlertDialog dlg;
	private DownloadWebPageTask downloader;
	private ArrayList<Channel> channels = new ArrayList<Channel>();
	private ChannelAdapter c_adapter;
	
	static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message hmsg) {
			if (hmsg.what == 1) {
				dlg.dismiss();
			}
		}		
	};
	
	protected void onListItemClick(android.widget.ListView l, View v, int position, long id) {
		TextView lu= (TextView)v.findViewById(R.id.url);		
		String url=lu.getText().toString();
		TextView ln= (TextView)v.findViewById(R.id.name);		
		String name=ln.getText().toString();
		processVideo(url, name);
	};
	
	private void processVideo(String url, String title){
		if(url.contains("jagobd"))
			showWebVideo(url, title);
		else
			showVideo(url, title);
	}
	
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
			
			mAdView.setAdListener(this);
			
			ad_container.addView(mAdView);
			
			AdRequest mAdRequest = new AdRequest();
			
			//mAdRequest.addTestDevice("SH15NTR29817");
			
			mAdView.loadAd(mAdRequest);
			
		}
		
        c_adapter = new ChannelAdapter(this, R.layout.list_channel, channels);        

        /** Setting the array adapter to the listview */
        setListAdapter(c_adapter);		
		
		/** Getting a reference to action bar of this activity */
		mActionBar = getSupportActionBar();

		/** Set tab navigation mode */
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		mActionBar.setDisplayShowTitleEnabled(true);		
		
	    if(savedInstanceState==null)
	    	update();
		
		//Calendar cal = Calendar.getInstance(); 		
		
		//if(savedInstanceState==null && cal.get(Calendar.MONTH)==2)
			//showSplash();
	}

	public void progress(boolean state) {
		setSupportProgressBarIndeterminateVisibility(state);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Refresh").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		SubMenu subMenu1 = menu.addSubMenu("Menu");
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
		} else if (item.getTitle().equals("Refresh")) {
			update();
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
	
    public void update() {
    	Log.d("updating", "Channel");
    	String server="http://deshitv.appspot.com/channel";
        downloader=new DownloadWebPageTask();
    	downloader.execute(new String[] { server });
	}
    
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
          String response="";
          for (String url : urls) {
            try {            	
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
                DefaultHttpClient client = new DefaultHttpClient(httpParameters);            
                HttpGet httpGet = new HttpGet(url);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                response = client.execute(httpGet, responseHandler);
            } catch (Exception e) {
            	e.printStackTrace();
            }
          }
          return response;
        }
        
        @Override
        protected void onPreExecute() {
        	// TODO Auto-generated method stub
        	super.onPreExecute();
        	progress(true);
        	channels.clear();
        	c_adapter.notifyDataSetChanged(); 
        	//Log.d("Starting","download");
        }

        @Override
        protected void onPostExecute(String result) { 
        	if(result=="" || result==null || !result.startsWith("{") || !result.endsWith("}")) {
        		Toast.makeText(getApplicationContext(), "Error retrieving channel list from server.\n\nProbable reason: SERVER BUSY", Toast.LENGTH_LONG).show();
        		progress(false);
        	}
        	else
        		process_json_channels(result);
        }
        
      }    
    
    
    public void process_json_channels(String json){
    	JSONObject jObject;
    	try {
			jObject = new JSONObject(json);
			JSONArray resultArray = jObject.getJSONArray("results");
			channels.clear();
			
			for (int i=0; i < resultArray.length(); i++)
			{
			    JSONObject oneObject = resultArray.getJSONObject(i);
			    String id = oneObject.getString("id");
			    String name = oneObject.getString("name");
			    String url = oneObject.getString("url");
			    String icon = oneObject.getString("icon");
			    String description = oneObject.getString("description");
			    String rating = oneObject.getString("rating");
			    String website = oneObject.getString("website");
			    
	            Channel c = new Channel();
	            c.id=id;
	            c.name=name;
	            c.url=url;
	            c.icon=icon;
	            c.description=description;
	            c.rating=rating;
	            c.website=website;
		              
	            channels.add(c);	
			}
			
	        c_adapter.notifyDataSetChanged(); 
	        
	        progress(false);
    	} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
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
	
    public void showApps(View view){
    	startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("market://search?q=pub:Codelixir+Lab")));
    }	
    
    public void showVideo(String url, String title){
		Intent myIntent = new Intent(getApplicationContext(), VideoActivity.class);
		myIntent.setData(Uri.parse(url));
		myIntent.putExtra("displayName", title);
        startActivity(myIntent);
    }	
    
    public void showWebVideo(String url, String title){
		Intent myIntent = new Intent(getApplicationContext(), WebVideoActivity.class);
		myIntent.setData(Uri.parse(url));
		myIntent.putExtra("displayName", title);
        startActivity(myIntent);
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