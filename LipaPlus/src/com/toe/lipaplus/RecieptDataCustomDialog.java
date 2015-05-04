package com.toe.lipaplus;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;

public class RecieptDataCustomDialog extends Dialog {

	public SherlockFragmentActivity activity;
	public Button bDone;
	public TextView tvTitle;
	public EditText etEmailAddress, etPhoneNumber;
	public String title, message;

	public RecieptDataCustomDialog(SherlockFragmentActivity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
		this.activity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature((int) Window.FEATURE_NO_TITLE);
		setContentView(R.layout.receipt_data_custom_dialog);

		Typeface font = Typeface.createFromAsset(activity.getAssets(),
				activity.getString(R.string.font));

		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setTypeface(font);
		tvTitle.setText(title);

		etEmailAddress = (EditText) findViewById(R.id.etEmailAddress);
		etEmailAddress.setTypeface(font);
		etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
		etPhoneNumber.setTypeface(font);

		bDone = (Button) findViewById(R.id.bDone);
	}
}
