package edu.uwcse.pond.proto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import edu.uwcse.pond.diary.DiaryDbHelper;
import edu.uwcse.pond.diary.FoodDiaryTableHelper;
import edu.uwcse.pond.diary.PointsDiaryTableHelper;
import edu.uwcse.pond.nutrition.Consts;
import edu.uwcse.pond.nutrition.Consts.PointComponent;
import edu.uwcse.pond.proto.OverviewDayListViewAdapter.PlusOneButtonClickListener;
import edu.uwcse.pond.proto.OverviewDayListViewAdapter.ShowInfoButtonClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.ValueCallback;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewSwitcher;

public class DailyDetailActivity extends Activity implements OnGesturePerformedListener {

	public static final String DATA_WHICH_COMPONENT_INT_KEY = "whichComponent";
	
	public static final String DATA_WHICH_DATE_STRING_KEY = "whichDate";

	static final int DIALOG_DATE = 1; 
	
	private static ArrayList<PointComponent> COMPONENT_ORDER = new ArrayList<PointComponent>();
			
	static{ 
		COMPONENT_ORDER.add(PointComponent.ALL); 
		COMPONENT_ORDER.add(PointComponent.VEGGIE); 
		COMPONENT_ORDER.add(PointComponent.VEGGIE_GREEN); 
		COMPONENT_ORDER.add(PointComponent.GRAINS_WHOLE);
		COMPONENT_ORDER.add( PointComponent.GRAINS);
		COMPONENT_ORDER.add(PointComponent.FRUIT_WHOLE); 
		COMPONENT_ORDER.add( PointComponent.FRUIT);
		COMPONENT_ORDER.add(PointComponent.DAIRY); 
		COMPONENT_ORDER.add( PointComponent.PROTEIN);
		
		COMPONENT_ORDER.add( PointComponent.SODIUM);
		COMPONENT_ORDER.add(PointComponent.SUGAR); 
		COMPONENT_ORDER.add( PointComponent.SOLID_FATS);
	}
	
	public static final SimpleDateFormat DATE_DISPLAY_FORMAT = new SimpleDateFormat("EEE MMM d");
	
	private DiaryDbHelper mDiaryHelper;
	private ListView mListView;
	private TextView mTextView; 
	//private ToggleButton mToggleButton; 
	private DailyEntriesListViewAdapter mListViewAdapter;
	

	private GestureLibrary mGestureLibrary; 
	
	private PointComponent mCurComponent; 
	
	private Calendar mCurDate; 
	
	
	// the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

    			@Override
                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                    mCurDate.set(Calendar.YEAR, year);
                    mCurDate.set(Calendar.MONTH, monthOfYear);
                    mCurDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                
                    bindData(mCurComponent,  mCurDate);
    			}
    			
            };
        
    @Override
    protected void onStart() {
    	super.onStart(); 
		bindData(PointComponent.ALL, mCurDate); 
    };
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	outState.putString(DATA_WHICH_DATE_STRING_KEY, DiaryDbHelper.DB_DATE_STORE_FORMAT.format(mCurDate.getTime()));
    	super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onRestoreInstanceState(savedInstanceState);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.daily_entries);
		
		if (savedInstanceState != null && 
				savedInstanceState.containsKey(DATA_WHICH_DATE_STRING_KEY)){
			String savedDate = savedInstanceState.getString(DATA_WHICH_DATE_STRING_KEY);
			Date dt;
			try {
				dt = DiaryDbHelper.DB_DATE_STORE_FORMAT.parse(savedDate);
				mCurDate = Calendar.getInstance(); 
				mCurDate.setTime(dt);
			} catch (ParseException e) {
				mCurDate = Calendar.getInstance(); 
			}
		}else{
			mCurDate = Calendar.getInstance(); 
		}
		
		mGestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		if (!mGestureLibrary.load()) {
		    finish();
		}
		
	 	mDiaryHelper =  DiaryDbHelper.getDiaryDbHelper(getApplicationContext());
	 	mListView = (ListView)findViewById(R.id.daily_entries_ListView);
	 	registerForContextMenu(mListView);
	 	
	 	
	 	mTextView = (TextView)findViewById(R.id.textView8);
	 /*	mToggleButton = (ToggleButton)findViewById(R.id.daily_entries_ShowWeek_ToggleButton);
	 	mToggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (mCurComponent == PointComponent.ALL){
					mCurComponent = getNextComponent(PointComponent.ALL);
				}
				
				bindData(mCurComponent, 
						(mToggleButton.isChecked()?VALUE_SHOW_WEEK:VALUE_SHOW_DAY), 
						mCurDate);
			}
		});*/
	 	
	 	Bundle b = this.getIntent().getExtras();
        
	 	GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
		gestures.addOnGesturePerformedListener(this);
		
		
		Button home_button = (Button)findViewById(R.id.home_button);
		home_button.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		        startActivity(new Intent(DailyDetailActivity.this, OverviewActivity.class));
		    }
		});
		
		Button prev_button = (Button)findViewById(R.id.prev_day_button);
		prev_button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCurDate.add(Calendar.DATE, -1);
				bindData(mCurComponent, mCurDate);
			}
		});
		
		Button next_button = (Button)findViewById(R.id.next_day_button);
		next_button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCurDate.add(Calendar.DATE, 1);
				bindData(mCurComponent, mCurDate);
			}
		});
		
		Button add_food_button = (Button)findViewById(R.id.add_food_button);
		add_food_button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DailyDetailActivity.this, FoodResults2ListActivity.class);
		    	startActivity(intent);
			}
		});
	}


	private void bindData(PointComponent component, Calendar whichDay) {
		// Remember the values the new cursor will represent
		mCurComponent = component; 
		mCurDate = whichDay; 
		
		//Button next_button = (Button)findViewById(R.id.next_day_button);
		// If mCurDate is today, disable the button; otherwise enable it. 
		Calendar today = Calendar.getInstance(); 
		if (mCurDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) && 
				(mCurDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)) && 
				(mCurDate.get(Calendar.DATE) == today.get(Calendar.DATE))){
			mTextView.setText( "Today" );
			((Button)findViewById(R.id.next_day_button)).setEnabled(false);
		}
		else{
			mTextView.setText( DATE_DISPLAY_FORMAT.format(mCurDate.getTime()));
			((Button)findViewById(R.id.next_day_button)).setEnabled(true);
		}
		
		String desc = "All Components"; 
		if (mCurComponent != null)
			desc = mCurComponent.getDesc();
		
		
		
		Cursor entries_cursor; 

			// Show day
			entries_cursor = mDiaryHelper.getAllEntriesForDay(mCurDate, mCurComponent);
			
		
		
		if (mListViewAdapter != null){
			mListViewAdapter.changeCursor(entries_cursor);
			mListViewAdapter.setPointComponentFilter(component);
			mListViewAdapter.notifyDataSetChanged();
		}
		else{
			mListViewAdapter = new DailyEntriesListViewAdapter(this, entries_cursor);
			mListViewAdapter.setPointComponentFilter(component);
			mListView.setAdapter(mListViewAdapter);
			
			
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long itemId) {
					Cursor clickItem = (Cursor) mListViewAdapter.getItem(position);
					System.out.println(clickItem);
					
				//	clickItem.moveToFirst();
					long id = clickItem.getLong(20);
					System.out.println(id);
					int curSource = clickItem.getInt(clickItem.getColumnIndex("source"));
					
				/*	AlertDialog.Builder builder = new AlertDialog.Builder(EntriesListActivity.this);
					builder.setView(EntriesListActivity.this.getLayoutInflater().inflate(R.layout.food_entry_edit, 
																(ViewGroup) findViewById(R.id.layout_root_nut_info_card)));
					
					builder.setPositiveButton("Save Changes",  new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                
				           }});
					
					builder.setNegativeButton("Cancel",  new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                
				           }});
					
					builder.setCancelable(true);
					
					AlertDialog alertDialog = builder.create();
					

					alertDialog.show();
					*/
					
					// From the id, get the source, and if the source is 2 then do "PointEntryEditActivity", 
					// or if it's 1 do "FoodEntryEditActivity". 
					
					if (curSource == 2 || curSource == 3){
						Intent intent = new Intent(DailyDetailActivity.this, PointEntryEditActivity.class);
				    	Bundle extras = intent.getExtras();
				    	if (extras == null){
				    		extras = new Bundle();
				    	}
				    	// move cursor to position/itemId/something-- to be the right one.  
				    	extras.putLong(FoodEntryEditActivity.DATA_DIARY_DB_ID_KEY, itemId);
				    	
				    	intent.putExtra(FoodEntryEditActivity.DATA_DIARY_DB_ID_KEY, itemId);
				    	
				        startActivity(intent);
					}
					else{
					
						Intent intent = new Intent(DailyDetailActivity.this, FoodEntryEditActivity.class);
				    	Bundle extras = intent.getExtras();
				    	if (extras == null){
				    		extras = new Bundle();
				    	}
				    	// move cursor to position/itemId/something-- to be the right one.  
				    	extras.putLong(FoodEntryEditActivity.DATA_DIARY_DB_ID_KEY, itemId);
				    	
				    	intent.putExtra(FoodEntryEditActivity.DATA_DIARY_DB_ID_KEY, itemId);
				    	
				        startActivity(intent);
					}
				}
			});
			
			
		}
		
		
		
	}
	
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		 switch (id) {
		    case DIALOG_DATE:
		        return new DatePickerDialog(this,
		                    mDateSetListener,
		                    mCurDate.get(Calendar.YEAR), 
		                    mCurDate.get(Calendar.MONTH), 
		                    mCurDate.get(Calendar.DAY_OF_MONTH));
		    }
		    return null;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mDiaryHelper.doneWithDb(); 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.entries_list_menu, menu);
       
        
        return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		
		case R.id.entrie_list_changeDate_menuItem:
			showDialog(DIALOG_DATE);
			break; 
		}
		
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		 Prediction best_pred = null; 
		 
		 Iterator< Prediction> iter = mGestureLibrary.recognize(gesture).iterator();
		 while(iter.hasNext()){
			 Prediction p = iter.next(); 
			 
			 if (best_pred == null || 
					 p.score > best_pred.score){
				 best_pred = p; 
			 }
		 }
		// Toast.makeText(this, best_pred.name, Toast.LENGTH_SHORT).show();
		 
		 if (best_pred.name.equalsIgnoreCase("r2l")){//"left")){
				 // Increment the day
				 mCurDate.add(Calendar.DATE, 1);
				 bindData(mCurComponent, mCurDate);
				 
			 
		 }else if(best_pred.name.equalsIgnoreCase("l2r")){//"right")){
			
				 // Decrement the day
				 mCurDate.add(Calendar.DATE, -1);
				 bindData(mCurComponent, mCurDate);
			 
		 }
		 else if(best_pred.name.equalsIgnoreCase("up")){
			 // Get the next component
			 bindData(getNextComponent(mCurComponent), mCurDate);
			 
		 }
		 else if(best_pred.name.equalsIgnoreCase("down")){
			// Get the previous component
			bindData(getPrevComponent(mCurComponent),  mCurDate);
			 
		 }
	}
	
	private PointComponent getPrevComponent(PointComponent start){
		int which = COMPONENT_ORDER.indexOf(start); 
		return (which==0)?COMPONENT_ORDER.get(0):COMPONENT_ORDER.get(which-1);
	}
	
	private PointComponent getNextComponent(PointComponent start){
		int which = COMPONENT_ORDER.indexOf(start); 
		return (which==COMPONENT_ORDER.size()-1)?COMPONENT_ORDER.get(which):COMPONENT_ORDER.get(which+1);
	}
	
	

	
	public class PrettyArrayAdapter extends ArrayAdapter<PointComponent>{
		public PrettyArrayAdapter(Context context, List<PointComponent> data) {
			super(context, android.R.layout.simple_spinner_item, data);
			
		}

		/*
		public PrettyCursorAdapter(Context context, T[] spinnerItems){
			super(context, android.R.layout.simple_spinner_item, spinnerItems);
		}
		*/
		
		
		
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
		
		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = DailyDetailActivity.this.getLayoutInflater();
				convertView = inflater.inflate(
						R.layout.pretty_spinner_drop_down_item, null);
			}
			
			CheckedTextView txtViewTitle;
			ImageView imgView; 
			
			// Set the component name
			txtViewTitle = (CheckedTextView) convertView
					.findViewById(R.id.item_CheckedTextView);
			PointComponent thisOne = getItem(position);
			txtViewTitle.setText(thisOne.getDesc());
			txtViewTitle.setTextAppearance(getContext(), R.style.locationSpinnerTextStyle);
			
			imgView = (ImageView)convertView.findViewById(R.id.imageView1);
			imgView.setImageResource(thisOne.getDrawableId());
			
			//if (position == this.)

			return convertView;
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	    ContextMenuInfo menuInfo) {
	  if (v.getId()==R.id.daily_entries_ListView) {
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
	    menu.setHeaderTitle("Diary Entry (?)");
	    String[] menuItems = {"Edit", "Delete"};//getResources().getStringArray(R.array.menu); // TODO: Create menu resource
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
	  
	  switch (menuItemIndex){
		  case 0: 
			  Toast.makeText(getApplicationContext(), "Edit " + list_item_id,
						Toast.LENGTH_SHORT).show();
			  return true; 
		  case 1: 
			  Toast.makeText(getApplicationContext(), "Delete " + list_item_id,
						Toast.LENGTH_SHORT).show();
			  mDiaryHelper.deleteDiaryEntry(list_item_id);
			  bindData(mCurComponent, mCurDate);
			  return true;
	  }
	  
	  //String[] menuItems = getResources().getStringArray(R.array.menu);
	  //String menuItemName = menuItems[menuItemIndex];
	  //String listItemName = Countries[info.position];
	  
//	  int result = mDiaryHelper.deleteLocation(list_item_id);
//	  mListViewAdapter.notifyDataSetChanged();
	  return true;
	}
	
}
