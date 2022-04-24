package com.harifrizki.crimemapsappsapi.model;

import lombok.Getter;
import lombok.Setter;

public class UtilizationModel {

    @Getter @Setter
    private Long countAdmin = 0L;

    @Getter @Setter
    private int countAdminToday = 0;

    @Getter @Setter
    private int countAdminMonth = 0;

    @Getter @Setter
    private Long countProvince = 0L;

    @Getter @Setter
    private int countProvinceToday = 0;

    @Getter @Setter
    private int countProvinceMonth = 0;

    @Getter @Setter
    private Long countCity = 0L;

    @Getter @Setter
    private int countCityToday = 0;

    @Getter @Setter
    private int countCityMonth = 0;

    @Getter @Setter
    private Long countSubDistrict = 0L;

    @Getter @Setter
    private int countSubDistrictToday = 0;

    @Getter @Setter
    private int countSubDistrictMonth = 0;

    @Getter @Setter
    private Long countUrbanVillage = 0L;

    @Getter @Setter
    private int countUrbanVillageToday = 0;

    @Getter @Setter
    private int countUrbanVillageMonth = 0;

    @Getter @Setter
    private Long countCrimeLocation = 0L;

    @Getter @Setter
    private int countCrimeLocationToday = 0;

    @Getter @Setter
    private int countCrimeLocationMonth = 0;

    public UtilizationModel() {
    }
}
