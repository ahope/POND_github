package edu.uwcse.pond.diary;

import java.util.Date;

public class LocationDiaryTableHelper {

	public static final String TABLE_NAME = "tblLocationDiary";

	public static final String COL_ROWID = "_id";
	public static final String COL_TIMESTAMP = "timestamp";
	public static String COL_LOC_NAME = "locName";
	public static String COL_LAT = "lat";
	public static String COL_LNG = "lng";
	public static String COL_SIZE = "size";
	public static String COL_REFID = "refId";
	public static String COL_IS_VALID = "isValid";

	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
			+ "( " + COL_ROWID + " integer primary key autoincrement, "
			+ COL_TIMESTAMP + " text not null, " + COL_LOC_NAME + " text, "
			+ COL_LAT + " real, " + COL_LNG + " real, " + COL_SIZE + " real, "
			+ COL_REFID + " integer default -1, " + COL_IS_VALID
			+ " integer default 1 " + ")";

	public static String TEST_HOME_LOC_ENTRY = "INSERT INTO " + TABLE_NAME
			+ " " + "( " + COL_TIMESTAMP + ",  " + COL_LOC_NAME + ",  "
			+ COL_LAT + ",  " + COL_LNG + ",  " + COL_SIZE + "  "
			+ ") VALUES ( '" + new Date().toString() + "' , " + "'Home',"
			+ "47.676479, " + "-122.323284, " + "31.2)";

	public static String getDefaultInsertRow() {
		return "INSERT INTO " + TABLE_NAME + "(" + COL_TIMESTAMP + ", "
				+ COL_LOC_NAME + ") VALUES( '" + new Date().toString()
				+ "', 'Other')";

	}

	/*
	 * public static GoalEntry getGoalEntryFromCursor(Cursor cursor){ GoalEntry
	 * entry = new GoalEntry();
	 * entry.setFruitGoal(cursor.getInt(cursor.getColumnIndex(COL_FRUIT_GOAL)));
	 * entry
	 * .setFruitWholeGoal(cursor.getInt(cursor.getColumnIndex(COL_FRUIT_WHOLE_GOAL
	 * )));
	 * entry.setVeggieGoal(cursor.getInt(cursor.getColumnIndex(COL_FRUIT_WHOLE_GOAL
	 * ))); entry.setVeggieWholeGoal(cursor.getInt(cursor.getColumnIndex(
	 * COL_FRUIT_WHOLE_GOAL)));
	 * entry.setGrainsGoal(cursor.getInt(cursor.getColumnIndex
	 * (COL_FRUIT_WHOLE_GOAL)));
	 * entry.setGrainsWholeGoal(cursor.getInt(cursor.getColumnIndex
	 * (COL_FRUIT_WHOLE_GOAL)));
	 * entry.setProteinGoal(cursor.getInt(cursor.getColumnIndex
	 * (COL_FRUIT_WHOLE_GOAL)));
	 * entry.setDairyGoal(cursor.getInt(cursor.getColumnIndex
	 * (COL_FRUIT_WHOLE_GOAL)));
	 * entry.setSodiumGoal(cursor.getInt(cursor.getColumnIndex
	 * (COL_FRUIT_WHOLE_GOAL)));
	 * entry.setAlcoholGoal(cursor.getInt(cursor.getColumnIndex
	 * (COL_FRUIT_WHOLE_GOAL)));
	 * entry.setSugarGoal(cursor.getInt(cursor.getColumnIndex
	 * (COL_FRUIT_WHOLE_GOAL)));
	 * entry.setFatsGoal(cursor.getInt(cursor.getColumnIndex
	 * (COL_FRUIT_WHOLE_GOAL)));
	 * 
	 * return entry; }
	 * 
	 * public static HashMap<PointComponent,Integer> getGoalMapFromCursor(Cursor
	 * cursor){ HashMap<PointComponent,Integer> entry = new
	 * HashMap<PointComponent,Integer>(); entry.put(PointComponent.FRUIT,
	 * cursor.getInt(cursor.getColumnIndex(COL_FRUIT_GOAL)));
	 * entry.put(PointComponent.FRUIT_WHOLE,
	 * cursor.getInt(cursor.getColumnIndex(COL_FRUIT_WHOLE_GOAL)));
	 * entry.put(PointComponent.VEGGIE,
	 * cursor.getInt(cursor.getColumnIndex(COL_VEGGIE_GOAL)));
	 * entry.put(PointComponent.VEGGIE_GREEN,
	 * cursor.getInt(cursor.getColumnIndex(COL_VEGGIE_WHOLE_GOAL)));
	 * entry.put(PointComponent.GRAINS,
	 * cursor.getInt(cursor.getColumnIndex(COL_GRAINS_GOAL)));
	 * entry.put(PointComponent.GRAINS_WHOLE,
	 * cursor.getInt(cursor.getColumnIndex(COL_GRAINS_WHOLE_GOAL)));
	 * entry.put(PointComponent.PROTEIN,
	 * cursor.getInt(cursor.getColumnIndex(COL_PROTEIN_GOAL)));
	 * entry.put(PointComponent.DAIRY,
	 * cursor.getInt(cursor.getColumnIndex(COL_DAIRY_GOAL)));
	 * entry.put(PointComponent.SODIUM,
	 * cursor.getInt(cursor.getColumnIndex(COL_SODIUM_GOAL)));
	 * entry.put(PointComponent.ALCOHOL,
	 * cursor.getInt(cursor.getColumnIndex(COL_ALCOHOL_GOAL)));
	 * entry.put(PointComponent.SUGAR,
	 * cursor.getInt(cursor.getColumnIndex(COL_SUGAR_GOAL)));
	 * entry.put(PointComponent.FATS,
	 * cursor.getInt(cursor.getColumnIndex(COL_FATS_GOAL)));
	 * 
	 * return entry; }
	 */

}
