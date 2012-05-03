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
package edu.uwcse.pond.activities;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.uwcse.pond.diary.DiaryDbHelper;
import edu.uwcse.pond.diary.LocationDiaryTableHelper;
import edu.uwcse.pond.diary.PointsDiaryTableHelper;
import edu.uwcse.pond.nutrition.Consts.PointComponent;
import edu.uwcse.pond.proto.R;

public class DailyEntriesListViewAdapter extends CursorAdapter {

	class FoodEntryClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					v.getContext());
			builder.setView(((Activity) v.getContext())
					.getLayoutInflater()
					.inflate(
							R.layout.food_nutrition_info_card,
							(ViewGroup) v
									.findViewById(R.id.layout_root_nut_info_card)));

			builder.setPositiveButton("Save Changes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {

						}
					});

			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {

						}
					});

			builder.setCancelable(true);

			final AlertDialog alertDialog = builder.create();

			alertDialog.show();
		}

	}

	private final SimpleDateFormat mDisplayDateFormat = new SimpleDateFormat(
			"h:mm a");

	private PointComponent mPointComponentFilter = null;

	private final long[] mIds;

	public DailyEntriesListViewAdapter(Context context, Cursor c) {
		super(context, c);

		mIds = new long[c.getCount()];
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// fillView(cursor, view);
		fillView2(view, cursor);
	}

	public void clearPointComponentFilter() {
		mPointComponentFilter = null;

	}

	private void fillView(Cursor cursor, View v) {
		final int srcCol = cursor
				.getColumnIndex(PointsDiaryTableHelper.COL_SOURCE);
		cursor.getInt(srcCol);

		final int locCol = cursor
				.getColumnIndex(PointsDiaryTableHelper.COL_LOCATION_ID);
		cursor.getInt(locCol);

		final int locNameCol = cursor
				.getColumnIndex(LocationDiaryTableHelper.COL_LOC_NAME);
		final int locName = cursor.getInt(locNameCol);

		final int timeCol = cursor
				.getColumnIndex(PointsDiaryTableHelper.COL_TIME_ENTERED);

		final String time = cursor.getString(timeCol);
		Date date = new Date();
		try {
			date = DiaryDbHelper.DB_DATE_STORE_FORMAT.parse(time);
		} catch (final IllegalArgumentException pe) {
			date = new Date();
		} catch (final java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final int commentCol = cursor
				.getColumnIndex(PointsDiaryTableHelper.COL_COMMENT);

		String comment = "no comment. ";
		if (commentCol >= 0) {
			comment = cursor.getString(commentCol);
		}

		/**
		 * 
		 * Next set the name of the entry.
		 */

		final TextView name_text = (TextView) v
				.findViewById(R.id.daily_entry_desc_TextView);

		if (name_text != null) {

			name_text.setText(comment);

		}

		final TextView time_text = (TextView) v
				.findViewById(R.id.daily_entry_time_TextView);
		if (time_text != null) {
			time_text.setText(mDisplayDateFormat.format(date));
		}

		final TextView detail_text = (TextView) v
				.findViewById(R.id.daily_entry_detail_TextView);
		if (detail_text != null) {
			detail_text.setText("@" + locName);
		}

		final LinearLayout layout = (LinearLayout) v
				.findViewById(R.id.linearLayout3);

		if (mPointComponentFilter == null) {
			// Go through each column, determine if it's empty, and get the
			// drawable from that.

			populatePointsView(cursor, v.getContext(), layout,
					PointsDiaryTableHelper.COL_VEGGIE_WHOLE_VAL);

			populatePointsView(cursor, v.getContext(), layout,
					PointsDiaryTableHelper.COL_VEGGIE_VAL);
			populatePointsView(cursor, v.getContext(), layout,
					PointsDiaryTableHelper.COL_GRAINS_VAL);
			populatePointsView(cursor, v.getContext(), layout,
					PointsDiaryTableHelper.COL_GRAINS_WHOLE_VAL);

			populatePointsView(cursor, v.getContext(), layout,
					PointsDiaryTableHelper.COL_FRUIT_VAL);
			populatePointsView(cursor, v.getContext(), layout,
					PointsDiaryTableHelper.COL_FRUIT_WHOLE_VAL);

			populatePointsView(cursor, v.getContext(), layout,
					PointsDiaryTableHelper.COL_DAIRY_VAL);
			populatePointsView(cursor, v.getContext(), layout,
					PointsDiaryTableHelper.COL_PROTEIN_VAL);
			populatePointsView(cursor, v.getContext(), layout,
					PointsDiaryTableHelper.COL_OILS_VAL);
			populatePointsView(cursor, v.getContext(), layout,
					PointsDiaryTableHelper.COL_SOLID_FATS_VAL);
			populatePointsView(cursor, v.getContext(), layout,
					PointsDiaryTableHelper.COL_SODIUM_VAL);
			populatePointsView(cursor, v.getContext(), layout,
					PointsDiaryTableHelper.COL_SUGAR_VAL);
		} else {
			populatePointsView(cursor, v.getContext(), layout,
					mPointComponentFilter.getPtDbColName());
		}
		/*
		 * if (source == 1){ v.setOnClickListener(new FoodEntryClickListener());
		 * }else{ v.setOnClickListener(null); }
		 */
	}

	private void fillView2(View view, Cursor cursor) {

		cursor.getLong(cursor.getColumnIndex(PointsDiaryTableHelper.COL_ROWID));

		final int srcCol = cursor
				.getColumnIndex(PointsDiaryTableHelper.COL_SOURCE);
		cursor.getInt(srcCol);

		final int locCol = cursor
				.getColumnIndex(PointsDiaryTableHelper.COL_LOCATION_ID);
		cursor.getInt(locCol);

		final int locNameCol = cursor
				.getColumnIndex(LocationDiaryTableHelper.COL_LOC_NAME);
		final String locName = cursor.getString(locNameCol);

		final int timeCol = cursor
				.getColumnIndex(PointsDiaryTableHelper.COL_TIME_ENTERED);

		final int commentCol = cursor
				.getColumnIndex(PointsDiaryTableHelper.COL_COMMENT);

		String comment = "no comment. ";
		if (commentCol >= 0) {
			comment = cursor.getString(commentCol);
		}

		final String time = cursor.getString(timeCol);
		Date date = new Date();
		try {
			date = DiaryDbHelper.DB_DATE_STORE_FORMAT.parse(time);
		} catch (final IllegalArgumentException pe) {
			date = new Date();
		} catch (final java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/**
		 * Next set the name of the entry.
		 */

		final TextView name_text = (TextView) view
				.findViewById(R.id.daily_entry_desc_TextView);

		if (name_text != null) {

			name_text.setText(comment);

		}

		final TextView time_text = (TextView) view
				.findViewById(R.id.daily_entry_time_TextView);
		if (time_text != null) {
			time_text.setText(mDisplayDateFormat.format(date));
		}

		final TextView detail_text = (TextView) view
				.findViewById(R.id.daily_entry_detail_TextView);
		if (detail_text != null) {
			detail_text.setText("@" + locName);// comment);
		}
		final LinearLayout layout = (LinearLayout) view
				.findViewById(R.id.linearLayout3);

		layout.removeAllViews();

		if (mPointComponentFilter == null) {
			// Go through each column, determine if it's empty, and get the
			// drawable from that.
			populatePointsView(cursor, view.getContext(), layout,
					PointsDiaryTableHelper.COL_VEGGIE_WHOLE_VAL);

			populatePointsView(cursor, view.getContext(), layout,
					PointsDiaryTableHelper.COL_VEGGIE_VAL);
			populatePointsView(cursor, view.getContext(), layout,
					PointsDiaryTableHelper.COL_GRAINS_VAL);
			populatePointsView(cursor, view.getContext(), layout,
					PointsDiaryTableHelper.COL_GRAINS_WHOLE_VAL);

			populatePointsView(cursor, view.getContext(), layout,
					PointsDiaryTableHelper.COL_FRUIT_VAL);
			populatePointsView(cursor, view.getContext(), layout,
					PointsDiaryTableHelper.COL_FRUIT_WHOLE_VAL);

			populatePointsView(cursor, view.getContext(), layout,
					PointsDiaryTableHelper.COL_DAIRY_VAL);
			populatePointsView(cursor, view.getContext(), layout,
					PointsDiaryTableHelper.COL_PROTEIN_VAL);
			populatePointsView(cursor, view.getContext(), layout,
					PointsDiaryTableHelper.COL_OILS_VAL);
			populatePointsView(cursor, view.getContext(), layout,
					PointsDiaryTableHelper.COL_SOLID_FATS_VAL);
			populatePointsView(cursor, view.getContext(), layout,
					PointsDiaryTableHelper.COL_SODIUM_VAL);
			populatePointsView(cursor, view.getContext(), layout,
					PointsDiaryTableHelper.COL_SUGAR_VAL);
		} else {
			populatePointsView(cursor, view.getContext(), layout,
					mPointComponentFilter.getPtDbColName());
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		// return super.getItemId(position);
		final Cursor c = getCursor();
		c.moveToPosition(position);
		return c.getLong(0);// c.getColumnIndex(PointsDiaryTableHelper.COL_ROWID));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// Cursor c = getCursor();

		final LayoutInflater inflater = LayoutInflater.from(context);

		final View v = inflater.inflate(R.layout.daily_entry_list_view_row,
				parent, false);

		fillView2(v, cursor);

		return v;

	}

	private void populatePointsView(Cursor cursor, Context context,
			LinearLayout layout, String col_name) {
		final int col_id = cursor.getColumnIndex(col_name);

		final double val = cursor.getDouble(col_id);
		if (val > 0) {
			for (int i = 0; i < val; i++) {
				final ImageView img2 = new ImageView(context);
				if ((i + 1) > val) {
					img2.setImageResource(PointComponent.getFromPtsColName(
							col_name).getDrawableHalfId());
				} else {
					img2.setImageResource(PointComponent.getFromPtsColName(
							col_name).getDrawableId()); // TODO: Update half
														// drawable
				}

				layout.addView(img2);

			}
		}
	}

	public void setPointComponentFilter(PointComponent pc) {
		if (pc == PointComponent.ALL) {
			clearPointComponentFilter();
		} else {
			mPointComponentFilter = pc;
		}
	}

}
