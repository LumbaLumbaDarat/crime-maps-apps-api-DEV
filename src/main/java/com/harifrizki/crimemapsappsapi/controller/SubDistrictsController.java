package com.harifrizki.crimemapsappsapi.controller;

import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import com.harifrizki.crimemapsappsapi.entity.CityEntity;
import com.harifrizki.crimemapsappsapi.entity.ProvinceEntity;
import com.harifrizki.crimemapsappsapi.entity.SubDistrictEntity;
import com.harifrizki.crimemapsappsapi.model.SubDistrictModel;
import com.harifrizki.crimemapsappsapi.model.response.GeneralMessageResponse;
import com.harifrizki.crimemapsappsapi.model.response.SubDistrictResponse;
import com.harifrizki.crimemapsappsapi.repository.AdminRepository;
import com.harifrizki.crimemapsappsapi.repository.CityRepository;
import com.harifrizki.crimemapsappsapi.repository.ProvinceRepository;
import com.harifrizki.crimemapsappsapi.repository.SubDistrictRepository;
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
public class SubDistrictsController extends ControllerService {

    @Autowired
    private SubDistrictRepository subDistrictRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PaginationServiceImpl paginationService;

    @Autowired
    private Environment environment;

    @PostMapping(END_POINT_SUB_DISTRICT)
    private String getAllSubDistrict(@RequestParam String searchBy,
                                     @RequestParam int pageNo,
                                     @Validated @RequestBody SubDistrictEntity subDistrictEntity) {
        SubDistrictResponse response = new SubDistrictResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            int realPage = pageNo - 1;
            Pageable pageable = PageRequest.
                    of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                            Sort.by(Sort.Direction.ASC, "sub_district_name"));

            Page<SubDistrictEntity> page = null;
            switch (searchBy) {
                case PARAM_NAME:
                    page = subDistrictRepository.findByName(
                            pageable,
                            subDistrictEntity.getSubDistrictName());
                    break;
                case PARAM_PROVINCE_ID:
                    ProvinceEntity existProvince = checkEntityExistOrNot(subDistrictEntity.getProvince());

                    if (existProvince == null)
                    {
                        message.setSuccess(false);
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_PROVINCE),
                                environment.getProperty(ENTITY_PROVINCE_ID),
                                String.valueOf(subDistrictEntity.getProvince().getProvinceId()),
                                environment.getProperty(OPERATION_NAME_READ_ALL),
                                environment.getProperty(ENTITY_SUB_DISTRICT)));

                        response.setMessage(message);
                        return response.toJson(response, OPERATION_SELECT);
                    } else page = subDistrictRepository.findByProvinceId(
                            pageable,
                            subDistrictEntity.getProvince().getProvinceId(),
                            subDistrictEntity.getSubDistrictName());
                    break;
                case PARAM_CITY_ID:
                    CityEntity existCity = checkEntityExistOrNot(subDistrictEntity.getCity());

                    if (existCity == null)
                    {
                        message.setSuccess(false);
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_CITY),
                                environment.getProperty(ENTITY_CITY_ID),
                                String.valueOf(subDistrictEntity.getCity().getCityId()),
                                environment.getProperty(OPERATION_NAME_READ_ALL),
                                environment.getProperty(ENTITY_SUB_DISTRICT)));

                        response.setMessage(message);
                        return response.toJson(response, OPERATION_SELECT);
                    } else page = subDistrictRepository.findByCityId(
                            pageable,
                            subDistrictEntity.getCity().getCityId(),
                            subDistrictEntity.getSubDistrictName());
                    break;
            }

            ArrayList<SubDistrictEntity> subDistrictEntities = new ArrayList<>(page.getContent());
            ArrayList<SubDistrictModel> subDistrictModels = new ArrayList<>();
            for (SubDistrictEntity subDistrict : subDistrictEntities) {
                subDistrictModels.add(new SubDistrictModel().
                        convertFromEntityToModel(
                                subDistrict,
                                subDistrict.getProvince(),
                                subDistrict.getCity(),
                                subDistrict.getCreatedBy(),
                                subDistrict.getUpdatedBy())
                );
            }

            response.setSubDistricts(subDistrictModels);
            response.setPage(paginationService.getPagination(
                    pageNo, END_POINT_SUB_DISTRICT,
                    page, new String[]{PARAM_SEARCH_BY, PARAM_PAGE_NO}, new String[]{searchBy}));

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

    @PostMapping(END_POINT_DETAIL_SUB_DISTRICT)
    private String getSubDistrict(@Validated @RequestBody SubDistrictEntity subDistrictEntity) {
        SubDistrictResponse response = new SubDistrictResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            SubDistrictEntity existSubDistrict = checkEntityExistOrNot(subDistrictEntity);

            if (existSubDistrict == null) {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_SUB_DISTRICT),
                        environment.getProperty(ENTITY_SUB_DISTRICT_ID),
                        String.valueOf(subDistrictEntity.getSubDistrictId())));
            } else {
                response.setSubDistrict(new SubDistrictModel().
                        convertFromEntityToModel(
                                existSubDistrict,
                                existSubDistrict.getProvince(),
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

    @PostMapping(END_POINT_ADD_SUB_DISTRICT)
    private String add(@Validated @RequestBody SubDistrictEntity subDistrictEntity) {
        SubDistrictResponse response = new SubDistrictResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity createdBy = checkEntityExistOrNot(
                    subDistrictEntity.getCreatedBy().getAdminId());

            if (createdBy == null) {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(subDistrictEntity.getCreatedBy().getAdminId()),
                        environment.getProperty(OPERATION_NAME_CREATE),
                        environment.getProperty(ENTITY_SUB_DISTRICT)));
            } else {
                ProvinceEntity province = checkEntityExistOrNot(
                        subDistrictEntity.getProvince());

                if (province == null) {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_PROVINCE),
                            environment.getProperty(ENTITY_PROVINCE_ID),
                            String.valueOf(subDistrictEntity.getProvince().getProvinceId()),
                            environment.getProperty(OPERATION_NAME_CREATE),
                            environment.getProperty(ENTITY_SUB_DISTRICT)));
                } else {
                    CityEntity city = checkEntityExistOrNot(
                            subDistrictEntity.getCity());

                    if (city == null) {
                        message.setSuccess(false);
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_CITY),
                                environment.getProperty(ENTITY_CITY_ID),
                                String.valueOf(subDistrictEntity.getCity().getCityId()),
                                environment.getProperty(OPERATION_NAME_CREATE),
                                environment.getProperty(ENTITY_SUB_DISTRICT)));
                    } else {
                        subDistrictEntity.setProvince(province);
                        subDistrictEntity.setCity(city);
                        subDistrictEntity.setCreatedBy(createdBy);
                        subDistrictEntity.setCreatedDate(LocalDateTime.now());
                        subDistrictEntity.setUpdatedBy(null);

                        SubDistrictEntity result = subDistrictRepository.save(subDistrictEntity);

                        response.setSubDistrict(new SubDistrictModel().
                                convertFromEntityToModel(
                                        result,
                                        result.getProvince(),
                                        result.getCity(),
                                        result.getCreatedBy(),
                                        result.getUpdatedBy()));

                        message.setSuccess(true);
                        message.setMessage(successProcess(
                                environment.getProperty(ENTITY_SUB_DISTRICT),
                                environment.getProperty(OPERATION_NAME_CREATE)
                        ));
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

    @PostMapping(END_POINT_UPDATE_SUB_DISTRICT)
    private String update(@Validated @RequestBody SubDistrictEntity subDistrictEntity) {
        SubDistrictResponse response = new SubDistrictResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            SubDistrictEntity existSubDistrict = checkEntityExistOrNot(
                    subDistrictEntity);

            if (existSubDistrict == null) {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_SUB_DISTRICT),
                        environment.getProperty(ENTITY_SUB_DISTRICT_ID),
                        String.valueOf(subDistrictEntity.getSubDistrictId())));
            } else {
                ProvinceEntity existProvince = checkEntityExistOrNot(
                        subDistrictEntity.getProvince());

                if (existProvince == null) {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_PROVINCE),
                            environment.getProperty(ENTITY_PROVINCE_ID),
                            String.valueOf(subDistrictEntity.getCity().getCityId()),
                            environment.getProperty(OPERATION_NAME_UPDATE),
                            environment.getProperty(ENTITY_SUB_DISTRICT)));
                } else {
                    CityEntity existCity = checkEntityExistOrNot(
                            subDistrictEntity.getCity());

                    if (existCity == null) {
                        message.setSuccess(false);
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_CITY),
                                environment.getProperty(ENTITY_CITY_ID),
                                String.valueOf(subDistrictEntity.getProvince().getProvinceId()),
                                environment.getProperty(OPERATION_NAME_UPDATE),
                                environment.getProperty(ENTITY_SUB_DISTRICT)));
                    } else {
                        AdminEntity updatedBy = checkEntityExistOrNot(
                                subDistrictEntity.getUpdatedBy().getAdminId());

                        if (updatedBy == null) {
                            message.setMessage(existEntityNotFound(
                                    environment.getProperty(ENTITY_ADMIN),
                                    environment.getProperty(ENTITY_ADMIN_ID),
                                    String.valueOf(subDistrictEntity.getUpdatedBy().getAdminId()),
                                    environment.getProperty(OPERATION_NAME_UPDATE),
                                    environment.getProperty(ENTITY_SUB_DISTRICT)));
                        } else {
                            existSubDistrict.setSubDistrictName(subDistrictEntity.getSubDistrictName());
                            existSubDistrict.setProvince(existProvince);
                            existSubDistrict.setCity(existCity);
                            existSubDistrict.setUpdatedBy(updatedBy);
                            existSubDistrict.setUpdatedDate(LocalDateTime.now());

                            SubDistrictEntity result = subDistrictRepository.save(existSubDistrict);

                            response.setSubDistrict(new SubDistrictModel().
                                    convertFromEntityToModel(
                                            result,
                                            result.getProvince(),
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
                                            environment.getProperty(OPERATION_NAME_UPDATE)));
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

    @PostMapping(END_POINT_DELETE_SUB_DISTRICT)
    private String delete(@Validated @RequestBody SubDistrictEntity subDistrictEntity) {
        SubDistrictResponse response = new SubDistrictResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            SubDistrictEntity existSubDistrict = checkEntityExistOrNot(
                    subDistrictEntity);

            if (existSubDistrict == null) {
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
                                environment.getProperty(OPERATION_NAME_DELETE)));

                subDistrictRepository.delete(existSubDistrict);
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
}
