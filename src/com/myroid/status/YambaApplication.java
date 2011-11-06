package com.myroid.status;

import java.net.URI;
import java.util.List;

import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.Status;
import winterwell.jtwitter.Twitter;
import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

public class YambaApplication extends Application 
	implements OnSharedPreferenceChangeListener {
	
	private static final String OAUTH_KEY = "t3u5Ewni1jYDLkKhgLBQ";
	private static final String OAUTH_SECRET = "tWT9YBw82vUifeDWSfAw3BXhEBIG3GLyqZPpDaahKHk";
	
	private static final String ACCESS_TOKEN = "111621749-3qIpEECdEkZW4089GxVxqsc16q5BNnIqsCCa46FV";
	private static final String ACCESS_TOKEN_SECRET = "yJjjWCAFEH0GcbmdbSYxhiwMNzOm3JJ5EjHq9pseUo";
	
	private OAuthSignpostClient oauthClient;
	
	// Log의 태그 설정
	private static final String TAG = 
			YambaApplication.class.getSimpleName();
	
	public Twitter twitter;
	private SharedPreferences prefs;
	
	private boolean serviceRunning;
	
	private StatusData statusData;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		oauthClient = 
        		new OAuthSignpostClient(OAUTH_KEY, 
        				OAUTH_SECRET, 
        				ACCESS_TOKEN, 
        				ACCESS_TOKEN_SECRET);
		
		prefs = 
			PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		
		Log.i(TAG, "onCreate");
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.i(TAG, "onTerminate");
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Log.i(TAG, "onSharedPreferenceChanged");
	}
	
	public synchronized Twitter getTwitter() {
		if (twitter == null) {
			String username = prefs.getString("username", null);			
			twitter = new Twitter(username, oauthClient);
		}
		return twitter;
	}
	
	public boolean isServiceRunning() {
		return this.serviceRunning;
	}
	
	public void setServiceRunning(boolean serviceRunning) {
		this.serviceRunning = serviceRunning;
	}
	
	public StatusData getStatusData() {
		if (this.statusData == null) {
			this.statusData = new StatusData(this);
		}
		return this.statusData;
	}
	
	/**
	 * 
	 * @return count updated status count
	 */
	public int fetchStatusUpdates() {
		Log.d(TAG, "fetching status updates");
		Twitter twitter = this.getTwitter();
		
		if (twitter == null) {
			Log.d(TAG, "Twitter connection info not initialized");			
			return 0;
		}
		
		List<Status> statusUpdates = twitter.getHomeTimeline();
		long latestStatusCreatedAtTime = 
				this.getStatusData().getLatestStatusCreatedAtTime();
	
		int count = 0;			
		ContentValues values = new ContentValues();
		for (Status status : statusUpdates) {
			values.put(StatusData.C_ID, status.getId().intValue());
			long createdAt = status.getCreatedAt().getTime();
			values.put(StatusData.C_CREATED_AT, createdAt);
			values.put(StatusData.C_TEXT, status.getText());				
			values.put(StatusData.C_USER, status.getUser().getName());	
			URI uri = status.getUser().getProfileImageUrl();
			values.put(StatusData.C_USER_IMG, uri.toString());
			Log.d(TAG, "Got update with id " + 							
					status.getId() + ". Saving");				
			this.getStatusData().insertOrIgnore(values);				
			if (latestStatusCreatedAtTime < createdAt) {					
				count++;				
			}
		}
		Log.d(TAG, count > 0 ? "Got " + count + " status updates" 					
				: "No new status updates");			
		return count;
	}
}
