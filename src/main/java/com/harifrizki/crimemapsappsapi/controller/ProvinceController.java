package com.harifrizki.crimemapsappsapi.controller;

import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import com.harifrizki.crimemapsappsapi.entity.ProvinceEntity;
import com.harifrizki.crimemapsappsapi.model.ProvinceModel;
import com.harifrizki.crimemapsappsapi.model.response.GeneralMessageResponse;
import com.harifrizki.crimemapsappsapi.model.response.ProvinceResponse;
import com.harifrizki.crimemapsappsapi.repository.AdminRepository;
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
public class ProvinceController {

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PaginationServiceImpl paginationService;

    @Autowired
    private Environment environment;

    @GetMapping(PROVINCE_GET_ALL_CONTROLLER)
    @ResponseBody
    private String getAllProvince(@RequestParam int pageNo) {
        ProvinceResponse response = new ProvinceResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            int realPage = pageNo - 1;
            Pageable pageable = PageRequest.
                    of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                            Sort.by(Sort.Direction.ASC, "provinceName"));

            Page<ProvinceEntity> page = provinceRepository.findAll(pageable);
            ArrayList<ProvinceEntity> provinceEntities = new ArrayList<>(page.getContent());

            ArrayList<ProvinceModel> provinceModels = new ArrayList<>();
            for (ProvinceEntity provinceEntity : provinceEntities) {
                provinceModels.add(new ProvinceModel().
                        convertFromEntityToModel(
                                provinceEntity,
                                provinceEntity.getCreatedBy(),
                                provinceEntity.getUpdatedBy())
                );
            }

            response.setProvinces(provinceModels);
            response.setPage(paginationService.getPagination(
                    pageNo, PROVINCE_GET_ALL_CONTROLLER,
                    page, new String[]{"pageNo"}, new String[]{}));

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

    @PostMapping(PROVINCE_GET_ALL_SEARCH_NAME_CONTROLLER)
    private String getAllProvince(@RequestParam int pageNo,
                                  @Validated @RequestParam("provinceName") String provinceName) {
        ProvinceResponse response = new ProvinceResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            int realPage = pageNo - 1;
            Pageable pageable = PageRequest.
                    of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                            Sort.by(Sort.Direction.ASC, "province_name"));

            Page<ProvinceEntity> page = provinceRepository.findByName(pageable, provinceName);
            ArrayList<ProvinceEntity> provinceEntities = new ArrayList<>(page.getContent());

            ArrayList<ProvinceModel> provinceModels = new ArrayList<>();
            for (ProvinceEntity provinceEntity : provinceEntities) {
                provinceModels.add(new ProvinceModel().
                        convertFromEntityToModel(
                                provinceEntity,
                                provinceEntity.getCreatedBy(),
                                provinceEntity.getUpdatedBy())
                );
            }

            response.setProvinces(provinceModels);
            response.setPage(paginationService.getPagination(
                    pageNo, PROVINCE_GET_ALL_SEARCH_NAME_CONTROLLER,
                    page, new String[]{"pageNo"}, new String[]{}));

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

    @PostMapping(PROVINCE_GET_SEARCH_ID_CONTROLLER)
    private String getProvince(@Validated @RequestParam("provinceId") UUID provinceId) {
        ProvinceResponse response = new ProvinceResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            ProvinceEntity existProvince = checkProvinceWasExistOrNot(provinceId);

            if (existProvince == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_PROVINCE),
                        environment.getProperty(ENTITY_PROVINCE_ID),
                        String.valueOf(provinceId)));
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

    @PostMapping(PROVINCE_ADD_CONTROLLER)
    private String add(@Validated @RequestBody ProvinceEntity provinceEntity) {
        ProvinceResponse response = new ProvinceResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity createdBy = checkAdminWasExistOrNot(provinceEntity.getCreatedBy().getAdminId());

            if (createdBy == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(provinceEntity.getCreatedBy().getAdminId()),
                        "Created New",
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
                                "Added New"
                        ));
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @PostMapping(PROVINCE_UPDATE_CONTROLLER)
    private String update(@Validated @RequestBody ProvinceEntity provinceEntity) {
        ProvinceResponse response = new ProvinceResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            ProvinceEntity existProvince = checkProvinceWasExistOrNot(provinceEntity.getProvinceId());

            if (existProvince == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_PROVINCE),
                        environment.getProperty(ENTITY_PROVINCE_ID),
                        String.valueOf(provinceEntity.getProvinceId())));
            } else {
                AdminEntity updatedBy = checkAdminWasExistOrNot(provinceEntity.getUpdatedBy().getAdminId());

                if (updatedBy == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(provinceEntity.getUpdatedBy().getAdminId()),
                            "Updated Existing",
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
                                    "Updated"));
                }
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @PostMapping(PROVINCE_DELETE_CONTROLLER)
    private String delete(@Validated @RequestBody ProvinceEntity provinceEntity) {
        ProvinceResponse response = new ProvinceResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            ProvinceEntity existProvince = checkProvinceWasExistOrNot(provinceEntity.getProvinceId());

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
                                "Deleted"));

                provinceRepository.delete(existProvince);
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
}
