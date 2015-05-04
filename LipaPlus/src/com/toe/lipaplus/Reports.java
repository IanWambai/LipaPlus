package com.toe.lipaplus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;
import com.toe.lipaplus.XListView.IXListViewListener;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class Reports extends SherlockFragmentActivity {

	ReportsFragmentAdapter mAdapter;
	ViewPager mPager;
	Intent i;
	PageIndicator mIndicator;
	ReportListItemAdapter adapter;
	XListView listView;
	SharedPreferences sp;
	StorageService storageService;
	ArrayList<String> jsonDocArray, jsonIdArray;
	String error;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.view_pager);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setSupportProgressBarIndeterminateVisibility(false);

		initialize();
		setAdapter();
		getData();
	}

	private void initialize() {
		// TODO Auto-generated method stub
		App42API.initialize(getApplicationContext(),
				getString(R.string.api_key), getString(R.string.secret_key));
		storageService = App42API.buildStorageService();
		sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
	}

	private void getData() {
		// TODO Auto-generated method stub
		setSupportProgressBarIndeterminateVisibility(true);

		HashMap<String, String> metaHeaders = new HashMap<String, String>();
		metaHeaders.put("orderByDescending", "_$createdAt");
		storageService.setOtherMetaHeaders(metaHeaders);
		storageService.findAllDocuments(getString(R.string.database_name),
				sp.getString("userEmail", null) + "_transactions",
				new App42CallBack() {
					public void onSuccess(Object response) {
						Storage storage = (Storage) response;
						ArrayList<Storage.JSONDocument> jsonDocList = storage
								.getJsonDocList();
						jsonDocArray = new ArrayList<String>();
						jsonIdArray = new ArrayList<String>();

						for (int i = 0; i < jsonDocList.size(); i++) {
							jsonDocArray.add(jsonDocList.get(i).getJsonDoc());
							jsonIdArray.add(jsonDocList.get(i).getDocId());
						}
					}

					public void onException(Exception ex) {
						System.out.println("Exception Message"
								+ ex.getMessage());
						errorHandler(ex);
					}
				});
	}

	public void initPagerView(int position, final View view) {
		listView = (XListView) findViewById(R.id.lvListItems);
		listView.setPullLoadEnable(false);
		listView.setPullRefreshEnable(false);
		listView.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub

			}
		});
		;

		switch (position) {
		case 0:
			ArrayList<ReportListItem> transactions = new ArrayList<ReportListItem>();

			for (int i = 0; i < jsonDocArray.size(); i++) {
				try {
					JSONObject json = new JSONObject(jsonDocArray.get(i));
					transactions.add(new ReportListItem(json.getString("date"),
							"Total sold: " + json.getString("totalCost")
									+ " via "
									+ json.getString("transactionType"),
							"Quantity: " + json.getString("numberOfItems")));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			Collections.reverse(transactions);

			adapter = new ReportListItemAdapter(getApplicationContext(),
					R.layout.inventory_list_item, transactions);
			listView.setAdapter(adapter);
			listView.stopRefresh();
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					i = new Intent(getApplicationContext(), Chart.class);
					Bundle b = new Bundle();
					b.putString("title", "Transaction trend");
					i.putExtras(b);
					startActivity(i);
				}
			});
			break;
		case 1:
			ArrayList<ReportListItem> dailyReports = new ArrayList<ReportListItem>();
			dailyReports.add(new ReportListItem("Mon 12-4-15",
					"Total sold: Ksh.56,870", "Quantity: 96 items"));
			dailyReports.add(new ReportListItem("Tue 12-4-15",
					"Total sold: Ksh.56,870", "Quantity: 96 items"));
			dailyReports.add(new ReportListItem("Wed 2-4-15",
					"Total sold: Ksh.56,870", "Quantity: 96 items"));
			dailyReports.add(new ReportListItem("Thur 12-4-15",
					"Total sold: Ksh.56,870", "Quantity: 96 items"));

			adapter = new ReportListItemAdapter(getApplicationContext(),
					R.layout.inventory_list_item, dailyReports);
			listView.setAdapter(adapter);
			listView.stopRefresh();
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					i = new Intent(getApplicationContext(), Chart.class);
					Bundle b = new Bundle();
					b.putString("title", "Daily trend");
					i.putExtras(b);
					startActivity(i);
				}
			});
			break;
		case 2:
			ArrayList<ReportListItem> weeklyReports = new ArrayList<ReportListItem>();
			weeklyReports.add(new ReportListItem("Week 11",
					"Total sold: Ksh.56,870", "Quantity: 196 items"));
			weeklyReports.add(new ReportListItem("Week 12",
					"Total sold: Ksh.56,870", "Quantity: 196 items"));
			weeklyReports.add(new ReportListItem("Week 13",
					"Total sold: Ksh.56,870", "Quantity: 196 items"));
			weeklyReports.add(new ReportListItem("Week 14",
					"Total sold: Ksh.56,870", "Quantity: 196 items"));

			adapter = new ReportListItemAdapter(getApplicationContext(),
					R.layout.inventory_list_item, weeklyReports);
			listView.setAdapter(adapter);
			listView.stopRefresh();
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					i = new Intent(getApplicationContext(), Chart.class);
					Bundle b = new Bundle();
					b.putString("title", "Weekly trend");
					i.putExtras(b);
					startActivity(i);
				}
			});
			break;
		case 3:
			ArrayList<ReportListItem> monthlyReports = new ArrayList<ReportListItem>();
			monthlyReports.add(new ReportListItem("Mar",
					"Total sold: Ksh.56,870", "Quantity: 196 items"));
			monthlyReports.add(new ReportListItem("Apr",
					"Total sold: Ksh.56,870", "Quantity: 196 items"));
			monthlyReports.add(new ReportListItem("May",
					"Total sold: Ksh.56,870", "Quantity: 196 items"));
			monthlyReports.add(new ReportListItem("Jun",
					"Total sold: Ksh.56,870", "Quantity: 196 items"));

			adapter = new ReportListItemAdapter(getApplicationContext(),
					R.layout.inventory_list_item, monthlyReports);
			listView.setAdapter(adapter);
			listView.stopRefresh();
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					i = new Intent(getApplicationContext(), Chart.class);
					Bundle b = new Bundle();
					b.putString("title", "Monthly trend");
					i.putExtras(b);
					startActivity(i);
				}
			});
			break;
		}
	}

	private void errorHandler(Exception ex) {
		// TODO Auto-generated method stub
		if (ex.getMessage().contains("refused")
				|| ex.getMessage().contains("UnknownHostException")
				|| ex.getMessage().contains("SSL")
				|| ex.getMessage().contains("ConnectTimeoutException")
				|| ex.getMessage().contains("Neither")
				|| ex.getMessage().contains("Socket")) {
			error = "No internet connection :-(";
		} else if (ex.getMessage().contains("No document")) {
			error = "User not found!";
		} else {
			error = ex.getMessage();
		}

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				setSupportProgressBarIndeterminateVisibility(false);
				Toast.makeText(getApplicationContext(), error,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void setAdapter() {
		// TODO Auto-generated method stub
		ReportsFragmentAdapter adapter = new ReportsFragmentAdapter(
				Reports.this);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(adapter);
		mPager.setCurrentItem(0);
		mPager.setOffscreenPageLimit(3);

		mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.reports_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.mExport:

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

}