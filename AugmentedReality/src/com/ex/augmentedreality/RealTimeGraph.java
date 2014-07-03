package com.ex.augmentedreality;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewStyle;



import com.jjoe64.graphview.LineGraphView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

public class RealTimeGraph {

	private final Handler mHandler = new Handler();
	private Runnable mTimer1;
	private Runnable mTimer2;
	private GraphView graphView;
	private GraphViewSeries rtSeries1;
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
		
		// init RT series 1:
		rtSeries1 = new GraphViewSeries(new GraphViewData[] {
			new GraphViewData(1, 2.0d)
			, new GraphViewData(2, 1.5d)
			, new GraphViewData(3, 2.5d)
			, new GraphViewData(4, 1.0d)
			, new GraphViewData(5, 3.0d)
		});
		
		
		
		rtSeries3 = new GraphViewSeries(new GraphViewData[] {});
	
	
		if (getIntent().getStringExtra("type").equals("bar")){
			graphView = new BarGraphView(this, "BarGraph RealTime");
		}else{
			graphView = new LineGraphView(this, "LineGraph RealTime");
		}
		graphView.addSeries(rtSeries1);
		graphView.addSeries(rtSeries3);
		
		graphView.setViewPort(1, 8);
		graphView.setScalable(true);
		
		
	}

	
}
