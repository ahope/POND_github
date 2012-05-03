package edu.uwcse.pond.proto.shared;

import java.util.Date;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyForName(value = "edu.uwcse.pond.proto.server.LocationEntry", locator = "edu.uwcse.pond.proto.server.LocationEntryLocator")
public interface LocationEntryProxy extends ValueProxy {

	Long getId();

	String getUserEmail();

	double getLongitude();

	double getLatitude();

	String getLocationName();

	Date getLastUpdate();

	Date getLastVisit();

	double getSize();

	String getComment();

	void setUserEmail(String email);

	void setLongitude(double lon);

	void setLatitude(double lat);

	void setLocationName(String name);

	void setLastUpdate(Date update);

	void setLastVisit(Date visit);

	void setSize(double siz);

	void setComment(String com);

}
