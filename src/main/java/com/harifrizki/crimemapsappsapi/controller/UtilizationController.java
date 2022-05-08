package com.harifrizki.crimemapsappsapi.controller;

import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import com.harifrizki.crimemapsappsapi.model.AdminModel;
import com.harifrizki.crimemapsappsapi.model.HandshakeModel;
import com.harifrizki.crimemapsappsapi.model.UtilizationModel;
import com.harifrizki.crimemapsappsapi.model.response.GeneralMessageResponse;
import com.harifrizki.crimemapsappsapi.model.response.UtilizationResponse;
import com.harifrizki.crimemapsappsapi.network.response.ImageStorageResponse;
import com.harifrizki.crimemapsappsapi.repository.*;
import com.harifrizki.crimemapsappsapi.service.ResponseImageService;
import com.harifrizki.crimemapsappsapi.service.impl.ImageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.UUID;

import static com.harifrizki.crimemapsappsapi.network.NetworkConstants.*;
import static com.harifrizki.crimemapsappsapi.utils.AppsConstants.*;
import static com.harifrizki.crimemapsappsapi.utils.ControllerConstants.*;
import static com.harifrizki.crimemapsappsapi.utils.UtilizationClass.*;

@RestController
@RequestMapping(GENERAL_CONTROLLER_URL)
@Validated
public class UtilizationController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private SubDistrictRepository subDistrictRepository;

    @Autowired
    private UrbanVillageRepository urbanVillageRepository;

    @Autowired
    private CrimeLocationRepository crimeLocationRepository;

    @Autowired
    private ImageServiceImpl uploadImageService;

    @Autowired
    private Environment environment;

    @GetMapping(HANDSHAKE_CONTROLLER)
    @ResponseBody
    private String handshakeApi() {
        UtilizationResponse response = new UtilizationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try
        {
            HandshakeModel handshake = new HandshakeModel();

            handshake.setUrlApi(environment.getProperty(SERVER_DEVELOPMENT_URL));
            handshake.setRoleAdminRoot(environment.getProperty(ADMIN_ROLE_ROOT));
            handshake.setRoleAdmin(environment.getProperty(ADMIN_ROLE_ADMIN));
            handshake.setDefaultImageAdmin(environment.getProperty(DEFAULT_IMAGE_ADMIN));
            handshake.setFirstRootAdmin(environment.getProperty(DEFAULT_ADMIN_FIRST_ROOT_USERNAME));

            uploadImageService.setResponseImageService(new ResponseImageService() {
                @Override
                public void onResponse(ImageStorageResponse response) {
                    super.onResponse(response);
                    if (response.getSuccess())
                    {
                        ArrayList<String> urlImageStorageApi = new ArrayList<>();
                        urlImageStorageApi.add(API_CONNECTION_URL_IMAGE.
                                concat(API_CONNECTION_URL_CRIME_LOCATION_IMAGE_STORAGE));
                        urlImageStorageApi.add(API_CONNECTION_URL_IMAGE.
                                concat(API_CONNECTION_URL_ADMIN_IMAGE_STORAGE));

                        handshake.setUrlImageStorageApi(urlImageStorageApi);

                        message.setSuccess(true);
                        message.setMessage(successProcess(
                                "to API",
                                "Handshake"));
                    } else {
                        message.setSuccess(false);
                        message.setMessage(response.getMessage());
                    }
                }
            });
            uploadImageService.handshake();

            response.setHandshake(handshake);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_HANDSHAKE);
    }

    @PostMapping(LOGIN_CONTROLLER)
    private String login(@Validated @RequestBody AdminEntity adminEntity) {
        UtilizationResponse response = new UtilizationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try
        {
            AdminEntity existAdmin = adminRepository.findByUsername(adminEntity.getAdminUsername());

            if (existAdmin == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_USERNAME),
                        String.valueOf(adminEntity.getAdminUsername())));
            } else {
                if (comparePassword(adminEntity.getAdminPassword(),
                        existAdmin.getAdminPassword()))
                {
                    if (existAdmin.isActive())
                    {
                        if (!existAdmin.isLogin())
                        {
                            AdminEntity createdBy = null;
                            if (existAdmin.getCreatedBy() != null)
                                createdBy = checkAdminWasExistOrNot(existAdmin.getCreatedBy());

                            AdminEntity updatedBy = null;
                            if (existAdmin.getUpdatedBy() != null)
                                updatedBy = checkAdminWasExistOrNot(existAdmin.getUpdatedBy());

                            existAdmin.setLogin(true);

                            AdminEntity result = adminRepository.save(existAdmin);

                            response.setLogin(new AdminModel().
                                    convertFromEntityToModel(
                                            result,
                                            createdBy,
                                            updatedBy));

                            message.setSuccess(true);
                            message.setMessage(
                                    successProcess(
                                            environment.getProperty(ENTITY_ADMIN),
                                            environment.getProperty(ENTITY_ADMIN_ID),
                                            String.valueOf(existAdmin.getAdminId()),
                                            environment.getProperty(ENTITY_ADMIN_USERNAME),
                                            existAdmin.getAdminUsername(),
                                            "Login"));
                        } else {
                            message.setSuccess(false);
                            message.setMessage("Failed to Login, This Admin was [Login in other Device]");
                        }
                    } else {
                        message.setSuccess(false);
                        message.setMessage("Failed to Login, This Admin was [Not Active]");
                    }
                } else {
                    message.setSuccess(false);
                    message.setMessage("Failed to Login, Something wrong with [Admin Username] or [Password]");
                }
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_LOGIN);
    }

    @PostMapping(LOGOUT_CONTROLLER)
    private String logout(@Validated @RequestBody AdminEntity adminEntity) {
        UtilizationResponse response = new UtilizationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try
        {
            AdminEntity existAdmin = checkAdminWasExistOrNot(adminEntity.getAdminId());

            if (existAdmin == null)
            {
                message.setSuccess(false);
                message.setMessage(existEntityNotFound(
                        environment.getProperty(ENTITY_ADMIN),
                        environment.getProperty(ENTITY_ADMIN_ID),
                        String.valueOf(adminEntity.getAdminId())));
            } else {
                existAdmin.setLogin(false);

                adminRepository.save(existAdmin);

                message.setSuccess(true);
                message.setMessage(
                        successProcess(
                                environment.getProperty(ENTITY_ADMIN),
                                environment.getProperty(ENTITY_ADMIN_ID),
                                String.valueOf(adminEntity.getAdminId()),
                                environment.getProperty(ENTITY_ADMIN_USERNAME),
                                existAdmin.getAdminUsername(),
                                "Logout"));
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_LOGOUT);
    }

    @GetMapping(UTILIZATION_CONTROLLER)
    @ResponseBody
    private String utilization() {
        UtilizationResponse response = new UtilizationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try
        {
            UtilizationModel utilization = new UtilizationModel();

            utilization.setCountAdmin(adminRepository.count());
            utilization.setCountAdminToday(adminRepository.countToday());
            utilization.setCountAdminMonth(adminRepository.countMonth());

            utilization.setCountProvince(provinceRepository.count());
            utilization.setCountProvinceToday(provinceRepository.countToday());
            utilization.setCountProvinceMonth(provinceRepository.countMonth());

            utilization.setCountCity(cityRepository.count());
            utilization.setCountCityToday(cityRepository.countToday());
            utilization.setCountCityMonth(cityRepository.countMonth());

            utilization.setCountSubDistrict(subDistrictRepository.count());
            utilization.setCountSubDistrictToday(subDistrictRepository.countToday());
            utilization.setCountSubDistrictMonth(subDistrictRepository.countMonth());

            utilization.setCountUrbanVillage(urbanVillageRepository.count());
            utilization.setCountUrbanVillageToday(urbanVillageRepository.countToday());
            utilization.setCountUrbanVillageMonth(urbanVillageRepository.countMonth());

            utilization.setCountCrimeLocation(crimeLocationRepository.count());
            utilization.setCountCrimeLocationToday(crimeLocationRepository.countToday());
            utilization.setCountCrimeLocationMonth(crimeLocationRepository.countMonth());

            response.setUtilization(utilization);

            message.setSuccess(true);
            message.setMessage(successProcess(
                    "Data",
                    "Get All Count"));
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_UTILIZATION);
    }

    private AdminEntity checkAdminWasExistOrNot(UUID adminId) {
        return adminRepository.findById(adminId).orElse(null);
    }
}
