package com.group1.interview_management.handler;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExceptionResponse {
     public Integer businessErrorCode;
     private String businessErrorDescription;
     private String error;
     private Set<String> validationErrors;
     private Map<String, String> errors;
}
