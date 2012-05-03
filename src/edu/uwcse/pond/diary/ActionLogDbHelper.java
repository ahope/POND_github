package edu.uwcse.pond.diary;

public class ActionLogDbHelper {
	
	public static final String TABLE_NAME = "tblActionLog"; 
	
	public static final String COL_ROWID = "_id"; 
	
	public static final String COL_TIMESTAMP = "timestamp"; 
	
	public static final String COL_DESC = "description"; 
	
	public static final String COL_ACTION_ID = "actionId"; 
	
	public static final String COL_REF_ID = "refId"; 
	
	public static final String COL_COMMENT = "comment"; 
	
	public static final String COL_TYPE_ID = "typeId";
	
	
	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + 
			"( " +
			COL_ROWID + " integer primary key autoincrement, " + 
			COL_TIMESTAMP + " text not null," +
			COL_ACTION_ID+ " integer," + 
			COL_REF_ID+ " integer, " + 
			COL_TYPE_ID+ " integer, " + 
			COL_DESC+ " text, " + 
			COL_COMMENT + " text " +
			")";

	
	
	/**
	 * These are the actions that I want to keep track of in 
	 * the ActionLog. 
	 * @author aha
	 *
	 */
	public enum Action {
		 START_APP,
		 PLUS_ONE,
		 VIEW_GOAL,
		 CHANGE_GOAL,
		 VIEW_DAY_LIST,
		 VIEW_WEEK_CHART,
		 FOOD_SEARCH,
		 VIEW_FOOD_DETAIL,
		 ADD_FOOD_TO_DIARY,
		 ADD_LOCATION,
		 VIEW_LOCATION,
		 CHANGE_LOCATION,
		 EDIT_RECORD,
		 DELETE_RECORD, 
		 STUDY, 
		 STUDY_TASK, 
		 STUDY_CONDITION, 
		 STUDY_TASK_RESULT,
		 STUDY_TASK_TIME, 
		 PLUS_HALF, 
		 FOOD_ENTRY_CALORIES
		/* CAFFEINE = 107,
		 CALORIES_FROM_FAT = 109,
		 CALORIES_FROM_SATURATED_FAT = 110,
		 SUGAR_ALCOHOL = 111,
		 OTHER_CARBOHYDRATE = 112,*/
	}
}
