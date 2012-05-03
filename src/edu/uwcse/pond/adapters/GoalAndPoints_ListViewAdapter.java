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

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.uwcse.pond.nutrition.Consts.PointComponent;
import edu.uwcse.pond.proto.R;

public class GoalAndPoints_ListViewAdapter extends BaseAdapter {

	Activity context;
	PointComponent[] keys;
	Map<PointComponent, Integer> goalVals;
	Map<PointComponent, Double> diaryVals;

	Map<PointComponent, Double> nutritionVals;// vals;
	private double mServingsMultiplier = 1.0;

	/**
	 * 
	 * @param context
	 * @param goals
	 *            A map of PointComponents and target vals, with all vlas being
	 *            targeted as either 0 or not present.
	 * @param entry
	 *            A map of all PointComponents and all the daily vals overall
	 */
	public GoalAndPoints_ListViewAdapter(Activity context,
			Map<PointComponent, Integer> goals,
			Map<PointComponent, Double> entry,
			Map<PointComponent, Double> nutVals) {
		super();
		this.context = context;

		this.keys = new PointComponent[goals.keySet().size()];
		goals.keySet().toArray(this.keys);
		this.goalVals = goals;
		this.diaryVals = entry;
		this.nutritionVals = nutVals;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return keys.length;
	}

	public Map<PointComponent, Integer> getGoalVals() {
		return goalVals;
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

	/*
	 * @Override public View getView(int position, View convertView, ViewGroup
	 * arg2) { if (convertView == null) { LayoutInflater inflater =
	 * context.getLayoutInflater(); convertView = inflater.inflate(
	 * R.layout.overview_component_list_view_row, null); }
	 * 
	 * TextView txtViewTitle;
	 * 
	 * // Set the component name txtViewTitle = (TextView) convertView
	 * .findViewById(R.id.overview_ptCompName_textView);
	 * txtViewTitle.setText(keys[position].getDesc());
	 * 
	 * // TODO: Include fancy transitions if I feel like it. // final
	 * LayoutTransition transitioner = new LayoutTransition(); //
	 * container.setLayoutTransition(transitioner);
	 * 
	 * LinearLayout layout = (LinearLayout) convertView
	 * .findViewById(R.id.linearLayout2); layout.removeAllViews();
	 * 
	 * // Add points LinearLayout tokenHolder =
	 * populatePointsView(convertView.getContext(), layout,
	 * diaryVals.get(keys[position]), keys[position]);
	 * 
	 * // Add goal drawables populateGoalView(convertView.getContext(),
	 * tokenHolder,layout, diaryVals.get(keys[position]),
	 * goalVals.get(keys[position])); // goalVals.get(keys[position]) -
	 * diaryVals.get(keys[position]));
	 * 
	 * // layout.setOnTouchListener(new //
	 * StartEntriesListButtonClickListener(keys[position]));
	 * 
	 * return convertView; }
	 */

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		final LayoutInflater inflater = context.getLayoutInflater();
		TextView txtViewTitle;
		LinearLayout parentTokenHolder;

		// if (convertView == null)
		// {
		convertView = inflater.inflate(R.layout.point_detail_list_view_row,
				null);
		// holder = new ViewHolder();
		txtViewTitle = (TextView) convertView
				.findViewById(R.id.component_name_TextView);
		parentTokenHolder = (LinearLayout) convertView
				.findViewById(R.id.points_layout);
		// convertView.setTag(holder);
		/*
		 * } else { //holder = (ViewHolder) convertView.getTag();
		 * 
		 * }
		 */

		txtViewTitle.setText(keys[position].getDesc());

		double adj_nutVal = 0;
		final double cur_diaryVal = diaryVals.get(keys[position]);
		final int curGoalVal = goalVals.get(keys[position]);

		if (nutritionVals.containsKey(keys[position])) {
			adj_nutVal = (float) mServingsMultiplier
					* nutritionVals.get(keys[position]).floatValue();
		}
		LinearLayout tokenHolder = populateFilledPointsView(
				convertView.getContext(), parentTokenHolder, cur_diaryVal,
				curGoalVal);

		tokenHolder = populateNutPointsView(convertView.getContext(),
				tokenHolder, parentTokenHolder, cur_diaryVal, adj_nutVal,
				curGoalVal, keys[position]);

		populateGoalView(convertView.getContext(), tokenHolder,
				parentTokenHolder, diaryVals.get(keys[position])
						+ ((int) adj_nutVal), curGoalVal);

		return convertView;
	}

	/***
	 * Fills in the points that have already been consumed in other entries.
	 * 
	 * @param context
	 * @param parent
	 * @param val
	 * @param pc
	 * @return
	 */
	private LinearLayout populateFilledPointsView(Context context,
			LinearLayout parent, double val, int goalVal) {

		// Create a new token holder & add it to the parent
		LinearLayout token_holder = new LinearLayout(context);
		token_holder.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		token_holder.setOrientation(LinearLayout.HORIZONTAL);
		parent.addView(token_holder);

		if (val > 0) {
			for (int i = 0; i < val; i++) {
				if (i > 1 && i % 9 == 0) {
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
				if (i > goalVal) {
					img2.setImageResource(R.drawable.generic_extra_filled_goal);
				} else {
					img2.setImageResource(R.drawable.generic_filled_goal);
				}
				token_holder.addView(img2);
			}
		}

		// TODO: If a partial-point is left, add a half-point.

		return token_holder;
	}

	private void populateGoalView(Context context, LinearLayout token_holder,
			LinearLayout parent, double howManyAreIn, int howManyTotal) {

		if (howManyTotal > howManyAreIn) {
			for (int i = ((int) Math.ceil(howManyAreIn)); i < howManyTotal; i++) {
				if (i % 9 == 0) {
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

	private LinearLayout populateNutPointsView(Context context,
			LinearLayout token_holder, LinearLayout parent, double diaryVal,
			double nutVal, int goalVal, PointComponent pc) {

		if (nutVal > 0) {
			for (int i = (int) Math.ceil(diaryVal); i < nutVal; i++) {
				if (i > 1 && i % 9 == 0) {
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
				if (i > goalVal) {
					img2.setImageResource(pc.getExtraDrawableId());
				} else {
					img2.setImageResource(pc.getDrawableId());
				}
				token_holder.addView(img2);
			}
		}

		/*
		 * if (val-i > 0 && val-i < 1){ LayoutParams param = new
		 * LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		 * double newWidth = (val - i) * 20; //TODO: Fix this magic number
		 * hack!!!//img2.getWidth(); param.width = (int)newWidth; param.height =
		 * 20; img2.setMaxWidth((int)newWidth);
		 * img2.setMinimumWidth((int)newWidth); img2.setLayoutParams(param); }
		 */

		return token_holder;
	}

	public void setServingsMultiplier(double val) {
		this.mServingsMultiplier = val;
	}

}
