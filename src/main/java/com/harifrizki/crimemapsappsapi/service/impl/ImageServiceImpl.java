package com.harifrizki.crimemapsappsapi.service.impl;

import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import com.harifrizki.crimemapsappsapi.entity.CrimeLocationEntity;
import com.harifrizki.crimemapsappsapi.network.EndPoint;
import com.harifrizki.crimemapsappsapi.network.NetworkApi;
import com.harifrizki.crimemapsappsapi.network.response.ImageStorageResponse;
import com.harifrizki.crimemapsappsapi.service.ResponseImageService;
import com.harifrizki.crimemapsappsapi.service.ImageService;
import lombok.Setter;
import okhttp3.RequestBody;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import retrofit2.Call;
import retrofit2.Response;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.harifrizki.crimemapsappsapi.network.NetworkConstants.API_CONNECTION_URL_IMAGE;
import static com.harifrizki.crimemapsappsapi.utils.AppsConstants.FORMAT_IMAGE_UPLOAD_PNG;
import static com.harifrizki.crimemapsappsapi.utils.UtilizationClass.convert;
import static com.harifrizki.crimemapsappsapi.utils.UtilizationClass.toRequestBody;

@Component
public class ImageServiceImpl implements ImageService {

    @Setter
    private ResponseImageService responseImageService;

    public ImageServiceImpl() {
    }

    @Override
    public void handshake() {
        ImageStorageResponse response;
        try
        {
            EndPoint endPoint = NetworkApi.
                    getConnectionApi(API_CONNECTION_URL_IMAGE).create(EndPoint.class);
            Call<ImageStorageResponse> call = endPoint.
                    handshakeImageStorageApi();

            Response<ImageStorageResponse> imageStorageResponse = call.execute();
            response = imageStorageResponse.body();
        } catch (Exception e) {
            response = new ImageStorageResponse();
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        responseImageService.onResponse(response);
    }

    @Override
    public void upload(Environment environment, AdminEntity adminEntity, MultipartFile photoProfile) {
        ImageStorageResponse response;
        try
        {
            Map<String, RequestBody> map = new HashMap<>();
            map.put("photoProfile\"; filename=\"" +
                    adminEntity.getAdminId() +
                    ".png\"",
                    toRequestBody(convert(environment, photoProfile, String.valueOf(adminEntity.getAdminId()))));

            EndPoint endPoint = NetworkApi.
                    getConnectionApi(API_CONNECTION_URL_IMAGE).create(EndPoint.class);
            Call<ImageStorageResponse> call = endPoint.
                    uploadAdminPhotoProfile(map);

            Response<ImageStorageResponse> imageStorageResponse = call.execute();
            response = imageStorageResponse.body();
        } catch (Exception e) {
            response = new ImageStorageResponse();
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        responseImageService.onResponse(adminEntity, response);
    }

    @Override
    public void upload(Environment environment, CrimeLocationEntity crimeLocationEntity, MultipartFile[] photoCrimeLocation) {
        ImageStorageResponse response;
        try
        {
            Map<String, RequestBody> map = new HashMap<>();
            map.put("photoName", toRequestBody(String.valueOf(crimeLocationEntity.getCrimeLocationId())));
            map.put("photoFormat", toRequestBody(FORMAT_IMAGE_UPLOAD_PNG));

            List<File> files = new ArrayList<>();
            for (int i = 0; i < photoCrimeLocation.length; i++)
            {
                files.add(
                        convert(
                                environment, photoCrimeLocation[i],
                                crimeLocationEntity.getCrimeLocationId() + "_" + i));
            }

            EndPoint endPoint = NetworkApi.
                    getConnectionApi(API_CONNECTION_URL_IMAGE).create(EndPoint.class);
            Call<ImageStorageResponse> call = endPoint.uploadCrimeLocationPhoto(
                    map,
                    toRequestBody(files, "crimeLocationPhoto[]"));

            Response<ImageStorageResponse> imageStorageResponse = call.execute();
            response = imageStorageResponse.body();
        } catch (Exception e) {
            response = new ImageStorageResponse();
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        responseImageService.onResponse(crimeLocationEntity, response);
    }

    @Override
    public void update(Environment environment, AdminEntity adminEntity, AdminEntity createdBy, MultipartFile photoProfile) {
        ImageStorageResponse response;
        try
        {
            Map<String, RequestBody> map = new HashMap<>();
            map.put("photoProfile\"; filename=\"" +
                            adminEntity.getAdminId() +
                            ".png\"",
                    toRequestBody(convert(environment, photoProfile, String.valueOf(adminEntity.getAdminId()))));

            EndPoint endPoint = NetworkApi.
                    getConnectionApi(API_CONNECTION_URL_IMAGE).create(EndPoint.class);
            Call<ImageStorageResponse> call = endPoint.
                    updateAdminPhotoProfile(map);

            Response<ImageStorageResponse> imageStorageResponse = call.execute();
            response = imageStorageResponse.body();
        } catch (Exception e) {
            response = new ImageStorageResponse();
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        responseImageService.onResponse(adminEntity, createdBy, response);
    }

    @Override
    public void delete(AdminEntity adminEntity) {
        ImageStorageResponse response;
        try
        {
            EndPoint endPoint = NetworkApi.
                    getConnectionApi(API_CONNECTION_URL_IMAGE).create(EndPoint.class);
            Call<ImageStorageResponse> call = endPoint.deleteAdminPhotoProfile(adminEntity.getAdminImage());

            Response<ImageStorageResponse> imageStorageResponse = call.execute();
            response = imageStorageResponse.body();
        } catch (Exception e) {
            response = new ImageStorageResponse();
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        responseImageService.onResponse(response);
    }
}
