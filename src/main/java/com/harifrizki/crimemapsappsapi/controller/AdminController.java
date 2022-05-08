package com.harifrizki.crimemapsappsapi.controller;

import com.google.gson.Gson;
import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import com.harifrizki.crimemapsappsapi.model.AdminModel;
import com.harifrizki.crimemapsappsapi.model.response.*;
import com.harifrizki.crimemapsappsapi.network.response.ImageStorageResponse;
import com.harifrizki.crimemapsappsapi.repository.AdminRepository;
import com.harifrizki.crimemapsappsapi.service.ResponseImageService;
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
import java.util.ArrayList;
import java.util.UUID;

import static com.harifrizki.crimemapsappsapi.utils.AppsConstants.*;
import static com.harifrizki.crimemapsappsapi.utils.ControllerConstants.*;
import static com.harifrizki.crimemapsappsapi.utils.UtilizationClass.*;

@RestController
@RequestMapping(GENERAL_CONTROLLER_URL)
@Validated
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ImageServiceImpl imageService;

    @Autowired
    private PaginationServiceImpl paginationService;

    @Autowired
    private Environment environment;

    @GetMapping(ADMIN_GET_ALL_CONTROLLER)
    @ResponseBody
    private String getAllAdmin(@RequestParam int pageNo) {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            int realPage = pageNo - 1;
            Pageable pageable = PageRequest.
                    of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                            Sort.by(Sort.Direction.ASC, "adminUsername"));

            Page<AdminEntity> page = adminRepository.findAll(pageable);
            ArrayList<AdminEntity> adminEntities = new ArrayList<>(page.getContent());

            ArrayList<AdminModel> adminModels = new ArrayList<>();
            for (AdminEntity adminEntity : adminEntities)
            {
                AdminEntity createdBy = null;
                if (adminEntity.getCreatedBy() != null)
                    createdBy = checkAdminWasExistOrNot(adminEntity.getCreatedBy());

                AdminEntity updatedBy = null;
                if (adminEntity.getUpdatedBy() != null)
                    updatedBy = checkAdminWasExistOrNot(adminEntity.getUpdatedBy());

                adminModels.add(new AdminModel().
                        convertFromEntityToModel(
                                adminEntity,
                                createdBy,
                                updatedBy)
                );
            }

            response.setAdmins(adminModels);
            response.setPage(paginationService.getPagination(
                    pageNo, ADMIN_GET_ALL_CONTROLLER,
                    page, new String[]{"pageNo"}, new String[]{}));

            message.setSuccess(true);
            message.setMessage(
                    successProcess(
                            SUCCESS_SELECT_ALL,
                            environment.getProperty(ENTITY_ADMIN)));
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_SELECT);
    }

    @PostMapping(ADMIN_GET_ALL_SEARCH_NAME_CONTROLLER)
    private String getAllAdmin(@RequestParam int pageNo,
                               @Validated @RequestParam("adminName") String adminName) {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            int realPage = pageNo - 1;
            Pageable pageable = PageRequest.
                    of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                            Sort.by(Sort.Direction.ASC, "admin_name"));

            Page<AdminEntity> page = adminRepository.findByName(pageable, adminName);
            ArrayList<AdminEntity> adminEntities = new ArrayList<>(page.getContent());

            ArrayList<AdminModel> adminModels = new ArrayList<>();
            for (AdminEntity adminEntity : adminEntities)
            {
                AdminEntity createdBy = null;
                if (adminEntity.getCreatedBy() != null)
                    createdBy = checkAdminWasExistOrNot(adminEntity.getCreatedBy());

                AdminEntity updatedBy = null;
                if (adminEntity.getUpdatedBy() != null)
                    updatedBy = checkAdminWasExistOrNot(adminEntity.getUpdatedBy());

                adminModels.add(new AdminModel().
                        convertFromEntityToModel(
                                adminEntity,
                                createdBy,
                                updatedBy)
                );
            }

            response.setAdmins(adminModels);
            response.setPage(paginationService.getPagination(
                    pageNo, ADMIN_GET_ALL_SEARCH_NAME_CONTROLLER,
                    page, new String[]{"pageNo"}, new String[]{}));

            message.setSuccess(true);
            message.setMessage(
                    successProcess(
                            SUCCESS_SELECT_ALL,
                            environment.getProperty(ENTITY_ADMIN)));
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_SELECT);
    }

    @PostMapping(ADMIN_SEARCH_ID_CONTROLLER)
    private String getAdmin(@Validated @RequestParam("adminId") UUID adminId) {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity existAdmin = checkAdminWasExistOrNot(adminId);

            if (existAdmin == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(adminId)));
            } else {
                AdminEntity createdBy = null;
                if (existAdmin.getCreatedBy() != null)
                    createdBy = checkAdminWasExistOrNot(existAdmin.getCreatedBy());

                AdminEntity updatedBy = null;
                if (existAdmin.getUpdatedBy() != null)
                    updatedBy = checkAdminWasExistOrNot(existAdmin.getUpdatedBy());

                response.setAdmin(
                        new AdminModel().
                                convertFromEntityToModel(
                                        existAdmin, createdBy, updatedBy));

                message.setSuccess(true);
                message.setMessage(
                        successProcess(
                                SUCCESS_SELECT_DETAIL,
                                environment.getProperty(ENTITY_ADMIN)));
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @RequestMapping(
            path = ADMIN_ADD_CONTROLLER,
            method = RequestMethod.POST,
            consumes = {"multipart/form-data"})
    private String add(@Validated @RequestParam("adminEntity") String jsonAdminEntity,
                       @Validated @RequestParam("adminPhotoProfile") MultipartFile photoProfile) {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity adminEntity = new Gson().fromJson(jsonAdminEntity, AdminEntity.class);
            AdminEntity existAdmin = adminRepository.findByUsername(adminEntity.getAdminUsername());
            AdminEntity createdBy = checkAdminWasExistOrNot(adminEntity.getCreatedBy());

            if (existAdmin != null)
            {
                message.setSuccess(false);
                message.setMessage("Failed [Created New] Admin. This Admin was Created at ".
                        concat(existAdmin.getCreatedDate().toString()));
            } else {
                if (createdBy == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(adminEntity.getCreatedBy()),
                            "Created New",
                            environment.getProperty(ENTITY_ADMIN)));
                } else {
                    adminEntity.setAdminPassword(hashPassword(environment.getProperty(DEFAULT_PASSWORD_ADMIN)));
                    adminEntity.setCreatedDate(LocalDateTime.now());
                    adminEntity.setCreatedBy(createdBy.getAdminId());

                    AdminEntity result = adminRepository.save(adminEntity);

                    imageService.setResponseImageService(new ResponseImageService() {
                        @Override
                        public void onResponse(AdminEntity admin, ImageStorageResponse imageStorageResponse) {
                            super.onResponse(admin, imageStorageResponse);
                            if (imageStorageResponse.getSuccess())
                            {
                                admin.setAdminImage(imageStorageResponse.getValue());
                                admin = adminRepository.save(admin);

                                response.setAdmin(
                                        new AdminModel().
                                                convertFromEntityToModel(
                                                        admin, createdBy, null));

                                message.setSuccess(true);
                                message.setMessage(
                                        successProcess(
                                                environment.getProperty(ENTITY_ADMIN),
                                                "Added New"));
                            } else {
                                adminRepository.delete(admin);

                                message.setSuccess(false);
                                message.setMessage(imageStorageResponse.getMessage());
                            }
                        }
                    });
                    imageService.upload(environment, result, photoProfile);
                }
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @PostMapping(ADMIN_ADD_DEFAULT_ROOT_ADMIN_CONTROLLER)
    private String addDefaultAdmin() {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity existAdmin = adminRepository.
                    findByUsername(environment.getProperty(DEFAULT_ADMIN_FIRST_ROOT_USERNAME));

            if (existAdmin != null)
            {
                message.setSuccess(false);
                message.setMessage("Failed [Added] Default Admin. This Admin was Added at ".
                        concat(existAdmin.getCreatedDate().toString()));
            } else {
                AdminEntity defaultAdmin = new AdminEntity();
                defaultAdmin.setAdminName(environment.getProperty(DEFAULT_ADMIN_FIRST_ROOT_NAME));
                defaultAdmin.setAdminUsername(environment.getProperty(DEFAULT_ADMIN_FIRST_ROOT_USERNAME));
                defaultAdmin.setAdminRole(environment.getProperty(ADMIN_ROLE_ROOT));
                defaultAdmin.setAdminPassword(hashPassword(environment.getProperty(DEFAULT_ADMIN_FIRST_ROOT_PASSWORD)));
                defaultAdmin.setAdminImage(environment.getProperty(DEFAULT_IMAGE_ADMIN));
                defaultAdmin.setCreatedDate(LocalDateTime.now());
                defaultAdmin.setActive(true);

                AdminEntity result = adminRepository.save(defaultAdmin);

                response.setAdmin(new AdminModel().
                        convertFromEntityToModel(
                                result,
                                null,
                                null));

                message.setSuccess(true);
                message.setMessage("Successfully [Added] First Admin");
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @PostMapping(ADMIN_UPDATE_CONTROLLER)
    private String update(@Validated @RequestBody AdminEntity adminEntity) {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity existAdmin = checkAdminWasExistOrNot(adminEntity.getAdminId());
            AdminEntity updatedBy = checkAdminWasExistOrNot(adminEntity.getUpdatedBy());

            if (existAdmin == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(adminEntity.getAdminId())));
            } else {
                if (updatedBy == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(adminEntity.getUpdatedBy()),
                            "Updated Existing",
                            environment.getProperty(ENTITY_ADMIN)));
                } else {
                    AdminEntity createdBy = null;
                    if (existAdmin.getCreatedBy() != null)
                        createdBy = checkAdminWasExistOrNot(existAdmin.getCreatedBy());

                    existAdmin.setAdminName(adminEntity.getAdminName());
                    existAdmin.setUpdatedBy(updatedBy.getAdminId());
                    existAdmin.setUpdatedDate(LocalDateTime.now());

                    response.setAdmin(
                            new AdminModel().
                                    convertFromEntityToModel(
                                            adminRepository.save(existAdmin),
                                            createdBy,
                                            updatedBy));

                    message.setSuccess(true);
                    message.setMessage(successProcess(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(adminEntity.getAdminId()),
                            environment.getProperty(ENTITY_ADMIN_USERNAME),
                            existAdmin.getAdminUsername(),
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

    @RequestMapping(
            path = ADMIN_UPDATE_IMAGE_PROFILE_CONTROLLER,
            method = RequestMethod.POST,
            consumes = {"multipart/form-data"})
    private String updatePhotoProfile(@Validated @RequestParam("adminEntity") String jsonAdminEntity,
                                      @Validated @RequestParam("adminPhotoProfile") MultipartFile photoProfile) {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity adminEntity = new Gson().fromJson(jsonAdminEntity, AdminEntity.class);
            AdminEntity existAdmin = checkAdminWasExistOrNot(adminEntity.getAdminId());
            AdminEntity updatedBy = checkAdminWasExistOrNot(adminEntity.getUpdatedBy());

            if (existAdmin == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(adminEntity.getAdminId())));
            } else {
                if (updatedBy == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(adminEntity.getUpdatedBy()),
                            "Updated Photo Profile Existing",
                            environment.getProperty(ENTITY_ADMIN)));
                } else {
                    imageService.setResponseImageService(new ResponseImageService() {
                        @Override
                        public void onResponse(AdminEntity admin, ImageStorageResponse imageStorageResponse) {
                            super.onResponse(admin, imageStorageResponse);
                            if (imageStorageResponse.getSuccess())
                            {
                                admin.setUpdatedBy(updatedBy.getAdminId());
                                admin.setUpdatedDate(LocalDateTime.now());

                                AdminEntity result = adminRepository.save(existAdmin);

                                response.setAdmin(
                                        new AdminModel().
                                                convertFromEntityToModel(
                                                        result,
                                                        checkAdminWasExistOrNot(result.getCreatedBy()),
                                                        updatedBy));

                                message.setSuccess(true);
                                message.setMessage(successProcess(
                                        environment.getProperty(ENTITY_ADMIN),
                                        environment.getProperty(ENTITY_ADMIN_ID),
                                        String.valueOf(adminEntity.getAdminId()),
                                        environment.getProperty(ENTITY_ADMIN_USERNAME),
                                        existAdmin.getAdminUsername(),
                                        "Updated Photo Profile"));
                            } else {
                                message.setSuccess(false);
                                message.setMessage(imageStorageResponse.getMessage());
                            }
                        }
                    });
                    imageService.update(environment, existAdmin, photoProfile);
                }
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @PostMapping(ADMIN_UPDATE_PASSWORD_CONTROLLER)
    private String updatePassword(@Validated @RequestParam("adminId") UUID adminId,
                                  @Validated @RequestParam("adminOldPassword") String adminOldPassword,
                                  @Validated @RequestParam("adminNewPassword") String adminNewPassword) {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity existAdmin = checkAdminWasExistOrNot(adminId);

            if (existAdmin == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(adminId)));
            } else {
                if (comparePassword(adminOldPassword, existAdmin.getAdminPassword()))
                {
                    existAdmin.setAdminPassword(hashPassword(adminNewPassword));
                    existAdmin.setUpdatedBy(adminId);
                    existAdmin.setUpdatedDate(LocalDateTime.now());

                    AdminEntity result = adminRepository.save(existAdmin);

                    AdminEntity createdBy = null;
                    if (result.getCreatedBy() != null)
                        createdBy = checkAdminWasExistOrNot(result.getCreatedBy());

                    AdminEntity updatedBy = null;
                    if (result.getUpdatedBy() != null)
                        updatedBy = checkAdminWasExistOrNot(result.getUpdatedBy());

                    response.setAdmin(
                            new AdminModel().
                                    convertFromEntityToModel(
                                            result,
                                            createdBy,
                                            updatedBy));

                    message.setSuccess(true);
                    message.setMessage(successProcess(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(adminId),
                            environment.getProperty(ENTITY_ADMIN_USERNAME),
                            existAdmin.getAdminUsername(),
                            "Update Password"));
                } else {
                    message.setSuccess(false);
                    message.setMessage("Failed [Updated Password], Existing Password was not Correct");
                }
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @PostMapping(ADMIN_RESET_PASSWORD_CONTROLLER)
    private String updatePassword(@Validated @RequestBody AdminEntity adminEntity) {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity existAdmin = checkAdminWasExistOrNot(adminEntity.getAdminId());
            AdminEntity updatedBy = checkAdminWasExistOrNot(adminEntity.getUpdatedBy());

            if (existAdmin == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(adminEntity.getAdminId())));
            } else {
                if (updatedBy == null)
                {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(adminEntity.getUpdatedBy()),
                            "Reset Password",
                            environment.getProperty(ENTITY_ADMIN)));
                } else {
                    AdminEntity createdBy = null;
                    if (existAdmin.getCreatedBy() != null)
                        createdBy = checkAdminWasExistOrNot(existAdmin.getCreatedBy());

                    existAdmin.setAdminPassword(hashPassword(environment.getProperty(DEFAULT_PASSWORD_ADMIN)));
                    existAdmin.setUpdatedBy(updatedBy.getAdminId());
                    existAdmin.setUpdatedDate(LocalDateTime.now());

                    response.setAdmin(
                            new AdminModel().
                                    convertFromEntityToModel(
                                            adminRepository.save(existAdmin),
                                            createdBy,
                                            updatedBy));

                    message.setSuccess(true);
                    message.setMessage(successProcess(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(adminEntity.getAdminId()),
                            environment.getProperty(ENTITY_ADMIN_USERNAME),
                            existAdmin.getAdminUsername(),
                            "Reset Password"));
                }
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @PostMapping(ADMIN_UPDATE_ACTIVE_CONTROLLER)
    private String updateActivated(@Validated @RequestBody AdminEntity adminEntity) {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity existAdmin = checkAdminWasExistOrNot(adminEntity.getAdminId());
            AdminEntity updatedBy = checkAdminWasExistOrNot(adminEntity.getUpdatedBy());

            if (existAdmin == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(adminEntity.getAdminId())));
            } else {
                if (updatedBy == null)
                {
                    message.setSuccess(false);
                    if (adminEntity.isActive())
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_ADMIN),
                                environment.getProperty(ENTITY_ADMIN_ID),
                                String.valueOf(adminEntity.getUpdatedBy()),
                                "Activated",
                                environment.getProperty(ENTITY_ADMIN)));
                    else message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(adminEntity.getUpdatedBy()),
                            "Deactivated",
                            environment.getProperty(ENTITY_ADMIN)));
                } else {
                    AdminEntity createdBy = null;
                    if (existAdmin.getCreatedBy() != null)
                        createdBy = checkAdminWasExistOrNot(existAdmin.getCreatedBy());

                    existAdmin.setActive(adminEntity.isActive());
                    existAdmin.setUpdatedBy(updatedBy.getAdminId());
                    existAdmin.setUpdatedDate(LocalDateTime.now());

                    response.setAdmin(
                            new AdminModel().
                                    convertFromEntityToModel(
                                            adminRepository.save(existAdmin),
                                            createdBy,
                                            updatedBy));

                    message.setSuccess(true);
                    if (adminEntity.isActive())
                        message.setMessage(
                                successProcess(
                                        environment.getProperty(ENTITY_ADMIN),
                                        environment.getProperty(ENTITY_ADMIN_ID),
                                        String.valueOf(adminEntity.getAdminId()),
                                        environment.getProperty(ENTITY_ADMIN_USERNAME),
                                        existAdmin.getAdminUsername(),
                                        "Activated"));
                    else message.setMessage(
                            successProcess(
                                    environment.getProperty(ENTITY_ADMIN),
                                    environment.getProperty(ENTITY_ADMIN_ID),
                                    String.valueOf(adminEntity.getAdminId()),
                                    environment.getProperty(ENTITY_ADMIN_USERNAME),
                                    existAdmin.getAdminUsername(),
                                    "Deactivated"));
                }
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @PostMapping(ADMIN_DELETE_CONTROLLER)
    private String delete(@Validated @RequestBody AdminEntity adminEntity) {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity existAdmin = checkAdminWasExistOrNot(adminEntity.getAdminId());

            if (existAdmin == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(adminEntity.getAdminId())));
            } else {
                imageService.setResponseImageService(new ResponseImageService() {
                    @Override
                    public void onResponse(ImageStorageResponse response) {
                        super.onResponse(response);
                        if (response.getSuccess())
                        {
                            adminRepository.delete(existAdmin);

                            message.setSuccess(true);
                            message.setMessage(
                                    successProcess(
                                            environment.getProperty(ENTITY_ADMIN),
                                            environment.getProperty(ENTITY_ADMIN_ID),
                                            String.valueOf(adminEntity.getAdminId()),
                                            environment.getProperty(ENTITY_ADMIN_USERNAME),
                                            existAdmin.getAdminUsername(),
                                            "Deleted"));
                        } else {
                            message.setSuccess(false);
                            message.setMessage(response.getMessage());
                        }
                    }
                });
                imageService.delete(existAdmin);
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
}
