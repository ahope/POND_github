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
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import edu.uwcse.pond.adapters.GoalListViewAdapter;
import edu.uwcse.pond.diary.ActionLogDbHelper.Action;
import edu.uwcse.pond.diary.DiaryDbHelper;
import edu.uwcse.pond.proto.R;

public class GoalEdityActivity extends Activity {

	private GoalListViewAdapter mGoalListViewAdapter;

	private ListView mGoalListView;

	private DiaryDbHelper mDbHelper;

	// private Map<Consts.PointComponent,Integer> mGoalMap;
	private ContentValues mGoalMap;

	private void fillData() {
		//
		mGoalListView = (ListView) findViewById(R.id.goal_view_listView);
		mGoalListViewAdapter = new GoalListViewAdapter(this, mGoalMap);
		mGoalListView.setAdapter(mGoalListViewAdapter);

		final SharedPreferences settings = getSharedPreferences(
				OverviewActivity.PREFS_INSITU_STUDY, 0);
		final boolean canChangeGoals = settings.getBoolean(
				OverviewActivity.PREFS_INSITU_STUDY_CAN_CHANGE_GOALS, true);

		final Button saveButton = (Button) findViewById(R.id.button1);
		if (canChangeGoals) {
			saveButton.setVisibility(View.VISIBLE);
			saveButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mDbHelper.updateGoal(mGoalListViewAdapter.getCurVals());
					GoalEdityActivity.this.finish();
				}
			});
		} else {
			saveButton.setVisibility(View.GONE);
		}

		final Button cancelButton = (Button) findViewById(R.id.button2);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				GoalEdityActivity.this.finish();
			}
		});

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Check for foodId passed in a bundle
		setContentView(R.layout.goal_view);
		mDbHelper = DiaryDbHelper.getDiaryDbHelper(getApplicationContext());
		mDbHelper.logAction(Action.VIEW_GOAL, -1, -1, "View goal activity", "");
		// GoalEntry mEntry = mDbHelper.getMostRecentGoal();
		mGoalMap = mDbHelper.getAllMostRecentGoalAsContentVals();// getAllMostRecentGoalAsMap();
		fillData();
	}

	@Override
	protected void onDestroy() {
		mDbHelper.doneWithDb();
		super.onDestroy();

	}

	@Override
	protected void onStop() {
		// TODO Check for saving stuff or not?
		super.onStop();

	}
}
