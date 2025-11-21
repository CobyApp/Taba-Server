package com.taba.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 400 Bad Request
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "INVALID_EMAIL", "error.invalid_email"),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "EMAIL_ALREADY_EXISTS", "error.email_already_exists"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", "error.invalid_password"),
    INVALID_INVITE_CODE(HttpStatus.BAD_REQUEST, "INVALID_INVITE_CODE", "error.invalid_invite_code"),
    INVITE_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "INVITE_CODE_EXPIRED", "error.invite_code_expired"),
    INVITE_CODE_ALREADY_USED(HttpStatus.BAD_REQUEST, "INVITE_CODE_ALREADY_USED", "error.invite_code_already_used"),
    CANNOT_USE_OWN_INVITE_CODE(HttpStatus.BAD_REQUEST, "CANNOT_USE_OWN_INVITE_CODE", "error.cannot_use_own_invite_code"),
    ALREADY_FRIENDS(HttpStatus.BAD_REQUEST, "ALREADY_FRIENDS", "error.already_friends"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "error.invalid_request"),
    
    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "error.unauthorized"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "error.token_expired"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "error.invalid_token"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "error.invalid_credentials"),
    
    // 403 Forbidden
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "error.forbidden"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "error.access_denied"),
    
    // 404 Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "error.user_not_found"),
    LETTER_NOT_FOUND(HttpStatus.NOT_FOUND, "LETTER_NOT_FOUND", "error.letter_not_found"),
    FRIENDSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "FRIENDSHIP_NOT_FOUND", "error.friendship_not_found"),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND", "error.notification_not_found"),
    
    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "error.internal_server_error");

    private final HttpStatus httpStatus;
    private final String code;
    private final String messageKey; // 메시지 키로 변경
}

