package com.toe.lipaplus;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.toe.lipaplus.XListView.IXListViewListener;
import com.viewpagerindicator.PageIndicator;

public class Checkout extends SherlockFragmentActivity {

	Intent i;
	PageIndicator mIndicator;
	ListItemAdapter adapter;
	XListView listView;
	SherlockFragmentActivity activity;
	ArrayList<String> productNames = new ArrayList<String>();
	ArrayList<String> productDescriptions = new ArrayList<String>();
	ArrayList<String> productPrices = new ArrayList<String>();
	SharedPreferences sp;
	float totalCost = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.list_view);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setSupportProgressBarIndeterminateVisibility(false);

		setUp();
		populateCheckoutBasket();
	}

	private void setUp() {
		// TODO Auto-generated method stub
		activity = this;
		sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
	}

	@SuppressWarnings("unchecked")
	private void getCheckoutItems() {
		// TODO Auto-generated method stub
		try {
			productNames = (ArrayList<String>) ObjectSerializer
					.deserialize(sp.getString("namesCheckout",
							ObjectSerializer.serialize(new ArrayList<String>())));
			productDescriptions = (ArrayList<String>) ObjectSerializer
					.deserialize(sp
							.getString("descriptionsCheckout", ObjectSerializer
									.serialize(new ArrayList<String>())));
			productPrices = (ArrayList<String>) ObjectSerializer
					.deserialize(sp
							.getString("pricesCheckout", ObjectSerializer
									.serialize(new ArrayList<String>())));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void populateCheckoutBasket() {
		// TODO Auto-generated method stub
		getCheckoutItems();
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ArrayList<ListItem> inventoryItems = new ArrayList<ListItem>();

				for (int i = 0; i < productNames.size(); i++) {
					inventoryItems.add(new ListItem(productNames.get(i),
							productDescriptions.get(i), productPrices.get(i)));
				}

				for (int i = 0; i < productPrices.size(); i++) {
					totalCost += Float.parseFloat(productPrices.get(i));
				}

				getSupportActionBar().setTitle("Total cost: " + totalCost);
				getSupportActionBar().setSubtitle(
						productPrices.size() + " items to checkout");

				adapter = new ListItemAdapter(getApplicationContext(),
						R.layout.inventory_list_item, inventoryItems);
				listView = (XListView) findViewById(R.id.lvListItems);
				listView.setPullLoadEnable(false);
				listView.setPullRefreshEnable(false);
				listView.setXListViewListener(new IXListViewListener() {

					@Override
					public void onRefresh() {
						// TODO Auto-generated method stub
						totalCost = 0;
						populateCheckoutBasket();
					}

					@Override
					public void onLoadMore() {
						// TODO Auto-generated method stub

					}
				});
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						productNames.remove(arg2 - 1);
						productDescriptions.remove(arg2 - 1);
						productPrices.remove(arg2 - 1);

						saveNewCheckout(productNames, productDescriptions,
								productPrices);
					}
				});
				setSupportProgressBarIndeterminateVisibility(false);
				listView.stopRefresh();
			}
		});
	}

	private void saveNewCheckout(ArrayList<String> productNames,
			ArrayList<String> productDescriptions,
			ArrayList<String> productPrices) {
		// TODO Auto-generated method stub
		try {
			sp.edit()
					.putString("namesCheckout",
							ObjectSerializer.serialize(productNames)).commit();
			sp.edit()
					.putString("descriptionsCheckout",
							ObjectSerializer.serialize(productDescriptions))
					.commit();
			sp.edit()
					.putString("pricesCheckout",
							ObjectSerializer.serialize(productPrices)).commit();
		} catch (IOException e) {
			e.printStackTrace();
		}

		totalCost = 0;
		populateCheckoutBasket();
		Toast.makeText(getApplicationContext(), "Item deleted",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.checkout_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.mPay:
			i = new Intent(getApplicationContext(), Payment.class);
			Bundle b = new Bundle();
			b.putStringArrayList("productNames", productNames);
			b.putStringArrayList("productDescriptions", productDescriptions);
			b.putStringArrayList("productPrices", productPrices);
			i.putExtras(b);
			startActivity(i);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try {
			sp.edit()
					.putString("namesCheckout",
							ObjectSerializer.serialize(new ArrayList<String>()))
					.commit();
			sp.edit()
					.putString("descriptionsCheckout",
							ObjectSerializer.serialize(new ArrayList<String>()))
					.commit();
			sp.edit()
					.putString("pricesCheckout",
							ObjectSerializer.serialize(new ArrayList<String>()))
					.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Toast.makeText(getApplicationContext(), "Checkout list cleared",
				Toast.LENGTH_SHORT).show();
		finish();
	}
}