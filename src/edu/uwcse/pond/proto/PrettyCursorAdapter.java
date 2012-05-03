package edu.uwcse.pond.proto;

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