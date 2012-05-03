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

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

public class PointDrawable extends ShapeDrawable {

	public PointDrawable() {
		super(new OvalShape());
		final int x = 0;
		final int y = 0;
		final int width = 300;
		final int height = 50;

		getPaint().setColor(0xff74AC23);
		setBounds(x, y, width, height);
	}

}
