package com.harifrizki.crimemapsappsapi.service.impl;

import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import com.harifrizki.crimemapsappsapi.network.EndPoint;
import com.harifrizki.crimemapsappsapi.network.NetworkApi;
import com.harifrizki.crimemapsappsapi.network.response.ImageStorageResponse;
import com.harifrizki.crimemapsappsapi.service.UploadImageService;
import com.harifrizki.crimemapsappsapi.service.OnUpload;
import lombok.Setter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import retrofit2.Call;
import retrofit2.Response;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.harifrizki.crimemapsappsapi.network.NetworkConstants.API_CONNECTION_URL_IMAGE;
import static com.harifrizki.crimemapsappsapi.utils.AppsConstants.FORMAT_IMAGE_UPLOAD_PNG;
import static com.harifrizki.crimemapsappsapi.utils.UtilizationClass.convert;
import static com.harifrizki.crimemapsappsapi.utils.UtilizationClass.toRequestBody;

@Component
public class UploadImageServiceImpl implements UploadImageService {

    @Setter
    private OnUpload onUpload;

    public UploadImageServiceImpl() {
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
        onUpload.onResponse(response);
    }

    @Override
    public void uploadPhotoProfile(AdminEntity adminEntity, MultipartFile photoProfile) {
        ImageStorageResponse response;
        try
        {
            Map<String, RequestBody> map = new HashMap<>();
            map.put("photoProfile\"; filename=\"" +
                    adminEntity.getAdminId() +
                    ".png\"",
                    toRequestBody(convert(photoProfile)));

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
        onUpload.onResponse(adminEntity, response);
    }
}
