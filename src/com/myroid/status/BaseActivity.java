package com.myroid.status;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends Activity {

	YambaApplication yamba;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		yamba = (YambaApplication)getApplication();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case R.id.itemPrefs:
			i = new Intent(this, PrefsActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(i);
			break;
		case R.id.itemStartService:
			i = new Intent(this, UpdaterService.class);
			startService(i);
			break;
		case R.id.itemStopService:
			i = new Intent(this, UpdaterService.class);
			stopService(i);
			break;
		case R.id.itemStatus:
			i = new Intent(this, StatusActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(i);
			break;
		case R.id.itemTimeline:
			i = new Intent(this, TimelineActivity2.class);
			i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
				.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(i);
			break;
		}
		return true;
	}
}
