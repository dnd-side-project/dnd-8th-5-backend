package com.dnd.modutime.core.common;

/**
 * modutime 공통상수 인터페이스
 */
public class Constants {

    private Constants() {
        throw new UnsupportedOperationException("Utility class");
    }

    // 정규식
    public static final String REG_EXP_IP_ADDRESS = "\\b\\d+$";
    public static final String REG_EXP_PHONE_NUMBER = "^01[0-9]\\d{7,8}$";
    public static final String REG_EXP_PASSWORD = "(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()_+|<>?:{}\\-=,./])[a-zA-Z\\d~!@#$%^&*()_+|<>?:{}\\-=,./]{8,20}$";
    public static final String REG_EXP_NAME = "^[가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z]*$";

    // time
    public static final String TIME_ZONE = "Asia/Seoul";
    public static final int ONE_DAY_CONVERT_SECOND = 86400;

    // EXCEL
    public static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    // ClientMode
    public static final String CLIENT_MODE_STUB = "stub";
    public static final String CLIENT_MODE_FEIGN = "feign";
    public static final String CLIENT_MODE_GOOGLE = "google";
    public static final String CLIENT_MODE_OAUTH2 = "oauth2";
    public static final String MODE_GPT = "gpt";
    public static final String MODE_DIRECT = "direct";
    public static final String CLIENT_MODE_REST = "rest";
    public static final String CLIENT_MODE_S3 = "s3";

    // 인증
    public static final String TOKEN_PREFIX_SEPARATOR = " ";
    public static final String BEARER = "Bearer";
    public static final String AUTHORIZATION = "Authorization";

    // 헤더
    public static final String FORM_URLENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded;charset=utf8;";
    public static final String JSON_CONTENT_TYPE = "application/json";

    // AWS S3
    public static final String BUCKET_NAME = "modutime-file-store";
    public static final String REGION = "ap-northeast-2";

    // openapi
    public static final class Tags {
        // API 소개
        public static final String OVERVIEW_DOC_ID = "1-1-overview";
        public static final String AUTHENTICATION_DOC_ID = "1-2-authentication";
        public static final String RESPONSE_DOC_ID = "1-3-response";
        public static final String CHANGES_DOC_ID = "1-4-changes";

        // 어드민 API 목록
        public static final String ADMIN_AUTHENTICATION_API_TAG_ID = "2-1-authentication";
        public static final String ADMIN_FILE_API_TAG_ID = "2-2-file";
        public static final String ADMIN_WORKBOOK_API_TAG_ID = "2-3-workbook";
        public static final String ADMIN_PRODUCT_API_TAG_ID = "2-4-product";
        public static final String ADMIN_ATTACHMENT_API_TAG_ID = "2-5-attachment";

        // 서비스 API 목록
        public static final String SERVICE_OAUTH2_API_TAG_ID = "3-1-oauth2";
        public static final String SERVICE_USER_API_TAG_ID = "3-2-user";
        public static final String SERVICE_PRODUCT_API_TAG_ID = "3-3-product";
        public static final String SERVICE_FILE_API_TAG_ID = "3-4-file";
    }

    public static final class Extensions {
        public static final String DISPLAY_TAG_NAME = "x-displayName";
        public static final String TAG_GROUPS = "x-tagGroups";
    }
}
