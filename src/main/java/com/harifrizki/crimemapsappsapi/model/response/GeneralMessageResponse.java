package com.harifrizki.crimemapsappsapi.model.response;

import lombok.Getter;
import lombok.Setter;

import static com.harifrizki.crimemapsappsapi.utils.AppsConstants.EMPTY_STRING;

public class GeneralMessageResponse {
    @Getter @Setter
    private boolean success = false;

    @Getter @Setter
    private String message = EMPTY_STRING;

    public GeneralMessageResponse() {
    }
}
