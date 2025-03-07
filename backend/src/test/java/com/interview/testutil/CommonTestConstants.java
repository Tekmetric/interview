package com.interview.testutil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommonTestConstants {
    public static final String EMAIL_1 = "test1@email.com";
    public static final String EMAIL_2 = "test2@email.com";
    public static final String PASSWORD = "password";
    public static final String ENCODED_PASSWORD = "encodedPassword";
    public static final String FIRST_NAME_1 = "firstName1";
    public static final String FIRST_NAME_2 = "firstName2";
    public static final String LAST_NAME_1 = "lastName1";
    public static final String LAST_NAME_2 = "lastName2";
    public static final long ID_1 = 1L;
    public static final long ID_2 = 2L;
    public static final long ID_3 = 3L;
    public static final long ID_4 = 4L;
    public static final String JWT_TOKEN = "jwtToken";
    public static final long JWT_EXPIRES_IN = 3600L;
    public static final String PHOTO_URL_1 = "photoUrl1";
    public static final String PHOTO_URL_2 = "photoUrl2";
    public static final String NAME_1 = "name1";
    public static final String NAME_2 = "name2";
    public static final String NAME_3 = "name3";
    public static final String NAME_4 = "name4";
    public static final int PUBLICATION_YEAR = 2000;
    public static final LocalDateTime SHARED_DATE = LocalDateTime.parse(
            "07/03/2025 10:00:00",
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
    );

    public static final String BEARER_TOKEN = "bearerToken";
    public static final String BEARER_TOKEN_HEADER = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";


}
