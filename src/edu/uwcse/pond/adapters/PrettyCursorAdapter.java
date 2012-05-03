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

import edu.uwcse.pond.proto.R;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class PrettyCursorAdapter extends SimpleCursorAdapter{
	public PrettyCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		// TODO Auto-generated constructor stub
	}

	/*
	public PrettyCursorAdapter(Context context, T[] spinnerItems){
		super(context, android.R.layout.simple_spinner_item, spinnerItems);
	}
	*/
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		super.bindView(view, context, cursor);
		TextView txt = (TextView)view.findViewById(android.R.id.text1);
		//txt.setTextColor(Color.MAGENTA);
		//txt.setBackgroundColor(Color.GREEN);
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		//return super.newView(context, cursor, parent);
		
		View view = super.newView(context, cursor, parent);
		//TextView txt = (TextView)view.findViewById(android.R.id.text1);
		//txt.setTextColor(Color.MAGENTA);
		//txt.setBackgroundColor(Color.GREEN);
		return view;
	}
	
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
	
}