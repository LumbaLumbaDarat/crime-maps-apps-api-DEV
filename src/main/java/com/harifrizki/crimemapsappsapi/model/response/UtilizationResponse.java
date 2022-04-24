package com.harifrizki.crimemapsappsapi.model.response;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.harifrizki.crimemapsappsapi.model.AdminModel;
import com.harifrizki.crimemapsappsapi.model.HandshakeModel;
import com.harifrizki.crimemapsappsapi.model.UtilizationModel;
import com.harifrizki.crimemapsappsapi.utils.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.harifrizki.crimemapsappsapi.utils.AppsConstants.*;

public class UtilizationResponse {

    @Getter @Setter
    private HandshakeModel handshake;

    @Getter @Setter
    private AdminModel login;

    @Getter @Setter
    private UtilizationModel utilization;

    @Getter @Setter
    private GeneralMessageResponse message;

    public UtilizationResponse() {
    }

    public String toJson(UtilizationResponse response, int operation) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
        gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                if (operation == OPERATION_LOGIN)
                    return aClass == HandshakeModel.class ||
                            aClass == UtilizationModel.class;
                else if (operation == OPERATION_LOGOUT)
                    return aClass == HandshakeModel.class ||
                            aClass == AdminModel.class ||
                            aClass == UtilizationModel.class;
                else if (operation == OPERATION_HANDSHAKE)
                    return aClass == AdminModel.class ||
                            aClass == UtilizationModel.class;
                else if (operation == OPERATION_UTILIZATION)
                    return aClass == HandshakeModel.class ||
                            aClass == AdminModel.class;
                else return false;
            }
        });
        Gson gson = gsonBuilder.create();
        return gson.toJson(response);
    }
}
