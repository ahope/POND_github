package edu.uwcse.pond.proto;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
import edu.uwcse.pond.nutrition.Consts.PointComponent;

public class PrettyComponentArrayAdapter extends ArrayAdapter<PointComponent>{
	public PrettyComponentArrayAdapter(Context context, List<PointComponent> data) {
		super(context, android.R.layout.simple_spinner_item, data);
		
	}

	/*
	public PrettyCursorAdapter(Context context, T[] spinnerItems){
		super(context, android.R.layout.simple_spinner_item, spinnerItems);
	}
	*/
	
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = super.getView(position, convertView, parent);
		TextView txt = (TextView)view.findViewById(android.R.id.text1);
		//txt.setTextColor(Color.WHITE);
		txt.setTextAppearance(view.getContext(), R.style.locationSpinnerTextStyle);
		//txt.setBackgroundColor(Color.GREEN);
		
		return view;
	}
	
	@Override
	public View getDropDownView(int position, View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater)getContext().getSystemService
				      (Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(
					R.layout.pretty_spinner_drop_down_item, null);
		}
		
		CheckedTextView txtViewTitle;
		ImageView imgView; 
		
		// Set the component name
		txtViewTitle = (CheckedTextView) convertView
				.findViewById(R.id.item_CheckedTextView);
		PointComponent thisOne = getItem(position);
		txtViewTitle.setText(thisOne.getDesc());
		txtViewTitle.setTextAppearance(getContext(), R.style.locationSpinnerTextStyle);
		
		imgView = (ImageView)convertView.findViewById(R.id.imageView1);
		imgView.setImageResource(thisOne.getDrawableId());
		
		//if (position == this.)

		return convertView;
	}
}