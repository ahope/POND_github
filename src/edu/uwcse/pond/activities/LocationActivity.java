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

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import edu.uwcse.pond.diary.DiaryDbHelper;
import edu.uwcse.pond.diary.LocationDiaryTableHelper;
import edu.uwcse.pond.proto.R;

public class LocationActivity extends Activity implements LocationListener {

	private DiaryDbHelper mDiaryHelper;
	private ListView mListView;
	private SimpleCursorAdapter mListViewAdapter;
	private LocationManager mLocationManager;
	private String mNewLocName;

	private Location getBestLocation(Location loc1, Location loc2) {
		final long curTime = new Date().getTime();
		final long timeDelta1 = curTime - loc1.getTime();
		final long timeDelta2 = curTime - loc2.getTime();

		final int TWO_MINUTES = 1000 * 60 * 2;

		if (timeDelta1 < TWO_MINUTES) {
			if (timeDelta2 < TWO_MINUTES) {
				// They are both recent enough; return the more accurate
				// location
				return (loc1.getAccuracy() < loc2.getAccuracy() ? loc1 : loc2);
			} else {
				// time1 is more recent, return it.
				return loc1;
			}
		} else {
			if (timeDelta2 < TWO_MINUTES) {
				return loc2;
			}

			return null;
		}

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		item.getItemId();
		final long list_item_id = mListViewAdapter.getItemId(info.position);
		// String[] menuItems = getResources().getStringArray(R.array.menu);
		// String menuItemName = menuItems[menuItemIndex];
		// String listItemName = Countries[info.position];

		mDiaryHelper.deleteLocation(list_item_id);
		mListViewAdapter.notifyDataSetChanged();
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_entries);
		mDiaryHelper = DiaryDbHelper.getDiaryDbHelper(getApplicationContext());
		mListView = (ListView) findViewById(R.id.locations_ListView);
		final Cursor entries_cursor = mDiaryHelper.getLocationEntries();

		final String[] from = new String[] { LocationDiaryTableHelper.COL_LOC_NAME };
		final int[] to = new int[] { R.id.location_name_TextView };

		mListViewAdapter = new SimpleCursorAdapter(this,
				R.layout.location_list_entry, entries_cursor, from, to);// DailyEntriesListViewAdapter(this,
																		// entries_cursor);
		mListView.setAdapter(mListViewAdapter);
		registerForContextMenu(mListView);

		final Button newButton = (Button) findViewById(R.id.new_location_Button);
		newButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final AlertDialog.Builder alert = new AlertDialog.Builder(
						LocationActivity.this);

				final LayoutInflater inflater = getLayoutInflater();
				// FrameLayout f1 =
				// (FrameLayout)alert.findViewById(android.R.id.body);
				// f1.addView(inflater.inflate(R.layout.dialog_view, f1,
				// false));
				final View layout = inflater.inflate(
						R.layout.location_name_dialog, null);

				final EditText input = (EditText) layout
						.findViewById(R.id.location_name_EditText);

				alert.setView(layout);

				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								mNewLocName = input.getText().toString();

								// Check location listening is turned on ()
								// Turn on location listening
								// Acquire a reference to the system Location
								// Manager
								mLocationManager = (LocationManager) LocationActivity.this
										.getSystemService(Context.LOCATION_SERVICE);

								final Location curNetLoc = mLocationManager
										.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
								final Location curGpsLoc = mLocationManager
										.getLastKnownLocation(LocationManager.GPS_PROVIDER);
								final Location bestLoc = getBestLocation(
										curNetLoc, curGpsLoc);
								if (bestLoc == null) {
									// Register the listener with the Location
									// Manager to receive location updates
									mLocationManager.requestLocationUpdates(
											LocationManager.NETWORK_PROVIDER,
											0, 0, LocationActivity.this);
									mLocationManager.requestLocationUpdates(
											LocationManager.GPS_PROVIDER, 0, 0,
											LocationActivity.this);
								} else {
									mDiaryHelper.addLocationEntry(bestLoc,
											mNewLocName);
									mListViewAdapter.notifyDataSetChanged();
									final CharSequence text = mNewLocName
											+ " remembered.";
									final int duration = Toast.LENGTH_SHORT;

									final Toast toast = Toast.makeText(
											LocationActivity.this
													.getApplicationContext(),
											text, duration);
									toast.show();
								}

							}
						});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.cancel();
							}
						});

				alert.show();

			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.locations_ListView) {
			menu.setHeaderTitle("Location Name");
			final String[] menuItems = { "Delete" };// getResources().getStringArray(R.array.menu);
													// // TODO: Create menu
													// resource
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mDiaryHelper.doneWithDb();
		if (mLocationManager != null) {
			// Remove the listener you previously added
			mLocationManager.removeUpdates(this);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		// Check whether the new location fix is newer or older
		final long timeDelta = location.getTime();// - location.getTime();
		final int TWO_MINUTES = 1000 * 60 * 2;
		final boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		if (location.getAccuracy() < 75 && isSignificantlyNewer
				&& mNewLocName != null) {
			mDiaryHelper.addLocationEntry(location, mNewLocName);
			mLocationManager.removeUpdates(this);
			mListViewAdapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}
