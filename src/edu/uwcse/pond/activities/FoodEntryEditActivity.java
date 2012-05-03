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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import edu.uwcse.pond.adapters.PrettyCursorAdapter;
import edu.uwcse.pond.diary.DiaryDbHelper;
import edu.uwcse.pond.diary.FoodDiaryTableHelper;
import edu.uwcse.pond.diary.LocationDiaryTableHelper;
import edu.uwcse.pond.diary.PointsDiaryTableHelper;
import edu.uwcse.pond.nutrition.NutritionDbHelper;
import edu.uwcse.pond.nutrition.NutritionEntry;
import edu.uwcse.pond.nutrition.NutritionEntry.Serving;
import edu.uwcse.pond.proto.R;

/**
 * The Activity that displays and allows editing of a FoodEntry.
 * 
 * @author aha
 * 
 */
public class FoodEntryEditActivity extends Activity {

	/**
	 * Key for storing/passing the FoodEntry Id (DiaryDbId)
	 */
	public static final String DATA_DIARY_DB_ID_KEY = "diaryDbId";
	/**
	 * Format for displaying the time
	 */
	public static final SimpleDateFormat TIME_DISPLAY_FORMAT = new SimpleDateFormat(
			"h:mm ");
	/**
	 * Format for displaying the date.
	 */
	public static final SimpleDateFormat DATE_DISPLAY_FORMAT = new SimpleDateFormat(
			"EEE, MMM d");// "yyyy-MM-dd");
	/**
	 * Key to show the dialog to change the amount for the food entry
	 */
	private static final int DIALOG_AMOUNT = 1;
	/**
	 * Key to show the dialog to change the time the food entry was eaten.
	 */
	private static final int DIALOG_TIME = 2;
	/**
	 * Key to show the dialog to change the date the food entry was eaten.
	 */
	private static final int DIALOG_DATE = 3;
	/**
	 * The values from the database.
	 */
	private ContentValues entryValues;
	/**
	 * The databaseHelper object (to get raw nutrition data)
	 */
	private NutritionDbHelper mNutritionDbHelper;
	/**
	 * The databaseHelper to access/edit the entry
	 */
	private DiaryDbHelper mDiaryDbHelper;
	/**
	 * The total information for the nutrition item for this food
	 */
	private NutritionEntry mEntry;
	/**
	 * The EditText field for Amount displayed in this activity
	 */
	private EditText mServingAmountValue_EditText;
	/**
	 * The Spinner that holds the ServingTypes for this food
	 */
	private Spinner mServingType_Spinner;
	/**
	 * The button used to save the modified entry.
	 */
	private Button saveButton;

	/**
	 * Callback for the change-date dialog.
	 */
	private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			Date curDate;
			try {
				curDate = DiaryDbHelper.DB_DATE_STORE_FORMAT
						.parse((String) entryValues
								.get(FoodDiaryTableHelper.COL_TIME_ENTERED));

				final Calendar cal = Calendar.getInstance();
				cal.setTime(curDate);
				cal.set(Calendar.YEAR, year);
				cal.set(Calendar.MONTH, monthOfYear);
				cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

				entryValues
						.put(FoodDiaryTableHelper.COL_TIME_ENTERED,
								DiaryDbHelper.DB_DATE_STORE_FORMAT.format(cal
										.getTime()));
				saveButton.setEnabled(true);
				fillDateInfo((String) entryValues
						.get(FoodDiaryTableHelper.COL_TIME_ENTERED));
			} catch (final ParseException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * Callback for the change-time dialog.
	 */
	private final OnTimeSetListener mTimeSetListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Date curTime;
			try {
				curTime = DiaryDbHelper.DB_DATE_STORE_FORMAT
						.parse((String) entryValues
								.get(FoodDiaryTableHelper.COL_TIME_ENTERED));

				final Calendar cal = Calendar.getInstance();
				cal.setTime(curTime);
				cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
				cal.set(Calendar.MINUTE, minute);

				entryValues
						.put(FoodDiaryTableHelper.COL_TIME_ENTERED,
								DiaryDbHelper.DB_DATE_STORE_FORMAT.format(cal
										.getTime()));
				saveButton.setEnabled(true);
				fillTimeInfo((String) entryValues
						.get(FoodDiaryTableHelper.COL_TIME_ENTERED));
			} catch (final ParseException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * Creates the dialog that can be used to change the amount.
	 * 
	 * @return
	 */
	private AlertDialog buildAmountDialog() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);

		final LayoutInflater inflater = getLayoutInflater();
		final View layout = inflater.inflate(R.layout.food_amount_dialog, null);

		final EditText whole_num = (EditText) layout
				.findViewById(R.id.food_amount_whole_EditText);
		layout.findViewById(R.id.food_amount_part_EditText);

		((Button) layout.findViewById(R.id.food_amount_whole_increase_Button))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						final int whole_amt = Integer.parseInt(whole_num
								.getText().toString());
						final int new_amt = whole_amt + 1;

						whole_num.setText(Integer.toString(new_amt));
					}
				});

		((Button) layout.findViewById(R.id.food_amount_whole_decrease_Button))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						final int whole_amt = Integer.parseInt(whole_num
								.getText().toString());
						if (whole_amt > 0) {
							final int new_amt = whole_amt - 1;

							whole_num.setText(Integer.toString(new_amt));
						}
					}
				});

		alert.setView(layout);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {

				mServingAmountValue_EditText.setText(whole_num.getText() + ".0");
				updateValuesOnServingChange();
				final int whole_amt = Integer.parseInt(whole_num.getText()
						.toString());

				entryValues.put(FoodDiaryTableHelper.COL_AMOUNT, whole_amt);
				saveButton.setEnabled(true);
				dialog.dismiss();
			}
		});

		alert.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
			}
		});

		alert.setCancelable(true);
		return alert.create();

	}

	/**
	 * Updates the Date widget on this Activity.
	 * 
	 * @param datetime
	 *            The new date.
	 */
	private void fillDateInfo(String datetime) {

		try {
			final Date entry_date = DiaryDbHelper.DB_DATE_STORE_FORMAT
					.parse(datetime);

			final TextView dateTextView = (TextView) findViewById(R.id.food_entry_edit_date_TextView);
			dateTextView.setText(DATE_DISPLAY_FORMAT.format(entry_date));

		} catch (final ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Populates all of the UI elements on the screen with the appropriate
	 * information.
	 * 
	 * @param entry
	 *            the basic Nutrition information
	 * @param foodDiaryEntry
	 *            the FoodDiary information-- how much was eaten & when
	 */
	private void fillFoodDiaryEntry(NutritionEntry entry,
			ContentValues foodDiaryEntry) {
		final TextView nameView = (TextView) findViewById(R.id.food_entry_edit_foodName_TextView);
		nameView.setText(entry.getFoodName());

		mServingType_Spinner = (Spinner) findViewById(R.id.food_entry_edit_serving_Spinner);
		final ArrayAdapter<NutritionEntry.Serving> adapter = new ArrayAdapter<NutritionEntry.Serving>(
				this, android.R.layout.simple_spinner_item, entry.getServings());
		mServingType_Spinner.setAdapter(adapter);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// NumberPicker num_picker =
		// (NumberPicker)findViewById(R.id.food_detail_serving_size_NumberPicker);

		mServingType_Spinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						final EditText amt_txt = (EditText) findViewById(R.id.food_entry_edit_servingAmt_editText);
						final TextView srvg_desc = (TextView) findViewById(R.id.food_entry_edit_servingNote_TextView);

						final NutritionEntry.Serving srvg = (Serving) arg0
								.getSelectedItem();

						if (srvg.getServingType().getId() != entryValues
								.getAsInteger(FoodDiaryTableHelper.COL_SERVINGID)) {
							amt_txt.setText(Double.toString(srvg
									.getServingAmtVal()));
						} else {
							amt_txt.setText(entryValues
									.getAsString(FoodDiaryTableHelper.COL_AMOUNT));
						}

						srvg_desc.setText(srvg.getServingAmtNote());

						updateValuesOnServingChange();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

		mServingAmountValue_EditText = (EditText) findViewById(R.id.food_entry_edit_servingAmt_editText);

		mServingAmountValue_EditText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// When the Amount EditText is touched,
				// show the dialog to change it with buttons.
				showDialog(DIALOG_AMOUNT);
				return true;
			}
		});

		mServingAmountValue_EditText.setText(foodDiaryEntry
				.getAsString(FoodDiaryTableHelper.COL_AMOUNT));

		mServingAmountValue_EditText
				.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (!hasFocus) {
							updateValuesOnServingChange();
						}
					}
				});

		mServingAmountValue_EditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}
		});

		fillTimeInfo(foodDiaryEntry
				.getAsString(FoodDiaryTableHelper.COL_TIME_ENTERED));
		fillDateInfo(foodDiaryEntry
				.getAsString(FoodDiaryTableHelper.COL_TIME_ENTERED));

		saveButton = (Button) findViewById(R.id.save_button);
		saveButton.setEnabled(false);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO: Update the date/time with edits?
				mDiaryDbHelper.updateFoodEntry(entryValues, mEntry,
						(Serving) mServingType_Spinner.getSelectedItem());

				final Context context = getApplicationContext();
				final CharSequence text = "Entry updated";
				final int duration = Toast.LENGTH_SHORT;

				final Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				saveButton.setEnabled(false);
			}
		});

		final Button changeTimeButton = (Button) findViewById(R.id.point_entry_edit_time_Button);
		final Button changeDateButton = (Button) findViewById(R.id.point_entry_edit_date_Button);

		changeTimeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DIALOG_TIME);
			}
		});

		changeDateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DIALOG_DATE);
			}
		});

	}

	/**
	 * Fills the spinner with the possible locations.
	 * 
	 * @param locId
	 */
	private void fillLocationSpinner(int locId) {
		final Spinner locationSpinner = (Spinner) findViewById(R.id.food_entry_edit_locationSpinner);
		final Cursor entries_cursor = mDiaryDbHelper.getLocationEntries();

		final String[] from = new String[] { LocationDiaryTableHelper.COL_LOC_NAME };
		final int[] to = new int[] { android.R.id.text1 };

		final PrettyCursorAdapter mLocationAdapter = new PrettyCursorAdapter(
				this, android.R.layout.simple_spinner_item, entries_cursor,
				from, to);
		mLocationAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		locationSpinner.setAdapter(mLocationAdapter);

		for (int i = 0; i < mLocationAdapter.getCount(); i++) {
			if (mLocationAdapter.getItemId(i) == locId) {
				locationSpinner.setSelection(i);
			}
		}
	}

	/**
	 * Updates the Time widget on this Activity.
	 * 
	 * @param datetime
	 *            The new time
	 */
	private void fillTimeInfo(String datetime) {

		try {
			final Date entry_date = DiaryDbHelper.DB_DATE_STORE_FORMAT
					.parse(datetime);

			// Time
			final TextView timeTextView = (TextView) findViewById(R.id.food_entry_edit_time_TextView);
			timeTextView.setText(TIME_DISPLAY_FORMAT.format(entry_date));

		} catch (final ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Check for foodId passed in a bundle
		setContentView(R.layout.food_entry_edit);
		mDiaryDbHelper = DiaryDbHelper.getDiaryDbHelper(this);

		mNutritionDbHelper = new NutritionDbHelper(this);
		mNutritionDbHelper.open();// openDataBase();

		final Bundle b = this.getIntent().getExtras();

		long pointDiaryId = b.getLong(DATA_DIARY_DB_ID_KEY);
		if (pointDiaryId == 0) {
			pointDiaryId = this.getIntent().getLongExtra(DATA_DIARY_DB_ID_KEY,
					-1);
		}
		entryValues = mDiaryDbHelper
				.getFoodEntryFromPointsEntryAsVals(pointDiaryId);

		if (entryValues.size() != 0) {
			final long food_id = entryValues
					.getAsLong(FoodDiaryTableHelper.COL_FOODID);
			mEntry = mNutritionDbHelper.getNutritionEntry((int) food_id);

			fillFoodDiaryEntry(mEntry, entryValues);

			fillLocationSpinner(entryValues
					.getAsInteger(FoodDiaryTableHelper.COL_LOCATION_ID));

		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case DIALOG_AMOUNT:
			dialog = buildAmountDialog();
			break;
		case DIALOG_DATE:
			Date curDate;
			try {
				curDate = DiaryDbHelper.DB_DATE_STORE_FORMAT
						.parse((String) entryValues
								.get(PointsDiaryTableHelper.COL_TIME_ENTERED));
			} catch (final ParseException e) {
				curDate = Calendar.getInstance().getTime();
			}

			final int yr = curDate.getYear() + 1900;
			final int mth = curDate.getMonth();
			final int date = curDate.getDate();

			return new DatePickerDialog(this, mDateSetListener, yr, mth, date);
		case DIALOG_TIME:
			Date curTime;
			try {
				curTime = DiaryDbHelper.DB_DATE_STORE_FORMAT
						.parse((String) entryValues
								.get(PointsDiaryTableHelper.COL_TIME_ENTERED));
			} catch (final ParseException e) {
				curTime = Calendar.getInstance().getTime();
			}

			final TimePickerDialog diag = new TimePickerDialog(this,
					mTimeSetListener, curTime.getHours(), curTime.getMinutes(),
					false);

			return diag;
		default:
			dialog = null;
		}
		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {

		switch (id) {
		case DIALOG_AMOUNT:
			final String amount = mServingAmountValue_EditText.getText()
					.toString();
			final int dec = amount.indexOf(".");
			((EditText) dialog.findViewById(R.id.food_amount_whole_EditText))
					.setText(amount.substring(0, dec));
			break;
		// case DIALOG_GET_FOOD_QUERY:
		// do the work to define the game over Dialog

		// break;
		default:
			dialog = null;
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		mNutritionDbHelper.close();
	}

	/**
	 * When the user chooses a different serving type, this method updates the
	 * amount EditText to the appropriate value.
	 */
	private void updateValuesOnServingChange() {
		final String amt = mServingAmountValue_EditText.getText().toString();
		final Serving srvg = (Serving) mServingType_Spinner.getSelectedItem();
		final double amt_d = Double.parseDouble(amt);

		double multiplier = FoodEntryEditActivity.this.mEntry
				.getServingMultiplier(srvg, amt_d);
		if (Double.isInfinite(multiplier) || Double.isNaN(multiplier)) {
			multiplier = 2.0; // TODO: fix hack by fixing the nutrition db.

		}
	}

}
