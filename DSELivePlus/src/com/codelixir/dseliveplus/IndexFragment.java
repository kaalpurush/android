package com.codelixir.dseliveplus;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class IndexFragment extends SherlockFragment{
	
	TextView text;    
	WebView webview;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.index, container, false); 	
    }
    
    public void update() {
    	webview.reload();
    }   
    
    @Override
    public void onAttach(Activity activity) {
    	// TODO Auto-generated method stub
    	super.onAttach(activity);
    	MyFragmentPagerAdapter.indexFragment=IndexFragment.this;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onActivityCreated(savedInstanceState);
    	webview = (WebView)getActivity().findViewById(R.id.webView1); 
    	webview.getSettings().setJavaScriptEnabled(true);
    	webview.loadUrl("file:///android_asset/www/index.html");
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    }
    
}
