package com.ex.augmentedreality;

import java.io.File;
import java.net.URL;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class UserGuide extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.userguide);

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
				Toast.makeText(UserGuide.this, "File NotFound",
						Toast.LENGTH_SHORT).show();
			}
		} catch (ActivityNotFoundException e) {
			Toast.makeText(UserGuide.this, "No Viewer Application Found",
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

// ---------------------------------------------------------------------------------------------------------------
/*
 * PUT IN MANIFEST : <uses-permission android:name="android.permission.INTERNET"
 * />
 * 
 * WebView webview = (WebView) findViewById(R.id.wv);
 * webview.setVerticalScrollBarEnabled(true);
 * webview.setHorizontalScrollBarEnabled(true);
 * webview.getSettings().setBuiltInZoomControls(true);
 * webview.getSettings().setJavaScriptEnabled(true);
 * webview.setWebViewClient(new WebViewClient());
 * 
 * webview.loadUrl(
 * "http://docs.google.com/document/d/1c5fuyw0QxA8wRvv0pihp6OPdgbV8xB9Qb3tI17kb9Hc/edit"
 * );
 */
