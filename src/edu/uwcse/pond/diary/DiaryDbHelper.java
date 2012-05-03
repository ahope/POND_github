package edu.uwcse.pond.diary;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.uwcse.pond.diary.ActionLogDbHelper.Action;
import edu.uwcse.pond.nutrition.Consts;
import edu.uwcse.pond.nutrition.Consts.PointComponent;
import edu.uwcse.pond.nutrition.NutritionEntry.Serving;
import edu.uwcse.pond.nutrition.NutritionEntry;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;

public class DiaryDbHelper  {

	private static DiaryDbHelper mDiaryDbHelperSingleton; 
	
	private static int mNumDiaryListeners = 0;
	
	public static DiaryDbHelper getDiaryDbHelper(Context ctx){
		if (mDiaryDbHelperSingleton == null){
			mDiaryDbHelperSingleton = new DiaryDbHelper(ctx);
			mDiaryDbHelperSingleton.open();
		}
		mNumDiaryListeners++;
		return mDiaryDbHelperSingleton;
	}
	
	public void doneWithDb(){
		mNumDiaryListeners--; 
		if (mNumDiaryListeners == 0){
			mDiaryDbHelperSingleton.close();
			mDiaryDbHelperSingleton = null; 
		}
	}
	
	private static final String DATABASE_NAME = "PondDiary";

	private static final int DATABASE_VERSION = 18; 
	
	private Context mContext; 
	
	private DbOpenHelper mDbHelper; 
	
	private SQLiteDatabase mDb; 
	
	private long mCurLocationId = -1; 
	
	public static final SimpleDateFormat DB_DATE_STORE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static class DbOpenHelper extends SQLiteOpenHelper{
		public DbOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// 
		}
	
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(PointsDiaryTableHelper.CREATE_TABLE);
			db.execSQL(FoodDiaryTableHelper.CREATE_TABLE);
			db.execSQL(GoalDiaryTableHelper.CREATE_TABLE);
			db.execSQL(LocationDiaryTableHelper.CREATE_TABLE);
			db.execSQL(ActionLogDbHelper.CREATE_TABLE);
			db.execSQL(SearchHistoryTableHelper.CREATE_TABLE);
			db.execSQL(MyFoodsTableHelper.CREATE_TABLE);
			db.execSQL(CustomFoodPointsDiaryTableHelper.CREATE_TABLE);
			
			db.execSQL(GoalDiaryTableHelper.getDefaultInsertRow());
			db.execSQL(LocationDiaryTableHelper.getDefaultInsertRow());
			db.execSQL(LocationDiaryTableHelper.TEST_HOME_LOC_ENTRY);
			
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS "+ PointsDiaryTableHelper.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "+ FoodDiaryTableHelper.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "+ GoalDiaryTableHelper.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "+ LocationDiaryTableHelper.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "+ ActionLogDbHelper.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "+ SearchHistoryTableHelper.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "+ CustomFoodPointsDiaryTableHelper.TABLE_NAME);
			
			db.execSQL("DROP TABLE IF EXISTS "+ MyFoodsTableHelper.TABLE_NAME);

			
		//	db.execSQL("DELETE FROM tblActionLog WHERE _id<1898"); 
			onCreate(db); 
		}

	}
	
	
	
	
	private DiaryDbHelper(Context ctx) {
		mContext = ctx; 
	}
	
	private DiaryDbHelper open() throws SQLException{
		mDbHelper = new DbOpenHelper(mContext); 
		mDb = mDbHelper.getWritableDatabase(); 
		return this; 
	}
	
	private void close(){
		mDbHelper.close(); 
	}
	
	public void setNewLocationId(long newId){
		this.mCurLocationId = newId; 
	}
	
	public long createNewCustomFoodPts(ContentValues vals){
		String newFoodName = vals.getAsString(CustomFoodPointsDiaryTableHelper.COL_NAME); 
		// Check to see if something already exists
		String query = "SELECT " + CustomFoodPointsDiaryTableHelper.COL_NAME + " " +
				"FROM " + CustomFoodPointsDiaryTableHelper.TABLE_NAME + 
				" WHERE " + CustomFoodPointsDiaryTableHelper.COL_NAME + 
				" LIKE ?;"; 
		Cursor c = mDb.rawQuery(query, new String[]{newFoodName});
		c.moveToFirst(); 
		if (c.getCount() > 0){
			// Error-- it already exists!!
			c.close(); 
			return -1;
		}else{
			c.close(); 
			vals.put(CustomFoodPointsDiaryTableHelper.COL_TIME_ENTERED, DB_DATE_STORE_FORMAT.format(new Date()));
			long id =mDb.insert(CustomFoodPointsDiaryTableHelper.TABLE_NAME, null, vals); 
			
			addCustomPtEntryToMyUsedFoods(id, vals.getAsString(CustomFoodPointsDiaryTableHelper.COL_NAME));
			
			return id;
		}
	} 
	
	/*public NutritionEntry getCustomFoodPtsAsNutritionEntry(long id){
		String query = "SELECT * FROM " + CustomFoodPointsDiaryTableHelper.TABLE_NAME + " WHERE " +
				CustomFoodPointsDiaryTableHelper.COL_ROWID + 
				" = ? AND " + CustomFoodPointsDiaryTableHelper.COL_IS_VALID + " = 1; "; 
		Cursor c = mDb.rawQuery(query, new String[]{Long.toString(id)}); 
		c.moveToFirst(); 
		
		NutritionEntry entry = new NutritionEntry(c.getInt(0), 
					c.getString(2), "custom");
		
	}*/
	
	public long createNewPointsEntry(ContentValues vals){
		vals.put(PointsDiaryTableHelper.COL_TIME_ENTERED, DB_DATE_STORE_FORMAT.format(new Date()));
		return mDb.insert(PointsDiaryTableHelper.TABLE_NAME, null, vals);
	}
	
	public long createNewPointsEntryFromCustomFood(ContentValues vals){
		vals.put(PointsDiaryTableHelper.COL_TIME_ENTERED, DB_DATE_STORE_FORMAT.format(new Date()));
		vals.remove("_id");
		vals.remove(CustomFoodPointsDiaryTableHelper.COL_NUT_ID);
		double numServings = vals.getAsDouble(CustomFoodPointsDiaryTableHelper.COL_NUM_SERVINGS);
		vals.remove(CustomFoodPointsDiaryTableHelper.COL_NUM_SERVINGS);
		vals.remove(CustomFoodPointsDiaryTableHelper.COL_TIME_ENTERED);
		
		if (numServings != 1.0){
			updateValsForServings(vals, numServings); 
		}
		
		vals.put("isValid", 1);
		
		vals.put(PointsDiaryTableHelper.COL_LOCATION_ID, mCurLocationId);
		vals.put(PointsDiaryTableHelper.COL_SOURCE, 3);
		vals.put(PointsDiaryTableHelper.COL_COMMENT, vals.getAsString(CustomFoodPointsDiaryTableHelper.COL_NAME));
		vals.remove(CustomFoodPointsDiaryTableHelper.COL_NAME);
		long id =  mDb.insert(PointsDiaryTableHelper.TABLE_NAME, null, vals);
		return id; 
	}
	
	public long createNewPointsEntryFromCustomFood(long customFoodId){
		 
		// Get the record
		String query = "SELECT * " +
				"FROM " + CustomFoodPointsDiaryTableHelper.TABLE_NAME + 
				" WHERE " + CustomFoodPointsDiaryTableHelper.COL_ROWID + 
				" = ?;"; 
		Cursor c = mDb.rawQuery(query, new String[]{Long.toString(customFoodId)});
		c.moveToFirst(); 
		if (c.getCount() == 0){
			// Error-- it doesn't exist!! 
			c.close(); 
			return -1;
		}
		ContentValues vals = new ContentValues();
		DatabaseUtils.cursorRowToContentValues(c, vals);
		c.close(); 
			
		return createNewPointsEntryFromCustomFood(vals); 
	}
	
	
	private void updateValsForServings(ContentValues vals, double numServings) {
		updateValForServings(CustomFoodPointsDiaryTableHelper.COL_DAIRY_VAL, vals, numServings);
		updateValForServings(CustomFoodPointsDiaryTableHelper.COL_FATS_VAL, vals, numServings);
		updateValForServings(CustomFoodPointsDiaryTableHelper.COL_FRUIT_VAL, vals, numServings);
		updateValForServings(CustomFoodPointsDiaryTableHelper.COL_FRUIT_WHOLE_VAL, vals, numServings);
		updateValForServings(CustomFoodPointsDiaryTableHelper.COL_GRAINS_VAL, vals, numServings);
		updateValForServings(CustomFoodPointsDiaryTableHelper.COL_GRAINS_WHOLE_VAL, vals, numServings);
		updateValForServings(CustomFoodPointsDiaryTableHelper.COL_OILS_VAL, vals, numServings);
		updateValForServings(CustomFoodPointsDiaryTableHelper.COL_PROTEIN_VAL, vals, numServings);
		updateValForServings(CustomFoodPointsDiaryTableHelper.COL_SODIUM_VAL, vals, numServings);
		updateValForServings(CustomFoodPointsDiaryTableHelper.COL_SOLID_FATS_VAL, vals, numServings);
		updateValForServings(CustomFoodPointsDiaryTableHelper.COL_SUGAR_VAL, vals, numServings);
		updateValForServings(CustomFoodPointsDiaryTableHelper.COL_VEGGIE_VAL, vals, numServings);
		updateValForServings(CustomFoodPointsDiaryTableHelper.COL_VEGGIE_WHOLE_VAL, vals, numServings);
	}
	
	private void updateValForServings(String colName, ContentValues val, double numServings){
		double curVal = val.getAsDouble(colName);
		val.put(colName, curVal * numServings);
	}

	public long updatePointsEntry(ContentValues vals){
		long old_id = vals.getAsLong("_id");
		String query = "UPDATE " + PointsDiaryTableHelper.TABLE_NAME + " SET isValid=0 WHERE _id = ?";
	//	mDb.rawQuery(query, new String[]{Long.toString(old_id)});
		
		
		String strFilter = "_id=" + old_id;
		ContentValues args = new ContentValues();
		args.put("isValid", 0);
		mDb.update(PointsDiaryTableHelper.TABLE_NAME, args, strFilter, null);
		
		// Consider refId
		//vals.put(PointsDiaryTableHelper., value)
		
		//vals.put(PointsDiaryTableHelper.COL_TIME_ENTERED, DB_DATE_STORE_FORMAT.format(new Date()));
		
		vals.remove("_id");
		
		long newId = mDb.insert(PointsDiaryTableHelper.TABLE_NAME, null, vals);
		
		logAction(Action.EDIT_RECORD, (int)newId, -1, "editing Record: " + old_id, Long.toString(old_id));
		return newId;
	}
	
	public long updateFoodEntry(ContentValues vals, NutritionEntry nutEntry, Serving serving){
		
		// First, update the Points Entry
		String strFilter = "_id=" + vals.getAsString(FoodDiaryTableHelper.COL_PTS_ENTRY_ID);
		ContentValues args = new ContentValues();
		args.put("isValid", 0);
		mDb.update(PointsDiaryTableHelper.TABLE_NAME, args, strFilter, null);
		args = null; 
		
		// Create a new Points Entry
		ContentValues ptVals = nutEntry.getPointsValsForAmount(
						serving, 
						vals.getAsDouble(FoodDiaryTableHelper.COL_AMOUNT));
				// Mark the entry as coming from foodEntry
				ptVals.put(PointsDiaryTableHelper.COL_SOURCE, 1);
				ptVals.put(PointsDiaryTableHelper.COL_COMMENT, nutEntry.getFoodName());
				ptVals.put(PointsDiaryTableHelper.COL_LOCATION_ID, vals.getAsLong(FoodDiaryTableHelper.COL_LOCATION_ID));
				ptVals.put(PointsDiaryTableHelper.COL_IS_VALID, 1);
				ptVals.put(PointsDiaryTableHelper.COL_TIME_ENTERED, vals.getAsString(FoodDiaryTableHelper.COL_TIME_ENTERED));
				
				long pts_id = mDb.insert(PointsDiaryTableHelper.TABLE_NAME, null, ptVals);
				
				
		long old_id = vals.getAsLong("_id");
	//	String query = "UPDATE " + PointsDiaryTableHelper.TABLE_NAME + " SET isValid=0 WHERE _id = ?";
	//	mDb.rawQuery(query, new String[]{Long.toString(old_id)});
		
		
		String foodDbFilter = "_id=" + old_id;
		ContentValues foodDbArgs = new ContentValues();
		foodDbArgs.put("isValid", 0);
		mDb.update(FoodDiaryTableHelper.TABLE_NAME, foodDbArgs, foodDbFilter, null);
		
		// Consider refId
		//vals.put(PointsDiaryTableHelper., value)
		
		//vals.put(PointsDiaryTableHelper.COL_TIME_ENTERED, DB_DATE_STORE_FORMAT.format(new Date()));
		
		vals.remove("_id");
		vals.put(FoodDiaryTableHelper.COL_PTS_ENTRY_ID, pts_id);
		long newId = mDb.insert(FoodDiaryTableHelper.TABLE_NAME, null, vals);
		
		logAction(Action.EDIT_RECORD, (int)newId, -1, "editing Record: " + old_id, Long.toString(old_id));
		
		logAction(Action.FOOD_ENTRY_CALORIES, (int)newId, (int)pts_id,"food Cals", 
				Double.toString(nutEntry.getCalories(serving, vals.getAsDouble(FoodDiaryTableHelper.COL_AMOUNT))));
		
		return newId;
	}
	
	
/*
	public long createNewPointsEntryPlusOne(PointComponent pc){
		ContentValues vals = new ContentValues(); 
		vals.put(pc.getPtDbColName(), 1);
		vals.put(PointsDiaryTableHelper.COL_TIME_ENTERED, DB_DATE_STORE_FORMAT.format(new Date()));
		vals.put(PointsDiaryTableHelper.COL_SOURCE, 2);
		vals.put(PointsDiaryTableHelper.COL_COMMENT, pc.getDesc() + "+1");
		return mDb.insert(PointsDiaryTableHelper.TABLE_NAME, null, vals);
	}*/
	
	public long createNewPointsEntryPlusHalf(PointComponent pc){
		long id;
		Cursor c = findVeryRecentRecord(pc); 
		if (c.getCount()>0){
			double newVal = c.getDouble(1)+1.0;
			String updateQuery = "UPDATE tblPtsDiary SET "+pc.getPtDbColName() +"="+newVal +" WHERE _id=" + c.getInt(0);
			Cursor updateCur = mDb.rawQuery(updateQuery, null);
			updateCur.moveToFirst(); 
			updateCur.close(); 
			id = c.getLong(0);
			
		}
		else{
			ContentValues vals = new ContentValues(); 
			vals.put(pc.getPtDbColName(), 0.5);
			vals.put(PointsDiaryTableHelper.COL_TIME_ENTERED, DB_DATE_STORE_FORMAT.format(new Date()));//mDbDateFormat.format(new Date()));
			vals.put(PointsDiaryTableHelper.COL_SOURCE, 2);
			vals.put(PointsDiaryTableHelper.COL_COMMENT, pc.getDesc() + "+1/2");
			vals.put(PointsDiaryTableHelper.COL_LOCATION_ID, mCurLocationId);
			vals.put(PointsDiaryTableHelper.COL_IS_VALID, 1);
			id = mDb.insert(PointsDiaryTableHelper.TABLE_NAME, null, vals);
		}
		logAction(Action.PLUS_HALF, (int)id, -1, pc.getDesc() + "+1/2", "");
		c.close(); 
		return id; 
	}
	
	public long createNewPointsEntryPlusOne(PointComponent pc){
		long id;
		Cursor c = findVeryRecentRecord(pc); 
		if (c.getCount()>0){
			double newVal = c.getDouble(1)+1.0;
			String updateQuery = "UPDATE tblPtsDiary SET "+pc.getPtDbColName() +"="+newVal +" WHERE _id=" + c.getInt(0);
			Cursor updateCur = mDb.rawQuery(updateQuery, null);
			updateCur.moveToFirst(); 
			updateCur.close(); 
			id = c.getLong(0);
			
		}
		else{
			ContentValues vals = new ContentValues(); 
			vals.put(pc.getPtDbColName(), 1.0);
			vals.put(PointsDiaryTableHelper.COL_TIME_ENTERED, DB_DATE_STORE_FORMAT.format(new Date()));//mDbDateFormat.format(new Date()));
			vals.put(PointsDiaryTableHelper.COL_SOURCE, 2);
			vals.put(PointsDiaryTableHelper.COL_COMMENT, pc.getDesc() + "+1");
			vals.put(PointsDiaryTableHelper.COL_LOCATION_ID, mCurLocationId);
			vals.put(PointsDiaryTableHelper.COL_IS_VALID, 1);
			id = mDb.insert(PointsDiaryTableHelper.TABLE_NAME, null, vals);
			
			
		}

		logAction(Action.PLUS_ONE, (int)id, -1, pc.getDesc() + "+1", "");
		c.close(); 
		return id; 
	}
	
	

	private Cursor findVeryRecentRecord(PointComponent pc) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, -15);
		String mostRecentQuery = "SELECT _id, "+pc.getPtDbColName()+" FROM tblPtsDiary WHERE isValid=1 AND timeEntered>'"+DB_DATE_STORE_FORMAT.format(cal.getTime())+"'";
		Cursor c = mDb.rawQuery(mostRecentQuery, null);
		c.moveToLast(); 
		long id;
		return c;
	}
	
	/*
	public long createNewFoodEntry(NutritionEntry entry, Serving serving, double amt){
		return createNewFoodEntry(entry, serving, amt, -1);
		
	}*/
	
	public long createNewFoodEntry(NutritionEntry entry, Serving serving, double amt){
		//String locName = getLocationName(locId);
		
		// Make a new Points entry 
		ContentValues vals = entry.getPointsValsForAmount(
				serving, 
				amt);
		// Mark the entry as coming from foodEntry
		vals.put(PointsDiaryTableHelper.COL_SOURCE, 1);
		vals.put(PointsDiaryTableHelper.COL_COMMENT, entry.getFoodName());
		vals.put(PointsDiaryTableHelper.COL_LOCATION_ID, mCurLocationId);
		vals.put(PointsDiaryTableHelper.COL_IS_VALID, 1);
		long pts_id = createNewPointsEntry(vals);
		
		ContentValues food_vals = new ContentValues();
		food_vals.put(FoodDiaryTableHelper.COL_FOODID, entry.getFoodId());
		food_vals.put(FoodDiaryTableHelper.COL_PTS_ENTRY_ID, pts_id);
		food_vals.put(FoodDiaryTableHelper.COL_AMOUNT, amt);
		food_vals.put(FoodDiaryTableHelper.COL_SERVINGID, serving.getServingType().getId());
		food_vals.put(FoodDiaryTableHelper.COL_TIME_ENTERED, DB_DATE_STORE_FORMAT.format(new Date()));
		food_vals.put(FoodDiaryTableHelper.COL_ISVALID, 1);
		food_vals.put(FoodDiaryTableHelper.COL_LOCATION_ID, mCurLocationId);
		long food_id = mDb.insert(FoodDiaryTableHelper.TABLE_NAME, null, food_vals);
		
		addToMyUsedFoods(entry);
		
		logAction(Action.ADD_FOOD_TO_DIARY, (int)food_id, -1, "add food to diary", entry.getFoodName() + " ");
		logAction(Action.FOOD_ENTRY_CALORIES, (int)food_id, (int)pts_id,"food Cals", Double.toString(entry.getCalories(serving, amt)));
		return food_id; 
	}

	private void addToMyUsedFoods(NutritionEntry entry) {
		// Check to see if the food is in myFoods yet
		String query = "select * " + 
				" FROM "+ MyFoodsTableHelper.TABLE_NAME +  
				" WHERE "+ MyFoodsTableHelper.COL_FOOD_ID +" = ? ;";
		Cursor results = mDb.rawQuery(query, new String[]{Long.toString(entry.getFoodId())});
		if (results.getCount() == 0){
			ContentValues myFoodVals = new ContentValues(); 
			myFoodVals.put(MyFoodsTableHelper.COL_FOOD_ID, entry.getFoodId()); 
			myFoodVals.put(MyFoodsTableHelper.COL_FOOD_NAME, entry.getFoodName()); 
			myFoodVals.put(MyFoodsTableHelper.COL_MANUFACTURER_NAME, entry.getManufacturerName());
			myFoodVals.put(MyFoodsTableHelper.COL_TIMESTAMP, DB_DATE_STORE_FORMAT.format(new Date()));
			myFoodVals.put(MyFoodsTableHelper.COL_SOURCE, 1);
			mDb.insert(MyFoodsTableHelper.TABLE_NAME, null, myFoodVals);
		}
	}
	
	private void addCustomPtEntryToMyUsedFoods(long foodId, String foodName) {
		// Check to see if the food is in myFoods yet
		String query = "select * " + 
				" FROM "+ MyFoodsTableHelper.TABLE_NAME +  
				" WHERE "+ MyFoodsTableHelper.COL_FOOD_ID +" = ? ;";
		Cursor results = mDb.rawQuery(query, new String[]{Long.toString(foodId)});
		if (results.getCount() == 0){
			ContentValues myFoodVals = new ContentValues(); 
			myFoodVals.put(MyFoodsTableHelper.COL_FOOD_ID, foodId); 
			myFoodVals.put(MyFoodsTableHelper.COL_FOOD_NAME, foodName); 
			myFoodVals.put(MyFoodsTableHelper.COL_MANUFACTURER_NAME, "custom");
			myFoodVals.put(MyFoodsTableHelper.COL_TIMESTAMP, DB_DATE_STORE_FORMAT.format(new Date()));
			myFoodVals.put(MyFoodsTableHelper.COL_SOURCE, 2);
			
			mDb.insert(MyFoodsTableHelper.TABLE_NAME, null, myFoodVals);
		}
	}
	
	public Cursor getFoodEntryFromPointsEntryId(long ptsEntryId){
		String query = "select * " + 
				" FROM "+ FoodDiaryTableHelper.TABLE_NAME +  
				" WHERE "+ FoodDiaryTableHelper.COL_PTS_ENTRY_ID +" = ? ;";
		return mDb.rawQuery(query, new String[]{Long.toString(ptsEntryId)});
	}
	
	public ContentValues getFoodEntryFromPointsEntryAsVals(long ptsEntryId){
		String query = "select * " + 
				" FROM "+ FoodDiaryTableHelper.TABLE_NAME +  
				" WHERE "+ FoodDiaryTableHelper.COL_PTS_ENTRY_ID +" = ? ;";
		Cursor c = mDb.rawQuery(query, new String[]{Long.toString(ptsEntryId)});
		c.moveToFirst(); 
		ContentValues vals = new ContentValues(); 
		
		//DatabaseUtils.cursorRowToContentValues(c, new ContentValues());
		
		for(int x = 0; x < c.getColumnCount(); x++){
			vals.put(c.getColumnName(x), c.getString(x));
		}
		
		c.close(); 
		return vals; 
	}
	
	public Cursor getFoodEntry(long entryId){
		String query = "select * " + 
				" FROM "+ FoodDiaryTableHelper.TABLE_NAME +  
				" WHERE "+ FoodDiaryTableHelper.COL_ROWID +" = ? ;";
		return mDb.rawQuery(query, new String[]{Long.toString(entryId)});
	}
	
	public Cursor getPointsEntry(long ptsId){
		String query = "select * " + 
				" FROM "+ PointsDiaryTableHelper.TABLE_NAME +  
				" WHERE "+ PointsDiaryTableHelper.COL_ROWID +" = ? ;";
		return mDb.rawQuery(query, new String[]{Long.toString(ptsId)});
	}
	
	public int getEntrySource(long id){
		String query = "select source " + 
				" FROM "+ PointsDiaryTableHelper.TABLE_NAME +  
				" WHERE "+ PointsDiaryTableHelper.COL_ROWID +" = ? ;";
		Cursor c = mDb.rawQuery(query, new String[]{Long.toString(id)});
		c.moveToFirst();
		return c.getInt(0);
	}
	
	/*
	public DiaryEntry getSummedPointsEntryForDay(Date day){
		day.setHours(0);
		day.setMinutes(0);
		day.setSeconds(0);
		Calendar cal = Calendar.getInstance();
		cal.setTime(day);
		cal.add(Calendar.DATE, 1);
		String query = "select * from "+ PointsDiaryTableHelper.TABLE_NAME +" WHERE isValid=1 AND "+ PointsDiaryTableHelper.COL_TIME_ENTERED +" > ? AND "+ PointsDiaryTableHelper.COL_TIME_ENTERED+" <  ?;";
		Cursor cursor = mDb.rawQuery(query, new String[]{day.toString(), cal.toString()});
		
		DiaryEntry entry = new DiaryEntry();
		
		if (cursor.moveToFirst()){
			do{
				updateEntryWithPointsRow(entry, cursor);
			}while (cursor.moveToNext());
		}
		
		cursor.close(); 
		
		return entry;
	}
	*/
	
	
	public HashMap<Consts.PointComponent,Double> getSummedPointsEntryForDayAsMap(Date day){
		day.setHours(0);
		day.setMinutes(0);
		day.setSeconds(0);
		
		return getSummedPointsEntryAsMap(day);
	}
	
	
	public HashMap<Consts.PointComponent,Double> getSummedPointsEntryAsMap(Date day){

		
		String start_date = DB_DATE_STORE_FORMAT.format(day);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(day);
		cal.add(Calendar.DATE, 1);
		String end_date = DB_DATE_STORE_FORMAT.format(cal.getTime());
		
		String select_str = "SUM(" + PointsDiaryTableHelper.COL_ALCOHOL_VAL + "), " +
				"SUM(" + PointsDiaryTableHelper.COL_DAIRY_VAL + "), " +
				"SUM(" + PointsDiaryTableHelper.COL_FATS_VAL + "), " +
				"SUM(" + PointsDiaryTableHelper.COL_FRUIT_VAL + "), " +
				"SUM(" + PointsDiaryTableHelper.COL_FRUIT_WHOLE_VAL+ "), " +
				"SUM(" + PointsDiaryTableHelper.COL_GRAINS_VAL + "), " +
				"SUM(" + PointsDiaryTableHelper.COL_GRAINS_WHOLE_VAL + "), " +
				"SUM(" + PointsDiaryTableHelper.COL_PROTEIN_VAL + "), " +
				"SUM(" + PointsDiaryTableHelper.COL_SODIUM_VAL + "), " +
				"SUM(" + PointsDiaryTableHelper.COL_SUGAR_VAL + "), " +
				"SUM(" + PointsDiaryTableHelper.COL_OILS_VAL+ "), " +
				"SUM(" + PointsDiaryTableHelper.COL_SOLID_FATS_VAL+ "), " +
				"SUM(" + PointsDiaryTableHelper.COL_VEGGIE_VAL + "), " +
				"SUM(" + PointsDiaryTableHelper.COL_VEGGIE_WHOLE_VAL + ") " ;
		
		String query = "select "+select_str+" from "+ PointsDiaryTableHelper.TABLE_NAME +" WHERE isValid=1 AND "+ PointsDiaryTableHelper.COL_TIME_ENTERED +" > ? AND "+ PointsDiaryTableHelper.COL_TIME_ENTERED+" <  ?;";
		Cursor cursor = mDb.rawQuery(query, new String[]{start_date, end_date});
		
		HashMap<Consts.PointComponent,Double> entry = new HashMap<Consts.PointComponent, Double>();
		
		if (cursor.moveToFirst()){
			do{
				for (int i=0; i<cursor.getColumnCount(); i++){
					String raw_col_name = cursor.getColumnName(i); 
					String col_name = raw_col_name.substring(4, raw_col_name.length()-1);
					entry.put(PointComponent.getFromPtsColName(col_name),
							cursor.getDouble(i));
				}
			}while (cursor.moveToNext());
		}else{
			entry = new HashMap<Consts.PointComponent,Double>();
		}
		
		cursor.close(); 
		
		return entry;
	}
	
	
public HashMap<Consts.PointComponent,Double> getSummedPointsEntryAsMapForTimeFrame(Date start, Date end){

		
		String start_date = DB_DATE_STORE_FORMAT.format(start);
		
		String end_date = DB_DATE_STORE_FORMAT.format(end);
		
		String select_str = "SUM(" + PointsDiaryTableHelper.COL_DAIRY_VAL + ")," +
				"SUM(" + PointsDiaryTableHelper.COL_FRUIT_VAL + "), " +
				"SUM(" + PointsDiaryTableHelper.COL_FRUIT_WHOLE_VAL+ "), " +
				"SUM(" + PointsDiaryTableHelper.COL_GRAINS_VAL + "), " +
				"SUM(" + PointsDiaryTableHelper.COL_GRAINS_WHOLE_VAL + "), " +
				"SUM(" + PointsDiaryTableHelper.COL_PROTEIN_VAL + "), " +
				"SUM(" + PointsDiaryTableHelper.COL_SODIUM_VAL + "), " +
				"SUM(" + PointsDiaryTableHelper.COL_SUGAR_VAL + "), " +
				"SUM(" + PointsDiaryTableHelper.COL_OILS_VAL+ "), " +
				"SUM(" + PointsDiaryTableHelper.COL_SOLID_FATS_VAL+ "), " +
				"SUM(" + PointsDiaryTableHelper.COL_VEGGIE_VAL + "), " +
				"SUM(" + PointsDiaryTableHelper.COL_VEGGIE_WHOLE_VAL + ") " ;
		
		String query = "select "+select_str+" from "+ PointsDiaryTableHelper.TABLE_NAME +" WHERE isValid=1 AND "+ PointsDiaryTableHelper.COL_TIME_ENTERED +" > ? AND "+ PointsDiaryTableHelper.COL_TIME_ENTERED+" <  ?;";
		Cursor cursor = mDb.rawQuery(query, new String[]{start_date, end_date});
		
		HashMap<Consts.PointComponent,Double> entry = new HashMap<Consts.PointComponent, Double>();
		
		if (cursor.moveToFirst()){
			do{
				for (int i=0; i<cursor.getColumnCount(); i++){
					String raw_col_name = cursor.getColumnName(i); 
					String col_name = raw_col_name.substring(4, raw_col_name.length()-1);
					entry.put(PointComponent.getFromPtsColName(col_name),
							cursor.getDouble(i));
				}
			}while (cursor.moveToNext());
		}else{
			entry = new HashMap<Consts.PointComponent,Double>();
		}
		
		cursor.close(); 
		
		return entry;
	}
	
	
	private Cursor getAllEntriesForDay(Calendar origDay){
		Calendar day = Calendar.getInstance(); 
		day.setTime(origDay.getTime());
		day.set(Calendar.HOUR_OF_DAY, 0);// setHours(0);
		day.set(Calendar.MINUTE, 0);//setMinutes(0);
		day.set(Calendar.SECOND, 0); //setSeconds(0);
		
		String start_date = DB_DATE_STORE_FORMAT.format(day.getTime());
		
		//Calendar cal = Calendar.getInstance();
		//cal.setTime(day);
		day.add(Calendar.DATE, 1);
		/*
		
		day.setHours(0);
		day.setMinutes(0);
		day.setSeconds(0);
		
		String start_date = DB_DATE_STORE_FORMAT.format(day);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(day);
		cal.add(Calendar.DATE, 1);
		*/
		String end_date = DB_DATE_STORE_FORMAT.format(day.getTime());
		
		
		String query = "select * " +
			/*	PointsDiaryTableHelper.COL_COMMENT + ", " + 
				PointsDiaryTableHelper.COL_SOURCE + ", " + 
				PointsDiaryTableHelper.COL_LOCATION_ID + ", " + 
				PointsDiaryTableHelper.TABLE_NAME + "." + PointsDiaryTableHelper.COL_ROWID + ", " +
				PointsDiaryTableHelper.COL_TIME_ENTERED + ", " + 
				LocationDiaryTableHelper.TABLE_NAME + "." + LocationDiaryTableHelper.COL_LOC_NAME +
			*/	" FROM "+ PointsDiaryTableHelper.TABLE_NAME + " LEFT OUTER JOIN " +
				LocationDiaryTableHelper.TABLE_NAME + " ON (" +
				PointsDiaryTableHelper.COL_LOCATION_ID + " = " + LocationDiaryTableHelper.TABLE_NAME + "."+ LocationDiaryTableHelper.COL_ROWID + 
				") WHERE tblPtsDiary.isValid=1 AND "+ PointsDiaryTableHelper.COL_TIME_ENTERED +" > ? AND "+ PointsDiaryTableHelper.COL_TIME_ENTERED+" <  ? " +
						" ORDER BY "+ PointsDiaryTableHelper.COL_TIME_ENTERED +" DESC ;";
		Cursor cursor = mDb.rawQuery(query, new String[]{start_date, end_date});
		
		return cursor;
	}
	
public ContentValues getPointsEntryAsVals(long id){
		
	String query = "select * " + 
			" FROM "+ PointsDiaryTableHelper.TABLE_NAME +  
			" WHERE "+ PointsDiaryTableHelper.COL_ROWID +" = ? ;";
	Cursor c = mDb.rawQuery(query, new String[]{Long.toString(id)});
	c.moveToFirst(); 
	ContentValues vals = new ContentValues();
	for(int x = 0; x < c.getColumnCount(); x++){
		String val = "0.0";
		if (!c.isNull(x)){
			val = c.getString(x);
		}
		vals.put(c.getColumnName(x), val);
	}
	c.close();
	return vals;
	}

public ContentValues getCustomFoodPointsEntryAsVals(long id){
	
	String query = "select * " + 
			" FROM "+ CustomFoodPointsDiaryTableHelper.TABLE_NAME +  
			" WHERE "+ CustomFoodPointsDiaryTableHelper.COL_ROWID +" = ? ;";
	Cursor c = mDb.rawQuery(query, new String[]{Long.toString(id)});
	c.moveToFirst(); 
	ContentValues vals = new ContentValues();
	for(int x = 0; x < c.getColumnCount(); x++){
		String val = "0"; 
		if (!c.isNull(x)){
			val = c.getString(x);
		}
		vals.put(c.getColumnName(x), val);
	}
	c.close();
	return vals;
	}
	
	public int getCountForDay(Calendar origDay, PointComponent whichComp){
		if (whichComp == PointComponent.ALL){
			return 0;
		}
		
		Calendar day = Calendar.getInstance(); 
		day.setTime(origDay.getTime());
		day.set(Calendar.HOUR_OF_DAY, 0);// setHours(0);
		day.set(Calendar.MINUTE, 0);//setMinutes(0);
		day.set(Calendar.SECOND, 0); //setSeconds(0);
		
		String start_date = DB_DATE_STORE_FORMAT.format(day.getTime());
		
		//Calendar cal = Calendar.getInstance();
		//cal.setTime(day);
		day.add(Calendar.DATE, 1);
		
		String end_date = DB_DATE_STORE_FORMAT.format(day.getTime());
		
		
		String query = "select SUM(" +
				whichComp.getPtDbColName() + ")  " + 
				" FROM "+ PointsDiaryTableHelper.TABLE_NAME +  
				" WHERE isValid=1 AND "+ PointsDiaryTableHelper.COL_TIME_ENTERED +" > ? AND "+ PointsDiaryTableHelper.COL_TIME_ENTERED+" <  ?;";
		Cursor cursor = mDb.rawQuery(query, new String[]{start_date, end_date});
		
		int amt = 0; 
		
		if (cursor.moveToFirst())
			amt = cursor.getInt(0);
		
		cursor.close();
		return amt; 
	}
	
	public Cursor getAllEntriesForDay(Calendar origDay, PointComponent whichComp){
		
		if (whichComp == null || whichComp == PointComponent.ALL){
			return getAllEntriesForDay(origDay);
		}

		Calendar day = Calendar.getInstance(); 
		day.setTime(origDay.getTime());
		
		day.set(Calendar.HOUR_OF_DAY, 0);// setHours(0);
		day.set(Calendar.MINUTE, 0);//setMinutes(0);
		day.set(Calendar.SECOND, 0); //setSeconds(0);
		
		String start_date = DB_DATE_STORE_FORMAT.format(day.getTime());
		
		//Calendar cal = Calendar.getInstance();
		//cal.setTime(day);
		day.add(Calendar.DATE, 1);
		
		String end_date = DB_DATE_STORE_FORMAT.format(day.getTime());
		
		/*String query = "select  " +
				PointsDiaryTableHelper.COL_COMMENT + ", " + 
				PointsDiaryTableHelper.COL_SOURCE + ", " + 
				PointsDiaryTableHelper.COL_LOCATION_ID + ", " + 
				PointsDiaryTableHelper.TABLE_NAME + "." + PointsDiaryTableHelper.COL_ROWID + ", " +
				PointsDiaryTableHelper.COL_TIME_ENTERED + ", " + 
				LocationDiaryTableHelper.TABLE_NAME + "." + LocationDiaryTableHelper.COL_LOC_NAME +
				" FROM "+ PointsDiaryTableHelper.TABLE_NAME + " LEFT OUTER JOIN " +
				LocationDiaryTableHelper.TABLE_NAME + " ON (" +
				PointsDiaryTableHelper.COL_LOCATION_ID + " = " + LocationDiaryTableHelper.TABLE_NAME + "."+ LocationDiaryTableHelper.COL_ROWID + 
				") WHERE "+ whichComp.getPtDbColName() + " > 0 AND " + PointsDiaryTableHelper.COL_TIME_ENTERED +" > ? AND "+ PointsDiaryTableHelper.COL_TIME_ENTERED+" <  ?;"; */
		
		String query = "select * " +
				" FROM "+ PointsDiaryTableHelper.TABLE_NAME + " LEFT OUTER JOIN " +
				LocationDiaryTableHelper.TABLE_NAME + " ON (" +
				PointsDiaryTableHelper.COL_LOCATION_ID + " = " + LocationDiaryTableHelper.TABLE_NAME + "."+ LocationDiaryTableHelper.COL_ROWID + 
				") WHERE tblPtsDiary.isValid=1 AND "+ whichComp.getPtDbColName() + " > 0 AND " + PointsDiaryTableHelper.COL_TIME_ENTERED +" > ? AND "+ PointsDiaryTableHelper.COL_TIME_ENTERED+" <  ? " +
						" ORDER BY "+PointsDiaryTableHelper.COL_TIME_ENTERED +" DESC ;"; 
				
		Cursor cursor = mDb.rawQuery(query, new String[]{start_date, end_date});
		
		return cursor;
	}
	
	/**
	 * Returns the entries for the entire week that contains the specified day, but just the given component. 
	 * @param day
	 * @param whichComp
	 * @return
	 */
	public Cursor getAllEntriesForWeek(Calendar origDay, PointComponent whichComp){
		Calendar day = Calendar.getInstance(); 
		day.setTime(origDay.getTime());
		while (day.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
			day.add(Calendar.DATE, -1);
		}
		
		
		
		String monday = DB_DATE_STORE_FORMAT.format(day.getTime());
		day.add(Calendar.DATE, 7);
		String sunday = DB_DATE_STORE_FORMAT.format(day.getTime());
		
		String query = "select *  " +
				/*PointsDiaryTableHelper.COL_COMMENT + ", " + 
				PointsDiaryTableHelper.COL_SOURCE + ", " + 
				PointsDiaryTableHelper.COL_LOCATION_ID + ", " + 
				PointsDiaryTableHelper.TABLE_NAME + "." + PointsDiaryTableHelper.COL_ROWID + ", " +
				PointsDiaryTableHelper.COL_TIME_ENTERED + ", " + 
				LocationDiaryTableHelper.TABLE_NAME + "." + LocationDiaryTableHelper.COL_LOC_NAME +
				*/" FROM "+ PointsDiaryTableHelper.TABLE_NAME + " LEFT OUTER JOIN " +
				LocationDiaryTableHelper.TABLE_NAME + " ON (" +
				PointsDiaryTableHelper.COL_LOCATION_ID + " = " + LocationDiaryTableHelper.TABLE_NAME + "."+ LocationDiaryTableHelper.COL_ROWID + 
				") WHERE tblPtsDiary.isValid=1 AND "+ whichComp.getPtDbColName() + " > 0 AND " + PointsDiaryTableHelper.COL_TIME_ENTERED +" > ? AND "+ PointsDiaryTableHelper.COL_TIME_ENTERED+" <  ?;";
		Cursor cursor = mDb.rawQuery(query, new String[]{monday, sunday});
		
		return cursor;
	}
	
	/**
	 * Returns the entries for the entire week that contains the specified day, but just the given component. 
	 * @param day
	 * @param whichComp
	 * @return
	 */
	public Cursor getFoodEntriesForTimePeriod(Calendar startDay, Calendar stopDay){
		Calendar day = Calendar.getInstance(); 
		day.setTime(startDay.getTime());

		
		
		String first_day = DB_DATE_STORE_FORMAT.format(startDay.getTime());
		
		String last_day = DB_DATE_STORE_FORMAT.format(stopDay.getTime());
		
		String query = "select *  " +
				" FROM "+ FoodDiaryTableHelper.TABLE_NAME + 
				" WHERE isValid=1 AND " + PointsDiaryTableHelper.COL_TIME_ENTERED +" > ? AND "+ PointsDiaryTableHelper.COL_TIME_ENTERED+" <  ?;";
		Cursor cursor = mDb.rawQuery(query, new String[]{first_day, last_day});
		
		return cursor;
	}
	
	public void deleteDiaryEntry(long ptsDiaryId){
		// Look to see if 
		String query = "UPDATE tblPtsDiary SET isValid=0 WHERE _id=" + ptsDiaryId;
		Cursor c = mDb.rawQuery(query, null);
		c.moveToFirst();
		c.close(); 
		
		String foodTblQuery = "UPDATE tblFoodDiary SET isValid=0 WHERE pointsEntryId=" + ptsDiaryId;
		c = mDb.rawQuery(foodTblQuery, null);
		c.moveToFirst(); 
		c.close(); 
	}
	
	
	public Cursor getRecentFoodDates(){
		String query = "SELECT SUBSTR(timestamp,1,10) AS timestamp, _id FROM tblMyFoods GROUP BY timestamp ORDER BY timestamp DESC";
		return mDb.rawQuery(query, new String[]{});
	}
	
	public Cursor getRecentFoodsFromDay(String timestamp){
		String query = "SELECT * FROM tblMyFoods WHERE timestamp LIKE '"+timestamp+"%' ORDER BY timestamp DESC";
		return mDb.rawQuery(query, new String[]{});//timestamp + "%"
	}
	
	public Cursor getMyFoods(){
		String query = "SELECT * FROM tblMyFoods ORDER BY foodName";
		return mDb.rawQuery(query, new String[]{});
	}
	
	public Cursor getMyFoodsThatStartWith(String beg){
		String query = "SELECT * FROM tblMyFoods WHERE foodName LIKE '"+beg +"%' ORDER BY foodName";
		return mDb.rawQuery(query, new String[]{});
	}
	
	
	public Cursor getMyFoodHeadings(){
		String query = "SELECT SUBSTR(foodName,1,1) AS heading, _id FROM tblMyFoods GROUP BY heading ORDER BY heading ASC";
		return mDb.rawQuery(query, new String[]{});
	}
	
	public Cursor getRecentSearches(){
		String query = "SELECT DISTINCT description,typeId,_id FROM tblActionLog WHERE actionID=6 GROUP BY description ORDER BY _id DESC LIMIT 25"; 
		return mDb.rawQuery(query,  null);
	}
	
	public Cursor getLocationEntries(){
		String query = "select * from " + LocationDiaryTableHelper.TABLE_NAME + " WHERE " + LocationDiaryTableHelper.COL_IS_VALID + " = 1 ";
		return mDb.rawQuery(query, null);
	}
	
	public int deleteLocation(long rowId){
		return mDb.delete(LocationDiaryTableHelper.TABLE_NAME, LocationDiaryTableHelper.COL_ROWID + " = ?", new String[]{Long.toString(rowId)});
	}
	/*
	public int deleteSampleEntries(){
		mDb.delete(FoodDiaryTableHelper.TABLE_NAME, FoodDiaryTableHelper.COL_ROWID + " > 15", new String[]{});
		return mDb.delete(PointsDiaryTableHelper.TABLE_NAME, PointsDiaryTableHelper.COL_ROWID + " > 105", new String[]{});
	}*/
	
	public long getClosestLocation(Location l){
		// Go through all locations; 
		// For each lcoation: 
		// determine maxLat/maxLng/minLat/minLng
		String query = "select * from " + LocationDiaryTableHelper.TABLE_NAME + " WHERE " + LocationDiaryTableHelper.COL_IS_VALID + " = 1 ";
		Cursor cursor = mDb.rawQuery(query, null);
		long rowId = -1; 
		String name = "Other"; 
		if (cursor.moveToFirst()){
			do{
				double lat = cursor.getDouble(cursor.getColumnIndex(LocationDiaryTableHelper.COL_LAT));
				double lng = cursor.getDouble(cursor.getColumnIndex(LocationDiaryTableHelper.COL_LNG));
				double range = cursor.getDouble(cursor.getColumnIndex(LocationDiaryTableHelper.COL_SIZE));
				Location saved_loc = new Location(LocationManager.PASSIVE_PROVIDER);
				saved_loc.setLatitude(lat);
				saved_loc.setLongitude(lng);
				if (l.distanceTo(saved_loc) < range){
					rowId = cursor.getLong(cursor.getColumnIndex(LocationDiaryTableHelper.COL_ROWID));
					name = cursor.getString(cursor.getColumnIndex(LocationDiaryTableHelper.COL_LOC_NAME));
					break; 
				}
					
			}while (cursor.moveToNext());
		}
		
		
		
		cursor.close(); 
		return rowId;
	}
	
	public String getLocationName(long locId){
		String query = "select " + LocationDiaryTableHelper.COL_LOC_NAME + " FROM " + LocationDiaryTableHelper.TABLE_NAME + " WHERE " +
				LocationDiaryTableHelper.COL_ROWID + " = ? "; 
		
		Cursor cursor = mDb.rawQuery(query, new String[]{Long.toString(locId)});

		String name = "None"; 
		if (cursor.moveToFirst()){
			do{
				name = cursor.getString(cursor.getColumnIndex(LocationDiaryTableHelper.COL_LOC_NAME));
				break; 
				
			}while (cursor.moveToNext());
		}
		
		cursor.close(); 
		return name;
	}
	
	public long addLocationEntry(Location loc, String name){
		return addLocationEntry(loc.getLatitude(), loc.getLongitude(), loc.getAccuracy(), name);
	}
	
	public long addLocationEntry(double lat, double lng, double acc, String name){
		String query = "select * from " + LocationDiaryTableHelper.TABLE_NAME + " WHERE " + LocationDiaryTableHelper.COL_IS_VALID + " = 1 AND " + LocationDiaryTableHelper.COL_LOC_NAME + " = ?" ;
		Cursor cursor = mDb.rawQuery(query, new String[]{name});
		long rowId = -1; 
		if (cursor.moveToFirst()){
			return -1;
		}
			
		ContentValues vals = new ContentValues();
		vals.put(LocationDiaryTableHelper.COL_IS_VALID, 1);
		vals.put(LocationDiaryTableHelper.COL_LAT, lat);
		vals.put(LocationDiaryTableHelper.COL_LNG, lng); 
		vals.put(LocationDiaryTableHelper.COL_LOC_NAME, name); 
		vals.put(LocationDiaryTableHelper.COL_SIZE, acc);
		vals.put(LocationDiaryTableHelper.COL_TIMESTAMP, DB_DATE_STORE_FORMAT.format(new Date()));
				
		
		return mDb.insert(LocationDiaryTableHelper.TABLE_NAME, null, vals);
	}
	/*
	private static void updateEntryWithPointsRow(DiaryEntry record, Cursor curRow){
		record.setAlcoholVal(record.getAlcoholVal() + curRow.getDouble(curRow.getColumnIndex(PointsDiaryTableHelper.COL_ALCOHOL_VAL)));
		record.setDairyVal(record.getDairyVal() + curRow.getDouble(curRow.getColumnIndex(PointsDiaryTableHelper.COL_DAIRY_VAL)));
		record.setFatsVal(record.getFatsVal() + curRow.getDouble(curRow.getColumnIndex(PointsDiaryTableHelper.COL_FATS_VAL)));
		record.setFruitVal(record.getFruitVal() + curRow.getDouble(curRow.getColumnIndex(PointsDiaryTableHelper.COL_FRUIT_VAL)));
		record.setFruitWholeVal(record.getFruitWholeVal() + curRow.getDouble(curRow.getColumnIndex(PointsDiaryTableHelper.COL_FRUIT_WHOLE_VAL)));
		record.setGrainsVal(record.getGrainsVal() + curRow.getDouble(curRow.getColumnIndex(PointsDiaryTableHelper.COL_GRAINS_VAL)));
		record.setGrainsWholeVal(record.getGrainsWholeVal() + curRow.getDouble(curRow.getColumnIndex(PointsDiaryTableHelper.COL_GRAINS_WHOLE_VAL)));
		record.setProteinVal(record.getProteinVal() + curRow.getDouble(curRow.getColumnIndex(PointsDiaryTableHelper.COL_PROTEIN_VAL)));
		record.setSodiumVal(record.getSodiumVal() + curRow.getDouble(curRow.getColumnIndex(PointsDiaryTableHelper.COL_SODIUM_VAL)));
		record.setSugarVal(record.getSugarVal() + curRow.getDouble(curRow.getColumnIndex(PointsDiaryTableHelper.COL_SUGAR_VAL)));
		record.setVeggieVal(record.getVeggieVal() + curRow.getDouble(curRow.getColumnIndex(PointsDiaryTableHelper.COL_VEGGIE_VAL)));
		record.setVeggieWholeVal(record.getVeggieWholeVal() + curRow.getDouble(curRow.getColumnIndex(PointsDiaryTableHelper.COL_VEGGIE_WHOLE_VAL)));
	}*/
	/*
	public GoalEntry getMostRecentGoal(){
		String query = "select * from "+ PointsDiaryTableHelper.TABLE_NAME + " LIMIT 1 ORDER BY _id";
		Cursor cursor = mDb.rawQuery(query, new String[]{});
		
		GoalEntry entry;
		
		if (cursor.moveToFirst()){
			do{
				entry = GoalDiaryTableHelper.getGoalEntryFromCursor(cursor);
			}while (cursor.moveToNext());
		}else{
			entry = new GoalEntry();
		}
		
		cursor.close(); 
		
		return entry;
	}*/
	
	/***
	 * Gets a hashmap with all of the goal components. 
	 * @return
	 */
	public HashMap<Consts.PointComponent,Integer> getAllMostRecentGoalAsMap(){
		String query = "select " +
				GoalDiaryTableHelper.COL_VEGGIE_WHOLE_GOAL + ", "  +
				GoalDiaryTableHelper.COL_VEGGIE_GOAL + ", "  +
				GoalDiaryTableHelper.COL_GRAINS_WHOLE_GOAL + ", "  +
				GoalDiaryTableHelper.COL_GRAINS_GOAL + ", "  +
				GoalDiaryTableHelper.COL_FRUIT_WHOLE_GOAL + ", "  +
				GoalDiaryTableHelper.COL_FRUIT_GOAL+ ", "  +
				GoalDiaryTableHelper.COL_DAIRY_GOAL+ ", "  +
				GoalDiaryTableHelper.COL_PROTEIN_GOAL+ ", "  +
				GoalDiaryTableHelper.COL_OILS_GOAL + ", "  +
				GoalDiaryTableHelper.COL_SUGAR_GOAL + ", "  +
				GoalDiaryTableHelper.COL_SODIUM_GOAL + ", "  +
				GoalDiaryTableHelper.COL_SOLID_FATS_GOAL+ 
				" from "+ GoalDiaryTableHelper.TABLE_NAME + " ORDER BY _id DESC LIMIT 1 ";
		Cursor cursor = mDb.rawQuery(query, new String[]{});
		
		HashMap<Consts.PointComponent,Integer> entry;
		
		if (cursor.moveToFirst()){
			do{
				entry = GoalDiaryTableHelper.getGoalMapFromCursor(cursor, false);
			}while (cursor.moveToNext());
		}else{
			entry = new HashMap<Consts.PointComponent,Integer>();
		}
		
		cursor.close(); 
		
		return entry;
	}
	
	public ContentValues getAllMostRecentGoalAsContentVals(){
		String query = "select * " +
				" from "+ GoalDiaryTableHelper.TABLE_NAME + " ORDER BY _id DESC LIMIT 1 ";
		Cursor cursor = mDb.rawQuery(query, new String[]{});
		cursor.moveToFirst(); 
		
		ContentValues vals = new ContentValues(); 
		DatabaseUtils.cursorRowToContentValues(cursor, vals);
		vals.remove(GoalDiaryTableHelper.COL_SAT_FATS_GOAL);
		vals.remove(GoalDiaryTableHelper.COL_SAT_FATS_ISVALID);
		
		cursor.close(); 
		
		return vals;
	}
	
	/**
	 * Gets a hashmap with only the active components. 
	 * @return
	 */
	public HashMap<Consts.PointComponent,Integer> getMostRecentGoalAsMap(){
		String query = "select * from "+ GoalDiaryTableHelper.TABLE_NAME + " ORDER BY _id DESC LIMIT 1 ";
		Cursor cursor = mDb.rawQuery(query, new String[]{});
		
		HashMap<Consts.PointComponent,Integer> entry;
		
		if (cursor.moveToFirst()){
			do{
				entry = GoalDiaryTableHelper.getValidGoalMap(cursor);//getGoalMapFromCursor(cursor,true);
			}while (cursor.moveToNext());
		}else{
			entry = new HashMap<Consts.PointComponent,Integer>();
		}
		
		cursor.close(); 
		
		return entry;
	}
	
	public int getGoalForDay(PointComponent whichComponent, Calendar day){
		
		
		
		String query = "select " + whichComponent.getGoalDbColName() +
				" from "+ GoalDiaryTableHelper.TABLE_NAME + 
				" WHERE " + GoalDiaryTableHelper.COL_TIMESTAMP + " < ? " + 
				" ORDER BY " + GoalDiaryTableHelper.COL_TIMESTAMP +
				" DESC LIMIT 1 ";
		Cursor cursor = mDb.rawQuery(query, new String[]{DB_DATE_STORE_FORMAT.format(day.getTime())});
		
		int amt = 0; 
		
		if (cursor.moveToFirst()){
			amt = cursor.getInt(0);
		}
		cursor.close(); 
		
		return amt;
	}
	
	public long updateGoalForInLabStudy(Map<PointComponent, Integer> newVals){
		
		
		ContentValues vals = GoalDiaryTableHelper.getContentValsFromMap(newVals);
		vals.put(GoalDiaryTableHelper.COL_TIMESTAMP, DB_DATE_STORE_FORMAT.format(new Date()));
		
		long goal_id = mDb.insert(GoalDiaryTableHelper.TABLE_NAME, null, vals);

		
		
		logAction(Action.CHANGE_GOAL, (int)goal_id, -1, "change goal", newVals.toString());
		return goal_id; 
	}
	
	public long updateGoal(ContentValues newVals){
		//ContentValues vals = GoalDiaryTableHelper.getContentValsFromMap(newVals);
		newVals.put(GoalDiaryTableHelper.COL_TIMESTAMP, DB_DATE_STORE_FORMAT.format(new Date()));
		newVals.remove(GoalDiaryTableHelper.COL_ROWID);
		
		long goal_id = mDb.insert(GoalDiaryTableHelper.TABLE_NAME, null, newVals);

		logAction(Action.CHANGE_GOAL, (int)goal_id, -1, "change goal", newVals.toString());
		return goal_id; 
	}
	
		
	
	public long logAction(Action action, int refId, int typeId, String comment, String desc){
		ContentValues vals = new ContentValues(); 
		vals.put(ActionLogDbHelper.COL_ACTION_ID, action.ordinal());
		vals.put(ActionLogDbHelper.COL_REF_ID, refId);
		vals.put(ActionLogDbHelper.COL_TYPE_ID, typeId);
		vals.put(ActionLogDbHelper.COL_COMMENT, comment);
		vals.put(ActionLogDbHelper.COL_DESC, desc);
		
		
		vals.put(GoalDiaryTableHelper.COL_TIMESTAMP, DB_DATE_STORE_FORMAT.format(new Date()));
	
		return mDb.insert(ActionLogDbHelper.TABLE_NAME, null, vals);
		
	}
	
}