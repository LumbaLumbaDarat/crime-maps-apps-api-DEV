package com.harifrizki.crimemapsappsapi.model;

import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.harifrizki.crimemapsappsapi.utils.AppsConstants.EMPTY_STRING;

public class AdminModel {

    @Getter @Setter
    private UUID adminId;

    @Getter @Setter
    private String adminName = EMPTY_STRING;

    @Getter @Setter
    private String adminUsername = EMPTY_STRING;

    @Getter @Setter
    private String adminRole = EMPTY_STRING;

    @Getter @Setter
    private String adminImage = EMPTY_STRING;

    @Getter @Setter
    private Boolean isLogin;

    @Getter @Setter
    private Boolean isActive;

    @Getter @Setter
    private AdminModel createdBy;

    @Getter @Setter
    private LocalDateTime createdDate;

    @Getter @Setter
    private AdminModel updatedBy;

    @Getter @Setter
    private LocalDateTime updatedDate;

    public AdminModel() {
    }

    public AdminModel convertFromEntityToModel(AdminEntity adminEntity) {
        AdminModel admin = new AdminModel();
        admin.setAdminId(adminEntity.getAdminId());
        admin.setAdminName(adminEntity.getAdminName());
        admin.setAdminUsername(adminEntity.getAdminUsername());
        admin.setAdminRole(adminEntity.getAdminRole());
        admin.setAdminImage(null);
        return admin;
    }

    public AdminModel convertFromEntityToModel(AdminEntity adminEntity, AdminEntity createdBy, AdminEntity updatedBy) {
        AdminModel admin = new AdminModel();
        admin.setAdminId(adminEntity.getAdminId());
        admin.setAdminName(adminEntity.getAdminName());
        admin.setAdminUsername(adminEntity.getAdminUsername());
        admin.setAdminRole(adminEntity.getAdminRole());

        if (createdBy != null)
            admin.setCreatedBy(new AdminModel().convertFromEntityToModel(createdBy));
        else admin.setCreatedBy(null);

        admin.setCreatedDate(adminEntity.getCreatedDate());

        if (updatedBy != null)
            admin.setUpdatedBy(new AdminModel().convertFromEntityToModel(updatedBy));
        else admin.setUpdatedBy(null);

        admin.setUpdatedDate(adminEntity.getUpdatedDate());
        admin.setIsLogin(adminEntity.isLogin());
        admin.setIsActive(adminEntity.isActive());
        return admin;
    }
}
