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

public class GoalEntry{


	

			private long id;
			
			
			
			
			private Date timeEntered; 
			
			
			
			private double fruitGoal; 
			
			
			private double fruitWholeGoal; 
			
			
			private double veggieGoal; 
			
			
			private double veggieWholeGoal; 
			
			
			private double grainsGoal; 
			
			
			private double grainsWholeGoal;
			 
			
			private double proteinGoal; 
			
			
			private double dairyGoal; 
			
			
			private double sodiumGoal; 
			
			
			private double alcoholGoal; 
			
			
			private double sugarGoal; 
			
			
			private double fatsGoal; 
			
			private double solidFatGoal; 
			
			private double oilsGoal; 
			
			
			
			public long getId(){
				return this.id;
			}
			
			
			public Date getTimeEntered(){
				return this.timeEntered;
			}
			
			
			public double getFruitGoal(){
				return this.fruitGoal; 
			}

			public double getFruitWholeGoal(){
				return this.fruitWholeGoal;
			}
			
			public double getVeggieGoal(){
				return this.veggieGoal; 
			}
			
			public double getVeggieWholeGoal(){
				return this.veggieWholeGoal;
			}


			public double getGrainsGoal(){
				return this.grainsGoal;
			}
			
			public double getGrainsWholeGoal(){
				return this.grainsWholeGoal;
			}
			 
			public double getProteinGoal(){
				return this.proteinGoal; 
			}

			public double getDairyGoal(){
				return this.dairyGoal;
			}
			
			public double getSodiumGoal(){
				return this.sodiumGoal; 
			}
			
			public double getAlcoholGoal(){
				return this.alcoholGoal; 
			}
			
			public double getSugarGoal(){
				return this.sugarGoal; 
			}
			
			public double getFatsGoal(){
				return this.fatsGoal;
			}
			
			public double getSolidFatGoal(){
				return this.solidFatGoal; 
			}

			public double getOilsGoal(){
				return this.oilsGoal; 
			}

			
			public void setTimeEntered(Date time){
				this.timeEntered = time;
			}
			
		
			public void setFruitGoal(double val){
				this.fruitGoal = val; 
			}

			public void setFruitWholeGoal(double val){
				this.fruitWholeGoal = val;
			}
			
			public void setVeggieGoal(double val){
				this.veggieGoal = val; 
			}
			
			public void setVeggieWholeGoal(double val){
				this.veggieWholeGoal = val;
			}


			public void setGrainsGoal(double val){
				this.grainsGoal = val;
			}
			
			public void setGrainsWholeGoal(double val){
				this.grainsWholeGoal = val;
			}
			 
			public void setProteinGoal(double val){
				this.proteinGoal = val; 
			}

			public void setDairyGoal(double val){
				this.dairyGoal = val;
			}
			
			public void setSodiumGoal(double val){
				this.sodiumGoal = val; 
			}
			
			public void setAlcoholGoal(double val){
				this.alcoholGoal = val; 
			}
			
			public void setSugarGoal(double val){
				this.sugarGoal = val; 
			}
			
			public void setFatsGoal(double val){
				this.fatsGoal = val;
			}
			
			public void setSolidFatsGoal(double val){
				this.solidFatGoal = val; 
			}
			
			public void setOilsGoal(double val){
				this.oilsGoal = val;
			}
	
}