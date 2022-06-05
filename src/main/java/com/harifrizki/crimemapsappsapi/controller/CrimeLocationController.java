package com.harifrizki.crimemapsappsapi.controller;

import com.google.gson.Gson;
import com.harifrizki.crimemapsappsapi.entity.*;
import com.harifrizki.crimemapsappsapi.model.CrimeLocationModel;
import com.harifrizki.crimemapsappsapi.model.response.CrimeLocationResponse;
import com.harifrizki.crimemapsappsapi.model.response.GeneralMessageResponse;
import com.harifrizki.crimemapsappsapi.network.response.ImageStorageResponse;
import com.harifrizki.crimemapsappsapi.repository.*;
import com.harifrizki.crimemapsappsapi.service.ControllerService;
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
@RequestMapping(GENERAL_END_POINT)
@Validated
public class CrimeLocationController extends ControllerService {

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

    @PostMapping(END_POINT_CRIME_LOCATION)
    private String getAllCrimeLocation(@RequestParam String searchBy,
                                       @RequestParam int pageNo,
                                       @Validated @RequestBody CrimeLocationEntity crimeLocationEntity) {
        CrimeLocationResponse response = new CrimeLocationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            int realPage = pageNo - 1;
            Pageable pageable = PageRequest.
                    of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                            Sort.by(Sort.Direction.ASC, "crime_maps_name"));

            Page<CrimeLocationEntity> page = null;
            switch (searchBy) {
                case PARAM_NAME:
                case PARAM_NEAREST_LOCATION:
                    page = crimeLocationRepository.findByName(
                            pageable,
                            crimeLocationEntity.getCrimeMapsName());
                    break;
                case PARAM_PROVINCE_ID:
                    ProvinceEntity existProvince = checkEntityExistOrNot(
                            crimeLocationEntity.getProvince());

                    if (existProvince == null) {
                        message.setSuccess(false);
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_PROVINCE),
                                environment.getProperty(ENTITY_PROVINCE_ID),
                                String.valueOf(crimeLocationEntity.getProvince().getProvinceId()),
                                environment.getProperty(OPERATION_NAME_READ_ALL),
                                environment.getProperty(ENTITY_CRIME_LOCATION)));

                        response.setMessage(message);
                        return response.toJson(response, OPERATION_SELECT);
                    } else page = crimeLocationRepository.findByProvinceId(
                            pageable,
                            existProvince.getProvinceId(),
                            crimeLocationEntity.getCrimeMapsName());
                    break;
                case PARAM_CITY_ID:
                    CityEntity existCity = checkEntityExistOrNot(
                            crimeLocationEntity.getCity());

                    if (existCity == null) {
                        message.setSuccess(false);
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_CITY),
                                environment.getProperty(ENTITY_CITY_ID),
                                String.valueOf(crimeLocationEntity.getCity().getCityId()),
                                environment.getProperty(OPERATION_NAME_READ_ALL),
                                environment.getProperty(ENTITY_CRIME_LOCATION)));

                        response.setMessage(message);
                        return response.toJson(response, OPERATION_SELECT);
                    } else page = crimeLocationRepository.findByCityId(
                            pageable,
                            existCity.getCityId(),
                            crimeLocationEntity.getCrimeMapsName());
                    break;
                case PARAM_SUB_DISTRICT_ID:
                    SubDistrictEntity existSubDistrict = checkEntityExistOrNot(
                            crimeLocationEntity.getSubDistrict());

                    if (existSubDistrict == null) {
                        message.setSuccess(false);
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_SUB_DISTRICT),
                                environment.getProperty(ENTITY_SUB_DISTRICT_ID),
                                String.valueOf(crimeLocationEntity.getSubDistrict().getSubDistrictId()),
                                environment.getProperty(OPERATION_NAME_READ_ALL),
                                environment.getProperty(ENTITY_CRIME_LOCATION)));

                        response.setMessage(message);
                        return response.toJson(response, OPERATION_SELECT);
                    } else page = crimeLocationRepository.findBySubDistrictId(
                            pageable,
                            existSubDistrict.getSubDistrictId(),
                            crimeLocationEntity.getCrimeMapsName());
                    break;
                case PARAM_URBAN_VILLAGE_ID:
                    UrbanVillageEntity existUrbanVillage = checkEntityExistOrNot(
                            crimeLocationEntity.getUrbanVillage());

                    if (existUrbanVillage == null) {
                        message.setSuccess(false);
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_URBAN_VILLAGE),
                                environment.getProperty(ENTITY_URBAN_VILLAGE_ID),
                                String.valueOf(crimeLocationEntity.getUrbanVillage().getUrbanVillageId()),
                                environment.getProperty(OPERATION_NAME_READ_ALL),
                                environment.getProperty(ENTITY_CRIME_LOCATION)));

                        response.setMessage(message);
                        return response.toJson(response, OPERATION_SELECT);
                    } else page = crimeLocationRepository.findByUrbanVillageId(
                            pageable,
                            existUrbanVillage.getUrbanVillageId(),
                            crimeLocationEntity.getCrimeMapsName());
                    break;
            }

            ArrayList<CrimeLocationEntity> crimeLocationEntities = new ArrayList<>(page.getContent());

            ArrayList<CrimeLocationModel> crimeLocationModels = new ArrayList<>();
            for (CrimeLocationEntity crimeLocation : crimeLocationEntities) {
                ProvinceEntity province = null;
                if (crimeLocation.getProvince() != null)
                    province = checkEntityExistOrNot(crimeLocation.getProvince());

                CityEntity city = null;
                if (crimeLocation.getCity() != null)
                    city = checkEntityExistOrNot(crimeLocation.getCity());

                SubDistrictEntity subDistrict = null;
                if (crimeLocation.getSubDistrict() != null)
                    subDistrict = checkEntityExistOrNot(
                            crimeLocation.getSubDistrict());

                UrbanVillageEntity urbanVillage = null;
                if (crimeLocation.getUrbanVillage() != null)
                    urbanVillage = checkEntityExistOrNot(
                            crimeLocation.getUrbanVillage());

                AdminEntity createdBy = null;
                if (crimeLocation.getCreatedBy() != null)
                    createdBy = checkEntityExistOrNot(crimeLocation.getCreatedBy().getAdminId());

                AdminEntity updatedBy = null;
                if (crimeLocation.getUpdatedBy() != null)
                    updatedBy = checkEntityExistOrNot(crimeLocation.getUpdatedBy().getAdminId());

                if (searchBy.equalsIgnoreCase(PARAM_NEAREST_LOCATION)) {
                    double distance = crimeLocationService.calculateDistance(
                            Double.parseDouble(crimeLocationEntity.getCrimeMapsLatitude()),
                            Double.parseDouble(crimeLocationEntity.getCrimeMapsLongitude()),
                            Double.parseDouble(crimeLocation.getCrimeMapsLatitude()),
                            Double.parseDouble(crimeLocation.getCrimeMapsLongitude()));

                    if (distance <= DEFAULT_MAX_NEAREST_DISTANCE)
                        crimeLocationModels.add(new CrimeLocationModel().
                                convertFromEntityToModel(
                                        crimeLocation,
                                        province,
                                        city,
                                        subDistrict,
                                        urbanVillage,
                                        crimeLocation.getImageCrimeLocations(),
                                        distance,
                                        DEFAULT_DISTANCE_UNIT,
                                        createdBy,
                                        updatedBy));
                } else crimeLocationModels.add(new CrimeLocationModel().
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
                    pageNo, END_POINT_CRIME_LOCATION,
                    page, new String[]{PARAM_SEARCH_BY, PARAM_PAGE_NO}, new String[]{searchBy}));

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

    @PostMapping(END_POINT_DETAIL_CRIME_LOCATION)
    private String getCrimeLocation(@Validated @RequestBody CrimeLocationEntity crimeLocationEntity) {
        CrimeLocationResponse response = new CrimeLocationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            CrimeLocationEntity existCrimeLocation = checkEntityExistOrNot(
                    crimeLocationEntity);

            if (existCrimeLocation == null) {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_CRIME_LOCATION),
                        environment.getProperty(ENTITY_CRIME_LOCATION_ID),
                        String.valueOf(crimeLocationEntity.getCrimeLocationId())));
            } else {
                ProvinceEntity province = null;
                if (existCrimeLocation.getProvince() != null)
                    province = checkEntityExistOrNot(existCrimeLocation.getProvince());

                CityEntity city = null;
                if (existCrimeLocation.getCity() != null)
                    city = checkEntityExistOrNot(existCrimeLocation.getCity());

                SubDistrictEntity subDistrict = null;
                if (existCrimeLocation.getSubDistrict() != null)
                    subDistrict = checkEntityExistOrNot(
                            existCrimeLocation.getSubDistrict());

                UrbanVillageEntity urbanVillage = null;
                if (existCrimeLocation.getUrbanVillage() != null)
                    urbanVillage = checkEntityExistOrNot(
                            existCrimeLocation.getUrbanVillage());

                AdminEntity createdBy = null;
                if (existCrimeLocation.getCreatedBy() != null)
                    createdBy = checkEntityExistOrNot(existCrimeLocation.getCreatedBy().getAdminId());

                AdminEntity updatedBy = null;
                if (existCrimeLocation.getUpdatedBy() != null)
                    updatedBy = checkEntityExistOrNot(existCrimeLocation.getUpdatedBy().getAdminId());

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
            path = END_POINT_ADD_CRIME_LOCATION,
            method = RequestMethod.POST,
            consumes = {"multipart/form-data"})
    private String add(@Validated @RequestParam("crimeLocationEntity") String jsonCrimeLocationEntity,
                       @Validated @RequestParam("crimeLocationPhoto") MultipartFile[] crimeLocationPhoto) {
        CrimeLocationResponse response = new CrimeLocationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            CrimeLocationEntity crimeLocationEntity = new Gson().fromJson(jsonCrimeLocationEntity, CrimeLocationEntity.class);
            AdminEntity createdBy = checkEntityExistOrNot(crimeLocationEntity.getCreatedBy().getAdminId());

            if (createdBy == null) {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(crimeLocationEntity.getCreatedBy().getAdminId()),
                        environment.getProperty(OPERATION_NAME_CREATE),
                        environment.getProperty(ENTITY_CRIME_LOCATION)));
            } else {
                ProvinceEntity province = checkEntityExistOrNot(
                        crimeLocationEntity.getProvince());

                if (province == null) {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_PROVINCE),
                            environment.getProperty(ENTITY_PROVINCE_ID),
                            String.valueOf(crimeLocationEntity.getProvince().getProvinceId()),
                            environment.getProperty(OPERATION_NAME_CREATE),
                            environment.getProperty(ENTITY_CRIME_LOCATION)));
                } else {
                    CityEntity city = checkEntityExistOrNot(crimeLocationEntity.getCity());

                    if (city == null) {
                        message.setSuccess(false);
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_CITY),
                                environment.getProperty(ENTITY_CITY_ID),
                                String.valueOf(crimeLocationEntity.getProvince().getProvinceId()),
                                environment.getProperty(OPERATION_NAME_CREATE),
                                environment.getProperty(ENTITY_CRIME_LOCATION)));
                    } else {
                        SubDistrictEntity subDistrict = checkEntityExistOrNot(
                                crimeLocationEntity.getSubDistrict());

                        if (subDistrict == null) {
                            message.setSuccess(false);
                            message.setMessage(existEntityNotFound(
                                    environment.getProperty(ENTITY_SUB_DISTRICT),
                                    environment.getProperty(ENTITY_SUB_DISTRICT_ID),
                                    String.valueOf(crimeLocationEntity.getProvince().getProvinceId()),
                                    environment.getProperty(OPERATION_NAME_CREATE),
                                    environment.getProperty(ENTITY_CRIME_LOCATION)));
                        } else {
                            UrbanVillageEntity urbanVillage = checkEntityExistOrNot(
                                    crimeLocationEntity.getUrbanVillage());

                            if (urbanVillage == null) {
                                message.setSuccess(false);
                                message.setMessage(existEntityNotFound(
                                        environment.getProperty(ENTITY_URBAN_VILLAGE),
                                        environment.getProperty(ENTITY_URBAN_VILLAGE_ID),
                                        String.valueOf(crimeLocationEntity.getProvince().getProvinceId()),
                                        environment.getProperty(OPERATION_NAME_CREATE),
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
                                        if (imageStorageResponse.getSuccess()) {
                                            List<ImageCrimeLocationEntity> imageCrimeLocationEntities = new ArrayList<>();
                                            for (String imageName : imageStorageResponse.getValues()) {
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
                                                            environment.getProperty(OPERATION_NAME_CREATE)));
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
            path = END_POINT_ADD_IMAGE_CRIME_LOCATION,
            method = RequestMethod.POST,
            consumes = {"multipart/form-data"})
    private String addImageCrimeLocation(@Validated @RequestParam("crimeLocationEntity") String jsonCrimeLocationEntity,
                                         @Validated @RequestParam("crimeLocationPhoto") MultipartFile[] crimeLocationPhoto) {
        CrimeLocationResponse response = new CrimeLocationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            CrimeLocationEntity crimeLocationEntity = new Gson().fromJson(jsonCrimeLocationEntity, CrimeLocationEntity.class);
            CrimeLocationEntity existCrimeLocation = checkEntityExistOrNot(
                    crimeLocationEntity);
            AdminEntity updateBy = checkEntityExistOrNot(crimeLocationEntity.getUpdatedBy().getAdminId());

            if (existCrimeLocation == null) {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_CRIME_LOCATION),
                        environment.getProperty(ENTITY_CRIME_LOCATION_ID),
                        String.valueOf(crimeLocationEntity.getCrimeLocationId())));
            } else {
                if (updateBy == null) {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(crimeLocationEntity.getUpdatedBy().getAdminId()),
                            environment.getProperty(OPERATION_NAME_UPDATE),
                            environment.getProperty(ENTITY_IMAGE_CRIME_LOCATION)));
                } else {
                    imageService.setResponseImageService(new ResponseImageService() {
                        @Override
                        public void onResponse(CrimeLocationEntity crimeLocation, ImageStorageResponse imageStorageResponse) {
                            super.onResponse(crimeLocation, imageStorageResponse);
                            if (imageStorageResponse.getSuccess()) {
                                List<ImageCrimeLocationEntity> imageCrimeLocationEntities = new ArrayList<>();
                                for (String imageName : imageStorageResponse.getValues()) {
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
                                                environment.getProperty(ENTITY_IMAGE_CRIME_LOCATION),
                                                environment.getProperty(OPERATION_NAME_CREATE)));
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

    @PostMapping(END_POINT_UPDATE_CRIME_LOCATION)
    private String update(@Validated @RequestBody CrimeLocationEntity crimeLocationEntity) {
        CrimeLocationResponse response = new CrimeLocationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            CrimeLocationEntity existCrimeLocation = checkEntityExistOrNot(
                    crimeLocationEntity);
            AdminEntity updatedBy = checkEntityExistOrNot(crimeLocationEntity.getUpdatedBy().getAdminId());

            if (existCrimeLocation == null) {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_CRIME_LOCATION),
                        environment.getProperty(ENTITY_CRIME_LOCATION_ID),
                        String.valueOf(crimeLocationEntity.getCrimeLocationId())));
            } else {
                if (updatedBy == null) {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(crimeLocationEntity.getUpdatedBy().getAdminId()),
                            environment.getProperty(OPERATION_NAME_UPDATE),
                            environment.getProperty(ENTITY_CRIME_LOCATION)));
                } else {
                    ProvinceEntity province = checkEntityExistOrNot(
                            crimeLocationEntity.getProvince());

                    if (province == null) {
                        message.setSuccess(false);
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_PROVINCE),
                                environment.getProperty(ENTITY_PROVINCE_ID),
                                String.valueOf(crimeLocationEntity.getProvince().getProvinceId()),
                                environment.getProperty(OPERATION_NAME_UPDATE),
                                environment.getProperty(ENTITY_CRIME_LOCATION)));
                    } else {
                        CityEntity city = checkEntityExistOrNot(crimeLocationEntity.getCity());

                        if (city == null) {
                            message.setSuccess(false);
                            message.setMessage(existEntityNotFound(
                                    environment.getProperty(ENTITY_CITY),
                                    environment.getProperty(ENTITY_CITY_ID),
                                    String.valueOf(crimeLocationEntity.getProvince().getProvinceId()),
                                    environment.getProperty(OPERATION_NAME_UPDATE),
                                    environment.getProperty(ENTITY_CRIME_LOCATION)));
                        } else {
                            SubDistrictEntity subDistrict = checkEntityExistOrNot(
                                    crimeLocationEntity.getSubDistrict());

                            if (subDistrict == null) {
                                message.setSuccess(false);
                                message.setMessage(existEntityNotFound(
                                        environment.getProperty(ENTITY_SUB_DISTRICT),
                                        environment.getProperty(ENTITY_SUB_DISTRICT_ID),
                                        String.valueOf(crimeLocationEntity.getProvince().getProvinceId()),
                                        environment.getProperty(OPERATION_NAME_UPDATE),
                                        environment.getProperty(ENTITY_CRIME_LOCATION)));
                            } else {
                                UrbanVillageEntity urbanVillage = checkEntityExistOrNot(
                                        crimeLocationEntity.getUrbanVillage());

                                if (urbanVillage == null) {
                                    message.setSuccess(false);
                                    message.setMessage(existEntityNotFound(
                                            environment.getProperty(ENTITY_URBAN_VILLAGE),
                                            environment.getProperty(ENTITY_URBAN_VILLAGE_ID),
                                            String.valueOf(crimeLocationEntity.getProvince().getProvinceId()),
                                            environment.getProperty(OPERATION_NAME_UPDATE),
                                            environment.getProperty(ENTITY_CRIME_LOCATION)));
                                } else {
                                    AdminEntity createdBy = null;
                                    if (existCrimeLocation.getCreatedBy() != null)
                                        createdBy = checkEntityExistOrNot(
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
                                            environment.getProperty(OPERATION_NAME_UPDATE)));
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

    @PostMapping(END_POINT_DELETE_CRIME_LOCATION)
    private String delete(@Validated @RequestBody CrimeLocationEntity crimeLocationEntity) {
        CrimeLocationResponse response = new CrimeLocationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            CrimeLocationEntity existCrimeLocation = checkEntityExistOrNot(
                    crimeLocationEntity);

            if (existCrimeLocation == null) {
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
                        if (response.getSuccess()) {
                            crimeLocationRepository.delete(existCrimeLocation);

                            message.setSuccess(true);
                            message.setMessage(
                                    successProcess(
                                            environment.getProperty(ENTITY_CRIME_LOCATION),
                                            environment.getProperty(ENTITY_CRIME_LOCATION_ID),
                                            String.valueOf(crimeLocationEntity.getCrimeLocationId()),
                                            environment.getProperty(ENTITY_CRIME_LOCATION_NAME),
                                            existCrimeLocation.getCrimeMapsName(),
                                            environment.getProperty(OPERATION_NAME_DELETE)));
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

    @PostMapping(END_POINT_DELETE_IMAGE_CRIME_LOCATION)
    private String deleteImageCrimeLocation(@Validated @RequestBody CrimeLocationEntity crimeLocationEntity) {
        CrimeLocationResponse response = new CrimeLocationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            CrimeLocationEntity existCrimeLocation = checkEntityExistOrNot(
                    crimeLocationEntity);
            AdminEntity updateBy = checkEntityExistOrNot(crimeLocationEntity.getUpdatedBy().getAdminId());

            if (existCrimeLocation == null) {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_CRIME_LOCATION),
                        environment.getProperty(ENTITY_CRIME_LOCATION_ID),
                        String.valueOf(crimeLocationEntity.getCrimeLocationId())));
            } else {
                if (updateBy == null) {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(crimeLocationEntity.getUpdatedBy().getAdminId()),
                            environment.getProperty(OPERATION_NAME_DELETE),
                            environment.getProperty(ENTITY_IMAGE_CRIME_LOCATION)));
                } else {
                    Set<ImageCrimeLocationEntity> imageCrimeLocationEntities = new HashSet<>();
                    for (ImageCrimeLocationEntity imageCrimeLocationEntity :
                            crimeLocationEntity.getImageCrimeLocations()) {
                        ImageCrimeLocationEntity existImageCrimeLocation =
                                checkEntityExistOrNot(imageCrimeLocationEntity);

                        if (existImageCrimeLocation == null) {
                            message.setSuccess(false);
                            message.setMessage(existEntityNotFound(
                                    environment.getProperty(ENTITY_IMAGE_CRIME_LOCATION),
                                    environment.getProperty(ENTITY_IMAGE_CRIME_LOCATION_ID),
                                    String.valueOf(imageCrimeLocationEntity.getImageCrimeLocationId())));

                            response.setMessage(message);
                            return response.toJson(response, OPERATION_DELETE);
                        } else imageCrimeLocationEntities.add(existImageCrimeLocation);
                    }

                    imageService.setResponseImageService(new ResponseImageService() {
                        @Override
                        public void onResponse(ImageStorageResponse response) {
                            super.onResponse(response);
                            if (response.getSuccess()) {
                                List<ImageCrimeLocationEntity> imageCrimeLocationDeletes = new ArrayList<>();
                                for (ImageCrimeLocationEntity imageCrimeLocationEntity :
                                        imageCrimeLocationEntities) {
                                    for (String imageName : response.getValues()) {
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
                                                environment.getProperty(OPERATION_NAME_DELETE)));
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

    @Override
    public AdminEntity checkEntityExistOrNot(UUID adminId) {
        return adminRepository.findById(adminId).orElse(null);
    }

    @Override
    public ProvinceEntity checkEntityExistOrNot(ProvinceEntity provinceEntity) {
        return provinceRepository.findById(provinceEntity.getProvinceId()).orElse(null);
    }

    @Override
    public CityEntity checkEntityExistOrNot(CityEntity cityEntity) {
        return cityRepository.findById(cityEntity.getCityId()).orElse(null);
    }

    @Override
    public SubDistrictEntity checkEntityExistOrNot(SubDistrictEntity subDistrictEntity) {
        return subDistrictRepository.findById(subDistrictEntity.getSubDistrictId()).orElse(null);
    }

    @Override
    public UrbanVillageEntity checkEntityExistOrNot(UrbanVillageEntity urbanVillageEntity) {
        return urbanVillageRepository.findById(urbanVillageEntity.getUrbanVillageId()).orElse(null);
    }

    @Override
    public CrimeLocationEntity checkEntityExistOrNot(CrimeLocationEntity crimeLocationEntity) {
        return crimeLocationRepository.findById(crimeLocationEntity.getCrimeLocationId()).orElse(null);
    }

    @Override
    public ImageCrimeLocationEntity checkEntityExistOrNot(ImageCrimeLocationEntity imageCrimeLocationEntity) {
        return imageCrimeLocationRepository.findById(imageCrimeLocationEntity.getImageCrimeLocationId()).orElse(null);
    }
}
