package com.harifrizki.crimemapsappsapi.utils;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.apache.commons.io.FileUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static com.harifrizki.crimemapsappsapi.utils.AppsConstants.*;
import static com.harifrizki.crimemapsappsapi.utils.ControllerConstants.GENERAL_CONTROLLER_URL;

public class UtilizationClass {

    public static String hashPassword(String passwordBeforeHash) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(passwordBeforeHash);
    }

    public static boolean comparePassword(String adminPasswordLogin, String adminPasswordFromDb) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.matches(adminPasswordLogin, adminPasswordFromDb);
    }

    public static String combineUrl(String controller, String[] params, String[]paramValue) {
        String url = GENERAL_CONTROLLER_URL.concat(controller);

        StringBuilder param = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            if (i == 0)
                param.append("?");

            if (i >= 1)
                param.append("&");

            param.append(String.valueOf(params[i]).concat("=").concat(paramValue[i]));
        }

        return url.concat(param.toString());
    }

    public static String existEntityNotFound(String entity, String entityId, String id) {
        return entity.concat(" with ").concat(entityId).concat(" [").concat(id).concat("] was Not Found");
    }

    public static String existEntityNotFound(
            String entity,
            String entityId,
            String id,
            String operation,
            String subjectOperation) {
        return entity.
                concat(" with ").
                concat(entityId).
                concat(" [").
                concat(id).
                concat("] who will [").
                concat(operation).
                concat("] ").
                concat(subjectOperation).
                concat(" was Not Found");
    }

    public static String successProcess(
            String entity,
            String entityId,
            String id,
            String entityName,
            String name,
            String operation) {
        return "Successfully [".
                concat(operation).
                concat("] ").
                concat(entity).
                concat(" with ").
                concat(entityId).
                concat(" [").
                concat(id).
                concat("], and ").
                concat(entityName).
                concat(" [").
                concat(name).
                concat("]");
    }

    public static String successProcess(String entity, String operation) {
        return "Successfully [".concat(operation).concat("] ").concat(entity);
    }

    public static String successProcess(int successType, String entity) {
        switch (successType)
        {
            case SUCCESS_SELECT_ALL:
                return "Successfully Get [All ".concat(entity).concat("] per Page");
            case SUCCESS_SELECT_DETAIL:
                return "Successfully Get [Detail ".concat(entity).concat("]");
            default:
                return EMPTY_STRING;
        }
    }

    public static File convert(MultipartFile multipartFile) throws IOException {
        File newFile = new File(System.getProperty("java.io.tmpdir") + multipartFile.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(newFile);
        fos.write(multipartFile.getBytes());
        fos.close();
        return newFile;
    }

    public static RequestBody toRequestBody (String value) {
        return RequestBody.create(value, MediaType.parse("text/plain"));
    }

    public static RequestBody toRequestBody (File file) {
        return RequestBody.create(file, MediaType.parse("image/*"));
    }
}
