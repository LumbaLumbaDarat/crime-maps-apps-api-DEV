package com.harifrizki.crimemapsappsapi.network;

import com.harifrizki.crimemapsappsapi.network.response.ImageStorageResponse;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

import java.util.Map;

public interface EndPoint {

    @GET("services/api_handshake.php?api=handshake")
    Call<ImageStorageResponse> handshakeImageStorageApi();

    @Multipart
    @POST("services/api_image_admin.php?api=upload_image_admin")
    Call<ImageStorageResponse> uploadAdminPhotoProfile(@PartMap() Map<String, RequestBody> partMap);
}
