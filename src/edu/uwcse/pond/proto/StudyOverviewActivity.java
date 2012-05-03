package edu.uwcse.pond.proto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import edu.uwcse.pond.diary.DiaryDbHelper;
import edu.uwcse.pond.diary.ActionLogDbHelper.Action;
import edu.uwcse.pond.nutrition.Consts;
import edu.uwcse.pond.nutrition.Consts.PointComponent;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;


public class StudyOverviewActivity extends OverviewActivity {

	private String startDateTimeString; 
	
	public static final String STUDY_PREFS_NAME  = "PondStudyPrefs";  
	// Condition ordering: balanced Latin square
	private static final String[][] CONDITION_ORDER = {{"F", "S", "B", "M"},
													   {"S", "M", "F", "B"},
													   {"M", "B", "S", "F"},
													   { "B", "F", "M", "S"}};
	private static final String[] TASK_ORDER = {
		"P1", "P2", "B1", "SS1", "L1", "BS1", "D1",
		"P1", "P2", "B2", "SS2", "L2", "BS2", "D2", 
		"P1", "P2", "B3", "SS3", "L3", "BS3", "D3",
		"P1", "P2", "B4", "SS4", "L4", "BS4", "D4"}; 
	
	public static final String STUDY_PPT_ID_KEY = "pptId"; 
	private static final String STUDY_PPT_NUM_KEY = "pptNum"; 
	private static final String STUDY_TASK_ID_KEY = "taskId";
	private static final String STUDY_TASK_INDEX_KEY = "taskIndex";
	private static final String STUDY_COND_ID_KEY = "condId";
	private static final String STUDY_COND_INDEX_KEY = "condIndex"; 
	private static final String STUDY_ENTRY_ID_KEY  = "entryId";
	private static final String STUDY_COND_1_ORDER = "condOrder_1";
	private static final String STUDY_COND_2_ORDER = "condOrder_2";
	private static final String STUDY_COND_3_ORDER = "condOrder_3";
	private static final String STUDY_COND_4_ORDER = "condOrder_4";
	
	private static final String STUDY_COND_START_TIME = "conditionStartTime";
	
	private String curPptId; 
	private String curTaskId; 
	private String curCondId; 
	
	private int curTaskIndex; 
	private int curCondIndex; 
	private int curPptNum; 
	
	private Date curCondStartTime; 
	
	private Date curTaskStartTime; 
	
	private boolean preppingForStudyStart = false; 
	
	
	private static final int NUM_TASKS_PER_CONDITION = 7; 
	
	@Override
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		SharedPreferences settings = getSharedPreferences(STUDY_PREFS_NAME, 0);
		curPptId = settings.getString(STUDY_PPT_ID_KEY, "-1");
		curTaskId = settings.getString(STUDY_TASK_ID_KEY, "-1");
		curCondId = settings.getString(STUDY_COND_ID_KEY, "none");
		
		curTaskIndex = settings.getInt(STUDY_TASK_INDEX_KEY, -1);
		curCondIndex = settings.getInt(STUDY_COND_INDEX_KEY, -1);
		curPptNum = settings.getInt(STUDY_PPT_NUM_KEY, -1);

		curTaskStartTime = new Date(); 
		curCondStartTime = new Date(settings.getLong(STUDY_COND_START_TIME, 0));
		
		if (curPptId.equals("-1")){
			mDiaryHelper.logAction(Action.STUDY, -1, -1, "Study not running", "");
			Toast.makeText(getApplicationContext(), "Study not running", Toast.LENGTH_LONG).show();
		}else{
			mDiaryHelper.logAction(Action.STUDY, -1, -1, "Creating Overview screen", getCurrentInfoString());	
			mDiaryHelper.logAction(Action.STUDY_CONDITION, -1, -1, "Starting Condition: " + curCondId, getCurrentInfoString());
			mDiaryHelper.logAction(Action.STUDY_TASK, -1, -1, "Starting Task: " + TASK_ORDER[curTaskIndex], getCurrentInfoString());
			Toast.makeText(getApplicationContext(), curCondId +": " + TASK_ORDER[curTaskIndex], Toast.LENGTH_LONG).show();
		}
		
	};
	
	@Override
	protected Map<Consts.PointComponent,Double> getSummedPointsEntry(){
		return mDiaryHelper.getSummedPointsEntryAsMap(curCondStartTime);
	}
    
	
	@Override 
	public void onBackPressed() {
		if (!preppingForStudyStart){
			
			if (curPptId.equals("-1")){
				mDiaryHelper.logAction(Action.STUDY, -1, -1, "Study not running", "");
				Toast.makeText(getApplicationContext(), "Study not running", Toast.LENGTH_LONG).show();
				finish(); 
			}else{
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Are you sure you're done with "+curTaskId+"?")
				       .setCancelable(false)
				       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   endTask();
				        	// Move to the next task
				   			curTaskIndex++;
				   			
				   			if (curTaskIndex >= TASK_ORDER.length){// There are no more tasks left //curCondIndex >= 4){
				   				// Study is over
				   				mDiaryHelper.logAction(Action.STUDY, -1, -1, "Study is over", getCurrentInfoString());
				   				SharedPreferences settings = getSharedPreferences(STUDY_PREFS_NAME, 0);
				   			    SharedPreferences.Editor editor = settings.edit();
				   			    editor.remove(STUDY_PPT_ID_KEY); 
				   			    editor.remove(STUDY_COND_ID_KEY); 
				   			    editor.remove(STUDY_TASK_ID_KEY);
				   			    editor.remove(STUDY_TASK_INDEX_KEY);
				   			    editor.remove(STUDY_PPT_NUM_KEY); 
				   			    editor.remove(STUDY_COND_INDEX_KEY);
				   			    editor.commit(); 
				   			}else{
				   				
				   				curTaskId = TASK_ORDER[curTaskIndex];
				   				
				   				// If done with tasks in this condition, move to the next condition
				   				if ((curTaskIndex % NUM_TASKS_PER_CONDITION) == 0){
				   					startNextCondition();
				   				}
				   				
				   				SharedPreferences settings = getSharedPreferences(STUDY_PREFS_NAME, 0);
				   				
				   				if (curTaskId.matches("B[1-4]")){
				   					clearGoals(settings);
				   				}
				   				
				   			    SharedPreferences.Editor editor = settings.edit();
				   			    editor.putString(STUDY_PPT_ID_KEY, curPptId); 
				   			    editor.putString(STUDY_COND_ID_KEY, CONDITION_ORDER[curPptNum%4][curCondIndex]); 
				   			    editor.putString(STUDY_TASK_ID_KEY, curTaskId);
				   			    editor.putInt(STUDY_TASK_INDEX_KEY, curTaskIndex);
				   			    editor.putInt(STUDY_PPT_NUM_KEY, curPptNum); 
				   			    editor.putInt(STUDY_COND_INDEX_KEY, curCondIndex);
				   			    editor.commit(); 
				   			    
				   			}
				   			finish();
				           }
				       })
				       .setNegativeButton("No", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                dialog.cancel();
				                 
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show(); 
			}
		}else{
			finish();
		}
		
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	}

	private void endTask() {
		mDiaryHelper.logAction(Action.STUDY_TASK, -1, curPptNum, "Completed task: " + curTaskId, getCurrentInfoString());
		Date endTime = new Date(); 
		HashMap<Consts.PointComponent,Double> results = mDiaryHelper.getSummedPointsEntryAsMapForTimeFrame(curTaskStartTime, endTime);
		String headings = results.toString().replaceAll("[.=[0-9]]", ""); 
		mDiaryHelper.logAction(Action.STUDY_TASK, -1, curPptNum, "Task result heading", headings);
		
		String modifiedResults = clearGoalsFromMap(results, curCondIndex);
		
		String trimmedResults = (results.toString() ).replaceAll("[{}[A-Z]_=]", "");
		
		
		mDiaryHelper.logAction(Action.STUDY_TASK_RESULT, -1, curPptNum, "Task result", getCurrentInfoString() + "," + trimmedResults);
		mDiaryHelper.logAction(Action.STUDY_TASK_TIME, -1, curPptNum, "Task time", getCurrentInfoString() + "," + (endTime.getTime()-curTaskStartTime.getTime()));
	}
	
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
		 super.onCreateOptionsMenu(menu);
		 MenuInflater inflater = getMenuInflater();
	        
	        //inflater.inflate(R.menu.main_menu, menu);
	        
	        
	        inflater.inflate(R.menu.study_menu, menu);
	        return true;
	    }
	
	    @Override
	    public boolean onPrepareOptionsMenu(Menu menu) {
	    	
	    	return true; 
	    }

	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()){
		case R.id.start_study_menuItem:
			startStudy();
			return true; 
		case R.id.reset_screen_menuItem:
			
			return true; 
		case R.id.restart_condition_menuItem: 
			
			return true; 
		case R.id.restart_task_menuItem:
			
			return true;
		
		}
		return super.onOptionsItemSelected(item); 
	}
	
	private void startNextCondition(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Condition over")
		       .setCancelable(false)
		       .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                StudyOverviewActivity.this.finish();
		           }
		       });
		AlertDialog alert = builder.create();
		// Change the goals 
		mDiaryHelper.logAction(Action.STUDY_CONDITION, -1, curPptNum, "Condition is over", getCurrentInfoString());
		curCondIndex++; 
		String newGoals = ""; 
		SharedPreferences settings = getSharedPreferences(STUDY_PREFS_NAME, 0);
		
		switch(curCondIndex){
		case 0:
			newGoals = settings.getString(STUDY_COND_1_ORDER, "");
			break; 
		case 1:
			newGoals = settings.getString(STUDY_COND_2_ORDER, "");
			break; 
		case 2:
			newGoals = settings.getString(STUDY_COND_3_ORDER, "");
			break; 
		case 3:
			newGoals = settings.getString(STUDY_COND_4_ORDER, "");
			break; 
		}
		
		mDiaryHelper.logAction(Action.STUDY_CONDITION, -1, curPptNum, "Starting next condition", "");
		mDiaryHelper.updateGoalForInLabStudy(getNewGoalMap(newGoals));
		
		clearGoals(settings); 
		
		//curTaskIndex = 0; 
		
		//alert.show();
	}

	private void clearGoals(SharedPreferences settings) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(STUDY_COND_START_TIME, new Date().getTime()); 
		editor.putInt(STUDY_COND_INDEX_KEY, curCondIndex);
		editor.commit();
	}
	
	private void startStudy(){
		mDiaryHelper.logAction(Action.STUDY, -1, curPptNum, "Starting study", "");
		
		// Ask for pptId
		final CharSequence[] items = {"13", "14", "15", "16", "17", "18", 
				"19", "20", "21", "22", "23", "24", 
				"25", "26", "27", "28", "29", 
				"30", "31", "32", "33", "34",
				"35", "36", "37", "38", "39", };

		int blah; 
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose PptId");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		        Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
		        curPptId = "ppt"+ items[item];
		        curTaskIndex = 0; 
		        
		     // Store everything in shared preferences
				SharedPreferences settings = getSharedPreferences(STUDY_PREFS_NAME, 0);
			    SharedPreferences.Editor editor = settings.edit();
			    editor.putString(STUDY_PPT_ID_KEY, curPptId); 
			    editor.putInt(STUDY_PPT_NUM_KEY, item); 
			    editor.putString(STUDY_COND_ID_KEY, CONDITION_ORDER[item%4][0]); 
			    editor.putString(STUDY_TASK_ID_KEY, TASK_ORDER[curTaskIndex]);
			    editor.putInt(STUDY_TASK_INDEX_KEY, curTaskIndex);
			    editor.putInt(STUDY_COND_INDEX_KEY, curCondIndex);
			    
			    
			    String goals = getRandomOrdering(CONDITION_ORDER[item%4][0]);
			    editor.putString(STUDY_COND_1_ORDER, goals);
			    mDiaryHelper.logAction(Action.STUDY, -1, curPptNum,  "Randomly chosen components", curPptId + ","  + goals);
			    
			    goals = getRandomOrdering(CONDITION_ORDER[item%4][1]);
			    editor.putString(STUDY_COND_2_ORDER, goals);
			    mDiaryHelper.logAction(Action.STUDY, -1, curPptNum,  "Randomly chosen components", curPptId + ","+ goals);
			    
			    goals = getRandomOrdering(CONDITION_ORDER[item%4][2]);
			    editor.putString(STUDY_COND_3_ORDER, goals);
			    mDiaryHelper.logAction(Action.STUDY, -1, curPptNum, "Randomly chosen components", curPptId + ","  + goals);
			    
			    goals = getRandomOrdering(CONDITION_ORDER[item%4][3]);
			    editor.putString(STUDY_COND_4_ORDER, goals);
			    mDiaryHelper.logAction(Action.STUDY, -1, curPptNum, "Randomly chosen components", curPptId + ","  + goals);
			    
			    editor.commit(); 
			    
			    curCondIndex = -1;
			    startNextCondition();
			    dialog.dismiss();
			    
			    preppingForStudyStart = true; 
			    finish(); 
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
		
	}
	
	private String clearGoalsFromMap(HashMap<PointComponent, Double> results, int whichCond){
		// Get the activated goals for the condition
		String newGoals = ""; 
		SharedPreferences settings = getSharedPreferences(STUDY_PREFS_NAME, 0);
		
		switch(curCondIndex){
		case 0:
			newGoals = settings.getString(STUDY_COND_1_ORDER, "");
			break; 
		case 1:
			newGoals = settings.getString(STUDY_COND_2_ORDER, "");
			break; 
		case 2:
			newGoals = settings.getString(STUDY_COND_3_ORDER, "");
			break; 
		case 3:
			newGoals = settings.getString(STUDY_COND_4_ORDER, "");
			break; 
		}
		
		String[] goals = newGoals.split(",");
		
		// Go through the map, setting all other goals to -1
		Iterator<PointComponent> iter = results.keySet().iterator(); 
		while (iter.hasNext()){
			PointComponent pc = (PointComponent) iter.next();
			boolean found = false; 
			for (int i=0; i<goals.length; i++){
				if (goals[i].equals(pc.getDesc())){
					found = true; 
					break;  
				}
				
			}
			if (!found){
				results.put(pc, -1.0);
			}
		}
		// Dump the map into a string
		//String headings = results.toString().replaceAll("[.=[0-9]]", ""); 
		//mDiaryHelper.logAction(Action.STUDY_TASK, -1, -1, "Task result heading", headings);
		//String trimmedResults = results.toString().replaceAll("[{}[A-Z]_=]", "");
		//mDiaryHelper.logAction(Action.STUDY_TASK_RESULT, -1, -1, "Task result", getCurrentInfoString() + "," + trimmedResults);

		return results.values().toString(); 
		
		
		// return it
	}
	
	private Map<PointComponent, Integer> getNewGoalMap(String goals){
		Map<PointComponent, Integer> map = new HashMap<PointComponent, Integer>(); 
		
		String[] items = goals.split(","); 
		for(int i=1; i<items.length; i++){
			map.put(PointComponent.getFromDesc(items[i]), 5);
		}
		
		return map;
	}
	
	
	
	private List<PointComponent> getNewPointComponentList(){
		List<PointComponent> comps = new ArrayList<PointComponent>(); 
		
		comps.add(PointComponent.DAIRY); 
		comps.add(PointComponent.SOLID_FATS); 
		comps.add(PointComponent.FRUIT); 
		comps.add(PointComponent.FRUIT_WHOLE); 
		comps.add(PointComponent.GRAINS); 
		comps.add(PointComponent.GRAINS_WHOLE); 
		comps.add(PointComponent.OILS); 
		comps.add(PointComponent.PROTEIN); 
		comps.add(PointComponent.SODIUM);  
		comps.add(PointComponent.SUGAR); 
		comps.add(PointComponent.VEGGIE); 
		comps.add(PointComponent.VEGGIE_GREEN); 
		
		return comps; 
	}
	
	List<PointComponent> leftOverComponents = getNewPointComponentList();
    
	
	private String getRandomOrdering( String condId){
		int num = -1; 
		if (condId.equals("F"))
			num = 12; 
		else if (condId.equals("B"))
			num = 9; 
		else if (condId.equals("M"))
			num = 5; 
		else if (condId.equals("S"))
			num = 2; 
		//else throw(new Exception());
		
		StringBuffer buffer = new StringBuffer(condId);
		
		List<PointComponent> components; 
		
		if (condId.equals("F")){
			components = getNewPointComponentList();
		}else{
			components = leftOverComponents;

			Collections.shuffle(components, new Random(new Date().getTime()));
			
			if (components.size() < num){
				List<PointComponent> newBatch = getNewPointComponentList();
				
				Iterator<PointComponent> existingIter = components.iterator();
				while(existingIter.hasNext()){
					newBatch.remove(existingIter.next());
				}
				
				Collections.shuffle(newBatch, new Random(new Date().getTime()));
				components.addAll(newBatch);
			}
		}
		
		
		for(int i=0; i<num; i++){
			buffer.append(","); 
			buffer.append(components.get(i).getDesc());
		}
		
		if (!condId.equals("F")){
			leftOverComponents = components.subList(num, components.size());
		}
		
		return buffer.toString(); 
		
	}
	
	private String getCurrentInfoString(){
		StringBuffer buffer = new StringBuffer(); 
		buffer.append(curPptId); 
		buffer.append(",");
		buffer.append(curCondId);
		buffer.append(","); 
		buffer.append(curTaskId); 
		
		
		return buffer.toString(); 
	}
	
	@Override
	public boolean onSearchRequested() {
	     Bundle appData = new Bundle();
	     //appData.putBoolean(SearchableActivity.JARGON, true);
	     appData.putLong(FoodDetailActivity.DATA_DATETIME_EATEN_KEY, curCondStartTime.getTime());
	     startSearch(null, false, appData, false);
	     return true;
	 }
	
}
