package com.robert.starsproject;

import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.ToggleButton;

public class DrawerItem {

	String ItemName;
	int imgResID;
	String id;
	CheckBox toggle;
	NumberPicker picker;
	

	//Header or item
	public DrawerItem(String ItemName, String id) {
		this.ItemName = ItemName;
		this.id = id;
		
	}
	
	//Item with image
	public DrawerItem(String itemName, String id, int imgResID) {
		ItemName = itemName;
		this.id = id;
		this.imgResID = imgResID;
	}
	
	//Toggle
	public DrawerItem(String ItemName, String id, CheckBox toggle) {
		this.ItemName = ItemName;
		this.id = id;
		this.toggle = toggle;
	}
	
	public DrawerItem(String ItemName, String id, NumberPicker picker) {
		this.ItemName = ItemName;
		this.id = id;
		this.picker = picker;
	}

	public String getItemName() {
		return ItemName;
	}

	public int getImgResID() {
		return imgResID;
	}
	
	public String getId() {
		return id;
	}
	
	public CheckBox getToggle() {
		return toggle;
	}
	
	public NumberPicker getNumberPicker() {
		return picker;
	}
	

}
