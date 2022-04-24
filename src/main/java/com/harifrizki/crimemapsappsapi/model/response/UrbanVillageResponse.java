package com.harifrizki.crimemapsappsapi.model.response;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.harifrizki.crimemapsappsapi.model.PaginationModel;
import com.harifrizki.crimemapsappsapi.model.UrbanVillageModel;
import com.harifrizki.crimemapsappsapi.utils.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.harifrizki.crimemapsappsapi.utils.AppsConstants.*;

public class UrbanVillageResponse {

    @Getter @Setter
    private UrbanVillageModel urbanVillage;

    @Getter @Setter
    private List<UrbanVillageModel> urbanVillages = new ArrayList<>();

    @Getter @Setter
    private PaginationModel page;

    @Getter @Setter
    private GeneralMessageResponse message;

    public UrbanVillageResponse() {
    }

    public String toJson(UrbanVillageResponse response, int operation) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
        gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                if (operation == OPERATION_SELECT)
                    return fieldAttributes.getDeclaredClass() == UrbanVillageModel.class;
                else return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                if (operation == OPERATION_CRU)
                    return aClass == List.class ||
                            aClass == PaginationModel.class;
                else if (operation == OPERATION_DELETE)
                    return aClass == UrbanVillageModel.class ||
                            aClass == List.class ||
                            aClass == PaginationModel.class;
                else return false;
            }
        });
        Gson gson = gsonBuilder.create();
        return gson.toJson(response);
    }
}
