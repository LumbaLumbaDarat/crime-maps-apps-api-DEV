package com.harifrizki.crimemapsappsapi.utils;

public class ControllerConstants {
    public static final String API_VERSION       = "v2/";
    public static final String GENERAL_END_POINT = "api/" + API_VERSION;

    public static final String ID      = "id/";
    public static final String ADD     = "add/";
    public static final String UPDATE  = "update/";
    public static final String DELETE  = "delete/";

    public static final String LOGIN  = "login/";
    public static final String LOGOUT = "logout/";

    public static final String HANDSHAKE   = "handshake/";
    public static final String UTILIZATION = "utilization/";

    public static final String PASSWORD = "password/";

    public static final String PARAM_SEARCH_BY        = "searchBy";
    public static final String PARAM_PAGE_NO          = "pageNo";
    public static final String PARAM_NAME             = "name";
    public static final String PARAM_PROVINCE_ID      = "provinceId";
    public static final String PARAM_CITY_ID          = "cityId";
    public static final String PARAM_SUB_DISTRICT_ID  = "subDistrictId";
    public static final String PARAM_URBAN_VILLAGE_ID = "urbanVillageId";
    public static final String PARAM_NEAREST_LOCATION = "nearestLocation";

    public static final String END_POINT_ADMIN                      = "admin/";
    public static final String END_POINT_DETAIL_ADMIN               = END_POINT_ADMIN + ID;
    public static final String END_POINT_ADD_ADMIN                  = END_POINT_ADMIN + ADD;
    public static final String END_POINT_ADD_DEFAULT_ADMIN          =
            END_POINT_ADMIN + "inject-default-first-admin-root/";
    public static final String END_POINT_UPDATE_ADMIN               = END_POINT_ADMIN + UPDATE;
    public static final String END_POINT_UPDATE_PHOTO_PROFILE_ADMIN =
            END_POINT_ADMIN + "change-photo-profile/";
    public static final String END_POINT_UPDATE_PASSWORD_ADMIN      =
            END_POINT_ADMIN + UPDATE + PASSWORD;
    public static final String END_POINT_RESET_PASSWORD_ADMIN       =
            END_POINT_ADMIN + "reset/" + PASSWORD;
    public static final String END_POINT_ACTIVATED_ADMIN            =
            END_POINT_ADMIN + "activated/";
    public static final String END_POINT_DELETE_ADMIN               = END_POINT_ADMIN + DELETE;

    public static final String END_POINT_PROVINCE        = "province/";
    public static final String END_POINT_DETAIL_PROVINCE = END_POINT_PROVINCE + ID;
    public static final String END_POINT_ADD_PROVINCE    = END_POINT_PROVINCE + ADD;
    public static final String END_POINT_UPDATE_PROVINCE = END_POINT_PROVINCE + UPDATE;
    public static final String END_POINT_DELETE_PROVINCE = END_POINT_PROVINCE + DELETE;

    public static final String END_POINT_CITY        = "city/";
    public static final String END_POINT_DETAIL_CITY = END_POINT_CITY + ID;
    public static final String END_POINT_ADD_CITY    = END_POINT_CITY + ADD;
    public static final String END_POINT_UPDATE_CITY = END_POINT_CITY + UPDATE;
    public static final String END_POINT_DELETE_CITY = END_POINT_CITY + DELETE;

    public static final String END_POINT_SUB_DISTRICT        = "sub-district/";
    public static final String END_POINT_DETAIL_SUB_DISTRICT = END_POINT_SUB_DISTRICT + ID;
    public static final String END_POINT_ADD_SUB_DISTRICT    = END_POINT_SUB_DISTRICT + ADD;
    public static final String END_POINT_UPDATE_SUB_DISTRICT = END_POINT_SUB_DISTRICT + UPDATE;
    public static final String END_POINT_DELETE_SUB_DISTRICT = END_POINT_SUB_DISTRICT + DELETE;

    public static final String END_POINT_URBAN_VILLAGE        = "urban-village/";
    public static final String END_POINT_DETAIL_URBAN_VILLAGE = END_POINT_URBAN_VILLAGE + ID;
    public static final String END_POINT_ADD_URBAN_VILLAGE    = END_POINT_URBAN_VILLAGE + ADD;
    public static final String END_POINT_UPDATE_URBAN_VILLAGE = END_POINT_URBAN_VILLAGE + UPDATE;
    public static final String END_POINT_DELETE_URBAN_VILLAGE = END_POINT_URBAN_VILLAGE + DELETE;

    public static final String END_POINT_CRIME_LOCATION              = "crime-location/";
    public static final String END_POINT_DETAIL_CRIME_LOCATION       = END_POINT_CRIME_LOCATION + ID;
    public static final String END_POINT_ADD_CRIME_LOCATION          = END_POINT_CRIME_LOCATION + ADD;
    public static final String END_POINT_ADD_IMAGE_CRIME_LOCATION    =
            END_POINT_CRIME_LOCATION + "add-image/";
    public static final String END_POINT_UPDATE_CRIME_LOCATION       = END_POINT_CRIME_LOCATION + UPDATE;
    public static final String END_POINT_DELETE_CRIME_LOCATION       = END_POINT_CRIME_LOCATION + DELETE;
    public static final String END_POINT_DELETE_IMAGE_CRIME_LOCATION =
            END_POINT_CRIME_LOCATION + "delete-image/";
}
