package com.codelixir.dseliveplus;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class SymbolAdapter extends ArrayAdapter<Symbol> implements Filterable {

    private ArrayList<Symbol> items;
    private ArrayList<Symbol> orig_items=null;
    private final Context context;

    public SymbolAdapter(Context context, int textViewResourceId, ArrayList<Symbol> items) {
            super(context, textViewResourceId, items);
            this.items = items;
            this.context=context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_symbol, null);
            }
            Symbol o=null;
            try{
            	o = items.get(position);
            }
            catch(Exception e){
            	
            }
            if (o != null) {
                    TextView tt = (TextView) v.findViewById(R.id.toptext);
                    TextView pt = (TextView) v.findViewById(R.id.pricetext);
                    TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                    ImageView iv = (ImageView) v.findViewById(R.id.icon); 
                    if (tt != null) {
                          tt.setText(o.code);                            
                    }
                    if (pt != null) {
                        pt.setText(o.lastprice);                            
                    }
                    if(bt != null){
                          bt.setText("YCP: "+o.ycp+" Change: "+o.change+" ("+o.pchange+"%)"+"\nOpen: "+o.open+" High: "+o.high+" Low: "+o.low);
                    }
                    if (iv != null) {
                        iv.setImageResource(o.indicator);                            
                  }
            }
            return v;
    }
    
    @Override
    public int getCount() {
    	// TODO Auto-generated method stub
    	return items.size();
    }
    
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {
            	items = (ArrayList<Symbol>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults(); // Holds the results of a filtering operation in values
                ArrayList<Symbol> filtered_items = new ArrayList<Symbol>();

                if (orig_items == null) {
                    orig_items = new ArrayList<Symbol>(items); // saves the original data in mOriginalValues
                }

                /********
                 * 
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)  
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {
                    // set the Original result to return  
                    results.count = orig_items.size();
                    results.values = orig_items;
                } else {
                    constraint = constraint.toString().toUpperCase();
                    for (int i = 0; i < orig_items.size(); i++) {
                        Symbol item = orig_items.get(i);
                        if (item.code.startsWith(constraint.toString())) {
                        	//Log.d("Adding","item");
                            filtered_items.add(item);
                        }
                    }
                    // set the Filtered result to return
                    results.count = filtered_items.size();
                    results.values = filtered_items;
                    //Log.d("results.count",String.valueOf(results.count));
                    
                }
                return results;
            }
        };
        return filter;
    }//Filter
    
}    