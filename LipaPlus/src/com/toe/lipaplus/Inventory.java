package com.toe.lipaplus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.view.Window;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;
import com.toe.lipaplus.XListView.IXListViewListener;
import com.viewpagerindicator.PageIndicator;

public class Inventory extends SherlockFragmentActivity {

	Intent i;
	PageIndicator mIndicator;
	ListItemAdapter adapter;
	XListView listView;
	SherlockFragmentActivity activity;
	InventoryDataCustomDialog icdDialog;
	SharedPreferences sp;
	boolean dataIsValid = false;
	ArrayList<String> productNamesCheckout = new ArrayList<String>();
	ArrayList<String> productDescriptionsCheckout = new ArrayList<String>();
	ArrayList<String> productPricesCheckout = new ArrayList<String>();
	ArrayList<String> jsonDocArray, jsonIdArray;
	ArrayList<ListItem> inventoryItems;
	StorageService storageService;
	String error;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.list_view);
		getSupportActionBar().setHomeButtonEnabled(true);
		setSupportProgressBarIndeterminateVisibility(false);

		initialize();
		setUp();
		getInventory();
	}

	private void initialize() {
		// TODO Auto-generated method stub
		App42API.initialize(getApplicationContext(),
				getString(R.string.api_key), getString(R.string.secret_key));
		storageService = App42API.buildStorageService();
	}

	private void setUp() {
		// TODO Auto-generated method stub
		activity = this;
		sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
	}

	private void getInventory() {
		// TODO Auto-generated method stub
		setSupportProgressBarIndeterminateVisibility(true);

		HashMap<String, String> metaHeaders = new HashMap<String, String>();
		metaHeaders.put("orderByDescending", "_$createdAt");
		storageService.setOtherMetaHeaders(metaHeaders);
		storageService.findAllDocuments(getString(R.string.database_name),
				sp.getString("userEmail", null) + "_inventory",
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

						populateInventory(jsonDocArray, jsonIdArray);
					}

					public void onException(Exception ex) {
						System.out.println("Exception Message"
								+ ex.getMessage());
						errorHandler(ex);
					}
				});
	}

	protected void populateInventory(ArrayList<String> jsonDocArray,
			final ArrayList<String> jsonIdArray) {
		// TODO Auto-generated method stub
		inventoryItems = new ArrayList<ListItem>();

		for (int i = 0; i < jsonDocArray.size(); i++) {
			try {
				JSONObject json = new JSONObject(jsonDocArray.get(i));
				inventoryItems.add(new ListItem(json.getString("productName"),
						json.getString("productDescription"), json
								.getString("productPrice")));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				adapter = new ListItemAdapter(getApplicationContext(),
						R.layout.inventory_list_item, inventoryItems);
				listView = (XListView) findViewById(R.id.lvListItems);
				listView.setPullLoadEnable(false);
				listView.setXListViewListener(new IXListViewListener() {

					@Override
					public void onRefresh() {
						// TODO Auto-generated method stub
						getInventory();
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
						String productName = inventoryItems.get(arg2 - 1)
								.getName();
						String productDescription = inventoryItems
								.get(arg2 - 1).getDescription();
						String productPrice = inventoryItems.get(arg2 - 1)
								.getPrice();

						addItemToCheckout(productName, productDescription,
								productPrice);
					}
				});
				listView.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						// TODO Auto-generated method stub
						storageService.deleteDocumentById(
								getString(R.string.database_name),
								sp.getString("userEmail", null),
								jsonIdArray.get(arg2 - 1), new App42CallBack() {
									public void onSuccess(Object response) {
										runOnUiThread(new Runnable() {

											@Override
											public void run() {
												// TODO Auto-generated method
												// stub
												Toast.makeText(
														getApplicationContext(),
														"Item deleted",
														Toast.LENGTH_SHORT)
														.show();
											}
										});
									}

									public void onException(Exception ex) {
										System.out.println("Exception Message"
												+ ex.getMessage());
										errorHandler(ex);
									}
								});
						return false;
					}
				});
				setSupportProgressBarIndeterminateVisibility(false);
				listView.stopRefresh();
			}
		});
	}

	private void saveInventoryItem(String productName,
			String productDescription, String productPrice) {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		try {
			json.put("productName", productName);
			json.put("productDescription", productDescription);
			json.put("productPrice", productPrice);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		storageService.insertJSONDocument(getString(R.string.database_name),
				sp.getString("userEmail", null) + "_inventory", json,
				new App42CallBack() {
					public void onSuccess(Object response) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								icdDialog.dismiss();
								Toast.makeText(getApplicationContext(),
										"New item created!", Toast.LENGTH_SHORT)
										.show();
								getInventory();
							}
						});
					}

					public void onException(Exception ex) {
						System.out.println("Exception Message"
								+ ex.getMessage());
						errorHandler(ex);
					}
				});
	}

	@SuppressWarnings("unchecked")
	private void addItemToCheckout(String productName,
			String productDescription, String productPrice) {
		// TODO Auto-generated method stub
		try {
			productNamesCheckout = (ArrayList<String>) ObjectSerializer
					.deserialize(sp.getString("namesCheckout",
							ObjectSerializer.serialize(new ArrayList<String>())));
			productDescriptionsCheckout = (ArrayList<String>) ObjectSerializer
					.deserialize(sp
							.getString("descriptionsCheckout", ObjectSerializer
									.serialize(new ArrayList<String>())));
			productPricesCheckout = (ArrayList<String>) ObjectSerializer
					.deserialize(sp
							.getString("pricesCheckout", ObjectSerializer
									.serialize(new ArrayList<String>())));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (productName != null && productDescription != null
				&& productPrice != null) {
			try {
				productNamesCheckout.add(productName);
				productDescriptionsCheckout.add(productDescription);
				productPricesCheckout.add(productPrice);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.toString());
			}
		}

		try {
			sp.edit()
					.putString("namesCheckout",
							ObjectSerializer.serialize(productNamesCheckout))
					.commit();
			sp.edit()
					.putString(
							"descriptionsCheckout",
							ObjectSerializer
									.serialize(productDescriptionsCheckout))
					.commit();
			sp.edit()
					.putString("pricesCheckout",
							ObjectSerializer.serialize(productPricesCheckout))
					.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Toast.makeText(getApplicationContext(),
				productName + " added to checkout!", Toast.LENGTH_SHORT).show();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.inventory_menu, menu);

		SubMenu subMenu = menu.addSubMenu("Options");
		subMenu.add(0, 0, 0, "Menu:");
		subMenu.add(1, 1, 1, "Reports");
		subMenu.add(2, 2, 2, "Settings");
		subMenu.add(3, 3, 3, "Export");
		subMenu.add(4, 4, 4, "Help");

		MenuItem subMenuItem = subMenu.getItem();
		subMenuItem.setIcon(android.R.drawable.ic_menu_more);
		subMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
				| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return super.onCreateOptionsMenu(menu);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			i = new Intent(getApplicationContext(), About.class);
			startActivity(i);
			break;
		case R.id.mAddItem:
			icdDialog = new InventoryDataCustomDialog(activity);
			icdDialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));
			icdDialog.title = "New inventory item";
			icdDialog.show();
			icdDialog.bDone.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String productName = icdDialog.etProductName.getText()
							.toString().trim();
					String productDescription = icdDialog.etProductDescription
							.getText().toString().trim();
					String productPrice = icdDialog.etProductPrice.getText()
							.toString().trim();

					if (productName.length() > 3) {
						icdDialog.etProductName.setError(null);
						dataIsValid = true;
					} else {
						icdDialog.etProductName
								.setError("Your product's name should have at least 3 characters");
						dataIsValid = false;
					}

					if (productDescription.length() > 5) {
						icdDialog.etProductDescription.setError(null);
						dataIsValid = true;
					} else {
						icdDialog.etProductDescription
								.setError("Your product's description should have at least 5 characters");
						dataIsValid = false;
					}

					if (productPrice.length() > 0) {
						icdDialog.etProductPrice.setError(null);
						dataIsValid = true;
					} else {
						icdDialog.etProductPrice
								.setError("Please enter a price");
						dataIsValid = false;
					}

					if (dataIsValid) {
						saveInventoryItem(productName, productDescription,
								productPrice);
					}
				}
			});
			break;
		case R.id.mCheckout:
			try {
				ArrayList<String> testProductNames = (ArrayList<String>) ObjectSerializer
						.deserialize(sp.getString("namesCheckout",
								ObjectSerializer
										.serialize(new ArrayList<String>())));
				if (testProductNames != null && testProductNames.size() > 0) {
					i = new Intent(getApplicationContext(), Checkout.class);
					startActivity(i);
				} else {
					Toast.makeText(getApplicationContext(),
							"The checkout list is empty", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 1:
			i = new Intent(getApplicationContext(), Reports.class);
			startActivity(i);
			break;
		case 2:
			i = new Intent(getApplicationContext(), Preferences.class);
			startActivity(i);
			break;
		case 3:
			Toast.makeText(getApplicationContext(), "Export as CSV",
					Toast.LENGTH_SHORT).show();
			break;
		case 4:
			i = new Intent(getApplicationContext(), Help.class);
			startActivity(i);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}