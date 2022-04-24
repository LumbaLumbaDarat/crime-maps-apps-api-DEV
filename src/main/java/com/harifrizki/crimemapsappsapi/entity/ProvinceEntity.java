package com.harifrizki.crimemapsappsapi.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "province")
public class ProvinceEntity {

    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    @Type(type="pg-uuid")
    @Getter @Setter
    private UUID provinceId;

    @Getter @Setter
    @Column(name = "province_name", columnDefinition="TEXT", nullable = false)
    private String provinceName = "";

    @Getter @Setter
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "created_by")
    private AdminEntity createdBy = new AdminEntity();

    @Getter @Setter
    @Column(name = "created_date", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createdDate;

    @Getter @Setter
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "updated_by")
    private AdminEntity updatedBy = new AdminEntity();

    @Getter @Setter
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;

    public ProvinceEntity() {
    }
}
