package com.harifrizki.crimemapsappsapi.service;

import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UploadImageService {
    void handshake();
    void uploadPhotoProfile(AdminEntity adminEntity, MultipartFile photoProfile);
}
