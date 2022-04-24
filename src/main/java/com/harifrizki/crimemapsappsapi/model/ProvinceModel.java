package com.harifrizki.crimemapsappsapi.model;

import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import com.harifrizki.crimemapsappsapi.entity.ProvinceEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProvinceModel {

    @Getter @Setter
    private UUID provinceId;

    @Getter @Setter
    private String provinceName = "";

    @Getter @Setter
    private AdminModel createdBy;

    @Getter @Setter
    private LocalDateTime createdDate;

    @Getter @Setter
    private AdminModel updatedBy;

    @Getter @Setter
    private LocalDateTime updatedDate;

    public ProvinceModel() {
    }

    public ProvinceModel convertFromEntityToModel(ProvinceEntity provinceEntity) {
        ProvinceModel province = new ProvinceModel();
        province.setProvinceId(provinceEntity.getProvinceId());
        province.setProvinceName(provinceEntity.getProvinceName());
        return  province;
    }

    public ProvinceModel convertFromEntityToModel(
            ProvinceEntity provinceEntity,
            AdminEntity createdBy,
            AdminEntity updatedBy) {
        ProvinceModel province = new ProvinceModel();
        province.setProvinceId(provinceEntity.getProvinceId());
        province.setProvinceName(provinceEntity.getProvinceName());

        if (createdBy != null)
            province.setCreatedBy(new AdminModel().convertFromEntityToModel(createdBy));
        else province.setCreatedBy(null);

        province.setCreatedDate(provinceEntity.getCreatedDate());

        if (updatedBy != null)
            province.setUpdatedBy(new AdminModel().convertFromEntityToModel(updatedBy));
        else province.setUpdatedBy(null);

        province.setUpdatedDate(provinceEntity.getUpdatedDate());
        return province;
    }
}
