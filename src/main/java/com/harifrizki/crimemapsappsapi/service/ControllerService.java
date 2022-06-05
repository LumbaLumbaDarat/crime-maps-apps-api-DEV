package com.harifrizki.crimemapsappsapi.service;

import com.harifrizki.crimemapsappsapi.entity.*;

import java.util.UUID;

public abstract class ControllerService {
    public AdminEntity checkEntityExistOrNot(UUID adminId) {
        return null;
    }
    public AdminEntity checkEntityExistOrNot(String adminUsername) {
        return null;
    }

    public ProvinceEntity     checkEntityExistOrNot(ProvinceEntity provinceEntity) {
        return null;
    }
    public CityEntity         checkEntityExistOrNot(CityEntity cityEntity) {
        return null;
    }
    public SubDistrictEntity  checkEntityExistOrNot(SubDistrictEntity subDistrictEntity) {
        return null;
    }
    public UrbanVillageEntity checkEntityExistOrNot(UrbanVillageEntity urbanVillageEntity) {
        return null;
    }

    public CrimeLocationEntity      checkEntityExistOrNot(CrimeLocationEntity crimeLocationEntity) { return null; }
    public ImageCrimeLocationEntity checkEntityExistOrNot(ImageCrimeLocationEntity imageCrimeLocationEntity) { return null; }
}
