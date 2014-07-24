package com.ex.augmentedreality;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

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
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.ImageView.ScaleType;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ARCam extends Activity implements OnClickListener,
		OnMenuItemClickListener, OnMultiChoiceClickListener {

	CustomCameraView cv;
	RealTimeGraph rtGraph;
	Camera camera;
	int orientation; // Device orientation;
	int sdk = android.os.Build.VERSION.SDK_INT; // Current SDK version
	int scrSize;
	boolean[] checkedItems = new boolean[] { false, false, false, false }; // Boolean
																			// array
																			// for
																			// checked
																			// items
																			// in
																			// dialog
																			// menu
	String[] values = null;

	// Defining constants for giving custom views a unique ID:
	final int FRAME_ID = 100;
	final int CAMERAVIEW_ID = 101;
	final int LISTVIEW_ID = 102;
	final int GRAPHVIEW_ID = 103;
	final int TVINFO_ID = 104;
	final int BSCAN_ID = 105;
	final int BOPTIONS_ID = 106;
	final int BORIENTATION_ID = 107;
	final int ALARMVIEW_ID = 108;

	// Creating all custom layouts, views and buttons. Setting OnClickListeners.
	// Creating an instance of the custom class CustomCameraView, a surfaceView
	// to hold the camera view.
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		// Setting full screen feature on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Creating new frame layout with ID=100, and setting this as the
		// content view:
		FrameLayout frame = new FrameLayout(this.getApplicationContext());
		setContentView(frame);
		frame.setId(FRAME_ID);

		// Creating new camera view with ID=101, and adding this to frame:
		cv = new CustomCameraView(this.getApplicationContext());
		cv.setId(CAMERAVIEW_ID);
		frame.addView(cv);

		// Getting screen size and orientation for device:
		scrSize = getScreenSize();
		orientation = getOrientation();

		// Creating button for QR scan with ID=105, and adding button to frame:
		Button bScanQR = new Button(this);
		bScanQR.setText("Scan QR");
		bScanQR.setTextColor(Color.WHITE);
		bScanQR.setId(BSCAN_ID);
		bScanQR.setBackground(getResources().getDrawable(R.drawable.testbutton));
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.LEFT;
		params.topMargin = getDensityPixels(2);
		params.leftMargin = getDensityPixels(2);
		frame.addView(bScanQR, params);
		bScanQR.setOnClickListener(this);

		// Creating ImageButton for options with ID=106, and adding button to
		// frame:
		ImageButton bOptions = new ImageButton(this);
		bOptions.setId(BOPTIONS_ID);
		bOptions.setBackground(getResources().getDrawable(
				R.drawable.options_icon));
		FrameLayout.LayoutParams paramB = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		paramB.gravity = Gravity.RIGHT;
		paramB.rightMargin = getDensityPixels(2);
		paramB.topMargin = getDensityPixels(2);
		frame.addView(bOptions, paramB);
		bOptions.setOnClickListener(this);

		// Creating TextView for component information with ID=104, and adding
		// this to frame:
		TextView tvInfo = new TextView(this);
		FrameLayout.LayoutParams paramTV = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		paramTV.gravity = Gravity.BOTTOM;
		paramTV.leftMargin = getDensityPixels(2);
		paramTV.bottomMargin = getDensityPixels(2);
		tvInfo.setId(TVINFO_ID);
		frame.addView(tvInfo, paramTV);

		// Creating ImageButton for changing of the device orientation with
		// ID=107, and adding button to frame:
		ImageButton bOrientation = new ImageButton(this);
		bOrientation.setId(BORIENTATION_ID);
		bOrientation.setBackground(getResources().getDrawable(
				R.drawable.orientation));
		FrameLayout.LayoutParams paramC = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		paramC.gravity = Gravity.RIGHT;
		paramC.topMargin = getDensityPixels(75);
		paramC.rightMargin = getDensityPixels(6);
		frame.addView(bOrientation, paramC);
		bOrientation.setOnClickListener(this);

		// Generating EXAMPLE graph series:
		GraphViewSeries sinus = generateSinusGraph();
		GraphViewSeries puls = generateTriangleGraph();
		GraphViewSeries constant = generateConstantGraph();

		// Creating GraphView with ID=103
		GraphView graphView = new LineGraphView(this, "Process values:");
		graphView.setId(GRAPHVIEW_ID);

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

		// Creating Layout parameters for the GraphView, and adding GraphView +
		// Parameters to Frame Layout:
		int width_dp = getDensityPixels(180);
		int height_dp = getDensityPixels(220);
		FrameLayout.LayoutParams graphParams = new FrameLayout.LayoutParams(
				width_dp, height_dp);
		graphParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
		graphParams.bottomMargin = getDensityPixels(2);
		graphParams.rightMargin = getDensityPixels(2);
		frame.addView(graphView, graphParams);

		// Creating ListView for showing process values with ID= 102, and adding
		// this to frame:
		ListView listview = new ListView(this);
		listview.setId(LISTVIEW_ID);
		FrameLayout.LayoutParams paramsList = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		paramsList.gravity = Gravity.TOP | Gravity.LEFT;
		paramsList.topMargin = getDensityPixels(52);
		paramsList.height = getDensityPixels(185);
		paramsList.width = getDensityPixels(154);
		paramsList.leftMargin = getDensityPixels(2);
		listview.setBackground(getResources()
				.getDrawable(R.drawable.customrect));
		TextView header_pValues = new TextView(this);
		header_pValues.setText("  PROCESS VALUES: ");
		header_pValues.setTextSize(18);
		listview.getBackground().setAlpha(210);
		listview.addHeaderView(header_pValues);
		frame.addView(listview, paramsList);

		// Creating ListView for showing alarms with ID = 108, and adding this
		// to frame:
		ListView alarmview = new ListView(this);
		alarmview.setId(ALARMVIEW_ID);
		FrameLayout.LayoutParams alarmparams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		alarmparams.gravity = Gravity.TOP | Gravity.LEFT;
		alarmparams.topMargin = getDensityPixels(52);
		alarmparams.leftMargin = getDensityPixels(190);
		alarmparams.height = getDensityPixels(185);
		alarmparams.width = getDensityPixels(154);

		alarmview.setBackgroundResource(R.drawable.customborder);
		alarmview.getBackground().setAlpha(160);
		TextView header_alarms = new TextView(this);
		header_alarms.setText(" ALARMS: ");
		header_alarms.setTextSize(18);
		header_alarms.setTextColor(Color.rgb(14, 13, 13));
		alarmview.addHeaderView(header_alarms);

		frame.addView(alarmview, alarmparams);

		// Creating file with component info:
		String filename1 = "components";
		String filename2 = "processValues";
		String filename3 = "alarms";

		String components = addComponentInfo(1, "110", "Vard Electro",
				"14.05.08")
				+ addComponentInfo(2, "645", "Vard Piping", "18.07.14")
				+ addComponentInfo(3, "433", "Vard Electro R&D", "11.06.13");

		String processValues = addProcessValues(1, 65.3f, 3.01f, 88.77f, 1204f,
				3.4f)
				+ addProcessValues(2, 94.3f, 2.61f, 102.9f, 6659.3f, 7.4f)
				+ addProcessValues(3, 105.3f, 4.21f, 21.77f, 56.76f, 19.4f);

		FileOutputStream stream1;
		FileOutputStream stream2;
		try {
			stream1 = openFileOutput(filename1, Context.MODE_PRIVATE);
			stream1.write(components.getBytes());
			stream1.close();

			stream2 = openFileOutput(filename2, Context.MODE_PRIVATE);
			stream2.write(processValues.getBytes());
			stream2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	} // End of the onCreate() method

	private String addComponentInfo(int id, String SFI, String manufacturer,
			String lastFix) {
		return "#" + id + " •" + SFI + " •" + manufacturer + " •" + lastFix;
	}

	private String addProcessValues(int id, float temperature, float pressure,
			float rpm, float torque, float oil_level) {
		// TODO Auto-generated method stub
		return "#" + id + " •" + temperature + " •" + pressure + " •" + rpm
				+ " •" + torque + " •" + oil_level;

	}

	private String addAlarms(int id, String type, String severity) {
		return "#" + id + " •" + type + " •" + severity;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		TextView tv = (TextView) findViewById(TVINFO_ID); // For component info
		GraphView gv = (GraphView) findViewById(GRAPHVIEW_ID); // For graphs
		ListView lv = (ListView) findViewById(LISTVIEW_ID); // For process
															// values
		ListView av = (ListView) findViewById(ALARMVIEW_ID); // For alarms

		if (checkedItems[0]) {
			tv.setVisibility(TextView.VISIBLE);
		} else {
			tv.setVisibility(TextView.GONE);
		}

		if (checkedItems[1]) {
			gv.setVisibility(GraphView.VISIBLE);
		} else {
			gv.setVisibility(GraphView.GONE);
		}
		if (checkedItems[2]) {
			lv.setVisibility(ListView.VISIBLE);
		} else {
			lv.setVisibility(ListView.GONE);
		}
		if (checkedItems[3]) {
			av.setVisibility(ListView.VISIBLE);
		} else {
			av.setVisibility(ListView.GONE);
		}
	}

	// -------------------- IMPLEMENTATION OF ONCLICK METHODS
	// -------------------- //

	// Implements the OnClick method for the custom views
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case BSCAN_ID:
			// Scan QR button pressed, using sleep-function to successfully
			// release the camera:
			try {
				Thread.sleep(300);
				if (camera != null) {
					camera.release();
					camera = null;
				}
				Thread.sleep(300);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Intent to use external application Barcode Scanner. If not
			// installed, it will take the user to Google Play Store.
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
			break;

		case BOPTIONS_ID:
			// Options button pressed
			PopupMenu pop = new PopupMenu(ARCam.this, v);
			pop.getMenuInflater().inflate(R.menu.mymenu, pop.getMenu());
			pop.show();
			pop.setOnMenuItemClickListener(this);
			break;

		case BORIENTATION_ID:
			// Orientation button pressed
			changeOrientation();
			changeParameters();
			break;
		}
	}

	// Implements the OnClick method for the custom Menu items:
	@Override
	public boolean onMenuItemClick(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.help:
			// Should display help information (PDFs and/or extra process
			// information)
			CharSequence items_array[] = new CharSequence[] { "Documentation",
					"Maintenance guide", "Emergency procedure" };
			AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
			myBuilder.setTitle("Select help function: ");

			myBuilder.setItems(items_array, myListener);

			AlertDialog myDialog = myBuilder.create();
			myDialog.show();
			break;

		case R.id.alarms:
			// Show alarms on a table view:
			AlertDialog.Builder alarmBuilder = new AlertDialog.Builder(this);
			alarmBuilder.setTitle("  Alarms:  ");
			alarmBuilder.setView(getLayoutInflater().inflate(R.layout.mytable,
					null));
			alarmBuilder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});

			Dialog myAlarm = alarmBuilder.create();
			myAlarm.show();

			// TableLayout myTable =
			// (TableLayout)myAlarm.findViewById(R.layout.mytable);

			// Getting content of the tableview for alarms:
			TextView tvTemperatureValue = (TextView) myAlarm
					.findViewById(R.id.tvTemperatureValue);
			TextView tvPressureValue = (TextView) myAlarm
					.findViewById(R.id.tvPressureValue);
			TextView tvOmegaValue = (TextView) myAlarm
					.findViewById(R.id.tvOmegaValue);
			TextView tvTorqueValue = (TextView) myAlarm
					.findViewById(R.id.tvTorqueValue);
			TextView tvOilLevelValue = (TextView) myAlarm
					.findViewById(R.id.tvOilLevelValue);

			ImageView ivTemperature = (ImageView) myAlarm
					.findViewById(R.id.ivTemperatureSeverity);
			ImageView ivPressure = (ImageView) myAlarm
					.findViewById(R.id.ivPressureSeverity);
			ImageView ivOmega = (ImageView) myAlarm
					.findViewById(R.id.ivOmegaSeverity);
			ImageView ivTorque = (ImageView) myAlarm
					.findViewById(R.id.ivTorqueSeverity);
			ImageView ivOilLevel = (ImageView) myAlarm
					.findViewById(R.id.ivOilLevelSeverity);
			ivTemperature.setScaleType(ScaleType.FIT_CENTER);
			ivPressure.setScaleType(ScaleType.FIT_CENTER);
			ivOmega.setScaleType(ScaleType.FIT_CENTER);
			ivTorque.setScaleType(ScaleType.FIT_CENTER);
			ivOilLevel.setScaleType(ScaleType.FIT_CENTER);

			float TEMP_MAX = 100f;
			if (values != null && values.length > 5) {
				tvTemperatureValue.setText(values[1]);
				tvPressureValue.setText(values[2]);
				tvOmegaValue.setText(values[3]);
				tvTorqueValue.setText(values[4]);
				tvOilLevelValue.setText(values[5]);

				ivPressure.setImageResource(R.drawable.greenicon);
				ivOmega.setImageResource(R.drawable.greenicon);
				ivTorque.setImageResource(R.drawable.greenicon);
				ivOilLevel.setImageResource(R.drawable.yellowicon);

				if (Float.parseFloat(values[1]) >= TEMP_MAX) {
					ivTemperature.setImageResource(R.drawable.redicon);
				} else if (Float.parseFloat(values[1]) >= TEMP_MAX - 10) {
					ivTemperature.setImageResource(R.drawable.yellowicon);
				} else {
					ivTemperature.setImageResource(R.drawable.greenicon);
				}
			} else {
				tvTemperatureValue.setText("");
				tvPressureValue.setText("");
				tvOmegaValue.setText("");
				tvTorqueValue.setText("");
				tvOilLevelValue.setText("");
				ivTemperature.setImageResource(android.R.color.transparent);
				ivPressure.setImageResource(android.R.color.transparent);
				ivOmega.setImageResource(android.R.color.transparent);
				ivTorque.setImageResource(android.R.color.transparent);
				ivOilLevel.setImageResource(android.R.color.transparent);
			}
			break;

		case R.id.dispsetting:
			// Display settings. Prompts the user to select which of the
			// augmented views to see.

			// Building AlertDialog to give options for changing the augmented
			// views:
			CharSequence view_alternatives[] = new CharSequence[] {
					"Component Info", "Graphs", "Process Values", "Alarms" };
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.magnifier);
			builder.setTitle("Select Augmented Views: ");
			builder.setMultiChoiceItems(view_alternatives, checkedItems, this);

			// Creating okButton:
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Toast myT = Toast.makeText(getApplicationContext(),
									"New views added!", Toast.LENGTH_SHORT);
							myT.show();

						}
					});
			AlertDialog alertDialog = builder.create();
			// Creating OnShowListener for the dialog:
			alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

				@Override
				public void onShow(DialogInterface dialog) {
					Button okButton = ((AlertDialog) dialog)
							.getButton(DialogInterface.BUTTON_POSITIVE);
					okButton.setTextSize(22);
					okButton.setBackgroundResource(R.drawable.custombutton);

				}
			});

			alertDialog.show();
			break;

		case R.id.transparent:
			// Prompts the user to select which views to edit the level of
			// transparency.
			selectViews();
			break;

		case R.id.ret:
			// Returns to the start-up activity.
			finish();
			// Intent myIntent = new Intent(this, RealTimeGraph.class);
			// startActivity(myIntent);
			break;
		}
		return true;
	}

	// HELP "button"
	DialogInterface.OnClickListener myListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			switch (which) {
			case 0:
				// Documentation is pressed: Should view component documentation
				try {
					File docFile = new File(
							Environment.getExternalStorageDirectory(),
							"Documentation.docx");
					try {
						if (docFile.exists()) {
							Uri path = Uri.fromFile(docFile);
							Intent fileIntent = new Intent(Intent.ACTION_VIEW);
							fileIntent
									.setDataAndType(path,
											"application/vnd.openxmlformats-officedocument.wordprocessingml.document");
							fileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(fileIntent);
						} else {
							Toast.makeText(ARCam.this, "File NotFound",
									Toast.LENGTH_SHORT).show();
						}
					} catch (ActivityNotFoundException e) {
						Toast.makeText(ARCam.this,
								"No Viewer Application Found",
								Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case 1:
				// Maintenance Guide is pressed: Should view maintenance
				// information for specific component
				try {
					File maintenanceFile = new File(
							Environment.getExternalStorageDirectory(),
							"MaintenanceGuide.docx");
					try {
						if (maintenanceFile.exists()) {
							Uri path = Uri.fromFile(maintenanceFile);
							Intent fileIntent = new Intent(Intent.ACTION_VIEW);
							fileIntent
									.setDataAndType(path,
											"application/vnd.openxmlformats-officedocument.wordprocessingml.document");
							fileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(fileIntent);
						} else {
							Toast.makeText(ARCam.this, "File NotFound",
									Toast.LENGTH_SHORT).show();
						}
					} catch (ActivityNotFoundException e) {
						Toast.makeText(ARCam.this,
								"No Viewer Application Found",
								Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case 2:
				// Emergency Procedure is pressed: Should view emergency
				// procedure for specific component
				try {
					File emergencyFile = new File(
							Environment.getExternalStorageDirectory(),
							"EmergencyProcedure.docx");
					try {
						if (emergencyFile.exists()) {
							Uri path = Uri.fromFile(emergencyFile);
							Intent fileIntent = new Intent(Intent.ACTION_VIEW);
							fileIntent
									.setDataAndType(path,
											"application/vnd.openxmlformats-officedocument.wordprocessingml.document");
							fileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(fileIntent);
						} else {
							Toast.makeText(ARCam.this, "File NotFound",
									Toast.LENGTH_SHORT).show();
						}
					} catch (ActivityNotFoundException e) {
						Toast.makeText(ARCam.this,
								"No Viewer Application Found",
								Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
	};

	protected void selectViews() {
		// TODO Auto-generated method stub
		// Building AlertDialog to give options for changing the augmented
		// views:

		final AlertDialog.Builder builder2 = new AlertDialog.Builder(ARCam.this);
		builder2.setTitle("Select view(s) to edit:");

		CharSequence items[] = new CharSequence[] { "Component Info", "Graphs",
				"Process Values", "Alarms", "ALL" };
		builder2.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:
					setTransparencyLevel(0);
					break;

				case 1:
					setTransparencyLevel(1);
					break;

				case 2:
					setTransparencyLevel(2);
					break;

				case 3:
					setTransparencyLevel(3);
					break;

				case 4:
					setTransparencyLevel(4);
					break;

				}
				dialog.dismiss();
			}

		});

		AlertDialog dialog2 = builder2.create();
		dialog2.show();

	}

	protected void setTransparencyLevel(final int selectedViews) {
		// TODO Auto-generated method stub
		final TextView tv = (TextView) findViewById(TVINFO_ID);
		final GraphView gv = (GraphView) findViewById(GRAPHVIEW_ID);
		final ListView lv = (ListView) findViewById(LISTVIEW_ID);
		final ListView av = (ListView) findViewById(ALARMVIEW_ID);

		final NumberPicker myPicker = new NumberPicker(ARCam.this);
		RelativeLayout relLayout = new RelativeLayout(ARCam.this);
		int dpix = getDensityPixels(50);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				dpix, dpix);
		RelativeLayout.LayoutParams paramsPicker = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		relLayout.setLayoutParams(params);
		paramsPicker.addRule(RelativeLayout.CENTER_HORIZONTAL);
		myPicker.setMinValue(0);
		myPicker.setMaxValue(10); // alpha=255 is fully visible
		relLayout.addView(myPicker, paramsPicker);

		AlertDialog.Builder transBuilder = new AlertDialog.Builder(ARCam.this);
		transBuilder.setTitle("Set transparency:");
		transBuilder.setMessage("0 is fully transparent");
		transBuilder.setView(relLayout);

		transBuilder.setPositiveButton("SET",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						boolean tvVisible = false;
						boolean gvVisible = false;
						boolean lvVisible = false;
						boolean avVisible = false;

						float frac = 255 / 10;
						final int value = myPicker.getValue();

						if (tv.getVisibility() == TextView.VISIBLE)
							tvVisible = true;

						if (gv.getVisibility() == GraphView.VISIBLE)
							gvVisible = true;

						if (lv.getVisibility() == ListView.VISIBLE)
							lvVisible = true;

						if (av.getVisibility() == ListView.VISIBLE)
							avVisible = true;

						switch (selectedViews) {

						case 0:
							if (tvVisible) {
								tv.getBackground().setAlpha(
										(int) (frac * value));
							}
							break;

						case 1:
							if (gvVisible) {
								gv.getBackground().setAlpha(
										(int) (frac * value));
							}
							break;

						case 2:
							if (lvVisible) {
								lv.getBackground().setAlpha(
										(int) (frac * value));
							}
							break;

						case 3:
							if (avVisible) {
								av.getBackground().setAlpha(
										(int) (frac * value));
							}
							break;

						case 4:
							if (tvVisible) {
								tv.getBackground().setAlpha(
										(int) (frac * value));
							}

							if (gvVisible) {
								gv.getBackground().setAlpha(
										(int) (frac * value));
							}

							if (lvVisible) {
								lv.getBackground().setAlpha(
										(int) (frac * value));
							}

							if (avVisible) {
								av.getBackground().setAlpha(
										(int) (frac * value));
							}

							break;

						}

					}
				});

		transBuilder.setNegativeButton("CANCEL",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});

		AlertDialog transDialog = transBuilder.create();
		// Setting onShow-listener for the dialog:
		transDialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				// TODO Auto-generated method stub
				Button posButton = ((AlertDialog) dialog)
						.getButton(DialogInterface.BUTTON_POSITIVE);
				Button negButton = ((AlertDialog) dialog)
						.getButton(DialogInterface.BUTTON_NEGATIVE);
				posButton.setTextSize(22);
				negButton.setTextSize(22);
				posButton.setBackgroundResource(R.drawable.custombutton);
				negButton.setBackgroundResource(R.drawable.custombutton);
			}
		});

		transDialog.show();
	}

	// Implements the OnClick method for the Dialog user interface
	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		// TODO Auto-generated method stub
		TextView tv = (TextView) findViewById(TVINFO_ID);
		GraphView gv = (GraphView) findViewById(GRAPHVIEW_ID);
		ListView lv = (ListView) findViewById(LISTVIEW_ID);
		ListView av = (ListView) findViewById(ALARMVIEW_ID);

		switch (which) {
		case 0:

			if (isChecked) {
				// Show component information
				tv.setVisibility(TextView.VISIBLE);
				checkedItems[0] = true;
			} else {
				// Hide component information
				tv.setVisibility(TextView.GONE);
				checkedItems[0] = false;
			}
			break;

		case 1:
			if (isChecked) {
				// Show graphs (process values)
				gv.setVisibility(GraphView.VISIBLE);
				checkedItems[1] = true;
			} else {
				// Hide graphs (process values)
				gv.setVisibility(GraphView.GONE);
				checkedItems[1] = false;
			}
			break;

		case 2:
			if (isChecked) {
				// Show warnings
				lv.setVisibility(ListView.VISIBLE);
				checkedItems[2] = true;
			} else {
				// Hide warnings
				lv.setVisibility(ListView.GONE);
				checkedItems[2] = false;
			}
			break;

		case 3:
			if (isChecked) {
				// Show alarms
				av.setVisibility(ListView.VISIBLE);
				checkedItems[3] = true;
			} else {
				// Hide alarms
				av.setVisibility(ListView.GONE);
				checkedItems[3] = false;
			}
			break;

		default:
			Toast myToast = Toast.makeText(getApplicationContext(),
					"No views selected!", Toast.LENGTH_LONG);
			myToast.show();
			break;
		}

		for (int i = 0; i < checkedItems.length; i++) {
			Log.v("SWITCH", "Checked Items[" + i + "] = " + checkedItems[i]
					+ "\n");
		}
	}

	// -------------------- IMPLEMENTATION OF onActivityResult METHOD
	// -------------------- //

	// Getting the scan result from the QR code scanning
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		super.onActivityResult(requestCode, resultCode, intent);
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		TextView tvInf = (TextView) findViewById(TVINFO_ID);
		GraphView gv = (GraphView) findViewById(GRAPHVIEW_ID);
		ListView lv = (ListView) findViewById(LISTVIEW_ID);
		ListView av = (ListView) findViewById(ALARMVIEW_ID);

		if (resultCode == RESULT_OK) {
			// Have got scanning result
			String QR_ID = scanningResult.getContents();

			changeParameters();
			tvInf.setBackgroundResource(R.drawable.customborder);
			tvInf.getBackground().setAlpha(160);
			tvInf.setTextColor(Color.rgb(14, 13, 13));

			// Getting component info and showing these in TextView:
			String filename1 = "components";
			Context context = this.getApplicationContext();
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			BufferedReader in = null;
			String theFinalRes = null;
			try {
				in = new BufferedReader(new FileReader(new File(
						context.getFilesDir(), filename1)));
				while ((line = in.readLine()) != null)
					stringBuilder.append(line);

			} catch (Exception e) {
				e.printStackTrace();
			}

			String compinfo = stringBuilder.toString();
			String[] result = compinfo.split("#");

			String theResult = null;
			if (result != null) {
				for (int i = 0; i < result.length; i++) {
					if (result[i].startsWith(QR_ID)) {
						theResult = result[i];
						break;
					}
				}
			}
			String[] temp = null;
			if (theResult != null) {
				temp = theResult.split("•");
			}
			if (temp != null) {
				theFinalRes = "ID: " + temp[0] + "\n •SFI: " + temp[1]
						+ "\n •Manufacturer: " + temp[2] + "\n •Last Repair: "
						+ temp[3];
				tvInf.setText(theFinalRes);
				checkedItems[0] = true;
			} else {
				checkedItems[0] = false;
				tvInf.setText("");
			}

			ArrayAdapter<String> listAdapter;
			String filename2 = "processValues";
			String line2;
			StringBuilder mySBuilder = new StringBuilder();
			BufferedReader in2 = null;
			String temp1 = null;
			String[] processVal = null;
			List<String> myResult = new ArrayList<String>();

			try {
				in2 = new BufferedReader(new FileReader(new File(
						context.getFilesDir(), filename2)));
				while ((line2 = in2.readLine()) != null)
					mySBuilder.append(line2);
			} catch (Exception e) {
				e.printStackTrace();
			}
			temp1 = mySBuilder.toString();
			processVal = temp1.split("#");

			String res = null;
			if (processVal != null) {
				for (int i = 0; i < processVal.length; i++) {
					if (processVal[i].startsWith(QR_ID)) {
						res = processVal[i];
						break;
					}
				}
			}
			if (res != null) {
				values = res.split("•");

				myResult.add("ID: " + values[0]);
				myResult.add("Temperature: " + values[1] + "\u00B0C");
				myResult.add("Pressure: " + values[2] + "bar");
				myResult.add("\u03C9: " + values[3] + "rad/s");
				myResult.add("Torque: " + values[4] + "Nm");
				myResult.add("Oil level: " + values[5] + "%");

				listAdapter = new ArrayAdapter<String>(this,
						R.layout.simplerow, myResult);
				lv.setAdapter(listAdapter);
				checkedItems[2] = true;
			} else {
				Toast myToast = Toast.makeText(getApplicationContext(),
						"ID not found!", Toast.LENGTH_LONG);
				myToast.show();
				lv.setAdapter(null);
				values = null;
				checkedItems[2] = false;
				checkedItems[1] = false;
			}

			// Getting process values and showing these in ListView:

			checkedItems[1] = true;

			// Checking if device has a large screen size:
			if (scrSize == 2 || scrSize == 3) {
				tvInf.setTextSize(30);
			} else {
				tvInf.setTextSize(18);
			}

		} else {
			// Didn't receive any scan data
			tvInf.setText("");
			lv.setAdapter(null);

			values = null;

			checkedItems[0] = false;
			checkedItems[1] = false;
			checkedItems[2] = false;
			checkedItems[3] = false;

			Toast toast = Toast.makeText(getApplicationContext(),
					"No scan data received!", Toast.LENGTH_LONG);
			toast.show();
		}

	}

	/*
	 * 
	 * private class HttpRequestTask extends AsyncTask<Void, Void, Component>{
	 * 
	 * @Override protected Component doInBackground(Void... params) { // TODO
	 * Auto-generated method stub return null; }
	 * 
	 * 
	 * }
	 */

	// ------------------------- MY METHODS ------------------------- //

	// Methods for generating example series for the GraphView:
	private GraphViewSeries generateSinusGraph() {
		// TODO Auto-generated method stub
		int num = 150;
		GraphViewData[] data = new GraphViewData[num];
		double v = 0;
		for (int i = 0; i < num; i++) {
			v += 0.2;
			data[i] = new GraphViewData(i, Math.sin(v));
		}
		GraphViewSeries sinus = new GraphViewSeries("Sinus",
				new GraphViewStyle(Color.BLUE, 4), data);

		return sinus;
	}

	private GraphViewSeries generateTriangleGraph() {
		// TODO Auto-generated method stub
		int num = 150;
		GraphViewData[] data2 = new GraphViewData[num];
		double value = 0.5;
		for (int k = 0; k < num; k++) {
			if (k % 5 == 0) {
				data2[k] = new GraphViewData(k, value * 2);
			} else {
				data2[k] = new GraphViewData(k, value);
			}
		}
		GraphViewSeries puls = new GraphViewSeries("Puls", new GraphViewStyle(
				Color.RED, 2), data2);
		return puls;
	}

	private GraphViewSeries generateConstantGraph() {
		// TODO Auto-generated method stub
		double con = -0.5;
		int num = 150;
		GraphViewData[] data3 = new GraphViewData[num];

		for (int j = 0; j < num; j++) {
			data3[j] = new GraphViewData(j, con);
		}
		GraphViewSeries constant = new GraphViewSeries("Constant",
				new GraphViewStyle(Color.GREEN, 5), data3);
		return constant;
	}

	// Method for getting the device's screen size:

	private int getScreenSize() {
		// TODO Auto-generated method stub
		int screenLayout = getResources().getConfiguration().screenLayout;
		screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

		switch (screenLayout) {
		case Configuration.SCREENLAYOUT_SIZE_SMALL:
			// Toast myT = Toast.makeText(getApplicationContext(),
			// "Small Screen Size", Toast.LENGTH_SHORT);
			// myT.show();
			return 0;
		case Configuration.SCREENLAYOUT_SIZE_NORMAL:
			// Toast myT2 = Toast.makeText(getApplicationContext(),
			// "Normal Screen Size", Toast.LENGTH_SHORT);
			// myT2.show();
			return 1;
		case Configuration.SCREENLAYOUT_SIZE_LARGE:
			// Toast myT3 = Toast.makeText(getApplicationContext(),
			// "Large Screen Size", Toast.LENGTH_SHORT);
			// myT3.show();
			return 2;
		case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			// Toast myT4 = Toast.makeText(getApplicationContext(),
			// "Extra Large Screen Size", Toast.LENGTH_SHORT);
			// myT4.show();
			return 3;
		default:
			Toast myT5 = Toast.makeText(getApplicationContext(),
					"Unable to get Screen Size", Toast.LENGTH_SHORT);
			myT5.show();
			return -1;

		}

	}

	// Method for getting pixels in Density independent format:
	private int getDensityPixels(int pixels) {
		// TODO Auto-generated method stub
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float dpPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				pixels, metrics);
		return (int) dpPixels;
	}

	// Method for getting the current device orientation:
	@SuppressWarnings("deprecation")
	private int getOrientation() {
		int rot;
		if (isAndroidSdkOld()) {
			rot = getWindowManager().getDefaultDisplay().getOrientation();
		} else {
			rot = getWindowManager().getDefaultDisplay().getRotation();
		}
		return rot;
	}

	// Method for changing the device orientation without interrupting camera
	// view:
	private void changeOrientation() {
		// TODO Auto-generated method stub
		if (isAndroidSdkOld()) {
			// Toast toast = Toast.makeText(getApplicationContext(),
			// "Old android version", Toast.LENGTH_SHORT);
			// toast.show();
			@SuppressWarnings("deprecation")
			int orient = getWindowManager().getDefaultDisplay()
					.getOrientation();

			if (orient == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		} else {
			int orient = getWindowManager().getDefaultDisplay().getRotation();

			if (orient == Surface.ROTATION_90 || orient == Surface.ROTATION_270) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		}
	}

	// Method returning 'true' if current Android SDK is older than Android 4.3
	// (Jelly Bean MR2):
	private boolean isAndroidSdkOld() {
		// TODO Auto-generated method stub
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
			return true;
		} else {
			return false;
		}
	}

	// Method for changing the Graph Parameters based on the current screen
	// orientation:
	private void changeParameters() {
		// TODO Auto-generated method stub
		GraphView gv = (GraphView) findViewById(GRAPHVIEW_ID);
		TextView tv = (TextView) findViewById(TVINFO_ID);
		ListView lv = (ListView) findViewById(LISTVIEW_ID);
		ListView av = (ListView) findViewById(ALARMVIEW_ID);
		LayoutParams p_gv = gv.getLayoutParams();
		LayoutParams p_tv = tv.getLayoutParams();
		LayoutParams p_lv = lv.getLayoutParams();
		LayoutParams p_av = av.getLayoutParams();

		orientation = getOrientation();

		if (scrSize == 2 || scrSize == 3) {

			p_lv.height = getDensityPixels(250);
			p_lv.width = getDensityPixels(185);

			p_av.height = getDensityPixels(250);
			p_av.width = getDensityPixels(185);

			if (isAndroidSdkOld()) {
				if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
					p_gv.width = getDensityPixels(600);
					p_gv.height = getDensityPixels(320);

				} else {
					p_gv.width = getDensityPixels(380);
					p_gv.height = getDensityPixels(420);

				}
			} else {
				if (orientation == Surface.ROTATION_90
						|| orientation == Surface.ROTATION_270) {
					p_gv.width = getDensityPixels(600);
					p_gv.height = getDensityPixels(320);
				} else {
					p_gv.width = getDensityPixels(380);
					p_gv.height = getDensityPixels(420);
				}
			}
		} else {

			if (isAndroidSdkOld()) {
				if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
					p_gv.width = getDensityPixels(330);
					p_gv.height = getDensityPixels(180);
				} else {
					p_gv.width = getDensityPixels(180);
					p_gv.height = getDensityPixels(220);

				}
			} else {
				if (orientation == Surface.ROTATION_90
						|| orientation == Surface.ROTATION_270) {
					p_gv.width = getDensityPixels(330);
					p_gv.height = getDensityPixels(180);
					p_tv.width = LayoutParams.WRAP_CONTENT;
					p_tv.height = LayoutParams.WRAP_CONTENT;
				} else {
					p_gv.width = getDensityPixels(180);
					p_gv.height = getDensityPixels(220);
					p_tv.width = getDensityPixels(165);

				}
			}
		}

		gv.setLayoutParams(p_gv);
		tv.setLayoutParams(p_tv);
		lv.setLayoutParams(p_lv);
	}

	// ---------------------- CUSTOM CAMERA VIEW CLASS -------------------- //

	// Class for adding camera to the custom surface view
	public class CustomCameraView extends SurfaceView implements
			SurfaceHolder.Callback {
		// Camera camera;
		SurfaceHolder previewHolder;
		boolean previewing = false;

		// Constructor :
		@SuppressWarnings("deprecation")
		public CustomCameraView(Context context) {
			super(context);

			previewHolder = this.getHolder();
			int sdk = android.os.Build.VERSION.SDK_INT;
			if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
				previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			}
			previewHolder.addCallback(this);
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (camera != null) {
				camera.stopPreview();
				camera.release();
				camera = null;
				previewing = false;
			}
			// unbindDrawables(findViewById(11));
			// System.gc();
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {

			camera = Camera.open();
			camera.setDisplayOrientation(90);

		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// holder.setSizeFromLayout();
			Parameters parameters = camera.getParameters();
			List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
			Log.v("TAG", "Listing all supported preview sizes");
			for (int i = 0; i < sizes.size(); i++)
				Log.v("TAG",
						" w: " + sizes.get(i).width + ", h: "
								+ sizes.get(i).height);
			// Camera.Size cs = sizes.get(0);
			if (previewing) {
				camera.stopPreview();
				previewing = false;
			}

			// Checking to see if device orientation is Portrait or Landscape :
			if (width > height) {

				Toast.makeText(getApplicationContext(), "width > height !",
						Toast.LENGTH_LONG).show();
				parameters.setPreviewSize(1280, 720);
				// parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
				// parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
				try {
					camera.setDisplayOrientation(0);
					camera.setParameters(parameters);
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(),
							"Failed to set parameters: " + e.getMessage(),
							Toast.LENGTH_LONG).show();
					Log.v("TAG", "Failed to set parameters: " + e.getMessage());
				}
			} else {

				Toast.makeText(getApplicationContext(), "height > width !",
						Toast.LENGTH_LONG).show();
				parameters.setPreviewSize(1280, 720);
				// parameters.setPreviewSize(height, cs.width);

				try {
					camera.setDisplayOrientation(90);
					camera.setParameters(parameters);
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(),
							"Failed to set parameters: " + e.getMessage(),
							Toast.LENGTH_LONG).show();
					Log.v("TAG", "Failed to set parameters: " + e.getMessage());
				}

			}

			try {
				camera.setPreviewDisplay(previewHolder);
				camera.startPreview();
				previewing = true;
			} catch (Exception e) {
				e.printStackTrace();
				camera.release();
				camera = null;
			}

		}
	}

}
