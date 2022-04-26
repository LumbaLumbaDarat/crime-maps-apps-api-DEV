package com.harifrizki.crimemapsappsapi.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "crime_location")
public class CrimeLocationEntity {

    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    @Type(type="pg-uuid")
    @Getter @Setter
    private UUID crimeLocationId;

    @Getter @Setter
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "province_id")
    private ProvinceEntity province = new ProvinceEntity();

    @Getter @Setter
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "city_id")
    private CityEntity city = new CityEntity();

    @Getter @Setter
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "sub_district_id")
    private SubDistrictEntity subDistrict = new SubDistrictEntity();

    @Getter @Setter
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "urban_village_id")
    private UrbanVillageEntity urbanVillage = new UrbanVillageEntity();

    @Getter @Setter
    @Column(name = "crime_maps_name", columnDefinition="TEXT", nullable = false)
    private String crimeMapsName = "";

    @Getter @Setter
    @Column(name = "crime_maps_address", columnDefinition="TEXT", nullable = false)
    private String crimeMapsAddress = "";

    @Getter @Setter
    @Column(name = "crime_maps_description", columnDefinition="TEXT", nullable = false)
    private String crimeMapsDescription = "";

    @Getter @Setter
    @Column(name = "crime_maps_latitude", nullable = false)
    private String crimeMapsLatitude = "";

    @Getter @Setter
    @Column(name = "crime_maps_longitude", nullable = false)
    private String crimeMapsLongitude = "";

    @Getter @Setter
    @Column(name = "crime_description", columnDefinition="TEXT", nullable = false)
    private String crimeDescription = "";

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

    public CrimeLocationEntity() {
    }
}
