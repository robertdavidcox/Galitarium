package com.robert.starsproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Switch;
import com.robert.starsproject.R;

@SuppressLint("ResourceAsColor")
public class LatitudePickerFragment extends DialogFragment {


	private View view;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
       
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		OpenGLES20Activity activity = (OpenGLES20Activity) getActivity();
		
		builder.setTitle("Latitude")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		               @Override
		               public void onClick(DialogInterface dialog, int id) {

		           		   NumberPicker lat_degrees_picker = (NumberPicker) view.findViewById(R.id.latitude_picker);
		            	   int lat_degrees_val = lat_degrees_picker.getValue();
		           		   
		           		   NumberPicker lat_minutes_picker = (NumberPicker) view.findViewById(R.id.latitude_minutes_picker);
		            	   int lat_minutes_val = lat_minutes_picker.getValue();
		           		   
		           		   NumberPicker lat_seconds_picker = (NumberPicker) view.findViewById(R.id.latitude_seconds_picker);
		            	   int lat_seconds_val = lat_seconds_picker.getValue();

		            	   Switch s = (Switch) view.findViewById(R.id.lat_switch);
		            	   
		            	   if (!s.isChecked()) {
		            		   lat_degrees_val = -lat_degrees_val;
		            		   lat_minutes_val = -lat_minutes_val;
		            		   lat_seconds_val = -lat_seconds_val;
		            		   
		            	   }
		            	   
		            	   Log.e("TAG", "lat: " + lat_degrees_val);
		            	   Log.e("TAG", "lat mins: " + lat_degrees_val);
		            	   Log.e("TAG", "lat seconds: " + lat_degrees_val);
		            	   
		           		   OpenGLES20Activity a = (OpenGLES20Activity) getActivity();
		           		   a.changeLocation("lat", lat_degrees_val, lat_minutes_val, lat_seconds_val);

		           		   
		               }
		           });
		
		
		
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		View v = inflater.inflate(R.layout.edit_latitude_fragment, null);
		
		builder.setView(v);
		this.view = v;
		
		
		NumberPicker lat_degrees_picker = (NumberPicker) v.findViewById(R.id.latitude_picker);
		String[] lat_degrees = new String[90];
		for(int i=0; i < lat_degrees.length; i++){
			lat_degrees[i] = Integer.toString(i) + "¡";
		}
		lat_degrees_picker.setMaxValue(lat_degrees.length - 1);
		lat_degrees_picker.setMinValue(0);
		lat_degrees_picker.setValue(0);
		lat_degrees_picker.setDisplayedValues(lat_degrees);
		lat_degrees_picker.setWrapSelectorWheel(true);
		
		NumberPicker lat_minutes_picker = (NumberPicker) v.findViewById(R.id.latitude_minutes_picker);

		String[] lat_minutes = new String[60];
		for(int i=0; i < lat_minutes.length; i++){
			lat_minutes[i] = Integer.toString(i) + "'";
		}
		lat_minutes_picker.setMaxValue(lat_minutes.length - 1);
		lat_minutes_picker.setMinValue(0);
		lat_minutes_picker.setDisplayedValues(lat_minutes);
		lat_minutes_picker.setWrapSelectorWheel(true);
		
		NumberPicker lat_seconds_picker = (NumberPicker) v.findViewById(R.id.latitude_seconds_picker);

		String[] lat_seconds = new String[60];
		for(int i=0; i < lat_seconds.length; i++){
			lat_seconds[i] = Integer.toString(i) + "''";
		}
		lat_seconds_picker.setMaxValue(lat_seconds.length - 1);
		lat_seconds_picker.setMinValue(0);
		lat_seconds_picker.setDisplayedValues(lat_seconds);
		lat_seconds_picker.setWrapSelectorWheel(true);
		

	
		//NumberPicker lat = (NumberPicker) v.findViewById(R.id.latitude_picker);
		//lat.setDisplayedValues(vals);


		
		
		return builder.create();
     	
	}


	
	

}