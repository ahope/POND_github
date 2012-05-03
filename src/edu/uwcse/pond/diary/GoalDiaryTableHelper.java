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


import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.uwcse.pond.nutrition.Consts.PointComponent;

import android.content.ContentValues;
import android.database.Cursor;

public class GoalDiaryTableHelper {

	public static final String TABLE_NAME = "tblGoalDiary"; 
	
	public static final String COL_ROWID = "_id"; 
	public static final String COL_TIMESTAMP = "timestamp"; 
	public static final String COL_FRUIT_GOAL = "fruitGoal"; 	
	public static final String COL_FRUIT_WHOLE_GOAL = "wholeFruitGoal"; 	
	public static final String COL_VEGGIE_GOAL = "veggieGoal"; 	
	public static final String COL_VEGGIE_WHOLE_GOAL = "wholeVeggieGoal"; 	
	public static final String COL_GRAINS_GOAL = "grainsGoal";	
	public static final String COL_GRAINS_WHOLE_GOAL = "wholeGrainsGoal";	
	public static final String COL_PROTEIN_GOAL = "proteinGoal"; 	
	public static final String COL_DAIRY_GOAL = "dairyGoal"; 	
	public static final String COL_SODIUM_GOAL = "sodiumGoal";	
	public static final String COL_SOLID_FATS_GOAL = "solidFatsGoal";
	public static final String COL_OILS_GOAL = "oilsGoal";
	public static final String COL_SUGAR_GOAL = "sugarGoal"; 	
	public static final String COL_SAT_FATS_GOAL = "fatsGoal"; 	
	
	public static final String COL_FRUIT_ISVALID = "fruitIsValid"; 	
	public static final String COL_FRUIT_WHOLE_ISVALID = "wholeFruitIsValid"; 	
	public static final String COL_VEGGIE_ISVALID = "veggieIsValid"; 	
	public static final String COL_VEGGIE_WHOLE_ISVALID = "wholeVeggieIsValid"; 	
	public static final String COL_GRAINS_ISVALID = "grainsIsValid";	
	public static final String COL_GRAINS_WHOLE_ISVALID = "wholeGrainsIsValid";	
	public static final String COL_PROTEIN_ISVALID = "proteinIsValid"; 	
	public static final String COL_DAIRY_ISVALID = "dairyIsValid"; 	
	public static final String COL_SODIUM_ISVALID = "sodiumIsValid";	
	public static final String COL_SOLID_FATS_ISVALID = "solidFatsIsValid";
	public static final String COL_OILS_ISVALID = "oilsIsValid";
	public static final String COL_SUGAR_ISVALID = "sugarIsValid"; 	
	public static final String COL_SAT_FATS_ISVALID= "fatsIsValid";
	public static final String COL_COMMENT = "comment"; 
	public static final String COL_IS_VALID = "isValid";
	
	public static final int USDA_FRUIT_GOAL = 2; // Goal: <1 cups/day, .5c/point
		public static final int USDA_FRUIT_WHOLE_GOAL = 3; // >1.5 cups/day, 0.5c/point
		public static final int USDA_VEGGIE_GOAL = 4;  // 2 c/day, 0.5c/point
		public static final int USDA_VEGGIE_WHOLE_GOAL = 2;  // 1 c/day, 0.5c/point
		public static final int USDA_GRAINS_GOAL = 6;  // 6 oz/day, 1 oz/point
		public static final int USDA_GRAINS_WHOLE_GOAL = 3; // 3 oz/day, 1 oz/point
		public static final int USDA_PROTEIN_GOAL = 5; // 5 oz/day, 1 oz/point
		public static final int USDA_DAIRY_GOAL = 5; // 2.6 cups/day, 0.5 cup/point
		public static final int USDA_SODIUM_GOAL = 6; // 1.4 grams/day, 250mg/point
		public static final int USDA_SOLID_FATS_GOAL = 6; // 150 cals/day, 25 cals/point.  ; proxy for Sat Fat;  
		public static final int USDA_OILS_GOAL = 3; // 24g/day; 8g/point.  
		public static final int USDA_SUGAR_GOAL = 8; // 400 cal/day, 50 cals/point. 
		public static final int USDA_SAT_FATS_GOAL = 6;


	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + 
			"( " +
			COL_ROWID + " integer primary key autoincrement, " + 
			COL_TIMESTAMP + " text not null, " + 
			COL_FRUIT_GOAL  + " integer default "+ USDA_FRUIT_GOAL  +", " +
			COL_FRUIT_WHOLE_GOAL  + " integer default "+ USDA_FRUIT_WHOLE_GOAL+", "  +
			COL_VEGGIE_GOAL  + " integer default "+ USDA_VEGGIE_GOAL  +", "  +
			COL_VEGGIE_WHOLE_GOAL + " integer default "+ USDA_VEGGIE_WHOLE_GOAL  +", "  +
			COL_GRAINS_GOAL  + " integer default "+ USDA_GRAINS_GOAL  +", "  +
			COL_GRAINS_WHOLE_GOAL + " integer default "+ USDA_GRAINS_WHOLE_GOAL  +", "  +
			COL_PROTEIN_GOAL  + " integer default "+ USDA_PROTEIN_GOAL  +", "  +
			COL_DAIRY_GOAL + " integer default "+ USDA_DAIRY_GOAL  +", "  +
			COL_SODIUM_GOAL  + " integer default "+ USDA_SODIUM_GOAL  +", "  +
			COL_SUGAR_GOAL + " integer default "+ USDA_SUGAR_GOAL  +", "  +
			COL_SAT_FATS_GOAL + " integer default "+ USDA_SAT_FATS_GOAL  +", "  +
			COL_OILS_GOAL + " integer default "+ USDA_OILS_GOAL  +", "  +
			COL_SOLID_FATS_GOAL + " integer default "+ USDA_SOLID_FATS_GOAL  +", "  +
			COL_FRUIT_ISVALID  + " integer default 1, " +
			COL_FRUIT_WHOLE_ISVALID  + " integer default 1, " +
			COL_VEGGIE_ISVALID  + " integer default 1, " +
			COL_VEGGIE_WHOLE_ISVALID + " integer default 1, " +
			COL_GRAINS_ISVALID  + " integer default 1, " +
			COL_GRAINS_WHOLE_ISVALID + " integer default 1, " +
			COL_PROTEIN_ISVALID  + " integer default 1, " +
			COL_DAIRY_ISVALID + " integer default 1, " +
			COL_SODIUM_ISVALID  + " integer default 1, " +
			COL_SUGAR_ISVALID + " integer default 1, " +
			COL_SAT_FATS_ISVALID + " integer default 1, " +
			COL_OILS_ISVALID + " integer default 1, " +
			COL_SOLID_FATS_ISVALID + " integer default 1, " +
			COL_COMMENT + " text, " +
			COL_IS_VALID + " integer default 1" +
			")"; 
	
	/*
	public static GoalEntry getGoalEntryFromCursor(Cursor cursor){
		GoalEntry entry = new GoalEntry(); 
		entry.setFruitGoal(cursor.getInt(cursor.getColumnIndex(COL_FRUIT_GOAL)));
		entry.setFruitWholeGoal(cursor.getInt(cursor.getColumnIndex(COL_FRUIT_WHOLE_GOAL)));
		entry.setVeggieGoal(cursor.getInt(cursor.getColumnIndex(COL_FRUIT_WHOLE_GOAL)));
		entry.setVeggieWholeGoal(cursor.getInt(cursor.getColumnIndex(COL_FRUIT_WHOLE_GOAL)));
		entry.setGrainsGoal(cursor.getInt(cursor.getColumnIndex(COL_FRUIT_WHOLE_GOAL)));
		entry.setGrainsWholeGoal(cursor.getInt(cursor.getColumnIndex(COL_FRUIT_WHOLE_GOAL)));
		entry.setProteinGoal(cursor.getInt(cursor.getColumnIndex(COL_FRUIT_WHOLE_GOAL)));
		entry.setDairyGoal(cursor.getInt(cursor.getColumnIndex(COL_FRUIT_WHOLE_GOAL)));
		entry.setSodiumGoal(cursor.getInt(cursor.getColumnIndex(COL_FRUIT_WHOLE_GOAL)));
		
		entry.setSugarGoal(cursor.getInt(cursor.getColumnIndex(COL_FRUIT_WHOLE_GOAL)));
		entry.setFatsGoal(cursor.getInt(cursor.getColumnIndex(COL_FRUIT_WHOLE_GOAL)));
		entry.setOilsGoal(cursor.getInt(cursor.getColumnIndex(COL_OILS_GOAL)));
		entry.setSolidFatsGoal(cursor.getInt(cursor.getColumnIndex(COL_SOLID_FATS_GOAL)));
		return entry; 
	}*/
	
	public static HashMap<PointComponent,Integer>  getValidGoalMap(Cursor cursor){
		HashMap<PointComponent,Integer> entry = new HashMap<PointComponent,Integer>();
		putValidGoalInMap(cursor, entry, PointComponent.FRUIT, COL_FRUIT_GOAL );
		putValidGoalInMap(cursor, entry, PointComponent.FRUIT_WHOLE, COL_FRUIT_WHOLE_GOAL );
		putValidGoalInMap(cursor, entry, PointComponent.VEGGIE, COL_VEGGIE_GOAL );
		putValidGoalInMap(cursor, entry, PointComponent.VEGGIE_GREEN, COL_VEGGIE_WHOLE_GOAL );
		putValidGoalInMap(cursor, entry, PointComponent.GRAINS, COL_GRAINS_GOAL );
		putValidGoalInMap(cursor, entry, PointComponent.GRAINS_WHOLE, COL_GRAINS_WHOLE_GOAL );
		putValidGoalInMap(cursor, entry, PointComponent.PROTEIN, COL_PROTEIN_GOAL );
		putValidGoalInMap(cursor, entry, PointComponent.DAIRY, COL_DAIRY_GOAL );
		putValidGoalInMap(cursor, entry, PointComponent.SODIUM, COL_SODIUM_GOAL );
		putValidGoalInMap(cursor, entry, PointComponent.SUGAR, COL_SUGAR_GOAL );
		putValidGoalInMap(cursor, entry, PointComponent.OILS, COL_OILS_GOAL );
		putValidGoalInMap(cursor, entry, PointComponent.SOLID_FATS, COL_SOLID_FATS_GOAL );
		return entry; 
	}
	
	private static void putValidGoalInMap(Cursor cursor,
			HashMap<PointComponent, Integer> entry, 
			PointComponent key, 
			String colName) {
		boolean isValid;
		String isValidColName = colName.replace("Goal", "IsValid");
		isValid = (cursor.getInt(cursor.getColumnIndex(isValidColName)) == 1); 
		
		if (isValid){//!ignoreZeros || val > 0){

			int val = cursor.getInt(cursor.getColumnIndex(colName));
			
			entry.put(key, val);
		}
	}
	
	public static HashMap<PointComponent,Integer>  getGoalMapFromCursor(Cursor cursor, boolean ignoreZeros){
		HashMap<PointComponent,Integer> entry = new HashMap<PointComponent,Integer>(); 
		checkGoal(cursor, entry, PointComponent.FRUIT, COL_FRUIT_GOAL, ignoreZeros);
		checkGoal(cursor, entry, PointComponent.FRUIT_WHOLE, COL_FRUIT_WHOLE_GOAL, ignoreZeros);
		checkGoal(cursor, entry, PointComponent.VEGGIE, COL_VEGGIE_GOAL, ignoreZeros);
		checkGoal(cursor, entry, PointComponent.VEGGIE_GREEN, COL_VEGGIE_WHOLE_GOAL, ignoreZeros);
		checkGoal(cursor, entry, PointComponent.GRAINS, COL_GRAINS_GOAL, ignoreZeros);
		checkGoal(cursor, entry, PointComponent.GRAINS_WHOLE, COL_GRAINS_WHOLE_GOAL, ignoreZeros);
		checkGoal(cursor, entry, PointComponent.PROTEIN, COL_PROTEIN_GOAL, ignoreZeros);
		checkGoal(cursor, entry, PointComponent.DAIRY, COL_DAIRY_GOAL, ignoreZeros);
		checkGoal(cursor, entry, PointComponent.SODIUM, COL_SODIUM_GOAL, ignoreZeros);
		checkGoal(cursor, entry, PointComponent.SUGAR, COL_SUGAR_GOAL, ignoreZeros);
		checkGoal(cursor, entry, PointComponent.OILS, COL_OILS_GOAL, ignoreZeros);
		checkGoal(cursor, entry, PointComponent.SOLID_FATS, COL_SOLID_FATS_GOAL, ignoreZeros);
		return entry; 
	}

	private static void checkGoal(Cursor cursor,
			HashMap<PointComponent, Integer> entry, 
			PointComponent key, 
			String colName, 
			boolean checkValid) {
		boolean isValid = true; 
		if (checkValid){
			String isValidColName = colName.replace("Goal", "IsValid");
			isValid = cursor.getInt(cursor.getColumnIndex(isValidColName)) == 1; 
		}
		
		int val = cursor.getInt(cursor.getColumnIndex(colName));
		if (isValid){//!ignoreZeros || val > 0){
			entry.put(key, val);
		}
	}
	
	public static ContentValues getContentValsFromMap(Map<PointComponent, Integer> newVals){
		ContentValues vals = new ContentValues();
		
		vals.put(COL_FRUIT_ISVALID, 0);  
		vals.put(COL_FRUIT_WHOLE_ISVALID, 0);   	
		vals.put(COL_VEGGIE_ISVALID, 0);   	
		vals.put(COL_VEGGIE_WHOLE_ISVALID, 0);   	
		vals.put(COL_GRAINS_ISVALID, 0);  	
		vals.put(COL_GRAINS_WHOLE_ISVALID, 0);  	
		vals.put(COL_PROTEIN_ISVALID, 0);   	
		vals.put(COL_DAIRY_ISVALID, 0);   	
		vals.put(COL_SODIUM_ISVALID, 0);  
		vals.put(COL_SOLID_FATS_ISVALID, 0);  
		vals.put(COL_OILS_ISVALID, 0);  
		vals.put(COL_SUGAR_ISVALID, 0);   	
		vals.put(COL_SAT_FATS_ISVALID, 0);  
		
		Set<PointComponent> keys = newVals.keySet();
		Iterator<PointComponent> iter = keys.iterator(); 
		while(iter.hasNext()){
			PointComponent ptComp = iter.next();
			String goalColName = ptComp.getGoalDbColName();
			String validColName = goalColName.replace("Goal", "IsValid");
			
			if (newVals.get(ptComp).intValue() == 0){
				vals.put(goalColName, newVals.get(ptComp));
				vals.put(validColName, 0);
			}else{
				vals.put(goalColName, newVals.get(ptComp));
				vals.put(validColName, 1);
			}
			//vals.put(ptComp.getGoalDbColName(), newVals.get(ptComp));
		}
		return vals; 
	}
	
	public static String getDefaultInsertRow(){
		return "INSERT INTO " + TABLE_NAME + "(" + COL_TIMESTAMP + ", " + COL_COMMENT + ") VALUES( '" + new Date().toString() + "', 'default')";
				
	}
	
	public static int getUsdaDefaultForComponent(PointComponent pc){
		switch(pc){
		
		case VEGGIE_GREEN:
			return USDA_VEGGIE_WHOLE_GOAL;
		case VEGGIE:
			return USDA_VEGGIE_GOAL;
		case FRUIT: 
			return USDA_FRUIT_GOAL;
		case FRUIT_WHOLE: 
			return USDA_FRUIT_WHOLE_GOAL; 
		case GRAINS: 
			return USDA_GRAINS_GOAL; 
		case GRAINS_WHOLE:
			return USDA_GRAINS_WHOLE_GOAL; 
		case OILS:
			return USDA_OILS_GOAL; 
		case PROTEIN:
			return USDA_PROTEIN_GOAL;
		case SAT_FATS:
			return USDA_SAT_FATS_GOAL; 
		case SOLID_FATS: 
			return USDA_SOLID_FATS_GOAL; 
		case SODIUM: 
			return USDA_SODIUM_GOAL; 
		case SUGAR: 
			return USDA_SUGAR_GOAL; 		
		case DAIRY:
			return USDA_DAIRY_GOAL;
			default: 
				return 4; 
		}
		
			
	}
}

