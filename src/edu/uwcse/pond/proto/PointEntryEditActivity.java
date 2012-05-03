package edu.uwcse.pond.proto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.uwcse.pond.diary.DiaryDbHelper;
import edu.uwcse.pond.diary.FoodDiaryTableHelper;
import edu.uwcse.pond.diary.LocationDiaryTableHelper;
import edu.uwcse.pond.diary.PointsDiaryTableHelper;
import edu.uwcse.pond.nutrition.Consts.FoodClass;
import edu.uwcse.pond.nutrition.Consts.Nutrient;
import edu.uwcse.pond.nutrition.Consts.PointComponent;
import edu.uwcse.pond.nutrition.NutritionDbHelper;
import edu.uwcse.pond.nutrition.NutritionEntry;
import edu.uwcse.pond.nutrition.NutritionEntry.Serving;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter; 
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;
import android.widget.Toast;

public class PointEntryEditActivity extends Activity{

	
	
	public static final String DATA_DIARY_DB_ID_KEY = "diaryDbId";
	
	public static final SimpleDateFormat TIME_DISPLAY_FORMAT = new SimpleDateFormat("h:mm ");
	public static final SimpleDateFormat DATE_DISPLAY_FORMAT = new SimpleDateFormat("EEE, MMM d");//"yyyy-MM-dd");
	
	private static final int DIALOG_EDIT_COMPONENT = 1;
	
	private static final int DIALOG_TIME = 2;
	
	private static final int DIALOG_DATE = 3;
	
	ContentValues entryValues; 
	
	DiaryDbHelper mDiaryDbHelper;
	
	private Button saveButton; 
	
	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener =
			new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, 
				int monthOfYear, int dayOfMonth) {



			Date curDate;
			try {
				curDate = DiaryDbHelper.DB_DATE_STORE_FORMAT.parse(
						(String)entryValues.get(FoodDiaryTableHelper.COL_TIME_ENTERED));

				Calendar cal = Calendar.getInstance();
				cal.setTime(curDate); 
				cal.set(Calendar.YEAR, year);
				cal.set(Calendar.MONTH, monthOfYear);
				cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

				entryValues.put(FoodDiaryTableHelper.COL_TIME_ENTERED, DiaryDbHelper.DB_DATE_STORE_FORMAT.format(cal.getTime()));
				saveButton.setEnabled(true);
				fillDateInfo((String)entryValues.get(FoodDiaryTableHelper.COL_TIME_ENTERED));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	// the callback received when the user "sets" the date in the dialog
	private OnTimeSetListener mTimeSetListener =
			new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Date curDate;
			try {
				curDate = DiaryDbHelper.DB_DATE_STORE_FORMAT.parse(
						(String)entryValues.get(FoodDiaryTableHelper.COL_TIME_ENTERED));

				Calendar cal = Calendar.getInstance();
				cal.setTime(curDate); 
				cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
				cal.set(Calendar.MINUTE, minute);

				entryValues.put(FoodDiaryTableHelper.COL_TIME_ENTERED, DiaryDbHelper.DB_DATE_STORE_FORMAT.format(cal.getTime()));
				saveButton.setEnabled(true);
				fillTimeInfo((String)entryValues.get(FoodDiaryTableHelper.COL_TIME_ENTERED));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};



	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check for foodId passed in a bundle
        setContentView(R.layout.point_entry_edit);//food_entry_edit);
        mDiaryDbHelper = DiaryDbHelper.getDiaryDbHelper(this);
        
        
        Bundle b = this.getIntent().getExtras();
        		
        long pointDiaryId = b.getLong(DATA_DIARY_DB_ID_KEY);
        if (pointDiaryId == 0){
        	pointDiaryId = this.getIntent().getLongExtra(DATA_DIARY_DB_ID_KEY, -1);
        }
        entryValues = mDiaryDbHelper.getPointsEntryAsVals(pointDiaryId);//getFoodEntryFromPointsEntryAsVals(pointDiaryId);
        
        if (entryValues.size() !=0){
	       // long food_id = entryValues.getAsLong(FoodDiaryTableHelper.COL_FOODID);
	       // mEntry = mNutritionDbHelper.getNutritionEntry((int) food_id);
	        
	        //fillFoodDiaryEntry(mEntry, entryValues);
	        fillPointsDiaryEntry(entryValues);
	        fillLocationSpinner(entryValues.getAsInteger(FoodDiaryTableHelper.COL_LOCATION_ID));
        }
    }

	private void fillLocationSpinner(int locId) {
		Spinner locationSpinner = (Spinner)findViewById(R.id.food_entry_edit_locationSpinner);
		Cursor entries_cursor = mDiaryDbHelper.getLocationEntries(); 
		
		String[] from = new String[]{LocationDiaryTableHelper.COL_LOC_NAME};
		int[] to = new int[]{android.R.id.text1};//R.id.location_name_TextView};
		
		PrettyCursorAdapter mLocationAdapter = new PrettyCursorAdapter(this, android.R.layout.simple_spinner_item, 
				entries_cursor, from, to);//DailyEntriesListViewAdapter(this, entries_cursor);
		mLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		locationSpinner.setAdapter(mLocationAdapter);
		
		for(int i=0; i<mLocationAdapter.getCount(); i++){
			if (mLocationAdapter.getItemId(i) == locId){
				locationSpinner.setSelection(i);
			}
		}
		
		locationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long id) {
				entryValues.put(PointsDiaryTableHelper.COL_LOCATION_ID, id);
				saveButton.setEnabled(true);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		 
	}
	
	
	
	/*
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		 MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.food_detail_menu, menu);
	        // Invoke the Register activity
	        //menu.getItem(0).setIntent(new Intent(this, AccountsActivity.class));
	        
	        return true;	
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch(item.getItemId()){
		case R.id.add_to_journal_menuItem:
			DiaryDbHelper diary_db =  DiaryDbHelper.getDiaryDbHelper(getApplicationContext());
		/*	ContentValues vals = mEntry.getPointsValsForAmount(
					(Serving)mServingType_Spinner.getSelectedItem(), 
					Double.parseDouble(mServingAmountValue_EditText.getText().toString()));
			diary_db.createNewPointsEntry(vals);
		*/
	/*		diary_db.createNewFoodEntry(mEntry, (Serving)mServingType_Spinner.getSelectedItem(),
					Double.parseDouble(mServingAmountValue_EditText.getText().toString()));
			diary_db.doneWithDb();
			
			// go back to overview
			Intent intent = new Intent(this, OverviewActivity.class);
	    	/*Bundle extras = intent.getExtras();
	    	if (extras == null){
	    		extras = new Bundle();
	    	}
	    	extras.putLong(FoodDetailActivity.DATA_FOOD_ID_KEY, id);
	    	intent.putExtra(FoodDetailActivity.DATA_FOOD_ID_KEY, id);*/
	 /*       startActivity(intent);
			
			return true; 
		}
		
		return false; 	
		
	}*/
	
	/*
	private void fillFoodDiaryEntry(NutritionEntry entry, ContentValues foodDiaryEntry) {
         TextView nameView = (TextView)findViewById(R.id.food_entry_edit_foodName_TextView);
         nameView.setText(entry.getFoodName());
         
         mServingType_Spinner = (Spinner)findViewById(R.id.food_entry_edit_serving_Spinner);
         ArrayAdapter<NutritionEntry.Serving> adapter = new ArrayAdapter<NutritionEntry.Serving>(this, android.R.layout.simple_spinner_item, entry.getServings());
         mServingType_Spinner.setAdapter(adapter);
         
         adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         //NumberPicker num_picker = (NumberPicker)findViewById(R.id.food_detail_serving_size_NumberPicker);
         
         mServingType_Spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				EditText amt_txt = (EditText)findViewById(R.id.food_entry_edit_servingAmt_editText);
				TextView srvg_desc = (TextView)findViewById(R.id.food_entry_edit_servingNote_TextView);
				
				NutritionEntry.Serving srvg = (Serving) arg0.getSelectedItem();
				
				amt_txt.setText(Double.toString(srvg.getServingAmtVal()));
				
				srvg_desc.setText(srvg.getServingAmtNote());
				
				updateValuesOnServingChange();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
         
         mServingAmountValue_EditText = (EditText)findViewById(R.id.food_entry_edit_servingAmt_editText);
         
         mServingAmountValue_EditText.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				showDialog(DIALOG_AMOUNT);
				return true; 
			}
		});
         
         mServingAmountValue_EditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus){
					updateValuesOnServingChange();
				}
			}
		});
         
         mServingAmountValue_EditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
         
         fillTimeInfo( foodDiaryEntry.getAsString(FoodDiaryTableHelper.COL_TIME_ENTERED));
         fillDateInfo( foodDiaryEntry.getAsString(FoodDiaryTableHelper.COL_TIME_ENTERED));
         
 		
         saveButton = (Button)findViewById(R.id.save_button);
 		saveButton.setEnabled(false);
 		saveButton.setOnClickListener(
 				new OnClickListener() {
 			
 			@Override
 			public void onClick(View v) {
 			
 				//TODO: Update the date/time with edits? 
 				mDiaryDbHelper.updateFoodEntry(entryValues, mEntry, 
 						(Serving)mServingType_Spinner.getSelectedItem());
 				
 				Context context = getApplicationContext();
 				CharSequence text = "Entry updated";
 				int duration = Toast.LENGTH_SHORT;

 				Toast toast = Toast.makeText(context, text, duration);
 				toast.show();
 				saveButton.setEnabled(false);
 			}
 		});
 		
 		Button changeTimeButton = (Button)findViewById(R.id.point_entry_edit_time_Button);
 		Button changeDateButton = (Button)findViewById(R.id.point_entry_edit_date_Button);
 		
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
*/
	private void fillTimeInfo(String datetime) {
		
         try {
        	 Date entry_date = DiaryDbHelper.DB_DATE_STORE_FORMAT.parse(datetime);
		
	         //Time
	         TextView timeTextView = (TextView)findViewById(R.id.food_entry_edit_time_TextView);
	         timeTextView.setText(TIME_DISPLAY_FORMAT.format(entry_date)); 
	       
	         
         } catch (ParseException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
	}
	
	private void fillDateInfo(String datetime) {
		
        try {
       	 Date entry_date = DiaryDbHelper.DB_DATE_STORE_FORMAT.parse(datetime);

	         TextView dateTextView = (TextView)findViewById(R.id.food_entry_edit_date_TextView);
	         dateTextView.setText(DATE_DISPLAY_FORMAT.format(entry_date));
	         
        } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void fillPointsDiaryEntry(ContentValues ptsEntry) {
        EditText nameView = (EditText)findViewById(R.id.point_entry_edit_foodName_EditText);
        nameView.setText(ptsEntry.getAsString(PointsDiaryTableHelper.COL_COMMENT));
        
        nameView.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				entryValues.put(PointsDiaryTableHelper.COL_COMMENT, v.getText().toString());
				saveButton.setEnabled(true);
				return true;
			}
		});
      
        
        fillTimeInfo(ptsEntry.getAsString(PointsDiaryTableHelper.COL_TIME_ENTERED));
        fillDateInfo(ptsEntry.getAsString(PointsDiaryTableHelper.COL_TIME_ENTERED));
        
        saveButton = (Button)findViewById(R.id.save_button);
 		saveButton.setEnabled(false);
 		saveButton.setOnClickListener(
 				new OnClickListener() {
 			
 			@Override
 			public void onClick(View v) {
 			
 				//TODO: Update the date/time with edits? 
 				//mDiaryDbHelper.updateFoodEntry(entryValues, mEntry, 
 				//		(Serving)mServingType_Spinner.getSelectedItem());
 				
 				mDiaryDbHelper.updatePointsEntry(entryValues);
 				
 				Context context = getApplicationContext();
 				CharSequence text = "Entry updated";
 				int duration = Toast.LENGTH_SHORT;

 				Toast toast = Toast.makeText(context, text, duration);
 				toast.show();
 				saveButton.setEnabled(false);
 			}
 		});
 		
 		Button changeTimeButton = (Button)findViewById(R.id.point_entry_edit_time_Button);
 		Button changeDateButton = (Button)findViewById(R.id.point_entry_edit_date_Button);
 		
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
 		
 		Button editComponentButton = (Button)findViewById(R.id.button1);
 		editComponentButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_EDIT_COMPONENT);
			}
		});
 		
 		updateTokensView();
			
		
   }

	private void updateTokensView() {
		LinearLayout layout = (LinearLayout)findViewById(R.id.point_entry_edit_tokenHolder);
		
		layout.removeAllViews();
		
		// Go through each column, determine if it's empty, and get the drawable from that. 

			populatePointsView(entryValues, this, layout, PointsDiaryTableHelper.COL_VEGGIE_VAL);
			populatePointsView(entryValues, this, layout, PointsDiaryTableHelper.COL_VEGGIE_WHOLE_VAL);

			populatePointsView(entryValues, this, layout, PointsDiaryTableHelper.COL_GRAINS_VAL);
			populatePointsView(entryValues, this, layout, PointsDiaryTableHelper.COL_GRAINS_WHOLE_VAL);
			populatePointsView(entryValues, this, layout, PointsDiaryTableHelper.COL_FRUIT_VAL);
			populatePointsView(entryValues, this, layout, PointsDiaryTableHelper.COL_FRUIT_WHOLE_VAL);
			populatePointsView(entryValues, this, layout, PointsDiaryTableHelper.COL_DAIRY_VAL);
			populatePointsView(entryValues, this, layout, PointsDiaryTableHelper.COL_PROTEIN_VAL);
			populatePointsView(entryValues, this, layout, PointsDiaryTableHelper.COL_OILS_VAL);
			populatePointsView(entryValues, this, layout, PointsDiaryTableHelper.COL_SUGAR_VAL);
			populatePointsView(entryValues, this, layout, PointsDiaryTableHelper.COL_SODIUM_VAL);
			populatePointsView(entryValues, this, layout, PointsDiaryTableHelper.COL_SOLID_FATS_VAL);
	}

	private void populatePointsView(ContentValues vals, 
									Context context, 
									LinearLayout layout, 
									String col_name) {
		
		double val = vals.getAsDouble(col_name);//cursor.getDouble(col_id);
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
	/*
	private void updateValuesOnServingChange() {
		String amt = mServingAmountValue_EditText.getText().toString();
		Serving srvg = (Serving) mServingType_Spinner.getSelectedItem();
		double amt_d = Double.parseDouble(amt);
		
		double multiplier = PointEntryEditActivity.this.mEntry.getServingMultiplier(srvg, amt_d);
		if (Double.isInfinite(multiplier) || Double.isNaN(multiplier)){
			multiplier = 2.0; // TODO: fix hack by fixing the nutrition db
		}
	//	mPointsListViewAdapter.setServingsMultiplier(multiplier);
	//	mPointsListViewAdapter.notifyDataSetChanged();
	}

	public class HashMapAdapter2 extends BaseAdapter {

		 private Map mData;// = new HashMap<Nutrient, Double>();
		 
		 private Object[] mKeys;
		 
		 
		 public HashMapAdapter2(Map data){
		        mData  = data;
		        mKeys = mData.keySet().toArray(new Object[data.size()]);
		    }
		 
		
		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(mKeys[position]);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup arg2) {
			Object key = mKeys[pos];
	        String value = getItem(pos).toString();

	        View v = convertView;
	        if (v == null) {
	            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.nutrient_detail_list_view_row, null);
	        }
	       // String o = mData.get(mKeys[pos]);
	        if (value != null) {
	                TextView tt = (TextView) v.findViewById(R.id.nut_name_list_label);
	                TextView bt = (TextView) v.findViewById(R.id.nut_value_list_label);
	                if (tt != null) {
	                      tt.setText("Name: "+key);                            }
	                if(bt != null){
	                      bt.setText("Value: "+ value );
	                }
	        }
	        return v;
		}

	
	}
	
	*/

	protected Dialog onCreateDialog(int id) {
	    Dialog dialog;
	    switch(id) {
	    case DIALOG_EDIT_COMPONENT:
	    	dialog = buildEditComponentDialog();
	    	break; 
	    case DIALOG_DATE:
	    	Date curDate; 
	    	try {
				curDate = DiaryDbHelper.DB_DATE_STORE_FORMAT.parse(
						(String)entryValues.get(PointsDiaryTableHelper.COL_TIME_ENTERED));
			} catch (ParseException e) {
				curDate = Calendar.getInstance().getTime(); 
			}
		
	    	int yr = curDate.getYear() + 1900; 
	    	int mth = curDate.getMonth(); 
	    	int date = curDate.getDate(); 
	    	
	        return new DatePickerDialog(this,
	                    mDateSetListener,
	                    yr, mth, date);
	    case DIALOG_TIME:
	    	Date curTime; 
	    	try {
				curTime = DiaryDbHelper.DB_DATE_STORE_FORMAT.parse(
						(String)entryValues.get(PointsDiaryTableHelper.COL_TIME_ENTERED));
			} catch (ParseException e) {
				curTime = Calendar.getInstance().getTime(); 
			}
		
	    	TimePickerDialog diag = new TimePickerDialog(this, 
	        		mTimeSetListener, 
	        		curTime.getHours(), curTime.getMinutes(), false);
	    	
	    	return diag; 
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
	
	private AlertDialog buildEditComponentDialog(){
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.component_amount_dialog, null);
		alert.setView(layout);
		alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				saveButton.setEnabled(true);
				updateTokensView();
			}
		});
		
		final EditText comp_amount_editText = (EditText)layout.findViewById(R.id.food_amount_whole_EditText);
		
		final Spinner chooseCompSpinner = (Spinner)layout.findViewById(R.id.choose_component_Spinner);
	 	/*ArrayAdapter<PointComponent> adapter = new ArrayAdapter<Consts.PointComponent>(this, 
	 			android.R.layout.simple_spinner_item, COMPONENT_ORDER);*/
	 	final PrettyComponentArrayAdapter adapter = new PrettyComponentArrayAdapter(this, 
	 			WeeklySummaryActivity.COMPONENT_ORDER);
	 	chooseCompSpinner.setAdapter(adapter);

	 	chooseCompSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int postion, long id) {
				
				// Get the EditText 
				// Fill it with the appropriate amount
				// COMPONENT_ORDER.get(postion)
				comp_amount_editText.setText(entryValues.getAsString(
						((PointComponent)arg0.getSelectedItem()).getPtDbColName()));
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	 	
	 	Button increaseButton = (Button)layout.findViewById(R.id.food_amount_whole_increase_Button);
	 	increaseButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// get the value from the text box 
				// increase it
				// save it to the entry 
				PointComponent selComp = (PointComponent)chooseCompSpinner.getSelectedItem();
				double curVal = entryValues.getAsDouble(selComp.getPtDbColName());
				curVal += 1.0; 
				entryValues.put(selComp.getPtDbColName(), curVal);
				comp_amount_editText.setText(Double.toString(curVal));
				saveButton.setEnabled(true);
			}
		});
	 	
	 	Button decreaseButton = (Button)layout.findViewById(R.id.food_amount_whole_decrease_Button);
	 	decreaseButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// get the value from the text box 
				// decrease it
				// save it to the entry 
				PointComponent selComp = (PointComponent)chooseCompSpinner.getSelectedItem();
				double curVal = entryValues.getAsDouble(selComp.getPtDbColName());
				curVal -= 1.0; 
				entryValues.put(selComp.getPtDbColName(), curVal);
				comp_amount_editText.setText(Double.toString(curVal));
				saveButton.setEnabled(true);
			}
		});
	 	
		return alert.create();
	}
	/*
	protected void onPrepareDialog(int id, Dialog dialog) {
	    
	    switch(id) {
	    case DIALOG_AMOUNT:
	    	String amount = mServingAmountValue_EditText.getText().toString();
	    	int dec = amount.indexOf(".");
	    	((EditText)dialog.findViewById(R.id.food_amount_whole_EditText)).setText(amount.substring(0,dec));
	    	break; 
	    //case DIALOG_GET_FOOD_QUERY:
	        // do the work to define the game over Dialog
	    	
	        //break;
	    default:
	        dialog = null;
	    }
	    
	}
	
	private AlertDialog buildAmountDialog() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.food_amount_dialog, null);
		
		final EditText whole_num = (EditText)layout.findViewById(R.id.food_amount_whole_EditText);
		final EditText part_num = (EditText)layout.findViewById(R.id.food_amount_part_EditText);
		
		((Button)layout.findViewById(R.id.food_amount_whole_increase_Button)).setOnClickListener(
				new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int whole_amt = Integer.parseInt(whole_num.getText().toString());
				int new_amt = whole_amt + 1; 
				
				whole_num.setText(Integer.toString(new_amt));
			}
		});
		
		((Button)layout.findViewById(R.id.food_amount_whole_decrease_Button)).setOnClickListener(
				new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int whole_amt = Integer.parseInt(whole_num.getText().toString());
				if (whole_amt > 0){
					int new_amt = whole_amt - 1; 
					
					whole_num.setText(Integer.toString(new_amt));
				}
			}
		});
		
		
		alert.setView(layout);
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
				mServingAmountValue_EditText.setText(whole_num.getText() + ".0");
				updateValuesOnServingChange();
				int whole_amt = Integer.parseInt(whole_num.getText().toString());
				
				
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
/*
		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});*/
		/*
		alert.setCancelable(true);
		return alert.create();
		
	}
	
	
	*/
}
