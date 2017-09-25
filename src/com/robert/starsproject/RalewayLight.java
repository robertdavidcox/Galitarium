package com.robert.starsproject;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RalewayLight extends TextView {

	public RalewayLight(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
    public RalewayLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
 
    public RalewayLight(Context context) {
        super(context);
        init();
    }
	
	
    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "ralewaylight.ttf");
            setTypeface(tf);
        }
    }
	
}
