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

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import edu.uwcse.pond.diary.GoalDiaryTableHelper;
import edu.uwcse.pond.diary.PointsDiaryTableHelper;


/**
 * A bunch of constants that are required throughout the app. 
 * Usually defining values as defined in NutritionistPro, to make 
 * it easier to use. 
 * @author aha
 *
 */
public class Consts {
	/**
	 * The pyramid categories as defined in tblPyramidCategories. 
	 * Since nutritionistPro has multiple entries for some categories 
	 * (for example, MEAT_EGG). In NP, this distinguishes between "old" and 
	 * "new" food pyramid categories. This enum doesn't really account for that 
	 * properly-- it just lumps them all together. 
	 * @author aha
	 *
	 */
	public enum PyramidCategory{ // *NEW* food pyramid categories are <70
		BREAD_CEREAL_RICE_AND_PASTA (1), 
		VEGETABLES_DARK_GREEN (2), 
		VEGETABLES_DEEP_YELLOW (3), 
		VEGETABLES_GREEN_PEAS (4), 
		VEGETABLES_OTHER_STARCHY (5), 
		FRUIT (6), 
		MEAT_FISH_POULTRY_TOFU_AND_PREPARED_MEAT (7), 
		MEAT_EGG (8), 
		MEAT_NUTS_SEEDS_BEANS_AND_PEANUT_BUTTER (9), 
		MILK_YOGURT_AND_CHEESE (10), 
		FATS (11), 
		SUGARS (12), 
		VEGETABLES_OTHER (13), 
		BREAD (14), 
		//FRUIT (15), 
		VEGETABLE_STARCHY (16), 
		VEGETABLE_NON_STARCHY (17), 
		SUGAR (18), 
		// MEAT_FISH_POULTRY_TOFU_AND_PREPARED_MEAT (19), 
		MILK (20), 
		FAT (21), 
		MEAT_RAW (22);
		//MEAT_EGG (23), 
		//MEAT_NUTS_SEEDS_BEANS_AND_PEANUT_BUTTER (24), 
		//MEAT_RAW (25), 
		//	VEGETABLES (71), 
		//	GRAINS (72), 
		//FRUIT (73), 
		//	MEAT_AND_BEANS (74), 
		//MILK (75), 
		//	OILS (76), 
		//	DISCRETIONARY (77);

		private final int id;  

		/**
		 * Constructor for the PyramidCategory enum
		 * @param id
		 */
		PyramidCategory(int id) {
			this.id = id;
		}

		/**
		 * Map linking id to category. 
		 */
		private static final Map<Integer,PyramidCategory> lookup = new HashMap<Integer,PyramidCategory>();

		
		static {
			for(PyramidCategory s : EnumSet.allOf(PyramidCategory.class))
				lookup.put(s.getId(), s);
			// Hard-coding the duplicates
			lookup.put(15, FRUIT);
			lookup.put(19, MEAT_FISH_POULTRY_TOFU_AND_PREPARED_MEAT); 
			lookup.put(23, MEAT_EGG);
			lookup.put(24, MEAT_NUTS_SEEDS_BEANS_AND_PEANUT_BUTTER);
			lookup.put(25, MEAT_RAW);
		}


		public int getId() { return this.id; }

		public static PyramidCategory get(int code) { 
			return lookup.get(code); 
		}
	}


	/**
	 * Nutrients and their ids, as defined in NutritionistPro tblNutrient.
	 * Ignoring names/abbreviations. 
	 * @author aha
	 *
	 */
	public enum Nutrient{

		WEIGHT (0), 
		KILOCALORIES (1), 
		CARBOHYDRATE (3), 
		FAT_TOTAL (4), 
		ALCOHOL (5), 
		CHOLESTEROL (6), 
		SATURATED_FAT (7), 
		TRANS_FATTY_ACID (33), 
		SODIUM (34), 
		DIETARY_FIBER_TOTAL (74), 
		SOLUBLE_FIBER (75), 
		INSOLUBLE_FIBER (76), 
		CRUDE_FIBER (77), 
		SUGAR_TOTAL (78), 
		CAFFEINE (107), 
		CALORIES_FROM_FAT (109), 
		CALORIES_FROM_SATURATED_FAT (110), 
		SUGAR_ALCOHOL (111), 
		OTHER_CARBOHYDRATE (112);

		private final int id;  

		Nutrient(int id) {
			this.id = id;
		}

		private static final Map<Integer,Nutrient> lookup = new HashMap<Integer,Nutrient>();

		static {
			for(Nutrient s : EnumSet.allOf(Nutrient.class))
				lookup.put(s.getId(), s);
		}


		public int getId() { return this.id; }

		public static Nutrient get(int code) { 
			return lookup.get(code); 
		}
	}

	/** 
	 * Representing FoodClasses and their ids, as defined in NutritionistPro
	 * tblFoodClass. This just includes the highest-level food classes (top-level parent), 
	 * but includes a mechanism to get all the ids (as a comma-delimited string) 
	 * of the child FoodClass ids for each parent. 
	 * @author aha
	 *
	 */
	public enum FoodClass{
		ACCOMPANIMENT (3),
		BABY_FOOD (47),
		BAKED_PRODUCT (59),
		BEVERAGE (82),
		FRUIT_JUICE (86), 
		FRUIT_NECTAR(89), 
		COMBINATION_FOOD (109),
		DAIRY (140),
		FISH (149),
		EGG (154),
		GRAIN (155),
		MEAT (156),
		INGREDIENT (157),
		NON_MEAT_PROTEIN (158),
		POULTRY (159),
		PREPARED_MEAT (160),
		SOUP (161),
		SUPPLEMENT (162),
		SWEETS (163),
		VEGETABLE (164),
		FRUIT (290),
		INTERNATIONAL_FOOD (651),
		UNSPECIFIED (766);

		private final int id; 

		private static final Map<Integer,FoodClass> lookup = new HashMap<Integer,FoodClass>();

		private static final Map<FoodClass,String> childQueryStrings = new HashMap<FoodClass,String>();

		static {
			for(FoodClass s : EnumSet.allOf(FoodClass.class)){
				lookup.put(s.getId(), s);
			}
			childQueryStrings.put(ACCOMPANIMENT, "(3,5,6,7,8,10,11,12,13)");
			childQueryStrings.put(BABY_FOOD, "47,49,50,51,52,53,54,55,56,57,58,783");
			childQueryStrings.put(BAKED_PRODUCT, "(59,61,62,63,64,66,67,68,69,70,71,73,74,75,76,77,78,79,80,81,785,786)");
			childQueryStrings.put(BEVERAGE, "(82,84,85,86,87,89,90,91,92,93,94,97,100,101,774)");
			childQueryStrings.put(COMBINATION_FOOD, "(109,111,112,114,115,116,118,119,120,121)");
			childQueryStrings.put(DAIRY, "(140,142,143,146,147,570)");
			childQueryStrings.put(FISH, "(149,151,152)");
			childQueryStrings.put(EGG, "(154)");
			childQueryStrings.put(GRAIN, "(155,167,168,169,170,172,173,174,175,176,177,178,590,594,595)");
			childQueryStrings.put(MEAT, "(156,182,183,184,185,186)");
			childQueryStrings.put(INGREDIENT, "(157,216,217,218,219,220,221,222,223,224,225,226,227,228,229,232,233,234,235,236,237,238,239,240,241,243,244,245,246,248,784)");
			childQueryStrings.put(NON_MEAT_PROTEIN, "(158,251,252,253,254,255,256,257)");
			childQueryStrings.put(POULTRY, "(159,267,268,270,280)");
			childQueryStrings.put(PREPARED_MEAT, "(160,283,284,285,287,288,289)");
			childQueryStrings.put(SOUP, "(161,801,802,803)");
			childQueryStrings.put(SUPPLEMENT, "(162,311,312,313)");
			childQueryStrings.put(SWEETS, "(163,296,299,300,301,302,303,304,305,306,307,308,788)");
			childQueryStrings.put(VEGETABLE, "(164,642,643,644,645)");
			childQueryStrings.put(FRUIT, "(290,585,814,818)");
			childQueryStrings.put(INTERNATIONAL_FOOD, "(651,655,660,665,671,680,691,697,698,703,704,713,721,730,740,744,745,755)");
			childQueryStrings.put(UNSPECIFIED, "(766)");

		}

		private FoodClass(int _id){
			this.id = _id; 
		}

		public int getId() { return this.id; }

		public static FoodClass get(int code) { 
			return lookup.get(code); 
		}

		public String getQueryString(){
			return childQueryStrings.get(this);
		}
	}

	/**
	 * ServingTypes, as defined in NutritionistPro tblServingType
	 * @author aha
	 *
	 */
	public enum ServingType{
		Typical (1), 
		Minimum(2), 
		Maximum(3), 
		Alt_Serving (4), 
		Alt_Item (5), 
		Alt_Slice (6), 
		Alt_Piece (7), 
		Pyramid (8);


		private final int id; 

		private static final Map<Integer,ServingType> lookup = new HashMap<Integer,ServingType>();

		static {
			for(ServingType s : EnumSet.allOf(ServingType.class)){
				lookup.put(s.getId(), s);
			}
		}

		private ServingType(int _id){
			this.id = _id; 
		}

		public int getId() { return this.id; }

		public static ServingType get(int code) { 
			return lookup.get(code); 
		}
	}

	/**
	 * Captures the Units as defined in NutritionistPro tblUnit. 
	 * @author aha
	 *
	 */
	public enum Unit{
		SVG(1),
		ITEM(2),
		SL(3),
		PC(4),
		TEA(5),
		TABLE(6),
		FL_OZ(7),
		C(8),
		QT(9),
		GAL(10),
		OZ(11),
		L(12),
		MG(13),
		ML_100(14),
		LB(15),
		KG(16),
		G(17),
		ML(18),
		IN(19),
		FT(20),
		CM(21),
		M(22),
		SEC(23),
		MIN(24),
		HR(25),
		DAY(26),
		WK(27),
		MO(28),
		YR(29),
		PT(30),
		RE(31),
		MICRO_G(32),
		IU(33),
		KCAL(34),
		G_100(36);


		private final int id; 

		private static final Map<Integer,Unit> lookup = new HashMap<Integer,Unit>();

		private static final Map<Unit,String> desc_lookup = new HashMap<Unit,String>();

		static {
			for(Unit s : EnumSet.allOf(Unit.class)){
				lookup.put(s.getId(), s);
			}
			desc_lookup.put(SVG, "serving(s)");
			desc_lookup.put(ITEM, "item(s)");
			desc_lookup.put(SL, "slice(s)");
			desc_lookup.put(PC, "piece(s)");
			desc_lookup.put(TEA, "teaspoon(s)");
			desc_lookup.put(TABLE, "tablespoon(s)");
			desc_lookup.put(FL_OZ, "fluid ounce(s)");
			desc_lookup.put(C, "cup(s)");
			desc_lookup.put(QT, "quart(s)");
			desc_lookup.put(GAL, "gallon(s)");
			desc_lookup.put(OZ, "ounce(s)");
			desc_lookup.put(L, "liter(s)");
			desc_lookup.put(MG, "milligram(s)");
			desc_lookup.put(ML_100, "100 milliliters");
			desc_lookup.put(LB, "pound(s)");
			desc_lookup.put(KG, "kilogram(s)");
			desc_lookup.put(G, "gram(s)");
			desc_lookup.put(ML, "milliliter(s)");
			desc_lookup.put(IN, "inch(es)");
			desc_lookup.put(FT, "foot(eet)");
			desc_lookup.put(CM, "centimeter(s)");
			desc_lookup.put(M, "meter(s)");
			desc_lookup.put(SEC, "second(s)");
			desc_lookup.put(MIN, "minute(s)");
			desc_lookup.put(HR, "hour(s)");
			desc_lookup.put(DAY, "day(s)");
			desc_lookup.put(WK, "week(s)");
			desc_lookup.put(MO, "month(s)");
			desc_lookup.put(YR, "year(s)");
			desc_lookup.put(PT, "pint(s)");
			desc_lookup.put(RE, "retinol equivalent(s)");
			desc_lookup.put(MICRO_G, "microgram(s)");
			desc_lookup.put(IU, "international unit(s)");
			desc_lookup.put(KCAL, "kilocalorie(s)");
			desc_lookup.put(MO, "milli-osmol(s)");
			desc_lookup.put(G_100, "100grams");
		}

		private Unit(int _id){
			this.id = _id; 
		}

		public int getId() { return this.id; }

		public static Unit get(int code) { 
			return lookup.get(code); 


		}

		public String getDescString(){
			return desc_lookup.get(this);
		}

	}

	
	/** 
	 * Defines PointComponents, as used by POND. 
	 * @author aha
	 *
	 */
	public enum PointComponent{ 

		SOLID_FATS("Solid Fats", PointsDiaryTableHelper.COL_SOLID_FATS_VAL, GoalDiaryTableHelper.COL_SOLID_FATS_GOAL, "fats", 12),//, edu.uwcse.pond.proto.R.drawable.alcohol, edu.uwcse.pond.proto.R.drawable.button_bg_alcohol, edu.uwcse.pond.proto.R.color.ALCOHOL),
		DAIRY("Dairy", PointsDiaryTableHelper.COL_DAIRY_VAL, GoalDiaryTableHelper.COL_DAIRY_GOAL, "dairy", 7),//,// edu.uwcse.pond.proto.R.drawable.dairy, edu.uwcse.pond.proto.R.drawable.button_bg_dairy, edu.uwcse.pond.proto.R.color.DAIRY),
		SAT_FATS("Sat Fat", PointsDiaryTableHelper.COL_FATS_VAL, GoalDiaryTableHelper.COL_SAT_FATS_GOAL, "fats", 13),//,// edu.uwcse.pond.proto.R.drawable.fats, edu.uwcse.pond.proto.R.drawable.button_bg_fats, edu.uwcse.pond.proto.R.color.FATS), 
		FRUIT("Fruit Juice", PointsDiaryTableHelper.COL_FRUIT_VAL, GoalDiaryTableHelper.COL_FRUIT_GOAL, "fruit", 6),// edu.uwcse.pond.proto.R.drawable.fruit, edu.uwcse.pond.proto.R.drawable.button_bg_fruit, edu.uwcse.pond.proto.R.color.FRUIT),
		FRUIT_WHOLE("Fruit", PointsDiaryTableHelper.COL_FRUIT_WHOLE_VAL, GoalDiaryTableHelper.COL_FRUIT_WHOLE_GOAL, "fruit_whole", 5),// edu.uwcse.pond.proto.R.drawable.fruit_whole, edu.uwcse.pond.proto.R.drawable.button_bg_fruit_whole, edu.uwcse.pond.proto.R.color.FRUIT_WHOLE),
		GRAINS("Grains", PointsDiaryTableHelper.COL_GRAINS_VAL, GoalDiaryTableHelper.COL_GRAINS_GOAL, "grains", 4),// edu.uwcse.pond.proto.R.drawable.grains, edu.uwcse.pond.proto.R.drawable.button_bg_grains, edu.uwcse.pond.proto.R.color.GRAINS), 
		GRAINS_WHOLE("Whole Grains", PointsDiaryTableHelper.COL_GRAINS_WHOLE_VAL, GoalDiaryTableHelper.COL_GRAINS_WHOLE_GOAL, "grains_whole", 3),// edu.uwcse.pond.proto.R.drawable.grains_whole, edu.uwcse.pond.proto.R.drawable.button_bg_grains_whole, edu.uwcse.pond.proto.R.color.GRAINS_WHOLE), 
		PROTEIN("Protein", PointsDiaryTableHelper.COL_PROTEIN_VAL, GoalDiaryTableHelper.COL_PROTEIN_GOAL, "protein", 8),// edu.uwcse.pond.proto.R.drawable.protein, edu.uwcse.pond.proto.R.drawable.button_bg_protein, edu.uwcse.pond.proto.R.color.PROTEIN), 
		SODIUM("Sodium", PointsDiaryTableHelper.COL_SODIUM_VAL, GoalDiaryTableHelper.COL_SODIUM_GOAL, "sodium", 11),// edu.uwcse.pond.proto.R.drawable.sodium, edu.uwcse.pond.proto.R.drawable.button_bg_sodium, edu.uwcse.pond.proto.R.color.SODIUM),
		SUGAR("Added Sugar", PointsDiaryTableHelper.COL_SUGAR_VAL, GoalDiaryTableHelper.COL_SUGAR_GOAL, "sugar", 10),// edu.uwcse.pond.proto.R.drawable.sugar, edu.uwcse.pond.proto.R.drawable.button_bg_sugar, edu.uwcse.pond.proto.R.color.SUGAR),
		VEGGIE("Veg", PointsDiaryTableHelper.COL_VEGGIE_VAL, GoalDiaryTableHelper.COL_VEGGIE_GOAL, "veggie", 2),// edu.uwcse.pond.proto.R.drawable.veggie_half, edu.uwcse.pond.proto.R.drawable.button_bg_veggie, edu.uwcse.pond.proto.R.color.VEGGIE),
		VEGGIE_GREEN("Dark Green/Orange Veg", PointsDiaryTableHelper.COL_VEGGIE_WHOLE_VAL, GoalDiaryTableHelper.COL_VEGGIE_WHOLE_GOAL, "veggie_green", 1),// edu.uwcse.pond.proto.R.drawable.veggie_green, edu.uwcse.pond.proto.R.drawable.button_bg_veggie_green, edu.uwcse.pond.proto.R.color.VEGGIE_GREEN),
		OILS("Oils", PointsDiaryTableHelper.COL_OILS_VAL, GoalDiaryTableHelper.COL_OILS_GOAL, "oils", 9),//, edu.uwcse.pond.proto.R.drawable.alcohol, edu.uwcse.pond.proto.R.drawable.button_bg_alcohol, edu.uwcse.pond.proto.R.color.ALCOHOL),
		ALL("All", "", "", "all", 1);//, edu.uwcse.pond.proto.R.drawable.all, edu.uwcse.pond.proto.R.drawable.button_bg_all, edu.uwcse.pond.proto.R.color.ALL);

		private final String desc;  
		private final String pts_col_name; 
		private final String goal_col_name; 
		private int myDrawableId = -1; 
		private int myListBgId = -1; 
		private int myButtonDrawableId = -1;
		private int myColorId = -1; 
		private int myHalfId = -1; 
		private int myExtraId = -1; 
		private int myWhatWhatId = -1;
		private final String myBaseId; 
		private final int myOrderId; 

		PointComponent(String dsc, String colName, String goalName, String baseId, int orderId) { //, int drawId, int buttonId, int colorId
			this.desc = dsc;
			this.pts_col_name = colName; 
			this.goal_col_name = goalName;
			//this.myDrawableId = drawId; 
			this.myListBgId = edu.uwcse.pond.proto.R.drawable.ol_bg_veggie_green;
			this.myBaseId = baseId; 
			this.myOrderId = orderId; 
			// this.myButtonDrawableId = buttonId;
			// this.myColorId = colorId; 
		}

		private static final Map<String,PointComponent> lookup_desc = new HashMap<String,PointComponent>();
		private static final Map<String,PointComponent> lookup_colname = new HashMap<String,PointComponent>();
		private static final Map<String,PointComponent> lookup_goal_colname = new HashMap<String,PointComponent>();

		static {
			for(PointComponent s : EnumSet.allOf(PointComponent.class)){
				lookup_desc.put(s.getDesc(), s);
				lookup_colname.put(s.getPtDbColName(), s);
				lookup_goal_colname.put(s.getGoalDbColName(), s);
			}
		}

		public int getDrawableId(){
			if (myDrawableId == -1){
				myDrawableId = getResId(myBaseId, edu.uwcse.pond.proto.R.drawable.class);
			}

			return myDrawableId;
			//return resources.getDrawable(id);
		}

		public int getOrderId(){
			return myOrderId;
		}

		public int getDrawableHalfId(){
			if (myHalfId == -1){
				myHalfId = getResId(myBaseId + "_half", edu.uwcse.pond.proto.R.drawable.class);
			}

			return myHalfId;
			//return resources.getDrawable(id);
		}

		public int getExtraDrawableId(){
			if (myExtraId == -1){
				myExtraId = getResId(myBaseId + "_extra", edu.uwcse.pond.proto.R.drawable.class);
			}

			return myExtraId;
		}

		public int getWhatDrawableId(){
			if (myWhatWhatId == -1){
				myWhatWhatId = getResId(myBaseId + "_dunno", edu.uwcse.pond.proto.R.drawable.class);
			}

			return myWhatWhatId;
		}

		public int getButtonBgId(){
			if (myButtonDrawableId == -1){
				myButtonDrawableId = getResId("button_bg_" + myBaseId, edu.uwcse.pond.proto.R.drawable.class);
			}
			return myButtonDrawableId;
		}

		public int getListBgId(){
			return myListBgId;
		}

		public int getColorId(){
			if (myColorId == -1){
				myColorId = getResId(myBaseId.toUpperCase(), edu.uwcse.pond.proto.R.color.class);
			}
			return myColorId;
		}


		public String getDesc() { return this.desc; }
		public String getPtDbColName() { return this.pts_col_name; }
		public String getGoalDbColName() { return this.goal_col_name; }

		public static PointComponent getFromDesc(String dsc) { 
			return lookup_desc.get(dsc); 
		}

		public static PointComponent getFromPtsColName(String pointsColName){
			return lookup_colname.get(pointsColName);
		}

		public static String getPtsColFromDesc(String dsc){
			return getFromDesc(dsc).getPtDbColName();
		}


		public static String getGoalColFromDesc(String dsc){
			return getFromDesc(dsc).getGoalDbColName();
		}

		public static int getResId(String variableName, Class<?> c) {

			try {
				Field idField = c.getDeclaredField(variableName);
				return idField.getInt(idField);
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			} 
		}

		/*
		   public static PointComponent getFromDesc(String dsc) { 
		        return lookup_desc.get(dsc); 
		   }*/
	}


}
