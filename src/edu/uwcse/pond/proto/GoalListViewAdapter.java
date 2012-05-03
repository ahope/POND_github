package edu.uwcse.pond.proto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import edu.uwcse.pond.diary.GoalDiaryTableHelper;
import edu.uwcse.pond.nutrition.NutritionEntry;
import edu.uwcse.pond.nutrition.Consts.PointComponent;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView.ScaleType;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class GoalListViewAdapter extends BaseAdapter {

	Activity context;
	PointComponent[] keys = {PointComponent.FRUIT, 
			PointComponent.FRUIT_WHOLE, 
			PointComponent.VEGGIE, 
			PointComponent.VEGGIE_GREEN, 
			PointComponent.GRAINS, 
			PointComponent.GRAINS_WHOLE, 
			PointComponent.PROTEIN,
			PointComponent.DAIRY, 
			PointComponent.SODIUM, 
			PointComponent.SUGAR, 
			PointComponent.OILS,
			PointComponent.SOLID_FATS};
	//Map<PointComponent, Integer> vals;
	ContentValues vals; 

	public GoalListViewAdapter(Activity context, ContentValues pointsVals) {
		super();
		this.context = context;
/*
		this.keys = new PointComponent[pointsVals.keySet().size()];
		pointsVals.keySet().toArray(this.keys);*/
		this.vals = pointsVals;
	}

	public ContentValues getCurVals(){
		return vals;
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return keys.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		//	ViewHolder holder;
		LayoutInflater inflater =  context.getLayoutInflater();
		TextView txtViewTitle; 
		SeekBar goalAmt; 
		CheckBox goalCheckbox; 
		ImageView token; 
		TextView txtInfo; 
		TextView txtValue; 
		Button increaseButton; 
		Button decreaseButton; 
		TextView goalValueTextView; 
		final LinearLayout tokenHolder; 

		convertView = inflater.inflate(R.layout.goal_view_list_entry, null);
		//holder = new ViewHolder();
		txtViewTitle = (TextView) convertView.findViewById(R.id.goal_name_TextView);
	//	goalAmt = (SeekBar) convertView.findViewById(R.id.goal_level_SeekBar);
		goalCheckbox = (CheckBox)convertView.findViewById(R.id.goal_include_CheckBox);
	//	token = (ImageView)convertView.findViewById(R.id.goal_tokenImage);
		txtInfo = (TextView)convertView.findViewById(R.id.goal_infoTextView);
	//	txtValue = (TextView)convertView.findViewById(R.id.goal_curValTextView);
		decreaseButton = (Button)convertView.findViewById(R.id.goal_decreaseButton);
		increaseButton = (Button)convertView.findViewById(R.id.goal_increaseButton);
	//	goalValueTextView = (TextView)convertView.findViewById(R.id.goal_valueTextView);
		tokenHolder = (LinearLayout)convertView.findViewById(R.id.linearLayout1);
		
		txtViewTitle.setText(keys[position].getDesc());

		String goalColName = keys[position].getGoalDbColName();
		String validColName = goalColName.replace("Goal", "IsValid");
	//	goalAmt.setProgress(vals.getAsInteger(goalColName));//(keys[position]).intValue());//setRating(val[keys[position]]);
		
		goalCheckbox.setChecked(vals.getAsInteger(validColName).intValue()==1);//(keys[position]).intValue()>0);

	//	goalAmt.setOnSeekBarChangeListener(new GoalSeekBarChangeListener(position));	
		goalCheckbox.setOnCheckedChangeListener(new GoalCheckBoxChangeListener(position));//goalAmt));
		
	//	token.setImageResource(keys[position].getDrawableId());
		txtInfo.setText("Default: " + GoalDiaryTableHelper.getUsdaDefaultForComponent(keys[position]));
	//	txtValue.setText(vals.getAsString(goalColName));

		decreaseButton.setBackgroundResource(keys[position].getButtonBgId());
		increaseButton.setBackgroundResource(keys[position].getButtonBgId());
	//	goalValueTextView.setText(vals.getAsString(goalColName)); 
		
		final String myGoalKeyName = keys[position].getGoalDbColName();
		final int whichPosition = position; 
		
		increaseButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int curVal = vals.getAsInteger(myGoalKeyName);
				vals.put(myGoalKeyName, curVal + 1);
				fillValues(context, tokenHolder, keys[whichPosition], vals.getAsInteger(myGoalKeyName));
			}
		});
		
		decreaseButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int curVal = vals.getAsInteger(myGoalKeyName);
				if (curVal > 1){
					vals.put(myGoalKeyName, curVal - 1);
					
					fillValues(context, tokenHolder, keys[whichPosition], vals.getAsInteger(myGoalKeyName));
				}
			}
		});
		
		fillValues(context, tokenHolder, keys[position], vals.getAsInteger(goalColName));
		
		return convertView;
	}

	protected void updateGoalItem(int position, int newVal){
		//vals.put(keys[position], newVal);
		vals.put(keys[position].getGoalDbColName(), newVal);
	}

	class GoalSeekBarChangeListener implements OnSeekBarChangeListener {

		int myPosition; 

		public GoalSeekBarChangeListener(int i){
			myPosition = i; 
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			vals.put(keys[myPosition].getGoalDbColName(), seekBar.getProgress());
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub

		}
	}

	class GoalCheckBoxChangeListener implements OnCheckedChangeListener{

		int myPosition; 

		public GoalCheckBoxChangeListener(int pos){
			myPosition = pos; 
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked){
				//vals.put(keys[myPosition].getGoalDbColName(), mySeekbar.getProgress());
				String goalColName = keys[myPosition].getGoalDbColName();
				String validColName = goalColName.replace("Goal", "IsValid");
				vals.put(validColName, 1);
			}else{
				String goalColName = keys[myPosition].getGoalDbColName();
				String validColName = goalColName.replace("Goal", "IsValid");
				vals.put(validColName, 0);
			}
		}
	}
/*
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
	*/
	private void fillValues(Context context, 
			LinearLayout parentTokenHolder, 
			PointComponent pointComponent,
			int curGoal){
		// inflate Component View into the listHolder
		//LayoutInflater inflater =  ((Activity) context).getLayoutInflater();
		//TextView txtViewTitle; 
		//LinearLayout parentTokenHolder; 

		//View newView = inflater.inflate(R.layout.point_detail_list_view_row, null);
		//txtViewTitle = (TextView) newView.findViewById(R.id.component_name_TextView);
		//parentTokenHolder = (LinearLayout)newView.findViewById(R.id.points_layout);

		// Set the name of the TextView in the Component View
		//txtViewTitle.setText(pointComponent.getDesc());
		//double adj_nutVal = 0;


	/*	LinearLayout tokenHolder = fillTokens(this, 
				parentTokenHolder, 
				null, 
				0, 
				curDiaryVal, 
				curGoalVal, 
				9, 
				R.drawable.generic_filled_goal, 
				R.drawable.generic_extra_filled_goal, 
				R.drawable.generic_filled_goal); // TODO: Make this a half-drawable
				*/
		int usdaGoal = GoalDiaryTableHelper.getUsdaDefaultForComponent(pointComponent);
		
		parentTokenHolder.removeAllViews();
		
		//R.drawable.generic_filled_goal, 
		//R.drawable.generic_extra_filled_goal, 
		//R.drawable.generic_filled_goal
		
		int maxTokensPerRow = 9;
		
		int pixels = parentTokenHolder.getWidth();
		int tokensPerRow = (int)(((double)pixels)/20.0); 
		
		LinearLayout tokenHolder = fillTokens(context, 
				parentTokenHolder, 
				null, //tokenHolder, 
				0, //curDiaryVal, 
				curGoal, 
				usdaGoal, 
				maxTokensPerRow, 
				pointComponent.getDrawableId(), 
				pointComponent.getExtraDrawableId(), 
				pointComponent.getDrawableHalfId());

		tokenHolder = fillTokens(context, 
				parentTokenHolder, 
				tokenHolder, 
				curGoal, //curDiaryVal + nutVal, 
				maxTokensPerRow-curGoal, //curDiaryVal + nutVal),//curGoalVal, 
				usdaGoal, 
				tokensPerRow, 
				R.drawable.generic_filled_goal, 
				R.drawable.dark_token, 
				R.drawable.dark_token);

		//listHolder.addView(newView);
	}

	private static LinearLayout fillTokens(Context context, 
			LinearLayout parent, 
			LinearLayout tokenHolder, 
			int curNumTokens, 
			int numTokensToAdd, 
			int goalTokens, 
			int maxTokensPerRow,
			int tokenDrawableId, 
			int altTokenDrawableId, 
			int halfTokenDrawableId){

		if (tokenHolder == null){
			// Create a new token holder & add it to the parent
			tokenHolder = new LinearLayout(context); 
			tokenHolder.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			tokenHolder.setOrientation(LinearLayout.HORIZONTAL);
			parent.addView(tokenHolder);
		}
		if (numTokensToAdd > 0){
			//for (int i=(int)curNumTokens; i<curNumTokens+numTokensToAdd; i++){
			for (double i=curNumTokens; i<curNumTokens+numTokensToAdd; i++){
				if (i>1 && (i%maxTokensPerRow <1)){
					tokenHolder = new LinearLayout(context); 
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
					ImageView img2 = new ImageView(context);
					img2.setScaleType(ScaleType.CENTER);
					if (i>(goalTokens-1)){
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


}
