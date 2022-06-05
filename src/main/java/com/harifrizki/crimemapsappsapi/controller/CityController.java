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
public class CityController extends ControllerService {

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

    @PostMapping(END_POINT_CITY)
    private String getAllCity(@RequestParam String searchBy,
                              @RequestParam int pageNo,
                              @Validated @RequestBody CityEntity cityEntity) {
        CityResponse response = new CityResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            int realPage = pageNo - 1;
            Pageable pageable = PageRequest.
                    of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                            Sort.by(Sort.Direction.ASC, "city_name"));

            Page<CityEntity> page = null;
            switch (searchBy) {
                case PARAM_NAME:
                    page = cityRepository.findByName(
                            pageable,
                            cityEntity.getCityName());
                    break;
                case PARAM_PROVINCE_ID:
                    ProvinceEntity existProvince = checkEntityExistOrNot(cityEntity.getProvince());

                    if (existProvince == null)
                    {
                        message.setSuccess(false);
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_PROVINCE),
                                environment.getProperty(ENTITY_PROVINCE_ID),
                                String.valueOf(cityEntity.getProvince().getProvinceId()),
                                environment.getProperty(OPERATION_NAME_READ_ALL),
                                environment.getProperty(ENTITY_CITY)));

                        response.setMessage(message);
                        return response.toJson(response, OPERATION_SELECT);
                    } else page = cityRepository.findByProvinceId(
                            pageable,
                            cityEntity.getProvince().getProvinceId(),
                            cityEntity.getCityName());
                    break;
            }

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
                    pageNo, END_POINT_CITY,
                    page, new String[]{PARAM_SEARCH_BY, PARAM_PAGE_NO}, new String[]{searchBy}));

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

    @PostMapping(END_POINT_DETAIL_CITY)
    private String getCity(@Validated @RequestBody CityEntity cityEntity) {
        CityResponse response = new CityResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            CityEntity existCity = checkEntityExistOrNot(cityEntity);

            if (existCity == null) {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_CITY),
                        environment.getProperty(ENTITY_CITY_ID),
                        String.valueOf(cityEntity.getCityId())));
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

    @PostMapping(END_POINT_ADD_CITY)
    private String add(@Validated @RequestBody CityEntity cityEntity) {
        CityResponse response = new CityResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity createdBy = checkEntityExistOrNot(cityEntity.getCreatedBy().getAdminId());

            if (createdBy == null) {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(cityEntity.getCreatedBy().getAdminId()),
                        environment.getProperty(OPERATION_NAME_CREATE),
                        environment.getProperty(ENTITY_CITY)));
            } else {
                ProvinceEntity province = checkEntityExistOrNot(cityEntity.getProvince());

                if (province == null) {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_PROVINCE),
                            environment.getProperty(ENTITY_PROVINCE_ID),
                            String.valueOf(cityEntity.getProvince().getProvinceId()),
                            environment.getProperty(OPERATION_NAME_CREATE),
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
                            environment.getProperty(OPERATION_NAME_CREATE)
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

    @PostMapping(END_POINT_UPDATE_CITY)
    private String update(@Validated @RequestBody CityEntity cityEntity) {
        CityResponse response = new CityResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            CityEntity existCity = checkEntityExistOrNot(cityEntity);

            if (existCity == null) {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_CITY),
                        environment.getProperty(ENTITY_CITY_ID),
                        String.valueOf(cityEntity.getCityId())));
            } else {
                ProvinceEntity existProvince = checkEntityExistOrNot(cityEntity.getProvince());

                if (existProvince == null) {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_PROVINCE),
                            environment.getProperty(ENTITY_PROVINCE_ID),
                            String.valueOf(cityEntity.getProvince().getProvinceId()),
                            environment.getProperty(OPERATION_NAME_UPDATE),
                            environment.getProperty(ENTITY_CITY)));
                } else {
                    AdminEntity updatedBy = checkEntityExistOrNot(cityEntity.getUpdatedBy().getAdminId());

                    if (updatedBy == null) {
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_ADMIN),
                                environment.getProperty(ENTITY_ADMIN_ID),
                                String.valueOf(cityEntity.getUpdatedBy().getAdminId()),
                                environment.getProperty(OPERATION_NAME_UPDATE),
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
                                        environment.getProperty(OPERATION_NAME_UPDATE)));
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

    @PostMapping(END_POINT_DELETE_CITY)
    private String delete(@Validated @RequestBody CityEntity cityEntity) {
        CityResponse response = new CityResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            CityEntity existCity = checkEntityExistOrNot(cityEntity);

            if (existCity == null) {
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
                                environment.getProperty(OPERATION_NAME_DELETE)));

                cityRepository.delete(existCity);
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
}
