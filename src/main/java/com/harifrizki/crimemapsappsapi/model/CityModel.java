package com.harifrizki.crimemapsappsapi.model;

import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import com.harifrizki.crimemapsappsapi.entity.CityEntity;
import com.harifrizki.crimemapsappsapi.entity.ProvinceEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

public class CityModel {

    @Getter @Setter
    private UUID cityId;

    @Getter @Setter
    private ProvinceModel province;

    @Getter @Setter
    private String cityName = "";

    @Getter @Setter
    private AdminModel createdBy;

    @Getter @Setter
    private LocalDateTime createdDate;

    @Getter @Setter
    private AdminModel updatedBy;

    @Getter @Setter
    private LocalDateTime updatedDate;

    public CityModel() {
    }

    public CityModel convertFromEntityToModel(CityEntity cityEntity) {
        CityModel city = new CityModel();
        city.setCityId(cityEntity.getCityId());
        city.setCityName(cityEntity.getCityName());
        return city;
    }

    public CityModel convertFromEntityToModel(
            CityEntity cityEntity,
            ProvinceEntity provinceEntity,
            AdminEntity createdBy,
            AdminEntity updatedBy) {
        CityModel city = new CityModel();
        city.setCityId(cityEntity.getCityId());

        if (provinceEntity != null)
            city.setProvince(new ProvinceModel().convertFromEntityToModel(provinceEntity));
        else city.setProvince(null);

        city.setCityName(cityEntity.getCityName());

        if (createdBy != null)
            city.setCreatedBy(new AdminModel().convertFromEntityToModel(createdBy));
        else city.setCreatedBy(null);

        city.setCreatedDate(cityEntity.getCreatedDate());

        if (updatedBy != null)
            city.setUpdatedBy(new AdminModel().convertFromEntityToModel(updatedBy));
        else city.setUpdatedBy(null);

        city.setUpdatedDate(cityEntity.getUpdatedDate());
        return city;
    }
}
