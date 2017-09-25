package com.robert.starsproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Calendar;
import java.util.LinkedList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.robert.starsproject.ErrorHandler.ErrorType;

public class StarRenderer implements GLSurfaceView.Renderer {
	/** Used for debug logs. */
	private static final String TAG = "StarRenderer";

	/** References to other classes */
	private StarCollector starCollector;
	private final ErrorHandler errorHandler;
	private Context context;

	/** Constants **/
	private float scaleFactor = 149597871;
	private double RADS = Math.PI / 180;
	private double DEGS = 180 / Math.PI;
	private int NUM_STARS = 1200;
	private int MIN_STARS = 1200;
	private float STAR_SCALE = 1;
	
	/** Retain the most recent delta for touch events. */
	public volatile float deltaX;
	public volatile float deltaY;
	
	/** Rotation info for augmented reality */
	public volatile float deltaPitch;
	public volatile float deltaYaw;
	public volatile float deltaRoll;
	
	private Cursor starCursor;

	
	/** Celestial objects */
	private Star[] stars;
	private Moon moon;
	private Planet mercury;
	private Planet venus;
	private Planet mars;
	private Planet jupiter;
	private Planet saturn;
	private Planet uranus;
	private Planet neptune;
	private Planet pluto;
	private Planet sun;
	private Planet[] planets;
	
	private JovianMoon jovianMoons;
	
	/** Celestial Graphic Objects */
	private CelestialArray celestialArray;
	private Circle moonGraphic;
	private Circle mercuryGraphic;
	private Circle venusGraphic;
	private Circle marsGraphic;
	private Circle jupiterGraphic;
	private Circle saturnGraphic;
	private Circle uranusGraphic;
	private Circle neptuneGraphic;
	private Circle plutoGraphic;
	private Circle sunGraphic;
	private Circle ioGraphic;
	private Circle europaGraphic;
	private Circle ganymedeGraphic;
	private Circle callistoGraphic;
	
	private Circle[] circles = new Circle[]{moonGraphic, mercuryGraphic,
									venusGraphic, marsGraphic,
									jupiterGraphic, saturnGraphic,
									uranusGraphic, neptuneGraphic,
									plutoGraphic, sunGraphic,
									ioGraphic, europaGraphic,
									ganymedeGraphic, callistoGraphic,
									ioGraphic
	};
			
	
	private Equatorial equatorial;

	private InfoRectangle selectionRectangle;
	private InfoRectangle mercuryTextGraphic;
	private InfoRectangle venusTextGraphic;
	private InfoRectangle marsTextGraphic;
	private InfoRectangle jupiterTextGraphic;
	private InfoRectangle saturnTextGraphic;
	private InfoRectangle uranusTextGraphic;
	private InfoRectangle neptuneTextGraphic;
	private InfoRectangle plutoTextGraphic;
	private InfoRectangle sunTextGraphic;
	private InfoRectangle moonTextGraphic;
	
	private Constellation orion;
	private Constellation crux;
	private Constellation dipper;
	private Constellation littleBear;
	private Constellation capricornus;
	private Constellation bootes;
	private Constellation lyra;
	private Constellation eridanus;
	private Constellation scorpius;
	private Constellation andromeda;
	private Constellation auriga;
	private Constellation leo;
	private Constellation carina;
	private Constellation cygnus;
	private Constellation canis_minor;
	private Constellation hydra;
	private Constellation cetus;
	private Constellation pegasus;
	private Constellation gemini;
	private Constellation aquila;
	private Constellation ophiuchus;
	private Constellation canis_major;
	private Constellation grus;
	private Constellation centaurus;
	
	private LinkedList<Constellation> constellations = new LinkedList<Constellation>();
	
	private InfoRectangle[] degrees;

	/** Texture data handle **/
	private int mercuryTextureHandle;
	private int venusTextureHandle;
	private int marsTextureHandle;
	private int jupiterTextureHandle;
	private int saturnTextureHandle;
	private int uranusTextureHandle;
	private int neptuneTextureHandle;
	private int plutoTextureHandle;
	private int sunTextureHandle;
	private int whiteTextureHandle;
	private int ioTextureHandle;
	private int europaTextureHandle;
	private int ganymedeTextureHandle;
	private int callistoTextureHandle;
	private int moonTextureHandle;
	private int zero_degreesTextureHandle;
	private int thirty_degreesTextureHandle;
	private int minus_thirty_degreesTextureHandle;
	private int ninety_degreesTextureHandle;
	private int one_eighty_degreesTextureHandle;
	private int two_seventy_degreesTextureHandle;
	private int selectionTextureHandle;
	private int mercuryTextTextureHandle;
	private int venusTextTextureHandle;
	private int marsTextTextureHandle;
	private int jupiterTextTextureHandle;
	private int saturnTextTextureHandle;
	private int uranusTextTextureHandle;
	private int neptuneTextTextureHandle;
	private int plutoTextTextureHandle;
	private int sunTextTextureHandle;
	private int moonTextTextureHandle;

	/** Handles for program */
	private int program;
	private int mvpMatrixUniform;
	private int mvMatrixUniform;
	private int lightPosUniform;
	private int textureUniform;
	private int positionAttribute;
	private int normalAttribute;
	private int colorAttribute;
	private int textureCoordinateAttribute;
	
	/** Handles for point program */
	private int programPoints;
	private int pointmvpMatrixUniform;
	private int pointTextureUniform;
	private int pointPositionAttribute;
	private int pointColorAttribute;
	private int pointSizeAttribute;
	private int pointZoomUniform;
	private static final String MVP_MATRIX_UNIFORM = "u_MVPMatrix";
	private static final String MV_MATRIX_UNIFORM = "u_MVMatrix";
	private static final String LIGHT_POSITION_UNIFORM = "u_LightPos";
	private static final String POSITION_ATTRIBUTE = "a_Position";
	private static final String NORMAL_ATTRIBUTE = "a_Normal";
	private static final String COLOR_ATTRIBUTE = "a_Color";
	private static final String TEXTURE_COORDINATE_ATTRIBUTE = "a_TexCoordinate";
	private static final String TEXTURE_UNIFORM = "u_Texture";
	
	/** Handles for rectangle program */
	private int programRectangles;
	private int rectanglemvpMatrixUniform;
	private int rectangleTextureUniform;
	private int rectanglePositionAttribute;
	private int rectangleColorAttribute;
	private int rectangleSizeAttribute;
	private static final String POINT_COLOR_ATTRIBUTE = "aColor";
	private static final String POINT_SIZE_ATTRIBUTE = "aSize";
	private static final String POINT_POSITION_ATTRIBUTE = "aPosition";
	private static final String POINT_TEXTURE_UNIFORM = "tex";
	private static final String POINT_ZOOM_UNIFORM = "zoom_factor";

	/** Additional constants. */
	private static final int POSITION_DATA_SIZE_IN_ELEMENTS = 3;
	private static final int NORMAL_DATA_SIZE_IN_ELEMENTS = 3;
	private static final int COLOR_DATA_SIZE_IN_ELEMENTS = 4;
	private static final int TEXTURE_DATA_SIZE_IN_ELEMENTS = 2;
	private static final int BYTES_PER_FLOAT = 4;
	private static final int BYTES_PER_SHORT = 2;
	private static final int STRIDE = (POSITION_DATA_SIZE_IN_ELEMENTS
								+ NORMAL_DATA_SIZE_IN_ELEMENTS + COLOR_DATA_SIZE_IN_ELEMENTS
								+ TEXTURE_DATA_SIZE_IN_ELEMENTS) * BYTES_PER_FLOAT;
	private static final int POINT_STRIDE = (POSITION_DATA_SIZE_IN_ELEMENTS + 4 + 1) * BYTES_PER_FLOAT;

	/** Positions of the light source in different spaces */
	private final float[] lightPosInModelSpace = new float[] { 0.0f, 0.0f,
			0.0f, 1.0f };
	private final float[] lightPosInWorldSpace = new float[4];
	private final float[] lightPosInEyeSpace = new float[4];

	/** MAtrix used to convert model space to world space */
	private final float[] modelMatrix = new float[16];

	/** Transforms world space to eye space */
	private final float[] viewMatrix = new float[16];

	private final float[] modelViewMatrix = new float[16];
	
	/** Projects the 3D scene to viewport */
	private final float[] projectionMatrix = new float[16];
	private float ratio;
	private float left;
	private float right;
	private float bottom;
	private float top;
	private float near;
	private float far;
	
	private float zoom;
	private float newZoom;
	private float zoomDiff;

	private float[] eye_vec = {0f, 0f, 0f};
	private float[] look_vec = {0f, 0f, 1f};
	private float[] up_vec = {0f, 1f, 0f};
	private float[] right_vec = {-1f, 0f, 0f};
	
	private float pitch = 0;
	private float yaw = 0;
	private float roll = 0;
	private float newPitch = 0;
	private float newYaw = 0;
	private float newRoll = 0;	
	private float pitchDiff = 0;
	private float yawDiff = 0;
	private float rollDiff = 0;
	private float minPitch = 0;
	private float maxPitch = 0;
	
	private float planetYaw = 0;
	private float planetPitch = 0;
	
	private Location currentLocation;
	private Calendar UTCTime;
	
	private int counter = 0;
	private boolean firstTime = true;

	private float selectionX;
	private float selectionY;
	private boolean selected = false;
	
	private LoadStarsTask task;

	/** Flags */
	private boolean augmentedReality = false;
	private boolean equatorialStatus = true;
	private boolean constellationStatus = true;
	private boolean planetTextsStatus = true;
	private boolean alteredLocationTime = false;
	private boolean alteredZoom = false;
	private boolean findingPlanet = false;
	private boolean selectedStar = false;

	/** The final combined matrix */
	private final float[] mvpMatrix = new float[16];

	/** Additional matrices. */
	private final float[] accumulatedRotation = new float[16];
	private final float[] currentRotation = new float[16];
	private final float[] lightModelMatrix = new float[16];
	private final float[] temporaryMatrix = new float[16];

	private boolean starsLoading = false;
	private boolean newStars = false;
	
	public StarRenderer(final Context context, ErrorHandler errorHandler,
			StarCollector starCollector, GLSurfaceView s) {
		this.context = context;
		this.errorHandler = errorHandler;
		this.starCollector = starCollector;
	}

	/** Surface Methods */
	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

		this.zoom = 1;
		OpenGLES20Activity a = (OpenGLES20Activity) context;
		this.currentLocation = a.getCurrentLocation();
		
		UTCTime = a.getUTCTime();
		
		
		GLES20.glClearColor(0.0196f, 0.03921569f, 0.0588235f, 1.0f);
		//GLES20.glClearColor(1f, 1f, 1f, 1.0f);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		
		
		//a.changeLocationToCurrent();
		buildShaders();
		

		// Set the view matrix

		Log.e(TAG, "matrix look: (" + look_vec[0] + ", " + look_vec[1] + ", " + look_vec[2] + ")");
		Log.e(TAG, "matrix eye: (" + eye_vec[0] + ", " + eye_vec[1] + ", " + eye_vec[2] + ")");
		
		GLES20.glUseProgram(program);
		loadPlanetStarTextures();
		loadOtherTextures();
		
		
		GLES20.glEnable( GLES20.GL_DEPTH_TEST );
		GLES20.glDepthFunc( GLES20.GL_LEQUAL );
		GLES20.glDepthMask( true );
		
		// Initialize the accumulated rotation matrix
		Matrix.setIdentityM(accumulatedRotation, 0);

		//GLES20.glUseProgram(program);
		// Build all universe objects
		buildPlanets();
		buildTexts();
		buildLines();
		buildStars();


		look_vec = getLookVector();
		up_vec = getUpVector();
		right_vec = getRightVector();
		setMinMaxPitch();
		yaw = 0;
		Matrix.setLookAtM(viewMatrix, 0, eye_vec[0], eye_vec[1], eye_vec[2],
				look_vec[0], look_vec[1], look_vec[2],
				up_vec[0], up_vec[1], up_vec[2]);
		
	    final Runnable updateRunnable = new Runnable() {
	        public void run() {
	            //call the activity method that updates the UI
	    		OpenGLES20Activity a = (OpenGLES20Activity) context;
	            RelativeLayout overlay = (RelativeLayout) a.findViewById(R.id.splash);
	            overlay.setVisibility(RelativeLayout.INVISIBLE);
	        }
	    };
	    
		 Activity activity=(Activity) this.context; 
		    activity.runOnUiThread(updateRunnable); 
		    
		   a.firstTimeDialog();

		Button b = (Button) a.findViewById(R.id.reset_button);
		b.setOnClickListener(new OnClickListener() {
			 
			@Override
			public void onClick(View arg0) {
				resetView();
				
 
			}
 
		});

		    
	}
	
	private void checkLoadCorrectly() {
		

		    
		
		
		
	}
		
	private void buildShaders() {
		
		// Build shaders
		final String vertexShader = RawResourceReader
				.readTextFileFromRawResource(context,
						R.raw.per_pixel_vertex_shader);
		final String fragmentShader = RawResourceReader
				.readTextFileFromRawResource(context,
						R.raw.per_pixel_fragment_shader);


		final int vertexShaderHandle = ShaderHelper.compileShader(
				GLES20.GL_VERTEX_SHADER, vertexShader);
		final int fragmentShaderHandle = ShaderHelper.compileShader(
				GLES20.GL_FRAGMENT_SHADER, fragmentShader);
		
		
		
		program = ShaderHelper.createAndLinkProgram(vertexShaderHandle,
				fragmentShaderHandle, new String[] { POSITION_ATTRIBUTE,
						COLOR_ATTRIBUTE, NORMAL_ATTRIBUTE,
						TEXTURE_COORDINATE_ATTRIBUTE });
		
		final String vertexShaderPoints = RawResourceReader
				.readTextFileFromRawResource(context,
						R.raw.per_pixel_vertex_shader_points);
		final String fragmentShaderPoints = RawResourceReader
				.readTextFileFromRawResource(context,
						R.raw.per_pixel_fragment_shader_points); 
		
		final int vertexShaderHandlePoints = ShaderHelper.compileShader(
				GLES20.GL_VERTEX_SHADER, vertexShaderPoints);
		final int fragmentShaderHandlePoints = ShaderHelper.compileShader(
				GLES20.GL_FRAGMENT_SHADER, fragmentShaderPoints); 

		programPoints = ShaderHelper.createAndLinkProgram(vertexShaderHandlePoints,
				fragmentShaderHandlePoints, new String[] { POINT_POSITION_ATTRIBUTE,
						POINT_COLOR_ATTRIBUTE, POINT_SIZE_ATTRIBUTE
						}); 
		
		final String vertexShaderRectangles = RawResourceReader
				.readTextFileFromRawResource(context,
						R.raw.per_pixel_vertex_shader_rectangles);
		final String fragmentShaderRectangles = RawResourceReader
				.readTextFileFromRawResource(context,
						R.raw.per_pixel_fragment_shader_rectangles); 
		
		final int vertexShaderHandleRectangles = ShaderHelper.compileShader(
				GLES20.GL_VERTEX_SHADER, vertexShaderRectangles);
		final int fragmentShaderHandleRectangles = ShaderHelper.compileShader(
				GLES20.GL_FRAGMENT_SHADER, fragmentShaderRectangles); 

		programRectangles = ShaderHelper.createAndLinkProgram(vertexShaderHandleRectangles,
				fragmentShaderHandleRectangles, new String[] { POINT_POSITION_ATTRIBUTE,
						POINT_COLOR_ATTRIBUTE, POINT_SIZE_ATTRIBUTE
						}); 
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {

		GLES20.glViewport(0, 0, width, height);
		
		this.ratio = (float) width / height;
		this.left = -ratio;
		this.right = ratio;
		this.bottom = -1.0f;
		this.top = 1.0f;
		this.near = 1.0f;
		this.far = 10000.0f;

		Matrix.frustumM(projectionMatrix, 0, zoom * left, zoom * right, zoom
				* bottom, zoom * top, near, far);
		
		//printMatrix(projectionMatrix);
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		
		//printMatrix(projectionMatrix);
		//Log.e(TAG, "error; " + GLES20.glGetError());
		

		
		OpenGLES20Activity a = (OpenGLES20Activity) context;
		this.currentLocation = a.getCurrentLocation();
		UTCTime = a.getUTCTime();
		
		//this.currentLocation = a.getCurrentLocation();
		
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		buildMainProgram();
		
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.setIdentityM(currentRotation, 0);
		
		//printMatrix(modelViewMatrix);
		
		

		
		
		if (findingPlanet) {
			
			if (counter < 100) {
			
				yaw += yawDiff/100;
				pitch += pitchDiff/100;
				
				Matrix.setIdentityM(accumulatedRotation, 0);
				
				yaw += deltaX;
				pitch += deltaY;
				//Log.e(TAG, "yaw: " + yaw + ", pitch: " + pitch);
				
				pitch = Math.max(-90, Math.min(pitch, 90));
				yaw = yaw % 360;
				
					
				if (firstTime) {
					zoomWithoutUpdate(zoom - zoomDiff/100);
				}

				Matrix.rotateM(accumulatedRotation, 0, pitch, right_vec[0], right_vec[1], right_vec[2]);
				Matrix.rotateM(accumulatedRotation, 0, yaw, up_vec[0], up_vec[1], up_vec[2]);
				
				counter++;
				
				
				
				
			} else {
				
				if (Math.abs(yaw - newYaw) > 1 || Math.abs(pitch - newPitch) > 1) {
					
					yawDiff = newYaw - yaw;;
					pitchDiff = newPitch - pitch;
					
					findingPlanet = true;
					firstTime = false;
					counter = 0;
				} else {
					
					zoom = newZoom;
					zoom(zoom); 

					Log.e(TAG, "yaw: " + yaw + ", pitch: " + pitch);

					findingPlanet = false;
					counter = 0;
					
				}
				

				
			}
			

			
		}  else if (augmentedReality) {
			
			Matrix.setIdentityM(accumulatedRotation, 0);
			
			//if (deltaYaw < 0) {
			//	deltaYaw = deltaYaw + 360;
				
				//orientation[0] = (360 + orientation[0]);
			//} 
			
			
			//yaw = yaw + deltaYaw;
			//deltaYaw = deltaYaw % 360;
			 
			
			
			Matrix.rotateM(accumulatedRotation, 0, deltaPitch, 1.0f, 0.0f, 0.0f);
			Matrix.rotateM(accumulatedRotation, 0, deltaYaw, 0.0f, 1.0f, 0.0f);
			Matrix.rotateM(currentRotation, 0, deltaRoll, 0.0f, 0.0f, 1.0f);
			
			//deltaYaw = 0;
			//deltaRoll = 0;
		//	deltaPitch = 0;
			
			//Log.e(TAG, "rotating by (" + deltaPitch + ", " + deltaYaw + ", " + deltaPitch + ")");

			
			
		} else {
			
			Matrix.setIdentityM(accumulatedRotation, 0);
			
			yaw += deltaX;
			
			if (yaw < 0) {
				yaw = yaw + 360;
			}
			
			pitch += deltaY;
			
			//Log.e(TAG, "pitch is: " + pitch + ", min is: " + minPitch + ", max is " + maxPitch);
			
			pitch = Math.max(-90, Math.min(pitch, 90));
			yaw = yaw % 360;
			

			Matrix.rotateM(accumulatedRotation, 0, pitch, right_vec[0], right_vec[1], right_vec[2]);
			Matrix.rotateM(accumulatedRotation, 0, yaw, up_vec[0], up_vec[1], up_vec[2]);
			
			deltaX = 0;
			deltaY = 0;
			
		}
		
		
		Matrix.multiplyMM(temporaryMatrix, 0, currentRotation, 0,
					accumulatedRotation, 0);
		System.arraycopy(temporaryMatrix, 0, accumulatedRotation, 0, 16);
		

		// Rotate the cube taking the overall rotation into account.
		Matrix.multiplyMM(temporaryMatrix, 0, modelMatrix, 0,
				accumulatedRotation, 0);
		System.arraycopy(temporaryMatrix, 0, modelMatrix, 0, 16);
		
		//Matrix.setLookAtM(viewMatrix, 0, 0, 0, 0, mvpMatrix[2], mvpMatrix[6], mvpMatrix[10],
			//	upX, upY, upZ)

		// This multiplies the view matrix by the model matrix, and stores
		// the result in the MVP matrix
		// (which currently contains model * view).
		Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		
		Matrix.setIdentityM(modelViewMatrix, 0);
		System.arraycopy(mvpMatrix, 0, modelViewMatrix, 0, 16);

		// Pass in the modelview matrix.
		GLES20.glUniformMatrix4fv(mvMatrixUniform, 1, false, mvpMatrix, 0);

		// This multiplies the modelview matrix by the projection matrix,
		// and stores the result in the MVP matrix
		// (which now contains model * view * projection).
		Matrix.multiplyMM(temporaryMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
		System.arraycopy(temporaryMatrix, 0, mvpMatrix, 0, 16);

		// Pass in the combined matrix.
		GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mvpMatrix, 0);

		
		if (viewMatrix == null){
			Matrix.setLookAtM(viewMatrix, 0, eye_vec[0], eye_vec[1], eye_vec[2],
					look_vec[0], look_vec[1], look_vec[2],
					up_vec[0], up_vec[1], up_vec[2]);
		}
		
		
	//	Log.e(TAG, "-----------");
//		printMatrix(mvpMatrix);
		
		// Pass in the light position in eye space.
		GLES20.glUniform3f(lightPosUniform, lightPosInEyeSpace[0],
				lightPosInEyeSpace[1], lightPosInEyeSpace[2]); 
		
		if (selected) {
			
			findClosest(selectionX, selectionY);
			
			selected = false;
			
		}
		
		
		if (newStars) {
			
			celestialArray.rebuildBuffers();
			newStars = false;
		}

		if (alteredLocationTime) {
			
			buildPlanets();
			buildLines();
			buildTexts();
			buildConstellations();
			rebuildStarGraphics();
			
			look_vec = getLookVector();
			up_vec = getUpVector();
			right_vec = getRightVector();
			setMinMaxPitch();
			yaw = 0;
			Matrix.setLookAtM(viewMatrix, 0, eye_vec[0], eye_vec[1], eye_vec[2],
					look_vec[0], look_vec[1], look_vec[2],
					up_vec[0], up_vec[1], up_vec[2]);
			
			alteredLocationTime = false;
			
		    final Runnable updateRunnable = new Runnable() {
		        public void run() {
		            //call the activity method that updates the UI
		    		OpenGLES20Activity a = (OpenGLES20Activity) context;
					a.setLocationBox(currentLocation);
					a.setDateTimeBox(UTCTime);

		        }
		        
		    };
		    
		    
			 Activity activity=(Activity) this.context; 
			    activity.runOnUiThread(updateRunnable); 
			    
			    

			
		}
		

		
		
		if (alteredZoom) {
			
			rebuildStarGraphics();
			buildTexts();
			//buildConstellations();


			alteredZoom = false;
			
		}

		renderUniverse();
		
		if (modelViewMatrix[0] != modelViewMatrix[0]){
			
			Log.e(TAG, "Error in view");
			Log.e(TAG, "Error in view");
			Log.e(TAG, "Error in view");
			
			Matrix.setIdentityM(viewMatrix, 0);
			
			look_vec = getLookVector();
			up_vec = Vector.crossProduct(look_vec, right_vec);
			right_vec = getRightVector();
			
			Matrix.setLookAtM(viewMatrix, 0, eye_vec[0], eye_vec[1], eye_vec[2],
					look_vec[0], look_vec[1], look_vec[2],
					up_vec[0], up_vec[1], up_vec[2]);
			
			Log.e(TAG, "eye is: (" + eye_vec[0] + ", " + eye_vec[1] + ", " + eye_vec[2] +")");
			Log.e(TAG, "look is: (" + look_vec[0] + ", " + look_vec[1] + ", " + look_vec[2] +")");
			Log.e(TAG, "up is: (" + up_vec[0] + ", " + up_vec[1] + ", " + up_vec[2] +")");
			
			//buildMainProgram();
			
			//setAlteredLocationTime(true);
			
		}
		
		Calendar c = Calendar.getInstance(); 
		int seconds = c.get(Calendar.SECOND);
		
		//Log.e("TAG", seconds + " seconds");
		if (seconds == 0) {
			buildConstellations();
			rebuildStarGraphics();
		}
		
		
		
		
		

		
	}
	
	public float[] getUpVector() {
		
		float ra = 0;
		float dec = (float) 89.9999999999;
		
		OpenGLES20Activity a = (OpenGLES20Activity) context;
		Location l = this.currentLocation;
		
		Tuple azAlt = computeHorizonCoordinates(ra, dec, l.getLatitude(), l.getLongitude());
		
		float azimuth = azAlt.x;
		float altitude = azAlt.y;
		
		double sin_az = Math.sin(Math.toRadians(azimuth));
		double cos_az = Math.cos(Math.toRadians(azimuth));
		double sin_alt = Math.sin(Math.toRadians(altitude));
		double cos_alt = Math.cos(Math.toRadians(altitude));
		
		float distance = 400;
		
		float x = (float) (sin_az * cos_alt);
		float y = (float) (sin_alt);
		float z = (float) (cos_az * cos_alt);
		
		float sum_squared = (float) Math.sqrt(x*x + y*y + z*z);
		x = x/sum_squared;
		y = y/sum_squared;
		z = z/sum_squared;
		
		return new float[] {x, y, z};
	}
	
	public void setMinMaxPitch() {
		
		OpenGLES20Activity a = (OpenGLES20Activity) context;
		Location l = this.currentLocation;
		
		Tuple azAlt = computeHorizonCoordinates(0, -90, l.getLatitude(), l.getLongitude());
		
		minPitch = azAlt.y;
		
		azAlt = computeHorizonCoordinates(180, 90, l.getLatitude(), l.getLongitude());
		
		maxPitch = azAlt.y;
		
	}
	
	public float[] getLookVector() {
		
		float ra = 0;
		float dec = 0;
		
		OpenGLES20Activity a = (OpenGLES20Activity) context;
		Location l = this.currentLocation;
			
		Tuple azAlt = computeHorizonCoordinates(ra, dec, l.getLatitude(), l.getLongitude());
		
		float azimuth = azAlt.x;
		float altitude = azAlt.y;
		
		double sin_az = Math.sin(Math.toRadians(azimuth));
		double cos_az = Math.cos(Math.toRadians(azimuth));
		double sin_alt = Math.sin(Math.toRadians(altitude));
		double cos_alt = Math.cos(Math.toRadians(altitude));
		
		float x = (float) (sin_az * cos_alt);
		float y = (float) (sin_alt);
		float z = (float) (cos_az * cos_alt);
		
		float sum_squared = (float) Math.sqrt(x*x + y*y + z*z);
		x = x/sum_squared;
		y = y/sum_squared;
		z = z/sum_squared;
		
		
		//Log.e(TAG, "look vector (" + azimuth + ", " + altitude + ")");
		
		return new float[] {-x, y, z};
		
	}

	public float[] getRightVector() {
		
		float ra = 90;
		float dec = 0;
		
		OpenGLES20Activity a = (OpenGLES20Activity) context;
		Location l = this.currentLocation;
		
		Tuple azAlt = computeHorizonCoordinates(ra, dec, l.getLatitude(), l.getLongitude());
		
		float azimuth = azAlt.x;
		float altitude = azAlt.y;
		
		double sin_az = Math.sin(Math.toRadians(azimuth));
		double cos_az = Math.cos(Math.toRadians(azimuth));
		double sin_alt = Math.sin(Math.toRadians(altitude));
		double cos_alt = Math.cos(Math.toRadians(altitude));
		
		float x = (float) (sin_az * cos_alt);
		float y = (float) (sin_alt);
		float z = (float) (cos_az * cos_alt);
		
		float sum_squared = (float) Math.sqrt(x*x + y*y + z*z);
		x = x/sum_squared;
		y = y/sum_squared;
		z = z/sum_squared;
		
	//	Log.e(TAG, "look vector (" + azimuth + ", " + altitude + ")");
		
		return new float[] {-x, y, z};
		
	}
	
	private void buildMainProgram() {
		
		GLES20.glUseProgram(program);

		mvpMatrixUniform = GLES20.glGetUniformLocation(program,
				MVP_MATRIX_UNIFORM);
		mvMatrixUniform = GLES20.glGetUniformLocation(program,
				MV_MATRIX_UNIFORM);
		lightPosUniform = GLES20.glGetUniformLocation(program,
				LIGHT_POSITION_UNIFORM);
		positionAttribute = GLES20.glGetAttribLocation(program,
				POSITION_ATTRIBUTE);
		normalAttribute = GLES20.glGetAttribLocation(program, NORMAL_ATTRIBUTE);
		colorAttribute = GLES20.glGetAttribLocation(program, COLOR_ATTRIBUTE);
		textureUniform = GLES20.glGetUniformLocation(program, TEXTURE_UNIFORM);
		textureCoordinateAttribute = GLES20.glGetAttribLocation(program,
				TEXTURE_COORDINATE_ATTRIBUTE);
		

		// Calculate position of the light. Push into the distance.
		Matrix.setIdentityM(lightModelMatrix, 0);
		Matrix.translateM(lightModelMatrix, 0, 0.0f, 7.5f, -8.0f);

		Matrix.multiplyMV(lightPosInWorldSpace, 0, lightModelMatrix, 0,
				lightPosInModelSpace, 0);
		Matrix.multiplyMV(lightPosInEyeSpace, 0, viewMatrix, 0,
				lightPosInWorldSpace, 0);
		
	}
	
	private void buildPlanets() {

		calcPlanetCoords();
		
		planets = new Planet[]{mercury, venus, mars, jupiter, saturn, uranus,
								neptune, pluto, sun};
		
		
		moonGraphic = new Circle(moon.getRadius()*10, (float) (moon.getAZ()),
				(float) (moon.getALT()), (float) (moon.getRA()), (float) (moon.getDEC()),
				 (float) (moon.getRVEC()*10), 2, "Moon", (float) moon.getMagnitude(), moon.getElongation());  
		
		//Log.e(TAG, "sun radius: " + sun.getRadius() + ", distance: " + sun.getRVEC());

		//Log.e(TAG, "moon radius: " + moon.getRadius() + ", distance: " + moon.getRVEC());
		//Log.e(TAG, "moon az: " + moonGraphic.getAZ() + ", alt:" + moonGraphic.getAlt());
		
		sunGraphic = new Circle(sun.getRadius(), (float) (sun.getAZ()),
			   	(float) (sun.getALT()), (float) (sun.getRA()),
			   	(float) (sun.getDEC()),	(float) sun.getRVEC(), 2, "Sun", sun.getMagnitude(), 0);
		
		//Log.e(TAG, "sun az: " + sun.getAZ() + ", sun: " + sun.getALT() +
		//		", distance: " + sun.getRVEC() + ", radius: " + sun.getRadius());
		
		mercuryGraphic = new Circle(mercury.getRadius(), (float) (mercury.getAZ()),
				(float) (mercury.getALT()), (float) (mercury.getRA()),
				(float) (mercury.getDEC()), (float) mercury.getRVEC(), 2, "Mercury", mercury.getMagnitude(),
				mercury.getPhase());
	
		venusGraphic = new Circle(venus.getRadius(), (float) (venus.getAZ()),
				(float) (venus.getALT()), (float) (venus.getRA()),
				(float) (venus.getDEC()), (float) venus.getRVEC(), 2, "Venus", venus.getMagnitude(),
				venus.getPhase());
		
		marsGraphic = new Circle(mars.getRadius(), (float) (mars.getAZ()),
				(float) (mars.getALT()), (float) (mars.getRA()),
				(float) (mars.getDEC()), (float) mars.getRVEC(), 2, "Mars", mars.getMagnitude(),0);
		
		jupiterGraphic = new Circle(jupiter.getRadius(), (float) (jupiter.getAZ()),
				(float) (jupiter.getALT()), (float) (jupiter.getRA()),
				(float) (jupiter.getDEC()), (float) jupiter.getRVEC(), 1, "Jupiter", jupiter.getMagnitude(),
				0);
		
		saturnGraphic = new Circle(saturn.getRadius(), (float) (saturn.getAZ()),
				(float) (saturn.getALT()), (float) (saturn.getRA()),
				(float) (saturn.getDEC()), (float) saturn.getRVEC(), 2, "Saturn", saturn.getMagnitude(),0);
		
		uranusGraphic = new Circle(uranus.getRadius(), (float) (uranus.getAZ()),
				(float) (uranus.getALT()), (float) (uranus.getRA()),
				(float) (uranus.getDEC()), (float) uranus.getRVEC(), 2, "Uranus", uranus.getMagnitude(),0);
		
		neptuneGraphic = new Circle(neptune.getRadius(), (float) (neptune.getAZ()),
				(float) (neptune.getALT()), (float) (neptune.getRA()),
				(float) (neptune.getDEC()), (float) neptune.getRVEC(), 2, "Neptune", neptune.getMagnitude(),0);
		
		plutoGraphic = new Circle(pluto.getRadius(), (float) (pluto.getAZ()),
				(float) (pluto.getALT()), (float) (pluto.getRA()),
				(float) (pluto.getDEC()), (float) pluto.getRVEC(), 2, "Pluto", pluto.getMagnitude(),0);
		
		ioGraphic = new Circle((float) 1821*100/scaleFactor, jovianMoons.getMoonAZ("IO"), 
					jovianMoons.getMoonALT("IO"), (float) jovianMoons.getMoonRA("IO"),
					(float) jovianMoons.getMoonDEC("IO"), (float) jupiter.getRVEC(), 2, "IO", 0,0);
		
		europaGraphic = new Circle((float) 1550*100/scaleFactor, jovianMoons.getMoonAZ("EUROPA"), 
				jovianMoons.getMoonALT("EUROPA"), jovianMoons.getMoonRA("EUROPA"),
				jovianMoons.getMoonDEC("EUROPA"), (float) jupiter.getRVEC(), 2, "Europa", 0,0);
		
		ganymedeGraphic = new Circle((float) 2634*100/scaleFactor, jovianMoons.getMoonAZ("GANYMEDE"), 
				jovianMoons.getMoonALT("GANYMEDE"), jovianMoons.getMoonRA("GANYMEDE"), jovianMoons.getMoonDEC("GANYMEDE"),
				(float) jupiter.getRVEC(), 2, "Ganymede", 0,0);
		
		callistoGraphic = new Circle((float) 2410*100/scaleFactor, jovianMoons.getMoonAZ("CALLISTO"), 
				jovianMoons.getMoonALT("CALLISTO"), jovianMoons.getMoonRA("CALLISTO"), jovianMoons.getMoonDEC("CALLISTO"),
				(float) jupiter.getRVEC(), 2, "Callisto", 0,0);
		
		circles = new Circle[]{moonGraphic, mercuryGraphic,
				venusGraphic, marsGraphic,
				jupiterGraphic, saturnGraphic,
				uranusGraphic, neptuneGraphic,
				plutoGraphic, sunGraphic,
				ioGraphic, europaGraphic,
				ganymedeGraphic, callistoGraphic,
				ioGraphic
		};

		
		//Log.e(TAG, "built planets ok");
		printPlanetPositions();
	

	}
	
	private void printPlanetPositions() {
		
		Log.e(TAG, "mercury AZ = " + mercury.getAZ() + ", ALT = " + mercury.getALT());
		Log.e(TAG, "venus AZ = " + venus.getAZ() + ", ALT = " + venus.getALT());
		Log.e(TAG, "mars AZ = " + mars.getAZ() + ", ALT = " + mars.getALT());
		Log.e(TAG, "jupiter AZ = " + jupiter.getAZ() + ", ALT = " + jupiter.getALT());
		Log.e(TAG, "saturn AZ = " + saturn.getAZ() + ", ALT = " + saturn.getALT());
		Log.e(TAG, "uranus AZ = " + uranus.getAZ() + ", ALT = " + uranus.getALT());
		Log.e(TAG, "neptune AZ = " + neptune.getAZ() + ", ALT = " + neptune.getALT());
		Log.e(TAG, "pluto AZ = " + pluto.getAZ() + ", ALT = " + pluto.getALT()); 
		Log.e(TAG, "sun AZ = " + sun.getAZ() + ", ALT = " + sun.getALT()); 
	}
	
	private void calcPlanetCoords() {
		
		OpenGLES20Activity a = (OpenGLES20Activity) context;
		
		float moonRadius = (float) (1737.4/scaleFactor);
		float mercuryRadius = 2440 / scaleFactor;
		float venusRadius = 6052 / scaleFactor;
		float marsRadius = 3390 / scaleFactor;
		float jupiterRadius = 69911 / scaleFactor;
		float saturnRadius = 58232 / scaleFactor;
		float uranusRadius = 25362 / scaleFactor;
		float neptuneRadius = 24622 / scaleFactor;
		float plutoRadius = 1180 / scaleFactor;
		float sunRadius = (float) (695500*1.7684 / scaleFactor);
		
	
		Location l = this.currentLocation;
		
		float lat = (float) l.getLatitude();
		float lon = (float) l.getLongitude();
		
		int year = UTCTime.get(Calendar.YEAR);
		int month = UTCTime.get(Calendar.MONTH);
		int day = UTCTime.get(Calendar.DAY_OF_MONTH);
		int hour = UTCTime.get(Calendar.HOUR_OF_DAY);
		int min = UTCTime.get(Calendar.MINUTE);
		int sec = UTCTime.get(Calendar.SECOND);
		
		sun = new Planet(SolarSystemObject.Sun, lat, lon, sunRadius, year, month, day,
				hour, min, sec, "Sun", 0);
		
		Tuple az_alt = computeHorizonCoordinates(sun.getRA(), sun.getALT(), lat, lon);
		//Log.e(TAG, "SUN AZ: " + az_alt.x + ", ALT: " + az_alt.y);
				
		float distanceToSun = (float) (sun.getRVEC()/100);
		
		moon = new Moon(lat, lon, moonRadius, year, month, day, hour, min, sec, distanceToSun,
				sun.getEclipticLongitude(), sun.getEclipticLatitude());
		
		buildMoonTexture(moon.getElongation());

		mercury = new Planet(SolarSystemObject.Mercury, lat, lon, mercuryRadius, year, month,
				day, hour, min, sec, "Mercury", distanceToSun);
		
		buildMercuryTexture(mercury.getPhase());

		venus = new Planet(SolarSystemObject.Venus, lat, lon, venusRadius, year, month, day,
				hour, min, sec, "Venus", distanceToSun);

		buildVenusTexture(venus.getPhase());
		
		mars = new Planet(SolarSystemObject.Mars, lat, lon, marsRadius, year, month, day,
				hour, min, sec, "Mars", distanceToSun);

		jupiter = new Planet(SolarSystemObject.Jupiter, lat, lon, jupiterRadius, year, month,
				day, hour, min, sec, "Jupiter", distanceToSun);
		
		saturn = new Planet(SolarSystemObject.Saturn, lat, lon, saturnRadius, year, month,
				day, hour, min, sec, "Saturn", distanceToSun);

		uranus = new Planet(SolarSystemObject.Uranus, lat, lon, uranusRadius, year, month,
				day, hour, min, sec, "Uranus", distanceToSun);

		neptune = new Planet(SolarSystemObject.Neptune, lat, lon, neptuneRadius, year, month,
				day, hour, min, sec, "Neptune", distanceToSun);

		pluto = new Planet(SolarSystemObject.Pluto, lat, lon, plutoRadius, year, month, day,
				hour, min, sec, "Pluto", distanceToSun);

		jovianMoons = new JovianMoon((float) jupiter.getRA(), (float) jupiter.getDEC(), lat, lon, moonRadius, year, month, day, hour, min, sec);


	}
	
	private void buildStars() {

		stars = new Star[NUM_STARS];
		Log.e(TAG, "about to read datbabase");
		starCursor = starCollector.read();
		Log.e(TAG, "done reading datbabase");
		starCursor.moveToFirst();
		starCursor.moveToNext();
		

		int counter = 0;
		while (counter < NUM_STARS) {
			
			int star_id = starCursor.getInt(0);
			
			float x = starCursor.getFloat(1);
			float y = starCursor.getFloat(2);
			float z = starCursor.getFloat(3);
			float rightAscension = starCursor.getFloat(4);
			float declination = starCursor.getFloat(5);
			CelestialColor starColor;
			
			if (!starCursor.isNull(4)) {
				starColor = indexToColor(starCursor.getFloat(6));
			} else {
				starColor = CelestialColor.White;
			}

			float mag = starCursor.getFloat(7);
			float distance = starCursor.getFloat(8);

			
		
			rightAscension = rightAscension*15;
			
			String bayerName = starCursor.getString(9);
			String properName = starCursor.getString(10);
			
			if (properName == null ){
				properName = "null";
			}
			
			float lat = 0;
			float lon = 0;

			Location currentLocation = this.currentLocation;
			
			if (currentLocation != null) {
				
				lat = (float) currentLocation.getLatitude();
				lon = (float) currentLocation.getLongitude();
				
			}
			
//			Tuple az_alt = computeHorizonCoordinates(rightAscension, declination, lat, lon);
			Star s = new Star(star_id, rightAscension, declination, 0, 0, starColor, mag, bayerName, properName, distance);
			s.computeHorizonCoordinates(lat, lon);
			
		
			
			stars[counter] = s;

			starCursor.moveToNext();

			counter++;
			


		}
		
	/*	Log.e(TAG, "done looping through stars");
		Log.e("TAG", "counter is " + counter);
		Log.e("TAG", "stars length is " + stars.length);
		Log.e("TAG", "example star: " + stars[600].altitude); */
		//starCursor.close();
		
		starCursor.moveToFirst();
		buildConstellations();
		
		celestialArray = new CelestialArray(stars);
		//celestialArray.build(stars);

	}

	private void buildConstellations() {
		
		constellations.clear();
	
		OpenGLES20Activity a = (OpenGLES20Activity) context;		
		//StringBuilder buf = new StringBuilder();
		try {
			InputStream json = a.getAssets().open("constellations.js");
			BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
			String str;
			in.readLine();
			in.readLine();
			
			boolean newConstellation = true;
			Constellation c;
			int constellationCount = 0;
			LinkedList<Tuple> l = new LinkedList<Tuple>();
			int counter = 0;
			String previousName = "Andromeda";
			
			Location location = currentLocation;
			
			while(((str=in.readLine()) != null)) {
			
				if (str.length() > 2) {
				
				String[] elements = str.split("Constellation\\(");
				String[] details = elements[1].split("\"");
				String[] coordinates = details[2].split(",");
				
				
				
				String name = details[1];
				//Log.e(TAG, "name is " + name);
				
				if (!previousName.equals(name)) {
					Tuple[] horizontal_coords = new Tuple[l.size()];
					
					counter = 0;
					for (Tuple t : l) {
						horizontal_coords[counter] = computeHorizonCoordinates(t.x, t.y, location.getLatitude(), location.getLongitude());
						counter++;
					}
					
					c = new Constellation(horizontal_coords);
					constellations.add(c);
					l.clear();

					//Log.e(TAG, "creating constellation");
				}
				
				
				
				float RA = Float.parseFloat(coordinates[2]);
				float DEC = Float.parseFloat(coordinates[1]);
				float RA_next = Float.parseFloat(coordinates[4].substring(0, coordinates[4].length()-2));
				float DEC_next = Float.parseFloat(coordinates[3]);
				
				//Log.e(TAG, "Coordinates: (" + RA*15 + ", " + DEC + ")");
				
				l.add(new Tuple(RA*15, DEC));
				l.add(new Tuple(RA_next*15, DEC_next));
				
				//l.add(new Tuple())
				previousName = name;
				
			}
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		//int[] orion_index = new int[]{9, 33, 56, 7, 71059, 28, 9};
		Tuple[] orion_equatorial = new Tuple[]{new Tuple((float) 88.79287155, (float) 7.40703634),
												new Tuple((float) 85.1896866, (float) -1.9425784),
												new Tuple((float) 86.93911536, (float) -9.6696019),
												new Tuple((float) 78.6344634, (float) -8.2016392),
												new Tuple((float) 83.0016, (float) - 0.29279), 
												new Tuple((float) 81.47452695, (float) 6.34973451), 
												new Tuple((float) 72.4583, (float) 6.9614), // pi 3
												new Tuple((float) 72.8000, (float) 5.6050), // pi 4
												new Tuple((float) 73.5625, (float) 2.4406), // pi 5
												new Tuple((float) 74.6375, (float) 1.7142), // pi 6
												new Tuple((float) 73.5625, (float) 2.4406), // pi 5
												new Tuple((float) 72.8000, (float) 5.6050), // pi 4
												new Tuple((float) 72.4583, (float) 6.9614), // pi 3
												new Tuple((float) 72.6542, (float) 8.9003), // pi 2
												new Tuple((float) 73.7250, (float) 10.1508), // pi 1
												new Tuple((float) 72.6542, (float) 8.9003), // pi 2
												new Tuple((float) 72.4583, (float) 6.9614), // pi 3
												new Tuple((float) 81.47452695, (float) 6.34973451), //bellatrix
												new Tuple((float) 83.75, (float) 9.9167),
												new Tuple((float) 88.79287155, (float) 7.40703634),
												new Tuple((float) 90.5958, (float) 9.6475),
												new Tuple((float) 91.8917, (float) 14.7683)};
		

		Tuple[] orion_az_alt = new Tuple[22];
		Location l = currentLocation;

		Tuple az_alt;
		int counter = 0;
		
		while (counter < 22) {
		
			az_alt = computeHorizonCoordinates(orion_equatorial[counter].x, orion_equatorial[counter].y, l.getLatitude(), l.getLongitude());
			orion_az_alt[counter] = az_alt;
			//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
		
			counter++;
		}
		
		 orion = new Constellation(orion_az_alt);
		 
			Tuple[] crux_equatorial = new Tuple[]{new Tuple((float) 191.930286, (float) -59.68659653),
												new Tuple((float) 188.022643, (float) -59.21459827),
												new Tuple((float) 186.64975, (float) -63.09276667),
												new Tuple((float) 188.022643, (float) -59.21459827),
												new Tuple((float) 187.791375, (float) -57.10753333),
												new Tuple((float) 188.022643, (float) -59.21459827),
												new Tuple((float) 184.115, (float) -58.7426),
												};


			Tuple[] crux_az_alt = new Tuple[7];

			counter = 0;
			
			while (counter < 7) {
			
				az_alt = computeHorizonCoordinates(crux_equatorial[counter].x, crux_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				crux_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
			}
			
			crux = new Constellation(crux_az_alt);
			
			Tuple[] dipper_equatorial = new Tuple[]{new Tuple((float) 206.8856085, (float) 49.3133029),
													new Tuple((float) 200.9809155, (float) 54.9254153),
													new Tuple((float) 193.506804, (float) 55.959843),
													new Tuple((float) 183.8565026, (float) 57.02623593),
													new Tuple((float) 178.4576972, (float) 53.69018917),
													new Tuple((float) 165.4599615, (float) 56.3823448),
													new Tuple((float) 165.9326535, (float) 61.7511189),
													new Tuple((float) 183.8565026, (float) 57.02623593)};
													
	


			Tuple[] dipper_az_alt = new Tuple[8];
			
			counter = 0;
			
			while (counter < 8) {
			
				az_alt = computeHorizonCoordinates(dipper_equatorial[counter].x, dipper_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				dipper_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
			}
			
			dipper = new Constellation(dipper_az_alt);
		 
			Tuple[] little_bear_equatorial = new Tuple[]{new Tuple((float) 37.95, (float) 89.26),
														 new Tuple((float) 263.0542, (float) 86.5864),
														 new Tuple((float) 251.4917, (float) 82.0372),
														 new Tuple((float) 236.0167, (float) 77.7944),
														 new Tuple((float) 222.6750, (float) 74.1556),
														 new Tuple((float) 230.1833, (float) 71.8339),
														 new Tuple((float) 244.3750, (float) 75.7553),
														 new Tuple((float) 236.0167, (float) 77.7944)
														 
			};
			
			Tuple[] little_bear_az_alt = new Tuple[8];
			
			counter = 0;
			
			while (counter < 8) {
				
				az_alt = computeHorizonCoordinates(little_bear_equatorial[counter].x, little_bear_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				little_bear_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
			}
			
			littleBear = new Constellation(little_bear_az_alt);
		
			
			Tuple[] capricornus_equatorial = new Tuple[]{new Tuple((float) 326.7583, (float) -16.1272),
														 new Tuple((float) 321.6667, (float) -22.4114),
														 new Tuple((float) 312.9542, (float) -26.9192),
														 new Tuple((float) 305.2542, (float) -14.7814),
														 new Tuple((float) 304.5125, (float) -12.5447),
														 new Tuple((float) 316.4875, (float) -17.2328),
														 new Tuple((float) 326.7583, (float) -16.1272)
			};
			
			Tuple[] capricornus_az_alt = new Tuple[7];
			
			counter = 0;
			
			while (counter < 7) {
				
				az_alt = computeHorizonCoordinates(capricornus_equatorial[counter].x, capricornus_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				capricornus_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
			}
			
			capricornus = new Constellation(capricornus_az_alt);
			
			Tuple[] bootes_equatorial = new Tuple[]{new Tuple((float) 220.2875,(float) 13.7283),
													new Tuple((float) 213.9167, (float) 19.1922),
													new Tuple((float) 221.2458, (float) 27.0742),
													new Tuple((float) 228.8750, (float) 33.3147),
													new Tuple((float) 225.4875, (float) 40.3906),
													new Tuple((float) 218.0208, (float) 38.3083),
													new Tuple((float) 217.9583, (float) 30.3714),
													new Tuple((float) 213.9167, (float) 19.1922),
													new Tuple((float) 208.6707, (float) 18.3978)
			
			};
			
			
			Tuple[] bootes_az_alt = new Tuple[9];
			
			
			counter = 0;
			
			while (counter < 9) {
				
				az_alt = computeHorizonCoordinates(bootes_equatorial[counter].x, bootes_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				bootes_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
			}
			
			bootes = new Constellation(bootes_az_alt);
			
			Tuple[] lyra_equatorial = new Tuple[]{new Tuple((float) 279.2333, (float) 38.7831),
												  new Tuple((float) 281.0833, (float) 39.6700),
												  new Tuple((float) 281.1917, (float) 37.6060),
												  new Tuple((float) 283.6250, (float) 36.8986),
												  new Tuple((float) 284.7375, (float) 32.6894),
												  new Tuple((float) 282.5208, (float) 33.3628),
												  new Tuple((float) 281.1917, (float) 37.6060),
												  new Tuple((float) 279.2333, (float) 38.7831),
												  new Tuple((float) 281.0833, (float) 39.6700)		
			};
			
			Tuple[] lyra_az_alt = new Tuple[9];
			
			counter = 0;
			
			while (counter < 9) {
				
				az_alt = computeHorizonCoordinates(lyra_equatorial[counter].x, lyra_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				lyra_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
			}
			
			lyra = new Constellation(lyra_az_alt);
			
			Tuple[] eridanus_equatorial = new Tuple[]{new Tuple((float) 24.4292, (float) -57.2367),
													  new Tuple((float) 28.9875, (float) -51.6089),
													  new Tuple((float) 36.7458, (float) -47.7039),
													  new Tuple((float) 40.1668, (float) -39.8553),
													  new Tuple((float) 44.5667, (float) -40.3047),
													  new Tuple((float) 49.9708, (float) -43.0714),
													  new Tuple((float) 54.2708, (float) -40.2744),
													  new Tuple((float) 57.3625, (float) -36.2000),
													  new Tuple((float) 64.4708, (float) -33.7981),
													  new Tuple((float) 66.0083, (float) -34.0169),
													  new Tuple((float) 68.8875, (float) -30.5622),
													  new Tuple((float) 68.3750, (float) -29.7656),
													  new Tuple((float) 59.9792, (float) -24.0161),
													  new Tuple((float) 58.4250, (float) -24.6119),
													  new Tuple((float) 56.7083, (float) -23.2483),
													  new Tuple((float) 53.4458, (float) -21.6328),
													  new Tuple((float) 49.8750, (float) -21.7578),
													  new Tuple((float) 45.5958, (float) -23.6242),
													  new Tuple((float) 42.7583, (float) -21.0039),
													  new Tuple((float) 41.2708, (float) -18.5725),
													  new Tuple((float) 41.0292, (float) -13.8586),
													  new Tuple((float) 44.1042, (float) -8.8975),
													  new Tuple((float) 55.8083, (float) -9.7650),
													  new Tuple((float) 56.5333, (float) -12.1017),
													  new Tuple((float) 59.5042, (float) -13.5081),
													  new Tuple((float) 62.9625, (float) -6.8375),
													  new Tuple((float) 69.0792, (float) -3.3522),
													  new Tuple((float) 71.3750, (float) -3.2544),
													  new Tuple((float) 76.9625, (float) -5.0861)

			};
			
			Tuple[] eridanus_az_alt = new Tuple[29];
			
			counter = 0;
			
			while (counter < 29) {
				
				az_alt = computeHorizonCoordinates(eridanus_equatorial[counter].x, eridanus_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				eridanus_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
			}
			
			eridanus = new Constellation(eridanus_az_alt);
			
			Tuple[] scorpius_equatorial = new Tuple[]{new Tuple((float) 267.4625, (float) -37.0433),
										   new Tuple((float) 263.4000, (float) -37.1036),
										   new Tuple((float) 262.6875, (float) -37.2956),
										   new Tuple((float) 265.6208, (float) -39.0297),
										   new Tuple((float) 266.8958, (float) -40.1269),
										   new Tuple((float) 264.3292, (float) -42.9978),
										   new Tuple((float) 258.0375, (float) -43.2383),
										   new Tuple((float) 253.6458, (float) -42.3606),
										   new Tuple((float) 252.5417, (float) -34.2925),
										   new Tuple((float) 248.9667, (float) -28.2158),
										   new Tuple((float) 247.3500, (float) -26.4317),
										   new Tuple((float) 245.2958, (float) -25.5925),
										   new Tuple((float) 240.0833, (float) -22.6214),
										   new Tuple((float) 241.3583, (float) -19.8053),
										   new Tuple((float) 240.0833, (float) -22.6214),
										   new Tuple((float) 239.7125, (float) -26.1139),
										   new Tuple((float) 239.2208, (float) -29.2139)

			};
			
			Tuple[] scorpius_az_alt = new Tuple[17];
			
			counter = 0;
			
			while (counter < 17) {
				
				az_alt = computeHorizonCoordinates(scorpius_equatorial[counter].x, scorpius_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				scorpius_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
				
			}
			
			scorpius = new Constellation(scorpius_az_alt);
			
			Tuple[] andromeda_equatorial = new Tuple[]{new Tuple((float) 345.4792, (float) 42.3258),
													   new Tuple((float) 354.5333, (float) 43.2681),
													   new Tuple((float) 355.1000, (float) 44.3339),
													   new Tuple((float) 354.3875, (float) 46.4592),
													   new Tuple((float) 355.1000, (float) 44.3339),
													   new Tuple((float) 354.5333, (float) 43.2681),
													   new Tuple((float) 9.2167, (float) 33.7192), // hip 2912
													   new Tuple((float) 9.8292, (float) 30.8611), //hip 3092
													   new Tuple((float) 2.0958, (float) 29.0906),
													   new Tuple((float) 9.8292, (float) 30.8611), //hip 3092
													   new Tuple((float) 9.6375, (float) 29.3122),
													   new Tuple((float) 11.8333, (float) 24.2672),
													   new Tuple((float) 16.4167, (float) 21.4731),
													   new Tuple((float) 11.8333, (float) 24.2672),
													   new Tuple((float) 9.6375, (float) 29.3122),
													   new Tuple((float) 9.8292, (float) 30.8611), //hip 3092
													   new Tuple((float) 17.4292, (float) 35.6206),
													   new Tuple((float) 9.2167, (float) 33.7192), // hip 2912
													   new Tuple((float) 17.4292, (float) 35.6206), //mirach
													   new Tuple((float) 30.9708, (float) 42.3297),
													   new Tuple((float) 17.4292, (float) 35.6206), //mirach
													   new Tuple((float) 14.1875, (float) 38.4992),
													   new Tuple((float) 12.4500, (float) 41.0789),
													   new Tuple((float) 17.3750, (float) 47.2417),
													   new Tuple((float) 24.4958, (float) 48.6283)

			};
			
			Tuple[] andromeda_az_alt = new Tuple[25];
			
			counter = 0;
			
			while (counter < 25) {
				
				az_alt = computeHorizonCoordinates(andromeda_equatorial[counter].x, andromeda_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				andromeda_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
				
				
			}
			
			andromeda = new Constellation(andromeda_az_alt);
			
			Tuple[] auriga_equatorial = new Tuple[]{new Tuple((float) 89.8792, (float) 54.2847),
													new Tuple((float) 79.1708, (float) 45.9989),
													new Tuple((float) 89.8792, (float) 44.9472),
													new Tuple((float) 89.8792, (float) 54.2847),
													new Tuple((float) 89.8792, (float) 44.9472),
													new Tuple((float) 89.9292, (float) 37.2125),
													new Tuple((float) 81.5708, (float) 28.6078),
													new Tuple((float) 74.2458, (float) 33.1661), 
													new Tuple((float) 76.6250, (float) 41.2344),
													new Tuple((float) 79.1708, (float) 45.9989)
					
			};
			
			Tuple[] auriga_az_alt = new Tuple[10];
			
			counter = 0;
			
			while (counter < 10) {
				
				az_alt = computeHorizonCoordinates(auriga_equatorial[counter].x, auriga_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				auriga_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
				
				
			}
			
			auriga = new Constellation(auriga_az_alt);
			
			Tuple[] leo_equatorial = new Tuple[]{new Tuple((float) 177.2625, (float) 14.5722),
												 new Tuple((float) 168.5583, (float) 15.4297),
												 new Tuple((float) 170.9792, (float) 10.5294),
												 new Tuple((float) 170.2833, (float) 6.0292),
												 new Tuple((float) 170.9792, (float) 10.5294),
												 new Tuple((float) 168.5583, (float) 15.4297), //chertan
												 new Tuple((float) 151.8292, (float) 16.7625), //hip 49583
												 new Tuple((float) 152.0917, (float) 11.9669),
												 new Tuple((float) 151.8292, (float) 16.7625), //hip 49583
												 new Tuple((float) 146.4625, (float) 23.7742), 
												 new Tuple((float) 142.9292, (float) 22.9681), 
												 new Tuple((float) 141.1625, (float) 26.1822), 
												 new Tuple((float) 148.1875, (float) 26.0069), 
												 new Tuple((float) 146.4625, (float) 23.7742),
												 new Tuple((float) 148.1875, (float) 26.0069),
												 new Tuple((float) 154.1708, (float) 23.4172),
												 new Tuple((float) 154.9917, (float) 19.8417),
												 new Tuple((float) 151.8292, (float) 16.7625), //hip 49583
												 new Tuple((float) 154.9917, (float) 19.8417),
												 new Tuple((float) 168.5250, (float) 20.5239), 
												 new Tuple((float) 168.5583, (float) 15.4297), //chertan
												 new Tuple((float) 168.5250, (float) 20.5239),
												 new Tuple((float) 177.2625, (float) 14.5722)
			};
			
			Tuple[] leo_az_alt = new Tuple[23];
			
			counter = 0;
			
			while (counter < 23) {
				
				az_alt = computeHorizonCoordinates(leo_equatorial[counter].x, leo_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				leo_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
				
				
			}
			
			leo = new Constellation(leo_az_alt);
			
			Tuple[] carina_equatorial = new Tuple[]{new Tuple((float) 95.9875, (float) -52.6956), 
													new Tuple((float) 138.3000, (float) -69.7172),
													new Tuple((float) 153.4333, (float) -70.0378),
													new Tuple((float) 160.5583, (float) -64.4664), //a
													new Tuple((float) 166.6333, (float) -62.4239),
													new Tuple((float) 167.9000, (float) -60.3175),
													new Tuple((float) 167.1458, (float) -58.9750), 
													new Tuple((float) 163.3708, (float) -58.8531),
													new Tuple((float) 158.0042, (float) -61.6853),
													new Tuple((float) 160.5583, (float) -64.4664), //a
													new Tuple((float) 158.0042, (float) -61.6853),
													new Tuple((float) 154.2708, (float) -61.3322),
													new Tuple((float) 139.2708, (float) -59.2750),
													new Tuple((float) 125.6250, (float) -59.5094),
													new Tuple((float) 119.1917, (float) -52.9822)
			};
			
			Tuple[] carina_az_alt = new Tuple[15];
			
			counter = 0;
			
			while (counter < 15) {
				
				az_alt = computeHorizonCoordinates(carina_equatorial[counter].x, carina_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				carina_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
				
				
			}
			
			carina = new Constellation(carina_az_alt);
			
			Tuple[] cygnus_equatorial = new Tuple[]{new Tuple((float) 292.6792, (float) 27.9594),
													new Tuple((float) 305.5542, (float) 40.2567),
													new Tuple((float) 311.5500, (float) 33.9694),
													new Tuple((float) 318.2333, (float) 30.2269),
													new Tuple((float) 314.2917, (float) 41.1669),
													new Tuple((float) 310.3542, (float) 45.2803),
													new Tuple((float) 305.5542, (float) 40.2567),
													new Tuple((float) 310.3542, (float) 45.2803),
													new Tuple((float) 292.4250, (float) 51.7294),
													new Tuple((float) 289.2750, (float) 53.3681),
													new Tuple((float) 292.4250, (float) 51.7294),
													new Tuple((float) 296.2417, (float) 45.1306),
													new Tuple((float) 305.5542, (float) 40.2567)
			};
			
			Tuple[] cygnus_az_alt = new Tuple[13];
			
			counter = 0;
			
			while (counter < 13) {
				
				az_alt = computeHorizonCoordinates(cygnus_equatorial[counter].x, cygnus_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				cygnus_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
				
				
			}
			
			cygnus = new Constellation(cygnus_az_alt);
			
			Tuple[] canis_minor_equatorial = new Tuple[]{new Tuple((float) 114.8250, (float) 5.2272),
														 new Tuple((float) 111.7875, (float) 8.2892)
			};
			
			Tuple[] canis_minor_az_alt = new Tuple[2];
			
			counter = 0;
			
			while (counter < 2) {
				
				az_alt = computeHorizonCoordinates(canis_minor_equatorial[counter].x, canis_minor_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				canis_minor_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
				
				
			}
			
			canis_minor = new Constellation(canis_minor_az_alt);
			
			Tuple[] hydra_equatorial = new Tuple[]{new Tuple((float) 222.5708, (float) -27.9600),
												   new Tuple((float) 211.5917, (float) -26.6819),
												   new Tuple((float) 199.7292, (float) -23.1714),
												   new Tuple((float) 178.2250, (float) -33.9081),
												   new Tuple((float) 173.2500, (float) -31.8575),
												   new Tuple((float) 167.9125, (float) -22.8356),
												   new Tuple((float) 164.9417, (float) -18.2989),
												   new Tuple((float) 162.4042, (float) -16.1939),
												   new Tuple((float) 156.5208, (float) -16.8358),
												   new Tuple((float) 152.6458, (float) -12.3536),
												   new Tuple((float) 151.2792, (float) -13.0644),
												   new Tuple((float) 147.8667, (float) -14.8464),
												   new Tuple((float) 141.8958, (float) -8.6586),
												   new Tuple((float) 144.9625, (float) -1.1425),
												   new Tuple((float) 138.5875, (float) 2.3150),
												   new Tuple((float) 133.8458, (float) 5.9453),
												   new Tuple((float) 131.6917, (float) 6.4189),
												   new Tuple((float) 129.4125, (float) 5.7036),
												   new Tuple((float) 129.6875, (float) 3.3414),
												   new Tuple((float) 130.8042, (float) 3.3986),
												   new Tuple((float) 132.1042, (float) 5.5378)
												  
			};
			
			Tuple[] hydra_az_alt = new Tuple[21];
			
			counter = 0;
			
			while (counter < 21) {
				
				az_alt = computeHorizonCoordinates(hydra_equatorial[counter].x, hydra_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				hydra_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
				
				
			}
			
			hydra = new Constellation(hydra_az_alt);
			
			Tuple[] cetus_equatorial = new Tuple[]{new Tuple((float) 40.8250, (float) 3.2361),
												   new Tuple((float) 45.5667, (float) 4.0897),
												   new Tuple((float) 44.9250, (float) 8.9072),
												   new Tuple((float) 41.2333, (float) 10.1142),
												   new Tuple((float) 37.0375, (float) 8.4600),
												   new Tuple((float) 40.8250, (float) 3.2361),
												   new Tuple((float) 39.8667, (float) -0.3283),
												   new Tuple((float) 27.8625, (float) -10.3347), //bb
												   new Tuple((float) 21.0042, (float) -8.1825),
												   new Tuple((float) 17.1458, (float) -10.1817),
												   new Tuple((float) 4.8542, (float) -8.8236),
												   new Tuple((float) 10.8958, (float) -17.9867),
												   new Tuple((float) 26.0208, (float) -15.9394),
												   new Tuple((float) 27.8625, (float) -10.3347)
			};
			
			Tuple[] cetus_az_alt = new Tuple[14];
			
			counter = 0;
			
			while (counter < 14) {
				
				az_alt = computeHorizonCoordinates(cetus_equatorial[counter].x, cetus_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				cetus_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
				
				
			}
			
			cetus = new Constellation(cetus_az_alt);
			
			Tuple[] pegasus_equatorial = new Tuple[]{new Tuple((float) 326.0458, (float) 9.8750),
													 new Tuple((float) 332.5458, (float) 6.1975),
													 new Tuple((float) 340.3635, (float) 10.8314),
													 new Tuple((float) 346.1875, (float) 15.2053), //markab
													 new Tuple((float) 3.3083, (float) 15.1833),
													 new Tuple((float) 2.0958, (float) 29.0906), //alpheratz
													 new Tuple((float) 345.9417, (float) 28.0822), //scheat
													 new Tuple((float) 340.7500, (float) 30.2211),
													 new Tuple((float) 332.4958, (float) 33.1781),
													 new Tuple((float) 340.7500, (float) 30.2211),
													 new Tuple((float) 345.9417, (float) 28.0822), //scheat
													 new Tuple((float) 342.5000, (float) 24.6017),
													 new Tuple((float) 341.6292, (float) 23.5656),
													 new Tuple((float) 331.7500, (float) 25.3450),
													 new Tuple((float) 326.1583, (float) 25.6447),
													 new Tuple((float) 331.7500, (float) 25.3450),
													 new Tuple((float) 341.6292, (float) 23.5656),
													 new Tuple((float) 342.5000, (float) 24.6017),
													 new Tuple((float) 345.9417, (float) 28.0822), //scheat
													 new Tuple((float) 346.1875, (float) 15.2053) //markab

			};
			
			Tuple[] pegasus_az_alt = new Tuple[20];
			
			counter = 0;
			
			while (counter < 20) {
				
				az_alt = computeHorizonCoordinates(pegasus_equatorial[counter].x, pegasus_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				pegasus_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
				
				
			}
			
			pegasus = new Constellation(pegasus_az_alt);
			
			Tuple[] gemini_equatorial = new Tuple[]{new Tuple((float) 91.0292, (float) 23.2636), 
													new Tuple((float) 93.7167, (float) 22.5067),
													new Tuple((float) 95.7375, (float) 22.5136),
													new Tuple((float) 100.9792, (float) 25.1311),
													new Tuple((float) 97.2375, (float) 20.2119),
													new Tuple((float) 100.9792, (float) 25.1311),
													new Tuple((float) 107.7833, (float) 30.2450),
													new Tuple((float) 103.1958, (float) 33.9611),
													new Tuple((float) 107.7833, (float) 30.2450),
													new Tuple((float) 113.6500, (float) 31.8886),
													new Tuple((float) 107.7833, (float) 30.2450),
													new Tuple((float) 111.4292, (float) 27.7981),
													new Tuple((float) 113.9792, (float) 26.8958),
													new Tuple((float) 116.3292, (float) 28.0261),
													new Tuple((float) 113.9792, (float) 26.8958),
													new Tuple((float) 116.1083, (float) 24.3981),
													new Tuple((float) 113.9792, (float) 26.8958),
													new Tuple((float) 110.0292, (float) 21.9822), //wassat
													new Tuple((float) 109.5208, (float) 16.5403),
													new Tuple((float) 101.3208, (float) 12.8958),
													new Tuple((float) 109.5208, (float) 16.5403),
													new Tuple((float) 110.0292, (float) 21.9822), //wassat
													new Tuple((float) 106.0250, (float) 20.5703),
													new Tuple((float) 99.4250, (float) 16.3992)

			};
			
			Tuple[] gemini_az_alt = new Tuple[24];
			
			counter = 0;
			
			while (counter < 24) {
				
				az_alt = computeHorizonCoordinates(gemini_equatorial[counter].x, gemini_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				gemini_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
				
				
			}
			
			gemini = new Constellation(gemini_az_alt);
			
			Tuple[] aquila_equatorial = new Tuple[]{new Tuple((float) 298.8250, (float) 6.4078),
													new Tuple((float) 297.6917, (float) 8.8672),
													new Tuple((float) 296.5625, (float) 10.6131),
													new Tuple((float) 291.3708, (float) 3.1144),
													new Tuple((float) 286.3500, (float) 13.8636),
													new Tuple((float) 284.9042, (float) 15.0683),
													new Tuple((float) 286.3500, (float) 13.8636),
													new Tuple((float) 286.5583, (float) -4.8822), 
													new Tuple((float) 285.4167, (float) -5.7389),
													new Tuple((float) 286.5583, (float) -4.8822), 
													new Tuple((float) 291.3708, (float) 3.1144),
													new Tuple((float) 286.5583, (float) -4.8822), 
													new Tuple((float) 294.1792, (float) -1.2864),
													new Tuple((float) 302.8250, (float) -0.8214), 
													new Tuple((float) 298.1167, (float) 1.0056),
													new Tuple((float) 291.3708, (float) 3.1144)
													
			};
			
			Tuple[] aquila_az_alt = new Tuple[16];
			
			counter = 0;
			
			while (counter < 16) {
				
				az_alt = computeHorizonCoordinates(aquila_equatorial[counter].x, aquila_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				aquila_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
				
				
			}
			
			aquila = new Constellation(aquila_az_alt);
			
			Tuple[] ophiuchus_equatorial = new Tuple[]{new Tuple((float) 261.9042, (float) -29.7244),
													   new Tuple((float) 260.5000, (float) -24.9994),
													   new Tuple((float) 257.5917, (float) -15.7250), //sabik
													   new Tuple((float) 264.3958, (float) -15.3983),
													   new Tuple((float) 269.7542, (float) -9.7733),
													   new Tuple((float) 266.9708, (float) 2.7072),
													   new Tuple((float) 265.8667, (float) 4.5667),
													   new Tuple((float) 257.5917, (float) -15.7250), //sabik
													   new Tuple((float) 265.8667, (float) 4.5667),
													   new Tuple((float) 263.7333, (float) 12.5606), 
													   new Tuple((float) 254.4167, (float) 9.3750), //cross
													   new Tuple((float) 247.7250, (float) 1.9839),
													   new Tuple((float) 243.5833, (float) -3.6939),
													   new Tuple((float) 244.5792, (float) -4.6925),
													   new Tuple((float) 246.9500, (float) -8.3717),
													   new Tuple((float) 249.2875, (float) -10.5669),
													   new Tuple((float) 254.4167, (float) 9.3750), //cross
													   new Tuple((float) 249.2875, (float) -10.5669), //2nd cross
													   new Tuple((float) 247.7833, (float) -16.6125),
													   new Tuple((float) 246.7542, (float) -18.4561),
													   new Tuple((float) 245.0250, (float) -20.0369),
													   new Tuple((float) 246.7542, (float) -18.4561),
													   new Tuple((float) 247.7833, (float) -16.6125),
													   new Tuple((float) 249.2875, (float) -10.5669), //2nd cross
													   new Tuple((float) 257.5917, (float) -15.7250) //sabik

					
			};
			
			Tuple[] ophiuchus_az_alt = new Tuple[25];
			
			counter = 0;
			
			while (counter < 25) {
				
				az_alt = computeHorizonCoordinates(ophiuchus_equatorial[counter].x, ophiuchus_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				ophiuchus_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
				
				
			}
			
			ophiuchus = new Constellation(ophiuchus_az_alt);
			
			Tuple[] canis_major_equatorial = new Tuple[]{new Tuple((float) 111.0208, (float) -29.3031),
														 new Tuple((float) 107.0958, (float) -26.3931),
														 new Tuple((float) 104.6542, (float) -28.9719),
														 new Tuple((float) 103.5292, (float) -24.1842),
														 new Tuple((float) 99.1708, (float) -19.2556), 
														 new Tuple((float) 95.6708, (float) -17.9558),
														 new Tuple((float) 101.2875, (float) -16.7128), //sirius
														 new Tuple((float) 104.0333, (float) -17.0542),
														 new Tuple((float) 105.9375, (float) -15.6331),
														 new Tuple((float) 103.5458, (float) -12.0383),
														 new Tuple((float) 104.0333, (float) -17.0542),
														 new Tuple((float) 101.2875, (float) -16.7128), //sirius
														 new Tuple((float) 107.0958, (float) -26.3931),
														 new Tuple((float) 111.0208, (float) -29.3031),
														 new Tuple((float) 107.0958, (float) -26.3931),
														 new Tuple((float) 107.0958, (float) -26.3931)
										 
			};
			
			Tuple[] canis_major_az_alt = new Tuple[16];
			
			counter = 0;
			
			while (counter < 16) {
				
				az_alt = computeHorizonCoordinates(canis_major_equatorial[counter].x, canis_major_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				canis_major_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
				
				
			}
			
			canis_major = new Constellation(canis_major_az_alt);
			
			Tuple[] grus_equatorial = new Tuple[]{new Tuple((float) 328.4792, (float) -37.3647),
												  new Tuple((float) 331.5250, (float) -39.5428),
												  new Tuple((float) 333.9000, (float) -41.3467),
												  new Tuple((float) 334.1083, (float) -41.6269),
												  new Tuple((float) 337.4375, (float) -43.7492),
												  new Tuple((float) 332.0542, (float) -46.9606),
												  new Tuple((float) 340.6625, (float) -46.8844),
												  new Tuple((float) 337.4375, (float) -43.7492),
												  new Tuple((float) 340.6625, (float) -46.8844),
												  new Tuple((float) 342.1375, (float) -51.3167),
												  new Tuple((float) 345.2167, (float) -52.7539)								 
			};
			
			Tuple[] grus_az_alt = new Tuple[11];
			
			counter = 0;
			
			while (counter < 11) {
				
				az_alt = computeHorizonCoordinates(grus_equatorial[counter].x, grus_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				grus_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
				
				
			}
			
			grus = new Constellation(grus_az_alt);
			
			Tuple[] centaurus_equatorial = new Tuple[]{new Tuple((float) 219.9167, (float) -60.8350),
													   new Tuple((float) 210.9542, (float) -60.3728),
													   new Tuple((float) 204.9708, (float) -53.4661), //birdun
													   new Tuple((float) 190.3792, (float) -48.9597), //mulphien
													   new Tuple((float) 187.0083, (float) -50.2306), //fork
													   new Tuple((float) 182.0208, (float) -50.6611),
													   new Tuple((float) 170.2500, (float) -54.4908),
													   new Tuple((float) 182.0208, (float) -50.6611),
													   new Tuple((float) 187.0083, (float) -50.2306), //fork
													   new Tuple((float) 182.9125, (float) -52.3683),
													   new Tuple((float) 172.9500, (float) -59.5156),
													   new Tuple((float) 182.9125, (float) -52.3683),
													   new Tuple((float) 187.0083, (float) -50.2306), //fork
													   new Tuple((float) 190.3792, (float) -48.9597), //mulphien
													   new Tuple((float) 208.8833, (float) -47.2881), //hip 68002
													   new Tuple((float) 204.9708, (float) -53.4661), //birdun
													   new Tuple((float) 208.8833, (float) -47.2881), //hip 68002
													   new Tuple((float) 207.4042, (float) -42.4736),
													   new Tuple((float) 207.3750, (float) -41.6875), //hip 67464
													   new Tuple((float) 202.7583, (float) -39.4072),
													   new Tuple((float) 200.1500, (float) -36.7119),
													   new Tuple((float) 189.9667, (float) -39.9872),
													   new Tuple((float) 200.1500, (float) -36.7119),
													   new Tuple((float) 202.7583, (float) -39.4072),
													   new Tuple((float) 207.3750, (float) -41.6875), //hip 67464
													   new Tuple((float) 211.6708, (float) -36.3686),
													   new Tuple((float) 215.1375, (float) -37.8850),
													   new Tuple((float) 211.5083, (float) -41.1794),
													   new Tuple((float) 209.5667, (float) -42.1006), //a
													   new Tuple((float) 210.4292, (float) -45.6033),
													   new Tuple((float) 208.8833, (float) -47.2881), //hip 68002
													   new Tuple((float) 210.4292, (float) -45.6033),
													   new Tuple((float) 209.5667, (float) -42.1006), //a
													   new Tuple((float) 218.8750, (float) -43.1575),
													   new Tuple((float) 224.7875, (float) -42.1039)
			};
			
			Tuple[] centaurus_az_alt = new Tuple[35];
			
			counter = 0;
			
			while (counter < 35) {
				
				az_alt = computeHorizonCoordinates(centaurus_equatorial[counter].x, centaurus_equatorial[counter].y, l.getLatitude(), l.getLongitude());
				centaurus_az_alt[counter] = az_alt;
				//Log.e(TAG, "azimuth: " + az_alt.x + ", altitude: " + az_alt.y);
			
				counter++;
				
				
			}
			
			centaurus = new Constellation(centaurus_az_alt);
			
			
		*/	
		
	}

	private void buildLines() {
		
		
		equatorial = new Equatorial();
		
		degrees = new InfoRectangle[12];
		
		Location l = this.currentLocation;
		
		float lat = (float) l.getLatitude();
		float lon = (float) l.getLongitude();
		
		Tuple az_alt;
		int counter = 0;
		int tex_id;
		
		for (int DEC = -30; DEC < 60; DEC += 30) {
			
			for (int RA = 0; RA < 360; RA += 90) {
				
				//Log.e(TAG, "RA: " + RA + ", DEC: " + DEC);
				
				if (DEC == -30) {
					tex_id = 15;
				} else if (DEC == 30) {
					tex_id = 16;
				} else {
					tex_id = 17;
				}
		
				az_alt = computeHorizonCoordinates(RA + 3, DEC + 3, lat, lon);
				degrees[counter] = new InfoRectangle(az_alt.x, az_alt.y, 100, 120, tex_id);
				counter++;

			}
		}
		
	//	Log.e(TAG, "info rectangle (" + az_alt.x + ", " + az_alt.y + ")");
		

//		zero_degreesGraphic = new InfoRectangle(5, 5, 100, 120, 20);
//		ninety_degreesGraphic = new InfoRectangle(95, 5, 100, 120, 21);
//		one_eighty_degreesGraphic = new InfoRectangle(185, 5, 100, 120, 22);
//		two_seventy_degreesGraphic = new InfoRectangle(275, 5, 100, 120, 23);

		
	}
	
	private void buildTexts() {
		
		Location l = this.currentLocation;
		float offset;
		
		if (zoom > 0.8) {
			
			offset = 0;
			
			Tuple az_alt = computeHorizonCoordinates(sun.getRA(), sun.getDEC() + offset, l.getLatitude(), l.getLongitude());
			sunTextGraphic = new InfoRectangle((float) az_alt.x, (float) (az_alt.y), 150, (float) (sun.getRVEC() - 0.2), 22);
			//selectionRectangle = new InfoRectangle(az_alt.x, az_alt.y, 150, (float) (sun.getRVEC() - 0.2), 21);
			
			
			az_alt = computeHorizonCoordinates(moon.getRA(), moon.getDEC() + offset, l.getLatitude(), l.getLongitude());
			moonTextGraphic = new InfoRectangle((float) az_alt.x, (float) (az_alt.y), 150, 80, 23);
			
			Log.e(TAG, "moon text is at az: " + az_alt.x + ", altitude: " + az_alt.y);
			
			az_alt = computeHorizonCoordinates(mercury.getRA(), mercury.getDEC() + offset, l.getLatitude(), l.getLongitude());
			mercuryTextGraphic = new InfoRectangle((float) az_alt.x, (float) (az_alt.y), 150, 80, 24);
			az_alt = computeHorizonCoordinates(venus.getRA(), venus.getDEC() + offset, l.getLatitude(), l.getLongitude());		
			venusTextGraphic = new InfoRectangle((float) az_alt.x, (float) (az_alt.y), 150, 80, 25);
			az_alt = computeHorizonCoordinates(mars.getRA(), mars.getDEC() + offset, l.getLatitude(), l.getLongitude());
			marsTextGraphic = new InfoRectangle((float) az_alt.x, (float) (az_alt.y), 150, 80, 26);
			az_alt = computeHorizonCoordinates(jupiter.getRA(), jupiter.getDEC() + offset, l.getLatitude(), l.getLongitude());
			jupiterTextGraphic = new InfoRectangle((float) az_alt.x, (float) (az_alt.y), 150, 80, 27);
			az_alt = computeHorizonCoordinates(saturn.getRA(), saturn.getDEC() + offset, l.getLatitude(), l.getLongitude());
			saturnTextGraphic = new InfoRectangle((float) az_alt.x, (float) (az_alt.y), 150, 80, 28);
			az_alt = computeHorizonCoordinates(uranus.getRA(), uranus.getDEC() + offset, l.getLatitude(), l.getLongitude());
			uranusTextGraphic = new InfoRectangle((float) az_alt.x, (float) (az_alt.y), 150, 80, 29);
			az_alt = computeHorizonCoordinates(neptune.getRA(), neptune.getDEC() + offset, l.getLatitude(), l.getLongitude());
			neptuneTextGraphic =new InfoRectangle((float) az_alt.x, (float) (az_alt.y), 150, 80, 30);
			az_alt = computeHorizonCoordinates(pluto.getRA(), pluto.getDEC() + offset, l.getLatitude(), l.getLongitude());
			plutoTextGraphic = new InfoRectangle((float) az_alt.x, (float) (az_alt.y), 150, 80, 31);
			

		} else {
			offset = 0f;
			
			
			Tuple az_alt = computeHorizonCoordinates(sun.getRA(), sun.getDEC() + sun.getRadius() + offset, l.getLatitude(), l.getLongitude());
			sunTextGraphic = new InfoRectangle((float) az_alt.x, (float) (az_alt.y), 150, (float) (sun.getRVEC() - 0.2), 22);
			az_alt = computeHorizonCoordinates(moon.getRA(), moon.getDEC() + moon.getRadius() + 0.5, l.getLatitude(), l.getLongitude());
			moonTextGraphic = new InfoRectangle((float) az_alt.x, (float) (az_alt.y), 150, 80, 23);
			az_alt = computeHorizonCoordinates(mercury.getRA(), mercury.getDEC() + mercury.getRadius() + 0.5, l.getLatitude(), l.getLongitude());
			mercuryTextGraphic = new InfoRectangle((float) az_alt.x, (float) (az_alt.y), 150, 80, 24);
			az_alt = computeHorizonCoordinates(venus.getRA(), venus.getDEC() + venus.getRadius() + offset, l.getLatitude(), l.getLongitude());		
			venusTextGraphic = new InfoRectangle((float) az_alt.x, (float) (az_alt.y), 150, 80, 25);
			az_alt = computeHorizonCoordinates(mars.getRA(), mars.getDEC() + mars.getRadius() + offset, l.getLatitude(), l.getLongitude());
			marsTextGraphic = new InfoRectangle((float) az_alt.x, (float) (az_alt.y), 150, 80, 26);
			az_alt = computeHorizonCoordinates(jupiter.getRA(), jupiter.getDEC() + jupiter.getRadius() + offset, l.getLatitude(), l.getLongitude());
			jupiterTextGraphic = new InfoRectangle((float) az_alt.x, (float) (az_alt.y), 150, 80, 27);
			az_alt = computeHorizonCoordinates(saturn.getRA(), saturn.getDEC() + saturn.getRadius() + offset, l.getLatitude(), l.getLongitude());
			saturnTextGraphic = new InfoRectangle((float) az_alt.x, (float) (az_alt.y), 150, 80, 28);
			az_alt = computeHorizonCoordinates(uranus.getRA(), uranus.getDEC() + uranus.getRadius() + offset, l.getLatitude(), l.getLongitude());
			uranusTextGraphic = new InfoRectangle((float) az_alt.x, (float) (az_alt.y), 150, 80, 29);
			az_alt = computeHorizonCoordinates(neptune.getRA(), neptune.getDEC() + neptune.getRadius() + offset, l.getLatitude(), l.getLongitude());
			neptuneTextGraphic =new InfoRectangle((float) az_alt.x, (float) (az_alt.y), 150, 80, 30);
			az_alt = computeHorizonCoordinates(pluto.getRA(), pluto.getDEC() + pluto.getRadius() + offset, l.getLatitude(), l.getLongitude());
			plutoTextGraphic = new InfoRectangle((float) az_alt.x, (float) (az_alt.y), 150, 80, 31);

		}
		
	}
	
	private void renderTexts() {
		
		if (planetTextsStatus) {
		
			sunTextGraphic.render();
			//selectionRectangle.render();
			moonTextGraphic.render();
			mercuryTextGraphic.render();
			venusTextGraphic.render();
			marsTextGraphic.render();
			jupiterTextGraphic.render();
			saturnTextGraphic.render();
			uranusTextGraphic.render();
			neptuneTextGraphic.render();
		}

	}
	
	private void renderUniverse() {
		
		
		renderPlanets();
		if (equatorialStatus) {
		
			renderLines();
		
		}
		
		if (constellationStatus) {
			
			renderConstellations();
			
		}

	
		renderTexts();
		
		if (selectedStar) {
		
			
			if (selectionRectangle != null ){
	
				selectionRectangle.render();

			}
		}
		
		celestialArray.render();
		
		//Log.e(TAG, "Error: " + GLES20.glGetError());
		
		
		
	}
	
	public void renderPlanets() {

		/*Log.e(TAG,"Sun RA :" + sun.getRA() + ", DEC: " + sun.getDEC());
		Log.e(TAG,"Mercury RA :" + mercury.getRA() + ", DEC: " + mercury.getDEC());
		Log.e(TAG,"Venus RA :" + venus.getRA() + ", DEC: " + venus.getDEC());
		Log.e(TAG,"Mars RA :" + mars.getRA() + ", DEC: " + mars.getDEC());
		Log.e(TAG,"Jupiter RA :" + jupiter.getRA() + ", DEC: " + jupiter.getDEC());
		Log.e(TAG,"Saturn RA :" + saturn.getRA() + ", DEC: " + saturn.getDEC());
		Log.e(TAG,"Uranus RA :" + uranus.getRA() + ", DEC: " + uranus.getDEC());
		Log.e(TAG,"Neptune RA :" + neptune.getRA() + ", DEC: " + neptune.getDEC());
		Log.e(TAG,"Pluto RA :" + pluto.getRA() + ", DEC: " + pluto.getDEC()); */
		
		if (zoom < 0.9) {
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glUniform1i(textureUniform, 1);
		sunGraphic.render();
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
		GLES20.glUniform1i(textureUniform, 2);
		moonGraphic.render();
		
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
		GLES20.glUniform1i(textureUniform, 3);
		mercuryGraphic.render();

		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
		GLES20.glUniform1i(textureUniform, 4);
		venusGraphic.render();
		  
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
		GLES20.glUniform1i(textureUniform, 5);
		marsGraphic.render();
		

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE6);
		GLES20.glUniform1i(textureUniform, 6);
		jupiterGraphic.render();
 
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE7);
		GLES20.glUniform1i(textureUniform, 7);
		saturnGraphic.render();

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE8);
		GLES20.glUniform1i(textureUniform, 8);
		uranusGraphic.render();

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE9);
		GLES20.glUniform1i(textureUniform, 9);
		neptuneGraphic.render();
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE10);
		GLES20.glUniform1i(textureUniform, 10);
		plutoGraphic.render();
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE12);
		GLES20.glUniform1i(textureUniform, 12);
		ioGraphic.render();
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE13);
		GLES20.glUniform1i(textureUniform, 13);
		europaGraphic.render();
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE14);
		GLES20.glUniform1i(textureUniform, 14);
		ganymedeGraphic.render();
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE18);
		GLES20.glUniform1i(textureUniform, 18);
		callistoGraphic.render();
		
		}
	//	Log.e(TAG, "error is " + GLES20.glGetError());
		
		
	//	}


	}
	
	private void renderLines() {

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE11);
		GLES20.glUniform1i(textureUniform, 11);
		GLES20.glLineWidth(1);
	
		equatorial.render();


		
		for (InfoRectangle r : degrees) {
			r.render();
		}
		

	}
	
	private void renderConstellations() {
		
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE11);
		GLES20.glUniform1i(textureUniform, 11);
		GLES20.glLineWidth(1);
		
		for (Constellation c : constellations) {
			c.render();
		}
		
		/*orion.render();
		crux.render();
		dipper.render();
		littleBear.render();
		capricornus.render();
		bootes.render();
		lyra.render();
		eridanus.render();
		scorpius.render();
		andromeda.render();
		auriga.render();
		leo.render();
		carina.render();
		cygnus.render();
		canis_minor.render();
		hydra.render();
		cetus.render();
		pegasus.render();
		gemini.render();
		aquila.render();
		ophiuchus.render();
		canis_major.render();
		grus.render();
		centaurus.render();*/
		
		
	}

	private void rebuildPlanetGraphics() {

		if (mercuryGraphic != null) {
			mercuryGraphic.build((float) (mercury.getAZ()),
					(float) (mercury.getALT()), (float) mercury.getRVEC());
			mercuryGraphic.rebuildBuffers();
			
			//Log.e(TAG, "mercury az: " + mercury.getAZ() + ", alt: " + mercury.getALT());
		}
		
		if (venusGraphic != null) {
		 
			  venusGraphic.build((float) (venus.getAZ()),
						 (float) (venus.getALT()), (float) venus.getRVEC());
			  venusGraphic.rebuildBuffers();
		}
		
		if (marsGraphic != null) {
			marsGraphic.build((float) (mars.getAZ()),
					(float) (mars.getALT()), (float) mars.getRVEC());
			marsGraphic.rebuildBuffers();
		}
		
		if (jupiterGraphic != null) {
			jupiterGraphic.build((float) (jupiter.getAZ()),
					(float) (jupiter.getALT()), (float) jupiter.getRVEC());
			jupiterGraphic.rebuildBuffers();
		}
		
		if (saturnGraphic != null) {
			saturnGraphic.build((float) (saturn.getAZ()),
					(float) (saturn.getALT()), (float) saturn.getRVEC());
			saturnGraphic.rebuildBuffers();
		}
		
		if (uranusGraphic != null) {
			uranusGraphic.build((float) (uranus.getAZ()),
					(float) (uranus.getALT()), (float) uranus.getRVEC());
			uranusGraphic.rebuildBuffers();
		}
		
		if (neptuneGraphic != null) {
			neptuneGraphic.build((float) (neptune.getAZ()),
					(float) (neptune.getALT()), (float) neptune.getRVEC());
			neptuneGraphic.rebuildBuffers();
		}
		
		if (plutoGraphic != null) {
			plutoGraphic.build((float) (pluto.getAZ()),
					(float) (pluto.getALT()), (float) pluto.getRVEC());
			plutoGraphic.rebuildBuffers();
		}
		
		if (sunGraphic != null) {
			  sunGraphic.build((float) (sun.getAZ()),
					 (float) (sun.getALT()), (float) sun.getRVEC());
			  sunGraphic.rebuildBuffers();
		}
		
		if (moonGraphic != null) {
			  moonGraphic.build((float) (moon.getAZ()),
						 (float) (moon.getALT()), moon.getRVEC());
			  moonGraphic.rebuildBuffers();
			
		}

	}
	
	private class LoadStarsTask extends AsyncTask<String, Void, String> {
		
		private boolean cancelled = false;
		private long startTime;

		@Override
		protected String doInBackground(String... arg0) {
			
			startTime = System.currentTimeMillis();
			Star[] stars;
			starsLoading = true;
			int STAR_COUNT = NUM_STARS;
			stars = new Star[STAR_COUNT];
			starCursor.moveToFirst();
			starCursor.moveToNext();
					
			int counter = 0;
			
			while (counter < STAR_COUNT) {
				
				if (isCancelled()) {
				//	Log.e("TAG", "Cancelled task");
					break;
				}
				
				int star_id = starCursor.getInt(0);
				float x = starCursor.getFloat(1);
				float y = starCursor.getFloat(2);
				float z = starCursor.getFloat(3);
				float rightAscension = starCursor.getFloat(4);
				float declination = starCursor.getFloat(5);
				CelestialColor starColor;
				
				if (!starCursor.isNull(4)) {
					starColor = indexToColor(starCursor.getFloat(6));
				} else {
					starColor = CelestialColor.White;
				}

				float mag = starCursor.getFloat(7);
				float distance = starCursor.getFloat(8);

				
				rightAscension = rightAscension*15;
				
				String bayerName = starCursor.getString(9);
				
				
				String properName = starCursor.getString(10);

				if (properName == null){
					properName = "null";
					
				}
				
				Location currentLocation = ((OpenGLES20Activity) context).getRenderer().currentLocation;
				
				float lat = 0;
				float lon = 0;
				
				if (currentLocation != null) {
					
					lat = (float) currentLocation.getLatitude();
					lon = (float) currentLocation.getLongitude();
					
				}
				
			
				
				//Tuple az_alt = computeHorizonCoordinates(rightAscension, declination, lat, lon);
				Star s = new Star(star_id, rightAscension, declination, 0, 0, starColor, mag, bayerName, properName, distance);
				
				s.computeHorizonCoordinates(lat, lon);
				//Log.e(TAG, "stars az: " + s.getAzimuth() + ", " + s.getAltitude());
				
				//Log.e(TAG, "current location (" + az_alt.x + ", " + az_alt.y + ")");
				
				
				stars[counter] = s;

				starCursor.moveToNext();

				counter++;


			}
		

			
			if (!isCancelled()) {
			
		//	celestialArray = new CelestialArray(stars);
				
			Log.e(TAG, "Time taken for " + stars.length + " is " + (System.currentTimeMillis() - startTime));
			setStarArray(stars);
			celestialArray.build(stars);
			return "OK";
			}
			
			return "CANCELLED";


		}
		
		@Override
		protected void onPostExecute(String result) {
			
			if (result.equals("OK")) {
			Log.e(TAG, "stars finished loading");
			starsLoading = false;
			newStars = true;
			}
		
			
		}
		
		
	}
	
	private void setStarArray(Star[] stars) {
		this.stars = stars;
	}
	
	private void rebuildStarGraphics() {
		
		if (!starsLoading) {
			
			Log.e(TAG, "creating new task");
			task = new LoadStarsTask();
			task.execute();
		} else {
			task.cancel(true);
			task = new LoadStarsTask();
			task.execute();
			
		}

		
	}

	
	
	/** Returns the colour associated with a given colorindex */
	private CelestialColor indexToColor(float colorIndex) {
		if (colorIndex > 1.11) {
			return CelestialColor.Red;
		} else if (colorIndex > 0.71) {
			return CelestialColor.Orange;
		} else if (colorIndex > 0.45) {
			return CelestialColor.Yellow;
		} else if (colorIndex > 0.15) {
			return CelestialColor.Yellowish;
		} else if (colorIndex > -0.15) {
			return CelestialColor.White;
		} else {
			return CelestialColor.Blue;
		}
	}
	
	/** Loads the textures for stars/planets */
	private void loadPlanetStarTextures() {
		
		sunTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.sun);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, sunTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, sunTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		moonTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.moon);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, moonTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, moonTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		mercuryTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.mercury);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mercuryTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mercuryTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		venusTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.venus);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, venusTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, venusTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

		marsTextureHandle = TextureHelper.loadTexture(context, R.drawable.mars);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, marsTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, marsTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

		jupiterTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.jupiter);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE6);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, jupiterTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, jupiterTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

		saturnTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.saturn);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE7);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, saturnTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, saturnTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

		uranusTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.uranus);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE8);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uranusTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uranusTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

		neptuneTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.neptune);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE9);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, neptuneTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, neptuneTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

		plutoTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.pluto);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE10);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, plutoTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, plutoTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

		whiteTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.star1);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE11);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, whiteTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, whiteTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		ioTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.io);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE12);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,ioTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ioTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		europaTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.europa);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE13);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,europaTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, europaTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		ganymedeTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.ganymede);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE14);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ganymedeTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ganymedeTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		callistoTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.ganymede);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE18);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, callistoTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, callistoTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
	}
	
	public void loadOtherTextures() {
		
		
		minus_thirty_degreesTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.minus_thirty_degrees);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 15);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, minus_thirty_degreesTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, minus_thirty_degreesTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		thirty_degreesTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.thirty_degrees);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 16);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, thirty_degreesTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, thirty_degreesTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		zero_degreesTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.zero_degrees);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 17);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, zero_degreesTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, zero_degreesTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0); 
		
		/*ninety_degreesTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.ninety_degrees);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 18);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ninety_degreesTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ninety_degreesTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0); */
		
		one_eighty_degreesTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.one_eighty_degrees);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 19);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, one_eighty_degreesTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, one_eighty_degreesTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		two_seventy_degreesTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.two_seventy_degrees);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 20);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, two_seventy_degreesTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, two_seventy_degreesTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		selectionTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.selection);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 21);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, selectionTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, selectionTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		
		sunTextTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.sun_text);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 22);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, sunTextTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, sunTextTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		moonTextTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.moon_text);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 23);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, moonTextTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, moonTextTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

		mercuryTextTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.mercury_text);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 24);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mercuryTextTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mercuryTextTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		venusTextTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.venus_text);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 25);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, venusTextTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, venusTextTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		
		marsTextTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.mars_text);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 26);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, marsTextTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, marsTextTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		jupiterTextTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.jupiter_text);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 27);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, jupiterTextTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, jupiterTextTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		saturnTextTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.saturn_text);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 28);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, saturnTextTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, saturnTextTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		uranusTextTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.uranus_text);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 29);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uranusTextTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uranusTextTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		neptuneTextTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.neptune_text);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 30);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, neptuneTextTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, neptuneTextTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		plutoTextTextureHandle = TextureHelper.loadTexture(context,
				R.drawable.moon_text);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 31);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, plutoTextTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, plutoTextTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		
		
	}

	public void zoom(float mult) {

		this.zoom = mult;
		
		if (zoom != 1) {
			NUM_STARS = (int) (MIN_STARS + (float) (Math.min(((100/zoom)*4)*STAR_SCALE, 30000)));   
		} else {
			NUM_STARS = MIN_STARS ; 
		}
		
		//Log.e(TAG, "num_stars: "+ NUM_STARS);
		
		alteredZoom = true;

		Matrix.frustumM(projectionMatrix, 0, zoom * left, zoom * right, zoom
				* bottom, zoom * top, near, far);
		


	}
	
	public void zoomWithoutUpdate(float mult) {
		
		this.zoom = mult;
		
		Matrix.frustumM(projectionMatrix, 0, zoom * left, zoom * right, zoom
				* bottom, zoom * top, near, far);
		
	}

	public float[] intersect(float x1, float y1, float z1, float x2, float y2,
			float z2, float cx, float cy, float cz, float r) {

		float[] coords = new float[3];

		float dx = x2 - x1;
		float dy = y2 - y1;
		float dz = z2 - z1;

		float a = dx * dx + dy * dy + dz * dz;
		float b = 2 * dx * (x1 - cx) + 2 * dy * (y1 - cy) + 2 * dz * (z1 - cz);
		float c = cx * cx + cy * cy + cz * cz + x1 * x1 + y1 * y1 + z1 * z1
				+ -2 * (cx * x1 + cy * y1 + cz * z1) - r * r;

		float discriminant = b * b - 4 * a * c;
		float t;
		if (discriminant > 0) {
			t = (float) ((-b - Math.sqrt(discriminant)) / 2 * a);
			coords[0] = x1 + t * dx;
			coords[1] = y1 + t * dy;
			coords[2] = z1 + t * dz;

		}

		return coords;

	}
	
	public void findPlanet(SolarSystemObject p) {
		
		//Log.e(TAG, "finding planet: " + p.toString());
		
     //   zoom(1);
		
        switch (p) {
        case Mercury:
            newYaw = (float) -mercury.getRA();
            newPitch = (float) mercury.getDEC();
            newZoom =  (float) (0.02/mercury.getRVEC());
            break;
                     
            
        case Venus:
     	   	newYaw = (float) -venus.getRA();
     	   	newPitch = (float) venus.getDEC();
            newZoom =  (float) (0.05/venus.getRVEC());
            break;
     	   	
        case Mars:
     	   	newYaw = (float) -mars.getRA();
     	   	newPitch = (float) mars.getDEC();
            newZoom =  (float) (0.02/mars.getRVEC());
            break;
     	   	
        case Jupiter:
     	   	newYaw = (float) -jupiter.getRA();
     	   	newPitch = (float) jupiter.getDEC();
            newZoom =  (float) (0.1/jupiter.getRVEC());
            break;
            
        case Saturn:
     	   	newYaw = (float) -saturn.getRA();
     	   	newPitch = (float) saturn.getDEC();
            newZoom =  (float) (0.1/saturn.getRVEC());
            break;
            
        case Uranus:
     	   	newYaw = (float) -uranus.getRA();
     	   	newPitch = (float) uranus.getDEC();
            newZoom =  (float) (0.1/uranus.getRVEC());
            
            Log.e("TAG", "uranus yaw is " + newYaw + ", pitch: " + newPitch);
            break;
            
        case Neptune:
     	   	newYaw = (float) -neptune.getRA();
     	   	newPitch = (float) neptune.getDEC();
            newZoom =  (float) (0.1/neptune.getRVEC());
            break;
            
        case Pluto:
     	   	newYaw = (float) -pluto.getRA();
     	   	newPitch = (float) pluto.getDEC();
            newZoom =  (float) (0.1/pluto.getRVEC());
            break;
        
        case Sun:
        	newYaw = (float) -sun.getRA();
            newPitch = (float) sun.getDEC();
            newZoom = (float) (0.7/sun.getRVEC());
            break;
            
        case Moon:
        	newYaw = (float) -moon.getRA();
            newPitch = (float) moon.getDEC();
            newZoom = (float) (0.02/moon.getRVEC());
            break;           
 
            

        default:
        }
          
        
        
        yawDiff = newYaw - yaw;
        pitchDiff = newPitch - pitch;
        zoomDiff = zoom - newZoom;
        
        
        

        findingPlanet = true;
        firstTime = true;
	
	}
	
	public void chooseSelection(float x, float y) {
		
		selectionX = x;
		selectionY = y;
		selected = true;
		
		
	}
	
	public void setUnselected() {
		selected = false;
		selectedStar = false;
		
		OpenGLES20Activity a = (OpenGLES20Activity) context;
        RalewayThin star_info = (RalewayThin) a.findViewById(R.id.star_info);
        star_info.setVisibility(View.GONE);
	}
	
	public void findClosest(float x, float y) {

		float[] nearPos = new float[4];
		
		IntBuffer viewportBuffer = IntBuffer.allocate(4);
		GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewportBuffer);
		
		int[] viewport = {viewportBuffer.get(0), viewportBuffer.get(1), viewportBuffer.get(2), viewportBuffer.get(3)}; 

		boolean unprojectedNear = (GLU.gluUnProject(x, viewport[3]-y, 0.0f, modelViewMatrix, 0,
				projectionMatrix, 0, viewport, 0, nearPos, 0) == GLES20.GL_TRUE);
		
		nearPos[0] /= nearPos[3];
		nearPos[1] /= nearPos[3];
		nearPos[2] /= nearPos[3];
	
	
		float min_star_distance = 1000;
		float min_planet_distance = 1000;
		float min_distance;
		int objectType = 0;
		Star closestStar = null;
		Circle closestPlanet = null;
	
		
		//Log.e(TAG, "checking " + stars.length + " stars");
		for (Star s : stars) {
			
			float distance = s.distanceToStar(nearPos);
			//Log.e(TAG, "distance: " + distance);
			if (distance < min_star_distance) {
				min_star_distance = distance;
				closestStar = s;
				
			}
			counter++;

		} 
		
		min_distance = min_star_distance;
		
		counter = 0;
		
		for (Circle c : circles) {
			
			float distance = c.distanceToCircle(nearPos);
			//Log.e(TAG, "distance: " + distance);
			if (distance < min_planet_distance) {
				min_planet_distance = distance;
				closestPlanet = c;
				
			}
			counter++;
		}
		
		if (min_planet_distance < min_star_distance) {
			
			objectType = 1;
			min_distance = min_planet_distance;
			
		}
		

	
		 
	//	Log.e(TAG, "closest has colour " + closest.getCelestialColor());
		//selectionRectangle = new InfoRectangle(closest.getAzimuth(), closest.getAltitude(), 100, 110, 24);
		selectedStar = true;
		
		final Star s = closestStar;
		final Circle c = closestPlanet;
		
		final int objectTypeFinal = objectType;
		
		  if (objectType == 0)  {
			  if (s != null){
			  Log.e(TAG, "star azimuth: " + s.getAzimuth() + ", altitude: " + s.getAltitude());
			  selectionRectangle = new InfoRectangle(s.getAzimuth(), s.getAltitude(), 150, 80, 21);
			  }
		  } else {
			  if (c != null) {
			  Log.e(TAG, "planet azimuth: " + c.getAZ() + ", altitude: " + c.getAlt());

			  selectionRectangle = new InfoRectangle((float) c.getAZ(), (float) c.getAlt(), 150, 80, 21);
			  }
			  
		  } 

	    final Runnable updateRunnable = new Runnable() {
	        public void run() {
	            //call the activity method that updates the UI
	    		OpenGLES20Activity a = (OpenGLES20Activity) context;
	            RalewayThin star_info = (RalewayThin) a.findViewById(R.id.star_info);
	            star_info.setVisibility(View.VISIBLE);
	            if (objectTypeFinal == 0) {
	            	if (s != null){
		            if (!s.getProperName().equals("null")) {
		            	star_info.setText(s.getProperName() + "\n" +
		            					  "ID: " + s.getID() + "\n" +
		            					  "Azimuth: " + String.format("%.2f",s.getAzimuth())+ "\n" +
		            					  "Altitude: " + String.format("%.2f",s.getAltitude()) + "\n" +
		            					  "Apparent magnitude: " + String.format("%.2f",s.getBrightness()) + "\n" +
		            					  "Distance: " +String.format("%.2f", s.getDistance()) + " parsecs\n" +
		            					  "RA: " + String.format("%.2f",s.getRightAscension()) + "\n" +
		            					  "DEC: " + String.format("%.2f",s.getDeclination()) + "\n");
		            } else {
		            	star_info.setText("ID: " + s.getID() + "\n" +
		            		  "Azimuth: " + String.format("%.2f",s.getAzimuth()) + "\n" +
	      					  "Altitude: " + String.format("%.2f",s.getAltitude()) + "\n" +
	      					  "Apparent magnitude: " + String.format("%.2f",s.getBrightness()) + "\n" +
	      					  "Distance: " + String.format("%.2f",s.getDistance()) + " parsecs\n" +
        					  "RA: " + String.format("%.2f",s.getRightAscension()) + "\n" +
        					  "DEC: " + String.format("%.2f",s.getDeclination()) + "\n");
		            }
	            	}
	            } else {
	            	if (c != null) {
	            		if (c.getPhase() != 0) {
	            	star_info.setText(c.getName() + "\n" +
      					  "Azimuth: " + String.format("%.2f",c.getAZ()) + "\n" +
      					  "Altitude: " + String.format("%.2f",c.getAlt()) + "\n" +
      					  "Distance: " + String.format("%.2f",c.getDistance()/100) + " AU\n" +
      					  "Apparent magnitude: " + String.format("%.2f",c.getMagnitude()) + "\n" +
      					  "RA: " + String.format("%.2f",c.getRA()) + "\n" +
      					  "DEC: " + String.format("%.2f",c.getDEC()) + "\n" +
      					"Radius: " + String.format("%.2f", c.getRadius() * scaleFactor/100) + "km\n" +
      					  "Phase: " + String.format("%.2f",c.getPhase()) + "\n");
	            		
	            		} else {
	            			if (c.getMagnitude() != 0) {
	            			star_info.setText(c.getName() + "\n" +
	            					  "Azimuth: " + String.format("%.2f",c.getAZ()) + "\n" +
	            					  "Altitude: " + String.format("%.2f",c.getAlt()) + "\n" +
	            					  "Distance: " + String.format("%.2f",c.getDistance()/100) + " AU\n" +
	            					  "Apparent magnitude: " + String.format("%.2f",c.getMagnitude()) + "\n" +
	            					  "RA: " + String.format("%.2f",c.getRA()) + "\n" +
	            					  "DEC: " + String.format("%.2f",c.getDEC()) + "\n" +
	            						"Radius: " + String.format("%.2f", c.getRadius()*scaleFactor/100) + "km");
	            					 // "Phase: " + String.format("%.2f",c.getPhase()) + "\n");
	            			} else {
		            			star_info.setText(c.getName() + "\n" +
		            					  "Azimuth: " + String.format("%.2f",c.getAZ()) + "\n" +
		            					  "Altitude: " + String.format("%.2f",c.getAlt()) + "\n" +
		            					  "Distance: " + String.format("%.2f",c.getDistance()/100) + " AU\n" +
		            					  "RA: " + String.format("%.2f",c.getRA()) + "\n" +
		            					  "DEC: " + String.format("%.2f",c.getDEC()) + "\n" +
		            						"Radius: " + String.format("%.2f", c.getRadius()*scaleFactor/100) + "km");
		            					 // "Phase: " + String.format("%.2f",c.getPhase()) + "\n");
	            			}
	            		}
	            	
	            	
	            }
	        }
	        }
	    };
	    
	    
		 Activity activity=(Activity) this.context; 
		    activity.runOnUiThread(updateRunnable); 
		    

		    
		  //150, 80, 30
		   
		    
		  


	} 

	public float getZoom() {
		return this.zoom;
	}

	public void releasePlanetBuffers() {

		mercuryGraphic.release();
		venusGraphic.release();
		marsGraphic.release();
		jupiterGraphic.release();
		saturnGraphic.release();
		uranusGraphic.release();
		neptuneGraphic.release();
		plutoGraphic.release();
		sunGraphic.release();

	}
	
	private float[] getStarColour(CelestialColor c) {
		
		float[] rgba = new float[4];
		
		if (c == CelestialColor.Blue) {
			rgba[0] = 0.106f;
			rgba[1] = 0.0078f;
			rgba[2] = 0.9921f;
			rgba[3] = 1.0f;
			
		} else if (c == CelestialColor.Orange) {
			rgba[0] = 0.933f;
			rgba[1] = 0.4f;
			rgba[2] = 0.0667f;
			rgba[3] = 1.0f;
			
		} else if (c == CelestialColor.Red) {
			rgba[0] = 1.0f;
			rgba[1] = 0.0f;
			rgba[2] = 0.0314f;
			rgba[3] = 1.0f;
			
		} else if (c == CelestialColor.Yellow) {
			rgba[0] = 0.996f;
			rgba[1] = 0.980f;
			rgba[2] = 0.0039f;
			rgba[3] = 1.0f;
			
		} else if (c == CelestialColor.Yellowish) {
			rgba[0] = 0.996f;
			rgba[1] = 0.980f;
			rgba[2] = 0.0039f;
			rgba[3] = 1.0f;
			
		} else {
			rgba[0] = 1.0f;
			rgba[1] = 1.0f;
			rgba[2] = 1.0f;
			rgba[3] = 1.0f;
			
		}
		
		return rgba;
			
		
		
	}

	private double mean_sidereal_time(double lon) {

		
			Calendar UTCTime = ((OpenGLES20Activity) context).getUTCTime();
			
			int year = UTCTime.get(Calendar.YEAR);
			int month = UTCTime.get(Calendar.MONTH) + 1;
			int day = UTCTime.get(Calendar.DAY_OF_MONTH);
			int hour = UTCTime.get(Calendar.HOUR_OF_DAY);
			int minute = UTCTime.get(Calendar.MINUTE);
			int second = UTCTime.get(Calendar.SECOND);
			
	
			
			
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

	private void buildMoonTexture(float elongation) {
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		if (elongation > 0 && elongation <= 15) {
			
			moonTextureHandle = TextureHelper.loadTexture(context,
					R.drawable.moon_new);
			
		} else if (elongation > 15 && elongation <= 75) {
			
			moonTextureHandle = TextureHelper.loadTexture(context,
					R.drawable.moon_waxing_crescent);
			
		} else if (elongation > 75 && elongation <= 105) {
			
			moonTextureHandle = TextureHelper.loadTexture(context,
					R.drawable.moon_waxing_quarter);
			
		} else if (elongation > 105 && elongation <= 165) {
			
			moonTextureHandle = TextureHelper.loadTexture(context,
					R.drawable.moon_waxing_gibbous);
			
		} else if (elongation > 165 && elongation <= 195) {
			
			moonTextureHandle = TextureHelper.loadTexture(context,
					R.drawable.moon_full);
			
		} else if (elongation > 195 && elongation <= 255) {
			
			moonTextureHandle = TextureHelper.loadTexture(context,
					R.drawable.moon_waning_gibbous);
			
		} else if (elongation > 255 && elongation <= 285) {
			
			moonTextureHandle = TextureHelper.loadTexture(context,
					R.drawable.moon_waning_quarter);
			
		} else if (elongation > 285 && elongation <= 360) {
			
			moonTextureHandle = TextureHelper.loadTexture(context,
					R.drawable.moon_new);
			
		}
		
		
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, moonTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, moonTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		
	}

	private void buildMercuryTexture(float FV) {
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		if (FV > 0 && FV <= 30) {
			
			mercuryTextureHandle = TextureHelper.loadTexture(context,
					R.drawable.moon_new);
			
		} else if (FV > 30 && FV <= 75) {
			
			mercuryTextureHandle = TextureHelper.loadTexture(context,
					R.drawable.mercury_near_empty);
			
		} else if (FV > 75 && FV <= 95) {
			
			mercuryTextureHandle = TextureHelper.loadTexture(context,
					R.drawable.mercury_half);
			
		} else if (FV > 105 && FV <= 145) {
			
			mercuryTextureHandle = TextureHelper.loadTexture(context,
					R.drawable.mercury_near_full);
			
		} else {
			
			mercuryTextureHandle = TextureHelper.loadTexture(context,
					R.drawable.mercury);
			
			
		}
		
		
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mercuryTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mercuryTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		
	}
	
	private void buildVenusTexture(float FV) {
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		if (FV > 0 && FV <= 30) {
			
			venusTextureHandle = TextureHelper.loadTexture(context,
					R.drawable.moon_new);
			
		} else if (FV > 30 && FV <= 75) {
			
			venusTextureHandle = TextureHelper.loadTexture(context,
					R.drawable.venus_near_empty);
			
		} else if (FV > 75 && FV <= 95) {
			
			venusTextureHandle = TextureHelper.loadTexture(context,
					R.drawable.venus_half);
			
		} else if (FV > 95 && FV <= 125) {
			
			venusTextureHandle = TextureHelper.loadTexture(context,
					R.drawable.venus_above_half);
			
		} else if (FV > 125 && FV <= 155) {
			
			venusTextureHandle = TextureHelper.loadTexture(context,
					R.drawable.venus_near_full);
			
		} else {
			
			venusTextureHandle = TextureHelper.loadTexture(context,
					R.drawable.venus_full);
			
		}
		
		
		
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, venusTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, venusTextureHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		
	}
	
	public class Star {
		
		private int star_id;
		private float rightAscension;
		private float declination;
		private CelestialColor c;
		private float brightness;
		private float azimuth;
		private float altitude;
		private float X;
		private float Y;
		private float Z;
		private String bayerName;
		private String properName;
		private float distance;
		
		public Star(int star_id, float rightAscension, float declination, float azimuth, float altitude, CelestialColor c, float brightness,
					String bayerName, String properName, float distance) {
			
			this.star_id = star_id;
			this.rightAscension = rightAscension;
			this.declination = declination;
			this.azimuth = azimuth;
			this.altitude = altitude;
			this.c = c;
			this.brightness = brightness;
			this.bayerName = bayerName;
			this.properName = properName;
			this.distance = distance;
			
			calcXYZ();
			
		}
		
		public float getRightAscension() {
			return rightAscension;
		}
		
		public float getDistance() {
			return distance;
		}
		
		public float getDeclination() {
			return declination;
		}
		
		public float getAzimuth() {
			return azimuth;
		}
		
		public float getAltitude() {
			return altitude;
		}
		
		public CelestialColor getCelestialColor() {
			return c;
		}
		
		public float getBrightness() {
			return brightness;
		}

		public void computeHorizonCoordinates(double lat, double lon) {

			double RA = rightAscension;
			double DEC = declination;

			double  ha, sin_alt, cos_az, alt, az, a, cos_a;

			ha = this.mean_sidereal_time(lon) - RA;
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
			calcXYZ();


		}
		
		
		private double mean_sidereal_time(double lon) {

			
			Calendar UTCTime = ((OpenGLES20Activity) context).getUTCTime();
			
			int year = UTCTime.get(Calendar.YEAR);
			int month = UTCTime.get(Calendar.MONTH) + 1;
			int day = UTCTime.get(Calendar.DAY_OF_MONTH);
			int hour = UTCTime.get(Calendar.HOUR_OF_DAY);
			int minute = UTCTime.get(Calendar.MINUTE);
			int second = UTCTime.get(Calendar.SECOND);
		
			
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
		public void calcXYZ() {
			
			X = (float) (-Math.sin(Math.toRadians(azimuth)) * Math.cos(Math.toRadians(altitude)));
			Y = (float) (Math.sin(Math.toRadians(altitude)));
			Z = (float) (Math.cos(Math.toRadians(azimuth)) * Math.cos(Math.toRadians(altitude)));
			
			
		}
		
		public float distanceToStar(float[] line) {
			
			
			float[] star = {-X, -Y, -Z};
			float[] origin = {0, 0 ,0};
			
			float numerator = Vector.length(Vector.crossProduct(line, Vector.minus(origin, star)));
			float denominator = line.length;
			
			return numerator/denominator; 

			
		}
		
		public float getX() {
			return X;
		}
		
		public float getY() {
			return Y;
		}
		
		public float getZ() {
			return Z;
		}
		
		public String getBayerName() {
			return bayerName;
		}
		
		public String getProperName() {
			return properName;
		}
		
		public int getID() {
			return star_id;
		}
		
	}
	
	private class Circle {

		private final int[] vbo = new int[1];
		private final int[] ibo = new int[1];
		private FloatBuffer circleVertexDataBuffer;
		private ShortBuffer circleIndexDataBuffer;

		private float[] circleVertexData;
		private short[] circleIndexData;
		private double radius;
		private int indexCount;
		private float x_coord;
		private float y_coord;
		private float z_coord;
		private float rightAscension;
		private float declination;
		private float azimuth;
		private float altitude;
		private float fidelity;
		private String name;
		private float mag;
		private float distance;
		private float phase;

		private Circle(double radius, float azimuth, float altitude, float rightAscension,
					float declination, float distance, float fidelity, String name, float mag, float phase) {

			try {

				this.radius = radius;
				this.rightAscension = rightAscension;
				this.declination = declination;
				this.azimuth = azimuth;
				this.altitude = altitude;
				this.fidelity = fidelity;
				this.name = name;
				this.mag = mag;
				this.distance = distance;
				this.phase = phase;
				
				build( azimuth, altitude, distance);
				prepareBuffers();

			} catch (Throwable t) {

				Log.w(TAG, t);
				errorHandler.handleError(ErrorType.BUFFER_CREATION_ERROR,
						t.getLocalizedMessage());

			}
		}

		public void build(float azimuth, float altitude, float distance) {

			circleVertexData = new float[(int) (360*12*2)];
			circleIndexData = new short[(int) (360*6*2)];

			this.azimuth = azimuth;
			this.altitude = altitude;
			
		//	Log.e(TAG, "right ascension is")
			
			Tuple az_alt_equator = StarRenderer.this.computeHorizonCoordinates(this.rightAscension, 0,
					currentLocation.getLatitude(), currentLocation.getLongitude());
			float y_equ = (float) (Math.sin(az_alt_equator.y) * distance);

			int offset = 0;
			int counter = 0;

			double sin_az = Math.sin(Math.toRadians(azimuth));
			double cos_az = Math.cos(Math.toRadians(azimuth));
			double sin_alt = Math.sin(Math.toRadians(altitude));
			double cos_alt = Math.cos(Math.toRadians(altitude));

			float[] centre = {(float) (-distance * sin_az * cos_alt),
					(float) (distance * sin_alt), (float) (distance * cos_az * cos_alt)};

			//Log.e(TAG, "azimuth: " + azimuth + ", altitude: " + altitude);
			//Log.e(TAG, "centre: (" + centre[0] + ", " + centre[1] + ", " + centre[2] + ")");

			
			
			double x_dir = -sin_alt * cos_az;
			double y_dir = cos_alt;
			double z_dir = -sin_alt * sin_az;

			double sum_squared = Math.sqrt(x_dir * x_dir + y_dir * y_dir
					+ z_dir * z_dir);

			x_dir = x_dir / sum_squared;
			y_dir = y_dir / sum_squared;
			z_dir = z_dir / sum_squared;

			float[] A = {(float) x_dir, (float) y_dir, (float) z_dir};

			double normal_sum_squared = Vector.length(centre);

			float[] normal = {(float) (-centre[0] / normal_sum_squared),
					(float) (-centre[1] / normal_sum_squared), (float) (-centre[2]
							/ normal_sum_squared)};

			x_coord = (float) (centre[0]/ normal_sum_squared);
			y_coord = (float) (centre[1] / normal_sum_squared);
			z_coord = (float) (centre[2] / normal_sum_squared);
			
			//this.x_coord =
			
			//Log.e(TAG, "centre is ( " + centre[0] + ", " + centre[1] + ", " + centre[2] + ")");


			float[] B = Vector.crossProduct(A, normal);
			
			//float test = (float) Math.toDegrees(Math.atan(A[1]/up_vec[1]));
			
			
			Location l = currentLocation;
			//Log.e(TAG, "test is: " + test);
			float max = 0;
			float max_theta = 0;
			float max_x = 0;
			float max_y = 0;
			float max_z = 0;
			float mx = -10000;
		//	float mt = 0;
			float md = 10000000;
			float max_diff = 0;
			float[] up = new float[]{up_vec[0]*100000, up_vec[1]*100000, up_vec[2]*100000};
			
			float mt =0;			
			// First, build the data for the vertex buffer
			for (double theta = 0; theta <= 360; theta += fidelity) {

				float sin_theta = (float) Math.sin(Math.toRadians(theta));
				float cos_theta = (float) Math.cos(Math.toRadians(theta));

				float x = (float) ((centre[0]) + (radius * cos_theta * A[0] + radius
						* sin_theta * B[0]));
				float y = (float) ((centre[1]) + (radius * cos_theta * A[1] + radius
						* sin_theta * B[1]));
				float z = (float) ((centre[2]) + (radius * cos_theta * A[2] + radius
						* sin_theta * B[2]));
				
				/*	float[] point = {(float) x, (float) y, (float) z};
				float[] origin = {0, 0 ,0};
				
				float numerator = Vector.length(Vector.crossProduct(planet_sun_line, Vector.minus(origin, planet)));
				float denominator = planet_sun_line.length;
				
				return numerator/denominator;  */
				
				float dx = (float) (sun.getX()*sun.getRVEC() - x);
				float dy = (float) (sun.getY()*sun.getRVEC() - y);
				float dz = (float) (sun.getZ()*sun.getRVEC() - z);
				
				
				
				float d = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
			
				
				
				if (d < md) {
					
					md = d;
					mt = (float) theta;
					
				} 
				


			}
			

			
			// First, build the data for the vertex buffer
			
			///mt = moon.getElongation();
			float vec_x =  (float) ((sun.getX() * sun.getRVEC()) - centre[0]);
			float vec_y =  (float) ((sun.getY() * sun.getRVEC()) - centre[1]);
			float vec_z =  (float) ((sun.getZ() * sun.getRVEC()) - centre[2]);

			float[] planet_sun_line = new float[]{vec_x, vec_y, vec_z};
			
			
			
		//	Vector planet_sun_line = new Vector(vec_x, vec_x, vec_x);
			//Vector.X = (float) ((sun.getX() * sun.getRVEC()) - centre[0]);
			
			Log.e(TAG, "angle is " + mt);
			
		//	if (y )
			
						for (double theta = 0; theta <= 360; theta += fidelity) {

							float sin_theta = (float) Math.sin(Math.toRadians(theta + mt));
							float cos_theta = (float) Math.cos(Math.toRadians(theta + mt));

							float x = (float) ((centre[0]) + (radius * cos_theta * A[0] + radius
									* sin_theta * B[0]));
							float y = (float) ((centre[1]) + (radius * cos_theta * A[1] + radius
									* sin_theta * B[1]));
							float z = (float) ((centre[2]) + (radius * cos_theta * A[2] + radius
									* sin_theta * B[2]));

							float tx = (float) ((Math.cos(Math.toRadians(theta)) * 0.5) + 0.5);
							float ty = (float) ((Math.sin(Math.toRadians(theta)) * 0.5) + 0.5);
							
							

							circleVertexData[offset++] = x;
							circleVertexData[offset++] = y;
							circleVertexData[offset++] = z;

							final float[] normalVector = { x, y, z };

							// Normalize the normal
							final float length = Matrix.length(normalVector[0],
									normalVector[1], normalVector[2]);

							// Normal Data
							circleVertexData[offset++] = normalVector[0] / length;
							circleVertexData[offset++] = normalVector[1] / length;
							circleVertexData[offset++] = normalVector[2] / length;

							// Color Data
							circleVertexData[offset++] = 1.0f;
							circleVertexData[offset++] = 1.0f;
							circleVertexData[offset++] = 1.0f;
							circleVertexData[offset++] = 1.0f;

							// Add texture data
							circleVertexData[offset++] = tx;
							circleVertexData[offset++] = ty;

							counter++;

						}
			
			
			
			offset = 0;

			for (int n = 0; n < counter; n++) {

				circleIndexData[offset++] = (short) (0);
				circleIndexData[offset++] = (short) ((n + 1) % counter);
				circleIndexData[offset++] = (short) ((n + 2) % counter);
				circleIndexData[offset++] = (short) (0);
				circleIndexData[offset++] = (short) ((n + 3) % counter);
				circleIndexData[offset++] = (short) ((n + 4) % counter);

			}

			indexCount = circleIndexData.length;

		}

		public void prepareBuffers() {

			
			
			GLES20.glUseProgram(program);
			
			circleVertexDataBuffer = ByteBuffer
					.allocateDirect(circleVertexData.length * BYTES_PER_FLOAT)
					.order(ByteOrder.nativeOrder()).asFloatBuffer();
			circleVertexDataBuffer.put(circleVertexData).position(0);

			circleIndexDataBuffer = ByteBuffer
					.allocateDirect(circleIndexData.length * BYTES_PER_SHORT)
					.order(ByteOrder.nativeOrder()).asShortBuffer();
			circleIndexDataBuffer.put(circleIndexData).position(0);

			GLES20.glGenBuffers(1, vbo, 0);
			GLES20.glGenBuffers(1, ibo, 0);

			if (vbo[0] > 0 && ibo[0] > 0) {

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
				GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
						circleVertexDataBuffer.capacity() * BYTES_PER_FLOAT,
						circleVertexDataBuffer, GLES20.GL_STREAM_DRAW);

				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
				GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,
						circleIndexDataBuffer.capacity() * BYTES_PER_SHORT,
						circleIndexDataBuffer, GLES20.GL_STREAM_DRAW);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

			} else {

				errorHandler.handleError(ErrorType.BUFFER_CREATION_ERROR,
						"glGenBuffers");
			}

		}

		public void rebuildBuffers() {
			GLES20.glUseProgram(program);
			FloatBuffer newCircleVertexDataBuffer = ByteBuffer
					.allocateDirect(circleVertexData.length * BYTES_PER_FLOAT)
					.order(ByteOrder.nativeOrder()).asFloatBuffer();
			newCircleVertexDataBuffer.put(circleVertexData).rewind();

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
					newCircleVertexDataBuffer.capacity() * BYTES_PER_FLOAT,
					newCircleVertexDataBuffer, GLES20.GL_STREAM_DRAW);

			ShortBuffer newCircleIndexDataBuffer = ByteBuffer
					.allocateDirect(circleIndexData.length * BYTES_PER_FLOAT)
					.order(ByteOrder.nativeOrder()).asShortBuffer();
			newCircleIndexDataBuffer.put(circleIndexData).rewind();

			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
			GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,
					newCircleIndexDataBuffer.capacity() * BYTES_PER_SHORT,
					newCircleIndexDataBuffer, GLES20.GL_STREAM_DRAW);

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

		}

		void render() {

			GLES20.glUseProgram(program);
			
			if (vbo[0] > 0 && ibo[0] > 0) {
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);

				// Bind Attributes
				GLES20.glVertexAttribPointer(positionAttribute,
						POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
						STRIDE, 0);
				GLES20.glEnableVertexAttribArray(positionAttribute);

				GLES20.glVertexAttribPointer(normalAttribute,
						NORMAL_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
						STRIDE, POSITION_DATA_SIZE_IN_ELEMENTS
								* BYTES_PER_FLOAT);
				GLES20.glEnableVertexAttribArray(normalAttribute);

				GLES20.glVertexAttribPointer(
						colorAttribute,
						COLOR_DATA_SIZE_IN_ELEMENTS,
						GLES20.GL_FLOAT,
						false,
						STRIDE,
						(POSITION_DATA_SIZE_IN_ELEMENTS + NORMAL_DATA_SIZE_IN_ELEMENTS)
								* BYTES_PER_FLOAT);
				GLES20.glEnableVertexAttribArray(colorAttribute);

				GLES20.glVertexAttribPointer(
						textureCoordinateAttribute,
						TEXTURE_DATA_SIZE_IN_ELEMENTS,
						GLES20.GL_FLOAT,
						false,
						STRIDE,
						(POSITION_DATA_SIZE_IN_ELEMENTS
								+ NORMAL_DATA_SIZE_IN_ELEMENTS + COLOR_DATA_SIZE_IN_ELEMENTS)
								* BYTES_PER_FLOAT);
				GLES20.glEnableVertexAttribArray(textureCoordinateAttribute);

				// Draw
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
				GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, indexCount,
						GLES20.GL_UNSIGNED_SHORT, 0);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
			}
		}

		void release() {
			if (vbo[0] > 0) {
				GLES20.glDeleteBuffers(vbo.length, vbo, 0);
				vbo[0] = 0;
			}

			if (ibo[0] > 0) {
				GLES20.glDeleteBuffers(ibo.length, ibo, 0);
				ibo[0] = 0;
			}
		}

		public float distanceToCircle(float[] line) {
			
			
			float[] planet = {(float) x_coord, (float) y_coord, (float) z_coord};
			float[] origin = {0, 0 ,0};
			
			float numerator = Vector.length(Vector.crossProduct(line, Vector.minus(origin, planet)));
			float denominator = line.length;
			
			return numerator/denominator; 

			
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
		
		public float getAlt() {
			return altitude;
		}
		
		public String getName() {
			return name;
		}
		
		public float getMagnitude() {
			return mag;
		}
		
		public float getDistance() {
			return distance;
		}
		
		public float getPhase() {
			return phase;
		}
		
		public float getRadius() {
			return (float) radius;
		}
		
		public float getX() {
			return x_coord;
		}
		
		public float getY() {
			return y_coord;
		}
		
		public float getZ() {
			return z_coord;
		}
		
		public void computeHorizonCoordinates(double lat, double lon) {

			double ha, sin_alt, cos_az, alt, az;

			double RA = rightAscension;
			double DEC = declination;

			ha = mean_sidereal_time(lon) - RA;
			if (ha < 0) {
				ha = ha + 360;
			}

			ha = ha * RADS;
			DEC = DEC * RADS;
			lat = lat * RADS;

			sin_alt = Math.sin(DEC) * Math.sin(lat) + Math.cos(DEC)
					* Math.cos(lat) * Math.cos(ha);
			alt = Math.asin(sin_alt);

			cos_az = (Math.sin(DEC) - Math.sin(alt) * Math.sin(lat))
					/ (Math.cos(alt) * Math.cos(lat));
			az = Math.acos(cos_az);

			alt = alt * DEGS;
			az = az * DEGS;

			if (Math.sin(ha) > 0) {
				az = 360 - az;
			}

			this.altitude = (float) alt;
			this.azimuth = (float) az;
			
			

		}

	}

	private class Equatorial {

		private final int[] vbo = new int[1];
		private final int[] ibo = new int[1];
		private FloatBuffer lineVertexDataBuffer;
		private ShortBuffer lineIndexDataBuffer;
		private float[] lineVertexData;
		private short[] lineIndexData;
		private int indexCount;

		private Equatorial() {

			try {

				build();
				prepareBuffers();

			} catch (Throwable t) {

				Log.w(TAG, t);
				errorHandler.handleError(ErrorType.BUFFER_CREATION_ERROR,
						t.getLocalizedMessage());

			}
		}

		public void build() {
	
			int vertexCount = 720 + (12*360) + (24*180) + 360;

			lineVertexData = new float[vertexCount * 12];
			lineIndexData = new short[vertexCount];

			int offset = 0;
			int counter = 0;

			OpenGLES20Activity a = (OpenGLES20Activity) context;
			Location l = a.getRenderer().currentLocation;
			
			for (int i = 0; i < 2 ; i++) {
				for (int ra = 0; ra < 360; ra++) {
					
					double dec = 23.5;
					
					if (i == 1) {
						dec = -23.5;
					}
					
					
					Tuple azAlt = computeHorizonCoordinates(ra, dec, l.getLatitude(), l.getLongitude());
					
					float azimuth = azAlt.x;
					float altitude = azAlt.y;
					
					double sin_az = Math.sin(Math.toRadians(azimuth));
					double cos_az = Math.cos(Math.toRadians(azimuth));
					double sin_alt = Math.sin(Math.toRadians(altitude));
					double cos_alt = Math.cos(Math.toRadians(altitude));
					
					float distance = 400;
					
					float x = (float) (-distance * sin_az * cos_alt);
					float y = (float) (distance * sin_alt);
					float z = (float) (distance * cos_az * cos_alt);
					
					lineVertexData[offset++] = x;
					lineVertexData[offset++] = y;
					lineVertexData[offset++] = z;
				
	
					final float[] normalVector = { x, y, z };
	
					// Normalize the normal
					final float length = Matrix.length(normalVector[0],
							normalVector[1], normalVector[2]);
	
					// Normal Data
					lineVertexData[offset++] = normalVector[0] / length;
					lineVertexData[offset++] = normalVector[1] / length;
					lineVertexData[offset++] = normalVector[2] / length;
	

						
					// Color Data
					lineVertexData[offset++] = 0.835294f;
					lineVertexData[offset++] = 0.73725f;
					lineVertexData[offset++] = 0.1647f;
					lineVertexData[offset++] = 1.0f;
						

	
					// Add texture data
					lineVertexData[offset++] = (float) 0.5;
					lineVertexData[offset++] = (float) 0.5;
	
					counter++;
					
				}
			}
			
			
			for (int dec = -90; dec < 90; dec += 15) {
			
				for (int ra = 0; ra < 360; ra++) {
				
						Tuple azAlt = computeHorizonCoordinates(ra, dec, l.getLatitude(), l.getLongitude());
						
						float azimuth = azAlt.x;
						float altitude = azAlt.y;
						
						double sin_az = Math.sin(Math.toRadians(azimuth));
						double cos_az = Math.cos(Math.toRadians(azimuth));
						double sin_alt = Math.sin(Math.toRadians(altitude));
						double cos_alt = Math.cos(Math.toRadians(altitude));
						
						float distance = 400;
						
						float x = (float) (-distance * sin_az * cos_alt);
						float y = (float) (distance * sin_alt);
						float z = (float) (distance * cos_az * cos_alt);
						
						lineVertexData[offset++] = x;
						lineVertexData[offset++] = y;
						lineVertexData[offset++] = z;
					
		
						final float[] normalVector = { x, y, z };
		
						// Normalize the normal
						final float length = Matrix.length(normalVector[0],
								normalVector[1], normalVector[2]);
		
						// Normal Data
						lineVertexData[offset++] = normalVector[0] / length;
						lineVertexData[offset++] = normalVector[1] / length;
						lineVertexData[offset++] = normalVector[2] / length;
		
						if (dec == 0) {
						
							// Color Data
							lineVertexData[offset++] = 0.93725f;
							lineVertexData[offset++] = 0.9529f;
							lineVertexData[offset++] = 0.0470588f;
							lineVertexData[offset++] = 1.0f;
							
						} else {
							
							
							// Color Data
							lineVertexData[offset++] = 0.415686f;
							lineVertexData[offset++] = 0.447f;
							lineVertexData[offset++] = 0.5843137f;
							lineVertexData[offset++] = 1.0f;
							
						}
							

						
		
						// Add texture data
						lineVertexData[offset++] = (float) 0.5;
						lineVertexData[offset++] = (float) 0.5;
		
						counter++;
						
					}
				} 
			
				
				for (int ra = 0; ra < 360; ra += 15) {
					
					for (int dec = -90; dec < 90; dec++) {
					
						
						Tuple azAlt = computeHorizonCoordinates(ra, dec, l.getLatitude(), l.getLongitude());
						
						float azimuth = azAlt.x;
						float altitude = azAlt.y;
						
						double sin_az = Math.sin(Math.toRadians(azimuth));
						double cos_az = Math.cos(Math.toRadians(azimuth));
						double sin_alt = Math.sin(Math.toRadians(altitude));
						double cos_alt = Math.cos(Math.toRadians(altitude));
						
						float distance = 400;
						
						float x = (float) (-distance * sin_az * cos_alt);
						float y = (float) (distance * sin_alt);
						float z = (float) (distance * cos_az * cos_alt);
						
						lineVertexData[offset++] = x;
						lineVertexData[offset++] = y;
						lineVertexData[offset++] = z;
					

						final float[] normalVector = { x, y, z };

						// Normalize the normal
						final float length = Matrix.length(normalVector[0],
								normalVector[1], normalVector[2]);

						// Normal Data
						lineVertexData[offset++] = normalVector[0] / length;
						lineVertexData[offset++] = normalVector[1] / length;
						lineVertexData[offset++] = normalVector[2] / length;
						
						if (dec == 0) {
						
							// Color Data
							lineVertexData[offset++] = 0.93725f;
							lineVertexData[offset++] = 0.9529f;
							lineVertexData[offset++] = 0.0470588f;
							lineVertexData[offset++] = 1.0f;
							
						
						} else {
							
							// Color Data
							lineVertexData[offset++] = 0.415686f;
							lineVertexData[offset++] = 0.447f;
							lineVertexData[offset++] = 0.5843137f;
							lineVertexData[offset++] = 1.0f;
							
							
						}



						// Add texture data
						lineVertexData[offset++] = (float) 0.5;
						lineVertexData[offset++] = (float) 0.5;

						counter++;
						
						}

						
					} 
				
				
			for (float az = 0; az < 360; az++) {
				
				double sin_az = Math.sin(Math.toRadians(az));
				double cos_az = Math.cos(Math.toRadians(az));
				double sin_alt = Math.sin(Math.toRadians(0));
				double cos_alt = Math.cos(Math.toRadians(0));
				
				float distance = 400;
				
				float x = (float) (-distance * sin_az * cos_alt);
				float y = (float) (distance * sin_alt);
				float z = (float) (distance * cos_az * cos_alt);
				
				lineVertexData[offset++] = x;
				lineVertexData[offset++] = y;
				lineVertexData[offset++] = z;
			

				final float[] normalVector = { x, y, z };

				// Normalize the normal
				final float length = Matrix.length(normalVector[0],
						normalVector[1], normalVector[2]);

				// Normal Data
				lineVertexData[offset++] = normalVector[0] / length;
				lineVertexData[offset++] = normalVector[1] / length;
				lineVertexData[offset++] = normalVector[2] / length;
				
				
				// Color Data
				lineVertexData[offset++] = 1.0f;
				lineVertexData[offset++] = 1.0f;
					lineVertexData[offset++] = 1.0f;
					lineVertexData[offset++] = 1.0f;

					
					// Add texture data
					lineVertexData[offset++] = (float) 0.5;
					lineVertexData[offset++] = (float) 0.5;

					counter++;
					
				}




			offset = 0;
			
			for (int n = 0; n < counter; n++) {

				lineIndexData[offset++] = (short) (n);

			}

			indexCount = lineIndexData.length;
			

		}

		public void prepareBuffers() {
			
			GLES20.glUseProgram(program);

			lineVertexDataBuffer = ByteBuffer
					.allocateDirect(lineVertexData.length * BYTES_PER_FLOAT)
					.order(ByteOrder.nativeOrder()).asFloatBuffer();
			lineVertexDataBuffer.put(lineVertexData).position(0);

			lineIndexDataBuffer = ByteBuffer
					.allocateDirect(lineIndexData.length * BYTES_PER_SHORT)
					.order(ByteOrder.nativeOrder()).asShortBuffer();
			lineIndexDataBuffer.put(lineIndexData).position(0);

			GLES20.glGenBuffers(1, vbo, 0);
			GLES20.glGenBuffers(1, ibo, 0);

			if (vbo[0] > 0 && ibo[0] > 0) {

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
				GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
						lineVertexDataBuffer.capacity() * BYTES_PER_FLOAT,
						lineVertexDataBuffer, GLES20.GL_STREAM_DRAW);

				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
				GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,
						lineIndexDataBuffer.capacity() * BYTES_PER_SHORT,
						lineIndexDataBuffer, GLES20.GL_STREAM_DRAW);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

			} else {

				errorHandler.handleError(ErrorType.BUFFER_CREATION_ERROR,
						"glGenBuffers");
			}

		}

	/*	public void rebuildBuffers() {

			FloatBuffer newCircleVertexDataBuffer = ByteBuffer
					.allocateDirect(lineVertexData.length * BYTES_PER_FLOAT)
					.order(ByteOrder.nativeOrder()).asFloatBuffer();
			newCircleVertexDataBuffer.put(lineVertexData).rewind();

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
					newCircleVertexDataBuffer.capacity() * BYTES_PER_FLOAT,
					newCircleVertexDataBuffer, GLES20.GL_STREAM_DRAW);

			ShortBuffer newCircleIndexDataBuffer = ByteBuffer
					.allocateDirect(lineIndexData.length * BYTES_PER_FLOAT)
					.order(ByteOrder.nativeOrder()).asShortBuffer();
			newCircleIndexDataBuffer.put(lineIndexData).rewind();

			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
			GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,
					newCircleIndexDataBuffer.capacity() * BYTES_PER_SHORT,
					newCircleIndexDataBuffer, GLES20.GL_STREAM_DRAW);

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

		} */

		void render() {
			GLES20.glUseProgram(program);
			
			if (vbo[0] > 0 && ibo[0] > 0) {
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);

				// Bind Attributes
				GLES20.glVertexAttribPointer(positionAttribute,
						POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
						STRIDE, 0);
				GLES20.glEnableVertexAttribArray(positionAttribute);

				GLES20.glVertexAttribPointer(normalAttribute,
						NORMAL_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
						STRIDE, POSITION_DATA_SIZE_IN_ELEMENTS
								* BYTES_PER_FLOAT);
				GLES20.glEnableVertexAttribArray(normalAttribute);

				GLES20.glVertexAttribPointer(
						colorAttribute,
						COLOR_DATA_SIZE_IN_ELEMENTS,
						GLES20.GL_FLOAT,
						false,
						STRIDE,
						(POSITION_DATA_SIZE_IN_ELEMENTS + NORMAL_DATA_SIZE_IN_ELEMENTS)
								* BYTES_PER_FLOAT);
				GLES20.glEnableVertexAttribArray(colorAttribute);

				GLES20.glVertexAttribPointer(
						textureCoordinateAttribute,
						TEXTURE_DATA_SIZE_IN_ELEMENTS,
						GLES20.GL_FLOAT,
						false,
						STRIDE,
						(POSITION_DATA_SIZE_IN_ELEMENTS
								+ NORMAL_DATA_SIZE_IN_ELEMENTS + COLOR_DATA_SIZE_IN_ELEMENTS)
								* BYTES_PER_FLOAT);
				GLES20.glEnableVertexAttribArray(textureCoordinateAttribute);

				// Draw
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
				GLES20.glDrawElements(GLES20.GL_LINES, indexCount,
						GLES20.GL_UNSIGNED_SHORT, 0);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
				
				//Log.e(TAG, GLES20.glGetError() + "");
			}
		}

	/*	void release() {
			if (vbo[0] > 0) {
				GLES20.glDeleteBuffers(vbo.length, vbo, 0);
				vbo[0] = 0;
			}

			if (ibo[0] > 0) {
				GLES20.glDeleteBuffers(ibo.length, ibo, 0);
				ibo[0] = 0;
			}
		} */

	}
	
	public Tuple computeHorizonCoordinates(double RA, double DEC,
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

		return new Tuple((float) az, (float) alt);
		
	}
	
	public class Tuple { 
		  public final float x; 
		  public final float y; 
		  public Tuple(float x, float y) { 
		    this.x = x; 
		    this.y = y; 
		  } 
		} 
	
	private class CelestialArray {

		private final int[] vbo = new int[1];
		private final int[] ibo = new int[1];
		private FloatBuffer pointVertexDataBuffer;
		private ShortBuffer pointIndexDataBuffer;
		private float[] pointVertexData;
		private short[] pointIndexData;
		private Star[] stars;
		private float mercuryZoomThreshold;
		private float venusZoomThreshold;
		private float marsZoomThreshold;
		private float jupiterZoomThreshold;
		private float saturnZoomThreshold;
		private float moonZoomThreshold;
		private float sunZoomThreshold;

		private CelestialArray(Star[] stars) {

			try {

				build(stars);
				prepareBuffers();

			} catch (Throwable t) {

				Log.w(TAG, t);
				errorHandler.handleError(ErrorType.BUFFER_CREATION_ERROR,
						t.getLocalizedMessage());

			}
		}

		public void build(Star[] stars) {
			
			this.stars = stars;
			
			int offset = 0;
			pointVertexData = new float[(stars.length+7)*8];
			pointIndexData = new short[stars.length+7];
			
			for (Star s : stars) {
				
				float distance = 90;
				
				double sin_az = Math.sin(Math.toRadians(s.azimuth));
				double cos_az = Math.cos(Math.toRadians(s.azimuth));
				double sin_alt = Math.sin(Math.toRadians(s.altitude));
				double cos_alt = Math.cos(Math.toRadians(s.altitude));

				float[] centre = {(float) (-distance * sin_az * cos_alt),
						(float) (distance * sin_alt), (float) (distance * cos_az * cos_alt)};

				float x = (float) centre[0];
				float y = (float) centre[1];
				float z = (float) centre[2];
		
				pointVertexData[offset++] = x;
				pointVertexData[offset++] = y;
				pointVertexData[offset++] = z;
				
				float[] colour = getStarColour(s.getCelestialColor());
			
//				pointVertexData[offset++] = colour[0];
	//			pointVertexData[offset++] = colour[1];
		//		pointVertexData[offset++] = colour[2];
			//	pointVertexData[offset++] = colour[3];
				
				pointVertexData[offset++] = 1.0f;
				pointVertexData[offset++] = 1.0f;
				pointVertexData[offset++] = 1.0f;
				pointVertexData[offset++] = 1.0f;
				
				double brightnessDifference = (0.03 - s.getBrightness());
				float brightnessFactor = (float) (brightnessDifference*5);
				//Log.e(TAG, "brightness factor: " + brightnessFactor + " and index: " + i);
				
				//if (zoom < 0.9) {
				
					pointVertexData[offset++] = (float)(0.7*(brightnessFactor + 25));  
					
				//}
				

			

				
			}
			
			
			//if (zoom >= 0.005) {
				
				float distance = 3;
				
				Log.e(TAG, "currentZoom: " + zoom);
				
				for (int i = 0; i < 7; i++) {
					
					float azimuth = 0;
					float altitude = 0;
					float magnitude = 0;
					float brightness = 0;
					float zoomThreshold = 0;
					
					if (i == 0) {
						
						azimuth = (float) mercury.getAZ();
						altitude = (float) mercury.getALT();
						magnitude = mercury.getMagnitude();
						distance = (float) mercury.getRVEC() - 1;
						
						zoomThreshold = (float) (1/mercury.getRVEC());
						mercuryZoomThreshold = zoomThreshold;
						
						
					} else if (i == 1) {
						
						azimuth = (float) venus.getAZ();
						altitude = (float) venus.getALT();
						magnitude = venus.getMagnitude();
						distance = (float) venus.getRVEC() - 1;
						
						zoomThreshold = (float) (1/venus.getRVEC());
						venusZoomThreshold = zoomThreshold;
						

						
					} else if (i == 2) {
						
						azimuth = (float) mars.getAZ();
						altitude = (float) mars.getALT();
						magnitude = mars.getMagnitude();
						distance = (float) mars.getRVEC() - 1;
						
						zoomThreshold = (float) (1/mars.getRVEC());
						marsZoomThreshold = zoomThreshold;
						
						Log.e(TAG, "mars threshold: " + zoomThreshold + " and distance: " + mars.getRVEC());
						
					} else if (i == 3) {
						
						azimuth = (float) moon.getAZ();
						altitude = (float) moon.getALT();
						magnitude = (float) moon.getMagnitude();
						distance = (float) 10*moon.getRVEC();
						
						zoomThreshold = (float) (0.8);
						moonZoomThreshold = zoomThreshold;
						
						Log.e(TAG, "moon threshold: " + zoomThreshold + " and distance: " + moon.getRVEC());
						

						
						
					} else if (i == 4) {
						
						
						azimuth = (float) sun.getAZ();
						altitude = (float) sun.getALT();
						magnitude = (float) sun.getMagnitude();
						distance = (float) (sun.getRVEC() - 0.1);
						
						zoomThreshold = (float) (100/sun.getRVEC());
						sunZoomThreshold = zoomThreshold;
						
						//Log.e(TAG, "sun threshold: " + zoomThreshold + " and distance: " + sun.getRVEC());

					
						
						
					} else if (i == 5) {
						
						azimuth = (float) jupiter.getAZ();
						altitude = (float) jupiter.getALT();
						magnitude = jupiter.getMagnitude();
						distance = (float) jupiter.getRVEC() - 1;
						
						
						zoomThreshold = (float) (10/jupiter.getRVEC());
						jupiterZoomThreshold = zoomThreshold;
						
						Log.e(TAG, "jupiter threshold: " + zoomThreshold + " and distance: " + jupiter.getRVEC());


						
						
					} else if (i == 6) {
					
						azimuth = (float) saturn.getAZ();
						altitude = (float) saturn.getALT();
						magnitude = (float) saturn.getMagnitude();
						distance = (float) saturn.getRVEC() - 1;
						zoomThreshold = (float) (1.2/saturn.getRVEC());
						saturnZoomThreshold = zoomThreshold;
						
						
					
					}
					
					if (zoom < zoomThreshold) {
						
						brightness = 0;
					} else {
						double brightnessDifference = 0.03 - magnitude;
						
						float brightnessFactor = (float) (brightnessDifference*2);
						
						brightness = (float) (0.7*(30 + brightnessFactor));
						
					}
					
					
						
					double sin_az = Math.sin(Math.toRadians(azimuth));
					double cos_az = Math.cos(Math.toRadians(azimuth));
					double sin_alt = Math.sin(Math.toRadians(altitude));
					double cos_alt = Math.cos(Math.toRadians(altitude));
	
					float[] centre = {(float) (-distance * sin_az * cos_alt),
							(float) (distance * sin_alt), (float) (distance * cos_az * cos_alt)};
	
					float x = (float) centre[0];
					float y = (float) centre[1];
					float z = (float) centre[2];
			
					pointVertexData[offset++] = x;
					pointVertexData[offset++] = y;
					pointVertexData[offset++] = z;
					
					pointVertexData[offset++] = 1.0f;
					pointVertexData[offset++] = 1.0f;
					pointVertexData[offset++] = 1.0f;
					pointVertexData[offset++] = 1.0f;
					

					//Log.e(TAG, "brightness factor: " + brightnessFactor + " and index: " + i);
					pointVertexData[offset++] = brightness;
					
					
					
				}
				
				
				
		//	}
			
			
			for (int i = 0; i < stars.length + 7; i++) {
				
				pointIndexData[i] = (short) i;
				
			}
			
			

		}

		public void prepareBuffers() {

			GLES20.glUseProgram(programPoints);
			 
			pointVertexDataBuffer = ByteBuffer
					.allocateDirect(pointVertexData.length * BYTES_PER_FLOAT)
					.order(ByteOrder.nativeOrder()).asFloatBuffer();
			pointVertexDataBuffer.put(pointVertexData).position(0);
			
			pointIndexDataBuffer = ByteBuffer
					.allocateDirect(pointIndexData.length * BYTES_PER_SHORT)
					.order(ByteOrder.nativeOrder()).asShortBuffer();
			pointIndexDataBuffer.put(pointIndexData).position(0);

			GLES20.glGenBuffers(1, vbo, 0);
			GLES20.glGenBuffers(1, ibo, 0);

			if (vbo[0] > 0 && ibo[0] > 0) {

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
				GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
						pointVertexDataBuffer.capacity() * BYTES_PER_FLOAT,
						pointVertexDataBuffer, GLES20.GL_STREAM_DRAW);
				
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
				GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,
						pointIndexDataBuffer.capacity() * BYTES_PER_SHORT,
						pointIndexDataBuffer, GLES20.GL_STREAM_DRAW);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

			} else {

				errorHandler.handleError(ErrorType.BUFFER_CREATION_ERROR,
						"glGenBuffers");
			}

		}

		public void rebuildBuffers() {

			GLES20.glUseProgram(programPoints);
			
			FloatBuffer newPointVertexDataBuffer = ByteBuffer
					.allocateDirect(pointVertexData.length * BYTES_PER_FLOAT)
					.order(ByteOrder.nativeOrder()).asFloatBuffer();
			newPointVertexDataBuffer.put(pointVertexData).rewind();
			
			ShortBuffer newPointIndexDataBuffer = ByteBuffer
					.allocateDirect(pointIndexData.length * BYTES_PER_SHORT)
					.order(ByteOrder.nativeOrder()).asShortBuffer();
			newPointIndexDataBuffer.put(pointIndexData).position(0);

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
					newPointVertexDataBuffer.capacity() * BYTES_PER_FLOAT,
					newPointVertexDataBuffer, GLES20.GL_STREAM_DRAW);
			
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
			GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,
					newPointIndexDataBuffer.capacity() * BYTES_PER_SHORT,
					newPointIndexDataBuffer, GLES20.GL_STREAM_DRAW);
			
			//Log.e("TAG", "new buffer size: " + newPointVertexDataBuffer.capacity());

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0); 
			
			//verticesCount = newPointVertexDataBuffer.

		} 

		public void render() {

			if (vbo[0] > 0 && ibo[0] > 0) {
				
				GLES20.glUseProgram(programPoints);
				pointmvpMatrixUniform = GLES20.glGetUniformLocation(programPoints, MVP_MATRIX_UNIFORM);
				pointPositionAttribute = GLES20.glGetAttribLocation(programPoints, POINT_POSITION_ATTRIBUTE);
				pointColorAttribute = GLES20.glGetAttribLocation(programPoints, POINT_COLOR_ATTRIBUTE);
				pointSizeAttribute = GLES20.glGetAttribLocation(programPoints, POINT_SIZE_ATTRIBUTE);
				pointTextureUniform = GLES20.glGetUniformLocation(programPoints, POINT_TEXTURE_UNIFORM);
				pointZoomUniform = GLES20.glGetUniformLocation(programPoints, POINT_ZOOM_UNIFORM);
				
				GLES20.glUniform1f(pointZoomUniform, (float) Math.sqrt(zoom));

				GLES20.glUniformMatrix4fv(pointmvpMatrixUniform, 1, false, mvpMatrix, 0);
				
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
				
				GLES20.glEnableVertexAttribArray(pointPositionAttribute);
				GLES20.glVertexAttribPointer(pointPositionAttribute,
						POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
						POINT_STRIDE, 0);
				
				GLES20.glEnableVertexAttribArray(pointColorAttribute);
				GLES20.glVertexAttribPointer(pointColorAttribute,
						4, GLES20.GL_FLOAT, false,
						POINT_STRIDE, POSITION_DATA_SIZE_IN_ELEMENTS*BYTES_PER_FLOAT);
				
				GLES20.glEnableVertexAttribArray(pointSizeAttribute);
				GLES20.glVertexAttribPointer(pointSizeAttribute,
						1, GLES20.GL_FLOAT, false,
						POINT_STRIDE, (POSITION_DATA_SIZE_IN_ELEMENTS+4)*BYTES_PER_FLOAT);

				GLES20.glActiveTexture(GLES20.GL_TEXTURE11);
				
				GLES20.glUniform1i(pointTextureUniform, 11);
				
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);

				GLES20.glDrawElements(GLES20.GL_POINTS, pointIndexData.length - 14,
						GLES20.GL_UNSIGNED_SHORT, 0);
				
				GLES20.glUniform1f(pointZoomUniform, (float) ((float) 1/Math.sqrt(zoom)));
				
				if (zoom > mercuryZoomThreshold) {
					
					
					GLES20.glDrawElements(GLES20.GL_POINTS, 1,
							GLES20.GL_UNSIGNED_SHORT, (pointIndexData.length-7)*2);
				
				} 
				
				if (zoom > venusZoomThreshold) {
				
				//	GLES20.glUniform1f(pointZoomUniform, (float) ((float) 1/Math.sqrt(zoom)));
					GLES20.glDrawElements(GLES20.GL_POINTS, 1,
							GLES20.GL_UNSIGNED_SHORT, (pointIndexData.length-6)*2);
					
				}
				
				if (zoom > marsZoomThreshold) {
					
				//	GLES20.glUniform1f(pointZoomUniform, (float) ((float) 1/Math.sqrt(zoom)));
					GLES20.glDrawElements(GLES20.GL_POINTS, 1,
							GLES20.GL_UNSIGNED_SHORT, (pointIndexData.length-5)*2);
					
				}
				
				if (zoom > moonZoomThreshold) {
					
				//	GLES20.glUniform1f(pointZoomUniform, (float) ((float) 1/Math.sqrt(zoom)));
					GLES20.glDrawElements(GLES20.GL_POINTS, 1,
							GLES20.GL_UNSIGNED_SHORT, (pointIndexData.length-4)*2);
					
				}
				
				if (zoom > sunZoomThreshold) {
					
				//	GLES20.glUniform1f(pointZoomUniform, (float) ((float) 1/Math.sqrt(zoom)));
					GLES20.glDrawElements(GLES20.GL_POINTS, 1,
							GLES20.GL_UNSIGNED_SHORT, (pointIndexData.length-3)*2);
					
				}
				
				if (zoom > jupiterZoomThreshold) {
					
				//	GLES20.glUniform1f(pointZoomUniform, (float) ((float) 1/Math.sqrt(zoom)));
					GLES20.glDrawElements(GLES20.GL_POINTS, 1,
							GLES20.GL_UNSIGNED_SHORT, (pointIndexData.length-2)*2);
					
					//Log.e("TAG", "zoom is " + zoom + ", jupiter threshold: " + jupiterZoomThreshold);
					
				}
				
				if (zoom > saturnZoomThreshold) {
					
				//	GLES20.glUniform1f(pointZoomUniform, (float) ((float) 1/Math.sqrt(zoom)));
					GLES20.glDrawElements(GLES20.GL_POINTS, 1,
							GLES20.GL_UNSIGNED_SHORT, (pointIndexData.length-1)*2);
					
				}
					


		

			}
		}

		/*void release() {
			if (vbo[0] > 0) {
				GLES20.glDeleteBuffers(vbo.length, vbo, 0);
				vbo[0] = 0;
			}

		}*/

	}

	public class InfoRectangle {
		
		private final int[] vbo = new int[1];
		private final int[] ibo = new int[1];
		private FloatBuffer pointVertexDataBuffer;
		private ShortBuffer pointIndexDataBuffer;
		private float[] pointVertexData;
		private short[] pointIndexData;
		private float azimuth;
		private float altitude;
		private float size;
		private float distance;
		private int texture;
		public float x;
		public float y;
		public float z;
		
		public InfoRectangle(float azimuth, float altitude, float size, float distance, int texture) {
			
			try {
				
				this.azimuth = azimuth;
				this.altitude = altitude;
				this.size = size;
				this.distance = distance;
				this.texture = texture;
				
				build(azimuth, altitude, size, distance);
				prepareBuffers();

			} catch (Throwable t) {

				Log.w(TAG, t);
				errorHandler.handleError(ErrorType.BUFFER_CREATION_ERROR,
					t.getLocalizedMessage());

			}
			
		}
		
		public void build(float azimuth, float altitude, float size, float distance) {
			
		//	Log.e(TAG, "building rectangle with azimuth: " + azimuth + ", altitude: " + altitude );
			
			int offset = 0;
			pointVertexData = new float[8];
			pointIndexData = new short[1];
					
			double sin_az = Math.sin(Math.toRadians(azimuth));
			double cos_az = Math.cos(Math.toRadians(azimuth));
			double sin_alt = Math.sin(Math.toRadians(altitude));
			double cos_alt = Math.cos(Math.toRadians(altitude));
			
			//Log.e(TAG, "buklding info rectangle at az: " + azimuth + ", altitude: " + altitude);

			float[] centre = {(float) (-distance * sin_az * cos_alt),
						(float) (distance * sin_alt), (float) (distance * cos_az * cos_alt)};

			float x = (float) centre[0];
			float y = (float) centre[1];
			float z = (float) centre[2];
			
			this.x = x;
			this.y = y;
			this.z = z;
			
		//	Log.e(TAG, "X is: " + x + ", Y is: " + y + ", Z is: " + z);
		
			pointVertexData[offset++] = x;
			pointVertexData[offset++] = y;
			pointVertexData[offset++] = z;
				
			pointVertexData[offset++] = 1f;
			pointVertexData[offset++] = 1f;
			pointVertexData[offset++] = 1f;
			pointVertexData[offset++] = 1f;
				
			pointVertexData[offset++] = (float) (size);
			
			pointIndexData[0] = (short) 0;
				
		}
				
		public void prepareBuffers() {
			
			GLES20.glUseProgram(programRectangles);
	 
			pointVertexDataBuffer = ByteBuffer
					.allocateDirect(pointVertexData.length * BYTES_PER_FLOAT)
					.order(ByteOrder.nativeOrder()).asFloatBuffer();
			pointVertexDataBuffer.put(pointVertexData).position(0);
			
			pointIndexDataBuffer = ByteBuffer
					.allocateDirect(pointIndexData.length * BYTES_PER_SHORT)
					.order(ByteOrder.nativeOrder()).asShortBuffer();
			pointIndexDataBuffer.put(pointIndexData).position(0);

			GLES20.glGenBuffers(1, vbo, 0);
			GLES20.glGenBuffers(1, ibo, 0);

			if (vbo[0] > 0 && ibo[0] > 0) {

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
				GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
						pointVertexDataBuffer.capacity() * BYTES_PER_FLOAT,
						pointVertexDataBuffer, GLES20.GL_STREAM_DRAW);
				
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
				GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,
						pointIndexDataBuffer.capacity() * BYTES_PER_SHORT,
						pointIndexDataBuffer, GLES20.GL_STREAM_DRAW);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

			} else {

				errorHandler.handleError(ErrorType.BUFFER_CREATION_ERROR,
						"glGenBuffers");
			}

		}
			
		public void rebuildBuffers() {

			FloatBuffer newPointVertexDataBuffer = ByteBuffer
					.allocateDirect(pointVertexData.length * BYTES_PER_FLOAT)
					.order(ByteOrder.nativeOrder()).asFloatBuffer();
			newPointVertexDataBuffer.put(pointVertexData).rewind();

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
					newPointVertexDataBuffer.capacity() * BYTES_PER_FLOAT,
					newPointVertexDataBuffer, GLES20.GL_STREAM_DRAW);

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0); 

		} 	

		public void render() {

			if (vbo[0] > 0 && ibo[0] > 0) {
				
				GLES20.glUseProgram(programRectangles);
				rectanglemvpMatrixUniform = GLES20.glGetUniformLocation(programRectangles, MVP_MATRIX_UNIFORM);
				rectanglePositionAttribute = GLES20.glGetAttribLocation(programRectangles, POINT_POSITION_ATTRIBUTE);
				rectangleColorAttribute = GLES20.glGetAttribLocation(programRectangles, POINT_COLOR_ATTRIBUTE);
				rectangleSizeAttribute = GLES20.glGetAttribLocation(programRectangles, POINT_SIZE_ATTRIBUTE);
				rectangleTextureUniform = GLES20.glGetUniformLocation(programRectangles, POINT_TEXTURE_UNIFORM);

				GLES20.glUniformMatrix4fv(rectanglemvpMatrixUniform, 1, false, mvpMatrix, 0);
				
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
				
				GLES20.glEnableVertexAttribArray(rectanglePositionAttribute);
				GLES20.glVertexAttribPointer(rectanglePositionAttribute,
						POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
						POINT_STRIDE, 0);
				
				GLES20.glEnableVertexAttribArray(rectangleColorAttribute);
				GLES20.glVertexAttribPointer(rectangleColorAttribute,
						4, GLES20.GL_FLOAT, false,
						POINT_STRIDE, POSITION_DATA_SIZE_IN_ELEMENTS*BYTES_PER_FLOAT);
				
				
				GLES20.glEnableVertexAttribArray(rectangleSizeAttribute);
				GLES20.glVertexAttribPointer(rectangleSizeAttribute,
						1, GLES20.GL_FLOAT, false,
						POINT_STRIDE, (POSITION_DATA_SIZE_IN_ELEMENTS+4)*BYTES_PER_FLOAT);

				
				GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + texture);
				GLES20.glUniform1i(rectangleTextureUniform, texture);
				
				
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);

				
				GLES20.glDrawElements(GLES20.GL_POINTS, 1,
						GLES20.GL_UNSIGNED_SHORT, 0); 
				
				//Log.e(TAG, "error code: " + GLES20.glGetError());
				//Log.e(TAG, "max: " + GLES20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS);

			}
		
		
			
	}

		
	}
	
	public class Sphere {

		
		private FloatBuffer sphereVertexDataBuffer;
		private ShortBuffer sphereIndexDataBuffer;
		
		private float[] sphereVertexData;
		private short[] sphereIndexData;
		
		final int[] vbo = new int[1];
		final int[] ibo = new int[1];

		int indexCount;

		public Sphere(float radius, int x_coord, int y_coord, int z_coord) {
			try {
				final int floatsPerVertex = POSITION_DATA_SIZE_IN_ELEMENTS + NORMAL_DATA_SIZE_IN_ELEMENTS
						+ COLOR_DATA_SIZE_IN_ELEMENTS;
				
				
				build();
				prepareBuffers();
				
			} catch (Throwable t) {

				Log.w(TAG, t);
				errorHandler.handleError(ErrorType.BUFFER_CREATION_ERROR,
						t.getLocalizedMessage());
			}

		}
		
		public void build() {

				int offset = 0;

				double STEP = 1;
			    double dTheta = STEP * Math.PI / 180;
			    double dPhi = dTheta;
			    
			    double radius = 1;

			    
			    int points = 0;
			    
				sphereVertexData = new float[100000];
				sphereIndexData = new short[100000];

			    for(double phi = -(Math.PI/2); phi <= Math.PI/2; phi+=Math.PI/32)
			    {
			        for(double theta = 0.0; theta <= (Math.PI * 2); theta+=Math.PI/32)
			        {
			        	
			        	float x =  (float) ((radius * Math.sin(phi) * Math.cos(theta)));
			        	float y =  (float) ((radius * Math.sin(phi) * Math.sin(theta)));
			        	float z = (float) ((radius * Math.cos(phi)));
			        	
			            sphereVertexData[offset++] = x;
			            sphereVertexData[offset++] = y;
			            sphereVertexData[offset++] = z;
			            
			           // Log.e(TAG, "(" + x + ", " + y + ", " + z + ")");
			            
		               	final float[] normalVector = {1, 1, 1};
			            
		               	// Normalize the normal						
		               	final float length = Matrix.length(normalVector[0], normalVector[1], normalVector[2]);
		                sphereVertexData[offset++] = normalVector[0] / length;
		               	sphereVertexData[offset++] = normalVector[1] / length;
		               	sphereVertexData[offset++] = normalVector[2] / length;

		                // Add some fancy colors.
		               	sphereVertexData[offset++] = 1.0f;
		               	sphereVertexData[offset++] = 1.0f;
		               	sphereVertexData[offset++] = 1.0f;
		               	sphereVertexData[offset++] = 1.0f;
		               	
						float tx = (float) (Math.cos(x)* 0.5 + 0.5);
						float ty = (float) (Math.sin(y) *0.5 + 0.5);	
						
		               	// Add texture data
		               	sphereVertexData[offset++] = tx;
				        sphereVertexData[offset++] = ty;

			            points++;
			        }
			    }


				// Now build the index data
				final short[] sphereIndexData = new short[100000];

				offset = 0;
				
		        for (int i = 0; i < points; i++) {
		        


		                sphereIndexData[offset++] = (short) i; 
		                sphereIndexData[offset++] = (short) ((i +1)%points);
		                sphereIndexData[offset++] = (short) ((i +2)%points);


		            
		        }


				indexCount = points;
				
		}
		
		public void prepareBuffers() {
			sphereVertexDataBuffer = ByteBuffer
				.allocateDirect(sphereVertexData.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
			sphereVertexDataBuffer.put(sphereVertexData).position(0);

			sphereIndexDataBuffer = ByteBuffer
				.allocateDirect(sphereIndexData.length * BYTES_PER_SHORT)
				.order(ByteOrder.nativeOrder()).asShortBuffer();
			sphereIndexDataBuffer.put(sphereIndexData).position(0);

			GLES20.glGenBuffers(1, vbo, 0);
			GLES20.glGenBuffers(1, ibo, 0);

			if (vbo[0] > 0 && ibo[0] > 0) {

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
				GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
						sphereVertexDataBuffer.capacity() * BYTES_PER_FLOAT,
						sphereVertexDataBuffer, GLES20.GL_STREAM_DRAW);

				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
				GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,
						sphereIndexDataBuffer.capacity() * BYTES_PER_SHORT,
						sphereIndexDataBuffer, GLES20.GL_STREAM_DRAW);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

			} else {

				errorHandler.handleError(ErrorType.BUFFER_CREATION_ERROR,
						"glGenBuffers");
			}

		}

		void render() {

			if (vbo[0] > 0 && ibo[0] > 0) {
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);

				// Bind Attributes
				GLES20.glVertexAttribPointer(positionAttribute,
						POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
						STRIDE, 0);
				GLES20.glEnableVertexAttribArray(positionAttribute);

				GLES20.glVertexAttribPointer(normalAttribute,
						NORMAL_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
						STRIDE, POSITION_DATA_SIZE_IN_ELEMENTS
								* BYTES_PER_FLOAT);
				GLES20.glEnableVertexAttribArray(normalAttribute);

				GLES20.glVertexAttribPointer(
						colorAttribute,
						COLOR_DATA_SIZE_IN_ELEMENTS,
						GLES20.GL_FLOAT,
						false,
						STRIDE,
						(POSITION_DATA_SIZE_IN_ELEMENTS + NORMAL_DATA_SIZE_IN_ELEMENTS)
								* BYTES_PER_FLOAT);
				GLES20.glEnableVertexAttribArray(colorAttribute);

				GLES20.glVertexAttribPointer(
						textureCoordinateAttribute,
						TEXTURE_DATA_SIZE_IN_ELEMENTS,
						GLES20.GL_FLOAT,
						false,
						STRIDE,
						(POSITION_DATA_SIZE_IN_ELEMENTS
								+ NORMAL_DATA_SIZE_IN_ELEMENTS + COLOR_DATA_SIZE_IN_ELEMENTS)
								* BYTES_PER_FLOAT);
				GLES20.glEnableVertexAttribArray(textureCoordinateAttribute);

				// Draw
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
				GLES20.glDrawElements(GLES20.GL_LINE_STRIP, indexCount*3,
						GLES20.GL_UNSIGNED_SHORT, 0);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
				
		       // Log.e(TAG, "index count: " + indexCount);
			//	Log.e(TAG, GLES20.glGetError() + "");
			}
		}

		public void release() {
			if (vbo[0] > 0) {
				GLES20.glDeleteBuffers(vbo.length, vbo, 0);
				vbo[0] = 0;
			}

			if (ibo[0] > 0) {
				GLES20.glDeleteBuffers(ibo.length, ibo, 0);
				ibo[0] = 0;
			}
		}

		
	}
	
	public class Constellation {
		
		private final int[] vbo = new int[1];
		private final int[] ibo = new int[1];
		private FloatBuffer pointVertexDataBuffer;
		private ShortBuffer pointIndexDataBuffer;
		private float[] pointVertexData;
		private short[] pointIndexData;
		private int indexCount;
		
		public Constellation(Tuple[] stars) {
			
			try {
			
				build(stars);
				prepareBuffers();

			} catch (Throwable t) {

				Log.w(TAG, t);
				errorHandler.handleError(ErrorType.BUFFER_CREATION_ERROR,
					t.getLocalizedMessage());

			}
			
			
		}

		private void build(Tuple[] stars) {
			GLES20.glUseProgram(program);
			pointVertexData = new float[12*stars.length];
			pointIndexData = new short[stars.length];
			int offset = 0;
			indexCount = stars.length;
		//	Log.e("TAG", "index count: " + indexCount);
			
			
			for (Tuple s : stars) {
				
				float distance = 400;
				
				float azimuth = s.x;
				float altitude = s.y;

						
				double sin_az = Math.sin(Math.toRadians(azimuth));
				double cos_az = Math.cos(Math.toRadians(azimuth));
				double sin_alt = Math.sin(Math.toRadians(altitude));
				double cos_alt = Math.cos(Math.toRadians(altitude));

				float[] centre = {(float) (-distance * sin_az * cos_alt),
							(float) (distance * sin_alt), (float) (distance * cos_az * cos_alt)};

				float x = (float) centre[0];
				float y = (float) centre[1];
				float z = (float) centre[2];
				
				final float[] normalVector = { x, y, z };
			
				pointVertexData[offset++] = x;
				pointVertexData[offset++] = y;
				pointVertexData[offset++] = z;
					
				// Normalize the normal
				final float length = Matrix.length(normalVector[0],
						normalVector[1], normalVector[2]);

				// Normal Data
				pointVertexData[offset++] = normalVector[0] / length;
				pointVertexData[offset++] = normalVector[1] / length;
				pointVertexData[offset++] = normalVector[2] / length;


					
				// Color Data
				pointVertexData[offset++] = 0.835294f;
				pointVertexData[offset++] = 0.73725f;
				pointVertexData[offset++] = 0.1647f;
				pointVertexData[offset++] = 1.0f;
					


				// Add texture data
				pointVertexData[offset++] = (float) 0.5;
				pointVertexData[offset++] = (float) 0.5;


			}
			
			
			offset = 0;
			int counter = stars.length;
			
			
			for (int n = 0; n < counter; n++) {

				pointIndexData[offset++] = (short) (n);

			}
			indexCount = pointIndexData.length;
			
		}
		
		
		public void prepareBuffers() {
			GLES20.glUseProgram(program);

			pointVertexDataBuffer = ByteBuffer
					.allocateDirect(pointVertexData.length * BYTES_PER_FLOAT)
					.order(ByteOrder.nativeOrder()).asFloatBuffer();
			pointVertexDataBuffer.put(pointVertexData).position(0);

			pointIndexDataBuffer = ByteBuffer
					.allocateDirect(pointIndexData.length * BYTES_PER_SHORT)
					.order(ByteOrder.nativeOrder()).asShortBuffer();
			pointIndexDataBuffer.put(pointIndexData).position(0);

			GLES20.glGenBuffers(1, vbo, 0);
			GLES20.glGenBuffers(1, ibo, 0);

			if (vbo[0] > 0 && ibo[0] > 0) {

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
				GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
						pointVertexDataBuffer.capacity() * BYTES_PER_FLOAT,
						pointVertexDataBuffer, GLES20.GL_STREAM_DRAW);

				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
				GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,
						pointIndexDataBuffer.capacity() * BYTES_PER_SHORT,
						pointIndexDataBuffer, GLES20.GL_STREAM_DRAW);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

			} else {

				errorHandler.handleError(ErrorType.BUFFER_CREATION_ERROR,
						"glGenBuffers");
			}
			
		}

		public void render() {
			
			GLES20.glUseProgram(program);
			
			if (vbo[0] > 0 && ibo[0] > 0) {
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);

				// Bind Attributes
				GLES20.glVertexAttribPointer(positionAttribute,
						POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
						STRIDE, 0);
				GLES20.glEnableVertexAttribArray(positionAttribute);

				GLES20.glVertexAttribPointer(normalAttribute,
						NORMAL_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
						STRIDE, POSITION_DATA_SIZE_IN_ELEMENTS
								* BYTES_PER_FLOAT);
				GLES20.glEnableVertexAttribArray(normalAttribute);

				GLES20.glVertexAttribPointer(
						colorAttribute,
						COLOR_DATA_SIZE_IN_ELEMENTS,
						GLES20.GL_FLOAT,
						false,
						STRIDE,
						(POSITION_DATA_SIZE_IN_ELEMENTS + NORMAL_DATA_SIZE_IN_ELEMENTS)
								* BYTES_PER_FLOAT);
				GLES20.glEnableVertexAttribArray(colorAttribute);

				GLES20.glVertexAttribPointer(
						textureCoordinateAttribute,
						TEXTURE_DATA_SIZE_IN_ELEMENTS,
						GLES20.GL_FLOAT,
						false,
						STRIDE,
						(POSITION_DATA_SIZE_IN_ELEMENTS
								+ NORMAL_DATA_SIZE_IN_ELEMENTS + COLOR_DATA_SIZE_IN_ELEMENTS)
								* BYTES_PER_FLOAT);
				GLES20.glEnableVertexAttribArray(textureCoordinateAttribute);

				// Draw
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
				GLES20.glDrawElements(GLES20.GL_LINES, indexCount,
						GLES20.GL_UNSIGNED_SHORT, 0);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
			
			//Log.e("TAG", "error: " + GLES20.glGetError());
		}
		
	}
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
	
	private void printMatrix(float[] mvpMatrix ) {
		
		Log.e(TAG, "[" + mvpMatrix[0] + ", " + mvpMatrix[1] + ", " + mvpMatrix[2] + ", " + mvpMatrix[3] + "]");
		Log.e(TAG, "[" + mvpMatrix[4] + ", " + mvpMatrix[5] + ", " + mvpMatrix[6] + ", " + mvpMatrix[7] + "]");
		Log.e(TAG, "[" + mvpMatrix[8] + ", " + mvpMatrix[9] + ", " + mvpMatrix[10] + ", " + mvpMatrix[11] + "]");
		Log.e(TAG, "[" + mvpMatrix[12] + ", " + mvpMatrix[13] + ", " + mvpMatrix[4] + ", " + mvpMatrix[15] + "]");
	}
	
	public void setAccelorometer(boolean status) {
		
		augmentedReality = status;
		
		if (status == true) {
		
			// Set the view matrix
			Matrix.setLookAtM(viewMatrix, 0, 0, 0, 0,
									0, 0, 1,
									0, 1, 0);
			
		} else {
			
			// Set the view matrix
			Matrix.setLookAtM(viewMatrix, 0, eye_vec[0], eye_vec[1], eye_vec[2],
										look_vec[0], look_vec[1], look_vec[2],
										up_vec[0], up_vec[1], up_vec[2]);
			
		}
	}
	
	public boolean getAccelerometerStatus() {
		return augmentedReality;
	}
		
	public void setEquatorialStatus(boolean status) {
		
		equatorialStatus = status;
	}
	
    public void setPlanetTexts(boolean status) {
    	
    	planetTextsStatus = status;
    	
    	
    }
    
    public boolean getPlanetTextsStatus() {
    	
    	return planetTextsStatus;
    	
    	
    }
	
	public boolean getEquatorialStatus() {
		return equatorialStatus;
	}
	
	public void resetView() {
		
		this.yaw = 0;
		this.pitch = 0;
		
		zoom(1);
		findingPlanet = false;
		//this.NUM_STARS = 1200;
		//alteredZoom = true;
	    	
	}

	public void setAlteredLocationTime(boolean b) {
		this.alteredLocationTime = b;
	}
	
	public void setMinStars(int num_stars) {
		this.MIN_STARS = num_stars;
	}
	
	public int getMinStars() {
		return MIN_STARS;
	}
	
	public float getStarScale() {
		return STAR_SCALE;
	}
	
	public void setStarScale( float scale) {
		this.STAR_SCALE = scale;
	}

	public boolean getConstellationStatus() {
		return constellationStatus;
	}

	public void setConstellationStatus(boolean checked) {
		this.constellationStatus = checked;
		
	}

}
