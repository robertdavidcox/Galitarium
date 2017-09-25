package com.robert.starsproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.robert.starsproject.R;

public class SplashScreenActivity extends Activity {
	
	private Context context;
	private boolean databaseFinished = false;
	
	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        this.context = this;
	        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.starview);
	        this.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	        final OpenGLES20Activity a = new OpenGLES20Activity();
	        
	        RelativeLayout splash = (RelativeLayout) findViewById(R.id.splash);
	        splash.bringToFront();
	        boolean newUser = false;

    		//Log.e("TAG", !checkDataBase() + "");
	        
	        Thread welcomeThread = new Thread() {

	        	
	      
	            @Override
	            public void run() {
	            	
            		boolean newUser = false;
	            	try {


	            		/*if (!checkDataBase() ) {
	            			
	            			
	        	            			
	            			newUser = true;
	            			
	            			
	            			Log.e("A", "Database doesn't exist");
	            			
	            		    final Runnable updateRunnable = new Runnable() {
	            		        public void run() {
	            		        	
	            		        	ImageView splash_message = (ImageView) findViewById(R.id.splash_message);
	            		        	splash_message.setImageResource(R.drawable.loading_database_message);
	            		        	splash_message.setVisibility(View.VISIBLE);
	            		        	
	    	            			ProgressBar progress = (ProgressBar) findViewById(R.id.loading_bar_db);
	    	            			progress.setVisibility(View.VISIBLE);
	    	            			
	    	            			RalewayThin r = (RalewayThin) findViewById(R.id.num_stars_message);
	    	            			r.setVisibility(View.VISIBLE);

	            		        }
	            		    };
	            		    
	            			 Activity activity=(Activity) context; 
	            			    activity.runOnUiThread(updateRunnable); 
	            			
	            				try {
	            					StarCollector starCollector = new StarCollector(context);
	            					starCollector.delete();
	            					starCollector.create();
	      

	            					
	            					while (!databaseFinished) {
	            						
	            							
	            					}
	            						
	            					
	            					
	            				} catch (SQLException e1) {
	            					// TODO Auto-generated catch block
	            					e1.printStackTrace();
	            				} catch (IOException e1) {
	            					// TODO Auto-generated catch block
	            					e1.printStackTrace();
	            				} 
	            			} else { */
	            				
	            			//	doesDatabaseExist(context, null);
	            				
	            				Log.e("TAG", "database exists");
	            				//findViewById(R.id.loading_screen_logo).set
	            				
	            				
	            		//}
	            		
	            		
	            	
	                   
	                   // sleep(1000);  //Delay of 10 seconds
	                } catch (Exception e) {
	                	
            			Log.e("A", "exception: " + e.toString());

	                } finally {

	                    Intent i = new Intent(SplashScreenActivity.this,
	                           a.getClass());
	                    i.putExtra("newuser", newUser);
	                    startActivity(i);
	                    finish();
	                }
	            }
	        };
	        welcomeThread.start();
	  }
	  
	  	public void setDatabaseFinished(boolean b) {
		  databaseFinished = b ;
		  
		  Log.e("SPLASH", "database finished: " + b);
	  }
	 
	  	
		private static boolean doesDatabaseExist(Context context, String dbName) throws IOException {
		    File dbFile = context.getDatabasePath("/data/data/com.example.starsproject/databases/stars");
		    InputStream in = new FileInputStream(dbFile);
		    File outFile = new File(Environment.getRootDirectory(), "stars.db");
		    OutputStream out = new FileOutputStream(outFile);
		    copyFile(in, out);
		    Log.e("TAG", "copying file");
		    
		    boolean fileExists = dbFile.exists();
		    
		    if (fileExists) {
		    	return true;
		    	
		    	
		    } else {
		    	return false;
		    }
		    
		}
		
		private static void copyFile(InputStream in, OutputStream out) throws IOException {
		    byte[] buffer = new byte[1024];
		    int read;
		    while((read = in.read(buffer)) != -1){
		      out.write(buffer, 0, read);
		    }
		}
		
		private boolean checkDataBase() 
		     {
		        SQLiteDatabase checkDB = null;
		        try 
		        {
		            String myPath = "/data/data/com.example.starsproject/databases/stars";
		            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		        }catch (Exception e){
		            System.out.println("database does't exist yet.");
		        }

		        if (checkDB != null){
		        	Cursor mCount= checkDB.rawQuery("select count(*) from stars", null);
		        	mCount.moveToFirst();
		        	int count= mCount.getInt(0);
		        	Log.e("TAG", "count is " + count);
		        	
		        	if (count < 119616) {
		        		return false;
		        	}
		        	mCount.close();
		            checkDB.close();
		            System.out.println("My db is:- " + checkDB.isOpen());
		            
		            
		            
		            return true;
		        }
		        else 
		            return false;
		     }

}
