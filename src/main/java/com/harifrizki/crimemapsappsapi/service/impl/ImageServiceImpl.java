package com.harifrizki.crimemapsappsapi.service.impl;

import com.google.gson.Gson;
import com.harifrizki.crimemapsappsapi.entity.*;
import com.harifrizki.crimemapsappsapi.network.EndPoint;
import com.harifrizki.crimemapsappsapi.network.NetworkApi;
import com.harifrizki.crimemapsappsapi.network.response.ImageStorageResponse;
import com.harifrizki.crimemapsappsapi.service.ResponseImageService;
import com.harifrizki.crimemapsappsapi.service.ImageService;
import lombok.Setter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import retrofit2.Call;
import retrofit2.Response;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
            EndPoint endPoint = NetworkApi.
                    getConnectionApi(API_CONNECTION_URL_IMAGE).create(EndPoint.class);
            Call<ImageStorageResponse> call = endPoint.
                    uploadAdminPhotoProfile(map(environment, adminEntity, photoProfile));

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
    public void upload(Environment environment,
                       CrimeLocationEntity crimeLocationEntity,
                       MultipartFile[] photoCrimeLocation) {
        ImageStorageResponse response;
        try
        {
            EndPoint endPoint = NetworkApi.
                    getConnectionApi(API_CONNECTION_URL_IMAGE).create(EndPoint.class);
            Call<ImageStorageResponse> call = endPoint.uploadCrimeLocationPhoto(
                    map(crimeLocationEntity),
                    toRequestBody(
                            files(environment, crimeLocationEntity, photoCrimeLocation),
                            "crimeLocationPhoto[]"));

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
    public void update(Environment environment,
                       AdminEntity adminEntity,
                       MultipartFile photoProfile) {
        ImageStorageResponse response;
        try
        {
            EndPoint endPoint = NetworkApi.
                    getConnectionApi(API_CONNECTION_URL_IMAGE).create(EndPoint.class);
            Call<ImageStorageResponse> call = endPoint.
                    updateAdminPhotoProfile(map(environment, adminEntity, photoProfile));

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
    public void delete(AdminEntity adminEntity) {
        ImageStorageResponse response;
        try
        {
            EndPoint endPoint = NetworkApi.
                    getConnectionApi(API_CONNECTION_URL_IMAGE).create(EndPoint.class);
            Call<ImageStorageResponse> call = endPoint.
                    deleteAdminPhotoProfile(adminEntity.getAdminImage());

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
    public void delete(CrimeLocationEntity crimeLocationEntity) {
        ImageStorageResponse response;
        try
        {
            EndPoint endPoint = NetworkApi.
                    getConnectionApi(API_CONNECTION_URL_IMAGE).create(EndPoint.class);
            Call<ImageStorageResponse> call = endPoint.
                    deleteCrimeLocationPhoto(
                            imageCrimeName(
                                    crimeLocationEntity.getImageCrimeLocations()));

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
    public void delete(Set<ImageCrimeLocationEntity> imageCrimeLocationEntities) {
        ImageStorageResponse response;
        try
        {
            EndPoint endPoint = NetworkApi.
                    getConnectionApi(API_CONNECTION_URL_IMAGE).create(EndPoint.class);
            Call<ImageStorageResponse> call = endPoint.
                    deleteCrimeLocationPhoto(
                            imageCrimeName(
                                    imageCrimeLocationEntities));

            Response<ImageStorageResponse> imageStorageResponse = call.execute();
            response = imageStorageResponse.body();
        } catch (Exception e) {
            response = new ImageStorageResponse();
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        responseImageService.onResponse(response);
    }

    private Map<String, RequestBody> map(CrimeLocationEntity crimeLocationEntity) {
        Map<String, RequestBody> map = new HashMap<>();
        map.put("photoName", toRequestBody(String.valueOf(crimeLocationEntity.getCrimeLocationId())));
        map.put("photoFormat", toRequestBody(FORMAT_IMAGE_UPLOAD_PNG));
        return map;
    }

    private Map<String, RequestBody> map(Environment environment,
                                         AdminEntity adminEntity,
                                         MultipartFile photoProfile) throws IOException {
        Map<String, RequestBody> map = new HashMap<>();
        map.put("photoProfile\"; filename=\"" +
                        adminEntity.getAdminId() +
                        ".png\"",
                toRequestBody(
                        convert(
                                environment,
                                photoProfile,
                                String.valueOf(adminEntity.getAdminId()))));
        return map;
    }

    private List<File> files(Environment environment,
                             CrimeLocationEntity crimeLocationEntity,
                             MultipartFile[] photoCrimeLocation) throws IOException {
        List<File> files = new ArrayList<>();
        for (int i = 0; i < photoCrimeLocation.length; i++)
        {
            files.add(
                    convert(
                            environment, photoCrimeLocation[i],
                            crimeLocationEntity.getCrimeLocationId() + "_" + i));
        }
        return files;
    }

    private List<String> imageCrimeName(Set<ImageCrimeLocationEntity> imageCrimeLocationEntities) {
        List<String> strings = new ArrayList<>();
        for (ImageCrimeLocationEntity imageCrimeLocationEntity : imageCrimeLocationEntities)
        {
            strings.add(imageCrimeLocationEntity.getImageName());
        }
        return strings;
    }
}
