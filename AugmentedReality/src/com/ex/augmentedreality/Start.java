package com.ex.augmentedreality;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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
			Intent guideIntent = new Intent(Start.this , UserGuide.class);
			startActivity(guideIntent);
			
			
			break;
		}
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		MenuInflater popUp = getMenuInflater();
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
