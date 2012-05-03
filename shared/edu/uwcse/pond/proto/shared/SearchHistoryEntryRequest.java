package edu.uwcse.pond.proto.shared;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

@ServiceName("edu.uwcse.pond.proto.server.POND_ProtoService")
public interface SearchHistoryEntryRequest extends RequestContext {

	Request<SearchHistoryEntryProxy> createSearchHistoryEntry();

	Request<SearchHistoryEntryProxy> readSearchHistoryEntry(Long id);

	Request<SearchHistoryEntryProxy> updateSearchHistoryEntry(
			SearchHistoryEntryProxy searchhistoryentry);

	Request<Void> deleteSearchHistoryEntry(
			SearchHistoryEntryProxy searchhistoryentry);

	Request<List<SearchHistoryEntryProxy>> querySearchHistoryEntrys();

}
