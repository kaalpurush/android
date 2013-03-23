package com.codelixir.dseliveplus;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;


import com.actionbarsherlock.app.SherlockListFragment;

public class WatchFragment extends SherlockListFragment{
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        /** Creating array adapter to set data in listview */

        /** Setting the array adapter to the listview */
        setListAdapter(PriceFragment.watch_s_adapter);
        
        return super.onCreateView(inflater, container, savedInstanceState);

    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	registerForContextMenu(getListView());
    	super.onActivityCreated(savedInstanceState);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	MenuInflater inflater = getActivity().getMenuInflater();
    	inflater.inflate(R.menu.price, menu);
    	menu.getItem(0).setVisible(false);
    	menu.getItem(2).setVisible(false);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Symbol symbol=PriceFragment.watch_s_adapter.getItem(info.position);
		String code=symbol.code;
		
    	switch (item.getItemId()) {
    		case R.id.remove_from_watch:	 
    			Log.d("Company","fgfg");
	    		if(PriceFragment.watchlist.contains(code)){   		
	    			PriceFragment.watchlist.remove(code);
	
		            SharedPreferences settings = getActivity().getSharedPreferences("Settings", 0);
		            SharedPreferences.Editor editor = settings.edit();
		            editor.putString("watchlist", PriceFragment.watchlist.toString());
		            editor.commit();
		            
		            PriceFragment.watch_symbols.remove(symbol);
		            PriceFragment.watch_s_adapter.notifyDataSetChanged();   
	            }
    		return true;
    		case R.id.show_company_info2:
    			((MainActivity)getActivity()).showCompanyInfo(code);
    		return true;
    	}
    	return super.onContextItemSelected(item);
    }
    
}