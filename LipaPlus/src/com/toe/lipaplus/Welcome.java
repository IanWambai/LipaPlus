package com.toe.lipaplus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class Welcome extends SherlockActivity {

	Intent i;
	Button bSignIn, bSignUp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.welcome);
		getSupportActionBar().hide();

		Intent i = new Intent(getApplicationContext(), Inventory.class);
		startActivity(i);
		// checkUserRegistration();
		setUp();
	}

	private void checkUserRegistration() {
		// TODO Auto-generated method stub
		SharedPreferences sp = this.getSharedPreferences(getPackageName(),
				MODE_PRIVATE);
		Boolean registered = sp.getBoolean("registered", false);
		if (registered == true) {
			Intent i = new Intent(getApplicationContext(), Inventory.class);
			startActivity(i);
		}
	}

	private void setUp() {
		// TODO Auto-generated method stub
		Typeface font = Typeface.createFromAsset(getAssets(), getResources()
				.getString(R.string.font));

		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setTypeface(font);

		bSignIn = (Button) findViewById(R.id.bSignIn);
		bSignIn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				i = new Intent(getApplicationContext(), SignIn.class);
				startActivity(i);
			}
		});

		bSignUp = (Button) findViewById(R.id.bSignUp);
		bSignUp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				i = new Intent(getApplicationContext(), SignUp.class);
				startActivity(i);
			}
		});
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}
}
