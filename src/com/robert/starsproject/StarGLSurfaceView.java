package com.robert.starsproject;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Toast;
 

@SuppressLint("FloatMath")
public class StarGLSurfaceView extends GLSurfaceView implements ErrorHandler
{	
	private StarRenderer renderer;
	
	// Offsets for touch events	 
    private float previousX;
    
    private float previousY;
    
    PointF start = new PointF();
    PointF mid = new PointF();
    
    private float density;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetectorCompat mDetector; 
    private float mScaleFactor = 1.f;
    private float oldDist = 1f;
    
    private int pointers;
    
    private Context context;
      	
	public StarGLSurfaceView(Context context) 
	{
		super(context);		
		this.context = context;
		
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		mDetector = new GestureDetectorCompat(context, new LongClickListener());
		
		

		
	}
	
	public StarGLSurfaceView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);	
		
	}
	
	@Override
	public void handleError(final ErrorType errorType, final String cause) {
		// Queue on UI thread.
		post(new Runnable() {
			@Override
			public void run() {
				final String text;

				switch (errorType) {
				case BUFFER_CREATION_ERROR:
					text = String
					.format(getContext().getResources().getString(
							R.string.lesson_eight_error_could_not_create_vbo), cause);
					break;
				default:
					text = String.format(
							getContext().getResources().getString(
									R.string.lesson_eight_error_unknown), cause);
				}

				Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();

			}
		});
	}

	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	
	private int mode = NONE;
			
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		
		
		
		mScaleDetector.onTouchEvent(event);
		mDetector.onTouchEvent(event);
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		
			case MotionEvent.ACTION_DOWN: {
			
				start.set(event.getX(), event.getY());
				mode = DRAG;
				break;
			
			}
			
			
			
			case MotionEvent.ACTION_POINTER_DOWN: {
				
			
			
				oldDist = fingerDist(event);
				
				if (oldDist > 10f) {
					
					midPoint(mid, event);
					mode = ZOOM;	
				}
				
				break;
			}
	
			
			
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP: {
				mode = NONE;
			//	Log.e("TAG", "mode=NONE");
			}
			case MotionEvent.ACTION_MOVE: {
				if (mode == DRAG) {
					
					//renderer.setUnselected();
					
        			float deltaX = (event.getX() - start.x) / density / 2f;
        			float deltaY = (event.getY() - start.y) / density / 2f;
        			
        			if (deltaX > 0.5f || deltaY > 0.5f) {
        				
    					renderer.setUnselected();
        			}
        			
        			float zoom = renderer.getZoom();
				
        			renderer.deltaX -= deltaX * zoom;
        			renderer.deltaY += deltaY * zoom;
        			
        			start.set(event.getX(), event.getY());
        

				
				} else if (mode == ZOOM) {
					

					
			/*		float newDist = fingerDist(event);
					if (newDist > 10f) {
						float scale = newDist/oldDist;
						oldDist = newDist;
						renderer.zoom(scale);
					} 
					*/
					
				}
    			break;
				
		
			}
			
		
        }

		return true;
		
	}
	
	private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor = renderer.getZoom();
			
			mScaleFactor /= (detector.getScaleFactor());

			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(0.00001f, Math.min(mScaleFactor, 1.0f));

			
			//Log.e("scale", "scale: " + detector.getScaleFactor());
			renderer.zoomWithoutUpdate(mScaleFactor);
		//	Log.e("tag", "" + mScaleFactor);
    	

			return true;
			
			
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector s) {
			
			renderer.setUnselected();
			//Log.e("SURFACE", "beginning scale");
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector arg0) {
			//Log.e("SURFACE", "ending scale");
			renderer.zoom(mScaleFactor);
			invalidate();
			
		}
		
		
		

	}
	
    class LongClickListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures"; 
        
        @Override
        public boolean onDown(MotionEvent event) { 
            //Log.d(DEBUG_TAG,"onDown: " + event.toString()); 
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            //Log.d(DEBUG_TAG, "onLongPress: " + event.toString()); 
            renderer.chooseSelection(event.getX(), event.getY());
            return true;
        }
    }
	
	private float fingerDist(MotionEvent event){

	    float x = event.getX(0) - event.getX(1);
	    float y = event.getY(0) - event.getY(1);
		//Log.e("TAG", "x is" + x + ", y is" + y);
	    return FloatMath.sqrt(x * x + y * y);
	}
	
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	// Hides superclass method.
	public void setRenderer(StarRenderer renderer, float density) 
	{
		this.renderer = renderer;
		this.density = density;
		super.setRenderer(renderer);
	}
	

	
	
}
