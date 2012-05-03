package edu.uwcse.pond.proto.shared;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

@ServiceName("edu.uwcse.pond.proto.server.POND_ProtoService")
public interface NutritionEntryRequest extends RequestContext {

	Request<NutritionEntryProxy> createNutritionEntry();

	Request<NutritionEntryProxy> readNutritionEntry(Long id);

	Request<NutritionEntryProxy> updateNutritionEntry(
			NutritionEntryProxy nutritionentry);

	Request<Void> deleteNutritionEntry(NutritionEntryProxy nutritionentry);

	Request<List<NutritionEntryProxy>> queryNutritionEntrys();

}
