package com.example.bibelvers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class DisplayBibleVerse extends Activity {
	
	private static final String DEBUG_TAG = "HttpExample";
	public final String DOWNLOADFROM = "http://www.biblegateway.com/votd/get/?format=json";
	public TextView textView;
	public String completeVerse = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_bible_verse);
		
		textView = (TextView) findViewById(R.id.bible_verse);
		setupActionBar();
		GetVerse();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void GetVerse() {
		// Check if connected to the Internet.
		// Starts downloading process.
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isConnected()) {
			new MyAsyncClass().execute(DOWNLOADFROM);
		} else {
			textView.setText("No network connection available.");
		}
	}

	private class MyAsyncClass extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			// Initiate download from url.
			try {
				return downloadUrl(urls[0]);
			} catch (IOException e) {
				return "Unable to retrieve web page. URL may be invalid";
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			// Sets text in textView.
			completeVerse = result;
			textView.setText(result);
		}
		
		private String downloadUrl (String myurl) throws IOException {
			// Opens connection to URL and downloads.
			InputStream is = null;
			
			try {
				URL url = new URL(myurl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(15000);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				// Starts the query
				
				conn.connect();
				int response = conn.getResponseCode();
				Log.d(DEBUG_TAG, "The response is " + response);
				is = conn.getInputStream();
				
				// Convert the InputStream into a string
				String contentAsString = readIt(is);
				return contentAsString;
			} finally {
				if(is != null) {
					is.close();
				}
			}
		}
		
		public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
			// Downloads the parts of the text I need.
			String str = "";
			BufferedReader buffer = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			
			while((str = buffer.readLine()) != null) {
				if(str.indexOf("content") > -1) {
					String temp = str;
					String bibelVerse = getBibleVerse(temp);
					return bibelVerse;
				}
			}
			return "No content found.";
		}
	}
	
	public String getBibleVerse(String rawJson) {
		JSONObject json = null;
		/* 
		  * Expected format:
		   * {"votd":{
		   *   "text": "...",
		   *   "content": "...",
		   *   "care",
		   *  }
		   * }
		   */
		try {
			json = new JSONObject(rawJson);
			JSONObject votd = json.getJSONObject("votd");
			return votd.getString("content");
		} catch (JSONException ex) {
			Log.e("WUT", "WHY!?");
			return null;
		} 
	}
	
	public void saveVerse(View view) {
		SQLFeedReader db = new SQLFeedReader(this);
		long test = db.insertVerse(completeVerse);
		if(test > -1) {
			TextView saved = (TextView) findViewById(R.id.verse_saved);
			saved.setText("You bible verse is saved!");
		}
	}
}
