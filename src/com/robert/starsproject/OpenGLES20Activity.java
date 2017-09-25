
package com.robert.starsproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.database.SQLException;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.robert.starsproject.DatePickerFragment.OnDatePickedListener;
import com.robert.starsproject.TimePickerFragment.OnTimePickedListener;

@SuppressLint("ResourceAsColor")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class OpenGLES20Activity extends Activity implements OnTimePickedListener,
															OnDatePickedListener,
															SensorEventListener,
															LocationListener,
													        GooglePlayServicesClient.ConnectionCallbacks,
													        GooglePlayServicesClient.OnConnectionFailedListener{

	
    private StarGLSurfaceView mGLView;
    private StarRenderer renderer;
    private StarCollector starCollector;
    
    //LOCATION STUFF
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    // A request to connect to Location Services
    private LocationRequest mLocationRequest;
    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;

	float[] mGravity;
	float[] mGeomagnetic;
    private Calendar localTime;
    private Calendar UTCTime;
    private Location currentLocation;
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    float[] rotationMatrix = new float[16];
    float[] I = new float[16];
    float[] outR = new float[16];
	private Rolling yaws = new Rolling(6);
	private Rolling pitches = new Rolling(6);
	private Rolling rolls = new Rolling(6);
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private boolean isCurrentTime;
    private boolean fetchingLocation = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
       // boolean newUser = getIntent().getExtras().getBoolean("newuser");
        
        setContentView(R.layout.starview);
    
        RelativeLayout splash = (RelativeLayout) findViewById(R.id.splash);
        splash.bringToFront();
        
        RalewayLight splash_title = (RalewayLight) findViewById(R.id.splash_title);
    	splash_title.setVisibility(View.VISIBLE);
        
    	RalewayThin splash_message = (RalewayThin) findViewById(R.id.splash_message);
    	splash_message.setVisibility(View.VISIBLE);
        
     
    	 this.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Font path
        String fontPath = "fonts/ralewaythin.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);

        
        List<DrawerItem> dataList = new ArrayList<DrawerItem>();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setBackgroundColor(getResources().getColor(R.color.main));
      

        dataList.add(new DrawerItem("Galitarium", "header"));
        dataList.add(new DrawerItem("Find planet ", "image", R.drawable.planet_icon));
        dataList.add(new DrawerItem("Change time ", "image", R.drawable.date_time_icon));
        dataList.add(new DrawerItem("Change location ", "image", R.drawable.location_icon));
        dataList.add(new DrawerItem("Settings ", "image", R.drawable.settings_icon));
    	CustomDrawerAdapter adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item,
    			dataList);
    	
    	mDrawerList.setAdapter(adapter);

        mDrawerLayout.setFocusableInTouchMode(false);
        // Set the adapter for the list view

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        try {
			starCollector = new StarCollector(this);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

      
        
        mGLView = new StarGLSurfaceView(this);
        FrameLayout surface = (FrameLayout) findViewById(R.id.content_frame);
        surface.addView(mGLView);
        
        RelativeLayout overlay = (RelativeLayout) findViewById(R.id.overlay);
        overlay.bringToFront();
        
        
        Button resetButton = (Button) findViewById(R.id.reset_button);
     //   resetButton.setBackgroundColor(R.color.secondary);
        resetButton.setTypeface(tf);
     //   resetButton.setTextColor(R.color.const_yellow);
        
        RelativeLayout star_overlay = (RelativeLayout) findViewById(R.id.star_overlay);
        star_overlay.bringToFront();
        
        splash.bringToFront();
        
		UTCTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"),
                Locale.getDefault());
		
		localTime = Calendar.getInstance(TimeZone.getDefault(),
				Locale.getDefault());
		
		
		isCurrentTime = true;
		
        
		
        currentLocation = new Location("A");
    	
        currentLocation.setLongitude(0);
        currentLocation.setLatitude(51); 
        renderer = new StarRenderer(this, (ErrorHandler) mGLView, starCollector, mGLView);
       
        
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean locationServicesOn = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
      //  locationServicesOn = locationServicesOn || lm.getAllProviders().contains(LocationManager.GPS_PROVIDER);
    
        
        if (locationServicesOn) {
        	
        	fetchingLocation = true;
        
        	int code =	GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        
        	if (code == ConnectionResult.SUCCESS) {
       		 
             
        		mLocationRequest = LocationRequest.create();
            
        		mLocationClient = new LocationClient(this, this, this);
        		mLocationClient.connect();

        		mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
        		mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        		mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

 
            
        	
        	} else {
        	
        		Dialog d = GooglePlayServicesUtil.getErrorDialog(code, this, code);
        		d.show();
        	
        	}
       	 
        } else {
        	
        	Toast.makeText(this, "Cannot retrieve current location. Setting " +
        			"current location to London", Toast.LENGTH_LONG).show();
        	

        	
        }
        

       
        
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        
       
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        

        
       // NewUserFragment newUserFragment = new NewUserFragment();
    	//newUserFragment.show(getFragmentManager(), "newuser");
    	//newUserFragment.onAttach(this);
        
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        

        
        if (supportsEs2) {
        	
        	final DisplayMetrics displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        	
        	mGLView.setEGLContextClientVersion(2);
            mGLView.setPreserveEGLContextOnPause(true);
        	mGLView.setRenderer(renderer, displayMetrics.density);
        	
        } else {
        	
        	return;
        	
        }
        
        setLocationBox(currentLocation);
        setDateTimeBox(UTCTime);

	
    }
    
    public void setLocationBox(Location currentLocation) {
    	
		RalewayThin r1 = (RalewayThin) findViewById(R.id.location_box);
		r1.setText("Longitude: " + Location.convert(currentLocation.getLongitude(), Location.FORMAT_SECONDS)
				+ "\nLatitude: " + Location.convert(currentLocation.getLatitude(), Location.FORMAT_SECONDS) );
    	
    	
    }
    
    public void setDateTimeBox(Calendar UTCTime) {
    	
		RalewayThin r = (RalewayThin) findViewById(R.id.date_and_time_box);
		
    	
		r.setText(addZero(UTCTime.get(Calendar.HOUR_OF_DAY)) + ":" +
				  addZero(UTCTime.get(Calendar.MINUTE)) + ":" +
				  addZero(UTCTime.get(Calendar.SECOND)) + " UTC " + 
				  addZero(UTCTime.get(Calendar.DAY_OF_MONTH)) + "/" +
				  addZero((UTCTime.get(Calendar.MONTH) + 1)) + "/" +
				  UTCTime.get(Calendar.YEAR));
    	
    }
    
    private String addZero(int num) {
    	
    	String s = "" + num;
    	
    	if (s.length() == 1) {
    		return "0" + s;
    	} else {
    		return s;
    	}
    	
    	
    	
    }
    
    public void changeLocation(String id, int degrees, int minutes, int seconds) {
    	
    	if (id.equals("lon")) {
    	
    		float lon = (float) (degrees + minutes/60.0 + seconds/3600.0);
        	currentLocation.setLongitude(lon);
    		
    	} else {
    		
    		float lat = (float) (degrees + minutes/60.0 + seconds/3600.0);
        	currentLocation.setLatitude(lat);
    	}
    		

    	
    	renderer.setAlteredLocationTime(true);
    	
    	
    	
    }
    
    public void findPlanet(SolarSystemObject s) {
    	
    	renderer.findPlanet(s);
    	
    	
    } 
    
    public void changeTime() {
    	
    	TimePickerFragment timeFragment = new TimePickerFragment();
    	
    	Bundle args = new Bundle();
    	args.putInt("hour", localTime.get(Calendar.HOUR_OF_DAY));
    	args.putInt("minute", localTime.get(Calendar.MINUTE));
    	timeFragment.setArguments(args);
    	
    	timeFragment.show(getFragmentManager(), "timePicker");
    	timeFragment.onAttach(this);

    }
    
	@Override
	public void onTimePicked(int hour, int minute) {
		
		localTime.set(Calendar.HOUR_OF_DAY, hour);
		localTime.set(Calendar.MINUTE, minute);
	
    	setEquivilantUTC(localTime);
    
    	
    	renderer.setAlteredLocationTime(true);
    	
	}
    
    public void changeDate() {
 	
    	DialogFragment dateFragment = new DatePickerFragment();
    	
    	Bundle args = new Bundle();
    	args.putInt("year", localTime.get(Calendar.YEAR));
    	args.putInt("month", localTime.get(Calendar.MONTH));
    	args.putInt("day", localTime.get(Calendar.DAY_OF_MONTH));
    	dateFragment.setArguments(args);
    	
    	dateFragment.show(getFragmentManager(), "datePicker");
    	
    	dateFragment.onAttach(this);
    	
    	
    }
    
	@Override
	public void onDatePicked(int year, int month, int day) {

    	localTime.set(Calendar.YEAR, year);
    	localTime.set(Calendar.MONTH, month);
    	localTime.set(Calendar.DAY_OF_MONTH, day);
    	
    	setEquivilantUTC(localTime);

    	
    	renderer.setAlteredLocationTime(true);
    	  	
	}
	
	public void setEquivilantUTC(Calendar localTime) {
		
		TimeZone local = TimeZone.getDefault();
		long UTC = UTCTime.getTimeInMillis();
		
		int offset = local.getOffset(UTC)/3600000;
		
		UTCTime.set(Calendar.YEAR, localTime.get(Calendar.YEAR));
		UTCTime.set(Calendar.MONTH, localTime.get(Calendar.MONTH));
		UTCTime.set(Calendar.DAY_OF_MONTH, localTime.get(Calendar.DAY_OF_MONTH));
		UTCTime.set(Calendar.HOUR_OF_DAY, localTime.get(Calendar.HOUR_OF_DAY));
		UTCTime.set(Calendar.MINUTE, localTime.get(Calendar.MINUTE));
		UTCTime.set(Calendar.SECOND, localTime.get(Calendar.SECOND));
		
		UTCTime.roll(Calendar.HOUR_OF_DAY, -offset);
		
	//	Log.e("Activity", "offset is " +offset);
		
		
	}

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
    	
    	int code =	GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    	
        super.onResume();
        
        if (code == ConnectionResult.SUCCESS) {
        	
            mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
            mGLView.onResume();
        	
        } else {
        	
        	Dialog d = GooglePlayServicesUtil.getErrorDialog(code, this, code);
        	d.show();
        	
        }
        

    }
 
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			mGravity = event.values;
		
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
		      mGeomagnetic = event.values;
		
		if (mGravity != null && mGeomagnetic != null) {
			
			
		
			float R[] = new float[9];
			float I[] = new float[9];
			
			boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
			
			if (success) {
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				
				SensorManager.getRotationMatrix(rotationMatrix, I, mGravity, mGeomagnetic);
				SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, outR);
				SensorManager.getOrientation(outR, orientation);
				
				orientation[0] = (float) Math.toDegrees(orientation[0]);
				orientation[1] = - (float) Math.toDegrees(orientation[1]);
				orientation[2] = - (float) Math.toDegrees(orientation[2]);
				
				if (orientation[0] < 0) {
					
					orientation[0] = (360 + orientation[0]);
				}
				

				
				renderer.deltaYaw = (float) (yaws.getAverage());
				renderer.deltaPitch = (float) pitches.getAverage();
				renderer.deltaRoll = (float) rolls.getAverage();
				
				yaws.add(orientation[0]);
				pitches.add(orientation[1]);
				rolls.add(orientation[2]);
				
				//Log.e("TAG", "rotating by (" + yaws.getAverage()*2 + ", " + pitches.getAverage() + ", " + rolls.getAverage() + ")");

				
				
			
				//Log.e("TAG", "yaw: " + orientation[0] + ", pitch: " + orientation[1] + ", roll: " + orientation[2] + ")");
		        	
					//azimut = orientation[0]; // orientation contains: azimut, pitch and roll
			}
		}
		
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
			
		}
	}
	
	private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        String fragID = null;
        
        switch (position) {
        case 1:
        	fragment = new FindPlanetFragment();
        	break;
        case 2:
        	fragment = new DateTimePickerFragment();
        	break;
        case 3:
        	fragment = new LocationMenuFragment();
        	break; 	
        case 4:
        	fragment = new SettingsFragment();
        	break; 	
        default:
        	break;

        }
        
          
        if (fragment != null) {
        	
        	
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "one").addToBackStack(null).commit();
            
   
            mDrawerLayout.closeDrawer(mDrawerList);
            
            
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        } 
        
	}

	@Override
	public void onDrawerSlide(View drawerView, float slideOffset) {
		
		
		Log.e("TAG", "drawer offset: " + slideOffset);
	}
	
	public void onToggleClicked(View view) {
	    // Is the toggle on?
		ToggleButton button = (ToggleButton) view;
	    boolean on = button.isChecked();
	    
	    Log.e("ACTIVITY", "id is " + button.getId());
	    if (button.getId() == 13456) {
	    	
		    if (on) {
		    	renderer.setAccelorometer(true);
		    } else {
		    	renderer.setAccelorometer(false);
		    }

	    }
	    

	}
	
	public StarRenderer getRenderer() {
		return renderer;
	}

	public boolean isCurrentTime() {
		return isCurrentTime;
	}
	
	public void setIsCurrentTime(boolean b) {
		this.isCurrentTime = b;
	}
	
	public void changeTimeToCurrent() {
		
		UTCTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"),
                Locale.getDefault());
		
		localTime = Calendar.getInstance(TimeZone.getDefault(),
				Locale.getDefault());
		
		isCurrentTime = true;
		
		printTime();
	
		
		
    	renderer.setAlteredLocationTime(true);
		
		
    	
		
	}
	
	public void printTime() {

		Log.e("Activity","Local Time is " + + localTime.get(Calendar.HOUR_OF_DAY) + ":"
											+ localTime.get(Calendar.MINUTE) + ":"
											+ localTime.get(Calendar.SECOND) + " on ("
											+ localTime.get(Calendar.DAY_OF_MONTH) + ", "
											+ localTime.get(Calendar.MONTH) + ", "
											+ localTime.get(Calendar.YEAR) + ")");
		
		Log.e("Activity","UTC Time is " + UTCTime.get(Calendar.HOUR_OF_DAY) + ":"
										+ UTCTime.get(Calendar.MINUTE) + ":"
										+ UTCTime.get(Calendar.SECOND) + ", on "
										+ UTCTime.get(Calendar.DAY_OF_MONTH) + ", "
										+ UTCTime.get(Calendar.MONTH) + ", "
										+ UTCTime.get(Calendar.YEAR));
		
	}
	
	public Calendar getUTCTime() {
		return UTCTime;
	}
	
	public Calendar getLocalTime() {
		return localTime;
	}
	
	public Location getCurrentLocation() {
		
		if (currentLocation != null) {
			
			return currentLocation;
		} else {
		
			Location l = new Location("B");
			l.setLatitude(51);
			l.setLongitude(1);
			return l;
		}
	}
	
	public void changeLocationToCurrent() {
		
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean locationServicesOn = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
      //  locationServicesOn = locationServicesOn || lm.getAllProviders().contains(LocationManager.GPS_PROVIDER);
    
        
        if (locationServicesOn) {
		
		currentLocation = getLocation();
		
		if (currentLocation != null) {
			
			
			
			this.runOnUiThread(new Runnable() {
				  public void run() {
					  Toast.makeText(getApplicationContext(), "Latitude: " + currentLocation.getLatitude()
								+ ", longitude: " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
				  }
				});
			
			
	    	renderer.setAlteredLocationTime(true);
			
			
		}
        } else {
        	
        	Toast.makeText(this, "Cannot retrive current location. Please turn " +
        			"on location access in your phone settings", Toast.LENGTH_LONG).show();
        }
		
		
	}
	
	public void resetView(View view) {
		
		Log.e("TAG", "Resetting VIEW");
		Log.e("TAG", "Resetting VIEW");
		Log.e("TAG", "Resetting VIEW");
		Log.e("TAG", "Resetting VIEW");
		renderer.resetView();
		
	}
	
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.e(LocationUtils.APPTAG, "available services");

            return true;
        // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getFragmentManager(), LocationUtils.APPTAG);
                
            }
            return false;
        }
    }
    
	public Location getLocation() {
        // If Google Play Services is available
        if (servicesConnected()) {

            // Get the current location
            Location currentLocation = mLocationClient.getLastLocation();
            
            // Display the current location in the UI
            Log.e("LOCATION", LocationUtils.getLatLng(this, currentLocation));
            
            return currentLocation;
        } else {
        	
        	Location currentLocation = new Location("A");
        	
            currentLocation.setLongitude(0);
            currentLocation.setLatitude(51);
        	
        	return currentLocation;
        }
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		
		Log.e("TAG", "connected");
		
        currentLocation = getLocation();
      //  renderer.se
        renderer.setAlteredLocationTime(true);
		//fetchingLocation = false;
		
	}
	
	@Override
	public void onBackPressed() {
		
		FragmentManager fragmentManager = getFragmentManager();
		Fragment f = fragmentManager.findFragmentByTag("one");
		
		//Log.e("TAG", "count is " + fragmentManager.getBackStackEntryCount());
		
		if (fragmentManager.getBackStackEntryCount() > 0) {
			
			
			fragmentManager.popBackStack();
			
	/*	} else if (mDrawerLayout.isDrawerOpen((DrawerLayout) findViewById(R.id.drawer_layout))) {
			
			mDrawerLayout.close
			
		} */
			
		} else if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			
			mDrawerLayout.closeDrawers();
			
		} else {
		
	    new AlertDialog.Builder(this)
	        .setTitle("Really Exit?")
	        .setMessage("Are you sure you want to exit?")
	        .setNegativeButton(android.R.string.no, null)
	        .setPositiveButton(android.R.string.yes, new OnClickListener() {

	            public void onClick(DialogInterface arg0, int arg1) {
	            	System.exit(0);                                                          
	            }
	        }).create().show();
		}
	
		
		}
		
	public void firstTimeDialog() {
		
		//final Context c = getApplicationContext();
		
	    final Runnable updateRunnable = new Runnable() {
	        public void run() {
	            //call the activity method that updates the UI
	    	    final String PREFS_NAME = "MyPrefsFile";

	    	    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

	    	    if (settings.getBoolean("my_first_time", true)) {
	    	        //the app is being launched for first time, do something        
	    	        Log.d("Comments", "First time");

	    		    new AlertDialog.Builder(OpenGLES20Activity.this)
	    	        .setTitle("Welcome to Galitarium")
	    	        .setMessage("Slide from the left of the screen to reveal the menu")
	    	        .setPositiveButton("OK", new OnClickListener() {

	    	            public void onClick(DialogInterface arg0, int arg1) {
	    	            	
	    	            }
	    	        }).create().show();

	    	        // record the fact that the app has been started at least once
	    	        settings.edit().putBoolean("my_first_time", false).commit(); 
	    	    }
	        }
	           
	    };
	    
	    
		 Activity activity=(Activity) this; 
		    activity.runOnUiThread(updateRunnable); 
		

		
	}
	
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}
	
    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
    
    public boolean isAugmented() {
    	return renderer.getAccelerometerStatus();
    }


  
    
}