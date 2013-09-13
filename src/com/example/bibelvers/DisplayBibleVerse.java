package com.example.bibelvers;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.sax.Element;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayBibleVerse extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_bible_verse);
		
		String tests[] = DownloadRSS("http://www.biblegateway.com/votd/get/?format=atom");
		// Show the Up button in the action bar.
		setupActionBar();
		
		DisplayVerse(tests);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_bible_verse, menu);
		return true;
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
	
	private void DisplayVerse(String another[]) {
		Intent intent = new Intent(this, DisplayBibleVerse.class);
		TextView textView = (TextView) findViewById(R.id.bible_verse);
		intent.putExtra(EXTRA_MESSAGE, another[0]);
		startActivity(intent)
	}
	
	private String[] DownloadRSS(String URL){
        InputStream in = null;
        String[] RSSTitles = new String[0];
        
        try {
            in = OpenHttpConnection(URL);
            Document doc = null;
            DocumentBuilderFactory dbf = 
                DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            
            try {
                db = dbf.newDocumentBuilder();
                doc = db.parse(in);
            } catch (ParserConfigurationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SAXException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }        
            
            doc.getDocumentElement().normalize(); 
            
            //---retrieve all the <item> nodes---
            NodeList itemNodes = doc.getElementsByTagName("item"); 
            
            String strTitle = "";
            RSSTitles = new String[itemNodes.getLength()];
            for (int i = 0; i < itemNodes.getLength(); i++) { 
                Node itemNode = itemNodes.item(i); 
                if (itemNode.getNodeType() == Node.ELEMENT_NODE) 
                {            
                    //---convert the Node into an Element---
                    Element itemElement = (Element) itemNode;
                    
                    //---get all the <title> element under the <item> 
                    // element---
                    NodeList titleNodes = 
                        ((org.w3c.dom.Element) itemElement).getElementsByTagName("title");
                    
                    //---convert a Node into an Element---
                    Element titleElement = (Element) titleNodes.item(0);
                    
                    //---get all the child nodes under the <title> element---
                    NodeList textNodes = 
                        ((Node) titleElement).getChildNodes();
                    
                    //---retrieve the text of the <title> element---
                    strTitle = ((Node) textNodes.item(0)).getNodeValue();
                    RSSTitles[i]=strTitle;
                    /*---display the title---
                    Toast.makeText(getBaseContext(),strTitle, 
                        Toast.LENGTH_LONG).show();*/
                } 
                
                
            }

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();            
        }
        return RSSTitles;
    }
	
	private InputStream OpenHttpConnection(String urlString) 
		    throws IOException
		    {
		        InputStream in = null;
		        int response = -1;
		               
		        URL url = new URL(urlString); 
		        URLConnection conn = url.openConnection();
		                 
		        if (!(conn instanceof HttpURLConnection))                     
		            throw new IOException("Not an HTTP connection");
		        
		        try{
		            HttpURLConnection httpConn = (HttpURLConnection) conn;
		            httpConn.setAllowUserInteraction(false);
		            httpConn.setInstanceFollowRedirects(true);
		            httpConn.setRequestMethod("GET");
		            httpConn.connect(); 

		            response = httpConn.getResponseCode();                 
		            if (response == HttpURLConnection.HTTP_OK) {
		                in = httpConn.getInputStream();                                 
		            }                     
		        }
		        catch (Exception ex)
		        {
		            throw new IOException("Error connecting");            
		        }
		        return in;     
		    }
	
	public void SaveVerse () {
		
	}

}
