package com.group1.interview_management.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

import java.util.HashMap;
import java.util.Map;

public enum BusinessErrorCodes {
    
    NO_CODE(0, NOT_IMPLEMENTED, "No code"),
    INCORRECT_CURRENT_PASSWORD(300, BAD_REQUEST, "Current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, BAD_REQUEST, "The new password does not match"),
    ACCOUNT_LOCKED(302, FORBIDDEN, "User account is locked"),
    ACCOUNT_DISABLED(303, FORBIDDEN, "User account is disabled"),
    BAD_CREDENTIALS(304, FORBIDDEN, "Login and / or Password is incorrect"),
    EXPIRED_TOKEN(305, BAD_REQUEST, "Token Invalid or Expired"),
    // Common errors
    CANDIDATE_ERROR(306, BAD_REQUEST, "Candidate Error"),
    INTERVIEW_ERROR(307, BAD_REQUEST, "Interview Error"),
    JOB_ERROR(308, BAD_REQUEST, "Job Error"),
    USER_ERROR(309, BAD_REQUEST, "User Error")
    ;

    @Getter
    private final int code;
    @Getter
    private final String description;
    @Getter
    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code, HttpStatus status, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = status;
    }

    public static final String getConstants(BusinessErrorCodes businessErrorCodes) {
        Map<String, Object> err = new HashMap<>();
        err.put("code", businessErrorCodes.getCode());
        err.put("description", businessErrorCodes.getDescription());
        err.put("status", businessErrorCodes.getHttpStatus());
        return err.toString();
    }
}
