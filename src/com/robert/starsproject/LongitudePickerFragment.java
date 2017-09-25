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
public class LongitudePickerFragment extends DialogFragment implements NumberPicker.OnValueChangeListener {

	private View view;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
       
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		OpenGLES20Activity activity = (OpenGLES20Activity) getActivity();

		
		builder.setTitle("Longitude")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		               @Override
		               public void onClick(DialogInterface dialog, int id) {
		            	   
		            	   NumberPicker lon_degrees_picker = (NumberPicker) view.findViewById(R.id.longitude_picker);
		            	   int lon_degrees_val = lon_degrees_picker.getValue();
		            	   
		            	   NumberPicker lon_minutes_picker = (NumberPicker) view.findViewById(R.id.longitude_minutes_picker);
		            	   int lon_minutes_val = lon_minutes_picker.getValue();
		            	   
		           		   NumberPicker lon_seconds_picker = (NumberPicker) view.findViewById(R.id.longitude_seconds_picker);
		            	   int lon_seconds_val = lon_seconds_picker.getValue();
		           		   
		            	   Switch s = (Switch) view.findViewById(R.id.lon_switch);
		            	   
		            	   if (!s.isChecked()) {
		            		   lon_degrees_val = -lon_degrees_val;
		            		   lon_minutes_val = -lon_minutes_val;
		            		   lon_seconds_val = -lon_seconds_val;
		            		   
		            	   }
		            	   
		           		   
		            	   Log.e("TAG", "lon: " + lon_degrees_val);
		            	   Log.e("TAG", "lon mins: " + lon_degrees_val);
		            	   Log.e("TAG", "lon seconds: " + lon_degrees_val);

		            	   
		           		   OpenGLES20Activity a = (OpenGLES20Activity) getActivity();
		           		   a.changeLocation("lon", lon_degrees_val, lon_minutes_val, lon_seconds_val);

		           		   
		           		   
		               }
		           });
		
		
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		View v = inflater.inflate(R.layout.edit_longitude_fragment, null);
		
		builder.setView(v);
		this.view = v;
		
        int current_longitude = getArguments().getInt("longitude");
		
		NumberPicker lon_degrees_picker = (NumberPicker) v.findViewById(R.id.longitude_picker);
		String[] lon_degrees = new String[180];
		for(int i=0; i < lon_degrees.length; i++){
			lon_degrees[i] = Integer.toString(i) + "¡";
		}
		lon_degrees_picker.setMaxValue(lon_degrees.length - 1);
		lon_degrees_picker.setValue(current_longitude);
		lon_degrees_picker.setMinValue(0);
		lon_degrees_picker.setDisplayedValues(lon_degrees);
		lon_degrees_picker.setWrapSelectorWheel(true);
		
		NumberPicker lon_minutes_picker = (NumberPicker) v.findViewById(R.id.longitude_minutes_picker);

		String[] lon_minutes = new String[60];
		for(int i=0; i < lon_minutes.length; i++){
			lon_minutes[i] = Integer.toString(i) + "'";
		}
		lon_minutes_picker.setMaxValue(lon_minutes.length - 1);
		lon_minutes_picker.setMinValue(0);
		lon_minutes_picker.setDisplayedValues(lon_minutes);
		lon_minutes_picker.setWrapSelectorWheel(true);
		
		NumberPicker lon_seconds_picker = (NumberPicker) v.findViewById(R.id.longitude_seconds_picker);

		String[] lon_seconds = new String[60];
		for(int i=0; i < lon_seconds.length; i++){
			lon_seconds[i] = Integer.toString(i) + "''";
		}
		lon_seconds_picker.setMaxValue(lon_seconds.length - 1);
		lon_seconds_picker.setMinValue(0);
		lon_seconds_picker.setDisplayedValues(lon_seconds);
		lon_seconds_picker.setWrapSelectorWheel(true);

		

	
		//NumberPicker lat = (NumberPicker) v.findViewById(R.id.latitude_picker);
		//lat.setDisplayedValues(vals);


		
		
		return builder.create();
     	
	}


	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		// TODO Auto-generated method stub
		
	}
}
	
	