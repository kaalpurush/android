package com.codelixir.islamtv;

import httpimage.HttpImageManager;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class ChannelAdapter extends ArrayAdapter<Channel> implements Filterable {

	private ArrayList<Channel> items;
	private final Activity context;
	private HttpImageManager mHttpImageManager;

	public ChannelAdapter(Activity context, int textViewResourceId,
			ArrayList<Channel> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.context = context;
		mHttpImageManager = ((Application) context.getApplication()).getHttpImageManager();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_channel, null);
		}
		Channel o = null;
		try {
			o = items.get(position);
		} catch (Exception e) {

		}
		if (o != null) {
			TextView ln = (TextView) v.findViewById(R.id.name);
			TextView lu = (TextView) v.findViewById(R.id.url);
			ImageView li = (ImageView) v.findViewById(R.id.icon);
			TextView ld = (TextView) v.findViewById(R.id.description);
			TextView lr = (TextView) v.findViewById(R.id.rating);
			TextView lw = (TextView) v.findViewById(R.id.website);

			if (ln != null) {
				ln.setText(o.name);
			}
			if (lu != null) {
				lu.setText(o.url);
			}
			if (li != null && o.icon!=null &&  o.icon.trim()!="") {
				try{
					Bitmap bitmap = mHttpImageManager.loadImage(new HttpImageManager.LoadRequest(Uri.parse(o.icon), li));
					if (bitmap != null) {
						li.setImageBitmap(bitmap);
				    }
				}catch(Exception e){}
			}
			if (ld != null) {
				ld.setText(o.description);
			}
			if (lr != null) {
				lr.setText(o.rating);
			}
			if (lw != null) {
				lw.setText(o.website);
			}

		}
		return v;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

}