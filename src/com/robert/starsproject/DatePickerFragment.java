package com.robert.starsproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DatePickerFragment extends DialogFragment
		implements DatePickerDialog.OnDateSetListener {
	
	private OnDatePickedListener mListener;

	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		
		int year = getArguments().getInt("year");
		int month = getArguments().getInt("month");
		int day = getArguments().getInt("day");
		

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}
	



	@Override
	public void onDateSet(DatePicker arg0, int year, int month, int day) {
		mListener.onDatePicked(year, month, day);
	
	}
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDatePickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDatePickedListener");
        }
    }
	
	public interface OnDatePickedListener {
		
		public void onDatePicked(int year, int month, int day);

		void onDrawerSlide(View drawerView, float slideOffset);
		
	}
}
