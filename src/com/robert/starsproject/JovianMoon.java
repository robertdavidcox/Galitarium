package com.robert.starsproject;

import com.robert.starsproject.StarRenderer.Tuple;

import android.util.Log;

public class JovianMoon {

	
	private double EPS = 1.0e-12;
	private float twopi = (float) (Math.PI*2);
	
	private float lat;
	private float lon;
	private float rightAscension;
	private float declination;
	private float azimuth;
	private float altitude;
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
	private float jupiterRA;
	private float jupiterDEC;
	
	private float ioRA;
	private float ioDEC;
	private float europaRA;
	private float europaDEC;
	private float ganymedeRA;
	private float ganymedeDEC;
	private float callistoRA;
	private float callistoDEC;
	private float angrad;
	private float ioAZ;
	private float ioALT;
	private float europaAZ;
	private float europaALT;
	private float ganymedeAZ;
	private float ganymedeALT;
	private float callistoAZ;
	private float callistoALT;
	
	
	
	private float jupiterRadii = 69911;
	
	public JovianMoon(float jupiterRA, float jupiterDEC, float lat, float lon, float radius, int year, int month, int day, int hour,
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

		/*this.year = 1997;
		this.month = 8;
		this.day = 7;
		this.hour = 22;
		this.min = 25;
		this.sec = 0;   */

		this.jupiterRA = jupiterRA;
		this.jupiterDEC = jupiterDEC;
		
		
		this.dayNumber = calcDayNumber();
		//this.dayNumber = (float) -3543;
		
		//Log.e("MOON", "MOON DAY NUMBER: " + dayNumber);
		this.numCenturies = (float) (dayNumber/36525.0);
		
		//Log.e("Moon", "numCenturies: " + numCenturies);
		
		
		//Log.e("MOON", "moon JD is " + dayNumber);
	
		
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
		
		float me = FNrange((357.529 + .9856003 * dayNumber) * RADS);
		
		float V = (float) ((172.74 + .00111588 * dayNumber) * RADS);
		float pj = (float) (.329 * Math.sin(V) * RADS);
		float mj = FNrange((20.02 + .0830853 * dayNumber) * RADS + pj);
		
		float j = FNrange((66.115 + .9025179 * dayNumber) * RADS - pj);
		float aj = (float) ((1.915 * Math.sin(me) + .02 * Math.sin(2 * me)) * RADS);
		float bj = (float) ((5.555 * Math.sin(mj) + .168 * Math.sin(2 * mj)) * RADS);
		float k = j + aj - bj;
		
		float res = (float) (1.00014 - .01671 * Math.cos(me) - .00014 * Math.cos(2 * me));
		float rjs = (float) (5.20872 - .25208 * Math.cos(mj) - .00611 * Math.cos(2 * mj));
		float rej = (float) Math.sqrt(res * res + rjs * rjs - 2 * res * rjs * Math.cos(k));

		float phi = FNasin((float) (res / rej * Math.sin(k)));
		
		float dd = dayNumber - rej / 173;
		float u1 = FNrange((163.8067 + 203.4058643 * dd) * RADS + phi - bj);
		float u2 = FNrange((358.4108 + 101.2916334 * dd) * RADS + phi - bj);
		float u3 = FNrange((5.7129 + 50.2345179 * dd) * RADS + phi - bj);
		float u4 = FNrange((224.8151 + 21.4879801 * dd) * RADS + phi - bj);
	    
		float Gj = FNrange((331.18 + 50.310482 * dd) * RADS);
		float Hj = FNrange((87.4 + 21.569231 * dd) * RADS);
		
		float io_jupiter_distance = (float) (5.9073 - .0244 * Math.cos(2 * (u1 - u2)));
		float europa_jupiter_distance = (float) (9.3991 - .0882 * Math.cos(2 * (u2 - u3)));
		float ganymede_jupiter_distance = (float) (14.9924 - .0216 * Math.cos(Gj));
		float callisto_jupiter_distance = (float) (26.3699 - .1935 * Math.cos(Hj));
		
		float io_jupiter_x = (float) (io_jupiter_distance * Math.sin(u1));
		float europa_jupiter_x = (float) (europa_jupiter_distance * Math.sin(u2));
		float ganymede_jupiter_x = (float) (ganymede_jupiter_distance * Math.sin(u3));
		float callisto_jupiter_x = (float) (callisto_jupiter_distance * Math.sin(u4));
		
		angrad = (float) (1.6375 / rej);
		
		ioRA = jupiterRA - (io_jupiter_x * angrad)/60;
		ioDEC = jupiterDEC;
		
		europaRA = jupiterRA - (europa_jupiter_x * angrad)/60;
		europaDEC = jupiterDEC;
		
		ganymedeRA = jupiterRA - (ganymede_jupiter_x * angrad)/60;
		ganymedeDEC = jupiterDEC;
		
		callistoRA = jupiterRA - (callisto_jupiter_x * angrad)/60;
		callistoDEC = jupiterDEC;
		
	//	Log.e("JOVIAN MOONS", "ra: " + ioRA + ", dec: " + ioDEC);
		
		float [] io_horizon = computeHorizonCoordinates(ioRA, ioDEC, lat, lon);
		ioAZ = io_horizon[0];
		ioALT = io_horizon[1];
		
		float[] europa_horizon = computeHorizonCoordinates(europaRA, europaDEC, lat, lon);
		europaAZ = europa_horizon[0];
		europaALT = europa_horizon[1];
		
		float[] ganymede_horizon = computeHorizonCoordinates(ganymedeRA, ganymedeDEC, lat, lon);
		ganymedeAZ = ganymede_horizon[0];
		ganymedeALT = ganymede_horizon[1];
		
		float[] callisto_horizon = computeHorizonCoordinates(callistoRA, callistoDEC, lat, lon);
		callistoAZ = callisto_horizon[0];
		callistoALT = callisto_horizon[1];
		
		//Log.e("JOVIAN MOONS", "az: " + ioAZ + ", alt: " + ioALT);
		
		
	//	//Log.e("JOVIAN MOONS", "(" + x1 + ", " + x2 + ", " + x3 + ", " + x4 + ")");
		
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
	
	public float getMoonRA(String id) {
		
		if (id.equals("IO")) {
			return ioRA;
		} else if (id.equals("EUROPA")) {
			return europaRA;
		} else if (id.equals("GANYMEDE")) {
			return ganymedeRA;
		} else {
			return callistoRA;
		}

		
	}
	
	public float getMoonDEC(String id) {
		
		if (id.equals("IO")) {
			return ioDEC;
		} else if (id.equals("EUROPA")) {
			return europaDEC;
		} else if (id.equals("GANYMEDE")) {
			return ganymedeDEC;
		} else {
			return callistoDEC;
		}

		
	}
	
	public float getMoonAZ(String id) {
		
		if (id.equals("IO")) {
			return ioAZ;
		} else if (id.equals("EUROPA")) {
			return europaAZ;
		} else if (id.equals("GANYMEDE")) {
			return ganymedeAZ;
		} else {
			return callistoAZ;
		}

		
	}
	
	public float getMoonALT(String id) {
		
		if (id.equals("IO")) {
			return ioALT;
		} else if (id.equals("EUROPA")) {
			return europaALT;
		} else if (id.equals("GANYMEDE")) {
			return ganymedeALT;
		} else {
			return callistoALT;
		}
		
	}
	
}
