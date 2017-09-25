package com.robert.starsproject;

public class Quaternion {
	
	private float x;
	private float y;
	private float z;
	private float w;
	
	public Quaternion(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		
	}
	
	public Quaternion getConjugate()
	{
		return new Quaternion(-x, -y, -z, w);
	}
	
	
	public void normalise()
	{
		// Don't normalize if we don't have to
		float mag2 = w * w + x * x + y * y + z * z;

		float mag = (float) Math.sqrt(mag2);
		w /= mag;
		x /= mag;
		y /= mag;
		z /= mag;
		
	}

	//Multiplying q1 with q2 applies the rotation q2 to q1
	Quaternion multiply(Quaternion rq) 
	{
	// the constructor takes its arguments as (x, y, z, w)
	return new Quaternion(w * rq.x + x * rq.w + y * rq.z - z * rq.y,
	                  w * rq.y + y * rq.w + z * rq.x - x * rq.z,
	                  w * rq.z + z * rq.w + x * rq.y - y * rq.x,
	                  w * rq.w - x * rq.x - y * rq.y - z * rq.z);
	}
	
	
	/*float[] operator(float[] vec) {
	{
		vec = Vector.normalise(vec);
	 
		Quaternion vecQuat, resQuat;
		vecQuat.x = vec[0];
		vecQuat.y = vec[1];
		vecQuat.z = vec[2];
		vecQuat.w = 0.0f;
	 
		resQuat = vecQuat * this.getConjugate();
		resQuat = *this * resQuat;
	 
		return (Vector3(resQuat.x, resQuat.y, resQuat.z));
	} */
}


