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


