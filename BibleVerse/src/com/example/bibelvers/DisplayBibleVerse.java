package com.example.bibelvers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

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
import android.widget.TextView;

public class DisplayBibleVerse extends Activity {
	
	private static final String DEBUG_TAG = "HttpExample";
	public final String DOWNLOADFROM = "http://www.biblegateway.com/votd/get/?format=atom";
	//public final String DOWNLOADFROM = "http://www.google.com/";
	public TextView textView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_bible_verse);
		// Show the Up button in the action bar.
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
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void GetVerse() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isConnected()) {
			new MyAsyncClass().execute(DOWNLOADFROM);
		} else {
			textView.setText("No network connection available.");
		}
	}
	
	public void SaveVerse () {
		
	}

	private class MyAsyncClass extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			
			// params comes from execute() call: params[0] is the url
			try {
				return downloadUrl(urls[0]);
			} catch (IOException e) {
				return "Unable to retrieve web page. URL may be invalid";
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			textView.setText(result);
		}
		
		private String downloadUrl (String myurl) throws IOException {
			InputStream is = null;
			
			try {
				URL url = new URL(myurl);
				System.out.println("DID WE COME HER NO?, -1");

				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				System.out.println("DID WE COME HER NO?, -2");
				conn.setReadTimeout(10000);
				System.out.println("DID WE COME HER NO?, -3");
				conn.setConnectTimeout(15000);
				System.out.println("DID WE COME HER NO?, -4");
				conn.setRequestMethod("GET");
				System.out.println("DID WE COME HER NO?, -5");
				conn.setDoInput(true);
				System.out.println("DID WE COME HER NO?, -23");
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
			//Reader reader = null;
			String str = "";
			//reader = new InputStreamReader(stream, "UTF-8");
			BufferedReader buffer = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			System.out.println("Where's my line!?");
			
			while((str = buffer.readLine()) != null) {
				if(str.indexOf("content") > -1) {
					String temp = str;
					String bibelVerse = manipulateVerse(temp);
					
					System.out.println(temp);
					//str = str.split("]").toString();
					return bibelVerse;
				}
			}
			return "No content found.";
		}
	}
	
	public String manipulateVerse(String verse) {
		int i = 0;
		int j = 0;
		boolean quit = false;
		
		while(i <verse.length() && quit != true){
			if(verse.charAt(i) == ']'){
				verse = verse.substring(i + 3);
				quit = true;
			}
			i++;
		}
		quit = false;
		while(j <verse.length() && quit != true){
			if(verse.charAt(j) == '&'){
				verse = verse.substring(0, j);
				quit = true;
			}
			j++;
		}
		return verse;
	}
}

