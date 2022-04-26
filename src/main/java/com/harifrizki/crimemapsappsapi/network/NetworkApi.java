package com.harifrizki.crimemapsappsapi.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.harifrizki.crimemapsappsapi.network.NetworkConstants.*;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

public class NetworkApi {

    private static Retrofit retrofit;

    public static Retrofit getConnectionApi(String connectionApiUrl){
        if (retrofit == null)
        {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().
                    addInterceptor(interceptor).
                    connectTimeout(CONNECT_TIME_OUT, MINUTES).
                    readTimeout(READ_TIME_OUT,       SECONDS).
                    writeTimeout(WRITE_TIME_OUT,     SECONDS).
                    build();

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder().
                    baseUrl(connectionApiUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson)).
                            client(client).build();
        }

        return retrofit;
    }
}
