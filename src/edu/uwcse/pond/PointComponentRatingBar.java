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
package edu.uwcse.pond;
import edu.uwcse.pond.nutrition.Consts.PointComponent;
import android.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RatingBar;


public class PointComponentRatingBar extends RatingBar {
	
	private PointComponent mPointComponent; 

	public PointComponentRatingBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public PointComponentRatingBar(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public PointComponentRatingBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void setPointComponent(PointComponent pc){
		mPointComponent = pc;
	}

	
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		//if (mPointComponent.equals(PointComponent.FATS)){
			//setProgressDrawable(getResources().getDrawable(edu.uwcse.pond.proto.R.drawable.pt_rating_bar_fats));
		//}
		Drawable fats = getResources().getDrawable(edu.uwcse.pond.proto.R.drawable.pt_rating_bar_fats);
		Drawable other = getProgressDrawable();
		fats.setBounds(other.getBounds());
		setProgressDrawable(fats);
		super.onDraw(canvas);
		
	}
}
