package edu.uwcse.pond.proto.shared;

import com.google.web.bindery.requestfactory.shared.RequestFactory;


public interface POND_ProtoRequestFactory extends RequestFactory {

	LocationEntryRequest locationEntryRequest();

	SearchHistoryEntryRequest searchHistoryEntryRequest();

	NutritionEntryRequest nutritionEntryRequest();

}
