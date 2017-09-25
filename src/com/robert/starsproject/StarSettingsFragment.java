package com.robert.starsproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import com.robert.starsproject.R;

public class StarSettingsFragment extends DialogFragment {

	private float star_scale;
	private int min_stars;

	private View view;

	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
       
		

		
		final OpenGLES20Activity activity = (OpenGLES20Activity) getActivity();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		View v = inflater.inflate(R.layout.star_settings_fragment, null);
		
		min_stars = activity.getRenderer().getMinStars();
		star_scale = activity.getRenderer().getStarScale();
		
		builder.setNegativeButton("Cancel",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
         	   	
	    		    

        		   
            }
        }); 
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		               @Override
		               public void onClick(DialogInterface dialog, int id) {
		            	   	
			    		    activity.getRenderer().setMinStars(min_stars);
			    		    activity.getRenderer().setStarScale(star_scale);
			    		    activity.getRenderer().resetView();

		           		   
		               }
		           }); 
		
	
		
		builder.setTitle("Star Settings").setView(v);
		
		
		this.view = v;
	    SeekBar.OnSeekBarChangeListener seekListener =
	            new SeekBar.OnSeekBarChangeListener() {

	    	OpenGLES20Activity a = (OpenGLES20Activity) getActivity();

			@Override
			public void onProgressChanged(SeekBar bar, int progress,
					boolean arg2) {
	            switch(bar.getId()) {
	            case 2001:	                    	
	            	min_stars = 1200 + (progress-50)*20;
	            	TextView minStarsText = (TextView) view.findViewById(R.id.min_stars_text);
	            	minStarsText.setText("Minimum stars: " + min_stars);
	            	
	    		    break;
	            case 2002:	                    	
	            	star_scale = (float) (1 + (progress-50.0)/100.0);
	            	TextView numStarsText = (TextView) view.findViewById(R.id.num_stars_text);
	            	numStarsText.setText("Star scale factor: " + star_scale);
	            	
	    		    break;
	            default:
	                return;
	        }
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
	    };
	    
	    
	    
    	TextView numStarsText = (TextView) v.findViewById(R.id.num_stars_text);
    	numStarsText.setText("Star scale factor: " + star_scale);
    	
    	TextView minStarsText = (TextView) v.findViewById(R.id.min_stars_text);
    	minStarsText.setText("Minimum stars: " + min_stars);

	    SeekBar minStarsSlider =  (SeekBar) v.findViewById(R.id.min_stars_seekbar);
	    minStarsSlider.setOnSeekBarChangeListener(seekListener);
	    minStarsSlider.setProgress(((min_stars-1200)/20) + 50);
	    minStarsSlider.setId(2001); 
	    
	    SeekBar numStarsSlider = (SeekBar) v.findViewById(R.id.num_stars_seekbar);
	    numStarsSlider.setOnSeekBarChangeListener(seekListener);
	    numStarsSlider.setId(2002); 
	    numStarsSlider.setProgress((int) ((star_scale *100.0)-50.0)); 
		
		

		
		return builder.create();
     	
	}
	
	


	
	

}
	
	


