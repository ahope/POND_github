package edu.uwcse.pond.proto.shared;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

@ServiceName("edu.uwcse.pond.proto.server.POND_ProtoService")
public interface LocationEntryRequest extends RequestContext {

	Request<LocationEntryProxy> createLocationEntry();

	Request<LocationEntryProxy> readLocationEntry(Long id);

	Request<LocationEntryProxy> updateLocationEntry(
			LocationEntryProxy locationentry);

	Request<Void> deleteLocationEntry(LocationEntryProxy locationentry);

	Request<List<LocationEntryProxy>> queryLocationEntrys();

}
