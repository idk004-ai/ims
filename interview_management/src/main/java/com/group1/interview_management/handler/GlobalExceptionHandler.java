package com.group1.interview_management.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.group1.interview_management.dto.interview.CreateInterviewDTO;

import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

     private final MessageSource messageSource;

     @ExceptionHandler(HttpMessageNotReadableException.class)
     public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
          if (ex.getCause() instanceof InvalidFormatException) {
               InvalidFormatException ife = (InvalidFormatException) ex.getCause();
               String fieldName = ife.getPath().get(0).getFieldName();

               // Create BindingResult
               CreateInterviewDTO dto = new CreateInterviewDTO();
               BindingResult bindingResult = new BeanPropertyBindingResult(dto, "createInterviewDTO");

               // Add error
               bindingResult.rejectValue(
                         fieldName,
                         "ME002.2",
                         messageSource.getMessage("ME002.2", null, Locale.getDefault()));

               // Convert to BindException
               return handleBindException(new BindException(bindingResult));
          }

          return ResponseEntity
                    .status(BAD_REQUEST)
                    .body(ExceptionResponse.builder()
                              .error(ex.getMessage())
                              .build());
     }

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
