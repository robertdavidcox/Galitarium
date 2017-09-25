precision mediump float;       	// Set the default precision to medium. We don't need as high of a 						
uniform sampler2D tex;
varying vec4 vColor;
vec2 p = gl_PointCoord * 2.0 - vec2(1.0);
float theta = atan(p.y,p.x);

void main () {

//if(dot(p,p) > 0.5*(exp(cos(theta*5.0)*0.75)) ) {
  //discard;
//} else {
 // gl_FragColor = vColor * texture2D(tex, gl_PointCoord);
 // }

	 if(dot(gl_PointCoord-0.5,gl_PointCoord-0.5)>0.25) {
  		 discard;
  	} else {
    	gl_FragColor = vColor * texture2D(tex, gl_PointCoord);
	}

}                                             