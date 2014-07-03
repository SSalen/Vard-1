package com.ex.augmentedreality;



import java.util.List;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;




public class ARCam extends Activity implements OnClickListener , OnMenuItemClickListener , OnMultiChoiceClickListener {

	TextView tvInfo;
	MenuItem showComponentInfo;
	CustomCameraView cv;
	RealTimeGraph rtGraph;
	FrameLayout.LayoutParams graphParams;
	View myView;
	Camera camera;
	int orientation;	// Device orientation;
	int sdk = android.os.Build.VERSION.SDK_INT; // Current SDK version
	int scrSize;
	boolean[] checkedItems = {false, false, false, false};  // Boolean array for checked items in dialog menu
	
	

	// Creating all custom layouts, views and buttons. Setting OnClickListeners.
	// Creating an instance of the custom class CustomCameraView, a surfaceView to hold the camera view.
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		//Setting full screen feature on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//Creating new frame layout with ID=10, and setting this as the content view:
		FrameLayout frame = new FrameLayout(this.getApplicationContext());
		setContentView(frame);
		frame.setId(10); // FrameLayout ID=10
		
		//Creating new camera view with ID=11, and adding this to frame:
		cv = new CustomCameraView(this.getApplicationContext());
		cv.setId(11); // ID = 11
		frame.addView(cv);
		
		
		//Getting screen size and orientation for device:
		scrSize = getScreenSize();
		orientation = getOrientation();
				
		//Creating button for QR scan with ID=5
		Button bScanQR = new Button(this);
		bScanQR.setText("Scan QR");
		bScanQR.setId(5);
		bScanQR.setBackground(getResources().getDrawable(R.drawable.mybutton));
		FrameLayout.LayoutParams params= new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		params.gravity=Gravity.LEFT;
		params.topMargin=5;
		frame.addView(bScanQR,params);
		bScanQR.setOnClickListener(this);
		
		//Creating ImageButton for options with ID=6
		ImageButton bOptions = new ImageButton(this);
		bOptions.setId(6); 
		bOptions.setBackground(getResources().getDrawable(R.drawable.options_icon));
		FrameLayout.LayoutParams paramB = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		paramB.gravity=Gravity.RIGHT;
		paramB.topMargin=5;
		frame.addView(bOptions, paramB);
		bOptions.setOnClickListener(this);
		
		//Creating TextView for object information with ID=7
		TextView tvInfo = new TextView(this);
		FrameLayout.LayoutParams paramTV = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		paramTV.gravity=Gravity.BOTTOM;
		tvInfo.setId(7); 
		tvInfo.setVisibility(View.INVISIBLE);
		frame.addView(tvInfo, paramTV);
		
		//Creating ImageButton for changing of the device orientation with ID=8
		ImageButton bOrientation = new ImageButton(this);
		bOrientation.setId(8);
		bOrientation.setBackground(getResources().getDrawable(R.drawable.orientation));
		FrameLayout.LayoutParams paramC = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		paramC.gravity=Gravity.RIGHT;
		paramC.topMargin= 160;
		paramC.rightMargin= 15;
		frame.addView(bOrientation, paramC);
		bOrientation.setOnClickListener(this);
		
	

		// Generating graph series:
		GraphViewSeries sinus = generateSinusGraph();
		GraphViewSeries puls  = generateTriangleGraph();
		GraphViewSeries constant = generateConstantGraph();
		
	
		
		// Creating GraphView with ID=9
		GraphView graphView = new LineGraphView(this, "Process values:");
		graphView.setId(9); // ID = 9
		
		// Adding series to GraphView:
		graphView.addSeries(sinus);
		graphView.addSeries(puls);
		graphView.addSeries(constant);
		
		// Set view port, show legend and set background for the GraphView:
		graphView.setViewPort(0, 10);
		graphView.setScalable(true);
		
		graphView.setShowLegend(true);
		graphView.setLegendAlign(LegendAlign.MIDDLE);
		
		graphView.setBackgroundColor(Color.BLACK);
		graphView.getBackground().setAlpha(130);
		graphView.setVisibility(View.INVISIBLE);
		
		
		// Creating Layout parameters for the GraphView, and adding GraphView + Parameters to Frame Layout:
		FrameLayout.LayoutParams graphParams = new FrameLayout.LayoutParams(400,300);
		graphParams.gravity = Gravity.RIGHT | Gravity.BOTTOM ;
		graphParams.bottomMargin = 5;
		graphParams.rightMargin = 5;
		frame.addView(graphView, graphParams);
		
	}  // End of the onCreate() method
	
	
	


	private GraphViewSeries generateSinusGraph() {
		// TODO Auto-generated method stub
		int num = 150;
		GraphViewData[] data = new GraphViewData[num];
		double v = 0;
		for (int i=0 ; i<num ; i++){
			v += 0.2;
			data[i] = new GraphViewData(i, Math.sin(v));
		}
		GraphViewSeries sinus = new GraphViewSeries("Sinus", new GraphViewStyle(Color.BLUE, 4) ,data);
	
		return sinus;
	}

	private GraphViewSeries generateTriangleGraph() {
		// TODO Auto-generated method stub
		int num = 150;
		GraphViewData[] data2 = new GraphViewData[num];
		double value = 0.5;
		for (int k=0 ; k<num ; k++){
			if (k%5 == 0){
				data2[k] = new GraphViewData(k, value*2);
			}else{
				data2[k] = new GraphViewData(k, value);
			}
		}
		GraphViewSeries puls = new GraphViewSeries("Puls", new GraphViewStyle(Color.RED,2) , data2);
		return puls;
	}
	
	private GraphViewSeries generateConstantGraph() {
		// TODO Auto-generated method stub
		double con = -0.5;
		int num = 150;
		GraphViewData[] data3 = new GraphViewData[num];
	
		for (int j=0 ; j<num; j++) {
			data3[j] = new GraphViewData(j, con);
		}
		GraphViewSeries constant = new GraphViewSeries("Constant", new GraphViewStyle(Color.GREEN, 5), data3);
		return constant;
	}


	// Method for getting the device's screen size:
	private int getScreenSize() {
		// TODO Auto-generated method stub
		int screenLayout = getResources().getConfiguration().screenLayout;
		screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;
		
		switch (screenLayout) {
		case Configuration.SCREENLAYOUT_SIZE_SMALL:
			return 0;
		case Configuration.SCREENLAYOUT_SIZE_NORMAL:
			return 1;
		case Configuration.SCREENLAYOUT_SIZE_LARGE:
			return 2;
		case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			return 3;
		default:
			return -1;
		
		}
	
	}
	
	// Method for adjusting the graph height based on the screen size of the device:  (NOT USED yet)
	private void setGraphHeight() {
		// TODO Auto-generated method stub
		if (scrSize ==0 || scrSize == 1){
			if (isAndroidSdkOld()){
				if( orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
					graphParams.bottomMargin = 125;
				}else{
					graphParams.bottomMargin = 5;
				}
			}else{
				if(orientation == Surface.ROTATION_0 || orientation == Surface.ROTATION_180){
					graphParams.bottomMargin = 125;
				}else{
					graphParams.bottomMargin = 5;
				}
			}
			
		}else{
			graphParams.bottomMargin = 5;
		}
	}


	// Implements the OnClick method for the custom views
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case 5:
			// Scan QR button pressed, using sleep-function to successfully release the camera:
			try{
				Thread.sleep(300);
				if (camera != null){
					camera.release();
					camera = null;
				}
				Thread.sleep(300);
			}catch (Exception e){
				e.printStackTrace();
			}
			// Intent to use external application Barcode Scanner. If not installed, it will take the user to Google Play Store.
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
			break;
			
		case 6:
			// Options button pressed
			myView = v;
			PopupMenu pop = new PopupMenu(ARCam.this , v);
			pop.getMenuInflater().inflate(R.menu.mymenu, pop.getMenu());
			pop.show();
			pop.setOnMenuItemClickListener(this);
			break;
			
		case 8:
			//Orientation button pressed
			changeOrientation();
			changeGraphParameters();
			break;
		}
	}
	

	
	

	// Method for getting the current device orientation:
	@SuppressWarnings("deprecation")
	private int getOrientation(){
		int rot;
		if (isAndroidSdkOld()){
			rot = getWindowManager().getDefaultDisplay().getOrientation();
		}else{
			rot = getWindowManager().getDefaultDisplay().getRotation();
		}
		return rot;
	}
	
	// Method for changing the device orientation without interrupting camera view:
	private void changeOrientation() {
		// TODO Auto-generated method stub
		if (isAndroidSdkOld()){
			Toast toast = Toast.makeText(getApplicationContext(), "Old android version", Toast.LENGTH_SHORT);
			toast.show();
			int orient = getWindowManager().getDefaultDisplay().getOrientation();
		
			if(orient == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		}else {
			int orient = getWindowManager().getDefaultDisplay().getRotation();
		
			if (orient == Surface.ROTATION_90 || orient == Surface.ROTATION_270){
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		}
	}

	// Method returning 'true' if current Android SDK is older than Android 4.3 (Jelly Bean MR2):
	private boolean isAndroidSdkOld() {
		// TODO Auto-generated method stub
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2){
			return true;
		}else{
			return false;
		}
	}
	
	// Method for changing the Graph Parameters based on the current screen orientation:
		private void changeGraphParameters() {
			// TODO Auto-generated method stub
			GraphView gv = (GraphView) findViewById(9);
			LayoutParams p = gv.getLayoutParams();
			orientation = getOrientation();
			
			if (isAndroidSdkOld()){
				if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
					p.width= 800;
					p.height= 300;
				}else{
					p.width = 350;
					p.height= 400;
					
				}
			}else{
				if (orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270){
					p.width= 800;
					p.height= 300;
				}else {
					p.width = 350;
					p.height= 400;
				}	
			}
			
		
			gv.setLayoutParams(p);
		}


	// Implements the OnClick method for the custom Menu items:
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		
		switch(item.getItemId()){
		case R.id.help:
			// Should display help information (PDFs and/or extra process information)
			
			break;
			
		case R.id.dispsetting:
			// Should give display options
			
			//Building AlertDialog to give options for changing the augmented views:
			CharSequence view_alternatives[] = new CharSequence[] {"Component Info" , "Graphs" , "Warnings" , "Alarms" };
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.magnifier);
			builder.setTitle("Select Augmented Views: ");
			
			checkedItems = getCheckedItems();
			builder.setMultiChoiceItems(view_alternatives, checkedItems, this);
				
			// Creating okButton:
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Toast myT = Toast.makeText(getApplicationContext(), "New views added!", Toast.LENGTH_SHORT);
					myT.show();
					
				}
			});
			AlertDialog alertDialog = builder.create();
			
			//Creating OnShowListener for the dialog:
			alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
				
				@Override
				public void onShow(DialogInterface dialog) { 
					Button okButton =((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
					okButton.setTextSize(22);
					okButton.setBackgroundResource(R.drawable.custombutton);
					
				}
			});
			
			alertDialog.show();
			break;
			

		case R.id.ret:
			finish();
			break;
		}
		return true;
	}


	private boolean[] getCheckedItems() {
		// TODO Auto-generated method stub
		boolean[] items = checkedItems;
		tvInfo = (TextView) findViewById(7);
		GraphView gv = (GraphView) findViewById(9);
		if (tvInfo.getVisibility() == View.VISIBLE){
			checkedItems[0] = true;
		}else{
			checkedItems[0] = false;
		}
		if (gv.getVisibility() == View.VISIBLE){
			checkedItems[1] = true;
		}else{
			checkedItems[1] = false;
		}
		
		
		return items;
	}


	// Implements the OnClick method for the Dialog user interface
	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		// TODO Auto-generated method stub
		TextView tv = (TextView) findViewById(7);
		GraphView gv = (GraphView) findViewById(9);
	
		
	
		switch (which){
		case 0:
			
			if (tv.getVisibility() == View.VISIBLE){
				tv.setVisibility(View.INVISIBLE);
				isChecked = false;
			}else {
				tv.setVisibility(View.VISIBLE);
				isChecked = true;
			}
			break;
			
		case 1:
			if (gv.getVisibility() == View.VISIBLE){
				// Hide graphs
				gv.setVisibility(View.INVISIBLE);
				isChecked = false;
			}else {
				// Show graphs
				gv.setVisibility(View.VISIBLE);
				isChecked = true;
			}
			break;
			
		case 2:
			if (isChecked){
				// Hide warnings
				isChecked = false;
			}else {
				// Show warnings
				isChecked = true;
			}
			break;
			
		case 3:
			if (isChecked){
				// Hide alarms
				isChecked = false;
			}else {
				// Show alarms
				
				isChecked = true;
			}
			break;
		default:
				Toast myToast = Toast.makeText(getApplicationContext(), "No views selected!", Toast.LENGTH_LONG);
				myToast.show();
		break;
		}
	}
	
	// Getting the scan result from the QR code scanning
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

		super.onActivityResult(requestCode, resultCode, intent);
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode,intent);
		TextView tvInf = (TextView) findViewById(7);
		if(resultCode == RESULT_OK ){
			// Have got scanning result
			String scanContent = scanningResult.getContents();
			tvInf.setVisibility(View.VISIBLE);
			tvInf.setBackgroundResource(R.drawable.customborder);
			tvInf.getBackground().setAlpha(160);
			tvInf.setText(scanContent);
			tvInf.setTextColor(Color.rgb(14, 13, 13));

			// Checking if device has a large screen size:
			if (scrSize == 2 || scrSize ==3){
				tvInf.setTextSize(30);	
			}else {
				tvInf.setTextSize(18);	
			}
	
		}else {
			// Didn't receive any scan data
			tvInf.setText("");
			tvInf.setVisibility(View.INVISIBLE);
			Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_LONG);
			toast.show();
		}
		
	} 
	

/*	private void unbindDrawables(View view) {
		// TODO Auto-generated method stub
		if(view.getBackground() != null){
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup){
			for(int i=0 ; i< ((ViewGroup) view).getChildCount() ; i++){
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}*/




	// Class for adding camera to the custom surface view
	public class CustomCameraView extends SurfaceView implements SurfaceHolder.Callback {
		//Camera camera;
		SurfaceHolder previewHolder;
		boolean previewing = false;
		
		// Constructor :
		@SuppressWarnings("deprecation")
		public CustomCameraView(Context context) {
			super(context);
			
			previewHolder = this.getHolder();
			int sdk = android.os.Build.VERSION.SDK_INT;
			if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB ){
				previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			}
			previewHolder.addCallback(this);
		}
	
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if (camera != null){
					camera.stopPreview();
					camera.release();
					camera = null;
					previewing = false;
				}
			//	unbindDrawables(findViewById(11));
			//	System.gc();
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				
				camera = Camera.open();
				camera.setDisplayOrientation(90);

			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
				
				Parameters parameters = camera.getParameters();
				List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
				Camera.Size cs = sizes.get(0);
				if(previewing){
					camera.stopPreview();
					previewing= false;
				}
				
				// Checking to see if natural device orientation is Portrait or Landscape :
				if (width > height){
					camera.setDisplayOrientation(0);
					parameters.setPreviewSize(cs.width, cs.height);
					//parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
					//parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
					camera.setParameters(parameters);
					
				}else {
					camera.setDisplayOrientation(90);
					
				//	parameters.setPreviewSize(cs.width, cs.height);
					parameters.setPreviewSize(height, width);
					camera.setParameters(parameters);
					
				}
	
				try {
					camera.setPreviewDisplay(previewHolder);
					camera.startPreview();
					previewing = true;
				} catch (Exception e) {
					e.printStackTrace();
					camera.release();
					camera=null;}	
				
			}
	}


	
	
	
}
