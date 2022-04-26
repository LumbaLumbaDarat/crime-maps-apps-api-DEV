package com.harifrizki.crimemapsappsapi.network;

public class NetworkConstants {

    public static String API_CONNECTION_URL_IMAGE = "http://localhost:80/crime-maps-apps-image-api/";

    public static String API_CONNECTION_URL_CRIME_LOCATION_IMAGE_STORAGE = "upload/image_maps/";
    public static String API_CONNECTION_URL_ADMIN_IMAGE_STORAGE          = "upload/image_admin/";

    public static int ERROR_FROM_SERVER_500 = 500;

    public static int ERROR_FROM_SERVER_419 = 419;

    public static int ERROR_FROM_SERVER_405 = 405;
    public static String MSG_ERROR_FROM_SERVER_405 = "Method Not Allowed";

    public static int ERROR_FROM_SERVER_404 = 404;

    //Constant for Timeout Time
    public static int CONNECT_TIME_OUT = 1;
    public static int READ_TIME_OUT    = 30;
    public static int WRITE_TIME_OUT   = 15;
}
