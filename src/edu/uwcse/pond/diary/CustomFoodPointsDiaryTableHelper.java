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


public class CustomFoodPointsDiaryTableHelper {

	public static final String TABLE_NAME = "tblCustomFoodPtsDiary";

	public static String COL_ROWID = "_id";
	public static String COL_NAME = "name";
	public static String COL_TIME_ENTERED = "timeCreated";
	public static String COL_FRUIT_VAL = "fruitVal";
	public static String COL_FRUIT_WHOLE_VAL = "wholeFruitVal";
	public static String COL_VEGGIE_VAL = "veggieVal";
	public static String COL_VEGGIE_WHOLE_VAL = "wholeVeggieVal";
	public static String COL_GRAINS_VAL = "grainsVal";
	public static String COL_GRAINS_WHOLE_VAL = "wholeGrainsVal";
	public static String COL_PROTEIN_VAL = "proteinVal";
	public static String COL_DAIRY_VAL = "dairyVal";
	public static String COL_SODIUM_VAL = "sodiumVal";
	public static String COL_ALCOHOL_VAL = "alcoholVal";
	public static String COL_SOLID_FATS_VAL = "solidFatsVal";
	public static String COL_OILS_VAL = "oilsVal";
	public static String COL_SUGAR_VAL = "sugarVal";
	public static String COL_FATS_VAL = "fatsVal";
	public static String COL_COMMENT = "comment";
	public static String COL_IS_VALID = "isValid";
	public static String COL_NUT_ID = "nutritionId";
	public static String COL_NUM_SERVINGS = "numServings";

	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
			+ "( " + COL_ROWID + " integer primary key autoincrement, "
			+ COL_TIME_ENTERED + " text not null, " + COL_NAME
			+ " text not null, " + COL_FRUIT_VAL + " real default 0, "
			+ COL_FRUIT_WHOLE_VAL + " real default 0, " + COL_VEGGIE_VAL
			+ " real default 0, " + COL_VEGGIE_WHOLE_VAL + " real default 0, "
			+ COL_GRAINS_VAL + " real default 0, " + COL_GRAINS_WHOLE_VAL
			+ " real default 0, " + COL_PROTEIN_VAL + " real default 0, "
			+ COL_DAIRY_VAL + " real default 0, " + COL_SODIUM_VAL
			+ " real default 0, " + COL_ALCOHOL_VAL + " real default 0, "
			+ COL_SUGAR_VAL + " real default 0, " + COL_FATS_VAL
			+ " real default 0, " + COL_SOLID_FATS_VAL + " real default 0, "
			+ COL_OILS_VAL + " real default 0, " + COL_COMMENT + " text,"
			+ COL_IS_VALID + " integer default 1," + COL_NUT_ID + " integer,"
			+ COL_NUM_SERVINGS + " integer default 1" + ")";

}
