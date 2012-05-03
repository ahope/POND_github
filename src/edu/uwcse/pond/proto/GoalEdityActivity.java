package edu.uwcse.pond.proto;

import java.util.Map;

import edu.uwcse.pond.diary.DiaryDbHelper;
import edu.uwcse.pond.diary.GoalEntry;
import edu.uwcse.pond.diary.ActionLogDbHelper.Action;
import edu.uwcse.pond.nutrition.Consts;
import edu.uwcse.pond.nutrition.NutritionDbHelper;
import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class GoalEdityActivity extends Activity {
	
	private GoalListViewAdapter mGoalListViewAdapter; 
	
	private ListView mGoalListView; 
	
	private DiaryDbHelper mDbHelper; 
	
	private GoalEntry mEntry; 
	//private Map<Consts.PointComponent,Integer> mGoalMap;
	private ContentValues mGoalMap; 
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check for foodId passed in a bundle
        setContentView(R.layout.goal_view);
        mDbHelper =  DiaryDbHelper.getDiaryDbHelper(getApplicationContext());
        mDbHelper.logAction(Action.VIEW_GOAL, -1, -1, "View goal activity", "");
        //GoalEntry mEntry = mDbHelper.getMostRecentGoal();
        mGoalMap = mDbHelper.getAllMostRecentGoalAsContentVals();//getAllMostRecentGoalAsMap();
        fillData();
    }
	
	@Override
	protected void onStop() {
		// TODO Check for saving stuff or not? 
		super.onStop();
		
	
	}
	
	private void fillData() {
		// 
		mGoalListView = (ListView)findViewById(R.id.goal_view_listView);
		mGoalListViewAdapter = new GoalListViewAdapter(this, mGoalMap);
		mGoalListView.setAdapter(mGoalListViewAdapter);
		
		SharedPreferences settings = getSharedPreferences(OverviewActivity.PREFS_INSITU_STUDY, 0);
    	boolean canChangeGoals = settings.getBoolean(OverviewActivity.PREFS_INSITU_STUDY_CAN_CHANGE_GOALS, true);
		
		Button saveButton = (Button)findViewById(R.id.button1); 
		if (canChangeGoals){
			saveButton.setVisibility(View.VISIBLE);
			saveButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mDbHelper.updateGoal(mGoalListViewAdapter.getCurVals());
					GoalEdityActivity.this.finish();
				}
			});
		}else{
			saveButton.setVisibility(View.GONE);
		}
		
		Button cancelButton = (Button)findViewById(R.id.button2); 
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GoalEdityActivity.this.finish();
			}
		});
		
	}



	@Override
	protected void onDestroy() {
		mDbHelper.doneWithDb(); 
		super.onDestroy();
		
	}
}
