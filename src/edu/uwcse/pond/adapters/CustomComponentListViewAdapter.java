/*
 * Copyright (C) 2012 University of Washington
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package edu.uwcse.pond.adapters;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.uwcse.pond.activities.CreateCustomPointFoodActivity;
import edu.uwcse.pond.activities.DailyDetailActivity;
import edu.uwcse.pond.nutrition.Consts.PointComponent;
import edu.uwcse.pond.proto.R;

public class CustomComponentListViewAdapter extends BaseAdapter {

	class MinusOneButtonClickListener implements OnClickListener,
			OnLongClickListener {

		int myPosition;

		// Might also need the rating bar, to increment.
		// Might be able to get away with firing a "dataChanged" event.
		public MinusOneButtonClickListener(int pos) {
			myPosition = pos;
		}

		@Override
		public void onClick(View v) {
			// Get existing amount
			final double val = foodVals.get(keys[myPosition]);

			if (val >= 1.0) {
				// Update amount
				foodVals.put(keys[myPosition], val - 1);

				CustomComponentListViewAdapter.this.notifyDataSetChanged();
				CustomComponentListViewAdapter.this.context
						.updateComponentMinusOne(keys[myPosition]);
				/*
				 * CharSequence text = "-1"; int duration = Toast.LENGTH_SHORT;
				 * 
				 * Toast toast =
				 * Toast.makeText(CustomComponentListViewAdapter.this.context,
				 * text, duration); toast.show();
				 */
			}
		}

		@Override
		public boolean onLongClick(View arg0) {
			// Get existing amount
			final double val = foodVals.get(keys[myPosition]);

			if (val >= 0.5) {
				// Update amount
				foodVals.put(keys[myPosition], val - 0.5);

				CustomComponentListViewAdapter.this.notifyDataSetChanged();
				CustomComponentListViewAdapter.this.context
						.updateComponentMinusHalf(keys[myPosition]);
				/*
				 * CharSequence text = "+1/2 Added"; int duration =
				 * Toast.LENGTH_SHORT;
				 * 
				 * Toast toast =
				 * Toast.makeText(CustomComponentListViewAdapter.this.context,
				 * text, duration); toast.show();
				 */
			}

			return true;
		}
	}

	class PlusOneButtonClickListener implements OnClickListener,
			OnLongClickListener {

		int myPosition;

		// Might also need the rating bar, to increment.
		// Might be able to get away with firing a "dataChanged" event.
		public PlusOneButtonClickListener(int pos) {
			myPosition = pos;
		}

		@Override
		public void onClick(View v) {
			// Get existing amount
			final double val = foodVals.get(keys[myPosition]);

			// Update amount
			foodVals.put(keys[myPosition], val + 1);

			CustomComponentListViewAdapter.this.notifyDataSetChanged();
			CustomComponentListViewAdapter.this.context
					.updateComponentPlusOne(keys[myPosition]);
			/*
			 * CharSequence text = "+1 Added"; int duration =
			 * Toast.LENGTH_SHORT;
			 * 
			 * Toast toast =
			 * Toast.makeText(CustomComponentListViewAdapter.this.context, text,
			 * duration); toast.show();
			 */
		}

		@Override
		public boolean onLongClick(View arg0) {
			// Get existing amount
			final double val = foodVals.get(keys[myPosition]);

			// Update amount
			foodVals.put(keys[myPosition], val + 0.5);

			CustomComponentListViewAdapter.this.notifyDataSetChanged();
			CustomComponentListViewAdapter.this.context
					.updateComponentPlusHalf(keys[myPosition]);
			/*
			 * CharSequence text = "+1/2 Added"; int duration =
			 * Toast.LENGTH_SHORT;
			 * 
			 * Toast toast =
			 * Toast.makeText(CustomComponentListViewAdapter.this.context, text,
			 * duration); toast.show();
			 */

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
		public ShowInfoButtonClickListener(int pos, LinearLayout parent) {
			myPosition = pos;
			myView = new WebView(parent.getContext());
			myView.loadUrl("file:///android_asset/docs/" + keys[pos] + ".html");
			myView.setTag("InfoView");
			myView.setBackgroundColor(keys[pos].getColorId());
			parentView = parent;
		}

		public void doTheRightThing(LinearLayout newParent) {
			final WebView curView = (WebView) newParent
					.findViewWithTag("InfoView");
			if (curView != null) {
				newParent.removeView(curView);
			}
			setParentView(newParent);
			if (isShowing) {
				ensureInfoIsShown();
			} else {
				ensureInfoIsHidden();
			}
		}

		public void ensureInfoIsHidden() {
			parentView.removeView(myView);
			isShowing = false;
		}

		public void ensureInfoIsShown() {
			if (myView.getParent() != null) {
				((LinearLayout) myView.getParent()).removeView(myView);
			}
			parentView.addView(myView, 1);// parentView.getChildCount()-2);
			isShowing = true;
		}

		@Override
		public void onClick(View v) {
			new AlertDialog.Builder(context);

			if (isShowing) {
				ensureInfoIsHidden();
			} else {
				ensureInfoIsShown();
			}

			/*
			 * if (myView.getVisibility() == View.VISIBLE){
			 * 
			 * myView.setVisibility(View.INVISIBLE); myView.invalidate(); }
			 * else{ myView.setVisibility(View.VISIBLE); myView.invalidate(); }
			 * /*LayoutInflater inflater = context.getLayoutInflater();
			 * //FrameLayout f1 =
			 * (FrameLayout)alert.findViewById(android.R.id.body);
			 * //f1.addView(inflater.inflate(R.layout.dialog_view, f1, false));
			 * View layout = inflater.inflate(R.layout.component_info_dialog,
			 * null);
			 * 
			 * 
			 * 
			 * /* WebView wv; wv = (WebView)
			 * layout.findViewById(R.id.component_info_WebView); WebSettings
			 * settings = wv.getSettings(); settings.setSupportZoom(true);
			 * settings.setBuiltInZoomControls(true);
			 * 
			 * 
			 * 
			 * wv.loadUrl("file:///android_asset/docs/"+keys[myPosition]+".html")
			 * ;
			 * 
			 * 
			 * alert.setView(layout);
			 * 
			 * alert.setPositiveButton("Ok", new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface dialog, int whichButton) {
			 * dialog.cancel(); } });
			 * 
			 * alert.setNegativeButton("Cancel", new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface dialog, int whichButton) {
			 * dialog.cancel(); } });
			 * 
			 * alert.show();
			 */

		}

		public void setParentView(LinearLayout layout) {
			parentView = layout;
		}
	}

	class StartEntriesListButtonClickListener implements OnTouchListener {

		private final PointComponent myPointComponent;

		public StartEntriesListButtonClickListener(PointComponent pc) {
			myPointComponent = pc;
		}

		/*
		 * @Override public void onClick(View v) { Intent intent = new
		 * Intent(context, EntriesListActivity.class); Bundle extras =
		 * intent.getExtras(); if (extras == null){ extras = new Bundle(); }
		 * //extras.putString(EntriesListActivity.DATA_WHICH_COMPONENT_KEY,
		 * PointComponent.FATS.getPtDbColName());
		 * intent.putExtra(EntriesListActivity.DATA_WHICH_COMPONENT_INT_KEY,
		 * myPointComponent.getPtDbColName()); context.startActivity(intent);
		 * 
		 * }
		 */

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			final Intent intent = new Intent(context, DailyDetailActivity.class);
			Bundle extras = intent.getExtras();
			if (extras == null) {
				extras = new Bundle();
			}
			// extras.putString(EntriesListActivity.DATA_WHICH_COMPONENT_KEY,
			// PointComponent.FATS.getPtDbColName());
			intent.putExtra(DailyDetailActivity.DATA_WHICH_COMPONENT_INT_KEY,
					myPointComponent.getPtDbColName());
			context.startActivity(intent);
			return true;
		}

	}

	CreateCustomPointFoodActivity context;
	PointComponent[] keys;

	Map<PointComponent, Double> foodVals;

	Map<PointComponent, ShowInfoButtonClickListener> showInfoButtonListeners;

	Map<PointComponent, PlusOneButtonClickListener> plusOneButtonListeners;

	Map<PointComponent, MinusOneButtonClickListener> minusOneButtonListeners;

	/**
	 * 
	 * @param context
	 * @param goals
	 *            A map of PointComponents and target vals, with all vlas being
	 *            targeted as either 0 or not present.
	 * @param entry
	 *            A map of all PointComponents and all the daily vals overall
	 */
	public CustomComponentListViewAdapter(CreateCustomPointFoodActivity context) {
		super();
		this.context = context;

		foodVals = new HashMap<PointComponent, Double>();

		foodVals.put(PointComponent.FRUIT, 0.0);
		foodVals.put(PointComponent.FRUIT_WHOLE, 0.0);
		foodVals.put(PointComponent.VEGGIE, 0.0);
		foodVals.put(PointComponent.VEGGIE_GREEN, 0.0);
		foodVals.put(PointComponent.GRAINS, 0.0);
		foodVals.put(PointComponent.GRAINS_WHOLE, 0.0);
		foodVals.put(PointComponent.PROTEIN, 0.0);
		foodVals.put(PointComponent.DAIRY, 0.0);
		foodVals.put(PointComponent.SODIUM, 0.0);
		foodVals.put(PointComponent.SUGAR, 0.0);
		foodVals.put(PointComponent.OILS, 0.0);
		foodVals.put(PointComponent.SOLID_FATS, 0.0);

		this.keys = new PointComponent[foodVals.keySet().size()];
		foodVals.keySet().toArray(this.keys);
		Arrays.sort(this.keys, new Comparator<PointComponent>() {

			@Override
			public int compare(PointComponent object1, PointComponent object2) {
				return object1.getOrderId() - object2.getOrderId();
			}
		});

		showInfoButtonListeners = new HashMap<PointComponent, CustomComponentListViewAdapter.ShowInfoButtonClickListener>();
		plusOneButtonListeners = new HashMap<PointComponent, CustomComponentListViewAdapter.PlusOneButtonClickListener>();
		minusOneButtonListeners = new HashMap<PointComponent, CustomComponentListViewAdapter.MinusOneButtonClickListener>();
	}

	private LinearLayout fillTokens(Context context, LinearLayout parent,
			LinearLayout tokenHolder, double numPoints, int maxTokensPerRow,
			int tokenDrawableId, int extraTokenDrawableId,
			int halfTokenDrawableId, int emptyGoalTokenDrawableId) {

		if (tokenHolder == null) {
			// Create a new token holder & add it to the parent
			tokenHolder = new LinearLayout(context);
			tokenHolder.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			tokenHolder.setOrientation(LinearLayout.HORIZONTAL);
			parent.addView(tokenHolder);
		}
		if (numPoints > 0) {
			// for (int i=(int)curNumTokens; i<curNumTokens+numTokensToAdd;
			// i++){
			for (double i = 0; i < numPoints; i++) {
				// Start a new row
				if (i > 1 && i % maxTokensPerRow == 0) {
					tokenHolder = new LinearLayout(context);
					tokenHolder
							.setLayoutParams(new LayoutParams(
									LayoutParams.FILL_PARENT,
									LayoutParams.WRAP_CONTENT));
					tokenHolder.setOrientation(LinearLayout.HORIZONTAL);
					parent.addView(tokenHolder);
				}

				// Detect if a half-point is appropriate
				if (numPoints - i < 1) {
					final ImageView img2 = new ImageView(context);
					img2.setScaleType(ScaleType.CENTER);

					img2.setImageResource(halfTokenDrawableId);

					tokenHolder.addView(img2);
				} else {
					final ImageView img2 = new ImageView(context);
					img2.setScaleType(ScaleType.CENTER);
					img2.setImageResource(tokenDrawableId);

					tokenHolder.addView(img2);
				}
			}

		}

		return tokenHolder;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return keys.length;
	}

	/**
	 * Returns which component the item represents. From that, one can get the
	 * goal & diary Vals
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
		if (convertView == null
				|| (convertView.getId() != R.layout.custom_food_component_list_view_row)) {
			final LayoutInflater inflater = context.getLayoutInflater();
			convertView = inflater.inflate(
					R.layout.custom_food_component_list_view_row, null);
		}

		TextView txtViewTitle;
		Button plusOneButton;
		Button minusOneButton;
		Button infoButton;

		// Set the component name
		txtViewTitle = (TextView) convertView
				.findViewById(R.id.overview_ptCompName_textView);
		txtViewTitle.setText(keys[position].getDesc());

		plusOneButton = (Button) convertView
				.findViewById(R.id.overview_ptCompAdd_Button);
		plusOneButton.setBackgroundResource(keys[position].getButtonBgId());
		if (!plusOneButtonListeners.containsKey(keys[position])) {
			plusOneButtonListeners.put(keys[position],
					new PlusOneButtonClickListener(position));
		}
		plusOneButton.setOnClickListener(plusOneButtonListeners
				.get(keys[position]));
		plusOneButton.setOnLongClickListener(plusOneButtonListeners
				.get(keys[position]));

		minusOneButton = (Button) convertView
				.findViewById(R.id.overview_ptCompMinus_Button);
		minusOneButton.setBackgroundResource(keys[position].getButtonBgId());
		if (!minusOneButtonListeners.containsKey(keys[position])) {
			minusOneButtonListeners.put(keys[position],
					new MinusOneButtonClickListener(position));
		}
		minusOneButton.setOnClickListener(minusOneButtonListeners
				.get(keys[position]));
		minusOneButton.setOnLongClickListener(minusOneButtonListeners
				.get(keys[position]));

		infoButton = (Button) convertView
				.findViewById(R.id.overview_ptCompShowInfo_Button);
		infoButton.setBackgroundColor(R.color.black);
		final int color_id = convertView.getContext().getResources()
				.getColor(keys[position].getColorId());
		infoButton.setTextColor(color_id);
		if (!showInfoButtonListeners.containsKey(keys[position])) {
			showInfoButtonListeners.put(keys[position],
					new ShowInfoButtonClickListener(position,
							(LinearLayout) convertView));
		}
		infoButton.setOnClickListener(showInfoButtonListeners
				.get(keys[position]));
		showInfoButtonListeners.get(keys[position]).doTheRightThing(
				(LinearLayout) convertView);

		// TODO: Include fancy transitions if I feel like it.
		// final LayoutTransition transitioner = new LayoutTransition();
		// container.setLayoutTransition(transitioner);

		final LinearLayout layout = (LinearLayout) convertView
				.findViewById(R.id.linearLayout2);
		layout.removeAllViews();

		// Add points
		// LinearLayout tokenHolder =
		// populatePointsView(convertView.getContext(), layout,
		// diaryVals.get(keys[position]), keys[position]);

		// Add goal drawables
		// populateGoalView(convertView.getContext(), tokenHolder,layout,
		// diaryVals.get(keys[position]), goalVals.get(keys[position]) );
		// goalVals.get(keys[position]) - diaryVals.get(keys[position]));

		// layout.setOnTouchListener(new
		// StartEntriesListButtonClickListener(keys[position]));

		fillTokens(convertView.getContext(), layout, null,
				foodVals.get(keys[position]), 11,
				keys[position].getDrawableId(),
				keys[position].getExtraDrawableId(),
				keys[position].getDrawableHalfId(), R.drawable.empty_circle);

		return convertView;
	}

	private void populateGoalView(Context context, LinearLayout token_holder,
			LinearLayout parent, double howManyAreIn, int howManyTotal) {

		if (howManyTotal > howManyAreIn) {
			for (int i = ((int) Math.ceil(howManyAreIn)); i < howManyTotal; i++) {
				if (i % 11 == 0) {
					token_holder = new LinearLayout(context);
					token_holder
							.setLayoutParams(new LayoutParams(
									LayoutParams.FILL_PARENT,
									LayoutParams.WRAP_CONTENT));
					token_holder.setOrientation(LinearLayout.HORIZONTAL);
					parent.addView(token_holder);
				}

				final ImageView img2 = new ImageView(context);
				img2.setImageResource(R.drawable.empty_circle);
				token_holder.addView(img2);
			}
		}
	}

	private LinearLayout populatePointsView(Context context,
			LinearLayout parent, double val, PointComponent pc) {

		LinearLayout token_holder = new LinearLayout(context);
		token_holder.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		token_holder.setOrientation(LinearLayout.HORIZONTAL);
		parent.addView(token_holder);

		if (val > 0) {
			for (int i = 0; i < val; i++) {
				if (i % 11 == 0) {
					token_holder = new LinearLayout(context);
					token_holder
							.setLayoutParams(new LayoutParams(
									LayoutParams.FILL_PARENT,
									LayoutParams.WRAP_CONTENT));
					token_holder.setOrientation(LinearLayout.HORIZONTAL);
					parent.addView(token_holder);
				}

				final ImageView img2 = new ImageView(context);
				img2.setScaleType(ScaleType.CENTER);
				img2.setImageResource(pc.getDrawableId());
				token_holder.addView(img2);
			}
		}

		// TODO: If a partial-point is left, add a half-point.

		return token_holder;
	}
}
