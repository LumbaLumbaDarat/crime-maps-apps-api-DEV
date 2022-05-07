package com.harifrizki.crimemapsappsapi.controller;

import com.google.gson.Gson;
import com.harifrizki.crimemapsappsapi.entity.*;
import com.harifrizki.crimemapsappsapi.model.CrimeLocationModel;
import com.harifrizki.crimemapsappsapi.model.response.CrimeLocationResponse;
import com.harifrizki.crimemapsappsapi.model.response.GeneralMessageResponse;
import com.harifrizki.crimemapsappsapi.network.response.ImageStorageResponse;
import com.harifrizki.crimemapsappsapi.repository.*;
import com.harifrizki.crimemapsappsapi.service.ResponseImageService;
import com.harifrizki.crimemapsappsapi.service.impl.CrimeLocationServiceImpl;
import com.harifrizki.crimemapsappsapi.service.impl.ImageServiceImpl;
import com.harifrizki.crimemapsappsapi.service.impl.PaginationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static com.harifrizki.crimemapsappsapi.utils.AppsConstants.*;
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
    private CrimeLocationServiceImpl crimeLocationService;

    @Autowired
    private PaginationServiceImpl paginationService;

    @Autowired
    private Environment environment;

    @GetMapping(CRIME_LOCATION_GET_ALL_CONTROLLER)
    @ResponseBody
    private String getAllCrimeLocation(@RequestParam int pageNo) {
        CrimeLocationResponse response = new CrimeLocationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try
        {
            int realPage = pageNo - 1;
            Pageable pageable = PageRequest.
                    of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                            Sort.by(Sort.Direction.ASC, "crimeMapsName"));

            Page<CrimeLocationEntity> page = crimeLocationRepository.findAll(pageable);
            ArrayList<CrimeLocationEntity> crimeLocationEntities = new ArrayList<>(page.getContent());

            ArrayList<CrimeLocationModel> crimeLocationModels = new ArrayList<>();
            for (CrimeLocationEntity crimeLocationEntity : crimeLocationEntities)
            {
                ProvinceEntity province = null;
                if (crimeLocationEntity.getProvince() != null)
                    province = checkProvinceWasExistOrNot(crimeLocationEntity.getProvince().getProvinceId());

                CityEntity city = null;
                if (crimeLocationEntity.getCity() != null)
                    city = checkCityWasExistOrNot(crimeLocationEntity.getCity().getCityId());

                SubDistrictEntity subDistrict = null;
                if (crimeLocationEntity.getSubDistrict() != null)
                    subDistrict = checkSubDistrictWasExistOrNot(
                            crimeLocationEntity.getSubDistrict().getSubDistrictId());

                UrbanVillageEntity urbanVillage = null;
                if (crimeLocationEntity.getUrbanVillage() != null)
                    urbanVillage = checkUrbanVillageWasExistOrNot(
                            crimeLocationEntity.getUrbanVillage().getUrbanVillageId());

                AdminEntity createdBy = null;
                if (crimeLocationEntity.getCreatedBy() != null)
                    createdBy = checkAdminWasExistOrNot(crimeLocationEntity.getCreatedBy().getAdminId());

                AdminEntity updatedBy = null;
                if (crimeLocationEntity.getUpdatedBy() != null)
                    updatedBy = checkAdminWasExistOrNot(crimeLocationEntity.getUpdatedBy().getAdminId());

                crimeLocationModels.add(new CrimeLocationModel().
                        convertFromEntityToModel(
                                crimeLocationEntity,
                                province,
                                city,
                                subDistrict,
                                urbanVillage,
                                crimeLocationEntity.getImageCrimeLocations(),
                                0.0,
                                DEFAULT_DISTANCE_UNIT,
                                createdBy,
                                updatedBy));
            }

            response.setCrimeLocations(crimeLocationModels);
            response.setPage(paginationService.getPagination(
                    pageNo, CRIME_LOCATION_GET_ALL_CONTROLLER,
                    page, new String[]{"pageNo"}, new String[]{}));

            message.setSuccess(true);
            message.setMessage(
                    successProcess(
                            SUCCESS_SELECT_ALL,
                            environment.getProperty(ENTITY_CRIME_LOCATION)));
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_SELECT);
    }

    @PostMapping(CRIME_LOCATION_SEARCH_AREA_ID_CONTROLLER)
    private String getAllCrimeLocation(@RequestParam int pageNo,
                                       @Validated @RequestBody CrimeLocationEntity crimeLocationEntity) {
        CrimeLocationResponse response = new CrimeLocationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try
        {
            int realPage = pageNo - 1;
            Pageable pageable = PageRequest.
                    of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                            Sort.by(Sort.Direction.ASC, "crime_maps_name"));

            Page<CrimeLocationEntity> page = null;
            if (crimeLocationEntity.getProvince().getProvinceId() != null)
            {
                ProvinceEntity existProvince = checkProvinceWasExistOrNot(
                        crimeLocationEntity.getProvince().getProvinceId());

                if (existProvince == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_PROVINCE),
                            environment.getProperty(ENTITY_PROVINCE_ID),
                            String.valueOf(crimeLocationEntity.getProvince().getProvinceId()),
                            "Get All",
                            environment.getProperty(ENTITY_CRIME_LOCATION)));
                } else {
                    page = crimeLocationRepository.findByProvinceId(
                            pageable,
                            existProvince.getProvinceId(),
                            crimeLocationEntity.getCrimeMapsName());
                }
            } else if (crimeLocationEntity.getCity().getCityId() != null)
            {
                CityEntity existCity = checkCityWasExistOrNot(
                        crimeLocationEntity.getCity().getCityId());

                if (existCity == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_CITY),
                            environment.getProperty(ENTITY_CITY_ID),
                            String.valueOf(crimeLocationEntity.getProvince().getProvinceId()),
                            "Get All",
                            environment.getProperty(ENTITY_CRIME_LOCATION)));
                } else {
                    page = crimeLocationRepository.findByCityId(
                            pageable,
                            existCity.getCityId(),
                            crimeLocationEntity.getCrimeMapsName());
                }
            } else if (crimeLocationEntity.getSubDistrict().getSubDistrictId() != null)
            {
                SubDistrictEntity existSubDistrict = checkSubDistrictWasExistOrNot(
                        crimeLocationEntity.getSubDistrict().getSubDistrictId());

                if (existSubDistrict == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_SUB_DISTRICT),
                            environment.getProperty(ENTITY_SUB_DISTRICT_ID),
                            String.valueOf(crimeLocationEntity.getProvince().getProvinceId()),
                            "Get All",
                            environment.getProperty(ENTITY_CRIME_LOCATION)));
                } else {
                    page = crimeLocationRepository.findBySubDistrictId(
                            pageable,
                            existSubDistrict.getSubDistrictId(),
                            crimeLocationEntity.getCrimeMapsName());
                }
            } else if (crimeLocationEntity.getUrbanVillage().getUrbanVillageId() != null)
            {
                UrbanVillageEntity existUrbanVillage = checkUrbanVillageWasExistOrNot(
                        crimeLocationEntity.getUrbanVillage().getUrbanVillageId());

                if (existUrbanVillage == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_URBAN_VILLAGE),
                            environment.getProperty(ENTITY_URBAN_VILLAGE_ID),
                            String.valueOf(crimeLocationEntity.getProvince().getProvinceId()),
                            "Get All",
                            environment.getProperty(ENTITY_CRIME_LOCATION)));
                } else {
                    page = crimeLocationRepository.findByUrbanVillageId(
                            pageable,
                            existUrbanVillage.getUrbanVillageId(),
                            crimeLocationEntity.getCrimeMapsName());
                }
            } else {
                page = crimeLocationRepository.findByName(
                        pageable,
                        crimeLocationEntity.getCrimeMapsName());
            }

            ArrayList<CrimeLocationEntity> crimeLocationEntities = new ArrayList<>(page.getContent());

            ArrayList<CrimeLocationModel> crimeLocationModels = new ArrayList<>();
            for (CrimeLocationEntity crimeLocation : crimeLocationEntities)
            {
                ProvinceEntity province = null;
                if (crimeLocation.getProvince() != null)
                    province = checkProvinceWasExistOrNot(crimeLocation.getProvince().getProvinceId());

                CityEntity city = null;
                if (crimeLocation.getCity() != null)
                    city = checkCityWasExistOrNot(crimeLocation.getCity().getCityId());

                SubDistrictEntity subDistrict = null;
                if (crimeLocation.getSubDistrict() != null)
                    subDistrict = checkSubDistrictWasExistOrNot(
                            crimeLocation.getSubDistrict().getSubDistrictId());

                UrbanVillageEntity urbanVillage = null;
                if (crimeLocation.getUrbanVillage() != null)
                    urbanVillage = checkUrbanVillageWasExistOrNot(
                            crimeLocation.getUrbanVillage().getUrbanVillageId());

                AdminEntity createdBy = null;
                if (crimeLocation.getCreatedBy() != null)
                    createdBy = checkAdminWasExistOrNot(crimeLocation.getCreatedBy().getAdminId());

                AdminEntity updatedBy = null;
                if (crimeLocation.getUpdatedBy() != null)
                    updatedBy = checkAdminWasExistOrNot(crimeLocation.getUpdatedBy().getAdminId());

                crimeLocationModels.add(new CrimeLocationModel().
                        convertFromEntityToModel(
                                crimeLocation,
                                province,
                                city,
                                subDistrict,
                                urbanVillage,
                                crimeLocation.getImageCrimeLocations(),
                                0.0,
                                DEFAULT_DISTANCE_UNIT,
                                createdBy,
                                updatedBy));
            }

            response.setCrimeLocations(crimeLocationModels);
            response.setPage(paginationService.getPagination(
                    pageNo, CRIME_LOCATION_SEARCH_AREA_ID_CONTROLLER,
                    page, new String[]{"pageNo"}, new String[]{}));

            message.setSuccess(true);
            message.setMessage(
                    successProcess(
                            SUCCESS_SELECT_ALL,
                            environment.getProperty(ENTITY_CRIME_LOCATION)));
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_SELECT);
    }

    @PostMapping(CRIME_LOCATION_GET_ALL_NEAREST_LOCATION_CONTROLLER)
    private String getAllCrimeLocation(@RequestParam double userLatitude, @RequestParam double userLongitude) {
        CrimeLocationResponse response = new CrimeLocationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try
        {
            int count = 0;
            ArrayList<CrimeLocationModel> crimeLocationModels = new ArrayList<>();
            for (CrimeLocationEntity crimeLocationEntity : crimeLocationRepository.findAll())
            {
                if (count >= 10)
                    break;

                double distance = crimeLocationService.calculateDistance(
                        userLatitude,
                        userLongitude,
                        Double.parseDouble(crimeLocationEntity.getCrimeMapsLatitude()),
                        Double.parseDouble(crimeLocationEntity.getCrimeMapsLongitude()));
                if (distance <= DEFAULT_MAX_NEAREST_DISTANCE)
                {
                    count++;

                    crimeLocationModels.add(new CrimeLocationModel().
                            convertFromEntityToModel(
                                    crimeLocationEntity,
                                    crimeLocationEntity.getProvince(),
                                    crimeLocationEntity.getCity(),
                                    crimeLocationEntity.getSubDistrict(),
                                    crimeLocationEntity.getUrbanVillage(),
                                    crimeLocationEntity.getImageCrimeLocations(),
                                    distance,
                                    DEFAULT_DISTANCE_UNIT,
                                    crimeLocationEntity.getCreatedBy(),
                                    crimeLocationEntity.getUpdatedBy()));
                }
            }

            response.setCrimeLocations(crimeLocationModels);

            message.setSuccess(true);
            message.setMessage(
                    successProcess(
                            SUCCESS_SELECT_ALL,
                            environment.getProperty(ENTITY_CRIME_LOCATION)));
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_SELECT_DIST);
    }

    @PostMapping(CRIME_LOCATION_SEARCH_ID_CONTROLLER)
    private String getCrimeLocation(@Validated @RequestParam("crimeLocationId") UUID crimeLocationId) {
        CrimeLocationResponse response = new CrimeLocationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            CrimeLocationEntity existCrimeLocation = checkCrimeLocationWasExistOrNot(crimeLocationId);

            if (existCrimeLocation == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_CRIME_LOCATION),
                        environment.getProperty(ENTITY_CRIME_LOCATION_ID),
                        String.valueOf(crimeLocationId)));
            } else {
                ProvinceEntity province = null;
                if (existCrimeLocation.getProvince() != null)
                    province = checkProvinceWasExistOrNot(existCrimeLocation.getProvince().getProvinceId());

                CityEntity city = null;
                if (existCrimeLocation.getCity() != null)
                    city = checkCityWasExistOrNot(existCrimeLocation.getCity().getCityId());

                SubDistrictEntity subDistrict = null;
                if (existCrimeLocation.getSubDistrict() != null)
                    subDistrict = checkSubDistrictWasExistOrNot(
                            existCrimeLocation.getSubDistrict().getSubDistrictId());

                UrbanVillageEntity urbanVillage = null;
                if (existCrimeLocation.getUrbanVillage() != null)
                    urbanVillage = checkUrbanVillageWasExistOrNot(
                            existCrimeLocation.getUrbanVillage().getUrbanVillageId());

                AdminEntity createdBy = null;
                if (existCrimeLocation.getCreatedBy() != null)
                    createdBy = checkAdminWasExistOrNot(existCrimeLocation.getCreatedBy().getAdminId());

                AdminEntity updatedBy = null;
                if (existCrimeLocation.getUpdatedBy() != null)
                    updatedBy = checkAdminWasExistOrNot(existCrimeLocation.getUpdatedBy().getAdminId());

                response.setCrimeLocation(
                        new CrimeLocationModel().
                                convertFromEntityToModel(
                                        existCrimeLocation,
                                        province,
                                        city,
                                        subDistrict,
                                        urbanVillage,
                                        existCrimeLocation.getImageCrimeLocations(),
                                        0.0,
                                        DEFAULT_DISTANCE_UNIT,
                                        createdBy,
                                        updatedBy));

                message.setSuccess(true);
                message.setMessage(
                        successProcess(
                                SUCCESS_SELECT_DETAIL,
                                environment.getProperty(ENTITY_CRIME_LOCATION)));
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

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
            AdminEntity createdBy = checkAdminWasExistOrNot(crimeLocationEntity.getCreatedBy().getAdminId());

            if (createdBy == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(crimeLocationEntity.getCreatedBy().getAdminId()),
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
                                crimeLocationEntity.setCreatedBy(createdBy);
                                crimeLocationEntity.setCreatedDate(LocalDateTime.now());
                                crimeLocationEntity.setUpdatedBy(null);

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
                                                imageCrimeLocationEntity.setCrimeLocation(crimeLocation);
                                                imageCrimeLocationEntity.setImageName(imageName);
                                                imageCrimeLocationEntity.setCreatedBy(createdBy);
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
                                                            new HashSet<>(
                                                                    imageCrimeLocationRepository.
                                                                            saveAll(imageCrimeLocationEntities)),
                                                            0.0,
                                                            DEFAULT_DISTANCE_UNIT,
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

    @RequestMapping(
            path = CRIME_LOCATION_ADD_IMAGE_CONTROLLER,
            method = RequestMethod.POST,
            consumes = {"multipart/form-data"})
    private String addImageCrimeLocation(@Validated @RequestParam("crimeLocationEntity") String jsonCrimeLocationEntity,
                                         @Validated @RequestParam("crimeLocationPhoto") MultipartFile[] crimeLocationPhoto) {
        CrimeLocationResponse response = new CrimeLocationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try
        {
            CrimeLocationEntity crimeLocationEntity = new Gson().fromJson(jsonCrimeLocationEntity, CrimeLocationEntity.class);
            CrimeLocationEntity existCrimeLocation = checkCrimeLocationWasExistOrNot(
                    crimeLocationEntity.getCrimeLocationId());
            AdminEntity updateBy = checkAdminWasExistOrNot(crimeLocationEntity.getUpdatedBy().getAdminId());

            if (existCrimeLocation == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_CRIME_LOCATION),
                        environment.getProperty(ENTITY_CRIME_LOCATION_ID),
                        String.valueOf(crimeLocationEntity.getCrimeLocationId())));
            } else {
                if (updateBy == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(crimeLocationEntity.getUpdatedBy().getAdminId()),
                            "Updated Existing",
                            environment.getProperty(ENTITY_CRIME_LOCATION)));
                } else {
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
                                    imageCrimeLocationEntity.setCrimeLocation(crimeLocation);
                                    imageCrimeLocationEntity.setImageName(imageName);
                                    imageCrimeLocationEntity.setCreatedBy(updateBy);
                                    imageCrimeLocationEntity.setCreatedDate(LocalDateTime.now());

                                    imageCrimeLocationEntities.add(imageCrimeLocationEntity);
                                }

                                imageCrimeLocationRepository.saveAll(imageCrimeLocationEntities);

                                crimeLocation.setUpdatedBy(updateBy);
                                crimeLocation.setUpdatedDate(LocalDateTime.now());

                                CrimeLocationEntity result = crimeLocationRepository.save(crimeLocation);

                                response.setCrimeLocation(
                                        new CrimeLocationModel().convertFromEntityToModel(
                                                result,
                                                result.getProvince(),
                                                result.getCity(),
                                                result.getSubDistrict(),
                                                result.getUrbanVillage(),
                                                result.getImageCrimeLocations(),
                                                0.0,
                                                DEFAULT_DISTANCE_UNIT,
                                                result.getCreatedBy(),
                                                updateBy));

                                message.setSuccess(true);
                                message.setMessage(
                                        successProcess(
                                                environment.getProperty(ENTITY_CRIME_LOCATION),
                                                "Added New"));
                            } else {
                                message.setSuccess(false);
                                message.setMessage(imageStorageResponse.getMessage());
                            }
                        }
                    });
                    imageService.upload(environment, existCrimeLocation, crimeLocationPhoto);
                }
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @PostMapping(CRIME_LOCATION_UPDATE_CONTROLLER)
    private String update(@Validated @RequestBody CrimeLocationEntity crimeLocationEntity) {
        CrimeLocationResponse response = new CrimeLocationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            CrimeLocationEntity existCrimeLocation = checkCrimeLocationWasExistOrNot(
                    crimeLocationEntity.getCrimeLocationId());
            AdminEntity updatedBy = checkAdminWasExistOrNot(crimeLocationEntity.getUpdatedBy().getAdminId());

            if (existCrimeLocation == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_CRIME_LOCATION),
                        environment.getProperty(ENTITY_CRIME_LOCATION_ID),
                        String.valueOf(crimeLocationEntity.getCrimeLocationId())));
            } else {
                if (updatedBy == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(crimeLocationEntity.getUpdatedBy().getAdminId()),
                            "Updated Existing",
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
                                "Updated Existing",
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
                                    "Updated Existing",
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
                                        "Updated Existing",
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
                                            "Updated Existing",
                                            environment.getProperty(ENTITY_CRIME_LOCATION)));
                                } else {
                                    AdminEntity createdBy = null;
                                    if (existCrimeLocation.getCreatedBy() != null)
                                        createdBy = checkAdminWasExistOrNot(
                                                existCrimeLocation.getCreatedBy().getAdminId());

                                    existCrimeLocation.setProvince(province);
                                    existCrimeLocation.setCity(city);
                                    existCrimeLocation.setSubDistrict(subDistrict);
                                    existCrimeLocation.setUrbanVillage(urbanVillage);
                                    existCrimeLocation.setCrimeMapsName(crimeLocationEntity.getCrimeMapsName());
                                    existCrimeLocation.setCrimeMapsAddress(crimeLocationEntity.getCrimeMapsAddress());
                                    existCrimeLocation.setCrimeMapsDescription(
                                            crimeLocationEntity.getCrimeMapsDescription());
                                    existCrimeLocation.setCrimeMapsLatitude(
                                            crimeLocationEntity.getCrimeMapsLatitude());
                                    existCrimeLocation.setCrimeMapsLongitude(
                                            crimeLocationEntity.getCrimeMapsLongitude());
                                    existCrimeLocation.setUpdatedBy(updatedBy);
                                    existCrimeLocation.setUpdatedDate(LocalDateTime.now());

                                    response.setCrimeLocation(
                                            new CrimeLocationModel().convertFromEntityToModel(
                                                    crimeLocationRepository.save(existCrimeLocation),
                                                    province,
                                                    city,
                                                    subDistrict,
                                                    urbanVillage,
                                                    existCrimeLocation.getImageCrimeLocations(),
                                                    0.0,
                                                    DEFAULT_DISTANCE_UNIT,
                                                    createdBy,
                                                    updatedBy));

                                    message.setSuccess(true);
                                    message.setMessage(successProcess(
                                            environment.getProperty(ENTITY_CRIME_LOCATION),
                                            environment.getProperty(ENTITY_CRIME_LOCATION_ID),
                                            String.valueOf(crimeLocationEntity.getCrimeLocationId()),
                                            environment.getProperty(ENTITY_CRIME_LOCATION_NAME),
                                            existCrimeLocation.getCrimeMapsName(),
                                            "Updated"));
                                }
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

    @PostMapping(CRIME_LOCATION_DELETE_CONTROLLER)
    private String delete(@Validated @RequestBody CrimeLocationEntity crimeLocationEntity) {
        CrimeLocationResponse response = new CrimeLocationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            CrimeLocationEntity existCrimeLocation = checkCrimeLocationWasExistOrNot(
                    crimeLocationEntity.getCrimeLocationId());

            if (existCrimeLocation == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_CRIME_LOCATION),
                        environment.getProperty(ENTITY_CRIME_LOCATION_ID),
                        String.valueOf(crimeLocationEntity.getCrimeLocationId())));
            } else {
                imageService.setResponseImageService(new ResponseImageService() {
                    @Override
                    public void onResponse(ImageStorageResponse response) {
                        super.onResponse(response);
                        if (response.getSuccess())
                        {
                            crimeLocationRepository.delete(existCrimeLocation);

                            message.setSuccess(true);
                            message.setMessage(
                                    successProcess(
                                            environment.getProperty(ENTITY_CRIME_LOCATION),
                                            environment.getProperty(ENTITY_CRIME_LOCATION_ID),
                                            String.valueOf(crimeLocationEntity.getCrimeLocationId()),
                                            environment.getProperty(ENTITY_CRIME_LOCATION_NAME),
                                            existCrimeLocation.getCrimeMapsName(),
                                            "Deleted"));
                        } else {
                            message.setSuccess(false);
                            message.setMessage(response.getMessage());
                        }
                    }
                });
                imageService.delete(existCrimeLocation);
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_DELETE);
    }

    @PostMapping(CRIME_LOCATION_DELETE_IMAGE_CONTROLLER)
    private String deleteImageCrimeLocation(@Validated @RequestBody CrimeLocationEntity crimeLocationEntity) {
        CrimeLocationResponse response = new CrimeLocationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            CrimeLocationEntity existCrimeLocation = checkCrimeLocationWasExistOrNot(
                    crimeLocationEntity.getCrimeLocationId());
            AdminEntity updateBy = checkAdminWasExistOrNot(crimeLocationEntity.getUpdatedBy().getAdminId());

            if (existCrimeLocation == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_CRIME_LOCATION),
                        environment.getProperty(ENTITY_CRIME_LOCATION_ID),
                        String.valueOf(crimeLocationEntity.getCrimeLocationId())));
            } else {
                if (updateBy == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(crimeLocationEntity.getUpdatedBy().getAdminId()),
                            "Delete Existing",
                            environment.getProperty(ENTITY_CRIME_LOCATION)));
                } else {
                    Set<ImageCrimeLocationEntity> imageCrimeLocationEntities = new HashSet<>();
                    for (ImageCrimeLocationEntity imageCrimeLocationEntity :
                            crimeLocationEntity.getImageCrimeLocations())
                    {
                        ImageCrimeLocationEntity existImageCrimeLocation =
                                checkImageCrimeLocationWasExistOrNot(imageCrimeLocationEntity.getImageCrimeLocationId());

                        if (existImageCrimeLocation == null)
                        {
                            message.setSuccess(false);
                            message.setMessage(existEntityNotFound(
                                    environment.getProperty(ENTITY_IMAGE_CRIME_LOCATION),
                                    environment.getProperty(ENTITY_IMAGE_CRIME_LOCATION_ID),
                                    String.valueOf(imageCrimeLocationEntity.getImageCrimeLocationId())));
                            break;
                        } else imageCrimeLocationEntities.add(existImageCrimeLocation);
                    }

                    imageService.setResponseImageService(new ResponseImageService() {
                        @Override
                        public void onResponse(ImageStorageResponse response) {
                            super.onResponse(response);
                            if (response.getSuccess())
                            {
                                List<ImageCrimeLocationEntity> imageCrimeLocationDeletes = new ArrayList<>();
                                for (ImageCrimeLocationEntity imageCrimeLocationEntity :
                                        imageCrimeLocationEntities)
                                {
                                    for (String imageName : response.getValues())
                                    {
                                        if (imageCrimeLocationEntity.getImageName().equalsIgnoreCase(imageName))
                                            imageCrimeLocationDeletes.add(imageCrimeLocationEntity);
                                    }
                                }

                                imageCrimeLocationRepository.deleteAll(imageCrimeLocationDeletes);

                                existCrimeLocation.setUpdatedBy(updateBy);
                                existCrimeLocation.setUpdatedDate(LocalDateTime.now());

                                crimeLocationRepository.save(existCrimeLocation);

                                message.setSuccess(true);
                                message.setMessage(
                                        successProcess(
                                                environment.getProperty(ENTITY_IMAGE_CRIME_LOCATION),
                                                "Deleted All"));
                            } else {
                                message.setSuccess(false);
                                message.setMessage(response.getMessage());
                            }
                        }
                    });
                    imageService.delete(imageCrimeLocationEntities);
                }
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_DELETE);
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

    private CrimeLocationEntity checkCrimeLocationWasExistOrNot(UUID crimeLocationId) {
        return crimeLocationRepository.findById(crimeLocationId).orElse(null);
    }

    private ImageCrimeLocationEntity checkImageCrimeLocationWasExistOrNot(UUID imageCrimeLocationId) {
        return imageCrimeLocationRepository.findById(imageCrimeLocationId).orElse(null);
    }
}
