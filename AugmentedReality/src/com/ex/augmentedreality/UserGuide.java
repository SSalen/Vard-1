package com.ex.augmentedreality;

import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
  
public class UserGuide extends Activity { 
  
    @Override
    protected void onCreate(Bundle savedInstanceState) { 
        // TODO Auto-generated method stub 
        super.onCreate(savedInstanceState); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.userguide); 
        WebView webview = (WebView) findViewById(R.id.wv);
        webview.setVerticalScrollBarEnabled(true);
        webview.setHorizontalScrollBarEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient());
       
        webview.loadUrl("http://docs.google.com/document/d/1c5fuyw0QxA8wRvv0pihp6OPdgbV8xB9Qb3tI17kb9Hc/edit");
    } 
  
} 