package edu.uwcse.pond.proto;

import java.text.ChoiceFormat;
import java.util.Date;

import edu.uwcse.pond.diary.DiaryDbHelper;
import edu.uwcse.pond.diary.LocationDiaryTableHelper;
import edu.uwcse.pond.nutrition.Consts.FoodClass;
import edu.uwcse.pond.nutrition.Consts.PointComponent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class LocationActivity extends Activity implements LocationListener{

	private DiaryDbHelper mDiaryHelper;
	private ListView mListView;
	private SimpleCursorAdapter mListViewAdapter;
	private LocationManager mLocationManager; 
	private String mNewLocName; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_entries);
	 	mDiaryHelper =  DiaryDbHelper.getDiaryDbHelper(getApplicationContext());
	 	mListView = (ListView)findViewById(R.id.locations_ListView);
	 	Cursor entries_cursor = mDiaryHelper.getLocationEntries(); 
	 	
	 	String[] from = new String[]{LocationDiaryTableHelper.COL_LOC_NAME};
	 	int[] to = new int[]{R.id.location_name_TextView};
        
		mListViewAdapter = new SimpleCursorAdapter(this, R.layout.location_list_entry, 
				entries_cursor, from, to);//DailyEntriesListViewAdapter(this, entries_cursor);
		mListView.setAdapter(mListViewAdapter);
		registerForContextMenu(mListView);

		Button newButton = (Button)findViewById(R.id.new_location_Button);
		newButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				final AlertDialog.Builder alert = new AlertDialog.Builder(LocationActivity.this);
				
				LayoutInflater inflater = getLayoutInflater();
				//FrameLayout f1 = (FrameLayout)alert.findViewById(android.R.id.body);
				//f1.addView(inflater.inflate(R.layout.dialog_view, f1, false));
				View layout = inflater.inflate(R.layout.location_name_dialog, null);
				
				final EditText input = (EditText)layout.findViewById(R.id.location_name_EditText);
				
				alert.setView(layout);
				
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) { 
						mNewLocName = input.getText().toString();
						
						// Check location listening is turned on ()
						// Turn on location listening
						// Acquire a reference to the system Location Manager
						mLocationManager = (LocationManager) LocationActivity.this.getSystemService(Context.LOCATION_SERVICE);
						
						Location curNetLoc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						Location curGpsLoc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						Location bestLoc = getBestLocation(curNetLoc, curGpsLoc);
						if (bestLoc == null){
							// Register the listener with the Location Manager to receive location updates
							mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, LocationActivity.this);
							mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, LocationActivity.this);
						}
						else{
							mDiaryHelper.addLocationEntry(bestLoc, mNewLocName);
							mListViewAdapter.notifyDataSetChanged();
							CharSequence text = mNewLocName + " remembered.";
							int duration = Toast.LENGTH_SHORT;

							Toast toast = Toast.makeText(LocationActivity.this.getApplicationContext(), text, duration);
							toast.show();
						}
						
					}
				});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								dialog.cancel();
							}
						});
				
				alert.show();
		        
				
				
				
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mDiaryHelper.doneWithDb();
		if (mLocationManager != null){
			// Remove the listener you previously added
			mLocationManager.removeUpdates(this);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	    ContextMenuInfo menuInfo) {
	  if (v.getId()==R.id.locations_ListView) {
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
	    menu.setHeaderTitle("Location Name");
	    String[] menuItems = {"Delete"};//getResources().getStringArray(R.array.menu); // TODO: Create menu resource
	    for (int i = 0; i<menuItems.length; i++) {
	      menu.add(Menu.NONE, i, i, menuItems[i]);
	    }
	  }
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	  int menuItemIndex = item.getItemId();
	  long list_item_id = mListViewAdapter.getItemId(info.position);
	  //String[] menuItems = getResources().getStringArray(R.array.menu);
	  //String menuItemName = menuItems[menuItemIndex];
	  //String listItemName = Countries[info.position];
	  
	  int result = mDiaryHelper.deleteLocation(list_item_id);
	  mListViewAdapter.notifyDataSetChanged();
	  return true;
	}
	
	private Location getBestLocation(Location loc1, Location loc2){
		long curTime = new Date().getTime();
		long timeDelta1 =  curTime - loc1.getTime();
		long timeDelta2 = curTime - loc2.getTime(); 
		
		    int TWO_MINUTES = 1000 * 60 * 2;
		   
		    
		 if (timeDelta1 < TWO_MINUTES){
			 if (timeDelta2 < TWO_MINUTES){
				 // They are both recent enough; return the more accurate location
				 return (loc1.getAccuracy()<loc2.getAccuracy()?loc1:loc2);
			 }
			 else{
				 // time1 is more recent, return it. 
				 return loc1; 
			 }
		 }else{
			 if (timeDelta2 < TWO_MINUTES){
				 return loc2; 
			 }

			 return null;
		 }
		    
		   
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		 // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime();// - location.getTime();
	    int TWO_MINUTES = 1000 * 60 * 2;
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;
	    
	    if (location.getAccuracy() < 75 && isSignificantlyNewer && mNewLocName != null){
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
