package edu.uwcse.pond.proto.shared;

import java.util.Date;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyForName(value = "edu.uwcse.pond.proto.server.NutritionEntry", locator = "edu.uwcse.pond.proto.server.NutritionEntryLocator")
public interface NutritionEntryProxy extends ValueProxy {

	String getUserEmail();

	Date getTimeEntered();

	int getLocationId();

	String getLocationName();

	double getFruitVal();

	double getFruitWholeVal();

	double getVeggieVal();

	double getVeggieWholeVal();

	double getGrainsVal();

	double getGrainsWholeVal();

	double getProteinVal();

	double getDairyVal();

	double getSodiumVal();

	double getAlcoholVal();

	double getSugarVal();

	double getFatsVal();

	String getComment();

	void setUserEmail(String email);

	void setTimeEntered(Date time);

	void setLocationId(int locId);

	void setLocationName(String locName);

	void setFruitVal(double val);

	void setFruitWholeVal(double val);

	void setVeggieVal(double val);

	void setVeggieWholeVal(double val);

	void setGrainsVal(double val);

	void setGrainsWholeVal(double val);

	void setProteinVal(double val);

	void setDairyVal(double val);

	void setSodiumVal(double val);

	void setAlcoholVal(double val);

	void setSugarVal(double val);

	void setFatsVal(double val);

	void setComment(String com);

}
