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

import java.util.Calendar;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import edu.uwcse.pond.diary.DiaryDbHelper;
import edu.uwcse.pond.diary.FoodDiaryTableHelper;

public class RecentEntriesListActivity extends ListActivity {

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		final DiaryDbHelper myDiaryDbHelper = DiaryDbHelper
				.getDiaryDbHelper(this);

		final Calendar today = Calendar.getInstance();
		final Calendar before = Calendar.getInstance();
		before.add(Calendar.DATE, -10);

		final Cursor c = myDiaryDbHelper.getFoodEntriesForTimePeriod(before,
				today);

		startManagingCursor(c);

		final String[] from = new String[] { FoodDiaryTableHelper.COL_FOODID,
				FoodDiaryTableHelper.COL_TIME_ENTERED };
		final int[] to = new int[] { android.R.id.text1, android.R.id.text2 };

		// Now create an array adapter and set it to display using our row
		final ListAdapter myListAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, c, from, to);

		setListAdapter(myListAdapter);
	}

}
