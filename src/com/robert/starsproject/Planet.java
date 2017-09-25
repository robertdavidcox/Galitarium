package com.robert.starsproject;

import android.text.format.Time;
import android.util.Log;

public class Planet {
	
	private double RADS = Math.PI/180;
	private double DEGS = 180/Math.PI;
	private double EPS = 1.0e-12;
	
	// Time/date information
	Time t;
	private int year;
	private int month;
	private int day;
	private int hour;
	private int min;
	private int sec;
	private	float dayNumber;
	private double numCenturies;
	
	// Planet Orbital Elements
	private	double meanDistancePlanet;
	private	double eccentricityPlanet;
	private	double inclinationPlanet;
	private	double lonAscendingPlanet;
	private	double lonPerihelionPlanet;
	private	double meanLonPlanet;
	
	// Earth Orbital Elements
	private	double meanDistanceEarth;
	private	double eccentricityEarth;
	private	double inclinationEarth;
	private	double lonAscendingEarth;
	private	double lonPerihelionEarth;
	private	double meanLonEarth;
	
	// Planet Anomalies
	private	double meanAnomalyPlanet;
	private	double trueAnomalyPlanet;
	private	double rp;
	
	private float radius;
	
	// Earth Anomalies
	private	double me;
	private	double ve;
	private	double re;
	
	// Heliocentric coordinates of planet
	private	double helioXPlanet;
	private	double helioYPlanet;
	private	double helioZPlanet;
	private float heliocentricDistance;
	
	// Heliocentric coordinates of earth
	private	double helioXEarth;
	private	double helioYEarth;
	private	double helioZEarth;
	
	// Geocentric coordinates
	private	double geoX;
	private	double geoY;
	private	double geoZ;
	
	// Equitorial coordinates
	private double ecl;
	private double xeq;
	private double yeq;
	private double zeq;
	
	// RA and DEC
	private double rightAscension;
	private double declination;
	private double RVEC;
	
	//ALT and AZ
	private double altitude;
	private double azimuth;
	
	//X, Y, Z
	private double x;
	private double y;
	private double z;
	
	private String name;
	
	private float d0;
	private float distanceToSun;
	private float magnitude;
	private float FV;
	private float elong;
	private float phase;
	
	private float eclipticLongitude;
	private float eclipticLatitude;
	
	
	private SolarSystemObject solarSystemObject;
	
	public Planet(SolarSystemObject solarSystemObject, float lat, float lon, float radius,
				int year, int month, int day, int hour, int minute, int second, String name,
				float distanceToSun) {
	
		this.solarSystemObject = solarSystemObject;
		
		this.radius = radius;
		this.name = name;
		
		this.year = year;
		this.month = month + 1;
		this.day = day;
		this.hour = hour;
		this.min = minute;
		this.sec = second;
		
		this.dayNumber = calcDayNumber();
		//Log.e("PLANET", "dayNumber: " + dayNumber);
		
		this.numCenturies = dayNumber/36525.0;
		
		computeMeanOrbitalElementsOfPlanet();
		computeMeanOrbitalElementsOfEarth();
		
	    computeCoordinates();
	    computeHorizonCoordinates(rightAscension, declination, lat, lon); 
	    computeXYZ();
	    this.distanceToSun = distanceToSun;
	    
	    if (!solarSystemObject.equals(SolarSystemObject.Sun)) {
	    	
		    computePhase();

		 
	    }
	    computeMagnitude();

	}
	
	
	private float calcDayNumber( ) {

		float h = (float) (hour + (min/60.0));
		dayNumber = (float) (367.0*year 
		           - Math.floor(7.0*(year + Math.floor((month + 9.0)/12.0))/4.0) 
		           + Math.floor(275.0*month/9.0) + day - 730531.5 + h/24.0); 

		return dayNumber;
		
	}
	
	private void computeXYZ() {
		
		x = (float) (-Math.sin(Math.toRadians(azimuth)) * Math.cos(Math.toRadians(altitude)));
		y = (float) (Math.sin(Math.toRadians(altitude)));
		z = (float) (Math.cos(Math.toRadians(azimuth)) * Math.cos(Math.toRadians(altitude)));
				
	}

	private void computePhase() {
		
		float R = (float) this.RVEC;
		float r = this.heliocentricDistance;
		float s = distanceToSun;
				
		elong = (float) Math.acos((s*s + R*R - r*r)/(2*s*R));
		FV = (float) Math.acos((r*r + R*R - s*s)/(2*r*R));
		
		//Log.e("PLANET", "planet: " + solarSystemObject.toString() + ", FV: " + FV*DEGS);
		
		phase = (float) ((1 + Math.cos(FV))/2);
		
	/*	Log.e("PLANET", "----------------");
		Log.e("PLANET", "elong: " + elong*DEGS);
		Log.e("PLANET", "FV: " + FV*DEGS);
		Log.e("PLANT", "heliocentric distance: " + r);
		Log.e("PLANT", "geocentric distance: " + R);
		Log.e("PLANT", "distance to sun: " + distanceToSun);
		Log.e("PLANET", "phase of planet " + solarSystemObject.toString() + ": " + phase*DEGS);*/
		
	}
	
	private void computeMagnitude() {
		
		float R = (float) this.RVEC;
		float r = this.heliocentricDistance;
		
		
		switch(solarSystemObject) {
		case Sun:
	        magnitude = -34;
			break;
		case Mercury: 
			magnitude = (float) (-0.36 + 5*Math.log10(r*R) + 0.027 * FV + 2.2E-13*Math.pow(FV, 6));
			break;
		case Venus:
			magnitude = (float) (-4.34 + 5*Math.log10(r*R) + 0.013 * FV + 4.2E-7*Math.pow(FV, 3));
			break;
		case Mars:
			magnitude = (float) (-1.51 + 5*Math.log10(r*R) + 0.016 * FV);
			break;
		case Jupiter:
			magnitude = (float) (-9.25 + 5*Math.log10(r*R) + 0.014 * FV);
			break;
		case Saturn:
			magnitude = (float) (-9.0 + 5*Math.log10(r*R) + 0.044 * FV);
			break;
		case Uranus:
			magnitude = (float) (-7.15 + 5*Math.log10(r*R) + 0.001 * FV);
			break;
		case Neptune:
			magnitude = (float) (-6.90 + 5*Math.log10(r*R) + 0.001 * FV);
			break;
		case Pluto:
			magnitude = 0;

			break;
		default:
			Log.e("TAG", "Error computing magnitude of planet");
		}
		
		//Log.e("PLANET", "magnitude of " + solarSystemObject.toString() + " is: " + magnitude);
		
	}
	
	private void computeCoordinates() {
		
		meanAnomalyPlanet = mod2pi(meanLonPlanet - lonPerihelionPlanet);
		trueAnomalyPlanet = computeTrueAnomaly(meanAnomalyPlanet, eccentricityPlanet);
		rp = meanDistancePlanet * (1 - eccentricityPlanet*eccentricityPlanet)/(1 + eccentricityPlanet*Math.cos(trueAnomalyPlanet));
	
		helioXPlanet = rp*(Math.cos(lonAscendingPlanet)*Math.cos(trueAnomalyPlanet + lonPerihelionPlanet - lonAscendingPlanet) -
						Math.sin(lonAscendingPlanet)*Math.sin(trueAnomalyPlanet + lonPerihelionPlanet - lonAscendingPlanet)*Math.cos(inclinationPlanet));
		helioYPlanet = rp*(Math.sin(lonAscendingPlanet)*Math.cos(trueAnomalyPlanet + lonPerihelionPlanet - lonAscendingPlanet) +
						Math.cos(lonAscendingPlanet)*Math.sin(trueAnomalyPlanet + lonPerihelionPlanet - lonAscendingPlanet)*Math.cos(inclinationPlanet));
		helioZPlanet = rp*(Math.sin(trueAnomalyPlanet + lonPerihelionPlanet - lonAscendingPlanet)*Math.sin(inclinationPlanet));
		
		heliocentricDistance = (float) rp; //(float) (Math.sqrt(helioXPlanet*helioXPlanet + helioYPlanet*helioYPlanet + helioZPlanet*helioZPlanet));
		
		if (solarSystemObject == SolarSystemObject.Sun) {
			helioXPlanet = 0;
			helioYPlanet = 0;
			helioZPlanet = 0;
			
			float g = (float) (357.52910 + 0.9856003 * dayNumber);
			g = (float) (mod2pi(g*RADS)*DEGS);
			//Log.e("PLANET", "g is " + g);
			
			float L = (float) (280.461 + 0.9856474 * dayNumber);
			
			L = (float) (mod2pi(L*RADS)*DEGS);
		//	Log.e("PLANET", "l is " + L);
			
			this.eclipticLongitude = (float)( (L + 1.915*Math.sin(g*RADS) + 0.02*Math.sin(2*g*RADS))*RADS);
			//Log.e("PLANET", "lamda is " + this.eclipticLongitude*DEGS);
			
			this.eclipticLatitude = (float) (0);
			
		}
			
		me = mod2pi(meanLonEarth - lonPerihelionEarth);
		ve = computeTrueAnomaly(me, eccentricityEarth);
		re = meanDistanceEarth * (1 - eccentricityEarth*eccentricityEarth)/(1 + eccentricityEarth*Math.cos(ve));
		
	    helioXEarth = re*Math.cos(ve + lonPerihelionEarth);
	    helioYEarth = re*Math.sin(ve + lonPerihelionEarth);
	    helioZEarth = 0.0;
	    
	    geoX = helioXPlanet - helioXEarth;
	    geoY = helioYPlanet - helioYEarth;
	    geoZ = helioZPlanet - helioZEarth;
			
	    ecl = 23.429281 * RADS;
	    xeq = geoX;
	    yeq = geoY * Math.cos(ecl) - geoZ * Math.sin(ecl);
	    zeq = geoY * Math.sin(ecl) + geoZ * Math.cos(ecl);
  
	    rightAscension = mod2pi(Math.atan2(yeq, xeq))*DEGS;	
	    declination = Math.atan(zeq/Math.sqrt(xeq*xeq + yeq*yeq))*DEGS;
	    RVEC = Math.sqrt(xeq*xeq + yeq*yeq + zeq*zeq);
	}
	
	private void computeMeanOrbitalElementsOfPlanet() {
		
		switch(solarSystemObject) {
			case Sun:
		        meanDistancePlanet = 1.00000011 - 0.00000005 * numCenturies;
		        eccentricityPlanet = 0.01671022 - 0.00003804 * numCenturies;
		        inclinationPlanet = (0.00005 - 46.94 * numCenturies/3600)*RADS;
		        lonAscendingPlanet = (-11.26064 - 18228.25 * numCenturies/3600)*RADS;
		        lonPerihelionPlanet = (102.94719 +  1198.28 * numCenturies/3600)*RADS;
		        meanLonPlanet = mod2pi((100.46435 + 129597740.63 * numCenturies/3600)*RADS);
				break;
			case Mercury: 
				meanDistancePlanet = 0.38709893 + 0.00000066*numCenturies;
		        eccentricityPlanet = 0.20563069 + 0.00002527*numCenturies;
		        inclinationPlanet = (7.00487 - 23.51*numCenturies/3600)*RADS;
		        lonAscendingPlanet = (48.33167 - 446.30*numCenturies/3600)*RADS;
		        lonPerihelionPlanet = (77.45645  + 573.57*numCenturies/3600)*RADS;
		        meanLonPlanet = mod2pi((252.25084 + 538101628.29*numCenturies/3600)*RADS);
				break;
			case Venus:
		        meanDistancePlanet = 0.72333199 + 0.00000092*numCenturies;
		        eccentricityPlanet = 0.00677323 - 0.00004938*numCenturies;
		        inclinationPlanet = (  3.39471 -   2.86*numCenturies/3600)*RADS;
		        lonAscendingPlanet = ( 76.68069 - 996.89*numCenturies/3600)*RADS;
		        lonPerihelionPlanet = (131.53298 - 108.80*numCenturies/3600)*RADS;
		        meanLonPlanet = mod2pi((181.97973 + 210664136.06*numCenturies/3600)*RADS);
				break;
			case Mars:
		        meanDistancePlanet = 1.52366231 - 0.00007221*numCenturies;
		        eccentricityPlanet = 0.09341233 + 0.00011902*numCenturies;
		        inclinationPlanet = (  1.85061 -   25.47*numCenturies/3600)*RADS;
		        lonAscendingPlanet = ( 49.57854 - 1020.19*numCenturies/3600)*RADS;
		        lonPerihelionPlanet = (336.04084 + 1560.78*numCenturies/3600)*RADS;
		        meanLonPlanet = mod2pi((355.45332 + 68905103.78*numCenturies/3600)*RADS);
				break;
			case Jupiter:
		        meanDistancePlanet = 5.20336301 + 0.00060737*numCenturies;
		        eccentricityPlanet = 0.04839266 - 0.00012880*numCenturies;
		        inclinationPlanet = (  1.30530 -    4.15*numCenturies/3600)*RADS;
		        lonAscendingPlanet = (100.55615 + 1217.17*numCenturies/3600)*RADS;
		        lonPerihelionPlanet = ( 14.75385 +  839.93*numCenturies/3600)*RADS;
		        meanLonPlanet = mod2pi((34.40438 + 10925078.35*numCenturies/3600)*RADS);
				break;
			case Saturn:
		        meanDistancePlanet = 9.53707032 - 0.00301530*numCenturies;
		        eccentricityPlanet = 0.05415060 - 0.00036762*numCenturies;
		        inclinationPlanet = (  2.48446 +    6.11*numCenturies/3600)*RADS;
		        lonAscendingPlanet = (113.71504 - 1591.05*numCenturies/3600)*RADS;
		        lonPerihelionPlanet = ( 92.43194 - 1948.89*numCenturies/3600)*RADS;
		        meanLonPlanet = mod2pi((49.94432 + 4401052.95*numCenturies/3600)*RADS);
				break;
			case Uranus:
		        meanDistancePlanet = 19.19126393 + 0.00152025*numCenturies;
		        eccentricityPlanet =  0.04716771 - 0.00019150*numCenturies;
		        inclinationPlanet = (  0.76986  -    2.09*numCenturies/3600)*RADS;
		        lonAscendingPlanet = ( 74.22988  - 1681.40*numCenturies/3600)*RADS;
		        lonPerihelionPlanet = (170.96424  + 1312.56*numCenturies/3600)*RADS;
		        meanLonPlanet = mod2pi((313.23218 + 1542547.79*numCenturies/3600)*RADS);
				break;
			case Neptune:
		        meanDistancePlanet = 30.06896348 - 0.00125196*numCenturies;
		        eccentricityPlanet =  0.00858587 + 0.00002510*numCenturies;
		        inclinationPlanet = (  1.76917  -   3.64*numCenturies/3600)*RADS;
		        lonAscendingPlanet = (131.72169  - 151.25*numCenturies/3600)*RADS;
		        lonPerihelionPlanet = ( 44.97135  - 844.43*numCenturies/3600)*RADS;
		        meanLonPlanet = mod2pi((304.88003 + 786449.21*numCenturies/3600)*RADS);
				break;
			case Pluto:
		        meanDistancePlanet = 39.48168677 - 0.00076912*numCenturies;
		        eccentricityPlanet =  0.24880766 + 0.00006465*numCenturies;
		        inclinationPlanet = ( 17.14175  +  11.07*numCenturies/3600)*RADS;
		        lonAscendingPlanet = (110.30347  -  37.33*numCenturies/3600)*RADS;
		        lonPerihelionPlanet = (224.06676  - 132.25*numCenturies/3600)*RADS;
		        meanLonPlanet = mod2pi((238.92881 + 522747.90*numCenturies/3600)*RADS);

				break;
			default:
				Log.e("TAG", "Error computing planet orbital elements");
				
		}
		
	}
	
	private void computeMeanOrbitalElementsOfEarth() {
		
		double cy = dayNumber/36525;
        meanDistanceEarth = 1.00000011 - 0.00000005*cy;
        eccentricityEarth = 0.01671022 - 0.00003804*cy;
        inclinationEarth = (  0.00005 -    46.94*cy/3600)*RADS;
        lonAscendingEarth = (-11.26064 - 18228.25*cy/3600)*RADS;
        lonPerihelionEarth = (102.94719 +  1198.28*cy/3600)*RADS;
        meanLonEarth = mod2pi((100.46435 + 129597740.63*cy/3600)*RADS);
		
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
	
	public void computeHorizonCoordinates(double RA, double DEC,
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
		
		this.altitude = alt;
		this.azimuth = az;
		
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
	
	// return an angle in the range 0 to 2pi radians
	private double mod2pi(double x)
	{
	    double b = (float) (x/(2*Math.PI));
	    double a = (2*Math.PI)*(b - abs_floor(b));  
	    if (a < 0) a = (2*Math.PI) + a;
	    return a;
	}
	
	// return the integer part of a number
	private double abs_floor(double x)
	{
	    double r;
	    if (x >= 0.0) r = Math.floor(x);
	    else          r = Math.ceil(x);
	    return r;
	}
	
	public double getRA() {
		return rightAscension;
	}
	
	public double getDEC() {
		return declination;
	}
	
	public double getALT() {
		return altitude;
	}
	
	public double getAZ() {
		return azimuth;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public double getRVEC() {
		///Log.e("PLANET", "planet distance in km is " + RVEC * 149597871);
		return RVEC * 100;
	}
	
	public float getRadius() {
		return radius*100;
	}

	public float distanceToPlanet(float[] line) {
		
		
		float[] planet = {(float) -x, (float) -y, (float) -z};
		float[] origin = {0, 0 ,0};
		
		float numerator = Vector.length(Vector.crossProduct(line, Vector.minus(origin, planet)));
		float denominator = line.length;
		
		return numerator/denominator; 

		
	}

	public String getName() {
		return name;
	}

	public float getMagnitude() {
		return magnitude;
	}
	
	public float getPhase() {
		return (float) (180 -(FV*DEGS));
	}
	
	
	
	public float getEclipticLongitude() {
		return eclipticLongitude;
	}
	
	public float getEclipticLatitude() {
		return eclipticLatitude;
	}

}
