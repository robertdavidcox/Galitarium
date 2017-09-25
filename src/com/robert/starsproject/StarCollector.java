package com.robert.starsproject;


import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.robert.starsproject.StarContract.StarEntry;

public class StarCollector {
	
	private StarDbHelper starDbHelper;
	private SQLiteDatabase db;
	private Context ctx;
	
	
	public StarCollector(Context ctx) throws SQLException, IOException {
		
		this.ctx = ctx;
		
		starDbHelper = new StarDbHelper(ctx);
		db = starDbHelper.getWritableDatabase();
		

	}
	
	public boolean create() {
		
		starDbHelper.onCreate(db);
		return starDbHelper.initialise(db);
			
	}
	
	public void delete() {
		starDbHelper.clear(db);
	}

	
	public boolean inTransaction() {
		return db.inTransaction();
	}

	public Cursor read() {
		
		String[] projection = {
				StarEntry._ID,
				StarEntry.COLUMN_NAME_X,
				StarEntry.COLUMN_NAME_Y,
				StarEntry.COLUMN_NAME_Z,
				StarEntry.COLUMN_NAME_RA,
				StarEntry.COLUMN_NAME_DEC,
		 		StarEntry.COLUMN_NAME_COLOR_INDEX,
		 		StarEntry.COLUMN_NAME_VISUAL_MAG,
		 		StarEntry.COLUMN_NAME_DISTANCE,
		 		StarEntry.COLUMN_NAME_BAYER_NAME,
		 		StarEntry.COLUMN_NAME_PROPER_NAME
		};
		
		String sortOrder = 
				StarEntry.COLUMN_NAME_VISUAL_MAG + " ASC";
		
		Cursor c = db.query(
			    StarEntry.TABLE_NAME,  					  // The table to query
			    projection,                               // The columns to return
			    null,                                // The columns for the WHERE clause
			    null,                            // The values for the WHERE clause
			    null,                                     // don't group the rows
			    null,                                     // don't filter by row groups
			    sortOrder                                 // The sort order
			    );
		
		return c;
		
	} 
	
	public Cursor starsInView(float xMin, float xMax, float yMin, float yMax) {
		
		
		Cursor c = db.rawQuery(
                   "SELECT * FROM " + StarEntry.TABLE_NAME + " where "
                   + StarEntry.COLUMN_NAME_X + " > " + xMin + " AND "
                   + StarEntry.COLUMN_NAME_X + " > " + xMax + " AND "
                   + StarEntry.COLUMN_NAME_Y + " > " + yMin + " AND "
                   + StarEntry.COLUMN_NAME_Y + " > " + yMax + " SORT BY "
                   + StarEntry.COLUMN_NAME_VISUAL_MAG + " DESC"
                   , null);
		
		return c;
		
		
	}
	

	

	

	
	

	


}
