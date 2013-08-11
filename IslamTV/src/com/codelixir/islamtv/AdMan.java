package com.codelixir.islamtv;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class AdMan {
	
	Activity activity;
	String appId;
	
	public AdMan(Activity activity, String appId) {
		this.activity=activity;
		this.appId=appId;
	}
	
	public String getID(){
		downloadID();		
		return getSetting("ad_id","");
	}
	
	private void downloadID(){
		String server="http://deshiapp.appspot.com/ad/"+appId;
		DownloadWebPageTask downloader=new DownloadWebPageTask();
    	downloader.execute(new String[] { server });
	}
	
	private String getSetting(String key,String def){
    	SharedPreferences settings = activity.getSharedPreferences("Settings", 0);
    	return settings.getString(key, def); 
    }
    
	private Boolean putSetting(String key, String value){
    	SharedPreferences settings = activity.getSharedPreferences("Settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
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
        }

        @Override
        protected void onPostExecute(String result) { 
        	if(result!="" && result!=null) {
        		putSetting("ad_id", result);
        	}
        }
        
      } 
	
}
