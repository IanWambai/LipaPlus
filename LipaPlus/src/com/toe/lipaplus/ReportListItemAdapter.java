package com.toe.lipaplus;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ReportListItemAdapter extends ArrayAdapter<ReportListItem> {
	private ArrayList<ReportListItem> objects;

	public ReportListItemAdapter(Context context, int textViewResourceId,
			ArrayList<ReportListItem> objects) {
		super(context, textViewResourceId, objects);
		this.objects = objects;
	}

	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.inventory_list_item, null);
		}

		ReportListItem i = objects.get(position);
		if (i != null) {

			TextView tvName = (TextView) v.findViewById(R.id.itemName);
			TextView tvDescription = (TextView) v
					.findViewById(R.id.itemDescription);
			TextView tvPrice = (TextView) v.findViewById(R.id.itemPrice);

			if (tvName != null) {
				tvName.setText(i.getName());
			}
			if (tvDescription != null) {
				tvDescription.setText(i.getDescription());
			}
			if (tvPrice != null) {
				tvPrice.setText(i.getPrice());
			}

		}
		return v;
	}
}