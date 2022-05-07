package com.harifrizki.crimemapsappsapi.service;

import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import com.harifrizki.crimemapsappsapi.entity.CrimeLocationEntity;
import com.harifrizki.crimemapsappsapi.entity.ImageCrimeLocationEntity;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface ImageService {
    void handshake();
    void upload(Environment environment,
                AdminEntity adminEntity,
                MultipartFile photoProfile);
    void upload(Environment environment,
                CrimeLocationEntity crimeLocationEntity,
                MultipartFile[] photoCrimeLocation);
    void update(Environment environment,
                AdminEntity adminEntity,
                MultipartFile photoProfile);
    void delete(AdminEntity adminEntity);
    void delete(CrimeLocationEntity crimeLocationEntity);
    void delete(Set<ImageCrimeLocationEntity> imageCrimeLocationEntities);
}
