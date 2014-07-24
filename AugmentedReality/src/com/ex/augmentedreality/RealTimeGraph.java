package com.ex.augmentedreality;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.LineGraphView;

public class RealTimeGraph extends Activity {

	private final Handler mHandler = new Handler();
	private Runnable mTimer1;
	private Runnable mTimer2;
	private GraphView graphView;
	private GraphViewSeries rtSeries2;
	private GraphViewSeries rtSeries3;
	private double graph2LastXValue = 5d;
	
	


	private double getRandom() {
		double high = 3;
		double low = 0.5;
		return Math.random()*(high-low) + low;
	}

	
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LinearLayout linLay = new LinearLayout(this.getApplicationContext());
		linLay.setId(13);
		
		setContentView(linLay);
	//	LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		rtSeries3 = new GraphViewSeries(new GraphViewData[] {});
		
	
		graphView = new LineGraphView(this, "Realtime");
		
		graphView.addSeries(rtSeries3);
		
		
	
		
		
		rtSeries2 = new GraphViewSeries(new GraphViewData[] {
			new GraphViewData(1, 2.0d)
			, new GraphViewData(2, 1.5d)
			, new GraphViewData(3, 2.5d)
			, new GraphViewData(4, 1.0d)
			, new GraphViewData(5, 3.0d)
		});
		
		((LineGraphView) graphView).setDrawBackground(true);
		graphView.addSeries(rtSeries2);
		graphView.setViewPort(1, 8);
		graphView.setScalable(true);
		linLay.addView(graphView);
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		mHandler.removeCallbacks(mTimer1);
		mHandler.removeCallbacks(mTimer2);
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mTimer1 = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				rtSeries3.resetData(new GraphViewData[] {
						new GraphViewData(2, getRandom())
						, new GraphViewData(3, getRandom())
						, new GraphViewData(4, getRandom())
				});
				mHandler.postDelayed(this, 300);
			}
			
		};
		mHandler.postDelayed(mTimer1, 300);
		
		mTimer2 = new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				graph2LastXValue += 1d;
				rtSeries2.appendData(new GraphViewData(graph2LastXValue, getRandom()), true);
				mHandler.postDelayed(this, 200);
			}
		};
		mHandler.postDelayed(mTimer2, 1000);
	}
	
	
	
	

	
}