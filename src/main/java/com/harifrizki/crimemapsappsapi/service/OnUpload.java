package com.harifrizki.crimemapsappsapi.service;

import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import com.harifrizki.crimemapsappsapi.network.response.ImageStorageResponse;

public abstract class OnUpload {
    public void onResponse(AdminEntity admin, ImageStorageResponse uploadResponse){}
    public void onResponse(ImageStorageResponse response){}
}
