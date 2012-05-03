package edu.uwcse.pond.proto;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

public class PointDrawable extends ShapeDrawable {

	public PointDrawable() {
		super(new OvalShape());
		int x = 0;
	    int y = 0;
	    int width = 300;
	    int height = 50;

	    getPaint().setColor(0xff74AC23);
	    setBounds(x, y, width, height);
	}
	
	
	
}
