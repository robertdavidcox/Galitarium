package com.robert.starsproject;

import android.provider.BaseColumns;

public final class StarContract {
	
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
	public StarContract() {}
	
    /* Inner class that defines the table contents */
	public static abstract class StarEntry implements BaseColumns {
		
        public static final String TABLE_NAME = "stars";
        public static final String COLUMN_NAME_STAR_ID = "starid";
        public static final String COLUMN_NAME_X = "xcoord";
        public static final String COLUMN_NAME_Y = "ycoord";
        public static final String COLUMN_NAME_Z = "zcoord";
        public static final String COLUMN_NAME_RA = "rightascension";
        public static final String COLUMN_NAME_DEC = "declination";
        public static final String COLUMN_NAME_COLOR_INDEX = "colorindex";
        public static final String COLUMN_NAME_VISUAL_MAG = "visualmagnitue";
        public static final String COLUMN_NAME_DISTANCE = "distance";
        public static final String COLUMN_NAME_BAYER_NAME = "bayerflamsteed";
        public static final String COLUMN_NAME_PROPER_NAME = "propername";
        
		
	}
	
	

}
