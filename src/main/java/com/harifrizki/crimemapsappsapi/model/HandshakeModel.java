package com.harifrizki.crimemapsappsapi.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class HandshakeModel {

    @Getter @Setter
    private String urlApi = "";

    @Getter @Setter
    private String roleAdminRoot = "";

    @Getter @Setter
    private String roleAdmin = "";

    @Getter @Setter
    private String defaultImageAdmin = "";

    @Getter @Setter
    private String firstRootAdmin = "";

    @Getter @Setter
    private ArrayList<String> urlImageStorageApi = new ArrayList<>();

    public HandshakeModel() {
    }
}
