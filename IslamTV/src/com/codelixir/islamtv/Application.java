package com.codelixir.islamtv;

import android.os.Environment;
import httpimage.*;

public class Application extends android.app.Application {
	
	private HttpImageManager mHttpImageManager; 

	public static final String BASEDIR = Environment.getExternalStorageDirectory().getPath()+"/tmp";
	
	public static boolean splashFinished=false;	
	
	@Override
	public void onCreate() {
		super.onCreate();

		// init HttpImageManager manager.
		mHttpImageManager = new HttpImageManager(HttpImageManager.createDefaultMemoryCache(), 
				new FileSystemPersistence(BASEDIR));
	}

	
	public HttpImageManager getHttpImageManager() {
		return mHttpImageManager;
	}

	
}
