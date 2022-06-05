package com.harifrizki.crimemapsappsapi.utils;

public class AppsConstants {
    public static final String DATE_FORMAT_FOR_IMAGE_NAME = "yyyyMMddHHmmss";
    public static final String FORMAT_IMAGE_UPLOAD_PNG = ".png";
    public static final String DEFAULT_DISTANCE_UNIT = "Kilometer";
    public static final double DEFAULT_MAX_NEAREST_DISTANCE = 10.0;

    public static final String SERVER_DEVELOPMENT_URL = "spring.development.url";
    public static final String PAGINATION_CONTENT_SIZE_PER_PAGE = "pagination.content.size";

    public static final int MAX_UPLOAD_IMAGE_CRIME_LOCATION = 5;

    public static final String TENS      = "0";
    public static final String HUNDREDS  = "00";
    public static final String THOUSANDS = "000";
    public static final String MILLIONS  = "0000";

    public static final String EMPTY_STRING = "";
    public static final String SPACE_STRING = " ";

    public static final int SUCCESS_SELECT_ALL    = 1;
    public static final int SUCCESS_SELECT_DETAIL = 2;

    public static final int OPERATION_SELECT      = 1;
    public static final int OPERATION_CRU         = 2;
    public static final int OPERATION_DELETE      = 3;
    public static final int OPERATION_LOGIN       = 4;
    public static final int OPERATION_LOGOUT      = 5;
    public static final int OPERATION_HANDSHAKE   = 6;
    public static final int OPERATION_UTILIZATION = 7;
    public static final int OPERATION_SELECT_DIST = 8;

    public static final String INSERT_FROM_BATCH = "ROOT from BATCH DATA";

    public static final String DEFAULT_IMAGE_ADMIN = "default.image.admin";
    public static final String DEFAULT_PASSWORD_ADMIN = "default.password.admin";

    public static final String DEFAULT_ADMIN_FIRST_ROOT_NAME     = "default.admin.first.root.name";
    public static final String DEFAULT_ADMIN_FIRST_ROOT_USERNAME = "default.admin.first.root.username";
    public static final String DEFAULT_ADMIN_FIRST_ROOT_PASSWORD = "default.admin.first.root.password";

    public static final String ADMIN_ROLE_ROOT  = "admin.role.root";
    public static final String ADMIN_ROLE_ADMIN = "admin.role.admin";

    public static final String TEMP_FOLDER_FILE_UPLOAD = "folder.temp.upload.image";

    public static final String PASSWORD_NAME      = "password.name";
    public static final String PHOTO_PROFILE_NAME = "photo.profile.name";

    public static final String ACTIVE_NAME      = "active.name";
    public static final String ACTIVATED_NAME   = "activated.name";
    public static final String NOT_ACTIVE_NAME  = "not.active.name";
    public static final String DEACTIVATED_NAME = "deactivated.name";

    public static final String LOGIN_NAME  = "login.name";
    public static final String LOGOUT_NAME = "logout.name";

    public static final String OPERATION_NAME_READ_ALL = "operation.name.read.all";
    public static final String OPERATION_NAME_CREATE   = "operation.name.add";
    public static final String OPERATION_NAME_UPDATE   = "operation.name.update";
    public static final String OPERATION_NAME_DELETE   = "operation.name.delete";
    public static final String OPERATION_NAME_RESET    = "operation.name.reset";

    public static final String ENTITY_ADMIN          = "entity.admin";
    public static final String ENTITY_ADMIN_ID       = "entity.admin.id";
    public static final String ENTITY_ADMIN_USERNAME = "entity.admin.username";

    public static final String ENTITY_PROVINCE      = "entity.province";
    public static final String ENTITY_PROVINCE_ID   = "entity.province.id";
    public static final String ENTITY_PROVINCE_NAME = "entity.province.name";

    public static final String ENTITY_CITY      = "entity.city";
    public static final String ENTITY_CITY_ID   = "entity.city.id";
    public static final String ENTITY_CITY_NAME = "entity.city.name";

    public static final String ENTITY_SUB_DISTRICT      = "entity.subdistrict";
    public static final String ENTITY_SUB_DISTRICT_ID   = "entity.subdistrict.id";
    public static final String ENTITY_SUB_DISTRICT_NAME = "entity.subdistrict.name";

    public static final String ENTITY_URBAN_VILLAGE      = "entity.urbanvillage";
    public static final String ENTITY_URBAN_VILLAGE_ID   = "entity.urbanvillage.id";
    public static final String ENTITY_URBAN_VILLAGE_NAME = "entity.urbanvillage.name";

    public static final String ENTITY_CRIME_LOCATION      = "entity.crimelocation";
    public static final String ENTITY_CRIME_LOCATION_ID   = "entity.crimelocation.id";
    public static final String ENTITY_CRIME_LOCATION_NAME = "entity.crimelocation.name";

    public static final String ENTITY_IMAGE_CRIME_LOCATION      = "entity.imagecrimelocation";
    public static final String ENTITY_IMAGE_CRIME_LOCATION_ID   = "entity.imagecrimelocation.id";
    public static final String ENTITY_IMAGE_CRIME_LOCATION_NAME = "entity.imagecrimelocation.name";
}
