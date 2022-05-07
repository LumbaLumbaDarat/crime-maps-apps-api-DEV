package com.harifrizki.crimemapsappsapi.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "image_crime_location")
public class ImageCrimeLocationEntity {

    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    @Type(type="pg-uuid")
    @Getter @Setter
    private UUID imageCrimeLocationId;

    @Getter @Setter
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="crime_location_id", nullable=false)
    private CrimeLocationEntity crimeLocation = new CrimeLocationEntity();

    @Getter @Setter
    @Column(name = "image_name", columnDefinition="TEXT", nullable = false)
    private String imageName = "";

    @Getter @Setter
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "created_by")
    private AdminEntity createdBy = new AdminEntity();

    @Getter @Setter
    @Column(name = "created_date", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createdDate;

    public ImageCrimeLocationEntity() {
    }
}
