/*
 * Copyright (C) 2012 YIXIA.COM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codelixir.deshitv;

import java.util.Calendar;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

public class VideoActivity extends Activity implements AdListener,OnPreparedListener,OnErrorListener{

	private String path;
	private VideoView mVideoView;
	private AdView mAdView;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
			return;

		path = getIntent().getDataString();
		
		//path = "http://devimages.apple.com/iphone/samples/bipbop/gear1/prog_index.m3u8";
		
		setContentView(R.layout.video);
		
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
	
		mVideoView = (VideoView) findViewById(R.id.surface_view);
		mVideoView.setVideoPath(path);
		mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
		MediaController mc= new MediaController(this);
		mVideoView.setMediaController(mc);	
		mVideoView.setOnPreparedListener(this);
		mVideoView.setOnErrorListener(this);
		toast(getResources().getString(R.string.loading));
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub		
		if (mAdView != null) {
			mAdView.destroy();
        }
		super.onDestroy();
	}
	

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (mVideoView != null)
			mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
		super.onConfigurationChanged(newConfig);
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
    
	public void toast(final String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
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
	
	@Override
	public void onPrepared(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		//arg0.start();
	}
	
	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_info)
		.setTitle("Video can't be played")
		.setMessage("Sorry this server is down at the moment. We will be replacing it with a working one soon.")
		.setNeutralButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						finish();
					}
				}).show();
		return true;
	}
	

}
