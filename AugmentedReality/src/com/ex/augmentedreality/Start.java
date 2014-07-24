package com.ex.augmentedreality;


import java.io.File;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Start extends Activity implements OnClickListener {

	Button bStartCamera , bUserGuide;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		initialize();
		bStartCamera.setOnClickListener(this);
		bUserGuide.setOnClickListener(this);
	}



	private void initialize() {
		// TODO Auto-generated method stub
		bStartCamera = (Button) findViewById(R.id.bEnterCam);
		bUserGuide = (Button) findViewById(R.id.bUserGuide);
		Toast toast = Toast.makeText(getApplicationContext(), "Welcome to (Augmented) Reality!", Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.bEnterCam:
			try{
				Intent camIntent = new Intent(Start.this , ARCam.class);
				startActivity(camIntent);
			}catch (Exception e){
				e.printStackTrace();
			}
			break; 
		case R.id.bUserGuide:
				/*	----------------------------------------------------------
			 	Intent guideIntent = new Intent(Start.this , UserGuide.class);
				startActivity(guideIntent); 
				
				UserGuide.class not necessary: 	
				----------------------------------------------------------- */
			
			File ugfile = new File(Environment.getExternalStorageDirectory(),
					"UserGuide.docx");

			try {
				if (ugfile.exists()) {
					Uri path = Uri.fromFile(ugfile);
					Intent fileIntent = new Intent(Intent.ACTION_VIEW);
					fileIntent
							.setDataAndType(path,
									"application/vnd.openxmlformats-officedocument.wordprocessingml.document");
					fileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(fileIntent);
				} else {
					Toast.makeText(Start.this, "File NotFound",
							Toast.LENGTH_SHORT).show();
				}
			} catch (ActivityNotFoundException e) {
				Toast.makeText(Start.this, "No Viewer Application Found",
						Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			break;
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		MenuInflater popUp = getMenuInflater();
	//	menu.add(1,1,Menu.FIRST,"").setIcon(R.drawable.ebutton);
		
		popUp.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
			case R.id.exit:
			finish();
			
			
			break;
		}
		return false;
	}
	
	
	
}
