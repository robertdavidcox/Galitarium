package com.robert.starsproject;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.robert.starsproject.R;

public class FindPlanetFragment extends ListFragment {
	
	public FindPlanetFragment() {
		
	}
	
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
	        List<DrawerItem> dataList = new ArrayList<DrawerItem>();	       
	        
	        dataList.add(new DrawerItem("Galitarium", "header"));
	        dataList.add(new DrawerItem("Mercury", "item"));
	        dataList.add(new DrawerItem("Venus", "item"));
	        dataList.add(new DrawerItem("Mars", "item"));
	        dataList.add(new DrawerItem("Jupiter", "item"));
	        dataList.add(new DrawerItem("Saturn", "item"));
	        dataList.add(new DrawerItem("Uranus", "item"));
	        dataList.add(new DrawerItem("Neptune", "item"));
	        dataList.add(new DrawerItem("Pluto", "item"));
	        dataList.add(new DrawerItem("Sun", "item"));
	        dataList.add(new DrawerItem("Moon", "item"));
	        
	    	CustomDrawerAdapter adapter = new CustomDrawerAdapter(getActivity(), R.layout.custom_drawer_item,
	    			dataList);
	    	
	    	this.setListAdapter(adapter);
	    	
	
	    //	l.setOnItemClickListener(new FindPlanetItemClickListener());
	    	//LinearLayout linearLayout = (LinearLayout) findViewById(R.id.)


	 }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	

    	
    	return inflater.inflate(R.layout.find_planet_menu, container, false);

    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        selectItem(position);
    }
    
    
	private void selectItem(int position) {
        
        OpenGLES20Activity activity = (OpenGLES20Activity) getActivity();
        
        if (!activity.getRenderer().getAccelerometerStatus()) {
        
        switch (position) {
        case 1:
	        activity.findPlanet(SolarSystemObject.Mercury);
        	break;
        case 2:
        	 activity.findPlanet(SolarSystemObject.Venus);
	        	break;
        case 3:
        	 activity.findPlanet(SolarSystemObject.Mars);
	        	break;
        case 4:
        	 activity.findPlanet(SolarSystemObject.Jupiter);
	        	break;
        case 5:
        	 activity.findPlanet(SolarSystemObject.Saturn);
	        	break;
        case 6:
        	 activity.findPlanet(SolarSystemObject.Uranus);
	        	break;
        case 7:
        	 activity.findPlanet(SolarSystemObject.Neptune);
	        	break;
        case 8:
        	 activity.findPlanet(SolarSystemObject.Pluto);
	        	break;
        case 9:
        	 activity.findPlanet(SolarSystemObject.Sun);
	        	break;
        case 10:
       	 activity.findPlanet(SolarSystemObject.Moon);
	        	break;
      //  case R.id.find_sun:
        //	 activity.findPlanet(SolarSystemObject.Moon);
	        	//break;
        default:
        	break;

        }
        
		FragmentManager fragmentManager = getFragmentManager();
		Fragment f = fragmentManager.findFragmentByTag("one");
		
		Log.e("TAG", "count is " + fragmentManager.getBackStackEntryCount());
		
		if (fragmentManager.getBackStackEntryCount() > 0) {
			
			
			fragmentManager.popBackStack();
		}
		
        } else {
        	
        	Toast.makeText(activity, "Cannot find planet in accelerometer mode", Toast.LENGTH_LONG).show();
        	
        }
        
      //  FragmentTransaction  ft = getFragmentManager().beginTransaction();
       // ft.remove(getFragmentManager().findFragmentByTag("frag")).commit();
        
	}

	
	
	
}