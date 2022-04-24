package com.harifrizki.crimemapsappsapi.controller;

import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import com.harifrizki.crimemapsappsapi.entity.CityEntity;
import com.harifrizki.crimemapsappsapi.entity.ProvinceEntity;
import com.harifrizki.crimemapsappsapi.entity.SubDistrictEntity;
import com.harifrizki.crimemapsappsapi.model.CityModel;
import com.harifrizki.crimemapsappsapi.model.SubDistrictModel;
import com.harifrizki.crimemapsappsapi.model.response.CityResponse;
import com.harifrizki.crimemapsappsapi.model.response.GeneralMessageResponse;
import com.harifrizki.crimemapsappsapi.model.response.SubDistrictResponse;
import com.harifrizki.crimemapsappsapi.repository.AdminRepository;
import com.harifrizki.crimemapsappsapi.repository.CityRepository;
import com.harifrizki.crimemapsappsapi.repository.SubDistrictRepository;
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
public class SubDistrictsController {

    @Autowired
    private SubDistrictRepository subDistrictRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PaginationServiceImpl paginationService;

    @Autowired
    private Environment environment;

    @GetMapping(SUB_DISTRICT_GET_ALL_CONTROLLER)
    @ResponseBody
    private String getAllSubDistrict(@RequestParam int pageNo) {
        SubDistrictResponse response = new SubDistrictResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            int realPage = pageNo - 1;
            Pageable pageable = PageRequest.
                    of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                            Sort.by(Sort.Direction.ASC, "subDistrictName"));

            Page<SubDistrictEntity> page = subDistrictRepository.findAll(pageable);
            ArrayList<SubDistrictEntity> subDistrictEntities = new ArrayList<>(page.getContent());

            ArrayList<SubDistrictModel> subDistrictModels = new ArrayList<>();
            for (SubDistrictEntity subDistrictEntity : subDistrictEntities) {
                subDistrictModels.add(new SubDistrictModel().
                        convertFromEntityToModel(
                                subDistrictEntity,
                                subDistrictEntity.getCity(),
                                subDistrictEntity.getCreatedBy(),
                                subDistrictEntity.getUpdatedBy())
                );
            }

            response.setSubDistricts(subDistrictModels);
            response.setPage(paginationService.getPagination(
                    pageNo, SUB_DISTRICT_GET_ALL_CONTROLLER,
                    page, new String[]{"pageNo"}, new String[]{}));

            message.setSuccess(true);
            message.setMessage(
                    successProcess(
                            SUCCESS_SELECT_ALL,
                            environment.getProperty(ENTITY_SUB_DISTRICT)));
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_SELECT);
    }

    @PostMapping(SUB_DISTRICT_GET_ALL_SEARCH_NAME_CONTROLLER)
    private String getAllSubDistrict(@RequestParam int pageNo,
                                     @Validated @RequestParam("subDistrictName") String subDistrictName) {
        SubDistrictResponse response = new SubDistrictResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            int realPage = pageNo - 1;
            Pageable pageable = PageRequest.
                    of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                            Sort.by(Sort.Direction.ASC, "sub_district_name"));

            Page<SubDistrictEntity> page = subDistrictRepository.findByName(pageable, subDistrictName);
            ArrayList<SubDistrictEntity> subDistrictEntities = new ArrayList<>(page.getContent());

            ArrayList<SubDistrictModel> subDistrictModels = new ArrayList<>();
            for (SubDistrictEntity subDistrictEntity : subDistrictEntities) {
                subDistrictModels.add(new SubDistrictModel().
                        convertFromEntityToModel(
                                subDistrictEntity,
                                subDistrictEntity.getCity(),
                                subDistrictEntity.getCreatedBy(),
                                subDistrictEntity.getUpdatedBy())
                );
            }

            response.setSubDistricts(subDistrictModels);
            response.setPage(paginationService.getPagination(
                    pageNo, SUB_DISTRICT_GET_ALL_SEARCH_NAME_CONTROLLER,
                    page, new String[]{"pageNo"}, new String[]{}));

            message.setSuccess(true);
            message.setMessage(
                    successProcess(
                            SUCCESS_SELECT_ALL,
                            environment.getProperty(ENTITY_SUB_DISTRICT)));
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_SELECT);
    }

    @PostMapping(SUB_DISTRICT_GET_ALL_SEARCH_CITY_ID_CONTROLLER)
    private String getAllSubDistrict(@RequestParam int pageNo,
                                     @Validated @RequestBody SubDistrictEntity subDistrictEntity) {
        SubDistrictResponse response = new SubDistrictResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            CityEntity existCity = checkCityWasExistOrNot(subDistrictEntity.getCity().getCityId());

            if (existCity == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_CITY),
                        environment.getProperty(ENTITY_CITY_ID),
                        String.valueOf(subDistrictEntity.getCity().getCityId()),
                        "Get All",
                        environment.getProperty(ENTITY_SUB_DISTRICT)));
            } else {
                int realPage = pageNo - 1;
                Pageable pageable = PageRequest.
                        of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                                Sort.by(Sort.Direction.ASC, "sub_district_name"));

                Page<SubDistrictEntity> page = subDistrictRepository.findByCityId(
                        pageable,
                        existCity.getCityId(),
                        subDistrictEntity.getSubDistrictName());
                ArrayList<SubDistrictEntity> subDistrictEntities = new ArrayList<>(page.getContent());

                ArrayList<SubDistrictModel> subDistrictModels = new ArrayList<>();
                for (SubDistrictEntity subDistrict : subDistrictEntities) {
                    subDistrictModels.add(new SubDistrictModel().
                            convertFromEntityToModel(
                                    subDistrict,
                                    subDistrict.getCity(),
                                    subDistrict.getCreatedBy(),
                                    subDistrict.getUpdatedBy())
                    );
                }

                response.setSubDistricts(subDistrictModels);
                response.setPage(paginationService.getPagination(
                        pageNo, SUB_DISTRICT_GET_ALL_SEARCH_CITY_ID_CONTROLLER,
                        page, new String[]{"pageNo"}, new String[]{}));

                message.setSuccess(true);
                message.setMessage(
                        successProcess(
                                SUCCESS_SELECT_ALL,
                                environment.getProperty(ENTITY_SUB_DISTRICT)));
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_SELECT);
    }

    @PostMapping(SUB_DISTRICT_GET_SEARCH_ID_CONTROLLER)
    private String getSubDistrict(@Validated @RequestParam("subDistrictId") UUID subDistrictId) {
        SubDistrictResponse response = new SubDistrictResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            SubDistrictEntity existSubDistrict = checkSubDistrictWasExistOrNot(subDistrictId);

            if (existSubDistrict == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_SUB_DISTRICT),
                        environment.getProperty(ENTITY_SUB_DISTRICT_ID),
                        String.valueOf(subDistrictId)));
            } else {
                response.setSubDistrict(new SubDistrictModel().
                        convertFromEntityToModel(
                                existSubDistrict,
                                existSubDistrict.getCity(),
                                existSubDistrict.getCreatedBy(),
                                existSubDistrict.getUpdatedBy()));

                message.setSuccess(true);
                message.setMessage(
                        successProcess(
                                SUCCESS_SELECT_DETAIL,
                                environment.getProperty(ENTITY_SUB_DISTRICT)));
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @PostMapping(SUB_DISTRICT_ADD_CONTROLLER)
    private String add(@Validated @RequestBody SubDistrictEntity subDistrictEntity) {
        SubDistrictResponse response = new SubDistrictResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity createdBy = checkAdminWasExistOrNot(subDistrictEntity.getCreatedBy().getAdminId());

            if (createdBy == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(subDistrictEntity.getCreatedBy().getAdminId()),
                        "Created New",
                        environment.getProperty(ENTITY_SUB_DISTRICT)));
            } else {
                CityEntity city = checkCityWasExistOrNot(subDistrictEntity.getCity().getCityId());

                if (city == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_CITY),
                            environment.getProperty(ENTITY_CITY_ID),
                            String.valueOf(subDistrictEntity.getCity().getCityId()),
                            "Created New",
                            environment.getProperty(ENTITY_SUB_DISTRICT)));
                } else {
                    subDistrictEntity.setCity(city);
                    subDistrictEntity.setCreatedBy(createdBy);
                    subDistrictEntity.setCreatedDate(LocalDateTime.now());
                    subDistrictEntity.setUpdatedBy(null);

                    SubDistrictEntity result = subDistrictRepository.save(subDistrictEntity);

                    response.setSubDistrict(new SubDistrictModel().
                            convertFromEntityToModel(
                                    result,
                                    result.getCity(),
                                    result.getCreatedBy(),
                                    result.getUpdatedBy()));

                    message.setSuccess(true);
                    message.setMessage(successProcess(
                            environment.getProperty(ENTITY_SUB_DISTRICT),
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

    @PostMapping(SUB_DISTRICT_UPDATE_CONTROLLER)
    private String update(@Validated @RequestBody SubDistrictEntity subDistrictEntity) {
        SubDistrictResponse response = new SubDistrictResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            SubDistrictEntity existSubDistrict = checkSubDistrictWasExistOrNot(subDistrictEntity.getSubDistrictId());

            if (existSubDistrict == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_SUB_DISTRICT),
                        environment.getProperty(ENTITY_SUB_DISTRICT_ID),
                        String.valueOf(subDistrictEntity.getSubDistrictId())));
            } else {
                CityEntity existCity = checkCityWasExistOrNot(subDistrictEntity.getCity().getCityId());

                if (existCity == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_CITY),
                            environment.getProperty(ENTITY_CITY_ID),
                            String.valueOf(subDistrictEntity.getCity().getCityId()),
                            "Updated Existing",
                            environment.getProperty(ENTITY_SUB_DISTRICT)));
                } else {
                    AdminEntity updatedBy = checkAdminWasExistOrNot(subDistrictEntity.getUpdatedBy().getAdminId());

                    if (updatedBy == null)
                    {
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_ADMIN),
                                environment.getProperty(ENTITY_ADMIN_ID),
                                String.valueOf(subDistrictEntity.getUpdatedBy().getAdminId()),
                                "Updated Existing",
                                environment.getProperty(ENTITY_SUB_DISTRICT)));
                    } else {
                        existSubDistrict.setSubDistrictName(subDistrictEntity.getSubDistrictName());
                        existSubDistrict.setCity(existCity);
                        existSubDistrict.setUpdatedBy(updatedBy);
                        existSubDistrict.setUpdatedDate(LocalDateTime.now());

                        SubDistrictEntity result = subDistrictRepository.save(existSubDistrict);

                        response.setSubDistrict(new SubDistrictModel().
                                convertFromEntityToModel(
                                        result,
                                        result.getCity(),
                                        result.getCreatedBy(),
                                        result.getUpdatedBy()));

                        message.setSuccess(true);
                        message.setMessage(
                                successProcess(
                                        environment.getProperty(ENTITY_SUB_DISTRICT),
                                        environment.getProperty(ENTITY_SUB_DISTRICT_ID),
                                        String.valueOf(existSubDistrict.getSubDistrictId()),
                                        environment.getProperty(ENTITY_SUB_DISTRICT_NAME),
                                        existSubDistrict.getSubDistrictName(),
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

    @PostMapping(SUB_DISTRICT_DELETE_CONTROLLER)
    private String delete(@Validated @RequestBody SubDistrictEntity subDistrictEntity) {
        SubDistrictResponse response = new SubDistrictResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            SubDistrictEntity existSubDistrict = checkSubDistrictWasExistOrNot(subDistrictEntity.getSubDistrictId());

            if (existSubDistrict == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_SUB_DISTRICT),
                        environment.getProperty(ENTITY_SUB_DISTRICT_ID),
                        String.valueOf(subDistrictEntity.getSubDistrictId())));
            } else {
                message.setSuccess(true);
                message.setMessage(
                        successProcess(
                                environment.getProperty(ENTITY_SUB_DISTRICT),
                                environment.getProperty(ENTITY_SUB_DISTRICT_ID),
                                String.valueOf(existSubDistrict.getSubDistrictId()),
                                environment.getProperty(ENTITY_SUB_DISTRICT_NAME),
                                existSubDistrict.getSubDistrictName(),
                                "Deleted"));

                subDistrictRepository.delete(existSubDistrict);
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

    private CityEntity checkCityWasExistOrNot(UUID cityId) {
        return  cityRepository.findById(cityId).orElse(null);
    }

    private SubDistrictEntity checkSubDistrictWasExistOrNot(UUID subDistrictId) {
        return subDistrictRepository.findById(subDistrictId).orElse(null);
    }
}
