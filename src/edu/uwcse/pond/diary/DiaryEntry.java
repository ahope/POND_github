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

import java.util.Date;

public class DiaryEntry {
	

		private long id;
		
		
		private String userEmail;
		
		
		private String userId; 
		
		
		private Date timeEntered; 
		
		
		private int locationId; 
		
		
		private String locationName; 
		
		
		private double fruitVal; 
		
		
		private double fruitWholeVal; 
		
		
		private double veggieVal; 
		
		
		private double veggieWholeVal; 
		
		
		private double grainsVal; 
		
		
		private double grainsWholeVal;
		 
		
		private double proteinVal; 
		
		
		private double dairyVal; 
		
		
		private double sodiumVal; 
		
		
		
		private double sugarVal; 
		
		
		private double fatsVal; 
		
		
		private String comment; 
		
		public long getId(){
			return this.id;
		}
		
		
		public String getUserEmail(){
			return this.userEmail; 
		}
		
		public String getUserId(){
			return this.userId; 
		}
		
		public Date getTimeEntered(){
			return this.timeEntered;
		}
		
		public int getLocationId(){
			return this.locationId;
		}
		
		public String getLocationName(){
			return this.locationName; 
		}
		
		public double getFruitVal(){
			return this.fruitVal; 
		}

		public double getFruitWholeVal(){
			return this.fruitWholeVal;
		}
		
		public double getVeggieVal(){
			return this.veggieVal; 
		}
		
		public double getVeggieWholeVal(){
			return this.veggieWholeVal;
		}


		public double getGrainsVal(){
			return this.grainsVal;
		}
		
		public double getGrainsWholeVal(){
			return this.grainsWholeVal;
		}
		 
		public double getProteinVal(){
			return this.proteinVal; 
		}

		public double getDairyVal(){
			return this.dairyVal;
		}
		
		public double getSodiumVal(){
			return this.sodiumVal; 
		}
		
		
		public double getSugarVal(){
			return this.sugarVal; 
		}
		
		public double getFatsVal(){
			return this.fatsVal;
		}
		
		public String getComment(){
			return this.comment; 
		}
		
		
		public void setUserEmail(String email){
			this.userEmail = email; 
		}
		
		public void setTimeEntered(Date time){
			this.timeEntered = time;
		}
		
		public void setLocationId(int locId){
			this.locationId = locId;
		}
		
		public void setLocationName(String locName){
			this.locationName = locName; 
		}
		
		public void setFruitVal(double val){
			this.fruitVal = val; 
		}

		public void setFruitWholeVal(double val){
			this.fruitWholeVal = val;
		}
		
		public void setVeggieVal(double val){
			this.veggieVal = val; 
		}
		
		public void setVeggieWholeVal(double val){
			this.veggieWholeVal = val;
		}


		public void setGrainsVal(double val){
			this.grainsVal = val;
		}
		
		public void setGrainsWholeVal(double val){
			this.grainsWholeVal = val;
		}
		 
		public void setProteinVal(double val){
			this.proteinVal = val; 
		}

		public void setDairyVal(double val){
			this.dairyVal = val;
		}
		
		public void setSodiumVal(double val){
			this.sodiumVal = val; 
		}
		
		
		
		public void setSugarVal(double val){
			this.sugarVal = val; 
		}
		
		public void setFatsVal(double val){
			this.fatsVal = val;
		}
		
		public void setComment(String com){
			this.comment = com; 
		}
		
		public void setUserId(String uId){
			this.userId = uId; 
		}
	

}
