package com.codelixir.deshitv;

import android.os.Environment;
import httpimage.*;

public class Application extends android.app.Application {
	
	private HttpImageManager mHttpImageManager; 

	public static final String BASEDIR = Environment.getExternalStorageDirectory().getPath()+"/tmp";
	
	public static boolean splashFinished=false;
	
	public static String adUnitId="";
	
	@Override
	public void onCreate() {
		super.onCreate();

		// init HttpImageManager manager.
		mHttpImageManager = new HttpImageManager(HttpImageManager.createDefaultMemoryCache(), 
				new FileSystemPersistence(BASEDIR));
		
		AdMan adman=new AdMan(this,getApplicationContext().getPackageName());
		adUnitId=adman.getID();
	}

	
	public HttpImageManager getHttpImageManager() {
		return mHttpImageManager;
	}

	
}
