package com.harifrizki.crimemapsappsapi.controller;

import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import com.harifrizki.crimemapsappsapi.model.AdminModel;
import com.harifrizki.crimemapsappsapi.model.HandshakeModel;
import com.harifrizki.crimemapsappsapi.model.UtilizationModel;
import com.harifrizki.crimemapsappsapi.model.response.GeneralMessageResponse;
import com.harifrizki.crimemapsappsapi.model.response.UtilizationResponse;
import com.harifrizki.crimemapsappsapi.network.response.ImageStorageResponse;
import com.harifrizki.crimemapsappsapi.repository.*;
import com.harifrizki.crimemapsappsapi.service.ControllerService;
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
@RequestMapping(GENERAL_END_POINT)
@Validated
public class UtilizationController extends ControllerService {

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

    @GetMapping(HANDSHAKE)
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
            handshake.setDistanceUnit(DEFAULT_DISTANCE_UNIT);
            handshake.setMaxDistance(DEFAULT_MAX_NEAREST_DISTANCE);
            handshake.setMaxUploadImageCrimeLocation(MAX_UPLOAD_IMAGE_CRIME_LOCATION);

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
                                "kedalam API",
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

    @PostMapping(LOGIN)
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
                                createdBy = checkEntityExistOrNot(existAdmin.getCreatedBy());

                            AdminEntity updatedBy = null;
                            if (existAdmin.getUpdatedBy() != null)
                                updatedBy = checkEntityExistOrNot(existAdmin.getUpdatedBy());

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
                                            environment.getProperty(LOGIN_NAME)));
                        } else {
                            message.setSuccess(false);
                            message.setMessage("Gagal "
                                    + environment.getProperty(LOGIN_NAME)
                                    + ", "
                                    + environment.getProperty(ENTITY_ADMIN)
                                    + " ini telah ["
                                    + environment.getProperty(LOGIN_NAME)
                                    + " pada Perangkat Lain]");
                        }
                    } else {
                        message.setSuccess(false);
                        message.setMessage("Gagal "
                                + environment.getProperty(LOGIN_NAME)
                                + ", "
                                + environment.getProperty(ENTITY_ADMIN)
                                + " ini ["
                                + environment.getProperty(NOT_ACTIVE_NAME)
                                + "]");
                    }
                } else {
                    message.setSuccess(false);
                    message.setMessage("Gagal "
                            + environment.getProperty(LOGIN_NAME)
                            + ", Terjadi Kesalahan pada ["
                            + environment.getProperty(ENTITY_ADMIN_USERNAME)
                            + "] dan ["
                            + environment.getProperty(PASSWORD_NAME)
                            + "]");
                }
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_LOGIN);
    }

    @PostMapping(LOGOUT)
    private String logout(@Validated @RequestBody AdminEntity adminEntity) {
        UtilizationResponse response = new UtilizationResponse();
        GeneralMessageResponse message = new GeneralMessageResponse();

        try
        {
            AdminEntity existAdmin = checkEntityExistOrNot(adminEntity.getAdminId());

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
                                environment.getProperty(LOGOUT_NAME)));
            }
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_LOGOUT);
    }

    @GetMapping(UTILIZATION)
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
                    environment.getProperty(OPERATION_NAME_READ_ALL)
                            + " Jumlah"));
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }

        response.setMessage(message);
        return response.toJson(response, OPERATION_UTILIZATION);
    }

    @Override
    public AdminEntity checkEntityExistOrNot(UUID adminId) {
        return adminRepository.findById(adminId).orElse(null);
    }
}
