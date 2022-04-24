package com.harifrizki.crimemapsappsapi.controller;

import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import com.harifrizki.crimemapsappsapi.entity.CityEntity;
import com.harifrizki.crimemapsappsapi.entity.ProvinceEntity;
import com.harifrizki.crimemapsappsapi.model.CityModel;
import com.harifrizki.crimemapsappsapi.model.response.CityResponse;
import com.harifrizki.crimemapsappsapi.model.response.GeneralMessageResponse;
import com.harifrizki.crimemapsappsapi.repository.AdminRepository;
import com.harifrizki.crimemapsappsapi.repository.CityRepository;
import com.harifrizki.crimemapsappsapi.repository.ProvinceRepository;
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
public class CityController {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PaginationServiceImpl paginationService;

    @Autowired
    private Environment environment;

    @GetMapping(CITY_GET_ALL_CONTROLLER)
    @ResponseBody
    private String getAllCity(@RequestParam int pageNo) {
        CityResponse response = new CityResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            int realPage = pageNo - 1;
            Pageable pageable = PageRequest.
                    of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                            Sort.by(Sort.Direction.ASC, "cityName"));

            Page<CityEntity> page = cityRepository.findAll(pageable);
            ArrayList<CityEntity> cityEntities = new ArrayList<>(page.getContent());

            ArrayList<CityModel> cityModels = new ArrayList<>();
            for (CityEntity cityEntity : cityEntities) {
                cityModels.add(new CityModel().
                        convertFromEntityToModel(
                                cityEntity,
                                cityEntity.getProvince(),
                                cityEntity.getCreatedBy(),
                                cityEntity.getUpdatedBy())
                );
            }

            response.setCities(cityModels);
            response.setPage(paginationService.getPagination(
                    pageNo, CITY_GET_ALL_CONTROLLER,
                    page, new String[]{"pageNo"}, new String[]{}));

            message.setSuccess(true);
            message.setMessage(
                    successProcess(
                            SUCCESS_SELECT_ALL,
                            environment.getProperty(ENTITY_CITY)));
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_SELECT);
    }

    @PostMapping(CITY_GET_ALL_SEARCH_NAME_CONTROLLER)
    private String getAllCity(@RequestParam int pageNo,
                              @Validated @RequestParam("cityName") String cityName) {
        CityResponse response = new CityResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            int realPage = pageNo - 1;
            Pageable pageable = PageRequest.
                    of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                            Sort.by(Sort.Direction.ASC, "city_name"));

            Page<CityEntity> page = cityRepository.findByName(pageable, cityName);
            ArrayList<CityEntity> cityEntities = new ArrayList<>(page.getContent());

            ArrayList<CityModel> cityModels = new ArrayList<>();
            for (CityEntity cityEntity : cityEntities) {
                cityModels.add(new CityModel().
                        convertFromEntityToModel(
                                cityEntity,
                                cityEntity.getProvince(),
                                cityEntity.getCreatedBy(),
                                cityEntity.getUpdatedBy())
                );
            }

            response.setCities(cityModels);
            response.setPage(paginationService.getPagination(
                    pageNo, CITY_GET_ALL_SEARCH_NAME_CONTROLLER,
                    page, new String[]{"pageNo"}, new String[]{}));

            message.setSuccess(true);
            message.setMessage(
                    successProcess(
                            SUCCESS_SELECT_ALL,
                            environment.getProperty(ENTITY_CITY)));
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_SELECT);
    }

    @PostMapping(CITY_GET_ALL_SEARCH_PROVINCE_ID_CONTROLLER)
    private String getAllCity(@RequestParam int pageNo,
                              @Validated @RequestBody CityEntity cityEntity) {
        CityResponse response = new CityResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            ProvinceEntity existProvince = checkProvinceWasExistOrNot(cityEntity.getProvince().getProvinceId());

            if (existProvince == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_PROVINCE),
                        environment.getProperty(ENTITY_PROVINCE_ID),
                        String.valueOf(cityEntity.getProvince().getProvinceId()),
                        "Get All",
                        environment.getProperty(ENTITY_CITY)));
            } else {
                int realPage = pageNo - 1;
                Pageable pageable = PageRequest.
                        of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                                Sort.by(Sort.Direction.ASC, "city_name"));

                Page<CityEntity> page = cityRepository.findByProvinceId(
                        pageable,
                        existProvince.getProvinceId(),
                        cityEntity.getCityName());
                ArrayList<CityEntity> cityEntities = new ArrayList<>(page.getContent());

                ArrayList<CityModel> cityModels = new ArrayList<>();
                for (CityEntity city : cityEntities) {
                    cityModels.add(new CityModel().
                            convertFromEntityToModel(
                                    city,
                                    city.getProvince(),
                                    city.getCreatedBy(),
                                    city.getUpdatedBy())
                    );
                }

                response.setCities(cityModels);
                response.setPage(paginationService.getPagination(
                        pageNo, CITY_GET_ALL_SEARCH_PROVINCE_ID_CONTROLLER,
                        page, new String[]{"pageNo"}, new String[]{}));

                message.setSuccess(true);
                message.setMessage(
                        successProcess(
                                SUCCESS_SELECT_ALL,
                                environment.getProperty(ENTITY_CITY)));
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_SELECT);
    }

    @PostMapping(CITY_GET_SEARCH_ID_CONTROLLER)
    private String getCity(@Validated @RequestParam("cityId") UUID cityId) {
        CityResponse response = new CityResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            CityEntity existCity = checkCityWasExistOrNot(cityId);

            if (existCity == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_CITY),
                        environment.getProperty(ENTITY_CITY_ID),
                        String.valueOf(cityId)));
            } else {
                response.setCity(new CityModel().
                        convertFromEntityToModel(
                                existCity,
                                existCity.getProvince(),
                                existCity.getCreatedBy(),
                                existCity.getUpdatedBy()));

                message.setSuccess(true);
                message.setMessage(
                        successProcess(
                                SUCCESS_SELECT_DETAIL,
                                environment.getProperty(ENTITY_CITY)));
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @PostMapping(CITY_ADD_CONTROLLER)
    private String add(@Validated @RequestBody CityEntity cityEntity) {
        CityResponse response = new CityResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity createdBy = checkAdminWasExistOrNot(cityEntity.getCreatedBy().getAdminId());

            if (createdBy == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(cityEntity.getCreatedBy().getAdminId()),
                        "Created New",
                        environment.getProperty(ENTITY_CITY)));
            } else {
                ProvinceEntity province = checkProvinceWasExistOrNot(cityEntity.getProvince().getProvinceId());

                if (province == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_PROVINCE),
                            environment.getProperty(ENTITY_PROVINCE_ID),
                            String.valueOf(cityEntity.getProvince().getProvinceId()),
                            "Created New",
                            environment.getProperty(ENTITY_CITY)));
                } else {
                    cityEntity.setProvince(province);
                    cityEntity.setCreatedBy(createdBy);
                    cityEntity.setCreatedDate(LocalDateTime.now());
                    cityEntity.setUpdatedBy(null);

                    CityEntity result = cityRepository.save(cityEntity);

                    response.setCity(new CityModel().
                            convertFromEntityToModel(
                                    result,
                                    result.getProvince(),
                                    result.getCreatedBy(),
                                    result.getUpdatedBy()));

                    message.setSuccess(true);
                    message.setMessage(successProcess(
                            environment.getProperty(ENTITY_CITY),
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

    @PostMapping(CITY_UPDATE_CONTROLLER)
    private String update(@Validated @RequestBody CityEntity cityEntity) {
        CityResponse response = new CityResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            CityEntity existCity = checkCityWasExistOrNot(cityEntity.getCityId());

            if (existCity == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_CITY),
                        environment.getProperty(ENTITY_CITY_ID),
                        String.valueOf(cityEntity.getCityId())));
            } else {
                ProvinceEntity existProvince = checkProvinceWasExistOrNot(cityEntity.getProvince().getProvinceId());

                if (existProvince == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_PROVINCE),
                            environment.getProperty(ENTITY_PROVINCE_ID),
                            String.valueOf(cityEntity.getProvince().getProvinceId()),
                            "Updated Existing",
                            environment.getProperty(ENTITY_CITY)));
                } else {
                    AdminEntity updatedBy = checkAdminWasExistOrNot(cityEntity.getUpdatedBy().getAdminId());

                    if (updatedBy == null)
                    {
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_ADMIN),
                                environment.getProperty(ENTITY_ADMIN_ID),
                                String.valueOf(cityEntity.getUpdatedBy().getAdminId()),
                                "Updated Existing",
                                environment.getProperty(ENTITY_CITY)));
                    } else {
                        existCity.setCityName(cityEntity.getCityName());
                        existCity.setProvince(existProvince);
                        existCity.setUpdatedBy(updatedBy);
                        existCity.setUpdatedDate(LocalDateTime.now());

                        CityEntity result = cityRepository.save(existCity);

                        response.setCity(new CityModel().
                                convertFromEntityToModel(
                                        result,
                                        result.getProvince(),
                                        result.getCreatedBy(),
                                        result.getUpdatedBy()));

                        message.setSuccess(true);
                        message.setMessage(
                                successProcess(
                                        environment.getProperty(ENTITY_CITY),
                                        environment.getProperty(ENTITY_CITY_ID),
                                        String.valueOf(existCity.getCityId()),
                                        environment.getProperty(ENTITY_CITY_NAME),
                                        existCity.getCityName(),
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

    @PostMapping(CITY_DELETE_CONTROLLER)
    private String delete(@Validated @RequestBody CityEntity cityEntity) {
        CityResponse response = new CityResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            CityEntity existCity = checkCityWasExistOrNot(cityEntity.getCityId());

            if (existCity == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_CITY),
                        environment.getProperty(ENTITY_CITY_ID),
                        String.valueOf(cityEntity.getCityId())));
            } else {
                message.setSuccess(true);
                message.setMessage(
                        successProcess(
                                environment.getProperty(ENTITY_CITY),
                                environment.getProperty(ENTITY_CITY_ID),
                                String.valueOf(existCity.getCityId()),
                                environment.getProperty(ENTITY_CITY_NAME),
                                existCity.getCityName(),
                                "Deleted"));

                cityRepository.delete(existCity);
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
        return  provinceRepository.findById(provinceId).orElse(null);
    }

    private CityEntity checkCityWasExistOrNot(UUID cityId) {
        return  cityRepository.findById(cityId).orElse(null);
    }
}
