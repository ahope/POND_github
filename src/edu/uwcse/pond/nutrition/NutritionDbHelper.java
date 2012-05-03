package edu.uwcse.pond.nutrition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uwcse.pond.diary.DiaryDbHelper;
import edu.uwcse.pond.diary.LocationDiaryTableHelper;
import edu.uwcse.pond.diary.PointsDiaryTableHelper;
import edu.uwcse.pond.nutrition.Consts.FoodClass;
import edu.uwcse.pond.nutrition.Consts.Nutrient;
import edu.uwcse.pond.nutrition.Consts.PyramidCategory;
import edu.uwcse.pond.nutrition.Consts.ServingType;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

public class NutritionDbHelper  {
	
	private static class AssetDbHelper extends SQLiteOpenHelper{

		public AssetDbHelper(Context context) {
			//super(context, name, factory, version);
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//db.execSQL("CREATE TABLE IF NOT EXISTS testTable (_id integer primary key autoincrement, foo text)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
		}

	}

	//The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/edu.uwcse.pond.proto/databases/";
 
    private static String DB_NAME = "nutritionDbFts3.db";//"nutritionDb.db";
 
    private SQLiteDatabase myDataBase; 
 
    private final Context myContext;
    
    private static final int DB_VERSION = 2; 
    
    private AssetDbHelper myDbHelper; 

    

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public NutritionDbHelper(Context context) {
    	this.myContext = context;
    	//aha:23jan12myDbHelper = new AssetDbHelper(myContext); 
    }	
	
	public NutritionDbHelper open() throws SQLException{
		myDbHelper = new AssetDbHelper(myContext); 
		myDataBase = myDbHelper.getReadableDatabase(); 
		return this; 
	}
	
	public void close(){
		myDataBase.close();
		myDbHelper.close(); 
	}
	
    
    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    		//do nothing - database already exist
    	}else{
 
        	try {
 
    			copyDataBase();
 
    		} catch (IOException e) {
 
    			e.printStackTrace();
    			throw e; 
        		//throw new Error("Error copying database");
 
        	}catch(Exception e){
        		throw new Error("Couldn't create a new file");
        	}
    	}
 
    }
 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
    	String myPath = DB_PATH + DB_NAME; ///data/data/edu.uwcse.pond.proto/databases/nutritionDbFts3.db
    	File dbFile = new File("/data/data/edu.uwcse.pond.proto/databases/nutritionDbFts3.db");
		
    	
    	
		boolean doesItExist = dbFile.exists();
    	
		SQLiteDatabase checkDB = null;
		 
    	try{
    		//String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
 
    		//database does't exist yet.
 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
		
		/*
    	boolean containsTable = false; 
    	
    	SQLiteDatabase checkDB = null;
 
    	try{
    		checkDB = myDbHelper.getReadableDatabase();
    		String query = "SELECT name FROM sqlite_master WHERE type='table' AND name='tblFood'";
    		Cursor c = checkDB.rawQuery(query, null);
    		c.moveToFirst(); 
    		if (c.getCount() == 1){
    			containsTable = true; 
    		}
    		c.close(); 
    		
    		//checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    		
    	}catch(SQLiteException e){
 
    		//database does't exist yet.
    		throw e; 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return containsTable;//checkDB != null ? true : false;
*/    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * @throws Exception 
     * */
    private void copyDataBase() throws Exception{
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
    	
    	/*
    	//Open your assets db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	SQLiteDatabase checkDB = null;
    	 
    	try{
    		
    		checkDB = SQLiteDatabase.openDatabase(outFileName, null, SQLiteDatabase.CREATE_IF_NECESSARY);
    		
    	}catch(SQLiteException e){
 
    		//database does't exist yet.
    		throw e; 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
    	
    	//Open the empty db as the output stream
    	File newDbFile = new File(outFileName); 
    	boolean newDbFileCreated =  newDbFile.createNewFile();
    	if (!newDbFileCreated){
    		throw new Exception("Couldn't create new file");
    	}
    	
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();*/
 
    }
 
    /**
     * Opens a read-only database. 
     * @throws SQLException
     */
 /*   public void openDataBase() throws SQLException{
 
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    }*/
 
    

    public void checkFTS(){
    	String query = "SELECT * from tblFullText WHERE foodNameText MATCH 'cheese'";
    	
    	Cursor c = myDataBase.rawQuery(query, new String[]{});
    	System.out.println(c.getCount() + " entries."); 
    	
    	while(!c.isAfterLast()){
    		System.out.println(c.getString(c.getColumnIndex("foodName"))); 
    		c.move(1);
    	}
    	
    }
 


    public Cursor getManufacturersForFoodWithName(String word){
    	String query = "SELECT DISTINCT(manufacturerName) FROM tblFood LEFT OUTER JOIN tblManufacturer ON tblFood.manufacturerId = tblManufacturer.manufacturerID WHERE foodName LIKE '?'";
    	return myDataBase.rawQuery(query, new String[]{"%" + word + "%"});
    }
    
    public Cursor getManufacturersForFoodWithName(String word, int manId){
    	String query = "SELECT * FROM tblFood WHERE foodName LIKE '?' AND (manufacturerId='?')";
    	return myDataBase.rawQuery(query, new String[]{"%" + word + "%", Integer.toString(manId)});
    }
	
	// Add your public helper methods to access and get content from the database.
	   // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
	   // to you to create adapters for your views.
	public Cursor getFoodsWithName(String word){
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables("tblFood LEFT OUTER JOIN tblManufacturer ON tblFood.manufacturerId = tblManufacturer.manufacturerID ");
		String[] selectColumns = {"tblFood._id", 
			"tblFood.foodName", 
			"tblManufacturer.manufacturerName", 
			"tblManufacturer.manufacturerID", 
			"tblFood.isCommon"};
		
		String whereColumns = "tblFood.foodName";
		
		String[] whereVals = {"like %cheese%"};
		String orderBy = "tblFood.isCommon DESC";
		
	    
	       
	      Cursor c = myDataBase.rawQuery("SELECT tblFood._id, tblFood.foodName, tblManufacturer.manufacturerName, tblManufacturer.manufacturerID, tblFood.isCommon FROM tblFood LEFT OUTER JOIN tblManufacturer ON tblFood.manufacturerId = tblManufacturer.manufacturerID " +
	      		"WHERE tblFood.foodName LIKE ? ORDER BY tblFood.isCommon DESC,tblManufacturer.manufacturerName,tblFood.foodName ASC", 
	    		   new String[] {"%" + word + "%"});
	    return c;
	}
	
	public Cursor getGenericFoodsWithName(String word){
		Cursor c = myDataBase.rawQuery("SELECT tblFood._id, tblFood.foodName FROM tblFood  " +
	      		"WHERE tblFood.foodName LIKE ? AND manufacturerId=''  ORDER BY tblFood.foodName ASC ", 
	    		   new String[] {"%" + word + "%"});
	    return c;
	}
	
	public int getNumGenericFoodsWithNameFTS(String word){
		String[] words = word.split(" "); 
		
		StringBuffer buffer = new StringBuffer(words[0].trim()); 
		for (int i=1; i<words.length; i++){
			buffer.append(" NEAR "); 
			buffer.append( words[i].trim());
		}
		
		String query = "SELECT count(tblFullText._id) AS count FROM tblFullText  " +
	      		"WHERE tblFullText.foodNameText MATCH '"+buffer.toString()+"' AND isGeneric=1 ";
		
		Cursor c = myDataBase.rawQuery(query, 
	    		   new String[] {  });
		c.moveToFirst(); 
	    return c.getInt(0);
	}
	
	public int getNumBrandFoodsWithNameFTS(String word){
		String[] words = word.split(" "); 
		
		StringBuffer buffer = new StringBuffer(words[0].trim()); 
		for (int i=1; i<words.length; i++){
			buffer.append(" NEAR "); 
			buffer.append( words[i].trim());
		}
		
		String query = "SELECT count(tblFullText._id) AS count FROM tblFullText  " +
	      		"WHERE tblFullText.foodNameText MATCH '"+buffer.toString()+"' AND isGeneric=0 ";
		
		Cursor c = myDataBase.rawQuery(query, 
	    		   new String[] {  });
		c.moveToFirst(); 
	    return c.getInt(0);
	}
	
	public Cursor getGenericCommonFoodsWithNameFTS(String word){
		String[] words = word.split(" "); 
		
		StringBuffer buffer = new StringBuffer(words[0].trim()); 
		for (int i=1; i<words.length; i++){
			buffer.append(" NEAR "); 
			buffer.append( words[i].trim());
		}
		
		String query = "SELECT tblFullText._id, tblFullText.foodNameText FROM tblFullText  " +
	      		"WHERE tblFullText.foodNameText MATCH '"+buffer.toString()+"' AND isGeneric=1 AND isCommon=1 ORDER BY foodClassId, tblFullText.foodNameText ASC ";
		
		Cursor c = myDataBase.rawQuery(query, 
	    		   new String[] {  });
	    return c;
	}
	
	public Cursor getFoodClassesForGenericFoodsWithNameFTS(String word){
		String[] words = word.split(" "); 
		
		StringBuffer buffer = new StringBuffer(words[0].trim()); 
		for (int i=1; i<words.length; i++){
			buffer.append(" NEAR "); 
			buffer.append( words[i].trim());
		}
		
		String query = "SELECT tblFoodClass.foodClassName, tblFoodClass.foodClassFullName, tblFoodClass._id, COUNT(tblFoodClass._id) FROM tblFoodClass JOIN tblFullText ON tblFullText.foodClassId = tblFoodClass._id " +
	      		"WHERE tblFullText.foodNameText MATCH '"+buffer.toString()+"' AND isGeneric=1 AND isCommon=0 GROUP BY foodClassId  ORDER BY COUNT(tblFoodClass._id) DESC";
		
		Cursor c = myDataBase.rawQuery(query, 
	    		   new String[] {  });
	    return c;
	}
	
	public Cursor getFoodClassesForGenericAndCommonFoodsWithNameFTS(String word){
		String[] words = word.split(" "); 
		
		StringBuffer buffer = new StringBuffer(words[0].trim()); 
		for (int i=1; i<words.length; i++){
			buffer.append(" NEAR "); 
			buffer.append( words[i].trim());
		}
		
		String query = "SELECT tblFoodClass.foodClassName, tblFoodClass.foodClassFullName, tblFoodClass._id, COUNT(tblFoodClass._id) FROM tblFoodClass JOIN tblFullText ON tblFullText.foodClassId = tblFoodClass._id " +
	      		"WHERE tblFullText.foodNameText MATCH '"+buffer.toString()+"' AND isGeneric=1 GROUP BY foodClassId  ORDER BY COUNT(tblFoodClass._id) DESC";
		
		Cursor c = myDataBase.rawQuery(query, 
	    		   new String[] {  });
	    return c;
	}
	
	public Cursor getGenericFoodsInFoodClassWithNameFTS(String word, int foodClassId){
		String[] words = word.split(" "); 
		
		StringBuffer buffer = new StringBuffer(words[0].trim()); 
		for (int i=1; i<words.length; i++){
			buffer.append(" NEAR "); 
			buffer.append( words[i].trim());
		}
		
		String query = "SELECT tblFullText._id, tblFullText.foodNameText FROM tblFullText  " +
	      		"WHERE tblFullText.foodNameText MATCH '"+buffer.toString()+"' AND isGeneric=1 AND isCommon=0 AND foodClassId="+foodClassId+" ORDER BY tblFullText.foodNameText ASC ";
		
		Cursor c = myDataBase.rawQuery(query, 
	    		   new String[] {  });
	    return c;
	}

	public Cursor getGenericFoodsWithNameFTS(String word){
		String[] words = word.split(" "); 
		
		StringBuffer buffer = new StringBuffer(words[0].trim()); 
		for (int i=1; i<words.length; i++){
			buffer.append(" NEAR "); 
			buffer.append( words[i].trim());
		}
		
		String query = "SELECT tblFullText._id, tblFullText.foodNameText FROM tblFullText  " +
	      		"WHERE tblFullText.foodNameText MATCH '"+buffer.toString()+"' AND manId='' AND isGeneric=1 ORDER BY isCommon DESC, foodClassId, tblFullText.foodNameText ASC ";
		
		Cursor c = myDataBase.rawQuery(query, 
	    		   new String[] {  });
	    return c;
	}
	
	public Cursor getFoodsWithNameAndManId(String word, long manufacturerId){
		/*Cursor c = myDataBase.rawQuery("SELECT tblFood._id, " +
				"tblFood.foodName, tblManufacturer.manufacturerName, " +
				"tblManufacturer.manufacturerID, " +
				"tblFood.isCommon " +
				"FROM tblFood LEFT OUTER JOIN tblManufacturer ON tblFood.manufacturerId = tblManufacturer.manufacturerID " +
				"WHERE tblFood.foodName LIKE ? AND tblManufacturer.manufacturerID = ?", 
	    		   new String[] {"%" + word + "%", Long.toString(manufacturerId)});
		*/
		 String[] words = word.split(" "); 
			
			StringBuffer buffer = new StringBuffer(words[0].trim()); 
			for (int i=1; i<words.length; i++){
				buffer.append(" NEAR "); 
				buffer.append( words[i].trim());
			}
			
			String query = "SELECT DISTINCT tblFullText._id AS _id, tblFullText.foodNameText AS foodName " +
					" FROM tblFullText  " +
		      		"WHERE tblFullText.foodNameText MATCH '"+buffer.toString()+"' AND manId= "+manufacturerId+" ORDER BY tblFullText.foodNameText ASC";
			
			Cursor c = myDataBase.rawQuery(query, 
		    		   new String[] {});
		    return c;
		
	    //return c;
	}
	
	
	public Cursor getManfacturersForFoodWithName(String word){
		/*
	    String query = "SELECT DISTINCT "+
	    		"manufacturerName, " +
	    		"tblManufacturer.manufacturerID AS _id " +
	    		"FROM tblManufacturer JOIN tblFood ON tblManufacturer.manufacturerID = tblFood.manufacturerId " +
	    		"WHERE tblFood.foodName LIKE ? ";
	       
	      Cursor c = myDataBase.rawQuery(query, 
	    		   new String[] {"%" + word + "%"});
	    return c;
	    */
	    String[] words = word.split(" "); 
		
		StringBuffer buffer = new StringBuffer(words[0].trim()); 
		for (int i=1; i<words.length; i++){
			buffer.append(" NEAR "); 
			buffer.append( words[i].trim());
		}
		
		String query = "SELECT DISTINCT manufacturerName, tblManufacturer.manufacturerID AS _id" +
				" FROM tblManufacturer JOIN tblFullText ON tblManufacturer.manufacturerID = tblFullText.manId " +
	      		"WHERE tblFullText.foodNameText MATCH '"+buffer.toString()+"' ORDER BY manufacturerName ASC";
		
		Cursor c = myDataBase.rawQuery(query, 
	    		   new String[] {  });
	    return c;
	}
	
 
        // Add your public helper methods to access and get content from the database.
       // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
       // to you to create adapters for your views.
	public Cursor getFoodsWithName(String word, FoodClass fClass){
		if (fClass == null){
			return getFoodsWithName(word);
		}
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		

	      Cursor c = myDataBase.rawQuery("SELECT tblFood._id, tblFood.foodName, " +
	      		"tblManufacturer.manufacturerName, tblManufacturer.manufacturerID, " +
	      		"tblFood.isCommon FROM tblFood LEFT OUTER JOIN tblManufacturer ON " +
	      		"tblFood.manufacturerId = tblManufacturer.manufacturerID " +
	      		"WHERE tblFood.foodName LIKE ? AND tblFood.foodClassID IN " + fClass.getQueryString(), 
	    		   new String[] {"%" + word + "%"});
	    return c;
	}

	public NutritionEntry getNutritionEntry(int foodId){
		
		// TODO: Fix this such that I don't have to do JOINS. because it's really not necessary (just do mulitple queries from different tables)
		String food_info_query = "SELECT tblFood._id, tblFood.foodName, tblFood.foodClassID, tblFood.cupGramWeight FROM tblFood  WHERE tblFood._id = ? ";
		
		// First, query for Nut items
		String nut_query = "SELECT tblFoodNutrients.nutrientID, tblFoodNutrients.nutrientValue FROM tblFoodNutrients WHERE tblFoodNutrients.foodID = ? AND tblFoodNutrients.nutrientID IN  (0,1,3,4,5,6,7,33,34,74,75,76,77,78,107,109,110,111,112)";
		
		// Pyr query
		String pyr_query = "SELECT * FROM tblFoodPyramidCategories WHERE foodID = ? AND pyramidCategoryId < 70"; 
		
		String srvg_query = "SELECT * FROM tblFoodServingTypes WHERE foodID = ?"; 

		
		NutritionEntry entry; 
		System.out.println("tracepoint 3.1: " + (new Date()).getTime());
		// First, get food info
		Cursor c = myDataBase.rawQuery(food_info_query, new String[]{Integer.toString(foodId)});
		if (c.moveToFirst()){ 
			
			entry = new NutritionEntry(c.getInt(c.getColumnIndex("_id")), 
				c.getString(c.getColumnIndex("foodName")), 
				"placeholder");
			entry.setFoodClass(FoodClass.get(c.getInt(c.getColumnIndex("foodClassID"))));
			entry.setCupGramWeight(c.getDouble(c.getColumnIndex("cupGramWeight"))); 
			// Should only be 1 or 0 entries
		}
		else{
			return null; 
			// NO ENTRIES!! 
		}
		c.close(); 
		
		System.out.println("tracepoint 3.2: " + (new Date()).getTime());
		
		// Then get nutrition info
		c = myDataBase.rawQuery(nut_query, new String[]{Integer.toString(foodId)});
		System.out.println("tracepoint 3.2.1: " + (new Date()).getTime());
		
		// Putting the nutrients into the HashTable is the most time consuming piece
		// here. I'm not sure the best way to deal with that, but something needs to be done. 
		if (c.moveToFirst()){ 
			
			int nutId = c.getInt(c.getColumnIndex("nutrientID"));
			double nutVal = c.getDouble(c.getColumnIndex("nutrientValue"));
			entry.addNutrientVal(Nutrient.get(nutId), nutVal);
			
			while(c.moveToNext()){
				nutId = c.getInt(c.getColumnIndex("nutrientID"));
				nutVal = c.getDouble(c.getColumnIndex("nutrientValue"));
				entry.addNutrientVal(Nutrient.get(nutId), nutVal);
			}
			
		}
		c.close(); 
		
		System.out.println("tracepoint 3.3: " + (new Date()).getTime());
		// Then get pyramid info
		c = myDataBase.rawQuery(pyr_query, new String[]{Integer.toString(foodId)});
		if (c.moveToFirst()){ 
			
			int pyrId = c.getInt(c.getColumnIndex("pyramidCategoryId"));
			double pyrVal = c.getDouble(c.getColumnIndex("pyramidValue"));
			entry.addPyramidVal(PyramidCategory.get(pyrId), pyrVal);
			
			int isHighFat = c.getInt(c.getColumnIndex("isHighFat"));
			int isHighSugar = c.getInt(c.getColumnIndex("isHighSugar"));
			
			if (isHighFat == 1){
				entry.setHighFat(true);
			}
			
			if (isHighSugar == 1){
				entry.setHighSugar(true);
			}
			
			while(c.moveToNext()){
				pyrId = c.getInt(c.getColumnIndex("pyramidCategoryId"));
				pyrVal = c.getDouble(c.getColumnIndex("pyramidValue"));
				entry.addPyramidVal(PyramidCategory.get(pyrId), pyrVal);

				isHighFat = c.getInt(c.getColumnIndex("isHighFat"));
				isHighSugar = c.getInt(c.getColumnIndex("isHighSugar"));
				
				if (isHighFat == 1){
					entry.setHighFat(true);
				}
				
				if (isHighSugar == 1){
					entry.setHighSugar(true);
				}
			}
			
			c.close();
		}
		
		System.out.println("tracepoint 3.4: " + (new Date()).getTime());
		// Get servings info
		c = myDataBase.rawQuery(srvg_query, new String[]{Integer.toString(foodId)});
		if (c.moveToFirst()){ 
			
			do{
				int srvgTypeId = c.getInt(c.getColumnIndex("servingType")); 
				int srvgAmountUnitId = c.getInt(c.getColumnIndex("servingAmountUnitID"));
				double srvgAmountValue = c.getDouble(c.getColumnIndex("servingAmountValue")); // It's an int now, but supposed to be double. 
				double grmWeightValue = c.getDouble(c.getColumnIndex("gramWeightValue")); 
				String srvgAmtNote = c.getString(c.getColumnIndex("servingAmountNote"));
				
				NutritionEntry.Serving srv = entry.new Serving(); 
				srv.setServingType(ServingType.get(srvgTypeId));
				srv.setServingAmtUnit(srvgAmountUnitId); 
				srv.setServingAmtVal(srvgAmountValue);
				srv.setGramWtVal(grmWeightValue);
				srv.setServingAmtNote(srvgAmtNote);
				entry.addServing(srv);
			}while(c.moveToNext());
			
			
		}
		c.close(); 
				
		
		System.out.println("tracepoint 3.5: " + (new Date()).getTime());
		// Just for fun, get exchange info (?) 
		
		
		myDataBase.close(); 
		return entry; 
	}
	
	public NutritionEntry getNutritionEntryWithJoins(int foodId){
		
		// TODO: Fix this such that I don't have to do JOINS. because it's really not necessary (just do mulitple queries from different tables)
		String food_info_query = "SELECT tblFood._id, tblFood.foodName, tblFood.foodClassID, tblFood.cupGramWeight FROM tblFood  WHERE tblFood._id = ? ";
		
		// First, query for Nut items
		String nut_query = "SELECT tblFoodNutrients.nutrientID, tblFoodNutrients.nutrientValue FROM tblFoodNutrients WHERE tblFoodNutrients.foodID = ? AND tblFoodNutrients.nutrientID IN  (0,1,3,4,5,6,7,33,34,74,75,76,77,78,107,109,110,111,112)";
		
		// Pyr query
		String pyr_query = "SELECT * FROM tblFoodPyramidCategories WHERE foodID = ? AND pyramidCategoryId < 70"; 
		
		String srvg_query = "SELECT * FROM tblFoodServingTypes WHERE foodID = ?"; 

		
		NutritionEntry entry; 
		
		// First, get food info
		Cursor c = myDataBase.rawQuery(food_info_query, new String[]{Integer.toString(foodId)});
		if (c.moveToFirst()){ 
			
			entry = new NutritionEntry(c.getInt(c.getColumnIndex("_id")), 
				c.getString(c.getColumnIndex("foodName")), 
				"placeholder");
			entry.setFoodClass(FoodClass.get(c.getInt(c.getColumnIndex("foodClassID"))));
			entry.setCupGramWeight(c.getDouble(c.getColumnIndex("cupGramWeight"))); 
			// Should only be 1 or 0 entries
		}
		else{
			return null; 
			// NO ENTRIES!! 
		}
		c.close(); 
		
		// Then get nutrition info
		c = myDataBase.rawQuery(nut_query, new String[]{Integer.toString(foodId)});
		if (c.moveToFirst()){ 
			
			int nutId = c.getInt(c.getColumnIndex("nutrientID"));
			double nutVal = c.getDouble(c.getColumnIndex("nutrientValue"));
			entry.addNutrientVal(Nutrient.get(nutId), nutVal);
			
			while(c.moveToNext()){
				nutId = c.getInt(c.getColumnIndex("nutrientID"));
				nutVal = c.getDouble(c.getColumnIndex("nutrientValue"));
				entry.addNutrientVal(Nutrient.get(nutId), nutVal);
			}
			
		}
		c.close(); 
		
		// Then get pyramid info
		c = myDataBase.rawQuery(pyr_query, new String[]{Integer.toString(foodId)});
		if (c.moveToFirst()){ 
			
			int pyrId = c.getInt(c.getColumnIndex("pyramidCategoryId"));
			double pyrVal = c.getDouble(c.getColumnIndex("pyramidValue"));
			entry.addPyramidVal(PyramidCategory.get(pyrId), pyrVal);
			
			int isHighFat = c.getInt(c.getColumnIndex("isHighFat"));
			int isHighSugar = c.getInt(c.getColumnIndex("isHighSugar"));
			
			if (isHighFat == 1){
				entry.setHighFat(true);
			}
			
			if (isHighSugar == 1){
				entry.setHighSugar(true);
			}
			
			while(c.moveToNext()){
				pyrId = c.getInt(c.getColumnIndex("pyramidCategoryId"));
				pyrVal = c.getDouble(c.getColumnIndex("pyramidValue"));
				entry.addPyramidVal(PyramidCategory.get(pyrId), pyrVal);

				isHighFat = c.getInt(c.getColumnIndex("isHighFat"));
				isHighSugar = c.getInt(c.getColumnIndex("isHighSugar"));
				
				if (isHighFat == 1){
					entry.setHighFat(true);
				}
				
				if (isHighSugar == 1){
					entry.setHighSugar(true);
				}
			}
			
			c.close();
		}
		
		// Get servings info
		c = myDataBase.rawQuery(srvg_query, new String[]{Integer.toString(foodId)});
		if (c.moveToFirst()){ 
			
			do{
				int srvgTypeId = c.getInt(c.getColumnIndex("servingType")); 
				int srvgAmountUnitId = c.getInt(c.getColumnIndex("servingAmountUnitID"));
				double srvgAmountValue = c.getDouble(c.getColumnIndex("servingAmountValue")); // It's an int now, but supposed to be double. 
				double grmWeightValue = c.getDouble(c.getColumnIndex("gramWeightValue")); 
				String srvgAmtNote = c.getString(c.getColumnIndex("servingAmountNote"));
				
				NutritionEntry.Serving srv = entry.new Serving(); 
				srv.setServingType(ServingType.get(srvgTypeId));
				srv.setServingAmtUnit(srvgAmountUnitId); 
				srv.setServingAmtVal(srvgAmountValue);
				srv.setGramWtVal(grmWeightValue);
				srv.setServingAmtNote(srvgAmtNote);
				entry.addServing(srv);
			}while(c.moveToNext());
			
			
		}
		c.close(); 
		
		return entry; 
	}
}
