package com.toe.lipaplus;

public class ReportListItem {
	private String name;
	private String description;
	private String price;

	public ReportListItem(String name, String description, String price) {
		this.name = name;
		this.description = description;
		this.setPrice(price);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

}