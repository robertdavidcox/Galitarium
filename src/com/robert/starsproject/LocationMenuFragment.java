package com.robert.starsproject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.robert.starsproject.R;

public class LocationMenuFragment extends ListFragment {

	public LocationMenuFragment() {
		
	}
	
	
	
	 @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
	        List<DrawerItem> dataList = new ArrayList<DrawerItem>();	
	        OpenGLES20Activity activity = (OpenGLES20Activity) getActivity();

	        dataList.add(new DrawerItem("Galitarium", "header"));
	        dataList.add(new DrawerItem("Use current location", "item"));
	        dataList.add(new DrawerItem("Set longitude", "item"));
	        dataList.add(new DrawerItem("Set latitude", "item"));
    
	    	CustomDrawerAdapter adapter = new CustomDrawerAdapter(getActivity(), R.layout.custom_drawer_item,
	    			dataList);
	    	
	    	this.setListAdapter(adapter);
	    	

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
        
        
        switch (position) {
        case 1:
        	activity.changeLocationToCurrent();
        	break;
        case 2:
        	DialogFragment longitudePickerFragment = new LongitudePickerFragment();
        	
        	Bundle args = new Bundle();
        	args.putInt("longitude", (int) activity.getLocation().getLongitude());
        	longitudePickerFragment.setArguments(args);
        	
        	longitudePickerFragment.show(getFragmentManager(), "longitudePicker");      
        	longitudePickerFragment.onAttach(activity);
        		
        	break;
        case 3:
        	DialogFragment latitudePickerFragment = new LatitudePickerFragment();
        	
        	Bundle args1 = new Bundle();
        	args1.putInt("latitude", (int) activity.getLocation().getLatitude());
        	latitudePickerFragment.setArguments(args1);
        	
        	latitudePickerFragment.show(getFragmentManager(), "locationPicker");
        	latitudePickerFragment.onAttach(activity);
        		
        	break;
        default:
        	
        	
        	break;

        }

        
	}
	

	
}
