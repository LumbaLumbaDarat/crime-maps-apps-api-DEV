package com.harifrizki.crimemapsappsapi.controller;

import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import com.harifrizki.crimemapsappsapi.entity.CityEntity;
import com.harifrizki.crimemapsappsapi.entity.SubDistrictEntity;
import com.harifrizki.crimemapsappsapi.entity.UrbanVillageEntity;
import com.harifrizki.crimemapsappsapi.model.SubDistrictModel;
import com.harifrizki.crimemapsappsapi.model.UrbanVillageModel;
import com.harifrizki.crimemapsappsapi.model.response.GeneralMessageResponse;
import com.harifrizki.crimemapsappsapi.model.response.SubDistrictResponse;
import com.harifrizki.crimemapsappsapi.model.response.UrbanVillageResponse;
import com.harifrizki.crimemapsappsapi.repository.AdminRepository;
import com.harifrizki.crimemapsappsapi.repository.SubDistrictRepository;
import com.harifrizki.crimemapsappsapi.repository.UrbanVillageRepository;
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
@RequestMapping(GENERAL_CONTROLLER_URL)
@Validated
public class UrbanVillageController {

    @Autowired
    private UrbanVillageRepository urbanVillageRepository;

    @Autowired
    private SubDistrictRepository subDistrictRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PaginationServiceImpl paginationService;

    @Autowired
    private Environment environment;

    @GetMapping(URBAN_VILLAGE_GET_ALL_CONTROLLER)
    @ResponseBody
    private String getAllUrbanVillage(@RequestParam int pageNo) {
        UrbanVillageResponse response = new UrbanVillageResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            int realPage = pageNo - 1;
            Pageable pageable = PageRequest.
                    of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                            Sort.by(Sort.Direction.ASC, "urbanVillageName"));

            Page<UrbanVillageEntity> page = urbanVillageRepository.findAll(pageable);
            ArrayList<UrbanVillageEntity> urbanVillageEntities = new ArrayList<>(page.getContent());

            ArrayList<UrbanVillageModel> urbanVillageModels = new ArrayList<>();
            for (UrbanVillageEntity urbanVillageEntity : urbanVillageEntities) {
                urbanVillageModels.add(new UrbanVillageModel().
                        convertFromEntityToModel(
                                urbanVillageEntity,
                                urbanVillageEntity.getSubDistrict(),
                                urbanVillageEntity.getCreatedBy(),
                                urbanVillageEntity.getUpdatedBy())
                );
            }

            response.setUrbanVillages(urbanVillageModels);
            response.setPage(paginationService.getPagination(
                    pageNo, URBAN_VILLAGE_GET_ALL_CONTROLLER,
                    page, new String[]{"pageNo"}, new String[]{}));

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

    @PostMapping(URBAN_VILLAGE_GET_ALL_SEARCH_NAME_CONTROLLER)
    private String getAllUrbanVillage(@RequestParam int pageNo,
                                      @Validated @RequestParam("urbanVillageName") String urbanVillageName) {
        UrbanVillageResponse response = new UrbanVillageResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            int realPage = pageNo - 1;
            Pageable pageable = PageRequest.
                    of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                            Sort.by(Sort.Direction.ASC, "urban_village_name"));

            Page<UrbanVillageEntity> page = urbanVillageRepository.findByName(pageable, urbanVillageName);
            ArrayList<UrbanVillageEntity> urbanVillageEntities = new ArrayList<>(page.getContent());

            ArrayList<UrbanVillageModel> urbanVillageModels = new ArrayList<>();
            for (UrbanVillageEntity urbanVillageEntity : urbanVillageEntities) {
                urbanVillageModels.add(new UrbanVillageModel().
                        convertFromEntityToModel(
                                urbanVillageEntity,
                                urbanVillageEntity.getSubDistrict(),
                                urbanVillageEntity.getCreatedBy(),
                                urbanVillageEntity.getUpdatedBy())
                );
            }

            response.setUrbanVillages(urbanVillageModels);
            response.setPage(paginationService.getPagination(
                    pageNo, URBAN_VILLAGE_GET_ALL_SEARCH_NAME_CONTROLLER,
                    page, new String[]{"pageNo"}, new String[]{}));

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

    @PostMapping(URBAN_VILLAGE_GET_ALL_SEARCH_SUB_DISTRICT_ID_CONTROLLER)
    private String getAllSubDistrict(@RequestParam int pageNo,
                                     @Validated @RequestBody UrbanVillageEntity urbanVillageEntity) {
        UrbanVillageResponse response = new UrbanVillageResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            SubDistrictEntity existSubDistrict = checkSubDistrictWasExistOrNot(
                    urbanVillageEntity.getSubDistrict().getSubDistrictId());

            if (existSubDistrict == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_SUB_DISTRICT),
                        environment.getProperty(ENTITY_SUB_DISTRICT_ID),
                        String.valueOf(urbanVillageEntity.getSubDistrict().getSubDistrictId()),
                        "Get All",
                        environment.getProperty(ENTITY_URBAN_VILLAGE)));
            } else {
                int realPage = pageNo - 1;
                Pageable pageable = PageRequest.
                        of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                                Sort.by(Sort.Direction.ASC, "urban_village_name"));

                Page<UrbanVillageEntity> page = urbanVillageRepository.findBySubDistrictId(
                        pageable,
                        existSubDistrict.getSubDistrictId(),
                        urbanVillageEntity.getUrbanVillageName());
                ArrayList<UrbanVillageEntity> urbanVillageEntities = new ArrayList<>(page.getContent());

                ArrayList<UrbanVillageModel> urbanVillageModels = new ArrayList<>();
                for (UrbanVillageEntity urbanVillage : urbanVillageEntities) {
                    urbanVillageModels.add(new UrbanVillageModel().
                            convertFromEntityToModel(
                                    urbanVillage,
                                    urbanVillage.getSubDistrict(),
                                    urbanVillage.getCreatedBy(),
                                    urbanVillage.getUpdatedBy())
                    );
                }

                response.setUrbanVillages(urbanVillageModels);
                response.setPage(paginationService.getPagination(
                        pageNo, URBAN_VILLAGE_GET_ALL_SEARCH_SUB_DISTRICT_ID_CONTROLLER,
                        page, new String[]{"pageNo"}, new String[]{}));

                message.setSuccess(true);
                message.setMessage(
                        successProcess(
                                SUCCESS_SELECT_ALL,
                                environment.getProperty(ENTITY_URBAN_VILLAGE)));
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_SELECT);
    }

    @PostMapping(URBAN_VILLAGE_GET_SEARCH_ID_CONTROLLER)
    private String getUrbanVillage(@Validated @RequestParam("urbanVillageId") UUID urbanVillageId) {
        UrbanVillageResponse response = new UrbanVillageResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            UrbanVillageEntity existUrbanVillage = checkUrbanVillageDistrictWasExistOrNot(urbanVillageId);

            if (existUrbanVillage == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_URBAN_VILLAGE),
                        environment.getProperty(ENTITY_URBAN_VILLAGE_ID),
                        String.valueOf(urbanVillageId)));
            } else {
                response.setUrbanVillage(new UrbanVillageModel().
                        convertFromEntityToModel(
                                existUrbanVillage,
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

    @PostMapping(URBAN_VILLAGE_ADD_CONTROLLER)
    private String add(@Validated @RequestBody UrbanVillageEntity urbanVillageEntity) {
        UrbanVillageResponse response = new UrbanVillageResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity createdBy = checkAdminWasExistOrNot(urbanVillageEntity.getCreatedBy().getAdminId());

            if (createdBy == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(urbanVillageEntity.getCreatedBy().getAdminId()),
                        "Created New",
                        environment.getProperty(ENTITY_URBAN_VILLAGE)));
            } else {
                SubDistrictEntity subDistrict = checkSubDistrictWasExistOrNot(urbanVillageEntity.getSubDistrict().getSubDistrictId());

                if (subDistrict == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_SUB_DISTRICT),
                            environment.getProperty(ENTITY_SUB_DISTRICT_ID),
                            String.valueOf(urbanVillageEntity.getSubDistrict().getSubDistrictId()),
                            "Created New",
                            environment.getProperty(ENTITY_URBAN_VILLAGE)));
                } else {
                    urbanVillageEntity.setSubDistrict(subDistrict);
                    urbanVillageEntity.setCreatedBy(createdBy);
                    urbanVillageEntity.setCreatedDate(LocalDateTime.now());
                    urbanVillageEntity.setUpdatedBy(null);

                    UrbanVillageEntity result = urbanVillageRepository.save(urbanVillageEntity);

                    response.setUrbanVillage(new UrbanVillageModel().
                            convertFromEntityToModel(
                                    result,
                                    result.getSubDistrict(),
                                    result.getCreatedBy(),
                                    result.getUpdatedBy()));

                    message.setSuccess(true);
                    message.setMessage(successProcess(
                            environment.getProperty(ENTITY_URBAN_VILLAGE),
                            "Added New"
                    ));
                }
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @PostMapping(URBAN_VILLAGE_UPDATE_CONTROLLER)
    private String update(@Validated @RequestBody UrbanVillageEntity urbanVillageEntity) {
        UrbanVillageResponse response = new UrbanVillageResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            UrbanVillageEntity existUrbanVillage = checkUrbanVillageDistrictWasExistOrNot(
                    urbanVillageEntity.getUrbanVillageId());

            if (existUrbanVillage == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_URBAN_VILLAGE),
                        environment.getProperty(ENTITY_URBAN_VILLAGE_ID),
                        String.valueOf(urbanVillageEntity.getUrbanVillageId())));
            } else {
                SubDistrictEntity existSubDistrict = checkSubDistrictWasExistOrNot(
                        urbanVillageEntity.getSubDistrict().getSubDistrictId());

                if (existSubDistrict == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_SUB_DISTRICT),
                            environment.getProperty(ENTITY_SUB_DISTRICT_ID),
                            String.valueOf(urbanVillageEntity.getSubDistrict().getSubDistrictId()),
                            "Updated Existing",
                            environment.getProperty(ENTITY_URBAN_VILLAGE)));
                } else {
                    AdminEntity updatedBy = checkAdminWasExistOrNot(urbanVillageEntity.getUpdatedBy().getAdminId());

                    if (updatedBy == null)
                    {
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_ADMIN),
                                environment.getProperty(ENTITY_ADMIN_ID),
                                String.valueOf(urbanVillageEntity.getUpdatedBy().getAdminId()),
                                "Updated Existing",
                                environment.getProperty(ENTITY_URBAN_VILLAGE)));
                    } else {
                        existUrbanVillage.setUrbanVillageName(urbanVillageEntity.getUrbanVillageName());
                        existUrbanVillage.setSubDistrict(existSubDistrict);
                        existUrbanVillage.setUpdatedBy(updatedBy);
                        existUrbanVillage.setUpdatedDate(LocalDateTime.now());

                        UrbanVillageEntity result = urbanVillageRepository.save(existUrbanVillage);

                        response.setUrbanVillage(new UrbanVillageModel().
                                convertFromEntityToModel(
                                        result,
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
                                        "Updated"));
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

    @PostMapping(URBAN_VILLAGE_DELETE_CONTROLLER)
    private String delete(@Validated @RequestBody UrbanVillageEntity urbanVillageEntity) {
        UrbanVillageResponse response = new UrbanVillageResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            UrbanVillageEntity existUrbanVillage = checkUrbanVillageDistrictWasExistOrNot(
                    urbanVillageEntity.getUrbanVillageId());

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
                                "Deleted"));

                urbanVillageRepository.delete(existUrbanVillage);
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

    private SubDistrictEntity checkSubDistrictWasExistOrNot(UUID subDistrictId) {
        return subDistrictRepository.findById(subDistrictId).orElse(null);
    }

    private UrbanVillageEntity checkUrbanVillageDistrictWasExistOrNot(UUID urbanVillageId) {
        return urbanVillageRepository.findById(urbanVillageId).orElse(null);
    }
}
