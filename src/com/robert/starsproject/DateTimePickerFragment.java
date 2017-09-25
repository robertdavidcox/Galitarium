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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ToggleButton;
import com.robert.starsproject.R;

public class DateTimePickerFragment extends ListFragment {

	
	public DateTimePickerFragment() {
		
	}
	
	
	 @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
	        List<DrawerItem> dataList = new ArrayList<DrawerItem>();	
	        OpenGLES20Activity activity = (OpenGLES20Activity) getActivity();
	        
	        ToggleButton currentTimeToggle = new ToggleButton(getActivity());
	        currentTimeToggle.setChecked(activity.isCurrentTime());

	        
	        dataList.add(new DrawerItem("Galitarium", "header"));
	        dataList.add(new DrawerItem("Use current time", "item"));
	        dataList.add(new DrawerItem("Set date", "item"));
	        dataList.add(new DrawerItem("Set time", "item"));

    
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
        	activity.changeTimeToCurrent();
        	break;
        case 2:
        	activity.changeDate();
        	break;
        case 3:
        	activity.changeTime();
        	break;
        default:
        	break;

        }
         
        
	}
	

	
}