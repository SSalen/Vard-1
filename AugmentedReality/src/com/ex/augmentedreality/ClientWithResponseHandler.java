package com.ex.augmentedreality;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
/*
public class ClientWithResponseHandler extends AsyncTask<String, Void, RSSFeed> {
	
	private Exception exception;
	
	protected RSSFeed doInBackground(String... urls){
		
	}
	public ClientWithResponseHandler() {}

	public ClientWithResponseHandler(String tag) throws Exception {
		
		HttpClient httpClient = new DefaultHttpClient();
		
		try{
			HttpGet httpGet = new HttpGet("http://172.16.50.57:8080/rest/component/"+tag);
			System.out.println("Executing request: " + httpGet.getURI());
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			
			String responseBody = httpClient.execute(httpGet, responseHandler);
			System.out.println("------------------------------------");
			System.out.println(responseBody);
			System.out.println("------------------------------------");
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}
}
*/