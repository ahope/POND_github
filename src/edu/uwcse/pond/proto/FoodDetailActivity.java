package edu.uwcse.pond.proto;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.uwcse.pond.diary.DiaryDbHelper;
import edu.uwcse.pond.diary.ActionLogDbHelper.Action;
import edu.uwcse.pond.nutrition.Consts.FoodClass;
import edu.uwcse.pond.nutrition.Consts.Nutrient;
import edu.uwcse.pond.nutrition.Consts.PointComponent;
import edu.uwcse.pond.nutrition.Consts;
import edu.uwcse.pond.nutrition.NutritionDbHelper;
import edu.uwcse.pond.nutrition.NutritionEntry;
import edu.uwcse.pond.nutrition.NutritionEntry.Serving;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView.ScaleType;
import android.widget.ArrayAdapter; 
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class FoodDetailActivity extends Activity implements Runnable{

	public static final String DATA_FOOD_ID_KEY = "foodId";
	
	public static final String DATA_AMOUNT_KEY = "amount";
	
	public static final String DATA_SERVING_ID_KEY = "servingId";
	
	public static final String DATA_DATETIME_EATEN_KEY = "datetimeEaten";
	
	public static final String DATA_DIARY_DB_ID_KEY = "diaryDbId";
	
	
	private DiaryDbHelper mDiaryHelper;
	
	private static final int DIALOG_AMOUNT = 1;
	
	NutritionDbHelper mDbHelper; 
	
	private NutritionEntry mEntry; 
	
	private EditText mServingAmountValue_EditText; 
	
	private Spinner mServingType_Spinner; 
	
	private double mServingMultiplier = 1.0;

	private ProgressDialog mProgressDialog; 
	
	private Map<Consts.PointComponent,Double> mDiaryEntries;
	
	private String[] PART_NUMS = {"0", "1/4", "1/3", "1/2", "2/3", "3/4"};
	private double[] PART_NUMS_VALS = {0, 0.25, 0.33, 0.5, 0.67, 0.75};
	
	private int mPartNumSelectedIndex = 0; 

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_nutrition_info_card);
        
        mProgressDialog = ProgressDialog.show(FoodDetailActivity.this, "", 
                "Loading. Please wait...", true);
        
        Thread thread = new Thread(this);
        thread.start();
        
        
    }

	private void loadData() {
		// Check for foodId passed in a bundle
        
        DiaryDbHelper diary_helper =  DiaryDbHelper.getDiaryDbHelper(getApplicationContext());
        mGoals = diary_helper.getMostRecentGoalAsMap();
        diary_helper.doneWithDb(); 
        
        mDbHelper = new NutritionDbHelper(this);
        mDbHelper.open();//openDataBase();
        
        Bundle b = this.getIntent().getExtras();
        		
        long id = b.getLong(DATA_FOOD_ID_KEY);
        mEntry = mDbHelper.getNutritionEntry((int) id);
        
        mDiaryHelper = DiaryDbHelper.getDiaryDbHelper(getApplicationContext());
	 	mDiaryHelper.logAction(Action.VIEW_FOOD_DETAIL, (int)id, -1, "View food detail", mEntry.getFoodName());
	 	
	 	long time = b.getLong(DATA_DATETIME_EATEN_KEY);
	 	if (time == 0L){
	 		Date today = new Date();
	 		today.setHours(0);
	 		today.setMinutes(0);
	 		today.setSeconds(0);
	 		time = today.getTime(); 
	 	}
        mDiaryEntries = mDiaryHelper.getSummedPointsEntryAsMap(new Date(time));//new Date());
        
        
	}	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mDbHelper.close(); 
	}
	
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
			return addRecordToJournal(); 
		}
		
		return false; 	
		
	}

	private boolean addRecordToJournal() {
		DiaryDbHelper diary_db =  DiaryDbHelper.getDiaryDbHelper(getApplicationContext());
/*	ContentValues vals = mEntry.getPointsValsForAmount(
				(Serving)mServingType_Spinner.getSelectedItem(), 
				Double.parseDouble(mServingAmountValue_EditText.getText().toString()));
		diary_db.createNewPointsEntry(vals);
*/
		diary_db.createNewFoodEntry(mEntry, (Serving)mServingType_Spinner.getSelectedItem(),
				Double.parseDouble(mServingAmountValue_EditText.getText().toString()));
		diary_db.doneWithDb();
		
		// go back to overview
//aha		Intent intent = new Intent(this, OverviewActivity.class);
		/*Bundle extras = intent.getExtras();
		if (extras == null){
			extras = new Bundle();
		}
		extras.putLong(FoodDetailActivity.DATA_FOOD_ID_KEY, id);
		intent.putExtra(FoodDetailActivity.DATA_FOOD_ID_KEY, id);*/
//aha        startActivity(intent);
		
		setResult(RESULT_OK);
		finish(); 
		return true;
	}
	
	
	private void fillData(NutritionEntry entry, 
						Map<PointComponent, Integer> goals, 
						Map<PointComponent,Double> diary) {
		
         TextView nameView = (TextView)findViewById(R.id.food_detail_foodName_TextView);
         nameView.setText(entry.getFoodName());

         mPrimaryPointVizHolder = (LinearLayout)findViewById(R.id.food_detail_Points_ListLayout);
         mSecondaryPointVizHolder = (LinearLayout)findViewById(R.id.food_detail_PointsExtra_ListLayout);
        
         final Button show_hide_button = (Button)findViewById(R.id.food_detail_ShowMoreButton);
         
         show_hide_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mSecondaryPointVizHolder.isShown()){
					mSecondaryPointVizHolder.setVisibility(View.GONE);
					show_hide_button.setText("Show more...");
				}else{
					mSecondaryPointVizHolder.setVisibility(View.VISIBLE);
					show_hide_button.setText("Hide details");
				}
				
			}
		});
         
         Button addToJournalButton = (Button)findViewById(R.id.button1);
         addToJournalButton.setOnClickListener(new OnClickListener() {
 			
 			@Override
 			public void onClick(View v) {
 				addRecordToJournal();
 			}
 		});
         
        // Iterator<PointComponent> goal_iter = goals.keySet().iterator(); 
         
         fillPointViz(entry, goals, diary, mPrimaryPointVizHolder, mSecondaryPointVizHolder);
       
         mSecondaryPointVizHolder.setVisibility(View.GONE);
         show_hide_button.setText("Show more...");
         
         mServingType_Spinner = (Spinner)findViewById(R.id.food_detail_serving_Spinner);
         ArrayAdapter<NutritionEntry.Serving> adapter = new ArrayAdapter<NutritionEntry.Serving>(this, android.R.layout.simple_spinner_item, entry.getServings());
         mServingType_Spinner.setAdapter(adapter);
         
         adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         //NumberPicker num_picker = (NumberPicker)findViewById(R.id.food_detail_serving_size_NumberPicker);
         
         mServingType_Spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				EditText amt_txt = (EditText)findViewById(R.id.food_detail_servingAmt_editText);
				TextView srvg_desc = (TextView)findViewById(R.id.food_detail_servingNote_TextView);
				
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
         
         mServingAmountValue_EditText = (EditText)findViewById(R.id.food_detail_servingAmt_editText);
         
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

    }

	private void fillPointViz(NutritionEntry entry,
			Map<PointComponent, Integer> goals,
			Map<PointComponent, Double> diary, LinearLayout list_holder,
			final LinearLayout extra_list_holder) {
		
		list_holder.removeAllViews();
		extra_list_holder.removeAllViews();
		
		
		PointComponent[] keys = new PointComponent[ entry.getPointsMap().keySet().size()];
         entry.getPointsMap().keySet().toArray(keys);
	 		Arrays.sort(keys, new Comparator<PointComponent>() {
	
	 			@Override
	 			public int compare(PointComponent object1, PointComponent object2) {
	 				return object1.getOrderId() - object2.getOrderId();
	 			}
	 		});
	         
     //    Iterator<PointComponent> entryComponents = entry.getPointsMap().keySet().iterator(); 
	 		
         for (int i=0; i<keys.length; i++){
	 		
        	 PointComponent curGoalComponent = keys[i];
        	 
        	 double adj_nutVal = 0;
        	 adj_nutVal = (float)mServingMultiplier * entry.getPointsMap().get(curGoalComponent).floatValue();
        	 if(goals.containsKey(curGoalComponent)){
        		 fillValues(list_holder, curGoalComponent, 
            			 diary.get(curGoalComponent),
            			 adj_nutVal,
            			 goals.get(curGoalComponent)
            			 );
        	 }else{
        		 fillValues(extra_list_holder, curGoalComponent, 
            			 diary.get(curGoalComponent),
            			 adj_nutVal,
            			 10
            			 );
        	 }
				
         }
	}
	
	private void updateValuesOnServingChange() {
		String amt = mServingAmountValue_EditText.getText().toString();
		Serving srvg = (Serving) mServingType_Spinner.getSelectedItem();
		double amt_d = Double.parseDouble(amt);
		
		double multiplier = FoodDetailActivity.this.mEntry.getServingMultiplier(srvg, amt_d);
		if (Double.isInfinite(multiplier) || Double.isNaN(multiplier)){
			multiplier = 2.0; // TODO: fix hack by fixing the nutrition db
		}
		mServingMultiplier = multiplier;
		
		fillPointViz(mEntry, mGoals, mDiaryEntries, mPrimaryPointVizHolder, mSecondaryPointVizHolder);
		
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
	

	protected Dialog onCreateDialog(int id) {
	    Dialog dialog;
	    switch(id) {
	    case DIALOG_AMOUNT:
	    	dialog = buildFilterDialog(mServingAmountValue_EditText.getText().toString());
	    	break; 
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
	
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
	
	private AlertDialog buildFilterDialog(String amount) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.food_amount_dialog, null);
		
		final EditText whole_num = (EditText)layout.findViewById(R.id.food_amount_whole_EditText);
		final EditText part_num = (EditText)layout.findViewById(R.id.food_amount_part_EditText);
		
		
		int dec_ind = amount.indexOf(".");
		
		whole_num.setText(amount.substring(0, dec_ind));
		part_num.setText(amount.substring(dec_ind + 1));
		
		((Button)layout.findViewById(R.id.food_amount_whole_increase_Button)).setOnClickListener(
				new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int whole_amt = Integer.parseInt(whole_num.getText().toString());
				
				whole_num.setText(Integer.toString(whole_amt+1));
			}
		});
		
		((Button)layout.findViewById(R.id.food_amount_whole_decrease_Button)).setOnClickListener(
				new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int whole_amt = Integer.parseInt(whole_num.getText().toString());
				if (whole_amt > 0){
					whole_num.setText(Integer.toString(whole_amt-1));
				}
			}
		});
		
		((Button)layout.findViewById(R.id.food_amount_part_increase_Button)).setOnClickListener(
				new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*int part_amt = Integer.parseInt(part_num.getText().toString());
				
				part_num.setText(Integer.toString(part_amt+1));
				*/
				mPartNumSelectedIndex++; 
				if (mPartNumSelectedIndex >= PART_NUMS.length){
					mPartNumSelectedIndex = PART_NUMS.length - 1; 
				}
				part_num.setText(PART_NUMS[mPartNumSelectedIndex]);
				
			}
		});
		
		((Button)layout.findViewById(R.id.food_amount_part_decrease_Button)).setOnClickListener(
				new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*int part_amt = Integer.parseInt(part_num.getText().toString());
				if (part_amt > 0){
					part_num.setText(Integer.toString(part_amt-1));
				}*/
				
				mPartNumSelectedIndex--; 
				if (mPartNumSelectedIndex < 0){
					mPartNumSelectedIndex = 0; 
				}
				part_num.setText(PART_NUMS[mPartNumSelectedIndex]);
			}
		});
		
		
		alert.setView(layout);
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				double amount = Double.parseDouble(whole_num.getText().toString()) + PART_NUMS_VALS[mPartNumSelectedIndex];
				mServingAmountValue_EditText.setText(Double.toString(amount));// part_num.getText());
				updateValuesOnServingChange();
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
		
		alert.setCancelable(true);
		return alert.create();
		
	}

	private void fillValues(LinearLayout listHolder, 
								PointComponent pointComponent,
								double curDiaryVal, 
								double nutVal, 
								int curGoalVal){
		// inflate Component View into the listHolder
		LayoutInflater inflater =  getLayoutInflater();
		TextView txtViewTitle; 
		LinearLayout parentTokenHolder; 

		View newView = inflater.inflate(R.layout.point_detail_list_view_row, null);
		txtViewTitle = (TextView) newView.findViewById(R.id.component_name_TextView);
		parentTokenHolder = (LinearLayout)newView.findViewById(R.id.points_layout);
		
		// Set the name of the TextView in the Component View
		txtViewTitle.setText(pointComponent.getDesc());
		//double adj_nutVal = 0;
		
		
		LinearLayout tokenHolder = fillTokens(this, 
											parentTokenHolder, 
											null, 
											0, 
											curDiaryVal, 
											curGoalVal, 
											9, 
											R.drawable.generic_filled_goal, 
											R.drawable.generic_extra_filled_goal, 
											R.drawable.generic_filled_goal); // TODO: Make this a half-drawable
										
		tokenHolder = fillTokens(this, 
				parentTokenHolder, 
				tokenHolder, 
				curDiaryVal, 
				nutVal, 
				curGoalVal, 
				9, 
				pointComponent.getDrawableId(), 
				pointComponent.getExtraDrawableId(), 
				pointComponent.getDrawableHalfId());
		
		tokenHolder = fillTokens(this, 
				parentTokenHolder, 
				tokenHolder, 
				curDiaryVal + nutVal, 
				curGoalVal-Math.ceil(curDiaryVal + nutVal),//curGoalVal, 
				curGoalVal, 
				9, 
				R.drawable.dark_token, 
				R.drawable.dark_token, 
				R.drawable.dark_token);
		
		listHolder.addView(newView);
	}
	
		private LinearLayout fillTokens(Context context, 
								LinearLayout parent, 
								LinearLayout tokenHolder, 
								double curNumTokens, 
								double numTokensToAdd, 
								int goalTokens, 
								int maxTokensPerRow,
								int tokenDrawableId, 
								int altTokenDrawableId, 
								int halfTokenDrawableId){
			
			if (tokenHolder == null){
				// Create a new token holder & add it to the parent
				tokenHolder = new LinearLayout(this); 
				tokenHolder.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
		                LayoutParams.WRAP_CONTENT));
				tokenHolder.setOrientation(LinearLayout.HORIZONTAL);
				parent.addView(tokenHolder);
			}
			if (numTokensToAdd > 0){
				//for (int i=(int)curNumTokens; i<curNumTokens+numTokensToAdd; i++){
				for (double i=curNumTokens; i<curNumTokens+numTokensToAdd; i++){
					if (i>1 && (i%maxTokensPerRow <1)){
						tokenHolder = new LinearLayout(this); 
						tokenHolder.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				                LayoutParams.WRAP_CONTENT));
						tokenHolder.setOrientation(LinearLayout.HORIZONTAL);
						parent.addView(tokenHolder);
					}
					
					// Detect if a half-point is appropriate
					if ((curNumTokens+numTokensToAdd)-i < 1 ){
						ImageView img2 = new ImageView(context);
						img2.setScaleType(ScaleType.CENTER);
						if (i > (goalTokens-1)){
							img2.setImageResource(altTokenDrawableId);
						}
						else{
							img2.setImageResource(halfTokenDrawableId);
						}
						tokenHolder.addView(img2);
					}
					else{
						ImageView img2 = new ImageView(this);
						img2.setScaleType(ScaleType.CENTER);
						if (i>goalTokens){
							img2.setImageResource(altTokenDrawableId);
						}else{
							img2.setImageResource(tokenDrawableId);
						}
						tokenHolder.addView(img2);
					}
				}
				
			}
			return tokenHolder;
		}
		
		private LinearLayout fillTokens(Context context, 
				LinearLayout parent, 
				LinearLayout tokenHolder, 
				double numOldPoints, 
				double numNewPoints, 
				int goalTokens, 
				int maxTokensPerRow,
				int tokenDrawableId, 
				int extraTokenDrawableId, 
				int halfTokenDrawableId, 
				int emptyGoalTokenDrawableId){

			if (tokenHolder == null){
				// Create a new token holder & add it to the parent
				tokenHolder = new LinearLayout(context); 
				tokenHolder.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
				tokenHolder.setOrientation(LinearLayout.HORIZONTAL);
				parent.addView(tokenHolder);
			}
			if (numNewPoints > 0){
				//for (int i=(int)curNumTokens; i<curNumTokens+numTokensToAdd; i++){
				for (double i=0; i<numNewPoints; i++){
					// Start a new row
					if (i>1 && i%maxTokensPerRow == 0){
						tokenHolder = new LinearLayout(context); 
						tokenHolder.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT));
						tokenHolder.setOrientation(LinearLayout.HORIZONTAL);
						parent.addView(tokenHolder);
					}

					// Detect if a half-point is appropriate
					if (numNewPoints-i < 1 ){
						ImageView img2 = new ImageView(context);
						img2.setScaleType(ScaleType.CENTER);
						if (i > (goalTokens-1)){
							img2.setImageResource(extraTokenDrawableId);
						}
						else{
							img2.setImageResource(halfTokenDrawableId);
						}
						tokenHolder.addView(img2);
					}
					else{
						ImageView img2 = new ImageView(context);
						img2.setScaleType(ScaleType.CENTER);
						if (i>(goalTokens-1)){
							img2.setImageResource(extraTokenDrawableId);
						}else{
							img2.setImageResource(tokenDrawableId);
						}
						tokenHolder.addView(img2);
					}
				}
				
			}
			
			for (double j = Math.ceil(numNewPoints); j<goalTokens; j++){
				// Start a new row
				if (j>1 && j%maxTokensPerRow == 0){
					tokenHolder = new LinearLayout(context); 
					tokenHolder.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
							LayoutParams.WRAP_CONTENT));
					tokenHolder.setOrientation(LinearLayout.HORIZONTAL);
					parent.addView(tokenHolder);
				}

					ImageView img2 = new ImageView(context);
					img2.setScaleType(ScaleType.CENTER);
					img2.setImageResource(emptyGoalTokenDrawableId);
					tokenHolder.addView(img2);
				
			}
			
			return tokenHolder;
		}

		@Override
		public void run() {
			loadData();
			handler.sendEmptyMessage(0);
		}
		
		private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                   
                    fillData(mEntry, mGoals, mDiaryEntries);
                    mProgressDialog.dismiss();
            }
		};

		private HashMap<PointComponent,Integer> mGoals;

		private LinearLayout mPrimaryPointVizHolder;

		private LinearLayout mSecondaryPointVizHolder;
				
}
