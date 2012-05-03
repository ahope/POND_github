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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.uwcse.pond.nutrition.Consts.PointComponent;
import edu.uwcse.pond.proto.R;

public class PointsListViewAdapter extends BaseAdapter {

	Activity context;
	PointComponent[] keys;
	Map<PointComponent, Double> vals;
	Map<PointComponent, Integer> goals;
	private double mServingsMultiplier = 1.0;

	public PointsListViewAdapter(Activity context,
			Map<PointComponent, Double> pointsVals,
			Map<PointComponent, Integer> goal) {
		super();
		this.context = context;

		this.keys = new PointComponent[pointsVals.keySet().size()];
		pointsVals.keySet().toArray(this.keys);
		this.vals = pointsVals;
		this.goals = goal;
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
		final LayoutInflater inflater = context.getLayoutInflater();
		TextView txtViewTitle;
		LinearLayout tokenHolder;

		// if (convertView == null)
		// {
		convertView = inflater.inflate(R.layout.point_detail_list_view_row,
				null);
		// holder = new ViewHolder();
		txtViewTitle = (TextView) convertView
				.findViewById(R.id.component_name_TextView);
		tokenHolder = (LinearLayout) convertView
				.findViewById(R.id.points_layout);
		// convertView.setTag(holder);
		/*
		 * } else { //holder = (ViewHolder) convertView.getTag();
		 * 
		 * }
		 */

		txtViewTitle.setText(keys[position].getDesc());

		final float adjusted_val = (float) mServingsMultiplier
				* vals.get(keys[position]).floatValue();

		populatePointsView(convertView.getContext(), tokenHolder, adjusted_val,
				keys[position]);
		populateGoalView(convertView.getContext(), tokenHolder,
				goals.get(keys[position]) - ((int) adjusted_val));

		return convertView;
	}

	private void populateGoalView(Context context, LinearLayout layout, int val) {

		if (val > 0) {
			for (int i = 0; i < val; i++) {
				final ImageView img2 = new ImageView(context);
				img2.setImageResource(R.drawable.empty_circle);
				layout.addView(img2);
			}
		}
	}

	private void populatePointsView(Context context, LinearLayout layout,
			float val, PointComponent pc) {

		if (val > 0) {
			for (int i = 0; i < val; i++) {
				final ImageView img2 = new ImageView(context);
				img2.setImageResource(pc.getDrawableId());
				/*
				 * if (val-i > 0 && val-i < 1){ LayoutParams param = new
				 * LayoutParams(LayoutParams.WRAP_CONTENT,
				 * LayoutParams.WRAP_CONTENT); double newWidth = (val - i) * 20;
				 * //TODO: Fix this magic number hack!!!//img2.getWidth();
				 * param.width = (int)newWidth; param.height = 20;
				 * img2.setMaxWidth((int)newWidth);
				 * img2.setMinimumWidth((int)newWidth);
				 * img2.setLayoutParams(param); }
				 */
				layout.addView(img2);
			}
		}
	}

	public void setServingsMultiplier(double val) {
		this.mServingsMultiplier = val;
	}

}
