package com.harifrizki.crimemapsappsapi.controller;

import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import com.harifrizki.crimemapsappsapi.entity.ProvinceEntity;
import com.harifrizki.crimemapsappsapi.model.ProvinceModel;
import com.harifrizki.crimemapsappsapi.model.response.GeneralMessageResponse;
import com.harifrizki.crimemapsappsapi.model.response.ProvinceResponse;
import com.harifrizki.crimemapsappsapi.repository.AdminRepository;
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
public class ProvinceController extends ControllerService {

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PaginationServiceImpl paginationService;

    @Autowired
    private Environment environment;

    @PostMapping(END_POINT_PROVINCE)
    private String getAllProvince(@RequestParam String searchBy,
                                  @RequestParam int pageNo,
                                  @Validated @RequestBody ProvinceEntity provinceEntity) {
        ProvinceResponse response = new ProvinceResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            int realPage = pageNo - 1;
            Pageable pageable = PageRequest.
                    of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                            Sort.by(Sort.Direction.ASC, "province_name"));

            Page<ProvinceEntity> page = null;
            if (searchBy.equalsIgnoreCase(PARAM_NAME))
                page = provinceRepository.findByName(pageable,
                        provinceEntity.getProvinceName());

            ArrayList<ProvinceEntity> provinceEntities = new ArrayList<>(page.getContent());

            ArrayList<ProvinceModel> provinceModels = new ArrayList<>();
            for (ProvinceEntity province : provinceEntities) {
                provinceModels.add(new ProvinceModel().
                        convertFromEntityToModel(
                                province,
                                province.getCreatedBy(),
                                province.getUpdatedBy())
                );
            }

            response.setProvinces(provinceModels);
            response.setPage(paginationService.getPagination(
                    pageNo, END_POINT_PROVINCE,
                    page, new String[]{PARAM_SEARCH_BY, PARAM_PAGE_NO}, new String[]{searchBy}));

            message.setSuccess(true);
            message.setMessage(
                    successProcess(
                            SUCCESS_SELECT_ALL,
                            environment.getProperty(ENTITY_PROVINCE)));
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_SELECT);
    }

    @PostMapping(END_POINT_DETAIL_PROVINCE)
    private String getProvince(@Validated @RequestBody ProvinceEntity provinceEntity) {
        ProvinceResponse response = new ProvinceResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            ProvinceEntity existProvince = checkEntityExistOrNot(provinceEntity);

            if (existProvince == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_PROVINCE),
                        environment.getProperty(ENTITY_PROVINCE_ID),
                        String.valueOf(provinceEntity.getProvinceId())));
            } else {
                response.setProvince(new ProvinceModel().
                        convertFromEntityToModel(
                                existProvince,
                                existProvince.getCreatedBy(),
                                existProvince.getUpdatedBy()));

                message.setSuccess(true);
                message.setMessage(
                        successProcess(
                                SUCCESS_SELECT_DETAIL,
                                environment.getProperty(ENTITY_PROVINCE)));
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @PostMapping(END_POINT_ADD_PROVINCE)
    private String add(@Validated @RequestBody ProvinceEntity provinceEntity) {
        ProvinceResponse response = new ProvinceResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity createdBy = checkEntityExistOrNot(provinceEntity.getCreatedBy().getAdminId());

            if (createdBy == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(provinceEntity.getCreatedBy().getAdminId()),
                        environment.getProperty(OPERATION_NAME_CREATE),
                        environment.getProperty(ENTITY_PROVINCE)));
            } else {
                provinceEntity.setCreatedBy(createdBy);
                provinceEntity.setCreatedDate(LocalDateTime.now());
                provinceEntity.setUpdatedBy(null);

                ProvinceEntity result = provinceRepository.save(provinceEntity);

                response.setProvince(new ProvinceModel().
                        convertFromEntityToModel(
                                result,
                                result.getCreatedBy(),
                                result.getUpdatedBy()));

                message.setSuccess(true);
                message.setMessage(
                        successProcess(
                                environment.getProperty(ENTITY_PROVINCE),
                                environment.getProperty(OPERATION_NAME_CREATE)
                        ));
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @PostMapping(END_POINT_UPDATE_PROVINCE)
    private String update(@Validated @RequestBody ProvinceEntity provinceEntity) {
        ProvinceResponse response = new ProvinceResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            ProvinceEntity existProvince = checkEntityExistOrNot(provinceEntity);

            if (existProvince == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_PROVINCE),
                        environment.getProperty(ENTITY_PROVINCE_ID),
                        String.valueOf(provinceEntity.getProvinceId())));
            } else {
                AdminEntity updatedBy = checkEntityExistOrNot(provinceEntity.getUpdatedBy().getAdminId());

                if (updatedBy == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(provinceEntity.getUpdatedBy().getAdminId()),
                            environment.getProperty(OPERATION_NAME_UPDATE),
                            environment.getProperty(ENTITY_PROVINCE)));
                } else {
                    existProvince.setProvinceName(provinceEntity.getProvinceName());
                    existProvince.setUpdatedBy(updatedBy);
                    existProvince.setUpdatedDate(LocalDateTime.now());

                    ProvinceEntity result = provinceRepository.save(existProvince);

                    response.setProvince(new ProvinceModel().
                            convertFromEntityToModel(
                                    result,
                                    result.getCreatedBy(),
                                    result.getUpdatedBy()));

                    message.setSuccess(true);
                    message.setMessage(
                            successProcess(
                                    environment.getProperty(ENTITY_PROVINCE),
                                    environment.getProperty(ENTITY_PROVINCE_ID),
                                    String.valueOf(existProvince.getProvinceId()),
                                    environment.getProperty(ENTITY_PROVINCE_NAME),
                                    existProvince.getProvinceName(),
                                    environment.getProperty(OPERATION_NAME_UPDATE)));
                }
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @PostMapping(END_POINT_DELETE_PROVINCE)
    private String delete(@Validated @RequestBody ProvinceEntity provinceEntity) {
        ProvinceResponse response = new ProvinceResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            ProvinceEntity existProvince = checkEntityExistOrNot(provinceEntity);

            if (existProvince == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_PROVINCE),
                        environment.getProperty(ENTITY_PROVINCE_ID),
                        String.valueOf(provinceEntity.getProvinceId())));
            } else {
                message.setSuccess(true);
                message.setMessage(
                        successProcess(
                                environment.getProperty(ENTITY_PROVINCE),
                                environment.getProperty(ENTITY_PROVINCE_ID),
                                String.valueOf(existProvince.getProvinceId()),
                                environment.getProperty(ENTITY_PROVINCE_NAME),
                                existProvince.getProvinceName(),
                                environment.getProperty(OPERATION_NAME_DELETE)));

                provinceRepository.delete(existProvince);
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
        return  provinceRepository.findById(provinceEntity.getProvinceId()).orElse(null);
    }
}
