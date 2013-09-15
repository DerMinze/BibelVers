package com.example.bibelvers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class SQLFeedReader extends SQLiteOpenHelper {
	private static final String TAG = "SQLFeedReader";
	
	private static final String TABLE_VERSES = "verses";
	private static final String COLUMN_ID = "id";
    private static final String COLUMN_VERSE = "verse";
    //private static final String COLUMN_DATE = "date";
    
    private static final String DB_NAME = "bibelverses.db";
    private static final int DB_VERSION = 1;
    
    private static final String DB_CREATE =
    					"create table if not exists "
    					+ TABLE_VERSES + "("
    					+ COLUMN_ID + " integer primary key autoincrement, "
    					+ COLUMN_VERSE + " REAL "
    					//+ COLUMN_DATE + " CURRENT_DATE"
    					+ ");";
    
    public SQLFeedReader(Context context) {
    	super(context, DB_NAME, null, DB_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase database) {
    	database.execSQL(DB_CREATE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    	
    }
    
    public long insertVerse(String verse) {
    	Log.i(TAG, "Attempting to insert verse: " + verse);
    	
    	final ContentValues values = new ContentValues();
    	values.put(COLUMN_VERSE, verse);
    	
    	final SQLiteDatabase db = getWritableDatabase();
    	long insertId = db.insert(TABLE_VERSES, null, values);
    	db.close();
    	
    	if(insertId == -1) {
    		Log.e(TAG, "failed to insert to dabase!");
    	}
    	
    	return insertId;
    }
}
