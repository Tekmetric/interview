/**
 * Class that contains endpoints name constants
 */
package com.interview.application.rest.v1.common;

public final class PathConstants {
    private PathConstants() {
    }

    public static final String SEPARATOR = "/";
    private static final String API_VERSION_V1 = SEPARATOR + "v1";
    private static final String API = SEPARATOR + "api";
    public static final String PATH_PREFIX = API + API_VERSION_V1;

    public static final String PATH_USERS = PATH_PREFIX + "/users";
    public static final String PATH_USER_RELATIONSHIPS = PATH_PREFIX + "/userRelationships";
    public static final String PATH_USER_RELATIONSHIPS_FRIENDS = "/friends";
    public static final String PATH_ACCEPTED = "/accept";
    public static final String PATH_DECLINED = "/decline";

    public static final String TOTAL_COUNT = "Total-Count";
}
