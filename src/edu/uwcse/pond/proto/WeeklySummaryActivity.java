package edu.uwcse.pond.proto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.ValueCallback;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
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

public class WeeklySummaryActivity extends Activity implements OnGesturePerformedListener {

	public static final String DATA_WHICH_COMPONENT_INT_KEY = "whichComponent";
	public static final String DATA_SHOW_WEEK_INT_KEY = "showWeek";
	public static final String DATA_WHICH_DATE_STRING_KEY = "whichDate";
	
	
	static final int DIALOG_DATE = 1; 
	
	public static ArrayList<PointComponent> COMPONENT_ORDER = new ArrayList<PointComponent>();
			
	static{ 
		//COMPONENT_ORDER.add(PointComponent.ALL); 
		COMPONENT_ORDER.add(PointComponent.VEGGIE); 
		COMPONENT_ORDER.add(PointComponent.VEGGIE_GREEN); 
		COMPONENT_ORDER.add(PointComponent.GRAINS_WHOLE);
		COMPONENT_ORDER.add( PointComponent.GRAINS);
		COMPONENT_ORDER.add(PointComponent.FRUIT_WHOLE); 
		COMPONENT_ORDER.add( PointComponent.FRUIT);
		COMPONENT_ORDER.add(PointComponent.DAIRY); 
		COMPONENT_ORDER.add( PointComponent.PROTEIN);
		
		COMPONENT_ORDER.add( PointComponent.OILS); 
		COMPONENT_ORDER.add( PointComponent.SOLID_FATS);
		COMPONENT_ORDER.add( PointComponent.SODIUM);
		COMPONENT_ORDER.add(PointComponent.SUGAR); 
	}
	
	public static final SimpleDateFormat DATE_DISPLAY_FORMAT = new SimpleDateFormat("MMM d, yy");
	
	private DiaryDbHelper mDiaryHelper;
	
	private TextView mTextView; 
	

	private GestureLibrary mGestureLibrary; 
	
	private PointComponent mCurComponent; 
	
	private Calendar mCurDate; 
	
	private int mCurWeekOrDayShowing;
	
	// the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

    			@Override
                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                    mCurDate.set(Calendar.YEAR, year);
                    mCurDate.set(Calendar.MONTH, monthOfYear);
                    mCurDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                
                    bindData(mCurComponent, mCurDate);
    			}
    			
            };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weekly_entries);
		
		
		mGestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures2);
		if (!mGestureLibrary.load()) {
		    finish();
		}
		
	 	mDiaryHelper =  DiaryDbHelper.getDiaryDbHelper(getApplicationContext());
	 	
	 	
	 	
	 //	mTextView = (TextView)findViewById(R.id.daily_entries_summary_TextView);
	/* 	mToggleButton = (ToggleButton)findViewById(R.id.daily_entries_ShowWeek_ToggleButton);
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
		});
	 	*/
	 	Bundle b = this.getIntent().getExtras();
        
	 	Spinner chooseCompSpinner = (Spinner)findViewById(R.id.daily_entries_ChooseComponent_Spinner);
	 	/*ArrayAdapter<PointComponent> adapter = new ArrayAdapter<Consts.PointComponent>(this, 
	 			android.R.layout.simple_spinner_item, COMPONENT_ORDER);*/
	 	PrettyComponentArrayAdapter adapter = new PrettyComponentArrayAdapter(this, COMPONENT_ORDER);
	 	chooseCompSpinner.setAdapter(adapter);
	 	//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	 	//adapter.setDropDownViewResource(R.layout.pretty_spinner_drop_down_item);
	 	
	 	PointComponent component = PointComponent.VEGGIE_GREEN; 
	 	
        if (b != null){
		 	if (b.containsKey(DATA_WHICH_COMPONENT_INT_KEY)){
		 		String pointsColName = b.getString(DATA_WHICH_COMPONENT_INT_KEY);
		 		component = PointComponent.getFromPtsColName(pointsColName);
		 	}
        }
	 	
        chooseCompSpinner.setSelection(COMPONENT_ORDER.indexOf(component));
	 	
	 	chooseCompSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

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
	 	
	 	
        
	 	bindData(component, Calendar.getInstance() );
	 	/*
	 	Button chooseDateButton = (Button)findViewById(R.id.daily_entries_ChangeDate_Button);
	 	chooseDateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_DATE);	
			}
		});*/
		
	/* 	ToggleButton chartToggle = (ToggleButton)findViewById(R.id.daily_entries_chart_ToggleButton);
	 	chartToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				switcher.showNext();
			}
		});
	 	*/
		GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
		gestures.addOnGesturePerformedListener(this);
		
	}


	private void bindData(PointComponent component,  Calendar whichDay) {
		
		
		// Remember the values the new cursor will represent
		mCurComponent = component; 
		mCurDate = whichDay; 
		
		String desc = "All Components"; 
		if (mCurComponent != null)
			desc = mCurComponent.getDesc();
//		mTextView.setText( DATE_DISPLAY_FORMAT.format(mCurDate.getTime()));
		

			// Show week
			//entries_cursor = mDiaryHelper.getAllEntriesForWeek(mCurDate, mCurComponent);
			fillWeekChart(mCurComponent, mCurDate);
			return; 
		
		
		
	}
	
	private void fillWeekChart(PointComponent pt, Calendar date){
		// Determine the Monday just before the given date
		Calendar curDay = Calendar.getInstance();
		curDay.setTime(date.getTime());
		while (curDay.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
			curDay.add(Calendar.DATE, -1);
		}
		
		// Get a cursor for Mon-T-Wed-... etc. 
		fillDayChart(mDiaryHelper.getCountForDay(curDay, pt), 
				pt.getDrawableId(), 
				(RelativeLayout)findViewById(R.id.daily_entries_Monday), 
				mDiaryHelper.getGoalForDay(pt, curDay));
		
		curDay.add(Calendar.DATE, 1);
		fillDayChart(mDiaryHelper.getCountForDay(curDay, pt), 
				pt.getDrawableId(), 
				(RelativeLayout)findViewById(R.id.daily_entries_Tuesday), 
				mDiaryHelper.getGoalForDay(pt, curDay));
		
		curDay.add(Calendar.DATE, 1);
		fillDayChart(mDiaryHelper.getCountForDay(curDay, pt), 
				pt.getDrawableId(), 
				(RelativeLayout)findViewById(R.id.daily_entries_Wednesday), 
				mDiaryHelper.getGoalForDay(pt, curDay));

		curDay.add(Calendar.DATE, 1);
		fillDayChart(mDiaryHelper.getCountForDay(curDay, pt), 
				pt.getDrawableId(), 
				(RelativeLayout)findViewById(R.id.daily_entries_Thursday), 
				mDiaryHelper.getGoalForDay(pt, curDay));

		curDay.add(Calendar.DATE, 1);
		fillDayChart(mDiaryHelper.getCountForDay(curDay, pt), 
				pt.getDrawableId(), 
				(RelativeLayout)findViewById(R.id.daily_entries_Friday), 
				mDiaryHelper.getGoalForDay(pt, curDay));

		curDay.add(Calendar.DATE, 1);
		fillDayChart(mDiaryHelper.getCountForDay(curDay, pt), 
				pt.getDrawableId(), 
				(RelativeLayout)findViewById(R.id.daily_entries_Saturday), 
				mDiaryHelper.getGoalForDay(pt, curDay));

		curDay.add(Calendar.DATE, 1);
		fillDayChart(mDiaryHelper.getCountForDay(curDay, pt), 
				pt.getDrawableId(), 
				(RelativeLayout)findViewById(R.id.daily_entries_Sunday), 
				mDiaryHelper.getGoalForDay(pt, curDay));

	}
	
	private void fillDayChart(int howMany, int drawableId, RelativeLayout bar, int goal){
		bar.removeAllViews(); 
		int curId =  15; 
		
		for (int i=0; i<howMany; i++){
			
			if (i == goal){
				ImageView curView = new ImageView(bar.getContext()) ;
				curView.setImageResource(R.drawable.goal_line);
				curView.setId(curId); 
				
				RelativeLayout.LayoutParams layoutParams = 
						new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				
				if (i==0){
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				}else{
					layoutParams.addRule(RelativeLayout.ABOVE, curId-1);
				}
				
				bar.addView(curView, layoutParams);
				curId++;
			}
			
			ImageView curView = new ImageView(bar.getContext()) ;
			curView.setImageResource(drawableId);
			
			curView.setId(curId); 
			
			RelativeLayout.LayoutParams layoutParams = 
					new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			
			if (i==0){
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			}else{
				layoutParams.addRule(RelativeLayout.ABOVE, curId-1);
			}
			
			bar.addView(curView, layoutParams);
			curId++;
		}
		
		for (int j=howMany; j<goal; j++){
			
			ImageView curView = new ImageView(bar.getContext()) ;
			
			curView.setImageResource(R.drawable.empty_chart_goal);
			curView.setMinimumWidth(30);
			curView.setMinimumHeight(30);
			
			curView.setId(curId); 
			
			RelativeLayout.LayoutParams layoutParams = 
					new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			
			if (j==0){
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			}else{
				layoutParams.addRule(RelativeLayout.ABOVE, curId-1);
			}
			
			bar.addView(curView, layoutParams);
			curId++;
		}
		
		if (howMany < goal){
			
				ImageView curView = new ImageView(bar.getContext()) ;
				curView.setImageResource(R.drawable.goal_line);
				curView.setId(curId); 
				
				RelativeLayout.LayoutParams layoutParams = 
						new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				
			
					layoutParams.addRule(RelativeLayout.ABOVE, curId-1);
				
				bar.addView(curView, layoutParams);
		}
		
		if (howMany==0 && goal == 0){
			ImageView curView = new ImageView(bar.getContext()) ;
			curView.setImageResource(R.drawable.empty_circle);
			
			curId = 3; 
			
			curView.setId(curId); 
			
			RelativeLayout.LayoutParams layoutParams = 
					new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			
			bar.addView(curView, layoutParams);
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
		 Toast.makeText(this, best_pred.name, Toast.LENGTH_SHORT).show();
		 
		 if (best_pred.name.equalsIgnoreCase("left")){
			
				 // Increment the week
				 mCurDate.add(Calendar.WEEK_OF_YEAR, 1);
				 bindData(mCurComponent,  mCurDate);
			 
		 }else if(best_pred.name.equalsIgnoreCase("right")){
			 
				 // Decrement the week
				 mCurDate.add(Calendar.WEEK_OF_YEAR, -1);
				 bindData(mCurComponent,  mCurDate);
			 
		 }
		 else if(best_pred.name.equalsIgnoreCase("up")){
			 // Get the next component
			 bindData(getNextComponent(mCurComponent),  mCurDate);
			 
		 }
		 else if(best_pred.name.equalsIgnoreCase("down")){
			// Get the previous component
			bindData(getPrevComponent(mCurComponent), mCurDate);
			 
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
	

}
