package com.harifrizki.crimemapsappsapi.model;

import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import com.harifrizki.crimemapsappsapi.entity.CityEntity;
import com.harifrizki.crimemapsappsapi.entity.ProvinceEntity;
import com.harifrizki.crimemapsappsapi.entity.SubDistrictEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

public class SubDistrictModel {

    @Getter @Setter
    private UUID subDistrictId;

    @Getter @Setter
    private ProvinceModel province;

    @Getter @Setter
    private CityModel city;

    @Getter @Setter
    private String subDistrictName = "";

    @Getter @Setter
    private AdminModel createdBy;

    @Getter @Setter
    private LocalDateTime createdDate;

    @Getter @Setter
    private AdminModel updatedBy;

    @Getter @Setter
    private LocalDateTime updatedDate;

    public SubDistrictModel() {
    }

    public SubDistrictModel convertFromEntityToModel(SubDistrictEntity subDistrictEntity) {
        SubDistrictModel subDistrict = new SubDistrictModel();
        subDistrict.setSubDistrictId(subDistrictEntity.getSubDistrictId());
        subDistrict.setSubDistrictName(subDistrictEntity.getSubDistrictName());
        return subDistrict;
    }

    public SubDistrictModel convertFromEntityToModel(
            SubDistrictEntity subDistrictEntity,
            ProvinceEntity provinceEntity,
            CityEntity cityEntity,
            AdminEntity createdBy,
            AdminEntity updatedBy) {
        SubDistrictModel subDistrict = new SubDistrictModel();
        subDistrict.setSubDistrictId(subDistrictEntity.getSubDistrictId());

        if (provinceEntity != null)
            subDistrict.setProvince(new ProvinceModel().convertFromEntityToModel(provinceEntity));
        else subDistrict.setProvince(null);

        if (cityEntity != null)
            subDistrict.setCity(new CityModel().convertFromEntityToModel(cityEntity));
        else subDistrict.setCity(null);

        subDistrict.setSubDistrictName(subDistrictEntity.getSubDistrictName());

        if (createdBy != null)
            subDistrict.setCreatedBy(new AdminModel().convertFromEntityToModel(createdBy));
        else subDistrict.setCreatedBy(null);

        subDistrict.setCreatedDate(subDistrictEntity.getCreatedDate());

        if (updatedBy != null)
            subDistrict.setUpdatedBy(new AdminModel().convertFromEntityToModel(updatedBy));
        else subDistrict.setUpdatedBy(null);

        subDistrict.setUpdatedDate(subDistrictEntity.getUpdatedDate());
        return subDistrict;
    }
}
