package com.ex.augmentedreality;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SharedPrefs extends Activity implements OnClickListener{

	EditText etData;
	TextView tvResults;
	public static String filename = "MyData";
	SharedPreferences someData ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sharedpreferences);
		initialize();
		someData = getSharedPreferences(filename,0);
		
		
		
		
	}

	private void initialize() {
		// TODO Auto-generated method stub
		Button bSave = (Button) findViewById(R.id.bSave);
		Button bLoad = (Button) findViewById(R.id.bLoad);
		etData = (EditText) findViewById(R.id.etData);
		tvResults = (TextView) findViewById(R.id.tvDisplay);
		bSave.setOnClickListener(this);
		bLoad.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.bSave:
			String stringData = etData.getText().toString();
			SharedPreferences.Editor editor = someData.edit();
			editor.putString("componentInfo", stringData);
			editor.commit();
			
			break;
			
		case R.id.bLoad:
			someData = getSharedPreferences(filename,0);
			String returned = someData.getString("componentInfo", "Couldn't load data");
			tvResults.setText(returned);
			break;
		}
		
	}

}
