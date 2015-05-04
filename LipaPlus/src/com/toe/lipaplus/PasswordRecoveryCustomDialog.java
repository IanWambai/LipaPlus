package com.toe.lipaplus;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;

public class PasswordRecoveryCustomDialog extends Dialog {

	public SherlockFragmentActivity activity;
	public Button bDone;
	public TextView tvTitle;
	public EditText etBusinessName;
	public String title, message;

	public PasswordRecoveryCustomDialog(SherlockFragmentActivity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
		this.activity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature((int) Window.FEATURE_NO_TITLE);
		setContentView(R.layout.password_recovery_custom_dialog);

		Typeface font = Typeface.createFromAsset(activity.getAssets(),
				activity.getString(R.string.font));

		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setTypeface(font);
		tvTitle.setText(title);

		etBusinessName = (EditText) findViewById(R.id.etBusinessName);
		etBusinessName.setTypeface(font);

		bDone = (Button) findViewById(R.id.bDone);
	}
}
