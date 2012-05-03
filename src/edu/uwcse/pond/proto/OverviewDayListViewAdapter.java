package edu.uwcse.pond.proto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import edu.uwcse.pond.diary.PointsDiaryTableHelper;
import edu.uwcse.pond.nutrition.Consts.FoodClass;
import edu.uwcse.pond.nutrition.Consts.PointComponent;
import edu.uwcse.pond.proto.R.color;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView.ScaleType;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class OverviewDayListViewAdapter extends BaseAdapter {

	OverviewActivity context;
	PointComponent[] keys;
	Map<PointComponent, Integer> goalVals;
	Map<PointComponent, Double> diaryVals;
	Map<PointComponent, ShowInfoButtonClickListener> showInfoButtonListeners; 
	Map<PointComponent, PlusOneButtonClickListener> plusOneButtonListeners; 
	
	/**
	 * 
	 * @param context
	 * @param goals A map of PointComponents and target vals, with all vlas being targeted as either 0 or not present.  
	 * @param entry A map of all PointComponents and all the daily vals overall 
	 */
	public OverviewDayListViewAdapter(OverviewActivity context, 
								Map<PointComponent,Integer> goals, 
								Map<PointComponent, Double> entry) {
		super();
		this.context = context;
		
		this.keys = new PointComponent[goals.keySet().size()];
		goals.keySet().toArray(this.keys);
		Arrays.sort(this.keys, new Comparator<PointComponent>() {

			@Override
			public int compare(PointComponent object1, PointComponent object2) {
				return object1.getOrderId() - object2.getOrderId();
			}
		});
	
		
		this.goalVals = goals;
		this.diaryVals = entry;
		showInfoButtonListeners = new HashMap<PointComponent, OverviewDayListViewAdapter.ShowInfoButtonClickListener>();
		plusOneButtonListeners = new HashMap<PointComponent, OverviewDayListViewAdapter.PlusOneButtonClickListener>();
	}
	
	public Map<PointComponent, Integer> getGoalVals(){
		return goalVals;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return keys.length;
	}
	
	/**
	 * Returns which component the item represents. From that, one can get the goal & diary Vals
	 */
	@Override
	public Object getItem(int arg0) {
		return keys[arg0];
	}

	/**
	 * 
	 */
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		if (convertView == null || 
				(convertView.getId() != R.layout.overview_component_list_view_row)) {
			LayoutInflater inflater = context.getLayoutInflater();
			convertView = inflater.inflate(
					R.layout.overview_component_list_view_row, null);
		}

		TextView txtViewTitle;
		Button plusOneButton;
		Button infoButton;
		
		// Set the component name
		txtViewTitle = (TextView) convertView
				.findViewById(R.id.overview_ptCompName_textView);
		txtViewTitle.setText(keys[position].getDesc());
		
		plusOneButton = (Button) convertView
				.findViewById(R.id.overview_ptCompAdd_Button);
		plusOneButton.setBackgroundResource(keys[position].getButtonBgId());
		if (!plusOneButtonListeners.containsKey(keys[position])){
			plusOneButtonListeners.put(keys[position], new PlusOneButtonClickListener(position));
		}
		plusOneButton.setOnClickListener(plusOneButtonListeners.get(keys[position]));
		plusOneButton.setOnLongClickListener(plusOneButtonListeners.get(keys[position]));

		infoButton = (Button) convertView
				.findViewById(R.id.overview_ptCompShowInfo_Button);
		infoButton.setBackgroundColor(R.color.black);
		int color_id = convertView.getContext().getResources()
				.getColor(keys[position].getColorId());
		infoButton.setTextColor(color_id);
		if (!showInfoButtonListeners.containsKey(keys[position])){
			showInfoButtonListeners.put(keys[position], new ShowInfoButtonClickListener(position,
					(LinearLayout) convertView));
		}
		infoButton.setOnClickListener(showInfoButtonListeners.get(keys[position]));
		showInfoButtonListeners.get(keys[position]).doTheRightThing((LinearLayout)convertView);

		// TODO: Include fancy transitions if I feel like it.
		// final LayoutTransition transitioner = new LayoutTransition();
		// container.setLayoutTransition(transitioner);

		LinearLayout layout = (LinearLayout) convertView
				.findViewById(R.id.linearLayout2);
		layout.removeAllViews();
		
		// Add points
//		LinearLayout tokenHolder = populatePointsView(convertView.getContext(), layout,
//				diaryVals.get(keys[position]), keys[position]);

		// Add goal drawables
//		populateGoalView(convertView.getContext(), tokenHolder,layout, diaryVals.get(keys[position]), goalVals.get(keys[position]) );
			//	goalVals.get(keys[position]) - diaryVals.get(keys[position]));

		// layout.setOnTouchListener(new
		// StartEntriesListButtonClickListener(keys[position]));
		
		fillTokens(convertView.getContext(), 
				layout, 
				null, 
				diaryVals.get(keys[position]), 
				goalVals.get(keys[position]), 
				11, 
				keys[position].getDrawableId(), 
				keys[position].getExtraDrawableId(), 
				keys[position].getDrawableHalfId(), 
				R.drawable.empty_circle);
				

		return convertView;
	}


	private LinearLayout fillTokens(Context context, 
			LinearLayout parent, 
			LinearLayout tokenHolder, 
			double numPoints, 
		//	double numTokensToAdd, 
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
		if (numPoints > 0){
			//for (int i=(int)curNumTokens; i<curNumTokens+numTokensToAdd; i++){
			for (double i=0; i<numPoints; i++){
				// Start a new row
				if (i>1 && i%maxTokensPerRow == 0){
					tokenHolder = new LinearLayout(context); 
					tokenHolder.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
							LayoutParams.WRAP_CONTENT));
					tokenHolder.setOrientation(LinearLayout.HORIZONTAL);
					parent.addView(tokenHolder);
				}

				// Detect if a half-point is appropriate
				if (numPoints-i < 1 ){
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
		
		for (double j = Math.ceil(numPoints); j<goalTokens; j++){
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


		private LinearLayout populatePointsView(Context context, LinearLayout parent, double val, PointComponent pc) {
			
			LinearLayout token_holder = new LinearLayout(context); 
			token_holder.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
	                LayoutParams.WRAP_CONTENT));
			token_holder.setOrientation(LinearLayout.HORIZONTAL);
			parent.addView(token_holder);
			
			if (val > 0){
				for (int i=0; i<val; i++){
					if (i%11 == 0){
						token_holder = new LinearLayout(context); 
						token_holder.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				                LayoutParams.WRAP_CONTENT));
						token_holder.setOrientation(LinearLayout.HORIZONTAL);
						parent.addView(token_holder);
					}
					
					ImageView img2 = new ImageView(context);
					img2.setScaleType(ScaleType.CENTER);
					img2.setImageResource(pc.getDrawableId());
					token_holder.addView(img2);
				}
			}
			
			// TODO: If a partial-point is left, add a half-point.
			
			return token_holder; 
		}
		
		private void populateGoalView(Context context, LinearLayout token_holder, LinearLayout parent, double howManyAreIn, int howManyTotal) {
			
			if (howManyTotal > howManyAreIn){
				for (int i=((int)Math.ceil(howManyAreIn)); i<howManyTotal; i++){
					if (i%11==0){
						token_holder = new LinearLayout(context); 
						token_holder.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				                LayoutParams.WRAP_CONTENT));
						token_holder.setOrientation(LinearLayout.HORIZONTAL);
						parent.addView(token_holder);
					}
					
					ImageView img2 = new ImageView(context);
					img2.setImageResource(R.drawable.empty_circle);
					token_holder.addView(img2);
				}
			}
		}

	
	class PlusOneButtonClickListener implements OnClickListener, OnLongClickListener {
		
		int myPosition; 
		
		// Might also need the rating bar, to increment. 
		// Might be able to get away with firing a "dataChanged" event. 
		public PlusOneButtonClickListener(int pos){
			myPosition = pos; 
		}
		
		@Override
		public void onClick(View v) {
			// Get existing amount
			double val = diaryVals.get(keys[myPosition]);
			
			// Update amount
			diaryVals.put(keys[myPosition], val + 1);
			
			OverviewDayListViewAdapter.this.notifyDataSetChanged();
			OverviewDayListViewAdapter.this.context.updateComponentPlusOne(keys[myPosition]);
			
			CharSequence text = "+1 Added";
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(OverviewDayListViewAdapter.this.context, text, duration);
			toast.show();
			
		}

		@Override
		public boolean onLongClick(View arg0) {
			// Get existing amount
			double val = diaryVals.get(keys[myPosition]);
			
			// Update amount
			diaryVals.put(keys[myPosition], val + 0.5);
			
			OverviewDayListViewAdapter.this.notifyDataSetChanged();
			OverviewDayListViewAdapter.this.context.updateComponentPlusHalf(keys[myPosition]);
			
			CharSequence text = "+1/2 Added";
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(OverviewDayListViewAdapter.this.context, text, duration);
			toast.show();
			
			return true; 
		}
	}
	

	
	class ShowInfoButtonClickListener implements OnClickListener {
		
		int myPosition; 
		WebView myView; 
		int defHeight; 
		LinearLayout parentView; 
		public boolean isShowing = false; 
		
		// Might also need the rating bar, to increment. 
		// Might be able to get away with firing a "dataChanged" event. 
		public ShowInfoButtonClickListener(int pos, LinearLayout parent){
			myPosition = pos; 
			myView = new WebView(parent.getContext()); 
			myView.loadUrl("file:///android_asset/docs/"+keys[pos]+".html");
			myView.setTag("InfoView");
			myView.setBackgroundColor(keys[pos].getColorId());
			parentView = parent; 
		}
		
		public void setParentView(LinearLayout layout){
			parentView = layout; 
		}
		
		@Override
		public void onClick(View v) {
			 final AlertDialog.Builder alert = new AlertDialog.Builder(context);

			 if (isShowing){
				 ensureInfoIsHidden(); 
			 }
			 else{
				 ensureInfoIsShown(); 
			 }
			 
				/*
			 if (myView.getVisibility() == View.VISIBLE){
				 
				 myView.setVisibility(View.INVISIBLE);
				 myView.invalidate();
			 }
			 else{
				 myView.setVisibility(View.VISIBLE);
				 myView.invalidate();
			 }
				/*LayoutInflater inflater = context.getLayoutInflater();
				//FrameLayout f1 = (FrameLayout)alert.findViewById(android.R.id.body);
				//f1.addView(inflater.inflate(R.layout.dialog_view, f1, false));
				View layout = inflater.inflate(R.layout.component_info_dialog, null);
				
				
				
			/*	WebView wv;  
		        wv = (WebView) layout.findViewById(R.id.component_info_WebView);
		        WebSettings settings = wv.getSettings();
		        settings.setSupportZoom(true); 
		        settings.setBuiltInZoomControls(true);
		        
		        
		        
		        wv.loadUrl("file:///android_asset/docs/"+keys[myPosition]+".html");   
		         
				
				alert.setView(layout);
				
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								dialog.cancel();
							}
						});
				
				alert.show();
		        */
		        
			
		}
		
		public void doTheRightThing(LinearLayout newParent){
			WebView curView = (WebView)newParent.findViewWithTag("InfoView");
			if (curView != null){
				newParent.removeView(curView);
			}
			setParentView(newParent);
			if (isShowing){
				ensureInfoIsShown();
			}
			else{
				ensureInfoIsHidden();
			}
		}

		public void ensureInfoIsShown() {
			if (myView.getParent()!=null){
				((LinearLayout)myView.getParent()).removeView(myView);
			}
			parentView.addView(myView, 1);//parentView.getChildCount()-2);
			 isShowing = true;
		}

		public void ensureInfoIsHidden() {
			parentView.removeView(myView);
			 isShowing = false;
		}
	}
	
	class StartEntriesListButtonClickListener implements OnTouchListener {

		private PointComponent myPointComponent; 
		
		public StartEntriesListButtonClickListener(PointComponent pc) {
			myPointComponent = pc; 
		}
		/*
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, EntriesListActivity.class);
			Bundle extras = intent.getExtras();
			if (extras == null){
				extras = new Bundle();
			}
			//extras.putString(EntriesListActivity.DATA_WHICH_COMPONENT_KEY, PointComponent.FATS.getPtDbColName());
			intent.putExtra(EntriesListActivity.DATA_WHICH_COMPONENT_INT_KEY, myPointComponent.getPtDbColName());
			context.startActivity(intent);
			
		}*/

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			Intent intent = new Intent(context, DailyDetailActivity.class);
			Bundle extras = intent.getExtras();
			if (extras == null){
				extras = new Bundle();
			}
			//extras.putString(EntriesListActivity.DATA_WHICH_COMPONENT_KEY, PointComponent.FATS.getPtDbColName());
			intent.putExtra(DailyDetailActivity.DATA_WHICH_COMPONENT_INT_KEY, myPointComponent.getPtDbColName());
			context.startActivity(intent);
			return true;
		}
	
	}
}
