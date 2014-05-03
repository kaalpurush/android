package com.codelixir.scover;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	boolean wasScreenOn = true;

	protected static final String TAG = "SCover";

	
	 protected void onCreate(Bundle savedInstanceState) {	  
		 	super.onCreate(savedInstanceState);
	        Intent i0 = new Intent(); 
	        i0.setAction("com.codelixir.scover.AEScreenOnOffService");
	        startService(i0);	
	 }
	 
	private String getSetting(String key,String def){
    	SharedPreferences settings = getSharedPreferences("Settings", 0);
    	return settings.getString(key, def); 
    }
    
	private Boolean putSetting(String key, String value){
    	SharedPreferences settings = getSharedPreferences("Settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }
	

	public void toast(final String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}
	
	
}
