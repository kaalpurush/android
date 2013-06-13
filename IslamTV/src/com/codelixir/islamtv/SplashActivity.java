package com.codelixir.islamtv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.widget.ImageView;

public class SplashActivity extends Activity {	
    
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message hmsg) {
			if (hmsg.what == 1) {
				if(!Application.splashFinished)
					closeSplash();
			}
		}		
	};
	
	public void closeSplash(){
		Application.splashFinished=true;
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
		finish();   
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
    	setContentView(R.layout.splash2);
	    final ImageView splashImageView = (ImageView) findViewById(R.id.SplashImageView);
	    splashImageView.setImageResource(R.drawable.splash);
	    Application.splashFinished=false;
	    mHandler.sendEmptyMessageDelayed(1, 2000);  	
	}
	
    /**
     * Processes splash screen touch events
     */
    @Override
    public boolean onTouchEvent(MotionEvent evt)
    {
    	if(evt.getAction() == MotionEvent.ACTION_DOWN)
    	{
    		closeSplash();
    	}
    	return true;
    }
	

}
