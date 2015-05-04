package com.toe.lipaplus;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;

public class InventoryDataCustomDialog extends Dialog {

	public SherlockFragmentActivity activity;
	public Button bDone;
	public TextView tvTitle;
	public EditText etProductName, etProductDescription, etProductPrice;
	public String title, message;

	public InventoryDataCustomDialog(SherlockFragmentActivity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
		this.activity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature((int) Window.FEATURE_NO_TITLE);
		setContentView(R.layout.inventory_data_custom_dialog);

		Typeface font = Typeface.createFromAsset(activity.getAssets(),
				activity.getString(R.string.font));

		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setTypeface(font);
		tvTitle.setText(title);

		etProductName = (EditText) findViewById(R.id.etProductName);
		etProductName.setTypeface(font);
		etProductDescription = (EditText) findViewById(R.id.etProductDescription);
		etProductDescription.setTypeface(font);
		etProductPrice = (EditText) findViewById(R.id.etProductPrice);
		etProductPrice.setTypeface(font);

		bDone = (Button) findViewById(R.id.bDone);
	}
}
