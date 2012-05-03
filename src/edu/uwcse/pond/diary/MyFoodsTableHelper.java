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
package edu.uwcse.pond.diary;

public class MyFoodsTableHelper {

	public static final String TABLE_NAME = "tblMyFoods"; 
	
	public static final String COL_ROWID = "_id"; 
	public static final String COL_TIMESTAMP = "timestamp";
	public static final String COL_FOOD_ID = "foodId";
	public static String COL_FOOD_NAME = "foodName"; 	
	public static String COL_MANUFACTURER_NAME = "manName"; 
	public static String COL_SOURCE = "source"; // 1 for FoodEntry, 2 for PtsEntry

	

	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + 
			"( " +
			COL_ROWID + " integer primary key autoincrement, " + 
			COL_TIMESTAMP + " text not null, " + 
			COL_FOOD_ID + " integer not null, " + 
			COL_FOOD_NAME + " text not null, " +
			COL_MANUFACTURER_NAME+ " text, " +
			COL_SOURCE + " integer not null " +
			")"; 
	
}
