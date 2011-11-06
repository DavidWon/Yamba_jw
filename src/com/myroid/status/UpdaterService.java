package com.myroid.status;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {

	private static final String TAG = "UpdaterService";
	
	private static final int DELAY = (1000*60*10);
	
	private boolean runFlag = false;
	
	private Updater updater;
	
	private YambaApplication yamba;
	
	public static final String NEW_STATUS_INTENT = 
			"com.blogspot.myroid.yamba.NEW_STATUS";
	public static final String NEW_STATUS_EXTRA_COUNT = 
			"com.blogspot.myroid.yamba.NEW_STATUS_COUNT";
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "created!");
		
		updater = new Updater();
		
		yamba = (YambaApplication)getApplication();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "destroyed!");
		runFlag = false;
		
		updater.interrupt();
		
		updater = null;
		yamba.setServiceRunning(false);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.d(TAG, "started!");
		
		runFlag = true;
		
		if (!yamba.isServiceRunning()) {
			// Thread를 시작한다.
			updater.start();
			yamba.setServiceRunning(true);
		}
		
		return START_STICKY;
	}
	
	class Updater extends Thread {
		
		Intent intent;
		
		public Updater() {
			super("updater-thread");
		}
		
		@Override
		public void run() {
			super.run();
			
			Log.d(TAG, "Updater run start");
			
			while (runFlag) {
				try {
					
					int newUpdates = yamba.fetchStatusUpdates();
					if (newUpdates > 0) {
						Log.d(TAG, "We have new updates");
						intent = new Intent(NEW_STATUS_INTENT);
						intent.putExtra(NEW_STATUS_EXTRA_COUNT, newUpdates);
						UpdaterService.this.sendBroadcast(intent);
					}
					
					Log.d(TAG, "Updater ran");
					sleep(DELAY);
					
				} catch (InterruptedException e) {
					runFlag = false;
				}
			}
		}
	}
}
