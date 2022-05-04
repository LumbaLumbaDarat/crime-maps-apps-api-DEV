package com.harifrizki.crimemapsappsapi.model;

import com.harifrizki.crimemapsappsapi.entity.ImageCrimeLocationEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.harifrizki.crimemapsappsapi.utils.AppsConstants.EMPTY_STRING;

public class ImageCrimeLocationModel {

    @Getter @Setter
    private UUID imageCrimeLocationId;

    @Getter @Setter
    private CrimeLocationModel crimeLocation;

    @Getter @Setter
    private String imageName = EMPTY_STRING;

    @Getter @Setter
    private AdminModel createdBy;

    @Getter @Setter
    private LocalDateTime createdDate;

    @Getter @Setter
    private AdminModel updatedBy;

    @Getter @Setter
    private LocalDateTime updatedDate;

    public ImageCrimeLocationModel() {
    }

    public ImageCrimeLocationModel convertFromEntityToModel(ImageCrimeLocationEntity imageCrimeLocationEntity) {
        ImageCrimeLocationModel imageCrimeLocation = new ImageCrimeLocationModel();
        imageCrimeLocation.setImageCrimeLocationId(imageCrimeLocationEntity.getImageCrimeLocationId());
        imageCrimeLocation.setImageName(imageCrimeLocationEntity.getImageName());
        return imageCrimeLocation;
    }
}
