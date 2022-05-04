package com.harifrizki.crimemapsappsapi.utils;

public class ControllerConstants {
    public static final String GENERAL_CONTROLLER_URL                = "api/v2/";
    public static final String GENERAL_CONTROLLER_ADD_URL            = "/add/";
    public static final String GENERAL_CONTROLLER_UPDATE_URL         = "/update/";
    public static final String GENERAL_CONTROLLER_DELETE_URL         = "/delete/";
    public static final String GENERAL_CONTROLLER_SEARCH_BY_NAME_URL = "/search-by-name";
    public static final String GENERAL_CONTROLLER_SEARCH_BY_ID_URL   = "/search-by-id";

    public static final String LOGIN_CONTROLLER       = "login";
    public static final String LOGOUT_CONTROLLER      = "logout";

    public static final String HANDSHAKE_CONTROLLER   = "handshake";
    public static final String UTILIZATION_CONTROLLER = "utilization";

    public static final String ADMIN_GET_ALL_CONTROLLER                      = "admin";
    public static final String ADMIN_GET_ALL_SEARCH_NAME_CONTROLLER          =
            ADMIN_GET_ALL_CONTROLLER + "/search-by-name";
    public static final String ADMIN_SEARCH_ID_CONTROLLER                    =
            ADMIN_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_SEARCH_BY_ID_URL;
    public static final String ADMIN_ADD_CONTROLLER                          =
            ADMIN_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_ADD_URL;
    public static final String ADMIN_ADD_DEFAULT_ROOT_ADMIN_CONTROLLER       =
            ADMIN_GET_ALL_CONTROLLER + "/inject-default-first-admin-root";
    public static final String ADMIN_UPDATE_CONTROLLER                       =
            ADMIN_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_UPDATE_URL;
    public static final String ADMIN_UPDATE_IMAGE_PROFILE_CONTROLLER         =
            ADMIN_GET_ALL_CONTROLLER + "/change-image-profile";
    public static final String ADMIN_UPDATE_PASSWORD_CONTROLLER              =
            ADMIN_GET_ALL_CONTROLLER + "/update-password";
    public static final String ADMIN_RESET_PASSWORD_CONTROLLER               =
            ADMIN_GET_ALL_CONTROLLER + "/reset-password";
    public static final String ADMIN_UPDATE_ACTIVE_CONTROLLER                =
            ADMIN_GET_ALL_CONTROLLER + "/activated";
    public static final String ADMIN_DELETE_CONTROLLER                       =
            ADMIN_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_DELETE_URL;

    public static final String PROVINCE_GET_ALL_CONTROLLER             = "province";
    public static final String PROVINCE_GET_ALL_SEARCH_NAME_CONTROLLER =
            PROVINCE_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_SEARCH_BY_NAME_URL;
    public static final String PROVINCE_GET_SEARCH_ID_CONTROLLER       =
            PROVINCE_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_SEARCH_BY_ID_URL;
    public static final String PROVINCE_ADD_CONTROLLER                 =
            PROVINCE_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_ADD_URL;
    public static final String PROVINCE_UPDATE_CONTROLLER              =
            PROVINCE_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_UPDATE_URL;
    public static final String PROVINCE_DELETE_CONTROLLER              =
            PROVINCE_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_DELETE_URL;

    public static final String CITY_GET_ALL_CONTROLLER                    = "city";
    public static final String CITY_GET_ALL_SEARCH_NAME_CONTROLLER        =
            CITY_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_SEARCH_BY_NAME_URL;
    public static final String CITY_GET_ALL_SEARCH_PROVINCE_ID_CONTROLLER =
            CITY_GET_ALL_CONTROLLER + "/search-by-province-id";
    public static final String CITY_GET_SEARCH_ID_CONTROLLER              =
            CITY_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_SEARCH_BY_ID_URL;
    public static final String CITY_ADD_CONTROLLER                        =
            CITY_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_ADD_URL;
    public static final String CITY_UPDATE_CONTROLLER                     =
            CITY_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_UPDATE_URL;
    public static final String CITY_DELETE_CONTROLLER                     =
            CITY_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_DELETE_URL;

    public static final String SUB_DISTRICT_GET_ALL_CONTROLLER                = "sub-district";
    public static final String SUB_DISTRICT_GET_ALL_SEARCH_NAME_CONTROLLER    =
            SUB_DISTRICT_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_SEARCH_BY_NAME_URL;
    public static final String SUB_DISTRICT_GET_ALL_SEARCH_CITY_ID_CONTROLLER =
            SUB_DISTRICT_GET_ALL_CONTROLLER + "/search-by-city-id";
    public static final String SUB_DISTRICT_GET_SEARCH_ID_CONTROLLER          =
            SUB_DISTRICT_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_SEARCH_BY_ID_URL;
    public static final String SUB_DISTRICT_ADD_CONTROLLER                    =
            SUB_DISTRICT_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_ADD_URL;
    public static final String SUB_DISTRICT_UPDATE_CONTROLLER                 =
            SUB_DISTRICT_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_UPDATE_URL;
    public static final String SUB_DISTRICT_DELETE_CONTROLLER                 =
            SUB_DISTRICT_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_DELETE_URL;

    public static final String URBAN_VILLAGE_GET_ALL_CONTROLLER                        = "urban-village";
    public static final String URBAN_VILLAGE_GET_ALL_SEARCH_NAME_CONTROLLER            =
            URBAN_VILLAGE_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_SEARCH_BY_NAME_URL;
    public static final String URBAN_VILLAGE_GET_ALL_SEARCH_SUB_DISTRICT_ID_CONTROLLER =
            URBAN_VILLAGE_GET_ALL_CONTROLLER + "/search-by-sub-district-id";
    public static final String URBAN_VILLAGE_GET_SEARCH_ID_CONTROLLER                  =
            URBAN_VILLAGE_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_SEARCH_BY_ID_URL;
    public static final String URBAN_VILLAGE_ADD_CONTROLLER                            =
            URBAN_VILLAGE_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_ADD_URL;
    public static final String URBAN_VILLAGE_UPDATE_CONTROLLER                         =
            URBAN_VILLAGE_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_UPDATE_URL;
    public static final String URBAN_VILLAGE_DELETE_CONTROLLER                         =
            URBAN_VILLAGE_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_DELETE_URL;

    public static final String CRIME_LOCATION_GET_ALL_CONTROLLER              = "crime-location";
    public static final String CRIME_LOCATION_ADD_CONTROLLER                  =
            CRIME_LOCATION_GET_ALL_CONTROLLER + GENERAL_CONTROLLER_ADD_URL;
}
