package edu.uwcse.pond.proto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import edu.uwcse.pond.diary.DiaryDbHelper;
import edu.uwcse.pond.diary.DiaryEntry;
import edu.uwcse.pond.diary.LocationDiaryTableHelper;
import edu.uwcse.pond.diary.PointsDiaryTableHelper;
import edu.uwcse.pond.nutrition.Consts.PointComponent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ParseException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class DailyEntriesListViewAdapter extends CursorAdapter {
	
	private SimpleDateFormat mDisplayDateFormat = new SimpleDateFormat("h:mm a"); 
	
	private PointComponent mPointComponentFilter = null; 
	
	private long[] mIds; 

	public DailyEntriesListViewAdapter(Context context, Cursor c) {
		super(context, c);

		mIds = new long[c.getCount()]; 
	}
	
	public void setPointComponentFilter(PointComponent pc){
		if (pc == PointComponent.ALL){
			clearPointComponentFilter();
		}
		else{
			mPointComponentFilter = pc;
		}
	}
	
	public void clearPointComponentFilter(){
		mPointComponentFilter = null; 

	
	}

	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		//return super.getItemId(position);
		Cursor c = getCursor();
		c.moveToPosition(position);
		return c.getLong(0);//c.getColumnIndex(PointsDiaryTableHelper.COL_ROWID));
	}
	

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		//fillView(cursor, view);
		fillView2(view, cursor);		
	}

	private void fillView2(View view, Cursor cursor) {
		
		long recId = cursor.getLong(cursor.getColumnIndex(PointsDiaryTableHelper.COL_ROWID));
		
		int srcCol = cursor.getColumnIndex(PointsDiaryTableHelper.COL_SOURCE);
		int source = cursor.getInt(srcCol);
		
		int locCol = cursor.getColumnIndex(PointsDiaryTableHelper.COL_LOCATION_ID);
		int loc = cursor.getInt(locCol);
		

		int locNameCol = cursor.getColumnIndex(LocationDiaryTableHelper.COL_LOC_NAME);
		String locName = cursor.getString(locNameCol);
		
		int timeCol = cursor.getColumnIndex(PointsDiaryTableHelper.COL_TIME_ENTERED);
		
		int commentCol = cursor.getColumnIndex(PointsDiaryTableHelper.COL_COMMENT);
		
		String comment = "no comment. ";
		if (commentCol >= 0){
			comment = cursor.getString(commentCol);
		}
		
		String time = cursor.getString(timeCol);
		Date date = new Date(); 
		try{
			date = DiaryDbHelper.DB_DATE_STORE_FORMAT.parse(time);
		}
		catch(IllegalArgumentException pe){
			date = new Date(); 
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/**
		 * Next set the name of the entry.
		 */

		TextView name_text = (TextView) view.findViewById(R.id.daily_entry_desc_TextView);

		if (name_text != null) {

		    name_text.setText(comment);

		}
		
		TextView time_text = (TextView) view.findViewById(R.id.daily_entry_time_TextView);
		if (time_text != null){
			time_text.setText(mDisplayDateFormat.format(date));
		}
		

		TextView detail_text = (TextView) view.findViewById(R.id.daily_entry_detail_TextView);
		if (detail_text != null){
			detail_text.setText("@" + locName);//comment);
		}
		LinearLayout layout = (LinearLayout)view.findViewById(R.id.linearLayout3);
		
		layout.removeAllViews();
		
		if (mPointComponentFilter == null){
		// Go through each column, determine if it's empty, and get the drawable from that. 
			populatePointsView(cursor, view.getContext(), layout, PointsDiaryTableHelper.COL_VEGGIE_WHOLE_VAL);

			populatePointsView(cursor, view.getContext(), layout, PointsDiaryTableHelper.COL_VEGGIE_VAL);
			populatePointsView(cursor, view.getContext(), layout, PointsDiaryTableHelper.COL_GRAINS_VAL);
			populatePointsView(cursor, view.getContext(), layout, PointsDiaryTableHelper.COL_GRAINS_WHOLE_VAL);
			
			populatePointsView(cursor, view.getContext(), layout, PointsDiaryTableHelper.COL_FRUIT_VAL);
			populatePointsView(cursor, view.getContext(), layout, PointsDiaryTableHelper.COL_FRUIT_WHOLE_VAL);

			populatePointsView(cursor, view.getContext(), layout, PointsDiaryTableHelper.COL_DAIRY_VAL);
			populatePointsView(cursor, view.getContext(), layout, PointsDiaryTableHelper.COL_PROTEIN_VAL);
			populatePointsView(cursor, view.getContext(), layout, PointsDiaryTableHelper.COL_OILS_VAL);
			populatePointsView(cursor, view.getContext(), layout, PointsDiaryTableHelper.COL_SOLID_FATS_VAL);
			populatePointsView(cursor, view.getContext(), layout, PointsDiaryTableHelper.COL_SODIUM_VAL);
			populatePointsView(cursor, view.getContext(), layout, PointsDiaryTableHelper.COL_SUGAR_VAL);
		}else{
			populatePointsView(cursor, view.getContext(), layout, mPointComponentFilter.getPtDbColName());
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		//Cursor c = getCursor();

		final LayoutInflater inflater = LayoutInflater.from(context);

		View v = inflater.inflate(R.layout.daily_entry_list_view_row, parent, false);

		fillView2(v, cursor);
		
		return v;

	}

	private void fillView(Cursor cursor, View v) {
		int srcCol = cursor.getColumnIndex(PointsDiaryTableHelper.COL_SOURCE);
		int source = cursor.getInt(srcCol);
		
		int locCol = cursor.getColumnIndex(PointsDiaryTableHelper.COL_LOCATION_ID);
		int loc = cursor.getInt(locCol);
		
		int locNameCol = cursor.getColumnIndex(LocationDiaryTableHelper.COL_LOC_NAME);
		int locName = cursor.getInt(locNameCol);
		
		int timeCol = cursor.getColumnIndex(PointsDiaryTableHelper.COL_TIME_ENTERED);
		
		String time = cursor.getString(timeCol);
		Date date = new Date(); 
		try{
			date = DiaryDbHelper.DB_DATE_STORE_FORMAT.parse(time);
		}
		catch(IllegalArgumentException pe){
			date = new Date(); 
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int commentCol = cursor.getColumnIndex(PointsDiaryTableHelper.COL_COMMENT);
		
		String comment = "no comment. ";
		if (commentCol >= 0){
			comment = cursor.getString(commentCol);
		}

		/**

		 * Next set the name of the entry.

		 */

		TextView name_text = (TextView) v.findViewById(R.id.daily_entry_desc_TextView);

		if (name_text != null) {

		    name_text.setText(comment);

		}
		
		TextView time_text = (TextView) v.findViewById(R.id.daily_entry_time_TextView);
		if (time_text != null){
			time_text.setText(mDisplayDateFormat.format(date));
		}
		

		TextView detail_text = (TextView) v.findViewById(R.id.daily_entry_detail_TextView);
		if (detail_text != null){
			detail_text.setText("@" + locName);
		}
		
		LinearLayout layout = (LinearLayout)v.findViewById(R.id.linearLayout3);
		
		if (mPointComponentFilter == null){
			// Go through each column, determine if it's empty, and get the drawable from that. 

			populatePointsView(cursor, v.getContext(), layout, PointsDiaryTableHelper.COL_VEGGIE_WHOLE_VAL);

			populatePointsView(cursor, v.getContext(), layout, PointsDiaryTableHelper.COL_VEGGIE_VAL);
			populatePointsView(cursor, v.getContext(), layout, PointsDiaryTableHelper.COL_GRAINS_VAL);
			populatePointsView(cursor, v.getContext(), layout, PointsDiaryTableHelper.COL_GRAINS_WHOLE_VAL);
			
			populatePointsView(cursor, v.getContext(), layout, PointsDiaryTableHelper.COL_FRUIT_VAL);
			populatePointsView(cursor, v.getContext(), layout, PointsDiaryTableHelper.COL_FRUIT_WHOLE_VAL);

			populatePointsView(cursor, v.getContext(), layout, PointsDiaryTableHelper.COL_DAIRY_VAL);
			populatePointsView(cursor, v.getContext(), layout, PointsDiaryTableHelper.COL_PROTEIN_VAL);
			populatePointsView(cursor, v.getContext(), layout, PointsDiaryTableHelper.COL_OILS_VAL);
			populatePointsView(cursor, v.getContext(), layout, PointsDiaryTableHelper.COL_SOLID_FATS_VAL);
			populatePointsView(cursor, v.getContext(), layout, PointsDiaryTableHelper.COL_SODIUM_VAL);
			populatePointsView(cursor, v.getContext(), layout, PointsDiaryTableHelper.COL_SUGAR_VAL);
		}else{
			populatePointsView(cursor, v.getContext(), layout, mPointComponentFilter.getPtDbColName());
		}
		/*
		if (source == 1){
			v.setOnClickListener(new FoodEntryClickListener());
		}else{
			v.setOnClickListener(null);
		}*/
	}


	private void populatePointsView(Cursor cursor, Context context, LinearLayout layout, String col_name) {
		int col_id = cursor.getColumnIndex(col_name);
		
		double val = cursor.getDouble(col_id);
		if (val > 0){
			for (int i=0; i<val; i++){
				ImageView img2 = new ImageView(context);
				if ((i +1)> val){
					img2.setImageResource(PointComponent.getFromPtsColName(col_name).getDrawableHalfId());
				}else{
					img2.setImageResource(PointComponent.getFromPtsColName(col_name).getDrawableId()); // TODO: Update half drawable	
				}
				
				layout.addView(img2);
				
				
				
			}
		}
	}
	
	class FoodEntryClickListener implements View.OnClickListener{

		
		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
			builder.setView(((Activity)v.getContext()).getLayoutInflater().inflate(R.layout.food_nutrition_info_card, 
														(ViewGroup) v.findViewById(R.id.layout_root_nut_info_card)));
			
			builder.setPositiveButton("Save Changes",  new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                
		           }});
			
			builder.setNegativeButton("Cancel",  new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                
		           }});
			
			builder.setCancelable(true);
			
			AlertDialog alertDialog = builder.create();
			

			alertDialog.show();
		}
		
	}
	
}
