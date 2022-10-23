package com.interview.domain.exception;

/**
 * Error internationalization key constants.
 */
public final class ErrorI18nKey {
    /** Bad request error internationalization key. */
    public static final String BAD_REQUEST_ERROR_I18N_KEY = "request.error";
    /** User email already exists internationalization key. */
    public static final String USER_EMAIL_ALREADY_EXISTS_ERROR_I18N_KEY = "request.userEmailAlreadyExists.error";
    /** Authorization error internationalization key. */
    public static final String AUTHORIZATION_I18N_KEY = "authorization.error";
    /** Forbidden error internationalization key. */
    public static final String FORBIDDEN_I18N_KEY = "forbidden.error";
    /** Not found error internationalization key. */
    public static final String NOT_FOUND_KEY = "request.notFound";
    /** Internal server error internationalization key. */
    public static final String SERVER_ERROR_I18N_KEY = "server.error";

    /** Priate constructor.*/
    private ErrorI18nKey() {
    }
}