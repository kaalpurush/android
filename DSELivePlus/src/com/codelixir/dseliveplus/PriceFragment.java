package com.codelixir.dseliveplus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

public class PriceFragment extends SherlockListFragment{
	
	public String json="{}";
	
	private ArrayList<Symbol> symbols = new ArrayList<Symbol>();
	private SymbolAdapter s_adapter;
	private DownloadWebPageTask downloader;
	private TextWatcher txtWatcher;
	
	public Boolean alternate=true;
		
	public static ArrayList<String> watchlist = new ArrayList<String>();
	public static ArrayList<Symbol> watch_symbols = new ArrayList<Symbol>();
	public static SymbolAdapter watch_s_adapter;
	public static Map<String, String> prices= new HashMap<String, String>();
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        /** Creating array adapter to set data in listview */
        s_adapter = new SymbolAdapter(getActivity(), R.layout.list_symbol, symbols);
        watch_s_adapter = new SymbolAdapter(getActivity(), R.layout.list_symbol, watch_symbols);

        /** Setting the array adapter to the listview */
        setListAdapter(s_adapter);
        
        return inflater.inflate(R.layout.price, container, false);
        
        //return super.onCreateView(inflater, container, savedInstanceState);

    } 
    
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString("json", json);
        
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    
    @Override
    public void onAttach(Activity activity) {
    	// TODO Auto-generated method stub
    	super.onAttach(activity);
    	MyFragmentPagerAdapter.priceFragment=PriceFragment.this;
    }
    
    public void update(Boolean alternate) {
    	Log.d("updating", "Price");
    	String server="http://www.stockbangladesh.com/resources/getpricelist";
    	String server2="http://codelixir.com/dseliveplus/price.json";
    	if(alternate)
    		server=server2;
    	else
    		this.alternate=true;
        downloader=new DownloadWebPageTask();
    	downloader.execute(new String[] { server });
	}
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onActivityCreated(savedInstanceState);
    	
    	Log.d("@","PriceFragment.onActivityCreated");
    	EditText editFilter = (EditText)getActivity().findViewById(R.id.editFilter);
	    txtWatcher = new TextWatcher() {

	        public void afterTextChanged(Editable s) {
	        	s_adapter.getFilter().filter(s);   
	        }

	        public void beforeTextChanged(CharSequence s, int start, int count,
	                int after) {
	        	;
	        }

	        public void onTextChanged(CharSequence s, int start, int before,
	                int count) {
	        	;
	        }
	    };
	    editFilter.addTextChangedListener(txtWatcher);
	    if(savedInstanceState==null)
	    	update(false);
	    else{
	    	clearlist();
	    	process_json_symbols(savedInstanceState.getString("json"));
	    }
	    
	    ListView mListView = (ListView) getActivity().findViewById(android.R.id.list);
	    //mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	    //mListView.setItemsCanFocus(false);
	    registerForContextMenu(mListView);
        
        SharedPreferences settings = getActivity().getSharedPreferences("Settings", 0);
        String watchlist = settings.getString("watchlist", "").replace("[", "").replace("]", "");
        
        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(',');
        splitter.setString(watchlist);
        while(splitter.hasNext()){
        	String symbol=splitter.next().trim();
        	if(!PriceFragment.watchlist.contains(symbol))
        		PriceFragment.watchlist.add(symbol);
        }

    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenuInfo menuInfo) {
    	// TODO Auto-generated method stub
    	super.onCreateContextMenu(menu, v, menuInfo);
    	MenuInflater inflater = getActivity().getMenuInflater();
    	inflater.inflate(R.menu.price, menu);
    	menu.getItem(1).setVisible(false);
    	menu.getItem(3).setVisible(false);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {    	
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		TextView tt= (TextView)info.targetView.findViewById(R.id.toptext);		
		String code=tt.getText().toString();
		
		Symbol symbol = null;
		
		Iterator<Symbol> mIterator=symbols.iterator();
		
		while (mIterator.hasNext()) {
			Symbol mSymbol=(Symbol) mIterator.next();	
			if(mSymbol.code.equals(code))
				symbol=mSymbol;
		}
    	
    	switch (item.getItemId()) {
    		case R.id.add_to_watch:    		
	    		if(!watchlist.contains(code)){ 		
	    			watchlist.add(code);
	
		            SharedPreferences settings = getActivity().getSharedPreferences("Settings", 0);
		            SharedPreferences.Editor editor = settings.edit();
		            editor.putString("watchlist", watchlist.toString());
		            editor.commit();
		            
		            watch_symbols.add(symbol);
		            watch_s_adapter.notifyDataSetChanged();   
	            }
    		return true;
    		case R.id.show_company_info:
    			((MainActivity)getActivity()).showCompanyInfo(code);
    		return true;
    	}
    	return super.onContextItemSelected(item);
    }
	
    
    @Override
    public void onStart() {    	
    	super.onStart();
    }	
    
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	try{
    		downloader.cancel(true);
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public void clearlist(){
    	symbols.clear();
    	s_adapter.notifyDataSetChanged();
    	watch_symbols.clear();
    	watch_s_adapter.notifyDataSetChanged();
    }
    
    public void process_json_symbols(String json){
    	this.json=json;
    	JSONObject jObject;
    	try {
	    	json=json.replace("(", "").replace(")", "");
			jObject = new JSONObject(json);
			JSONArray resultArray = jObject.getJSONArray("results");
			
			for (int i=0; i < resultArray.length(); i++)
			{
			    JSONObject oneObject = resultArray.getJSONObject(i);
			    String code = oneObject.getString("code");
			    String lastprice = oneObject.getString("lastprice");
			    String change = oneObject.getString("change");
			    String pchange = oneObject.getString("pchange");
			    String ycp = oneObject.getString("ycp");
			    String open = oneObject.getString("open");
			    String high = oneObject.getString("high");
			    String low = oneObject.getString("low");
			    
			    //Log.d("Code", code);
			    
	            Symbol s = new Symbol();
	            s.code=code;
	            s.indicator=Float.valueOf(change)>0?R.drawable.up:R.drawable.down;
	            s.lastprice=lastprice;
	            s.change=change;
	            s.pchange=pchange;
	            s.ycp=ycp;
	            s.open=open;
	            s.high=high;
	            s.low=low;
	              
	            symbols.add(s);	
	            
	            prices.put(code, lastprice);
	            
	            if(watchlist.contains(code))
	            	watch_symbols.add(s);
			}
			
	        SymbolComparator comparator = new SymbolComparator();
	        
	        Collections.sort(symbols, comparator);
	        Collections.sort(watch_symbols, comparator);
	        
	        s_adapter.notifyDataSetChanged(); 
	        watch_s_adapter.notifyDataSetChanged();
	        
	        ((MainActivity)getActivity()).progress(false);
    	} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
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
            	response="{}";
            }
          }
          return response;
        }
        
        @Override
        protected void onPreExecute() {
        	// TODO Auto-generated method stub
        	super.onPreExecute();
        	((MainActivity)getActivity()).progress(true);
        	clearlist();
        	//Log.d("Starting","download");
        }

        @Override
        protected void onPostExecute(String result) { 
        	if(result=="{}" || result=="" || result==null || !result.startsWith("({") || !result.endsWith("})")) {
        		Toast.makeText(getActivity().getApplicationContext(), "Error while retrieving data from server.\n\nProbable reason: SERVER BUSY", Toast.LENGTH_LONG).show();
        		((MainActivity)getActivity()).progress(false);
        		if(alternate){
        			Toast.makeText(getActivity().getApplicationContext(), "Trying alternate server...\n\nNote: Alternate server is a caching server. It might not always return live data", Toast.LENGTH_SHORT).show();
        			update(true);
        			alternate=false;
        		}
        	}
        	else
        		process_json_symbols(result);
        }
        
      }    
    
}