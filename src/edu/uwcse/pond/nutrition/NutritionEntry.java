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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import edu.uwcse.pond.nutrition.Consts.FoodClass;
import edu.uwcse.pond.nutrition.Consts.Nutrient;
import edu.uwcse.pond.nutrition.Consts.PointComponent;
import edu.uwcse.pond.nutrition.Consts.PyramidCategory;
import edu.uwcse.pond.nutrition.Consts.ServingType;
import edu.uwcse.pond.nutrition.Consts.Unit;

/**
 * An object that contains ALL necessary/relevant information about a food from
 * the NutritionistPro database. Heavy-duty, but complete. All of the data in
 * this object should reflect the data in the NutritionistPro database.
 * 
 * @author aha
 * 
 */
public class NutritionEntry {

	/**
	 * An object that represents a Serving of a food. Most foods have multiple
	 * Servings.
	 * 
	 * @author aha
	 * 
	 */
	public class Serving {

		private ServingType mServingTypeId;
		private Unit mServingAmountUnitId;
		private double mServingAmountValue;
		private double mGramWeightValue;
		private String mServingAmtNote;

		public double getGramWtVal() {
			return this.mGramWeightValue;
		}

		public String getServingAmtNote() {
			return this.mServingAmtNote;
		}

		public Unit getServingAmtUnit() {
			return this.mServingAmountUnitId;
		}

		public double getServingAmtVal() {
			return this.mServingAmountValue;
		}

		public ServingType getServingType() {
			return this.mServingTypeId;
		}

		public void setGramWtVal(double val) {
			this.mGramWeightValue = val;
		}

		public void setServingAmtNote(String note) {
			this.mServingAmtNote = note;
		}

		public void setServingAmtUnit(int id) {
			this.mServingAmountUnitId = Unit.get(id);
		}

		public void setServingAmtVal(double val) {
			this.mServingAmountValue = val;
		}

		/**
		 * A "type" of serving, as used by NutritionistPro (typical, minimum,
		 * max, alternate...)
		 * 
		 * @param st
		 */
		public void setServingType(ServingType st) {
			this.mServingTypeId = st;
		}

		@Override
		public String toString() {
			return mServingAmountUnitId.getDescString();
		}
	}

	/**
	 * The id of the food in NutritionstPro
	 */
	private final int foodId;
	/**
	 * The name of the food
	 */
	private final String foodName;
	/**
	 * The manufacturer of this food (if any)
	 */
	private final String manufacturerName;
	/**
	 * A map of the nutritrient values of this food.
	 */
	private final Map<Nutrient, Double> nutrientVals;
	/**
	 * A map of the pyramid categories of this food.
	 */
	private final Map<PyramidCategory, Double> pyramidVals;
	/**
	 * True if this food is considered high in fat.
	 */
	private boolean isHighFat = false;
	/**
	 * True if this food is considered high in sugar.
	 */
	private boolean isHighSugar = false;
	/**
	 * The FoodClass for this food.
	 */
	private FoodClass mFoodClass;
	/**
	 * A List of the various Serving types that are available for this food.
	 */
	private final List<Serving> mServings = new ArrayList<Serving>();
	/**
	 * A Map representing the Points (POND) calculated for this food.
	 */
	private final Map<PointComponent, Double> mPointsMap = new HashMap<PointComponent, Double>();
	/**
	 * A conversion factor: The number of grams in a typical serving.
	 */
	private double typicalServingGramWt;
	/**
	 * For reference: The number of calories in a typical serving.
	 */
	private double mCaloriesPerTypicalServing = -1;

	/**
	 * 
	 * @param foodId
	 * @param foodName
	 * @param manufacturerName
	 */
	public NutritionEntry(int foodId, String foodName, String manufacturerName) {
		this.foodId = foodId;
		this.foodName = foodName;
		this.manufacturerName = manufacturerName;
		this.nutrientVals = new HashMap<Nutrient, Double>();
		this.pyramidVals = new HashMap<PyramidCategory, Double>();

	}

	public void addNutrientVal(Nutrient name, double val) {
		this.nutrientVals.put(name, val);
	}

	public void addPyramidVal(PyramidCategory cat, double val) {

		this.pyramidVals.put(cat, val);
	}

	public void addServing(Serving s) {
		if (s.getServingType() == ServingType.Typical) {
			this.typicalServingGramWt = s.getGramWtVal();
		}
		this.mServings.add(s);
	}

	// This is where the entire algorithm that computes points is located.
	// I put it here, even though it is probably more efficient elsewhere (ie,
	// building the points map while the other maps are being built), to contain
	// it all in one place until the algs are solidified.
	private void buildPointsMap() {
		// What to do with Solid Fats?

		try {
			if (nutrientVals.containsKey(Nutrient.SUGAR_TOTAL)) {
				if (mFoodClass != FoodClass.DAIRY
						&& mFoodClass != FoodClass.FRUIT) {
					mPointsMap
							.put(PointComponent.SUGAR,
									roundToHalf((nutrientVals
											.get(Nutrient.SUGAR_TOTAL) * 4) / 50.0));
				}
			}

			if (nutrientVals.containsKey(Nutrient.SODIUM)) {
				mPointsMap.put(PointComponent.SODIUM,
						roundToHalf(nutrientVals.get(Nutrient.SODIUM) / 250.0));
			}
			if (nutrientVals.containsKey(Nutrient.SATURATED_FAT)) {
				mPointsMap.put(PointComponent.SOLID_FATS,
						(nutrientVals.get(Nutrient.SATURATED_FAT) * 9) / 25.0);
			}

			double prot_pts = 0;
			if (pyramidVals.containsKey(PyramidCategory.MEAT_EGG)) {
				prot_pts = pyramidVals.get(PyramidCategory.MEAT_EGG);
			} else if (pyramidVals
					.containsKey(PyramidCategory.MEAT_FISH_POULTRY_TOFU_AND_PREPARED_MEAT)) {
				prot_pts = pyramidVals
						.get(PyramidCategory.MEAT_FISH_POULTRY_TOFU_AND_PREPARED_MEAT);
			} else if (pyramidVals.containsKey(PyramidCategory.MEAT_RAW)) {
				prot_pts = pyramidVals.get(PyramidCategory.MEAT_RAW);
			}
			mPointsMap.put(PointComponent.PROTEIN, roundToHalf(prot_pts));

			double other_veg_pts = 0; // 4,5, 13,16
			if (pyramidVals.containsKey(PyramidCategory.VEGETABLES_GREEN_PEAS)) {
				other_veg_pts = pyramidVals
						.get(PyramidCategory.VEGETABLES_GREEN_PEAS);
			} else if (pyramidVals
					.containsKey(PyramidCategory.VEGETABLES_OTHER_STARCHY)) {
				other_veg_pts = pyramidVals
						.get(PyramidCategory.VEGETABLES_OTHER_STARCHY);
			} else if (pyramidVals
					.containsKey(PyramidCategory.VEGETABLES_OTHER)) {
				other_veg_pts = pyramidVals
						.get(PyramidCategory.VEGETABLES_OTHER);
			} else if (pyramidVals
					.containsKey(PyramidCategory.VEGETABLE_STARCHY)) {
				other_veg_pts = pyramidVals
						.get(PyramidCategory.VEGETABLE_STARCHY);
			}
			mPointsMap.put(PointComponent.VEGGIE, roundToHalf(other_veg_pts));

			double dg_veg_pts = 0; // 2,3

			if (pyramidVals.containsKey(PyramidCategory.VEGETABLES_DARK_GREEN)) {
				dg_veg_pts = pyramidVals
						.get(PyramidCategory.VEGETABLES_DARK_GREEN);
			}
			if (pyramidVals.containsKey(PyramidCategory.VEGETABLES_DEEP_YELLOW)) {
				dg_veg_pts += pyramidVals
						.get(PyramidCategory.VEGETABLES_DEEP_YELLOW);
			}
			mPointsMap
					.put(PointComponent.VEGGIE_GREEN, roundToHalf(dg_veg_pts));

			double g_pts = 0; // 1
			if (pyramidVals
					.containsKey(PyramidCategory.BREAD_CEREAL_RICE_AND_PASTA)) {
				g_pts = pyramidVals
						.get(PyramidCategory.BREAD_CEREAL_RICE_AND_PASTA);
			} else if (pyramidVals.containsKey(PyramidCategory.BREAD)) {
				g_pts = pyramidVals.get(PyramidCategory.BREAD);
			}
			mPointsMap.put(PointComponent.GRAINS, roundToHalf(g_pts));

			// mPointsMap.put("WholeGrains Points",
			// (nutrientVals.get(Nutrient.SUGAR_TOTAL)*4)/50.0);

			double f_pts = 0; // 6, 15 //6, 15; Food Class ID: 86
			// if (pyramidVals.containsKey(PyramidCategory.FRUIT) &&
			// mFoodClass.
			if (mFoodClass == FoodClass.FRUIT_JUICE) {
				final Serving typicalSrv = getTypicalServing();
				switch (typicalSrv.mServingAmountUnitId) {
				case FL_OZ:
					f_pts = typicalSrv.mServingAmountValue / 4.0;
					break;
				case ML:
					f_pts = typicalSrv.mServingAmountValue / 120.0;
					break;
				default:

					break;
				}
				// 1 pt per 1/2 c. serving size. Base on Typical serving, 8 oz =
				// 2; 240 mL = 2;
			}

			mPointsMap.put(PointComponent.FRUIT, f_pts); // TODO: Fruit is now
															// Fruit Juice

			double wf_pts = 0;
			if (mFoodClass == FoodClass.FRUIT
					&& pyramidVals.containsKey(PyramidCategory.FRUIT)) {

				wf_pts = pyramidVals.get(PyramidCategory.FRUIT);
				mPointsMap.put(PointComponent.FRUIT_WHOLE, roundToHalf(wf_pts));
			} else {

			}
			mPointsMap.put(PointComponent.FRUIT_WHOLE, roundToHalf(wf_pts));

			double d_pts = 0; // 10,20
			if (pyramidVals.containsKey(PyramidCategory.MILK)) {
				d_pts = pyramidVals.get(PyramidCategory.MILK);
			} else if (pyramidVals
					.containsKey(PyramidCategory.MILK_YOGURT_AND_CHEESE)) {
				d_pts = pyramidVals.get(PyramidCategory.MILK_YOGURT_AND_CHEESE);
			}
			mPointsMap.put(PointComponent.DAIRY, roundToHalf(d_pts));
		} catch (final Exception e) {
			System.out.println(e.getStackTrace());
		}
	}

	/**
	 * Calculates the number of calories for the given number of servings
	 * 
	 * @param srvg
	 *            The desired serving type (eg, cups)
	 * @param numSrvgs
	 *            The desired serving size (eg, 2)
	 * @return The number of calories in the given serving size
	 */
	public double getCalories(Serving srvg, double numSrvgs) {
		final double adjust = srvg.getGramWtVal() / this.typicalServingGramWt;
		mCaloriesPerTypicalServing = nutrientVals.get(Nutrient.KILOCALORIES);
		return (mCaloriesPerTypicalServing * (adjust * numSrvgs));
	}

	public int getFoodId() {
		return this.foodId;
	}

	public String getFoodName() {
		return this.foodName;
	}

	public boolean getHighFat() {
		return this.isHighFat;
	}

	public boolean getHighSugar() {
		return this.isHighSugar;
	}

	public String getManufacturerName() {
		return this.manufacturerName;
	}

	public Map<Nutrient, Double> getNutrientsList() {
		return this.nutrientVals;
	}

	public double getNutrientVal(String type) {
		return this.nutrientVals.get(type);
	}

	public Map<PointComponent, Double> getPointsMap() {
		if (mPointsMap.keySet().size() == 0) {
			buildPointsMap();
		}
		return mPointsMap;
	}

	/**
	 * Given a serving size, returns a ContentValues object which contains the
	 * number of points calculated for each component.
	 * 
	 * @param srvg
	 * @param amt
	 * @return
	 */
	public ContentValues getPointsValsForAmount(Serving srvg, double amt) {
		final ContentValues vals = new ContentValues();
		// Key is DiaryDbHelper col
		getPointsMap(); // ensures pointsmap is created

		// get the keys
		final Set<PointComponent> keys = mPointsMap.keySet();

		final double srvg_mult = getServingMultiplier(srvg, amt);

		for (final Iterator<PointComponent> i = keys.iterator(); i.hasNext();) {
			final PointComponent key = i.next();
			vals.put(key.getPtDbColName(), mPointsMap.get(key) * srvg_mult);
		}

		return vals;
	}

	public Map<PyramidCategory, Double> getPyramidList() {
		return this.pyramidVals;
	}

	/**
	 * Calculates the specified pyramid value for a Typical Serving of this
	 * food.
	 * 
	 * @param category
	 * @return
	 */
	public double getPyramidVal(String category) {
		return this.pyramidVals.get(category);
	}

	/**
	 * 
	 * @param category
	 * @param srvg
	 * @return specified pyramid value for this food and the given serving
	 */
	public double getPyramidVal(String category, Serving srvg) {
		return this.getPyramidVal(category, srvg, 1.0);
	}

	/**
	 * 
	 * @param category
	 * @param srvg
	 * @param numSrvgs
	 * @return
	 */
	public double getPyramidVal(String category, Serving srvg, double numSrvgs) {
		final double adjust = srvg.getGramWtVal() / this.typicalServingGramWt;

		return (this.pyramidVals.get(category)) * (adjust * numSrvgs);
	}

	/**
	 * Returns the multiplier to be used to calculate values for different
	 * serving sizes.
	 * 
	 * @param whichServing
	 * @param numServings
	 * @return
	 */
	public double getServingMultiplier(Serving whichServing, double desAmt) {
		final double numServings = desAmt / whichServing.getServingAmtVal();
		return (numServings * (whichServing.getGramWtVal() / this.typicalServingGramWt));
	}

	public List<Serving> getServings() {
		return this.mServings;
	}

	public Serving getTypicalServing() {
		final Iterator<Serving> servings = mServings.iterator();
		while (servings.hasNext()) {
			final Serving serv = servings.next();
			if (serv.mServingTypeId == ServingType.Typical) {
				return serv;
			}
		}
		return null;
	}

	/***
	 * Rounds the given value to the nearest half. (1.25 -> 1.5, 1.24 -> 1).
	 * 
	 * @param val
	 * @return The nearest 1/2 value to the given val
	 */
	private double roundToHalf(double val) {
		final double wholeNum = Math.floor(val);
		final double remainder = val % 1.0;

		if (remainder < 0.25) {
			return wholeNum;
		}
		if (remainder < 0.75) {
			return wholeNum + 0.5;
		}
		return wholeNum + 1.0;
	}

	public void setCupGramWeight(double wt) {
	}

	public void setFoodClass(FoodClass id) {
		this.mFoodClass = id;
	}

	public void setHighFat(boolean bool) {
		this.isHighFat = bool;
	}

	public void setHighSugar(boolean bool) {
		this.isHighSugar = bool;
	}
}
