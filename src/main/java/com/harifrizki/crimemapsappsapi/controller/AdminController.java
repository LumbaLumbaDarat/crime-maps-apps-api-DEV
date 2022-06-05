package com.harifrizki.crimemapsappsapi.controller;

import com.google.gson.Gson;
import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import com.harifrizki.crimemapsappsapi.model.AdminModel;
import com.harifrizki.crimemapsappsapi.model.response.*;
import com.harifrizki.crimemapsappsapi.network.response.ImageStorageResponse;
import com.harifrizki.crimemapsappsapi.repository.AdminRepository;
import com.harifrizki.crimemapsappsapi.service.ControllerService;
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
@RequestMapping(GENERAL_END_POINT)
@Validated
public class AdminController extends ControllerService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ImageServiceImpl imageService;

    @Autowired
    private PaginationServiceImpl paginationService;

    @Autowired
    private Environment environment;

    @PostMapping(END_POINT_ADMIN)
    private String getAllAdmin(@RequestParam String searchBy,
                               @RequestParam int pageNo,
                               @Validated @RequestBody AdminEntity adminEntity) {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            int realPage = pageNo - 1;
            Pageable pageable = PageRequest.
                    of(realPage, Integer.parseInt(environment.getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)),
                            Sort.by(Sort.Direction.ASC, "admin_username"));

            Page<AdminEntity> page = null;
            if (searchBy.equalsIgnoreCase(PARAM_NAME))
                page = adminRepository.findByName(pageable, adminEntity.getAdminName());

            ArrayList<AdminEntity> adminEntities = new ArrayList<>(page.getContent());

            ArrayList<AdminModel> adminModels = new ArrayList<>();
            for (AdminEntity admin : adminEntities) {
                AdminEntity createdBy = null;
                if (admin.getCreatedBy() != null)
                    createdBy = checkEntityExistOrNot(admin.getCreatedBy());

                AdminEntity updatedBy = null;
                if (admin.getUpdatedBy() != null)
                    updatedBy = checkEntityExistOrNot(admin.getUpdatedBy());

                adminModels.add(new AdminModel().
                        convertFromEntityToModel(
                                admin,
                                createdBy,
                                updatedBy)
                );
            }

            response.setAdmins(adminModels);
            response.setPage(paginationService.getPagination(
                    pageNo, END_POINT_ADMIN,
                    page, new String[]{PARAM_SEARCH_BY, PARAM_PAGE_NO}, new String[]{searchBy}));

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

    @PostMapping(END_POINT_DETAIL_ADMIN)
    private String getAdmin(@Validated @RequestParam("adminId") UUID adminId) {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity existAdmin = checkEntityExistOrNot(adminId);

            if (existAdmin == null) {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(adminId)));
            } else {
                AdminEntity createdBy = null;
                if (existAdmin.getCreatedBy() != null)
                    createdBy = checkEntityExistOrNot(existAdmin.getCreatedBy());

                AdminEntity updatedBy = null;
                if (existAdmin.getUpdatedBy() != null)
                    updatedBy = checkEntityExistOrNot(existAdmin.getUpdatedBy());

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
            path = END_POINT_ADD_ADMIN,
            method = RequestMethod.POST,
            consumes = {"multipart/form-data"})
    private String add(@Validated @RequestParam("adminEntity") String jsonAdminEntity,
                       @Validated @RequestParam("adminPhotoProfile") MultipartFile photoProfile) {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity adminEntity = new Gson().fromJson(jsonAdminEntity, AdminEntity.class);
            AdminEntity existAdmin = adminRepository.findByUsername(adminEntity.getAdminUsername());
            AdminEntity createdBy = checkEntityExistOrNot(adminEntity.getCreatedBy());

            if (existAdmin != null) {
                message.setSuccess(false);
                message.setMessage("Gagal ["
                        + environment.getProperty(OPERATION_NAME_CREATE)
                        +"] "
                        + environment.getProperty(ENTITY_ADMIN)
                        + ". "
                        + environment.getProperty(ENTITY_ADMIN)
                        + " ini telah dibuat pada ".
                        concat(existAdmin.getCreatedDate().toString()));
            } else {
                if (createdBy == null) {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(adminEntity.getCreatedBy()),
                            environment.getProperty(OPERATION_NAME_CREATE),
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
                            if (imageStorageResponse.getSuccess()) {
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
                                                environment.getProperty(OPERATION_NAME_CREATE)));
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

    @PostMapping(END_POINT_ADD_DEFAULT_ADMIN)
    private String addDefaultAdmin() {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity existAdmin = checkEntityExistOrNot(
                    environment.getProperty(DEFAULT_ADMIN_FIRST_ROOT_USERNAME));

            if (existAdmin != null) {
                message.setSuccess(false);
                message.setMessage("Gagal ["
                        + environment.getProperty(OPERATION_NAME_CREATE)
                        + "] "
                        + environment.getProperty(DEFAULT_ADMIN_FIRST_ROOT_USERNAME)
                        + SPACE_STRING
                        + environment.getProperty(ENTITY_ADMIN)
                        + " ini telah ditambahkan pada ".
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
                message.setMessage("Berhasil ["
                        + environment.getProperty(OPERATION_NAME_CREATE)
                        + "] "
                        + environment.getProperty(DEFAULT_ADMIN_FIRST_ROOT_USERNAME));
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @PostMapping(END_POINT_UPDATE_ADMIN)
    private String update(@Validated @RequestBody AdminEntity adminEntity) {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity existAdmin = checkEntityExistOrNot(adminEntity.getAdminId());
            AdminEntity updatedBy  = checkEntityExistOrNot(adminEntity.getUpdatedBy());

            if (existAdmin == null) {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(adminEntity.getAdminId())));
            } else {
                if (updatedBy == null) {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(adminEntity.getUpdatedBy()),
                            environment.getProperty(OPERATION_NAME_UPDATE),
                            environment.getProperty(ENTITY_ADMIN)));
                } else {
                    AdminEntity createdBy = null;
                    if (existAdmin.getCreatedBy() != null)
                        createdBy = checkEntityExistOrNot(existAdmin.getCreatedBy());

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

    @RequestMapping(
            path = END_POINT_UPDATE_PHOTO_PROFILE_ADMIN,
            method = RequestMethod.POST,
            consumes = {"multipart/form-data"})
    private String updatePhotoProfile(@Validated @RequestParam("adminEntity") String jsonAdminEntity,
                                      @Validated @RequestParam("adminPhotoProfile") MultipartFile photoProfile) {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity adminEntity = new Gson().fromJson(jsonAdminEntity, AdminEntity.class);
            AdminEntity existAdmin = checkEntityExistOrNot(adminEntity.getAdminId());
            AdminEntity updatedBy  = checkEntityExistOrNot(adminEntity.getUpdatedBy());

            if (existAdmin == null) {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(adminEntity.getAdminId())));
            } else {
                if (updatedBy == null) {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(adminEntity.getUpdatedBy()),
                            environment.getProperty(OPERATION_NAME_UPDATE)
                                    + SPACE_STRING
                                    + environment.getProperty(PHOTO_PROFILE_NAME),
                            environment.getProperty(ENTITY_ADMIN)));
                } else {
                    imageService.setResponseImageService(new ResponseImageService() {
                        @Override
                        public void onResponse(AdminEntity admin, ImageStorageResponse imageStorageResponse) {
                            super.onResponse(admin, imageStorageResponse);
                            if (imageStorageResponse.getSuccess()) {
                                admin.setUpdatedBy(updatedBy.getAdminId());
                                admin.setUpdatedDate(LocalDateTime.now());

                                AdminEntity result = adminRepository.save(existAdmin);

                                response.setAdmin(
                                        new AdminModel().
                                                convertFromEntityToModel(
                                                        result,
                                                        checkEntityExistOrNot(result.getCreatedBy()),
                                                        updatedBy));

                                message.setSuccess(true);
                                message.setMessage(successProcess(
                                        environment.getProperty(ENTITY_ADMIN),
                                        environment.getProperty(ENTITY_ADMIN_ID),
                                        String.valueOf(adminEntity.getAdminId()),
                                        environment.getProperty(ENTITY_ADMIN_USERNAME),
                                        existAdmin.getAdminUsername(),
                                        environment.getProperty(OPERATION_NAME_UPDATE)
                                                + SPACE_STRING
                                                + environment.getProperty(PHOTO_PROFILE_NAME)));
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

    @PostMapping(END_POINT_UPDATE_PASSWORD_ADMIN)
    private String updatePassword(@Validated @RequestParam("adminId") UUID adminId,
                                  @Validated @RequestParam("adminOldPassword") String adminOldPassword,
                                  @Validated @RequestParam("adminNewPassword") String adminNewPassword) {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity existAdmin = checkEntityExistOrNot(adminId);

            if (existAdmin == null) {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(adminId)));
            } else {
                if (comparePassword(adminOldPassword, existAdmin.getAdminPassword())) {
                    existAdmin.setAdminPassword(hashPassword(adminNewPassword));
                    existAdmin.setUpdatedBy(adminId);
                    existAdmin.setUpdatedDate(LocalDateTime.now());

                    AdminEntity result = adminRepository.save(existAdmin);

                    AdminEntity createdBy = null;
                    if (result.getCreatedBy() != null)
                        createdBy = checkEntityExistOrNot(result.getCreatedBy());

                    AdminEntity updatedBy = null;
                    if (result.getUpdatedBy() != null)
                        updatedBy = checkEntityExistOrNot(result.getUpdatedBy());

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
                            environment.getProperty(OPERATION_NAME_UPDATE)
                                    + SPACE_STRING
                                    + environment.getProperty(PASSWORD_NAME)));
                } else {
                    message.setSuccess(false);
                    message.setMessage("Gagal ["
                            + environment.getProperty(OPERATION_NAME_UPDATE)
                            + SPACE_STRING
                            + environment.getProperty(PASSWORD_NAME)
                            + "], "
                            + environment.getProperty(PASSWORD_NAME)
                            + " Sebelumnya tidak sesuai");
                }
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @PostMapping(END_POINT_RESET_PASSWORD_ADMIN)
    private String updatePassword(@Validated @RequestBody AdminEntity adminEntity) {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity existAdmin = checkEntityExistOrNot(adminEntity.getAdminId());
            AdminEntity updatedBy  = checkEntityExistOrNot(adminEntity.getUpdatedBy());

            if (existAdmin == null) {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(adminEntity.getAdminId())));
            } else {
                if (updatedBy == null) {
                    message.setSuccess(false);
                    message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(adminEntity.getUpdatedBy()),
                            environment.getProperty(OPERATION_NAME_RESET)
                                    + SPACE_STRING
                                    + environment.getProperty(PASSWORD_NAME),
                            environment.getProperty(ENTITY_ADMIN)));
                } else {
                    AdminEntity createdBy = null;
                    if (existAdmin.getCreatedBy() != null)
                        createdBy = checkEntityExistOrNot(existAdmin.getCreatedBy());

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
                            environment.getProperty(OPERATION_NAME_RESET)
                                    + SPACE_STRING
                                    + environment.getProperty(PASSWORD_NAME)));
                }
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @PostMapping(END_POINT_ACTIVATED_ADMIN)
    private String updateActivated(@Validated @RequestBody AdminEntity adminEntity) {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity existAdmin = checkEntityExistOrNot(adminEntity.getAdminId());
            AdminEntity updatedBy  = checkEntityExistOrNot(adminEntity.getUpdatedBy());

            if (existAdmin == null) {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(adminEntity.getAdminId())));
            } else {
                if (updatedBy == null) {
                    message.setSuccess(false);
                    if (adminEntity.isActive())
                        message.setMessage(existEntityNotFound(
                                environment.getProperty(ENTITY_ADMIN),
                                environment.getProperty(ENTITY_ADMIN_ID),
                                String.valueOf(adminEntity.getUpdatedBy()),
                                environment.getProperty(ACTIVATED_NAME),
                                environment.getProperty(ENTITY_ADMIN)));
                    else message.setMessage(existEntityNotFound(
                            environment.getProperty(ENTITY_ADMIN),
                            environment.getProperty(ENTITY_ADMIN_ID),
                            String.valueOf(adminEntity.getUpdatedBy()),
                            environment.getProperty(DEACTIVATED_NAME),
                            environment.getProperty(ENTITY_ADMIN)));
                } else {
                    AdminEntity createdBy = null;
                    if (existAdmin.getCreatedBy() != null)
                        createdBy = checkEntityExistOrNot(existAdmin.getCreatedBy());

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
                                        environment.getProperty(ACTIVATED_NAME)));
                    else message.setMessage(
                            successProcess(
                                    environment.getProperty(ENTITY_ADMIN),
                                    environment.getProperty(ENTITY_ADMIN_ID),
                                    String.valueOf(adminEntity.getAdminId()),
                                    environment.getProperty(ENTITY_ADMIN_USERNAME),
                                    existAdmin.getAdminUsername(),
                                    environment.getProperty(DEACTIVATED_NAME)));
                }
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_CRU);
    }

    @PostMapping(END_POINT_DELETE_ADMIN)
    private String delete(@Validated @RequestBody AdminEntity adminEntity) {
        AdminResponse response = new AdminResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try {
            AdminEntity existAdmin = checkEntityExistOrNot(adminEntity.getAdminId());

            if (existAdmin == null) {
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
                        if (response.getSuccess()) {
                            adminRepository.delete(existAdmin);

                            message.setSuccess(true);
                            message.setMessage(
                                    successProcess(
                                            environment.getProperty(ENTITY_ADMIN),
                                            environment.getProperty(ENTITY_ADMIN_ID),
                                            String.valueOf(adminEntity.getAdminId()),
                                            environment.getProperty(ENTITY_ADMIN_USERNAME),
                                            existAdmin.getAdminUsername(),
                                            environment.getProperty(OPERATION_NAME_DELETE)));
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

    @Override
    public AdminEntity checkEntityExistOrNot(UUID adminId) {
        return adminRepository.findById(adminId).orElse(null);
    }

    @Override
    public AdminEntity checkEntityExistOrNot(String adminUsername) {
        return adminRepository.findByUsername(adminUsername);
    }
}
