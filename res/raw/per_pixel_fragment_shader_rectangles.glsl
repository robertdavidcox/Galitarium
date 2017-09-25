precision mediump float;       	// Set the default precision to medium. We don't need as high of a 
uniform sampler2D tex;
varying vec4 vColor;


void main () {



    	gl_FragColor = vColor * texture2D(tex, gl_PointCoord);

    	  if (gl_FragColor.r < 0.5)
               // alpha value less than user-specified threshold?
            {
               discard; // yes: discard this fragment
            }
     
           

}                                             