package com.harifrizki.crimemapsappsapi.controller;

import com.google.gson.Gson;
import com.harifrizki.crimemapsappsapi.entity.*;
import com.harifrizki.crimemapsappsapi.model.CrimeLocationModel;
import com.harifrizki.crimemapsappsapi.model.response.CrimeLocationResponse;
import com.harifrizki.crimemapsappsapi.model.response.GeneralMessageResponse;
import com.harifrizki.crimemapsappsapi.network.response.ImageStorageResponse;
import com.harifrizki.crimemapsappsapi.repository.*;
import com.harifrizki.crimemapsappsapi.service.ResponseImageService;
import com.harifrizki.crimemapsappsapi.service.impl.ImageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.harifrizki.crimemapsappsapi.utils.AppsConstants.*;
import static com.harifrizki.crimemapsappsapi.utils.AppsConstants.ENTITY_ADMIN;
import static com.harifrizki.crimemapsappsapi.utils.ControllerConstants.*;
import static com.harifrizki.crimemapsappsapi.utils.UtilizationClass.existEntityNotFound;
import static com.harifrizki.crimemapsappsapi.utils.UtilizationClass.successProcess;

@RestController
@RequestMapping(GENERAL_CONTROLLER_URL)
@Validated
public class CrimeLocationController {

    @Autowired
    private CrimeLocationRepository crimeLocationRepository;

    @Autowired
    private ImageCrimeLocationRepository imageCrimeLocationRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private SubDistrictRepository subDistrictRepository;

    @Autowired
    private UrbanVillageRepository urbanVillageRepository;

    @Autowired
    private ImageServiceImpl imageService;

    @Autowired
    private Environment environment;

    @RequestMapping(
            path = CRIME_LOCATION_ADD_CONTROLLER,
            method = RequestMethod.POST,
            consumes = {"multipart/form-data"})
    private String add(@Validated @RequestParam("crimeLocationEntity") String jsonCrimeLocationEntity,
                       @Validated @RequestParam("crimeLocationPhoto") MultipartFile[] crimeLocationPhoto) {
        CrimeLocationResponse response = new CrimeLocationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try
        {
            CrimeLocationEntity crimeLocationEntity = new Gson().fromJson(jsonCrimeLocationEntity, CrimeLocationEntity.class);
            AdminEntity createdBy = checkAdminWasExistOrNot(crimeLocationEntity.getCreatedBy());

            if (createdBy == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(crimeLocationEntity.getCreatedBy()),
                        "Created New",
                        environment.getProperty(ENTITY_CRIME_LOCATION)));
            } else {
                ProvinceEntity province = checkProvinceWasExistOrNot(
                        crimeLocationEntity.getProvince().getProvinceId());

                if (province == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_PROVINCE),
                            environment.getProperty(ENTITY_PROVINCE_ID),
                            String.valueOf(crimeLocationEntity.getProvince().getProvinceId()),
                            "Created New",
                            environment.getProperty(ENTITY_CRIME_LOCATION)));
                } else {
                    CityEntity city = checkCityWasExistOrNot(crimeLocationEntity.getCity().getCityId());

                    if (city == null)
                    {
                        message.setSuccess(false);
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_CITY),
                                environment.getProperty(ENTITY_CITY_ID),
                                String.valueOf(crimeLocationEntity.getProvince().getProvinceId()),
                                "Created New",
                                environment.getProperty(ENTITY_CRIME_LOCATION)));
                    } else {
                        SubDistrictEntity subDistrict = checkSubDistrictWasExistOrNot(
                                crimeLocationEntity.getSubDistrict().getSubDistrictId());

                        if (subDistrict == null)
                        {
                            message.setSuccess(false);
                            message.setMessage(existEntityNotFound(
                                    environment.getProperty(ENTITY_SUB_DISTRICT),
                                    environment.getProperty(ENTITY_SUB_DISTRICT_ID),
                                    String.valueOf(crimeLocationEntity.getProvince().getProvinceId()),
                                    "Created New",
                                    environment.getProperty(ENTITY_CRIME_LOCATION)));
                        } else {
                            UrbanVillageEntity urbanVillage = checkUrbanVillageWasExistOrNot(
                                    crimeLocationEntity.getUrbanVillage().getUrbanVillageId());

                            if (urbanVillage == null)
                            {
                                message.setSuccess(false);
                                message.setMessage(existEntityNotFound(
                                        environment.getProperty(ENTITY_URBAN_VILLAGE),
                                        environment.getProperty(ENTITY_URBAN_VILLAGE_ID),
                                        String.valueOf(crimeLocationEntity.getProvince().getProvinceId()),
                                        "Created New",
                                        environment.getProperty(ENTITY_CRIME_LOCATION)));
                            } else {
                                crimeLocationEntity.setCreatedBy(createdBy.getAdminId());
                                crimeLocationEntity.setCreatedDate(LocalDateTime.now());

                                CrimeLocationEntity result = crimeLocationRepository.save(crimeLocationEntity);

                                imageService.setResponseImageService(new ResponseImageService() {
                                    @Override
                                    public void onResponse(CrimeLocationEntity crimeLocation, ImageStorageResponse imageStorageResponse) {
                                        super.onResponse(crimeLocation, imageStorageResponse);
                                        if (imageStorageResponse.getSuccess())
                                        {
                                            List<ImageCrimeLocationEntity> imageCrimeLocationEntities = new ArrayList<>();
                                            for (String imageName : imageStorageResponse.getValues())
                                            {
                                                ImageCrimeLocationEntity imageCrimeLocationEntity = new ImageCrimeLocationEntity();
                                                imageCrimeLocationEntity.setCrimeLocationId(crimeLocation.getCrimeLocationId());
                                                imageCrimeLocationEntity.setImageName(imageName);
                                                imageCrimeLocationEntity.setCreatedBy(createdBy.getAdminId());
                                                imageCrimeLocationEntity.setCreatedDate(LocalDateTime.now());

                                                imageCrimeLocationEntities.add(imageCrimeLocationEntity);
                                            }

                                            response.setCrimeLocation(
                                                    new CrimeLocationModel().convertFromEntityToModel(
                                                            crimeLocation,
                                                            province,
                                                            city,
                                                            subDistrict,
                                                            urbanVillage,
                                                            imageCrimeLocationRepository.saveAll(imageCrimeLocationEntities),
                                                            createdBy, null));

                                            message.setSuccess(true);
                                            message.setMessage(
                                                    successProcess(
                                                            environment.getProperty(ENTITY_CRIME_LOCATION),
                                                            "Added New"));
                                        } else {
                                            crimeLocationRepository.delete(crimeLocation);

                                            message.setSuccess(false);
                                            message.setMessage(imageStorageResponse.getMessage());
                                        }
                                    }
                                });
                                imageService.upload(environment, result, crimeLocationPhoto);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    private AdminEntity checkAdminWasExistOrNot(UUID adminId) {
        return adminRepository.findById(adminId).orElse(null);
    }

    private ProvinceEntity checkProvinceWasExistOrNot(UUID provinceId) {
        return provinceRepository.findById(provinceId).orElse(null);
    }

    private CityEntity checkCityWasExistOrNot(UUID cityId) {
        return cityRepository.findById(cityId).orElse(null);
    }

    private SubDistrictEntity checkSubDistrictWasExistOrNot(UUID subDistrictId) {
        return subDistrictRepository.findById(subDistrictId).orElse(null);
    }

    private UrbanVillageEntity checkUrbanVillageWasExistOrNot(UUID urbanVillageId) {
        return urbanVillageRepository.findById(urbanVillageId).orElse(null);
    }
}
