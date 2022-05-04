package com.harifrizki.crimemapsappsapi.network;

import com.harifrizki.crimemapsappsapi.network.response.ImageStorageResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface EndPoint {

    @GET("services/api_handshake.php?api=handshake")
    Call<ImageStorageResponse> handshakeImageStorageApi();

    @Multipart
    @POST("services/api_image_admin.php?api=upload_image_admin")
    Call<ImageStorageResponse> uploadAdminPhotoProfile(@PartMap() Map<String, RequestBody> partMap);

    @Multipart
    @POST("services/api_image_admin.php?api=update_image_admin")
    Call<ImageStorageResponse> updateAdminPhotoProfile(@PartMap() Map<String, RequestBody> partMap);

    @FormUrlEncoded
    @POST("services/api_image_admin.php?api=delete_image_admin")
    Call<ImageStorageResponse> deleteAdminPhotoProfile(@Field("image_admin") String imageAdmin);

    @Multipart
    @POST("services/api_image_crime_location.php?api=upload_image_crime_location")
    Call<ImageStorageResponse> uploadCrimeLocationPhoto(@PartMap() Map<String, RequestBody> partMap,
                                                        @Part List<MultipartBody.Part> files);
}
