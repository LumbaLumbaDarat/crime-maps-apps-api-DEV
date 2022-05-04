package com.harifrizki.crimemapsappsapi.model;

import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import com.harifrizki.crimemapsappsapi.entity.SubDistrictEntity;
import com.harifrizki.crimemapsappsapi.entity.UrbanVillageEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

public class UrbanVillageModel {

    @Getter @Setter
    private UUID urbanVillageId;

    @Getter @Setter
    private SubDistrictModel subDistrict;

    @Getter @Setter
    private String urbanVillageName = "";

    @Getter @Setter
    private AdminModel createdBy;

    @Getter @Setter
    private LocalDateTime createdDate;

    @Getter @Setter
    private AdminModel updatedBy;

    @Getter @Setter
    private LocalDateTime updatedDate;

    public UrbanVillageModel() {
    }

    public UrbanVillageModel convertFromEntityToModel(UrbanVillageEntity urbanVillageEntity) {
        UrbanVillageModel urbanVillage = new UrbanVillageModel();
        urbanVillage.setUrbanVillageId(urbanVillageEntity.getUrbanVillageId());
        urbanVillage.setUrbanVillageName(urbanVillageEntity.getUrbanVillageName());
        return urbanVillage;
    }

    public UrbanVillageModel convertFromEntityToModel(
            UrbanVillageEntity urbanVillageEntity,
            SubDistrictEntity subDistrictEntity,
            AdminEntity createdBy,
            AdminEntity updatedBy) {
        UrbanVillageModel urbanVillage = new UrbanVillageModel();
        urbanVillage.setUrbanVillageId(urbanVillageEntity.getUrbanVillageId());

        if (subDistrictEntity != null)
            urbanVillage.setSubDistrict(new SubDistrictModel().convertFromEntityToModel(subDistrictEntity));
        else urbanVillage.setSubDistrict(null);

        urbanVillage.setUrbanVillageName(urbanVillageEntity.getUrbanVillageName());

        if (createdBy != null)
            urbanVillage.setCreatedBy(new AdminModel().convertFromEntityToModel(createdBy));
        else urbanVillage.setCreatedBy(null);

        urbanVillage.setCreatedDate(urbanVillageEntity.getCreatedDate());

        if (updatedBy != null)
            urbanVillage.setUpdatedBy(new AdminModel().convertFromEntityToModel(updatedBy));
        else urbanVillage.setUpdatedBy(null);

        urbanVillage.setUpdatedDate(urbanVillageEntity.getUpdatedDate());
        return urbanVillage;
    }
}
