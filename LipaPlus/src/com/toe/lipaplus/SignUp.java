package com.toe.lipaplus;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;
import com.shephertz.app42.paas.sdk.android.user.UserService;

public class SignUp extends SherlockActivity {

	EditText etBusinessName, etEmailAddress, etPhoneNumber, etPassword;
	Button bSignUpUser;
	Intent i;
	SharedPreferences sp;
	UserService userService;
	StorageService storageService;
	String error;
	boolean dataIsValid = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		initialize();
		setUp();
	}

	private void initialize() {
		// TODO Auto-generated method stub
		App42API.initialize(getApplicationContext(),
				getString(R.string.api_key), getString(R.string.secret_key));
		userService = App42API.buildUserService();
		storageService = App42API.buildStorageService();
	}

	private void setUp() {
		// TODO Auto-generated method stub
		sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);

		etBusinessName = (EditText) findViewById(R.id.etBusinessName);
		etEmailAddress = (EditText) findViewById(R.id.etEmailAddress);
		etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
		etPassword = (EditText) findViewById(R.id.etPassword);

		bSignUpUser = (Button) findViewById(R.id.bSignUpUser);
		bSignUpUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String businessName = etBusinessName.getText().toString()
						.trim();
				String emailAddress = etEmailAddress.getText().toString()
						.trim();
				String phoneNumber = etPhoneNumber.getText().toString().trim();
				String password = etPassword.getText().toString().trim();

				if (businessName.length() > 3) {
					etBusinessName.setError(null);
					dataIsValid = true;
				} else {
					etBusinessName
							.setError("Your business's name should have at least 3 characters");
					dataIsValid = false;
				}

				if (phoneNumber.length() > 5 && phoneNumber.startsWith("+254")) {
					etPhoneNumber.setError(null);
					dataIsValid = true;
				} else {
					etPhoneNumber
							.setError("Please enter a proper phone number. Start with +254");
					dataIsValid = false;
				}

				if (password.length() > 5) {
					etPassword.setError(null);
					dataIsValid = true;
				} else {
					etPassword
							.setError("Your password should have at least 6 characters");
					dataIsValid = false;
				}

				if (dataIsValid) {
					signUpUser(businessName, emailAddress, phoneNumber,
							password);
				} else {
					Toast.makeText(getApplicationContext(),
							"Please check your input", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}

	private void signUpUser(final String businessName,
			final String emailAddress, final String phoneNumber, String password) {
		// TODO Auto-generated method stub
		userService.createUser(businessName, password, emailAddress,
				new App42CallBack() {
					public void onSuccess(Object response) {
						storeUser(businessName, emailAddress, phoneNumber);
					}

					public void onException(Exception ex) {
						System.out.println("Exception Message"
								+ ex.getMessage());
						errorHandler(ex);
					}
				});
	}

	private void storeUser(String businessName, final String emailAddress,
			String phoneNumber) {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		try {
			json.put("name", businessName);
			json.put("email", emailAddress);
			json.put("phoneNumber", phoneNumber);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		storageService.insertJSONDocument(getString(R.string.database_name),
				getString(R.string.collection_name), json, new App42CallBack() {
					public void onSuccess(Object response) {
						sp.edit().putBoolean("registered", true).commit();
						sp.edit().putString("userEmail", emailAddress).commit();
						Toast.makeText(getApplicationContext(),
								"Registration done!", Toast.LENGTH_SHORT)
								.show();
						i = new Intent(getApplicationContext(), Inventory.class);
						startActivity(i);
					}

					public void onException(Exception ex) {
						System.out.println("Exception Message"
								+ ex.getMessage());
						errorHandler(ex);
					}
				});
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
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			i = new Intent(getApplicationContext(), Welcome.class);
			startActivity(i);
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
