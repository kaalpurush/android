package com.codelixir.dseliveplus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter{
	
	final int PAGE_COUNT = 3;
	public static IndexFragment indexFragment;
	public static PriceFragment priceFragment;
	public static WatchFragment watchFragment;
	
	/** Constructor of the class */
	public MyFragmentPagerAdapter(FragmentManager fm) {
		super(fm);		
	}

	/** This method will be invoked when a page is requested to create */
	@Override
	public Fragment getItem(int arg0) {
		Bundle data = new Bundle();
		switch(arg0){		
			/** Indexes tab is selected */
			case 0:
				indexFragment = new IndexFragment();				
				data.putInt("current_page", arg0+1);
				indexFragment.setArguments(data);
				return indexFragment;
				
			/** Price tab is selected */
			case 1:
				priceFragment = new PriceFragment();
				data.putInt("current_page", arg0+1);
				priceFragment.setArguments(data);
				return priceFragment;
				
			/** Watch tab is selected */
			case 2:
				watchFragment = new WatchFragment();
				data.putInt("current_page", arg0+1);
				watchFragment.setArguments(data);
				return watchFragment;					
		}
		
		return null;
	}

	/** Returns the number of pages */
	@Override
	public int getCount() {		
		return PAGE_COUNT;
	}
	
}
