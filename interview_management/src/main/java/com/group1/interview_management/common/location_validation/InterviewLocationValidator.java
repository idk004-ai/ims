package com.group1.interview_management.common.location_validation;

import java.util.Locale;

import org.springframework.context.MessageSource;

import com.group1.interview_management.dto.interview.CreateInterviewDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InterviewLocationValidator implements ConstraintValidator<ValidInterviewLocation, CreateInterviewDTO> {
     private final MessageSource messageSource;

     private boolean isLocationEmpty(String location) {
          return location == null || location.trim().isEmpty();
     }

     private boolean isMeetingLinkEmpty(String meetingLink) {
          return meetingLink == null || meetingLink.trim().isEmpty();
     }

     @Override
     public boolean isValid(CreateInterviewDTO value, ConstraintValidatorContext context) {
          String error = "";
          if (isLocationEmpty(value.getInterview_location()) && isMeetingLinkEmpty(value.getMeetingLink())) {
               context.disableDefaultConstraintViolation();
               error = messageSource.getMessage("ME002", null, Locale.getDefault());
               context.buildConstraintViolationWithTemplate(error)
                         .addPropertyNode("meetingLink")
                         .addConstraintViolation();
               return false;
          } else if (!isLocationEmpty(value.getInterview_location()) && !isMeetingLinkEmpty(value.getMeetingLink())) {
               context.disableDefaultConstraintViolation();
               error = messageSource.getMessage("ME022.4", null, Locale.getDefault());
               context.buildConstraintViolationWithTemplate(error)
                         .addPropertyNode("meetingLink")
                         .addConstraintViolation();
               return false;
          }
          return true;
     }

}
