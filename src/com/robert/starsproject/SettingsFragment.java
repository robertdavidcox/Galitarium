package com.robert.starsproject;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.ToggleButton;
import com.robert.starsproject.R;

public class SettingsFragment extends ListFragment {
	
	
	public SettingsFragment() {
		
	}
	
	
	 @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
	        List<DrawerItem> dataList = new ArrayList<DrawerItem>();	
	                
	       CompoundButton.OnCheckedChangeListener listener =
	                new CompoundButton.OnCheckedChangeListener() {
	            @Override
	            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	            	OpenGLES20Activity a = (OpenGLES20Activity) getActivity();
	            	
	            	Log.e("TAG", "id: " + buttonView.getId());
	            	Log.e("TAG", "ischecked: " + isChecked);
	           
	                switch(buttonView.getId()) {
	                    case 1001:	                    	
	            		    a.getRenderer().setAccelorometer(buttonView.isChecked());
	            		    break;
	                    case 1002:
	            		    a.getRenderer().setEquatorialStatus(buttonView.isChecked());
	                        break;
	                    case 1003:
	                    	a.getRenderer().setPlanetTexts(buttonView.isChecked());
	                    	break;
	                    case 1004:
	                    	a.getRenderer().setConstellationStatus(buttonView.isChecked());
	                    	break;
	                    default:
	                        return;
	                }
	                // Save the state here using key
	            }
	        };
	        
	        OpenGLES20Activity a = (OpenGLES20Activity) getActivity();
	       
	        
	        CheckBox augmentedToggle = new CheckBox(getActivity());
	        augmentedToggle.setId(1001);
	        augmentedToggle.setChecked(a.isAugmented());
	        augmentedToggle.setOnCheckedChangeListener(listener);
	        augmentedToggle.setGravity(Gravity.RIGHT);
	        augmentedToggle.setPadding(0, 0, 0, 3);
	        
	        CheckBox equatorialToggle = new CheckBox(getActivity());
	        equatorialToggle.setId(1002);
	        Log.e("TAG", "equatorial status: " + a.getRenderer().getEquatorialStatus());
	        equatorialToggle.setChecked(a.getRenderer().getEquatorialStatus());
	        equatorialToggle.setOnCheckedChangeListener(listener);
	        equatorialToggle.setGravity(Gravity.RIGHT);
	        equatorialToggle.setPadding(0, 0, 0, 3);
	        
	        CheckBox planetTextToggle = new CheckBox(getActivity());
	        planetTextToggle.setId(1003);
	      //  Log.e("TAG", "equatorial status: " + a.getRenderer().getEquatorialStatus());
	        planetTextToggle.setChecked(a.getRenderer().getPlanetTextsStatus());
	        planetTextToggle.setOnCheckedChangeListener(listener);
	        planetTextToggle.setGravity(Gravity.RIGHT);
	        planetTextToggle.setPadding(0, 0, 0, 3); 
	        
	        CheckBox constellationToggle = new CheckBox(getActivity());
	        constellationToggle.setId(1004);
	      //  Log.e("TAG", "equatorial status: " + a.getRenderer().getEquatorialStatus());
	        constellationToggle.setChecked(a.getRenderer().getConstellationStatus());
	        constellationToggle.setOnCheckedChangeListener(listener);
	        constellationToggle.setGravity(Gravity.RIGHT);
	        constellationToggle.setPadding(0, 0, 0, 3); 
	        
	        

	        
	        dataList.add(new DrawerItem("Galitarium", "header"));
	        dataList.add(new DrawerItem("Adjust Visible Stars", "item"));
	        dataList.add(new DrawerItem("Accelerometer", "toggle", augmentedToggle));
	       dataList.add(new DrawerItem("Equatorial Lines", "toggle", equatorialToggle));
	        dataList.add(new DrawerItem("Planet Texts", "toggle", planetTextToggle));
	        dataList.add(new DrawerItem("Constellations", "toggle", constellationToggle));

    
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
        
		Fragment fragment = null;
		OpenGLES20Activity activity = (OpenGLES20Activity) getActivity();
		
		switch(position) {
			case 1:

	        	
	        	DialogFragment starSettingsFragment = new StarSettingsFragment();
	        	Bundle args = new Bundle();
	        	
	        	starSettingsFragment.setArguments(args);
	        	starSettingsFragment.show(getFragmentManager(), "star_settings");
	        	starSettingsFragment.onAttach(activity);
				//Log.e("TAG", "opening fragment"); 
	        	
				break;
		    default:
		    	break;
			
		}
	
	
	
	
	
}
	}