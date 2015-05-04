package com.toe.lipaplus;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class Help extends SherlockActivity {

	TextView tvTitle;
	ImageView ivTwitter, ivFacebook, ivEmail, ivWebsite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setUp();
	}

	private void setUp() {
		// TODO Auto-generated method stub
		Typeface font = Typeface.createFromAsset(getAssets(), getResources()
				.getString(R.string.font));

		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setTypeface(font);

		ivTwitter = (ImageView) findViewById(R.id.ivTwitter);
		ivTwitter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(getString(R.string.twitter_url)));
				startActivity(i);
			}
		});
		ivFacebook = (ImageView) findViewById(R.id.ivFacebook);
		ivFacebook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(getString(R.string.facebook_url)));
				startActivity(i);
			}
		});

		ivEmail = (ImageView) findViewById(R.id.ivEmail);
		ivEmail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri
						.fromParts("mailto",
								getString(R.string.lipa_plus_email), null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Lipa Plus: Help");
				startActivity(Intent
						.createChooser(emailIntent, "Send email..."));
			}
		});
		ivWebsite = (ImageView) findViewById(R.id.ivWebsite);
		ivWebsite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(getString(R.string.website_url)));
				startActivity(i);
			}
		});
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
