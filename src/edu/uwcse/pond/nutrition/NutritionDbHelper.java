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
package edu.uwcse.pond.nutrition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import edu.uwcse.pond.nutrition.Consts.FoodClass;
import edu.uwcse.pond.nutrition.Consts.Nutrient;
import edu.uwcse.pond.nutrition.Consts.PyramidCategory;
import edu.uwcse.pond.nutrition.Consts.ServingType;

/**
 * DatabaseHelper for the Nutrition data. Since the nutrition data should not
 * change, there are no write/set methods.
 * 
 * The nutrition database is packaged with the application in the assets folder.
 * When opening/using NutritionDbHelper for the first time, it checks that the
 * nutrition database exists where it's supposed to (in the data directory). If
 * it's not there, it copies the database from assets to the data directory, and
 * then uses it from the data directory. This process works well on all Android
 * versions except 2.2 (8?), which is why this app targets 2.3 and above.
 * 
 * @author aha
 * 
 */
public class NutritionDbHelper {

	/**
	 * Helper class to open the existing database. The methods that are usually
	 * important for creating a database are empty, because the database gets
	 * "created" by coping the file into the right place, so we don't want to
	 * overwrite that file.
	 * 
	 * @author aha
	 * 
	 */
	private static class AssetDbHelper extends SQLiteOpenHelper {

		public AssetDbHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}

	}

	/**
	 * Android's default system path of the nutrition database.
	 */
	private static String DB_PATH = "/data/data/edu.uwcse.pond.proto/databases/";
	/**
	 * The name of the nutrition database file.
	 */
	private static String DB_NAME = "nutritionDbFts3.db";// "nutritionDb.db";
	/**
	 * The database to make queries on.
	 */
	private SQLiteDatabase myDataBase;
	/**
	 * The context within which to open the database.
	 */
	private final Context myContext;
	/**
	 * Most recent version of the database.
	 */
	private static final int DB_VERSION = 2;
	/**
	 * Used to access the database.
	 */
	private AssetDbHelper myDbHelper;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public NutritionDbHelper(Context context) {
		this.myContext = context;
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {
		final String myPath = DB_PATH + DB_NAME; // /data/data/edu.uwcse.pond.proto/databases/nutritionDbFts3.db
		new File("/data/data/edu.uwcse.pond.proto/databases/nutritionDbFts3.db");

		SQLiteDatabase checkDB = null;

		try {
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (final SQLiteException e) {
			// database does't exist yet.
		}

		if (checkDB != null) {
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}

	public void close() {
		myDataBase.close();
		myDbHelper.close();
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * 
	 * @throws Exception
	 * */
	private void copyDataBase() throws Exception {
		// Open your local db as the input stream
		final InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		final String outFileName = DB_PATH + DB_NAME;

		// Open the empty db as the output stream
		final OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		final byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException {

		final boolean dbExist = checkDataBase();

		if (dbExist) {
			// do nothing - database already exist
		} else {

			try {
				copyDataBase();

			} catch (final IOException e) {
				e.printStackTrace();
				throw e;
			} catch (final Exception e) {
				throw new Error("Couldn't create a new file");
			}
		}

	}

	public Cursor getFoodClassesForGenericAndCommonFoodsWithNameFTS(String word) {
		final String[] words = word.split(" ");

		final StringBuffer buffer = new StringBuffer(words[0].trim());
		for (int i = 1; i < words.length; i++) {
			buffer.append(" NEAR ");
			buffer.append(words[i].trim());
		}

		final String query = "SELECT tblFoodClass.foodClassName, tblFoodClass.foodClassFullName, tblFoodClass._id, COUNT(tblFoodClass._id) FROM tblFoodClass JOIN tblFullText ON tblFullText.foodClassId = tblFoodClass._id "
				+ "WHERE tblFullText.foodNameText MATCH '"
				+ buffer.toString()
				+ "' AND isGeneric=1 GROUP BY foodClassId  ORDER BY COUNT(tblFoodClass._id) DESC";

		final Cursor c = myDataBase.rawQuery(query, new String[] {});
		return c;
	}

	/**
	 * Gets the FoodClasses for Generic foods with a given food name.
	 * isGeneric=1 and isCommon=0. Ordered by number of items in a foodClass.
	 * 
	 * @param word
	 * @return cursor with foodClassName, foodClassFullName, foodClass_id,
	 *         number of entries of that food class
	 */
	public Cursor getFoodClassesForGenericFoodsWithNameFTS(String word) {
		final String[] words = word.split(" ");

		final StringBuffer buffer = new StringBuffer(words[0].trim());
		for (int i = 1; i < words.length; i++) {
			buffer.append(" NEAR ");
			buffer.append(words[i].trim());
		}

		final String query = "SELECT tblFoodClass.foodClassName, tblFoodClass.foodClassFullName, tblFoodClass._id, COUNT(tblFoodClass._id) FROM tblFoodClass JOIN tblFullText ON tblFullText.foodClassId = tblFoodClass._id "
				+ "WHERE tblFullText.foodNameText MATCH '"
				+ buffer.toString()
				+ "' AND isGeneric=1 AND isCommon=0 GROUP BY foodClassId  ORDER BY COUNT(tblFoodClass._id) DESC";

		final Cursor c = myDataBase.rawQuery(query, new String[] {});
		return c;
	}

	/**
	 * Returns a cursor with records with a foodName LIKE the given word. Cursor
	 * includes the columns: _id, foodName, manufacturerName, manufacturerId,
	 * isCommon. Ordered by whether the food isCommon, then by manufacturerName,
	 * then by foodName.
	 * 
	 * @param word
	 *            desired word in the foodName
	 * @return cursor with entries
	 */
	public Cursor getFoodsWithName(String word) {
		final Cursor c = myDataBase
				.rawQuery(
						"SELECT tblFood._id, tblFood.foodName, tblManufacturer.manufacturerName, tblManufacturer.manufacturerID, tblFood.isCommon "
								+ "FROM tblFood LEFT OUTER JOIN tblManufacturer ON tblFood.manufacturerId = tblManufacturer.manufacturerID "
								+ "WHERE tblFood.foodName LIKE ? "
								+ "ORDER BY tblFood.isCommon DESC,tblManufacturer.manufacturerName,tblFood.foodName ASC",
						new String[] { "%" + word + "%" });
		return c;
	}

	/**
	 * 
	 * @param word
	 * @param fClass
	 * @return cursor with food_id, foodName, manufacturer name,
	 *         manufacturer_id, isCommon
	 */
	public Cursor getFoodsWithName(String word, FoodClass fClass) {
		if (fClass == null) {
			return getFoodsWithName(word);
		}

		final Cursor c = myDataBase
				.rawQuery(
						"SELECT tblFood._id, tblFood.foodName, "
								+ "tblManufacturer.manufacturerName, tblManufacturer.manufacturerID, "
								+ "tblFood.isCommon FROM tblFood LEFT OUTER JOIN tblManufacturer ON "
								+ "tblFood.manufacturerId = tblManufacturer.manufacturerID "
								+ "WHERE tblFood.foodName LIKE ? AND tblFood.foodClassID IN "
								+ fClass.getQueryString(), new String[] { "%"
								+ word + "%" });
		return c;
	}

	/**
	 * Get a cursor of foods with a given name and manufacturer Id.
	 * 
	 * @param word
	 * @param manufacturerId
	 * @return
	 */
	public Cursor getFoodsWithNameAndManId(String word, long manufacturerId) {
		/*
		 * Cursor c = myDataBase.rawQuery("SELECT tblFood._id, " +
		 * "tblFood.foodName, tblManufacturer.manufacturerName, " +
		 * "tblManufacturer.manufacturerID, " + "tblFood.isCommon " +
		 * "FROM tblFood LEFT OUTER JOIN tblManufacturer ON tblFood.manufacturerId = tblManufacturer.manufacturerID "
		 * +
		 * "WHERE tblFood.foodName LIKE ? AND tblManufacturer.manufacturerID = ?"
		 * , new String[] {"%" + word + "%", Long.toString(manufacturerId)});
		 */
		final String[] words = word.split(" ");

		final StringBuffer buffer = new StringBuffer(words[0].trim());
		for (int i = 1; i < words.length; i++) {
			buffer.append(" NEAR ");
			buffer.append(words[i].trim());
		}

		final String query = "SELECT DISTINCT tblFullText._id AS _id, tblFullText.foodNameText AS foodName "
				+ " FROM tblFullText  "
				+ "WHERE tblFullText.foodNameText MATCH '"
				+ buffer.toString()
				+ "' AND manId= "
				+ manufacturerId
				+ " ORDER BY tblFullText.foodNameText ASC";

		final Cursor c = myDataBase.rawQuery(query, new String[] {});
		return c;

		// return c;
	}

	/**
	 * Gets a cursor with foods considered generic (isGeneric=1), common
	 * (isCommon=1) and has the given word in the foodName (using
	 * Full-Text-Search)
	 * 
	 * @param word
	 * @return Cursor with _id & foodNameText
	 */
	public Cursor getGenericCommonFoodsWithNameFTS(String word) {
		final String[] words = word.split(" ");

		final StringBuffer buffer = new StringBuffer(words[0].trim());
		for (int i = 1; i < words.length; i++) {
			buffer.append(" NEAR ");
			buffer.append(words[i].trim());
		}

		final String query = "SELECT tblFullText._id, tblFullText.foodNameText FROM tblFullText  "
				+ "WHERE tblFullText.foodNameText MATCH '"
				+ buffer.toString()
				+ "' AND isGeneric=1 AND isCommon=1 ORDER BY foodClassId, tblFullText.foodNameText ASC ";

		final Cursor c = myDataBase.rawQuery(query, new String[] {});
		return c;
	}

	/**
	 * Gets the Generic (but not common) foods with a given word in the food
	 * name and a given foodClassId, using FullText-Search. Ordered by food
	 * name.
	 * 
	 * @param word
	 * @param foodClassId
	 * @return Cursor with foodName_id, foodName
	 */
	public Cursor getGenericFoodsInFoodClassWithNameFTS(String word,
			int foodClassId) {
		final String[] words = word.split(" ");

		final StringBuffer buffer = new StringBuffer(words[0].trim());
		for (int i = 1; i < words.length; i++) {
			buffer.append(" NEAR ");
			buffer.append(words[i].trim());
		}

		final String query = "SELECT tblFullText._id, tblFullText.foodNameText FROM tblFullText  "
				+ "WHERE tblFullText.foodNameText MATCH '"
				+ buffer.toString()
				+ "' AND isGeneric=1 AND isCommon=0 AND foodClassId="
				+ foodClassId + " ORDER BY tblFullText.foodNameText ASC ";

		final Cursor c = myDataBase.rawQuery(query, new String[] {});
		return c;
	}

	/**
	 * Gets the FoodClasses for Generic foods with a given food name.
	 * isGeneric=1 and manId IS null. Ordered by number of items in a foodClass.
	 * 
	 * @param word
	 *            search string
	 * @return cursor with foodClassName, foodClassFullName, foodClass_id,
	 *         number of entries of that food class
	 */
	public Cursor getGenericFoodsWithNameFTS(String word) {
		final String[] words = word.split(" ");

		final StringBuffer buffer = new StringBuffer(words[0].trim());
		for (int i = 1; i < words.length; i++) {
			buffer.append(" NEAR ");
			buffer.append(words[i].trim());
		}

		final String query = "SELECT tblFullText._id, tblFullText.foodNameText FROM tblFullText  "
				+ "WHERE tblFullText.foodNameText MATCH '"
				+ buffer.toString()
				+ "' AND manId='' AND isGeneric=1 ORDER BY isCommon DESC, foodClassId, tblFullText.foodNameText ASC ";

		final Cursor c = myDataBase.rawQuery(query, new String[] {});
		return c;
	}

	/**
	 * Gets the manufacturers that produce foods with given words in the name,
	 * using FTS.
	 * 
	 * @param word
	 * @return Cursor with manufacturer name, id
	 */
	public Cursor getManfacturersForFoodWithName(String word) {
		/*
		 * String query = "SELECT DISTINCT "+ "manufacturerName, " +
		 * "tblManufacturer.manufacturerID AS _id " +
		 * "FROM tblManufacturer JOIN tblFood ON tblManufacturer.manufacturerID = tblFood.manufacturerId "
		 * + "WHERE tblFood.foodName LIKE ? ";
		 * 
		 * Cursor c = myDataBase.rawQuery(query, new String[] {"%" + word +
		 * "%"}); return c;
		 */
		final String[] words = word.split(" ");

		final StringBuffer buffer = new StringBuffer(words[0].trim());
		for (int i = 1; i < words.length; i++) {
			buffer.append(" NEAR ");
			buffer.append(words[i].trim());
		}

		final String query = "SELECT DISTINCT manufacturerName, tblManufacturer.manufacturerID AS _id"
				+ " FROM tblManufacturer JOIN tblFullText ON tblManufacturer.manufacturerID = tblFullText.manId "
				+ "WHERE tblFullText.foodNameText MATCH '"
				+ buffer.toString()
				+ "' ORDER BY manufacturerName ASC";

		final Cursor c = myDataBase.rawQuery(query, new String[] {});
		return c;
	}

	/**
	 * Gets a cursor listing manufacturers who produce a food with the given
	 * name. Does not use Full Text Search table.
	 * 
	 * @param word
	 *            the name of a food (or word in a food name).
	 * @return A cursor where each row is a manufacturers; no other columns are
	 *         selected.
	 */
	public Cursor getManufacturersForFoodWithName(String word) {
		// TODO: Update to use the FTS table instead of just tblFood.foodName
		final String query = "SELECT DISTINCT(manufacturerName) "
				+ "FROM tblFood LEFT OUTER JOIN tblManufacturer ON tblFood.manufacturerId = tblManufacturer.manufacturerID "
				+ "WHERE foodName LIKE '?'";
		return myDataBase.rawQuery(query, new String[] { "%" + word + "%" });
	}

	/**
	 * Gets a cursor listing all the foods with the given food word and
	 * manufacturerId.
	 * 
	 * @param word
	 *            The word to be in the foodName
	 * @param manId
	 *            The manufacturerId
	 * @return A cursor (all tblFood columns)
	 */
	public Cursor getManufacturersForFoodWithName(String word, int manId) {
		final String query = "SELECT * FROM tblFood WHERE foodName LIKE '?' AND (manufacturerId='?')";
		return myDataBase.rawQuery(query, new String[] { "%" + word + "%",
				Integer.toString(manId) });
	}

	/**
	 * Uses Full-Text Search to get the number of Brand-Name foods with the
	 * given word in foodName.
	 * 
	 * @param word
	 * @return Number of entries that have the given word in the foodName AND
	 *         isGeneric=false.
	 */
	public int getNumBrandFoodsWithNameFTS(String word) {
		final String[] words = word.split(" ");

		final StringBuffer buffer = new StringBuffer(words[0].trim());
		for (int i = 1; i < words.length; i++) {
			buffer.append(" NEAR ");
			buffer.append(words[i].trim());
		}

		final String query = "SELECT count(tblFullText._id) AS count FROM tblFullText  "
				+ "WHERE tblFullText.foodNameText MATCH '"
				+ buffer.toString()
				+ "' AND isGeneric=0 ";

		final Cursor c = myDataBase.rawQuery(query, new String[] {});
		c.moveToFirst();
		return c.getInt(0);
	}

	/**
	 * Uses Full-Text Search to determine the number of entries in the database
	 * that have the given word.
	 * 
	 * @param word
	 * @return Number of entries that have the given word in the foodName AND
	 *         isGeneric=true.
	 */
	public int getNumGenericFoodsWithNameFTS(String word) {
		final String[] words = word.split(" ");

		final StringBuffer buffer = new StringBuffer(words[0].trim());
		for (int i = 1; i < words.length; i++) {
			buffer.append(" NEAR ");
			buffer.append(words[i].trim());
		}

		final String query = "SELECT count(tblFullText._id) AS count FROM tblFullText  "
				+ "WHERE tblFullText.foodNameText MATCH '"
				+ buffer.toString()
				+ "' AND isGeneric=1 ";

		final Cursor c = myDataBase.rawQuery(query, new String[] {});
		c.moveToFirst();
		return c.getInt(0);
	}

	/**
	 * Gets a nutritionEntry object for a given foodId. This can probably be
	 * GREATLY optimized. It's definitely created from a
	 * "make it conceptually simple first" approach, hence the object being
	 * returned. So when the response time is too slow, look here.
	 * 
	 * @param foodId
	 * @return
	 */
	public NutritionEntry getNutritionEntry(int foodId) {

		// TODO: Fix this such that I don't have to do JOINS. because it's
		// really not necessary (just do mulitple queries from different tables)
		final String food_info_query = "SELECT tblFood._id, tblFood.foodName, tblFood.foodClassID, tblFood.cupGramWeight FROM tblFood  WHERE tblFood._id = ? ";

		// First, query for Nut items
		final String nut_query = "SELECT tblFoodNutrients.nutrientID, tblFoodNutrients.nutrientValue FROM tblFoodNutrients WHERE tblFoodNutrients.foodID = ? AND tblFoodNutrients.nutrientID IN  (0,1,3,4,5,6,7,33,34,74,75,76,77,78,107,109,110,111,112)";

		// Pyr query
		final String pyr_query = "SELECT * FROM tblFoodPyramidCategories WHERE foodID = ? AND pyramidCategoryId < 70";

		final String srvg_query = "SELECT * FROM tblFoodServingTypes WHERE foodID = ?";

		NutritionEntry entry;
		System.out.println("tracepoint 3.1: " + (new Date()).getTime());
		// First, get food info
		Cursor c = myDataBase.rawQuery(food_info_query,
				new String[] { Integer.toString(foodId) });
		if (c.moveToFirst()) {

			entry = new NutritionEntry(c.getInt(c.getColumnIndex("_id")),
					c.getString(c.getColumnIndex("foodName")), "placeholder");
			entry.setFoodClass(FoodClass.get(c.getInt(c
					.getColumnIndex("foodClassID"))));
			entry.setCupGramWeight(c.getDouble(c
					.getColumnIndex("cupGramWeight")));
			// Should only be 1 or 0 entries
		} else {
			return null;
			// NO ENTRIES!!
		}
		c.close();

		System.out.println("tracepoint 3.2: " + (new Date()).getTime());

		// Then get nutrition info
		c = myDataBase.rawQuery(nut_query,
				new String[] { Integer.toString(foodId) });
		System.out.println("tracepoint 3.2.1: " + (new Date()).getTime());

		// Putting the nutrients into the HashTable is the most time consuming
		// piece
		// here. I'm not sure the best way to deal with that, but something
		// needs to be done.
		if (c.moveToFirst()) {

			int nutId = c.getInt(c.getColumnIndex("nutrientID"));
			double nutVal = c.getDouble(c.getColumnIndex("nutrientValue"));
			entry.addNutrientVal(Nutrient.get(nutId), nutVal);

			while (c.moveToNext()) {
				nutId = c.getInt(c.getColumnIndex("nutrientID"));
				nutVal = c.getDouble(c.getColumnIndex("nutrientValue"));
				entry.addNutrientVal(Nutrient.get(nutId), nutVal);
			}

		}
		c.close();

		System.out.println("tracepoint 3.3: " + (new Date()).getTime());
		// Then get pyramid info
		c = myDataBase.rawQuery(pyr_query,
				new String[] { Integer.toString(foodId) });
		if (c.moveToFirst()) {

			int pyrId = c.getInt(c.getColumnIndex("pyramidCategoryId"));
			double pyrVal = c.getDouble(c.getColumnIndex("pyramidValue"));
			entry.addPyramidVal(PyramidCategory.get(pyrId), pyrVal);

			int isHighFat = c.getInt(c.getColumnIndex("isHighFat"));
			int isHighSugar = c.getInt(c.getColumnIndex("isHighSugar"));

			if (isHighFat == 1) {
				entry.setHighFat(true);
			}

			if (isHighSugar == 1) {
				entry.setHighSugar(true);
			}

			while (c.moveToNext()) {
				pyrId = c.getInt(c.getColumnIndex("pyramidCategoryId"));
				pyrVal = c.getDouble(c.getColumnIndex("pyramidValue"));
				entry.addPyramidVal(PyramidCategory.get(pyrId), pyrVal);

				isHighFat = c.getInt(c.getColumnIndex("isHighFat"));
				isHighSugar = c.getInt(c.getColumnIndex("isHighSugar"));

				if (isHighFat == 1) {
					entry.setHighFat(true);
				}

				if (isHighSugar == 1) {
					entry.setHighSugar(true);
				}
			}

			c.close();
		}

		System.out.println("tracepoint 3.4: " + (new Date()).getTime());
		// Get servings info
		c = myDataBase.rawQuery(srvg_query,
				new String[] { Integer.toString(foodId) });
		if (c.moveToFirst()) {

			do {
				final int srvgTypeId = c
						.getInt(c.getColumnIndex("servingType"));
				final int srvgAmountUnitId = c.getInt(c
						.getColumnIndex("servingAmountUnitID"));
				final double srvgAmountValue = c.getDouble(c
						.getColumnIndex("servingAmountValue")); // It's an int
																// now, but
																// supposed to
																// be double.
				final double grmWeightValue = c.getDouble(c
						.getColumnIndex("gramWeightValue"));
				final String srvgAmtNote = c.getString(c
						.getColumnIndex("servingAmountNote"));

				final NutritionEntry.Serving srv = entry.new Serving();
				srv.setServingType(ServingType.get(srvgTypeId));
				srv.setServingAmtUnit(srvgAmountUnitId);
				srv.setServingAmtVal(srvgAmountValue);
				srv.setGramWtVal(grmWeightValue);
				srv.setServingAmtNote(srvgAmtNote);
				entry.addServing(srv);
			} while (c.moveToNext());

		}
		c.close();

		System.out.println("tracepoint 3.5: " + (new Date()).getTime());
		// Just for fun, get exchange info (?)

		myDataBase.close();
		return entry;
	}

	public NutritionEntry getNutritionEntryWithJoins(int foodId) {

		// TODO: Fix this such that I don't have to do JOINS. because it's
		// really not necessary (just do mulitple queries from different tables)
		final String food_info_query = "SELECT tblFood._id, tblFood.foodName, tblFood.foodClassID, tblFood.cupGramWeight FROM tblFood  WHERE tblFood._id = ? ";

		// First, query for Nut items
		final String nut_query = "SELECT tblFoodNutrients.nutrientID, tblFoodNutrients.nutrientValue FROM tblFoodNutrients WHERE tblFoodNutrients.foodID = ? AND tblFoodNutrients.nutrientID IN  (0,1,3,4,5,6,7,33,34,74,75,76,77,78,107,109,110,111,112)";

		// Pyr query
		final String pyr_query = "SELECT * FROM tblFoodPyramidCategories WHERE foodID = ? AND pyramidCategoryId < 70";

		final String srvg_query = "SELECT * FROM tblFoodServingTypes WHERE foodID = ?";

		NutritionEntry entry;

		// First, get food info
		Cursor c = myDataBase.rawQuery(food_info_query,
				new String[] { Integer.toString(foodId) });
		if (c.moveToFirst()) {

			entry = new NutritionEntry(c.getInt(c.getColumnIndex("_id")),
					c.getString(c.getColumnIndex("foodName")), "placeholder");
			entry.setFoodClass(FoodClass.get(c.getInt(c
					.getColumnIndex("foodClassID"))));
			entry.setCupGramWeight(c.getDouble(c
					.getColumnIndex("cupGramWeight")));
			// Should only be 1 or 0 entries
		} else {
			return null;
			// NO ENTRIES!!
		}
		c.close();

		// Then get nutrition info
		c = myDataBase.rawQuery(nut_query,
				new String[] { Integer.toString(foodId) });
		if (c.moveToFirst()) {

			int nutId = c.getInt(c.getColumnIndex("nutrientID"));
			double nutVal = c.getDouble(c.getColumnIndex("nutrientValue"));
			entry.addNutrientVal(Nutrient.get(nutId), nutVal);

			while (c.moveToNext()) {
				nutId = c.getInt(c.getColumnIndex("nutrientID"));
				nutVal = c.getDouble(c.getColumnIndex("nutrientValue"));
				entry.addNutrientVal(Nutrient.get(nutId), nutVal);
			}

		}
		c.close();

		// Then get pyramid info
		c = myDataBase.rawQuery(pyr_query,
				new String[] { Integer.toString(foodId) });
		if (c.moveToFirst()) {

			int pyrId = c.getInt(c.getColumnIndex("pyramidCategoryId"));
			double pyrVal = c.getDouble(c.getColumnIndex("pyramidValue"));
			entry.addPyramidVal(PyramidCategory.get(pyrId), pyrVal);

			int isHighFat = c.getInt(c.getColumnIndex("isHighFat"));
			int isHighSugar = c.getInt(c.getColumnIndex("isHighSugar"));

			if (isHighFat == 1) {
				entry.setHighFat(true);
			}

			if (isHighSugar == 1) {
				entry.setHighSugar(true);
			}

			while (c.moveToNext()) {
				pyrId = c.getInt(c.getColumnIndex("pyramidCategoryId"));
				pyrVal = c.getDouble(c.getColumnIndex("pyramidValue"));
				entry.addPyramidVal(PyramidCategory.get(pyrId), pyrVal);

				isHighFat = c.getInt(c.getColumnIndex("isHighFat"));
				isHighSugar = c.getInt(c.getColumnIndex("isHighSugar"));

				if (isHighFat == 1) {
					entry.setHighFat(true);
				}

				if (isHighSugar == 1) {
					entry.setHighSugar(true);
				}
			}

			c.close();
		}

		// Get servings info
		c = myDataBase.rawQuery(srvg_query,
				new String[] { Integer.toString(foodId) });
		if (c.moveToFirst()) {

			do {
				final int srvgTypeId = c
						.getInt(c.getColumnIndex("servingType"));
				final int srvgAmountUnitId = c.getInt(c
						.getColumnIndex("servingAmountUnitID"));
				final double srvgAmountValue = c.getDouble(c
						.getColumnIndex("servingAmountValue")); // It's an int
																// now, but
																// supposed to
																// be double.
				final double grmWeightValue = c.getDouble(c
						.getColumnIndex("gramWeightValue"));
				final String srvgAmtNote = c.getString(c
						.getColumnIndex("servingAmountNote"));

				final NutritionEntry.Serving srv = entry.new Serving();
				srv.setServingType(ServingType.get(srvgTypeId));
				srv.setServingAmtUnit(srvgAmountUnitId);
				srv.setServingAmtVal(srvgAmountValue);
				srv.setGramWtVal(grmWeightValue);
				srv.setServingAmtNote(srvgAmtNote);
				entry.addServing(srv);
			} while (c.moveToNext());

		}
		c.close();

		return entry;
	}

	public NutritionDbHelper open() throws SQLException {
		myDbHelper = new AssetDbHelper(myContext);
		myDataBase = myDbHelper.getReadableDatabase();
		return this;
	}
}
