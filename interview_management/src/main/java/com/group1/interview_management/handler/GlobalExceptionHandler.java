package com.group1.interview_management.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.http.HttpStatus;

@RestControllerAdvice
public class GlobalExceptionHandler {

     @ExceptionHandler(MessagingException.class)
     public ResponseEntity<?> handleException(MessagingException exp) {
          return ResponseEntity
                    .status(INTERNAL_SERVER_ERROR)
                    .body(
                              ExceptionResponse.builder()
                                        .error(exp.getMessage())
                                        .build());
     }

     @ExceptionHandler(IllegalArgumentException.class)
     public ResponseEntity<?> handleJwtAuthenticationException(IllegalArgumentException ex) {
          return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ExceptionResponse.builder()
                              .error(ex.getMessage())
                              .build());
     }

     @ExceptionHandler(ExpiredJwtException.class)
     public ResponseEntity<?> handleJwtExpiredException(ExpiredJwtException ex) {
          return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ExceptionResponse.builder()
                              .businessErrorCode(BusinessErrorCodes.EXPIRED_TOKEN.getCode())
                              .businessErrorDescription(BusinessErrorCodes.EXPIRED_TOKEN.getDescription())
                              .error(ex.getMessage())
                              .build());
     }

     @ExceptionHandler(AccessDeniedException.class)
     public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException exp) {
          return ResponseEntity
                    .status(FORBIDDEN)
                    .body(exp.getMessage());
     }

     @ExceptionHandler(BindException.class)
     public ResponseEntity<?> handleBindException(BindException exp) {
          return ResponseEntity
                    .status(BAD_REQUEST)
                    .body(exp.getAllErrors());
     }

     @ExceptionHandler(Exception.class)
     public ResponseEntity<?> handleException(Exception exp) {
          exp.printStackTrace();
          return ResponseEntity
                    .status(INTERNAL_SERVER_ERROR)
                    .body(
                              ExceptionResponse.builder()
                                        .businessErrorDescription("Internal error, please contact the admin")
                                        .error(exp.getMessage())
                                        .build());
     }

}
