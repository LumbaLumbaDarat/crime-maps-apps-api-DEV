package com.harifrizki.crimemapsappsapi.service;

import com.harifrizki.crimemapsappsapi.entity.*;
import com.harifrizki.crimemapsappsapi.network.response.ImageStorageResponse;

public abstract class ResponseImageService {
    public void onResponse(AdminEntity admin,
                           ImageStorageResponse imageStorageResponse){}
    public void onResponse(CrimeLocationEntity crimeLocation,
                           ImageStorageResponse imageStorageResponse){}
    public void onResponse(ImageStorageResponse response){}
}
