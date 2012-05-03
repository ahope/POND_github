package edu.uwcse.pond.diary;

import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;

public class PointsDiaryTableHelper {
	
	public static final String TABLE_NAME = "tblPtsDiary"; 
	

	public static String COL_ROWID = "_id"; 	
	public static String COL_TIME_ENTERED = "timeEntered";	
	public static String COL_LOCATION_ID = "locationId";	
	//public static String COL_LOCATIONNAME = "locationName" ;	
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
	public static String COL_SOURCE = "source"; // 0 for user; 1 for "from Food entry"
	
	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + 
			"( " +
			COL_ROWID + " integer primary key autoincrement, " + 
			COL_TIME_ENTERED + " text not null, " + 
			COL_LOCATION_ID + " integer, " +
			COL_FRUIT_VAL  + " real, " +
			COL_FRUIT_WHOLE_VAL  + " real, " +
			COL_VEGGIE_VAL  + " real, " +
			COL_VEGGIE_WHOLE_VAL + " real, " +
			COL_GRAINS_VAL  + " real, " +
			COL_GRAINS_WHOLE_VAL + " real, " +
			COL_PROTEIN_VAL  + " real, " +
			COL_DAIRY_VAL + " real, " +
			COL_SODIUM_VAL  + " real, " +
			COL_ALCOHOL_VAL  + " real, " +
			COL_SUGAR_VAL + " real, " +
			COL_FATS_VAL + " real, " +
			COL_SOLID_FATS_VAL + " real, " +
			COL_OILS_VAL + " real, " +
			COL_COMMENT + " text," +
			COL_IS_VALID+ " integer," +
			COL_SOURCE + " integer default 0" +
			")"; 
	
	
	
	
}
