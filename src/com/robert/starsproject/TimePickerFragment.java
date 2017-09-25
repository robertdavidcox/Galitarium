package com.robert.starsproject;


import java.util.Calendar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TimePickerFragment extends DialogFragment
								implements TimePickerDialog.OnTimeSetListener {
	
	private OnTimePickedListener mListener;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = getArguments().getInt("hour");
        int minute = getArguments().getInt("minute");
        
        OpenGLES20Activity activity = (OpenGLES20Activity) getActivity();
        



        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));		
	}

	@Override
	public void onTimeSet(TimePicker arg0, int hour, int minute) {

		
		mListener.onTimePicked(hour, minute);
		
	}
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTimePickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTimePickedListener");
        }
    }
	
	public interface OnTimePickedListener {
		public void onTimePicked(int hour, int minute);
	}
	

}
