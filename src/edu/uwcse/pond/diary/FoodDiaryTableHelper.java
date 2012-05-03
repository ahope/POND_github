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

public class FoodDiaryTableHelper {

	public static final String TABLE_NAME = "tblFoodDiary"; 
	
	public static final String COL_ROWID = "_id";
	public static String COL_TIME_ENTERED = "timeEntered";	
	public static final String COL_FOODID = "foodId"; 
	public static final String COL_SERVINGID = "servingId"; 
	public static final String COL_AMOUNT = "amount"; 
	public static final String COL_ISVALID = "isValid"; 
	public static final String COL_LOCATION_ID = "locationId"; 
	public static final String COL_PTS_ENTRY_ID = "pointsEntryId"; 
	
	
	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + 
			"( " +
			COL_ROWID + " integer primary key autoincrement, " + 
			COL_TIME_ENTERED + " text not null," +
			COL_FOODID + " integer," + 
			COL_SERVINGID + " integer, " + 
			COL_AMOUNT + " real, " + 
			COL_ISVALID + " integer default 1, " + 
			COL_LOCATION_ID + " integer, " + 
			COL_PTS_ENTRY_ID + " integer " + 
			")";
	
	
}
