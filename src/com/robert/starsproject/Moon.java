package com.robert.starsproject;

import android.util.Log;

public class Moon {
	
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
	private float RVEC;
	
	private float d0;
	private float sunGeocentric;
	private float magnitude;
	private float FV;
	private float elong;
	private float phase;
	private float slat;
	private float slon;
	private float mlat;
	private float mlon;
	
	private float x;
	private float y;
	private float z;
	
	
	public Moon(float lat, float lon, float radius, int year, int month, int day, int hour,
				int minute, int second, float sunGeocentric, float slon, float slat) {
		
		this.lon = lon;
		this.lat = lat;
		
		this.radius = radius;
		
		this.year = year;
		this.month = month + 1;
		this.day = day;
		this.hour = hour;
		this.min = minute;
		this.sec = second;  
		
		this.sunGeocentric = sunGeocentric;
		this.slat = slat;
		this.slon = slon;
		
		/* this.year = 1998;
		this.month = 8;
		this.day = 9;
		this.hour = 11;
		this.min = 56;
		this.sec = 0; */
		
		
		this.dayNumber = calcDayNumber();
		//this.dayNumber = (float) -3543;
		
		//Log.e("MOON", "MOON DAY NUMBER: " + dayNumber);
		this.numCenturies = (float) (dayNumber/36525.0);
		
		//Log.e("Moon", "numCenturies: " + numCenturies);
	
		
		//Log.e("MOON", "moon JD is " + dayNumber);
		
		
		
		moon_posit();
		

		computeHorizonCoordinates(rightAscension, declination, lat, lon);
		
		computePhase();
		computeMagnitude();
		computeXYZ();
		

	   /* Log.e("MOON", "moon rightAscension: " + rightAscension);
	    Log.e("MOON", "moon declination: " + declination);
	    Log.e("MOON", "moon azimuth: " + azimuth);
	    Log.e("MOON", "moon altitude: " + altitude); */
	    
		
	}
	
	private float calcDayNumber() {

	/*	float h = (float) ((hour + min/60)/24.0);
		
		float d = (float) (367*this.year - 7 * ( this.year + (this.month +9)/12 ) / 4 + 275*this.month/9 + this.day - 730530 + h);
		dayNumber = d; */
		

		
		//return dayNumber; 
		
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
	
	public void moon_posit() {
		
				float l = FNrange(RADS * (218.32 + 481267.883 * numCenturies));
				l = (float) (l + RADS * 6.29 * Math.sin(FNrange((134.9 + 477198.85 * numCenturies) * RADS)));
				l = (float) (l - RADS * 1.27 * Math.sin(FNrange((259.2 - 413335.38 * numCenturies) * RADS)));
				l = (float) (l + RADS * .66 * Math.sin(FNrange((235.7 + 890534.23 * numCenturies) * RADS)));
				l = (float) (l + RADS * .21 * Math.sin(FNrange((269.9 + 954397.7 * numCenturies) * RADS)));
				l = (float) (l - RADS * .19 * Math.sin(FNrange((357.5 + 35999.05 * numCenturies) * RADS)));
				l = (float) (l - RADS * .11 * Math.sin(FNrange((186.6 + 966404.05 * numCenturies) * RADS)));
				l = FNrange(l);
				
				this.mlon = l;
				
				//   calculate the geocentric latitude
				
				float bm = (float) (RADS * 5.13 * Math.sin(FNrange((93.3 + 483202.03 * numCenturies) * RADS)));
				bm = (float) (bm + RADS * .28 * Math.sin(FNrange((228.2 + 960400.87 * numCenturies) * RADS)));
				bm = (float) (bm - RADS * .28 * Math.sin(FNrange((318.3 + 6003.18 * numCenturies) * RADS)));;
				bm = (float) (bm - RADS * .17 * Math.sin(FNrange((217.6 - 407332.2 * numCenturies) * RADS)));
				

				this.mlat = bm;
				
			//	Log.e("MOON", "l is: " + l*DEGS + ", bm is: " + bm*DEGS);
				//   get the parallax
				
				float gp = (float) (.9508 * RADS);
				gp = (float) (gp + RADS * .0518 * Math.cos(FNrange((134.9 + 477198.85 * numCenturies) * RADS)));
				gp = (float) (gp + RADS * .0095 * Math.cos(FNrange((259.2 - 413335.38 * numCenturies) * RADS)));
				gp = (float) (gp + RADS * .0078 * Math.cos(FNrange((235.7 + 890534.23 *numCenturies) * RADS)));
				gp = (float) (gp + RADS * .0028 * Math.cos(FNrange((269.9 + 954397.7 * numCenturies) * RADS)));
				
			//	Log.e("MOON", "lunar paralax" + gp*DEGS);
				
				//   from the parallax, get the semidiameter and the radius vector
				
				float sdia = (float) (.2725 * gp);
				float rm = (float) (1 / (Math.sin(gp)));
				float xg = (float) (rm * Math.cos(l) * Math.cos(bm));
				float yg = (float) (rm * Math.sin(l) * Math.cos(bm));
				float zg = (float) (rm * Math.sin(bm));
				
				//   rotate to equatorial coords
				//  obliquity of ecliptic
				
				float ecl = (float) ((23.4393 - 3.563E-07 * dayNumber) * RADS);
				
			//	Log.e("MOON", "equator of date: " + ecl*DEGS);

				float xe = xg;
				float ye = (float) (yg * Math.cos(ecl) - zg * Math.sin(ecl));
				float ze = (float) (yg * Math.sin(ecl) + zg * Math.cos(ecl));
				
				//   geocentric RA and Dec
				
				float ra = (float) (FNatn2(ye, xe)*DEGS);
				float dec = (float) (Math.atan(ze / Math.sqrt(xe * xe + ye * ye))*DEGS);
			
				float h = hour  + min/60;

		
				float lst = (float) mean_sidereal_time(lon);// (float) (100.46 + .985647352 * dayNumber + h * 15 + lon);
				
				float glat = (float) (lat * RADS);
				float glong = (float) (lon * RADS);
				
				lst = FNrange(lst * RADS);
				
			//	Log.e("MOON", "lst " + lst*DEGS/24);
				
				float xtop = (float) (xe - Math.cos(glat) * Math.cos(lst));
				float ytop = (float) (ye - Math.cos(glat) * Math.sin(lst));
				float ztop = (float) (ze - Math.sin(glat));
				
				float rtop = (float) Math.sqrt(xtop * xtop + ytop * ytop + ztop * ztop);
				float ratop = (float) (FNatn2(ytop, xtop) * DEGS);
				float dectop = (float) (Math.atan(ztop / Math.sqrt(xtop * xtop + ytop * ytop))*DEGS);
				
			//	Log.e("MOON", "moon lat: " + lat + "lon " + lon);
				
				this.rightAscension = ratop;
				this.declination = dectop; 
				this.RVEC = (float) (rtop*6285.320956/149597871);
		


	    
	}
	
	private double computeTrueAnomaly(double M, double e) {
		
		double V, E1;
		
		double E = M + e*Math.sin(M)*(1.0 + e*Math.cos(M));
		
		do
		{
			E1 = E;
			E = E1 - (E1 - e*Math.sin(E1) - M)/(1 - e*Math.cos(E1));
		}
		while (E - E1 > EPS);
		V = 2*Math.atan(Math.sqrt((1 + e)/(1 - e))*Math.tan(0.5*E));
		
	    if (V < 0) {
	    	V = V + (2*Math.PI); 
	    }
	    return V;
	}
	
	
	public void computePhase() {
		
		
		
		mlon = (float) mod2pi(mlon);
		mlat = (float) mod2pi(mlat);
		
		elong = (float) (Math.acos(Math.cos(slon - mlon) * Math.cos(mlat))*DEGS);
		FV = (float) (180 - elong);
		
		phase  =  (float) (( 1 + Math.cos(FV*RADS) ) / 2);
		
		
		
		//Log.e("MOON", "----------------");
		//Log.e("MOON", "elong: " + elong);
		//Log.e("MOON", "FV: " + FV);
		//Log.e("MOON", "phase of moon: " + phase*DEGS);
	}
	
	
	public void computeMagnitude() {
		
		magnitude = (float) (0.23 + 5*Math.log10(sunGeocentric*RVEC) + 0.026 * FV + 4.0E-9 * Math.pow(FV, 4));
		Log.e("MOON", "moon magnitude: " + magnitude);
		
	}

	
	public void computeHorizonCoordinates(double RA, double DEC,
			   double lat, double lon) {

			double  ha, sin_alt, cos_az, alt, az, a, cos_a;
			
			//Log.e("PLANET", "latitude: " + lat + ", longitude : " + lon);
			
			ha = mean_sidereal_time(lon) - RA;
			if (ha < 0) {
				ha = ha + 360;
			}
			
			ha = ha*RADS;
			DEC = DEC*RADS;
			lat = lat*RADS;
					
			sin_alt = Math.sin(DEC)*Math.sin(lat) + 
			Math.cos(DEC)*Math.cos(lat)*Math.cos(ha);
			alt = Math.asin(sin_alt);
			
			cos_a = (Math.sin(DEC) - Math.sin(alt) * Math.sin(lat))/
			(Math.cos(alt)*Math.cos(lat));
			a = Math.acos(cos_a);
			
			alt = alt*DEGS;
			a = a*DEGS;
			
			if (Math.sin(ha) > 0) {
				az  = 360 - a;
			} else {
				az = a;
			}
			
			this.altitude = (float) alt;
			this.azimuth = (float) az;

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
	
	public float getRA() {
		return rightAscension;
	}
	
	public float getDEC() {
		return declination;
	}
	
	public float getAZ() {
		return azimuth;
	}
	
	public float getALT() {
		return altitude;
	}
	
	public float getRadius() {
		return radius*100;
	}
	
	public float getRVEC() {
		
	//	Log.e("MOON", "moon distance in km is " + RVEC*149597871);
		
		return (float) (RVEC*100);
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
	

	private void computeXYZ() {
		
		x = (float) (-Math.sin(Math.toRadians(azimuth)) * Math.cos(Math.toRadians(altitude)));
		y = (float) (Math.sin(Math.toRadians(altitude)));
		z = (float) (Math.cos(Math.toRadians(azimuth)) * Math.cos(Math.toRadians(altitude)));
				
	}
	
	public float getElongation() {
		return elong;
	}
	
	public float getMagnitude() {
		return magnitude;
	}
	
	public float distanceToMoon(float[] line) {
		
		
		float[] moon = {(float) -x, (float) -y, (float) -z};
		float[] origin = {0, 0 ,0};
		
		float numerator = Vector.length(Vector.crossProduct(line, Vector.minus(origin, moon)));
		float denominator = line.length;
		
		return numerator/denominator; 

		
	}
	
}

