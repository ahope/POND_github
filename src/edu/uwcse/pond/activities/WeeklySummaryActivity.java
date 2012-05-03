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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import edu.uwcse.pond.adapters.PrettyComponentArrayAdapter;
import edu.uwcse.pond.diary.DiaryDbHelper;
import edu.uwcse.pond.nutrition.Consts.PointComponent;
import edu.uwcse.pond.proto.R;

/**
 * The activity that shows the weekly summary of progress. 
 * @author aha
 *
 */
public class WeeklySummaryActivity extends Activity implements
		OnGesturePerformedListener {

	public static final String DATA_WHICH_COMPONENT_INT_KEY = "whichComponent";
	public static final String DATA_SHOW_WEEK_INT_KEY = "showWeek";
	public static final String DATA_WHICH_DATE_STRING_KEY = "whichDate";

	static final int DIALOG_DATE = 1;

	public static ArrayList<PointComponent> COMPONENT_ORDER = new ArrayList<PointComponent>();

	static {
		// COMPONENT_ORDER.add(PointComponent.ALL);
		COMPONENT_ORDER.add(PointComponent.VEGGIE);
		COMPONENT_ORDER.add(PointComponent.VEGGIE_GREEN);
		COMPONENT_ORDER.add(PointComponent.GRAINS_WHOLE);
		COMPONENT_ORDER.add(PointComponent.GRAINS);
		COMPONENT_ORDER.add(PointComponent.FRUIT_WHOLE);
		COMPONENT_ORDER.add(PointComponent.FRUIT);
		COMPONENT_ORDER.add(PointComponent.DAIRY);
		COMPONENT_ORDER.add(PointComponent.PROTEIN);

		COMPONENT_ORDER.add(PointComponent.OILS);
		COMPONENT_ORDER.add(PointComponent.SOLID_FATS);
		COMPONENT_ORDER.add(PointComponent.SODIUM);
		COMPONENT_ORDER.add(PointComponent.SUGAR);
	}

	public static final SimpleDateFormat DATE_DISPLAY_FORMAT = new SimpleDateFormat(
			"MMM d, yy");

	private DiaryDbHelper mDiaryHelper;

	private GestureLibrary mGestureLibrary;

	private PointComponent mCurComponent;

	private Calendar mCurDate;

	// the callback received when the user "sets" the date in the dialog
	private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mCurDate.set(Calendar.YEAR, year);
			mCurDate.set(Calendar.MONTH, monthOfYear);
			mCurDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

			bindData(mCurComponent, mCurDate);
		}

	};

	private void bindData(PointComponent component, Calendar whichDay) {

		// Remember the values the new cursor will represent
		mCurComponent = component;
		mCurDate = whichDay;

		if (mCurComponent != null) {
			mCurComponent.getDesc();
		}

		// Show week
		// entries_cursor = mDiaryHelper.getAllEntriesForWeek(mCurDate,
		// mCurComponent);
		fillWeekChart(mCurComponent, mCurDate);
		return;

	}

	private void fillDayChart(int howMany, int drawableId, RelativeLayout bar,
			int goal) {
		bar.removeAllViews();
		int curId = 15;

		for (int i = 0; i < howMany; i++) {

			if (i == goal) {
				final ImageView curView = new ImageView(bar.getContext());
				curView.setImageResource(R.drawable.goal_line);
				curView.setId(curId);

				final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

				if (i == 0) {
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				} else {
					layoutParams.addRule(RelativeLayout.ABOVE, curId - 1);
				}

				bar.addView(curView, layoutParams);
				curId++;
			}

			final ImageView curView = new ImageView(bar.getContext());
			curView.setImageResource(drawableId);

			curView.setId(curId);

			final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			if (i == 0) {
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			} else {
				layoutParams.addRule(RelativeLayout.ABOVE, curId - 1);
			}

			bar.addView(curView, layoutParams);
			curId++;
		}

		for (int j = howMany; j < goal; j++) {

			final ImageView curView = new ImageView(bar.getContext());

			curView.setImageResource(R.drawable.empty_chart_goal);
			curView.setMinimumWidth(30);
			curView.setMinimumHeight(30);

			curView.setId(curId);

			final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			if (j == 0) {
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			} else {
				layoutParams.addRule(RelativeLayout.ABOVE, curId - 1);
			}

			bar.addView(curView, layoutParams);
			curId++;
		}

		if (howMany < goal) {

			final ImageView curView = new ImageView(bar.getContext());
			curView.setImageResource(R.drawable.goal_line);
			curView.setId(curId);

			final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			layoutParams.addRule(RelativeLayout.ABOVE, curId - 1);

			bar.addView(curView, layoutParams);
		}

		if (howMany == 0 && goal == 0) {
			final ImageView curView = new ImageView(bar.getContext());
			curView.setImageResource(R.drawable.empty_circle);

			curId = 3;

			curView.setId(curId);

			final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

			bar.addView(curView, layoutParams);
		}

	}

	private void fillWeekChart(PointComponent pt, Calendar date) {
		// Determine the Monday just before the given date
		final Calendar curDay = Calendar.getInstance();
		curDay.setTime(date.getTime());
		while (curDay.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			curDay.add(Calendar.DATE, -1);
		}

		// Get a cursor for Mon-T-Wed-... etc.
		fillDayChart(mDiaryHelper.getCountForDay(curDay, pt),
				pt.getDrawableId(),
				(RelativeLayout) findViewById(R.id.daily_entries_Monday),
				mDiaryHelper.getGoalForDay(pt, curDay));

		curDay.add(Calendar.DATE, 1);
		fillDayChart(mDiaryHelper.getCountForDay(curDay, pt),
				pt.getDrawableId(),
				(RelativeLayout) findViewById(R.id.daily_entries_Tuesday),
				mDiaryHelper.getGoalForDay(pt, curDay));

		curDay.add(Calendar.DATE, 1);
		fillDayChart(mDiaryHelper.getCountForDay(curDay, pt),
				pt.getDrawableId(),
				(RelativeLayout) findViewById(R.id.daily_entries_Wednesday),
				mDiaryHelper.getGoalForDay(pt, curDay));

		curDay.add(Calendar.DATE, 1);
		fillDayChart(mDiaryHelper.getCountForDay(curDay, pt),
				pt.getDrawableId(),
				(RelativeLayout) findViewById(R.id.daily_entries_Thursday),
				mDiaryHelper.getGoalForDay(pt, curDay));

		curDay.add(Calendar.DATE, 1);
		fillDayChart(mDiaryHelper.getCountForDay(curDay, pt),
				pt.getDrawableId(),
				(RelativeLayout) findViewById(R.id.daily_entries_Friday),
				mDiaryHelper.getGoalForDay(pt, curDay));

		curDay.add(Calendar.DATE, 1);
		fillDayChart(mDiaryHelper.getCountForDay(curDay, pt),
				pt.getDrawableId(),
				(RelativeLayout) findViewById(R.id.daily_entries_Saturday),
				mDiaryHelper.getGoalForDay(pt, curDay));

		curDay.add(Calendar.DATE, 1);
		fillDayChart(mDiaryHelper.getCountForDay(curDay, pt),
				pt.getDrawableId(),
				(RelativeLayout) findViewById(R.id.daily_entries_Sunday),
				mDiaryHelper.getGoalForDay(pt, curDay));

	}

	private PointComponent getNextComponent(PointComponent start) {
		final int which = COMPONENT_ORDER.indexOf(start);
		return (which == COMPONENT_ORDER.size() - 1) ? COMPONENT_ORDER
				.get(which) : COMPONENT_ORDER.get(which + 1);
	}

	private PointComponent getPrevComponent(PointComponent start) {
		final int which = COMPONENT_ORDER.indexOf(start);
		return (which == 0) ? COMPONENT_ORDER.get(0) : COMPONENT_ORDER
				.get(which - 1);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weekly_entries);

		mGestureLibrary = GestureLibraries.fromRawResource(this,
				R.raw.gestures2);
		if (!mGestureLibrary.load()) {
			finish();
		}

		mDiaryHelper = DiaryDbHelper.getDiaryDbHelper(getApplicationContext());

		// mTextView =
		// (TextView)findViewById(R.id.daily_entries_summary_TextView);
		/*
		 * mToggleButton =
		 * (ToggleButton)findViewById(R.id.daily_entries_ShowWeek_ToggleButton);
		 * mToggleButton.setOnCheckedChangeListener(new
		 * OnCheckedChangeListener() {
		 * 
		 * @Override public void onCheckedChanged(CompoundButton buttonView,
		 * boolean isChecked) { if (mCurComponent == PointComponent.ALL){
		 * mCurComponent = getNextComponent(PointComponent.ALL); }
		 * 
		 * bindData(mCurComponent,
		 * (mToggleButton.isChecked()?VALUE_SHOW_WEEK:VALUE_SHOW_DAY),
		 * mCurDate); } });
		 */
		final Bundle b = this.getIntent().getExtras();

		final Spinner chooseCompSpinner = (Spinner) findViewById(R.id.daily_entries_ChooseComponent_Spinner);
		/*
		 * ArrayAdapter<PointComponent> adapter = new
		 * ArrayAdapter<Consts.PointComponent>(this,
		 * android.R.layout.simple_spinner_item, COMPONENT_ORDER);
		 */
		final PrettyComponentArrayAdapter adapter = new PrettyComponentArrayAdapter(
				this, COMPONENT_ORDER);
		chooseCompSpinner.setAdapter(adapter);
		// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// adapter.setDropDownViewResource(R.layout.pretty_spinner_drop_down_item);

		PointComponent component = PointComponent.VEGGIE_GREEN;

		if (b != null) {
			if (b.containsKey(DATA_WHICH_COMPONENT_INT_KEY)) {
				final String pointsColName = b
						.getString(DATA_WHICH_COMPONENT_INT_KEY);
				component = PointComponent.getFromPtsColName(pointsColName);
			}
		}

		chooseCompSpinner.setSelection(COMPONENT_ORDER.indexOf(component));

		chooseCompSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int postion, long id) {

						bindData(COMPONENT_ORDER.get(postion), mCurDate);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});

		bindData(component, Calendar.getInstance());
		/*
		 * Button chooseDateButton =
		 * (Button)findViewById(R.id.daily_entries_ChangeDate_Button);
		 * chooseDateButton.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { showDialog(DIALOG_DATE); }
		 * });
		 */

		/*
		 * ToggleButton chartToggle =
		 * (ToggleButton)findViewById(R.id.daily_entries_chart_ToggleButton);
		 * chartToggle.setOnCheckedChangeListener(new OnCheckedChangeListener()
		 * {
		 * 
		 * @Override public void onCheckedChanged(CompoundButton buttonView,
		 * boolean isChecked) { switcher.showNext(); } });
		 */
		final GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
		gestures.addOnGesturePerformedListener(this);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.daily_entries_ListView) {
			menu.setHeaderTitle("Diary Entry (?)");
			final String[] menuItems = { "Edit", "Delete" };// getResources().getStringArray(R.array.menu);
															// // TODO: Create
															// menu resource
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DATE:
			return new DatePickerDialog(this, mDateSetListener,
					mCurDate.get(Calendar.YEAR), mCurDate.get(Calendar.MONTH),
					mCurDate.get(Calendar.DAY_OF_MONTH));
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.entries_list_menu, menu);

		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mDiaryHelper.doneWithDb();
	}

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		Prediction best_pred = null;

		final Iterator<Prediction> iter = mGestureLibrary.recognize(gesture)
				.iterator();
		while (iter.hasNext()) {
			final Prediction p = iter.next();

			if (best_pred == null || p.score > best_pred.score) {
				best_pred = p;
			}
		}
		Toast.makeText(this, best_pred.name, Toast.LENGTH_SHORT).show();

		if (best_pred.name.equalsIgnoreCase("left")) {

			// Increment the week
			mCurDate.add(Calendar.WEEK_OF_YEAR, 1);
			bindData(mCurComponent, mCurDate);

		} else if (best_pred.name.equalsIgnoreCase("right")) {

			// Decrement the week
			mCurDate.add(Calendar.WEEK_OF_YEAR, -1);
			bindData(mCurComponent, mCurDate);

		} else if (best_pred.name.equalsIgnoreCase("up")) {
			// Get the next component
			bindData(getNextComponent(mCurComponent), mCurDate);

		} else if (best_pred.name.equalsIgnoreCase("down")) {
			// Get the previous component
			bindData(getPrevComponent(mCurComponent), mCurDate);

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.entrie_list_changeDate_menuItem:
			showDialog(DIALOG_DATE);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

}
