package com.robert.starsproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.robert.starsproject.StarContract.StarEntry;

public class StarDbHelper extends SQLiteAssetHelper {
	
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String TEXT_TYPE = " TEXT";
    private static final String FLOAT_TYPE = " FLOAT";
    private static final String COMMA_SEP = ",";
    private Context context;
    private SQLiteDatabase db;
    
    private static final String STARS_TABLE_CREATE =
            "CREATE TABLE " + StarEntry.TABLE_NAME + " (" +
            		StarEntry._ID + " INTEGER PRIMARY KEY," +
            		StarEntry.COLUMN_NAME_X + FLOAT_TYPE + COMMA_SEP +
            		StarEntry.COLUMN_NAME_Y + FLOAT_TYPE + COMMA_SEP +
            		StarEntry.COLUMN_NAME_Z + FLOAT_TYPE + COMMA_SEP +
            		StarEntry.COLUMN_NAME_RA + FLOAT_TYPE + COMMA_SEP +
            		StarEntry.COLUMN_NAME_DEC + FLOAT_TYPE + COMMA_SEP +
            		StarEntry.COLUMN_NAME_COLOR_INDEX + FLOAT_TYPE + COMMA_SEP +
            		StarEntry.COLUMN_NAME_VISUAL_MAG + FLOAT_TYPE + COMMA_SEP +
            		StarEntry.COLUMN_NAME_DISTANCE + FLOAT_TYPE + COMMA_SEP +
            		StarEntry.COLUMN_NAME_BAYER_NAME + TEXT_TYPE + COMMA_SEP +
            		StarEntry.COLUMN_NAME_PROPER_NAME + TEXT_TYPE +
            " )";
    
    private static final String SQL_DELETE_ENTRIES =
    	    "DROP TABLE IF EXISTS " + StarEntry.TABLE_NAME;

	public StarDbHelper(Context context) {
        super(context, "stars.db", null, DATABASE_VERSION);
        this.context = context;

	}
	

/*	@Override
	public void onCreate(SQLiteDatabase db) {
        db.execSQL(STARS_TABLE_CREATE);
        this.db = db;
	} */
	
	public void clear(SQLiteDatabase db) {
		db.execSQL(SQL_DELETE_ENTRIES);
	}
	
	
	 public class BuildDatabaseTask extends AsyncTask<SQLiteDatabase, Integer, Boolean> {
	     protected Boolean doInBackground(SQLiteDatabase... database) {
	    	 
	    	SQLiteDatabase db = database[0];
	    	 
	 		InputStream inputStream = context.getResources().openRawResource(R.raw.stars);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			
			String line = "";
			String columns = StarEntry._ID + ", " +
					 		 StarEntry.COLUMN_NAME_X + ", " +
					 		 StarEntry.COLUMN_NAME_Y + ", " +
					 		 StarEntry.COLUMN_NAME_Z + ", " +
					 		 StarEntry.COLUMN_NAME_RA + ", " +
					 		 StarEntry.COLUMN_NAME_DEC + ", " +
					 		 StarEntry.COLUMN_NAME_COLOR_INDEX + ", " +
					 		 StarEntry.COLUMN_NAME_VISUAL_MAG + ", " +
					 		 StarEntry.COLUMN_NAME_DISTANCE + ", " +
					 		 StarEntry.COLUMN_NAME_BAYER_NAME + ", " + 
					 		 StarEntry.COLUMN_NAME_PROPER_NAME;

			String str1 = "INSERT INTO " + StarEntry.TABLE_NAME + " (" + columns + ") values(";
			String str2 = ");";

			db.beginTransaction();
			
			int counter = 0;
			try {
				line = reader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				while ((line = reader.readLine()) != null) {
					
					publishProgress((int) ((counter / (float) 118000) * 100), counter);
					
					//Log.e("TAG", "adding to db: " + line);
				    StringBuilder sb = new StringBuilder(str1);
				    String[] str = line.split(",");
				    sb.append("'" + str[0] + "',"); // star id
				    sb.append(str[17] + ", "); // x
				    sb.append(str[18] + ", "); // y
				    sb.append(str[19] + ", "); // z
				    sb.append(str[7] + ", "); // ra
				    sb.append(str[8] + ", "); // dec
				    
				    //Log.e("TAG", "str[16] is " + str[16]);
				    if (str[16].equals(" ") || str[16].equals("") || str[16] == null) {
					//    Log.e("TAG", "null");
				    	sb.append("null, "); //color_index
				    } else {
					    sb.append(str[16] + ", ");
				    }
				    
				    sb.append(str[13] + ", "); // visual mag
				    sb.append(str[9] + ", "); // distance
				    
				    if (str[5].equals(" ") || str[5].equals("") || str[5] == null) {
					 //   Log.e("TAG", "null");
				    	sb.append("null, "); // bayer flamsteed
				    } else {
					    sb.append("'" + str[5].replaceAll("'", "") + "', ");
				    }
				    
				    if (str[6].equals(" ") || str[6].equals("") || str[6] == null) {
					//    Log.e("TAG", "null");
				    	sb.append("null"); // proper name
				    } else {
					    sb.append("'" + str[6].replaceAll("'", "") + "'");
				    }
				    
				    sb.append(str2);
				   // Log.e("TAG", sb.toString());
				    db.execSQL(sb.toString());
				    counter++;
				    
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			db.setTransactionSuccessful(); 
			db.endTransaction(); 
			Log.e("DB", "#################");
			Log.e("DB", "done adding stars");
			Log.e("DB", "#################");
	    	 
	    	 
			
			return true;
	     }

	     protected void onProgressUpdate(Integer... progress) {
	    	 
	    	 Activity a = (Activity) context;
	    	 ProgressBar bar = (ProgressBar) a.findViewById(R.id.loading_bar_db);
	    	 bar.setProgress(progress[0]);
	    	 
	    	 

 			RalewayThin r = (RalewayThin) a.findViewById(R.id.num_stars_message);
 			int left = (119617 - progress[1]);
 			
 			if (left > 1) {
 			
 				r.setText(left + " stars remaining...");
 				
 			} else {
 				
 				r.setText(left + " star remaining...");	
 				
 			}

	     }

	     protected void onPostExecute(Boolean result) {
	    	 
	    	 SplashScreenActivity a = (SplashScreenActivity) context;
	    	 a.setDatabaseFinished(result);
	         
	     }
	 }
	
	
	public boolean initialise(SQLiteDatabase db) {
			
		new BuildDatabaseTask().execute(db);
		return true;
		
		/*InputStream inputStream = context.getResources().openRawResource(R.raw.stars);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		
		String line = "";
		String columns = StarEntry._ID + ", " +
				 		 StarEntry.COLUMN_NAME_X + ", " +
				 		 StarEntry.COLUMN_NAME_Y + ", " +
				 		 StarEntry.COLUMN_NAME_Z + ", " +
				 		 StarEntry.COLUMN_NAME_RA + ", " +
				 		 StarEntry.COLUMN_NAME_DEC + ", " +
				 		 StarEntry.COLUMN_NAME_COLOR_INDEX + ", " +
				 		 StarEntry.COLUMN_NAME_VISUAL_MAG + ", " +
				 		 StarEntry.COLUMN_NAME_DISTANCE + ", " +
				 		 StarEntry.COLUMN_NAME_BAYER_NAME + ", " + 
				 		 StarEntry.COLUMN_NAME_PROPER_NAME;

		String str1 = "INSERT INTO " + StarEntry.TABLE_NAME + " (" + columns + ") values(";
		String str2 = ");";

		db.beginTransaction();
		try {
			line = reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			while ((line = reader.readLine()) != null) {
				//Log.e("TAG", "adding to db: " + line);
			    StringBuilder sb = new StringBuilder(str1);
			    String[] str = line.split(",");
			    sb.append("'" + str[0] + "',"); // star id
			    sb.append(str[17] + ", "); // x
			    sb.append(str[18] + ", "); // y
			    sb.append(str[19] + ", "); // z
			    sb.append(str[7] + ", "); // ra
			    sb.append(str[8] + ", "); // dec
			    
			    //Log.e("TAG", "str[16] is " + str[16]);
			    if (str[16].equals(" ") || str[16].equals("") || str[16] == null) {
				//    Log.e("TAG", "null");
			    	sb.append("null, "); //color_index
			    } else {
				    sb.append(str[16] + ", ");
			    }
			    
			    sb.append(str[13] + ", "); // visual mag
			    sb.append(str[9] + ", "); // distance
			    
			    if (str[5].equals(" ") || str[5].equals("") || str[5] == null) {
				 //   Log.e("TAG", "null");
			    	sb.append("null, "); // bayer flamsteed
			    } else {
				    sb.append("'" + str[5].replaceAll("'", "") + "', ");
			    }
			    
			    if (str[6].equals(" ") || str[6].equals("") || str[6] == null) {
				//    Log.e("TAG", "null");
			    	sb.append("null"); // proper name
			    } else {
				    sb.append("'" + str[6].replaceAll("'", "") + "'");
			    }
			    
			    sb.append(str2);
			   // Log.e("TAG", sb.toString());
			    db.execSQL(sb.toString());
			    
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.setTransactionSuccessful(); 
		db.endTransaction(); 
		Log.e("DB", "#################");
		Log.e("DB", "done adding stars");
		Log.e("DB", "#################");

		return true; */
	}
	

	@Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
	
/*    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }*/
    



}
