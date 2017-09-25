package com.robert.starsproject;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.robert.starsproject.R;



public class CustomDrawerAdapter extends ArrayAdapter<DrawerItem> {

	Context context;
	List<DrawerItem> drawerItemList;
	int layoutResID;

	public CustomDrawerAdapter(Context context, int layoutResourceID,
			List<DrawerItem> listItems) {
		super(context, layoutResourceID, listItems);
		this.context = context;
		this.drawerItemList = listItems;
		this.layoutResID = layoutResourceID;

	}
	
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		DrawerItemHolder drawerHolder;
		View view = convertView;
		
		if (view == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			drawerHolder = new DrawerItemHolder();

			view = inflater.inflate(layoutResID, parent, false);
			
			//((ViewGroup) view.getParent()).removeView(view);
			
			drawerHolder.headerLayout = (LinearLayout) view
					.findViewById(R.id.header_layout);
			drawerHolder.itemLayout = (LinearLayout) view
					.findViewById(R.id.item_layout);
			drawerHolder.itemWithPicLayout = (LinearLayout) view
					.findViewById(R.id.item_with_pic_layout);
			drawerHolder.toggleLayout = (LinearLayout) view
					.findViewById(R.id.toggle_layout);
			drawerHolder.pickerLayout = (LinearLayout) view
					.findViewById(R.id.picker_layout);

			
			drawerHolder.header = (TextView) view.
					findViewById(R.id.header_title);
			drawerHolder.itemName = (TextView) view
					.findViewById(R.id.item_name);
			drawerHolder.itemWithPicName = (TextView) view
					.findViewById(R.id.item_with_pic_name);
			drawerHolder.icon = (ImageView) view.
					findViewById(R.id.item_with_pic_icon);
			drawerHolder.toggleName = (TextView) view
					.findViewById(R.id.toggle_name);
			drawerHolder.pickerName = (TextView) view
					.findViewById(R.id.picker_name);
			

			//drawerHolder.toggle = (ToggleButton) view
				//	.findViewById(R.id.toggle_button);
		//	drawerHolder.picker = (NumberPicker) view
			//		.findViewById(R.id.picker);

		

			view.setTag(drawerHolder);

		} else {
			drawerHolder = (DrawerItemHolder) view.getTag();

		}
		
		DrawerItem dItem = (DrawerItem) this.drawerItemList.get(position);
		
		if (dItem.getId().equals("image")) {
		// ITEM WITH PIC
			drawerHolder.headerLayout.setVisibility(LinearLayout.INVISIBLE);
			drawerHolder.itemLayout.setVisibility(LinearLayout.INVISIBLE);
			drawerHolder.itemWithPicLayout.setVisibility(LinearLayout.VISIBLE);
			drawerHolder.toggleLayout.setVisibility(LinearLayout.INVISIBLE);
			drawerHolder.pickerLayout.setVisibility(LinearLayout.INVISIBLE);
		
			drawerHolder.itemWithPicName.setText(dItem.getItemName());
			drawerHolder.icon.setImageDrawable(view.getResources().getDrawable(
				dItem.getImgResID()));
		
		//Log.e("ADAPTER", "image");

		} else if (dItem.getId().equals("header")) {
			// HEADER
			drawerHolder.headerLayout.setVisibility(LinearLayout.VISIBLE);
			drawerHolder.itemLayout.setVisibility(LinearLayout.INVISIBLE);
			drawerHolder.itemWithPicLayout.setVisibility(LinearLayout.INVISIBLE);
			drawerHolder.toggleLayout.setVisibility(LinearLayout.INVISIBLE);
			drawerHolder.pickerLayout.setVisibility(LinearLayout.INVISIBLE);
			
			drawerHolder.header.setText(dItem.getItemName());
		//	Log.e("ADAPTER", "header");
			
		} else if (dItem.getId().equals("item")) {
			// ITEM
			drawerHolder.headerLayout.setVisibility(LinearLayout.INVISIBLE);
			drawerHolder.itemLayout.setVisibility(LinearLayout.VISIBLE);
			drawerHolder.itemWithPicLayout.setVisibility(LinearLayout.INVISIBLE);
			drawerHolder.toggleLayout.setVisibility(LinearLayout.INVISIBLE);
			drawerHolder.pickerLayout.setVisibility(LinearLayout.INVISIBLE);
			
			drawerHolder.itemName.setText(dItem.getItemName());
		//	Log.e("ADAPTER", "item");
			
			
		} else if (dItem.getId().equals("toggle")) {
			// ITEM
			drawerHolder.headerLayout.setVisibility(LinearLayout.INVISIBLE);
			drawerHolder.itemLayout.setVisibility(LinearLayout.INVISIBLE);
			drawerHolder.itemWithPicLayout.setVisibility(LinearLayout.INVISIBLE);
			drawerHolder.toggleLayout.setVisibility(LinearLayout.VISIBLE);
			drawerHolder.pickerLayout.setVisibility(LinearLayout.INVISIBLE);
			
			CheckBox toggle = dItem.getToggle();
			LinearLayout l = (LinearLayout) drawerHolder.toggleLayout.getChildAt(0);
			l.removeView(toggle);
			l.addView(toggle);
		
			
			drawerHolder.toggleName.setText(dItem.getItemName());

		} else {
			
			drawerHolder.headerLayout.setVisibility(LinearLayout.INVISIBLE);
			drawerHolder.itemLayout.setVisibility(LinearLayout.INVISIBLE);
			drawerHolder.itemWithPicLayout.setVisibility(LinearLayout.INVISIBLE);
			drawerHolder.toggleLayout.setVisibility(LinearLayout.INVISIBLE);
			drawerHolder.pickerLayout.setVisibility(LinearLayout.VISIBLE);
		
			NumberPicker picker = dItem.getNumberPicker();
			LinearLayout l = (LinearLayout) drawerHolder.pickerLayout.getChildAt(0);
			l.addView(picker);
		
			
			drawerHolder.pickerName.setText(dItem.getItemName());
			
		}
		return view;
	}
	

	public static class DrawerItemHolder {
		TextView itemName, header, itemWithPicName, toggleName, pickerName;
		ImageView icon;
		CheckBox toggle;
		NumberPicker picker;
		LinearLayout headerLayout, itemWithPicLayout, itemLayout, toggleLayout, pickerLayout;

	}
}