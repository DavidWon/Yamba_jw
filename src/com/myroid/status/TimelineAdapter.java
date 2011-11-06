package com.myroid.status;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TimelineAdapter extends SimpleCursorAdapter {

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
	
	public TimelineAdapter(Context context, Cursor c) {
		// super(context, R.layout.row, c, FROM, TO);
		super(context, R.layout.row_prof, c, FROM, TO);
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		super.bindView(view, context, cursor);
		
		long timestamp = cursor.getLong(
				cursor.getColumnIndex(
						StatusData.C_CREATED_AT));
		TextView textCreatedAt = 
				(TextView)view.findViewById(R.id.textCreatedAt);
		textCreatedAt.setText(
				DateUtils.getRelativeTimeSpanString(timestamp));
		
		String imageUrl = cursor.getString(
			cursor.getColumnIndex(StatusData.C_USER_IMG));
		ImageView imageView = (ImageView)view.findViewById(R.id.imageProfile);
		
		new Handler().post(new ImageThread(imageView, imageUrl));
	}
	
	class ImageThread implements Runnable {

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
