package com.myroid.status;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class TimelineActivity3 extends BaseActivity {

	Cursor cursor;
	ListView listTimeline;
	SimpleCursorAdapter adapter;
	
	IntentFilter filter;
	TimelineReceiver receiver;
	
	static final String[] FROM = {
		StatusData.C_CREATED_AT,
		StatusData.C_USER,
		StatusData.C_TEXT
	};
	
	static final int[] TO = {
		R.id.textCreatedAt,
		R.id.textUser,
		R.id.textText
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);
		
		listTimeline = (ListView)findViewById(R.id.listTimeline);
		
		filter = new IntentFilter(UpdaterService.NEW_STATUS_INTENT);
		receiver = new TimelineReceiver();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();

		cursor = yamba.getStatusData().getStatusUpdates();
		startManagingCursor(cursor);
		
		adapter = new TimelineAdapter(this, cursor);
		listTimeline.setAdapter(adapter);
		
		// Register the receiver
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		unregisterReceiver(receiver);
	}

	class TimelineReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			cursor.requery();
			adapter.notifyDataSetChanged();
			Log.d("TimelineReceiver", "onReceived");
		}
	}
	
	public class ImageThread implements Runnable {

		private ImageView mImage;
		private String mUrl;
		public ImageThread(ImageView imageView, String imageUrl) {
			this.mImage = imageView;
			this.mUrl = imageUrl;
		}
		
		@Override
		public void run() {
			try {
				URL url = new URL(mUrl);
				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				if (conn != null) {
					Drawable image = Drawable.createFromStream(conn.getInputStream(), 
							"src");
					mImage.setImageDrawable(image);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
