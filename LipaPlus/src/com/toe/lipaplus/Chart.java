package com.toe.lipaplus;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import android.graphics.Typeface;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class Chart extends SherlockActivity {

	String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chart);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getBundle();
		setUp();
	}

	private void getBundle() {
		// TODO Auto-generated method stub
		Bundle b = getIntent().getExtras();
		title = b.getString("title");
		getSupportActionBar().setTitle(title);
	}

	private void setUp() {
		// TODO Auto-generated method stub
		Typeface font = Typeface.createFromAsset(getAssets(), getResources()
				.getString(R.string.font));

		ValueLineChart mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);

		ValueLineSeries series = new ValueLineSeries();
		series.setColor(0xFF56B7F1);

		series.addPoint(new ValueLinePoint("Jan", 2.4f));
		series.addPoint(new ValueLinePoint("Feb", 3.4f));
		series.addPoint(new ValueLinePoint("Mar", .4f));
		series.addPoint(new ValueLinePoint("Apr", 1.2f));
		series.addPoint(new ValueLinePoint("Mai", 2.6f));
		series.addPoint(new ValueLinePoint("Jun", 1.0f));
		series.addPoint(new ValueLinePoint("Jul", 3.5f));
		series.addPoint(new ValueLinePoint("Aug", 2.4f));
		series.addPoint(new ValueLinePoint("Sep", 2.4f));
		series.addPoint(new ValueLinePoint("Oct", 3.4f));
		series.addPoint(new ValueLinePoint("Nov", .4f));
		series.addPoint(new ValueLinePoint("Dec", 1.3f));

		mCubicValueLineChart.addSeries(series);
		mCubicValueLineChart.startAnimation();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

}
