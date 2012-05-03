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
