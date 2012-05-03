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
