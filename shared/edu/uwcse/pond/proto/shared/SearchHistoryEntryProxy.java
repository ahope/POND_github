package edu.uwcse.pond.proto.shared;

import java.util.Date;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyForName(value = "edu.uwcse.pond.proto.server.SearchHistoryEntry", locator = "edu.uwcse.pond.proto.server.SearchHistoryEntryLocator")
public interface SearchHistoryEntryProxy extends ValueProxy {

	Long getId();

	String getUserEmail();

	String getSearchTerm();

	Date getTimestamp();

	boolean getHit();

	String getComment();

	void setUserEmail(String val);

	void setSearchTerm(String val);

	void setTimestamp(Date val);

	void setHitInDb(boolean hit);

	void setComment(String val);

}
