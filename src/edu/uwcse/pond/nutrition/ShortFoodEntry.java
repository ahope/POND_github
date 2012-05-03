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

public class ShortFoodEntry {

	private String foodName; 
	private int foodId; 
	private String manufacturerName; 
	private int manufacturerId; 
	private boolean isCommon; 
	
	public ShortFoodEntry(String fName, int fId, 
						String mName, int mId, 
						boolean isCom){
		this.foodName = fName; 
		this.foodId = fId; 
		this.manufacturerName = mName; 
		this.manufacturerId = mId; 
		this.isCommon = isCom; 
	}
	
	public String getFoodName(){
		return foodName; 
	}
	
	public String getManufacturerName(){
		return manufacturerName; 
	}
	
	public int getFoodId(){
		return foodId; 
	}
	
	public int getManufacturerId(){
		return manufacturerId; 
	}
	
	public boolean isCommon(){
		return isCommon;
	}
	
}


