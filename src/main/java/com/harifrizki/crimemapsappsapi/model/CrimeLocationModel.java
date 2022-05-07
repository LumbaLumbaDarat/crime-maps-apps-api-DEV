package com.harifrizki.crimemapsappsapi.model;

import com.harifrizki.crimemapsappsapi.entity.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

import static com.harifrizki.crimemapsappsapi.utils.AppsConstants.EMPTY_STRING;

public class CrimeLocationModel {

    @Getter @Setter
    private UUID crimeLocationId;

    @Getter @Setter
    private ProvinceModel province;

    @Getter @Setter
    private CityModel city;

    @Getter @Setter
    private SubDistrictModel subDistrict;

    @Getter @Setter
    private UrbanVillageModel urbanVillage;

    @Getter @Setter
    private String crimeMapsName = EMPTY_STRING;

    @Getter @Setter
    private String crimeMapsAddress = EMPTY_STRING;

    @Getter @Setter
    private String crimeMapsDescription = EMPTY_STRING;

    @Getter @Setter
    private String crimeMapsLatitude = EMPTY_STRING;

    @Getter @Setter
    private String crimeMapsLongitude = EMPTY_STRING;

    @Getter @Setter
    private Set<ImageCrimeLocationModel> imageCrimeLocations;

    @Getter @Setter
    private double distance;

    @Getter @Setter
    private String distanceUnit;

    @Getter @Setter
    private AdminModel createdBy;

    @Getter @Setter
    private LocalDateTime createdDate;

    @Getter @Setter
    private AdminModel updatedBy;

    @Getter @Setter
    private LocalDateTime updatedDate;

    public CrimeLocationModel() {
    }

    public CrimeLocationModel convertFromEntityToModel(CrimeLocationEntity crimeLocationEntity) {
        CrimeLocationModel crimeLocation = new CrimeLocationModel();
        crimeLocation.setCrimeLocationId(crimeLocationEntity.getCrimeLocationId());
        crimeLocation.setCrimeMapsName(crimeLocationEntity.getCrimeMapsName());
        return crimeLocation;
    }

    public CrimeLocationModel convertFromEntityToModel(CrimeLocationEntity crimeLocationEntity,
                                                       ProvinceEntity provinceEntity,
                                                       CityEntity cityEntity,
                                                       SubDistrictEntity subDistrictEntity,
                                                       UrbanVillageEntity urbanVillageEntity,
                                                       Set<ImageCrimeLocationEntity> imageCrimeLocationEntities,
                                                       double distance,
                                                       String distanceUnit,
                                                       AdminEntity createdBy,
                                                       AdminEntity updatedBy) {
        CrimeLocationModel crimeLocation = new CrimeLocationModel();
        crimeLocation.setCrimeLocationId(crimeLocationEntity.getCrimeLocationId());

        if (provinceEntity != null)
            crimeLocation.setProvince(new ProvinceModel().convertFromEntityToModel(provinceEntity));
        else crimeLocation.setProvince(null);

        if (cityEntity != null)
            crimeLocation.setCity(new CityModel().convertFromEntityToModel(cityEntity));
        else crimeLocation.setCity(null);

        if (subDistrictEntity != null)
            crimeLocation.setSubDistrict(new SubDistrictModel().convertFromEntityToModel(subDistrictEntity));
        else crimeLocation.setSubDistrict(null);

        if (urbanVillageEntity != null)
            crimeLocation.setUrbanVillage(new UrbanVillageModel().convertFromEntityToModel(urbanVillageEntity));
        else crimeLocation.setUrbanVillage(null);

        crimeLocation.setCrimeMapsName(crimeLocationEntity.getCrimeMapsName());
        crimeLocation.setCrimeMapsAddress(crimeLocationEntity.getCrimeMapsAddress());
        crimeLocation.setCrimeMapsDescription(crimeLocationEntity.getCrimeMapsDescription());
        crimeLocation.setCrimeMapsLatitude(crimeLocationEntity.getCrimeMapsLatitude());
        crimeLocation.setCrimeMapsLongitude(crimeLocationEntity.getCrimeMapsLongitude());

        Set<ImageCrimeLocationModel> imageCrimeLocationModels = new HashSet<>();
        for (ImageCrimeLocationEntity imageCrimeLocationEntity : imageCrimeLocationEntities)
        {
            imageCrimeLocationModels.add(new ImageCrimeLocationModel().
                    convertFromEntityToModel(imageCrimeLocationEntity));
        }
        crimeLocation.setImageCrimeLocations(imageCrimeLocationModels);

        crimeLocation.setDistance(distance);
        crimeLocation.setDistanceUnit(distanceUnit);

        if (createdBy != null)
            crimeLocation.setCreatedBy(new AdminModel().convertFromEntityToModel(createdBy));
        else crimeLocation.setCreatedBy(null);

        crimeLocation.setCreatedDate(crimeLocationEntity.getCreatedDate());

        if (updatedBy != null)
            crimeLocation.setUpdatedBy(new AdminModel().convertFromEntityToModel(updatedBy));
        else crimeLocation.setUpdatedBy(null);

        crimeLocation.setUpdatedDate(crimeLocationEntity.getUpdatedDate());

        return crimeLocation;
    }
}
