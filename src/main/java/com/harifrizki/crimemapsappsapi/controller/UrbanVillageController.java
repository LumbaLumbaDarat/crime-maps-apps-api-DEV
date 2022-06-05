package com.harifrizki.crimemapsappsapi.controller;

import com.harifrizki.crimemapsappsapi.entity.*;
import com.harifrizki.crimemapsappsapi.model.UrbanVillageModel;
import com.harifrizki.crimemapsappsapi.model.response.GeneralMessageResponse;
import com.harifrizki.crimemapsappsapi.model.response.UrbanVillageResponse;
import com.harifrizki.crimemapsappsapi.repository.*;
import com.harifrizki.crimemapsappsapi.service.ControllerService;
import com.harifrizki.crimemapsappsapi.service.impl.PaginationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static com.harifrizki.crimemapsappsapi.utils.AppsConstants.*;
import static com.harifrizki.crimemapsappsapi.utils.ControllerConstants.*;
import static com.harifrizki.crimemapsappsapi.utils.UtilizationClass.existEntityNotFound;
import static com.harifrizki.crimemapsappsapi.utils.UtilizationClass.successProcess;

@RestController
@RequestMapping(GENERAL_END_POINT)
@Validated
public class UrbanVillageController extends ControllerService {

    @Autowired
    private UrbanVillageRepository urbanVillageRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private SubDistrictRepository subDistrictRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PaginationServiceImpl paginationService;

    @Autowired
    private Environment environment;

    @PostMapping(END_POINT_URBAN_VILLAGE)
    private String getAllUrbanVillage(@RequestParam String searchBy,
                                      @RequestParam int pageNo,
                                      @Validated @RequestBody UrbanVillageEntity urbanVillageEntity) {
        UrbanVillageResponse response = new UrbanVillageResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            int realPage = pageNo - 1;
            Pageable pageable = PageRequest.
                    of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                            Sort.by(Sort.Direction.ASC, "urban_village_name"));

            Page<UrbanVillageEntity> page = null;
            switch (searchBy)
            {
                case PARAM_NAME:
                    page = urbanVillageRepository.findByName(
                            pageable,
                            urbanVillageEntity.getUrbanVillageName());
                    break;
                case PARAM_PROVINCE_ID:
                    ProvinceEntity existProvince = checkEntityExistOrNot(
                            urbanVillageEntity.getProvince());

                    if (existProvince == null)
                    {
                        message.setSuccess(false);
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_PROVINCE),
                                environment.getProperty(ENTITY_PROVINCE_ID),
                                String.valueOf(urbanVillageEntity.getProvince().getProvinceId()),
                                environment.getProperty(OPERATION_NAME_READ_ALL),
                                environment.getProperty(ENTITY_URBAN_VILLAGE)));

                        response.setMessage(message);
                        return response.toJson(response, OPERATION_SELECT);
                    } else page = urbanVillageRepository.findByProvinceId(
                            pageable,
                            urbanVillageEntity.getProvince().getProvinceId(),
                            urbanVillageEntity.getUrbanVillageName());
                    break;
                case PARAM_CITY_ID:
                    CityEntity existCity = checkEntityExistOrNot(urbanVillageEntity.getCity());

                    if (existCity == null)
                    {
                        message.setSuccess(false);
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_CITY),
                                environment.getProperty(ENTITY_CITY_ID),
                                String.valueOf(urbanVillageEntity.getCity().getCityId()),
                                environment.getProperty(OPERATION_NAME_READ_ALL),
                                environment.getProperty(ENTITY_URBAN_VILLAGE)));

                        response.setMessage(message);
                        return response.toJson(response, OPERATION_SELECT);
                    } else page = urbanVillageRepository.findByCityId(
                            pageable,
                            urbanVillageEntity.getCity().getCityId(),
                            urbanVillageEntity.getUrbanVillageName());
                    break;
                case PARAM_SUB_DISTRICT_ID:
                    SubDistrictEntity existSubDistrict = checkEntityExistOrNot(
                            urbanVillageEntity.getSubDistrict());

                    if (existSubDistrict == null)
                    {
                        message.setSuccess(false);
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_SUB_DISTRICT),
                                environment.getProperty(ENTITY_SUB_DISTRICT_ID),
                                String.valueOf(urbanVillageEntity.getSubDistrict().getSubDistrictId()),
                                environment.getProperty(OPERATION_NAME_READ_ALL),
                                environment.getProperty(ENTITY_URBAN_VILLAGE)));

                        response.setMessage(message);
                        return response.toJson(response, OPERATION_SELECT);
                    } else page = urbanVillageRepository.findBySubDistrictId(
                            pageable,
                            urbanVillageEntity.getSubDistrict().getSubDistrictId(),
                            urbanVillageEntity.getUrbanVillageName());
                    break;
            }

            ArrayList<UrbanVillageEntity> urbanVillageEntities = new ArrayList<>(page.getContent());

            ArrayList<UrbanVillageModel> urbanVillageModels = new ArrayList<>();
            for (UrbanVillageEntity urbanVillage : urbanVillageEntities) {
                urbanVillageModels.add(new UrbanVillageModel().
                        convertFromEntityToModel(
                                urbanVillage,
                                urbanVillage.getProvince(),
                                urbanVillage.getCity(),
                                urbanVillage.getSubDistrict(),
                                urbanVillage.getCreatedBy(),
                                urbanVillage.getUpdatedBy())
                );
            }

            response.setUrbanVillages(urbanVillageModels);
            response.setPage(paginationService.getPagination(
                    pageNo, END_POINT_URBAN_VILLAGE,
                    page, new String[]{PARAM_SEARCH_BY, PARAM_PAGE_NO}, new String[]{searchBy}));

            message.setSuccess(true);
            message.setMessage(
                    successProcess(
                            SUCCESS_SELECT_ALL,
                            environment.getProperty(ENTITY_URBAN_VILLAGE)));
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_SELECT);
    }

    @PostMapping(END_POINT_DETAIL_URBAN_VILLAGE)
    private String getUrbanVillage(@Validated @RequestBody UrbanVillageEntity urbanVillageEntity) {
        UrbanVillageResponse response = new UrbanVillageResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            UrbanVillageEntity existUrbanVillage = checkEntityExistOrNot(
                    urbanVillageEntity);

            if (existUrbanVillage == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_URBAN_VILLAGE),
                        environment.getProperty(ENTITY_URBAN_VILLAGE_ID),
                        String.valueOf(urbanVillageEntity.getUrbanVillageId())));
            } else {
                response.setUrbanVillage(new UrbanVillageModel().
                        convertFromEntityToModel(
                                existUrbanVillage,
                                existUrbanVillage.getProvince(),
                                existUrbanVillage.getCity(),
                                existUrbanVillage.getSubDistrict(),
                                existUrbanVillage.getCreatedBy(),
                                existUrbanVillage.getUpdatedBy()));

                message.setSuccess(true);
                message.setMessage(
                        successProcess(
                                SUCCESS_SELECT_DETAIL,
                                environment.getProperty(ENTITY_URBAN_VILLAGE)));
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @PostMapping(END_POINT_ADD_URBAN_VILLAGE)
    private String add(@Validated @RequestBody UrbanVillageEntity urbanVillageEntity) {
        UrbanVillageResponse response = new UrbanVillageResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity createdBy = checkEntityExistOrNot(urbanVillageEntity.getCreatedBy().getAdminId());

            if (createdBy == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(urbanVillageEntity.getCreatedBy().getAdminId()),
                        environment.getProperty(OPERATION_NAME_CREATE),
                        environment.getProperty(ENTITY_URBAN_VILLAGE)));
            } else {
                ProvinceEntity province = checkEntityExistOrNot(urbanVillageEntity.getProvince());

                if (province == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_PROVINCE),
                            environment.getProperty(ENTITY_PROVINCE_ID),
                            String.valueOf(urbanVillageEntity.getProvince().getProvinceId()),
                            environment.getProperty(OPERATION_NAME_CREATE),
                            environment.getProperty(ENTITY_URBAN_VILLAGE)));
                } else {
                    CityEntity city = checkEntityExistOrNot(urbanVillageEntity.getCity());

                    if (city == null)
                    {
                        message.setSuccess(false);
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_CITY),
                                environment.getProperty(ENTITY_CITY_ID),
                                String.valueOf(urbanVillageEntity.getCity().getCityId()),
                                environment.getProperty(OPERATION_NAME_CREATE),
                                environment.getProperty(ENTITY_URBAN_VILLAGE)));
                    } else {
                        SubDistrictEntity subDistrict = checkEntityExistOrNot(
                                urbanVillageEntity.getSubDistrict());

                        if (subDistrict == null)
                        {
                            message.setSuccess(false);
                            message.setMessage(existEntityNotFound(
                                    environment.getProperty(ENTITY_SUB_DISTRICT),
                                    environment.getProperty(ENTITY_SUB_DISTRICT_ID),
                                    String.valueOf(urbanVillageEntity.getSubDistrict().getSubDistrictId()),
                                    environment.getProperty(OPERATION_NAME_CREATE),
                                    environment.getProperty(ENTITY_URBAN_VILLAGE)));
                        } else {
                            urbanVillageEntity.setProvince(province);
                            urbanVillageEntity.setCity(city);
                            urbanVillageEntity.setSubDistrict(subDistrict);
                            urbanVillageEntity.setCreatedBy(createdBy);
                            urbanVillageEntity.setCreatedDate(LocalDateTime.now());
                            urbanVillageEntity.setUpdatedBy(null);

                            UrbanVillageEntity result = urbanVillageRepository.save(urbanVillageEntity);

                            response.setUrbanVillage(new UrbanVillageModel().
                                    convertFromEntityToModel(
                                            result,
                                            result.getProvince(),
                                            result.getCity(),
                                            result.getSubDistrict(),
                                            result.getCreatedBy(),
                                            result.getUpdatedBy()));

                            message.setSuccess(true);
                            message.setMessage(successProcess(
                                    environment.getProperty(ENTITY_URBAN_VILLAGE),
                                    environment.getProperty(OPERATION_NAME_CREATE)
                            ));
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

    @PostMapping(END_POINT_UPDATE_URBAN_VILLAGE)
    private String update(@Validated @RequestBody UrbanVillageEntity urbanVillageEntity) {
        UrbanVillageResponse response = new UrbanVillageResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            UrbanVillageEntity existUrbanVillage = checkEntityExistOrNot(
                    urbanVillageEntity);

            if (existUrbanVillage == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_URBAN_VILLAGE),
                        environment.getProperty(ENTITY_URBAN_VILLAGE_ID),
                        String.valueOf(urbanVillageEntity.getUrbanVillageId())));
            } else {
                ProvinceEntity existProvince = checkEntityExistOrNot(
                        urbanVillageEntity.getProvince());

                if (existProvince == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_PROVINCE),
                            environment.getProperty(ENTITY_PROVINCE_ID),
                            String.valueOf(urbanVillageEntity.getProvince().getProvinceId()),
                            environment.getProperty(OPERATION_NAME_UPDATE),
                            environment.getProperty(ENTITY_URBAN_VILLAGE)));
                } else {
                    CityEntity existCity = checkEntityExistOrNot(
                            urbanVillageEntity.getCity());

                    if (existCity == null)
                    {
                        message.setSuccess(false);
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_CITY),
                                environment.getProperty(ENTITY_CITY_ID),
                                String.valueOf(urbanVillageEntity.getCity().getCityId()),
                                environment.getProperty(OPERATION_NAME_UPDATE),
                                environment.getProperty(ENTITY_URBAN_VILLAGE)));
                    } else {
                        SubDistrictEntity existSubDistrict = checkEntityExistOrNot(
                                urbanVillageEntity.getSubDistrict());

                        if (existSubDistrict == null)
                        {
                            message.setSuccess(false);
                            message.setMessage(existEntityNotFound(
                                    environment.getProperty(ENTITY_SUB_DISTRICT),
                                    environment.getProperty(ENTITY_SUB_DISTRICT_ID),
                                    String.valueOf(urbanVillageEntity.getSubDistrict().getSubDistrictId()),
                                    environment.getProperty(OPERATION_NAME_UPDATE),
                                    environment.getProperty(ENTITY_URBAN_VILLAGE)));
                        } else {
                            AdminEntity updatedBy = checkEntityExistOrNot(
                                    urbanVillageEntity.getUpdatedBy().getAdminId());

                            if (updatedBy == null)
                            {
                                message.setMessage(existEntityNotFound(
                                        environment.getProperty(ENTITY_ADMIN),
                                        environment.getProperty(ENTITY_ADMIN_ID),
                                        String.valueOf(urbanVillageEntity.getUpdatedBy().getAdminId()),
                                        environment.getProperty(OPERATION_NAME_UPDATE),
                                        environment.getProperty(ENTITY_URBAN_VILLAGE)));
                            } else {
                                existUrbanVillage.setUrbanVillageName(urbanVillageEntity.getUrbanVillageName());
                                existUrbanVillage.setProvince(existProvince);
                                existUrbanVillage.setCity(existCity);
                                existUrbanVillage.setSubDistrict(existSubDistrict);
                                existUrbanVillage.setUpdatedBy(updatedBy);
                                existUrbanVillage.setUpdatedDate(LocalDateTime.now());

                                UrbanVillageEntity result = urbanVillageRepository.save(existUrbanVillage);

                                response.setUrbanVillage(new UrbanVillageModel().
                                        convertFromEntityToModel(
                                                result,
                                                result.getProvince(),
                                                result.getCity(),
                                                result.getSubDistrict(),
                                                result.getCreatedBy(),
                                                result.getUpdatedBy()));

                                message.setSuccess(true);
                                message.setMessage(
                                        successProcess(
                                                environment.getProperty(ENTITY_URBAN_VILLAGE),
                                                environment.getProperty(ENTITY_URBAN_VILLAGE_ID),
                                                String.valueOf(existUrbanVillage.getUrbanVillageId()),
                                                environment.getProperty(ENTITY_URBAN_VILLAGE_NAME),
                                                existUrbanVillage.getUrbanVillageName(),
                                                environment.getProperty(OPERATION_NAME_UPDATE)));
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

    @PostMapping(END_POINT_DELETE_URBAN_VILLAGE)
    private String delete(@Validated @RequestBody UrbanVillageEntity urbanVillageEntity) {
        UrbanVillageResponse response = new UrbanVillageResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            UrbanVillageEntity existUrbanVillage = checkEntityExistOrNot(
                    urbanVillageEntity);

            if (existUrbanVillage == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_URBAN_VILLAGE),
                        environment.getProperty(ENTITY_URBAN_VILLAGE_ID),
                        String.valueOf(urbanVillageEntity.getUrbanVillageId())));
            } else {
                message.setSuccess(true);
                message.setMessage(
                        successProcess(
                                environment.getProperty(ENTITY_URBAN_VILLAGE),
                                environment.getProperty(ENTITY_URBAN_VILLAGE_ID),
                                String.valueOf(existUrbanVillage.getUrbanVillageId()),
                                environment.getProperty(ENTITY_URBAN_VILLAGE_NAME),
                                existUrbanVillage.getUrbanVillageName(),
                                environment.getProperty(OPERATION_NAME_DELETE)));

                urbanVillageRepository.delete(existUrbanVillage);
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
}
