package com.harifrizki.crimemapsappsapi.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.harifrizki.crimemapsappsapi.utils.AppsConstants.EMPTY_STRING;

@Entity
@Table(name = "admin")
public class AdminEntity {
    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    @Type(type="pg-uuid")
    @Getter @Setter
    private UUID adminId;

    @Getter @Setter
    @Column(name = "admin_name", columnDefinition="TEXT", nullable = false)
    private String adminName = EMPTY_STRING;

    @Getter @Setter
    @Column(name = "admin_username", nullable = false)
    private String adminUsername = EMPTY_STRING;

    @Getter @Setter
    @Column(name = "admin_role", nullable = false)
    private String adminRole = EMPTY_STRING;

    @Getter @Setter
    @Column(name = "admin_password", columnDefinition="TEXT", nullable = false)
    private String adminPassword = EMPTY_STRING;

    @Getter @Setter
    @Column(name = "admin_image", columnDefinition="TEXT", nullable = false)
    private String adminImage = EMPTY_STRING;

    @Getter @Setter
    @Column(name = "is_login", columnDefinition="BOOLEAN DEFAULT false")
    private boolean isLogin = false;

    @Getter @Setter
    @Column(name = "is_active", columnDefinition="BOOLEAN DEFAULT false")
    private boolean isActive = false;

    @Getter @Setter
    @Column(name = "created_by")
    private UUID createdBy;

    @Getter @Setter
    @Column(name = "created_date", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createdDate;

    @Getter @Setter
    @Column(name = "updated_by")
    private UUID updatedBy;

    @Getter @Setter
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;

    public AdminEntity() {
    }
}
