package edu.uwcse.pond.diary;


public class SearchHistoryTableHelper {

	public static final String TABLE_NAME = "tblSearches";

	public static final String COL_ROWID = "_id";
	public static final String COL_TIMESTAMP = "timestamp";
	public static String COL_SEARCH_TERM = "searchTerm";
	public static String COL_NUM_RESULTS = "numResults";

	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
			+ "( " + COL_ROWID + " integer primary key autoincrement, "
			+ COL_TIMESTAMP + " text not null, " + COL_SEARCH_TERM + " text, "
			+ COL_NUM_RESULTS + " integer " + ")";

}
