package com.harifrizki.crimemapsappsapi.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class ImageStorageResponse {

    @SerializedName("success")
    @Expose
    @Getter @Setter
    private Boolean success;

    @SerializedName("value")
    @Expose
    @Getter @Setter
    private String value;

    @SerializedName("values")
    @Expose
    @Getter @Setter
    private ArrayList<String> values;

    @SerializedName("message")
    @Expose
    @Getter @Setter
    private String message;
}
