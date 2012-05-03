/*
 * Copyright (C) 2012 University of Washington
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package edu.uwcse.pond.adapters;

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
import edu.uwcse.pond.proto.R;

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