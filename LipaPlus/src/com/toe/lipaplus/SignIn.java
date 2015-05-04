package com.toe.lipaplus;

import java.util.HashMap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.user.UserService;

public class SignIn extends SherlockFragmentActivity {

	EditText etEmailAddress, etPassword;
	Button bSignInUser;
	TextView tvForgotPassword;
	Intent i;
	boolean dataIsValid = false;
	SharedPreferences sp;
	UserService userService;
	String error;
	PasswordRecoveryCustomDialog prcDialog;
	SherlockFragmentActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_in);
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
	}

	private void setUp() {
		// TODO Auto-generated method stub
		activity = this;
		sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);
		etEmailAddress = (EditText) findViewById(R.id.etEmailAddress);
		etPassword = (EditText) findViewById(R.id.etPassword);

		bSignInUser = (Button) findViewById(R.id.bSignInUser);
		bSignInUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String emailAddress = etEmailAddress.getText().toString()
						.trim();
				String password = etPassword.getText().toString().trim();

				if (password.length() > 6) {
					etPassword.setError(null);
					dataIsValid = true;
				} else {
					etPassword
							.setError("Your password must have at least 6 characters");
					dataIsValid = false;
				}

				if (dataIsValid) {
					signInUser(emailAddress, password);
				}
			}
		});

		tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
		tvForgotPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				prcDialog = new PasswordRecoveryCustomDialog(activity);
				prcDialog.getWindow().setBackgroundDrawable(
						new ColorDrawable(Color.TRANSPARENT));
				prcDialog.title = "Recover your password";
				prcDialog.show();
				prcDialog.bDone.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String businessName = prcDialog.etBusinessName
								.getText().toString().trim();

						if (businessName.length() > 3) {
							userService.resetUserPassword(businessName,
									new App42CallBack() {

										@Override
										public void onSuccess(Object arg0) {
											// TODO Auto-generated method stub
											Toast.makeText(
													getApplicationContext(),
													"Please check your email for a new password",
													Toast.LENGTH_LONG).show();
											prcDialog.dismiss();
										}

										@Override
										public void onException(Exception arg0) {
											// TODO Auto-generated method stub
											errorHandler(arg0);
										}
									});
						} else {
							prcDialog.etBusinessName
									.setError("Your business's name must have at least 3 characters");
						}
					}
				});
			}
		});
	}

	protected void signInUser(final String emailAddress, String password) {
		// TODO Auto-generated method stub
		HashMap<String, String> otherMetaHeaders = new HashMap<String, String>();
		otherMetaHeaders.put("emailAuth", "true");
		userService.setOtherMetaHeaders(otherMetaHeaders);
		userService.authenticate(emailAddress, password, new App42CallBack() {
			public void onSuccess(Object response) {
				sp.edit().putBoolean("registered", true).commit();
				sp.edit().putString("userEmail", emailAddress).commit();
				i = new Intent(getApplicationContext(), Inventory.class);
				startActivity(i);
			}

			public void onException(Exception ex) {
				System.out.println("Exception Message : " + ex.getMessage());
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
