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
