package edu.uwcse.pond.proto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.uwcse.pond.diary.CustomFoodPointsDiaryTableHelper;
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
import android.app.ListActivity;
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

public class CreateCustomPointFoodActivity extends Activity{
	public static final String DATA_DIARY_DB_ID_KEY = "diaryDbId";
	
	
	
	
	//NutritionDbHelper mNutritionDbHelper; 
	
	DiaryDbHelper mDiaryDbHelper;

	private ContentValues entryValues; 
	
	private Button saveButton; 
	
                   
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_point_create);
        
        
        final EditText nameEditText = (EditText)findViewById(R.id.editText1); 
        final EditText commentEditText = (EditText)findViewById(R.id.editText2);
        
        final CheckBox addToDiaryCheckBox = (CheckBox)findViewById(R.id.add_to_diary_CheckBox);
        
        saveButton = (Button)findViewById(R.id.button1);
        
        ListView listView = (ListView) findViewById(R.id.listView1);
        
        
        listView.setAdapter(new CustomComponentListViewAdapter(this));
        
        //saveButton.setEnabled(false);
		saveButton.setOnClickListener(
				new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (nameEditText.getText().length()==0){
					AlertDialog.Builder builder = new AlertDialog.Builder(CreateCustomPointFoodActivity.this);
					builder.setMessage("Please enter a name first")
					       .setCancelable(false)
					       .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					                dialog.cancel();
					           }
					       });
					AlertDialog alert = builder.create();
					alert.show();
				}else{
				
					entryValues.put(CustomFoodPointsDiaryTableHelper.COL_NAME, nameEditText.getText().toString());
					entryValues.put(CustomFoodPointsDiaryTableHelper.COL_COMMENT, commentEditText.getText().toString());
					
					long newId = mDiaryDbHelper.createNewCustomFoodPts(entryValues);
					if (newId == -1){
						AlertDialog.Builder builder = new AlertDialog.Builder(CreateCustomPointFoodActivity.this);
						builder.setMessage("A food with this name already exists.")
						       .setCancelable(false)
						       .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
						                dialog.cancel();
						           }
						       });
						AlertDialog alert = builder.create();
						alert.show();
					}else{
						if (addToDiaryCheckBox.isChecked()){
							mDiaryDbHelper.createNewPointsEntryFromCustomFood(newId);
						}
						
						
						Context context = getApplicationContext();
						CharSequence text = "New Food Item Created";
						int duration = Toast.LENGTH_SHORT;		
						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
						CreateCustomPointFoodActivity.this.finish(); 
					}
					
					//saveButton.setEnabled(false);
					
				}
			}
		});
        
        mDiaryDbHelper = DiaryDbHelper.getDiaryDbHelper(this);
      
        	
        entryValues = new ContentValues();//mDiaryDbHelper.getPointsEntryAsVals(pointDiaryId);
        	
        
    }
	
	

	@Override
	protected void onStop() {
		
		super.onStop();
	}



	public void updateComponentPlusOne(PointComponent pointComponent) {
		double val = 0;
		if (entryValues.containsKey(pointComponent.getPtDbColName())){
			val = entryValues.getAsDouble(pointComponent.getPtDbColName());	
		}
		
		// Update amount
		entryValues.put(pointComponent.getPtDbColName(), val + 1);
	}



	public void updateComponentPlusHalf(PointComponent pointComponent) {
		double val = 0;
		if (entryValues.containsKey(pointComponent.getPtDbColName())){
			val = entryValues.getAsDouble(pointComponent.getPtDbColName());	
		}
		
		// Update amount
		entryValues.put(pointComponent.getPtDbColName(), val + 0.5);
		
	}



	public void updateComponentMinusOne(PointComponent pointComponent) {
		double val = 0;
		if (entryValues.containsKey(pointComponent.getPtDbColName())){
			val = entryValues.getAsDouble(pointComponent.getPtDbColName());	
		}
		
		// Update amount
		entryValues.put(pointComponent.getPtDbColName(), val - 1);
		
	}



	public void updateComponentMinusHalf(PointComponent pointComponent) {
		double val = 0;
		if (entryValues.containsKey(pointComponent.getPtDbColName())){
			val = entryValues.getAsDouble(pointComponent.getPtDbColName());	
		}
				
		// Update amount
		entryValues.put(pointComponent.getPtDbColName(), val - 0.5);
		
	}
	
	
}
