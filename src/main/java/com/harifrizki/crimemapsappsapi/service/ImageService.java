package com.harifrizki.crimemapsappsapi.service;

import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import com.harifrizki.crimemapsappsapi.entity.CrimeLocationEntity;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    void handshake();
    void upload(Environment environment, AdminEntity adminEntity, MultipartFile photoProfile);
    void upload(Environment environment, CrimeLocationEntity crimeLocationEntity, MultipartFile[] photoCrimeLocation);
    void update(Environment environment, AdminEntity adminEntity, AdminEntity createdBy, MultipartFile photoProfile);
    void delete(AdminEntity adminEntity);
}
