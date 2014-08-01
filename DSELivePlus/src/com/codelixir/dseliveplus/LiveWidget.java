package com.codelixir.dseliveplus;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.text.format.Time;
import android.widget.RemoteViews;

/**
 * Define a simple widget that shows the Wiktionary "Word of the day." To build
 * an update we spawn a background {@link Service} to perform the API queries.
 */
public class LiveWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // To prevent any ANR timeouts, we perform the update in a service
        context.startService(new Intent(context, UpdateService.class));
    }

    public static class UpdateService extends Service {
        @Override
        public void onStart(Intent intent, int startId) {
            // Build the widget update for today
            RemoteViews updateViews = buildUpdate(this);

            // Push update for this widget to the home screen
            ComponentName thisWidget = new ComponentName(this, LiveWidget.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(thisWidget, updateViews);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        /**
         * Build a widget update to show the current Wiktionary
         * "Word of the day." Will block until the online API returns.
         */
        public RemoteViews buildUpdate(Context context) {
            RemoteViews views = null;
           
            views = new RemoteViews(context.getPackageName(), R.layout.livewidget);
            
            Time today = new Time();
            today.setToNow();

            views.setTextViewText(R.id.word_title, "Now:"+today.hour+":"+today.minute+":"+today.second);
            views.setTextViewText(R.id.definition, "Definition");
                                
            return views;
        }
        
    	public Boolean putSetting(String key, String value){
        	SharedPreferences settings = getSharedPreferences("Widget", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(key, value);
            return editor.commit();
        }
    	
    	public Boolean deleteSetting(String key) {
    		SharedPreferences settings = getSharedPreferences("Widget", 0);
    		SharedPreferences.Editor editor = settings.edit();
    		editor.remove(key);
    		return editor.commit();
    	}
        
    }
    
    
}
