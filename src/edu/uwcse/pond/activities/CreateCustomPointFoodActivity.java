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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import edu.uwcse.pond.adapters.CustomComponentListViewAdapter;
import edu.uwcse.pond.diary.CustomFoodPointsDiaryTableHelper;
import edu.uwcse.pond.diary.DiaryDbHelper;
import edu.uwcse.pond.nutrition.Consts.PointComponent;
import edu.uwcse.pond.proto.R;

public class CreateCustomPointFoodActivity extends Activity {
	public static final String DATA_DIARY_DB_ID_KEY = "diaryDbId";

	// NutritionDbHelper mNutritionDbHelper;

	DiaryDbHelper mDiaryDbHelper;

	private ContentValues entryValues;

	private Button saveButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_point_create);

		final EditText nameEditText = (EditText) findViewById(R.id.editText1);
		final EditText commentEditText = (EditText) findViewById(R.id.editText2);

		final CheckBox addToDiaryCheckBox = (CheckBox) findViewById(R.id.add_to_diary_CheckBox);

		saveButton = (Button) findViewById(R.id.button1);

		final ListView listView = (ListView) findViewById(R.id.listView1);

		listView.setAdapter(new CustomComponentListViewAdapter(this));

		// saveButton.setEnabled(false);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (nameEditText.getText().length() == 0) {
					final AlertDialog.Builder builder = new AlertDialog.Builder(
							CreateCustomPointFoodActivity.this);
					builder.setMessage("Please enter a name first")
							.setCancelable(false)
							.setPositiveButton("Okay",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
					final AlertDialog alert = builder.create();
					alert.show();
				} else {

					entryValues.put(CustomFoodPointsDiaryTableHelper.COL_NAME,
							nameEditText.getText().toString());
					entryValues.put(
							CustomFoodPointsDiaryTableHelper.COL_COMMENT,
							commentEditText.getText().toString());

					final long newId = mDiaryDbHelper
							.createNewCustomFoodPts(entryValues);
					if (newId == -1) {
						final AlertDialog.Builder builder = new AlertDialog.Builder(
								CreateCustomPointFoodActivity.this);
						builder.setMessage(
								"A food with this name already exists.")
								.setCancelable(false)
								.setPositiveButton("Okay",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int id) {
												dialog.cancel();
											}
										});
						final AlertDialog alert = builder.create();
						alert.show();
					} else {
						if (addToDiaryCheckBox.isChecked()) {
							mDiaryDbHelper
									.createNewPointsEntryFromCustomFood(newId);
						}

						final Context context = getApplicationContext();
						final CharSequence text = "New Food Item Created";
						final int duration = Toast.LENGTH_SHORT;
						final Toast toast = Toast.makeText(context, text,
								duration);
						toast.show();
						CreateCustomPointFoodActivity.this.finish();
					}

					// saveButton.setEnabled(false);

				}
			}
		});

		mDiaryDbHelper = DiaryDbHelper.getDiaryDbHelper(this);

		entryValues = new ContentValues();// mDiaryDbHelper.getPointsEntryAsVals(pointDiaryId);

	}

	@Override
	protected void onStop() {

		super.onStop();
	}

	public void updateComponentMinusHalf(PointComponent pointComponent) {
		double val = 0;
		if (entryValues.containsKey(pointComponent.getPtDbColName())) {
			val = entryValues.getAsDouble(pointComponent.getPtDbColName());
		}

		// Update amount
		entryValues.put(pointComponent.getPtDbColName(), val - 0.5);

	}

	public void updateComponentMinusOne(PointComponent pointComponent) {
		double val = 0;
		if (entryValues.containsKey(pointComponent.getPtDbColName())) {
			val = entryValues.getAsDouble(pointComponent.getPtDbColName());
		}

		// Update amount
		entryValues.put(pointComponent.getPtDbColName(), val - 1);

	}

	public void updateComponentPlusHalf(PointComponent pointComponent) {
		double val = 0;
		if (entryValues.containsKey(pointComponent.getPtDbColName())) {
			val = entryValues.getAsDouble(pointComponent.getPtDbColName());
		}

		// Update amount
		entryValues.put(pointComponent.getPtDbColName(), val + 0.5);

	}

	public void updateComponentPlusOne(PointComponent pointComponent) {
		double val = 0;
		if (entryValues.containsKey(pointComponent.getPtDbColName())) {
			val = entryValues.getAsDouble(pointComponent.getPtDbColName());
		}

		// Update amount
		entryValues.put(pointComponent.getPtDbColName(), val + 1);
	}

}
