package com.myroid.status;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class TimelineActivity2 extends BaseActivity {

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

		loadListView();
		
		// Register the receiver
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		unregisterReceiver(receiver);
	}
	
	private void loadListView() {
		cursor = yamba.getStatusData().getStatusUpdates();
		startManagingCursor(cursor);
		
		adapter = new TimelineAdapter(this, cursor);
		listTimeline.setAdapter(adapter);
	}

	class TimelineReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			loadListView();
			Log.d("TimelineReceiver", "onReceived");
		}
	}
}
