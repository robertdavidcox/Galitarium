package com.robert.starsproject;

public class SaturnMoons {

	private double EPS = 1.0e-12;
	private float twopi = (float) (Math.PI*2);
	
	private float lat;
	private float lon;
	private int year;
	private int month;
	private int day;
	private int hour;
	private int min;
	private int sec;
	private	float dayNumber;
	private float numCenturies;
	private double RADS = Math.PI/180;
	private double DEGS = 180/Math.PI;
	private float radius;
	private float scaleFactor = 149597871;	
	private float saturnRA;
	private float saturnDEC;
	
	private float titanRA;
	private float titanDEC;
	private float angrad;
	private float titanAZ;
	private float titanALT;

	private float saturnRadii = 29116;
	
	public SaturnMoons(float saturnRA, float saturnDEC, float lat, float lon, float radius, int year, int month, int day, int hour,
				int minute, int second) {
		
		this.lon = lon;
		this.lat = lat;
		
		this.radius = radius;
		
		this.year = year;
		this.month = month + 1;
		this.day = day;
		this.hour = hour;
		this.min = minute;
		this.sec = second;   

		this.saturnRA = saturnRA;
		this.saturnDEC = saturnDEC;
		
		this.dayNumber = calcDayNumber();
		this.numCenturies = (float) (dayNumber/36525.0);
		moon_posit();

	}
	
	private float calcDayNumber() {
		
		float h = (float) (hour + (min/60.0));
		dayNumber = (float) (367.0*year 
		           - Math.floor(7.0*(year + Math.floor((month + 9.0)/12.0))/4.0) 
		           + Math.floor(275.0*month/9.0) + day - 730531.5 + h/24.0); 

		return dayNumber; 
		
	}
	
	float FNatn2(float y, float x) {
		
		float a = (float) Math.atan(y/x);
		if (x < 0) {
			a = (float) (a + Math.PI);
		}
		if (y < 0 && x > 0) {
			a = (float) (a + Math.PI*2);
		}
		return a;
		
	}
	
	float FNrange(double x) {
        return  (float) (x - Math.floor(x/twopi)*twopi);
	}
	
    double rev( double x )
    {
        return  x - Math.floor(x/360.0)*360.0;
    }
    
    float FNasin (float x) {
    	float c = (float) Math.sqrt(1 - x * x);
    	return (float) Math.atan(x / c);
    }

	public void moon_posit() {
		
		float epoch = (float) 2433282.5;
		float ecc = (float) (29092 * 1E-6);
		float gamma = (float) (2960 * (Math.PI/180)/10000);
		
		float t_d = dayNumber - epoch;
		float t = (float) (t_d/365.25);
		float t_centuries = t/100;
		
		
	}


	
	public float[] computeHorizonCoordinates(double RA, double DEC,
			   double lat, double lon) {

		double  ha, sin_alt, cos_az, alt, az, a, cos_a;


		ha = mean_sidereal_time(lon) - RA;
		if (ha < 0) {
			ha = ha + 360;
		}

		ha = ha*RADS;
		DEC = DEC*RADS;
		lat = lat*RADS;

		sin_alt = Math.sin(DEC)*Math.sin(lat) + 
				Math.cos(DEC)*Math.cos(lat)*Math.cos(ha);
		alt = (float) Math.asin(sin_alt);

		cos_a = (Math.sin(DEC) - Math.sin(alt) * Math.sin(lat))/
				(Math.cos(alt)*Math.cos(lat));
		a = Math.acos(cos_a);

		alt = (float) (alt*DEGS);
		a = a*DEGS;

		if (Math.sin(ha) > 0) {
			az  = (float) (360 - a);
		} else {
			az = (float) a;
		}

		return new float[]{(float) az, (float) alt};

//	Log.e("PLANET", "latitude: " + lat + ", longitude : " + lon);




}
	
	private double mean_sidereal_time(double lon) {
		
		int year = this.year;
		int month = this.month;
		int day = this.day;
		int hour = this.hour;
		int minute = this.min;
		int second = this.sec;
		
		
		if ((month == 1) || (month == 2)) {
			year = year - 1;
			month = month + 12;
		}
		
		double a = Math.floor(year/100);
		double b = 2 - a + Math.floor(a/4);
		double c = Math.floor(365.25*year);
		double d = Math.floor(30.6001*(month + 1));
		
		double jd = b + c + d - 730550.5 + day +
				    (hour + minute/60.0 + second/3600.0)/24.0;
		
		double jt = jd/36525.0;
		
		double mst = 280.46061837 + 360.98564736629*jd
					+ 0.000387933*jt*jt - jt*jt*jt/38710000 + lon;
		
	    if (mst > 0.0)
	    {
	        while (mst > 360.0)
	            mst = mst - 360.0;
	    }
	    else
	    {
	        while (mst < 0.0)
	            mst = mst + 360.0;
	    }
	    
	    
	    return mst;
	    

	}

	public float normalize(float v) {
		v = (float) (v - Math.floor( v ) );
		if (v < 0) {
			v = v + 1;
		}
		return v;
	}
	
	private double mod2pi(double x)
	{
	    double b = (float) (x/(2*Math.PI));
	    double a = (2*Math.PI)*(b - abs_floor(b));  
	    if (a < 0) a = (2*Math.PI) + a;
	    return a;
	}
	
	private double abs_floor(double x)
	{
	    double r;
	    if (x >= 0.0) r = Math.floor(x);
	    else          r = Math.ceil(x);
	    return r;
	}
	

	
}
