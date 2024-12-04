package com.group1.interview_management.common.schedule_validation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.group1.interview_management.dto.interview.CreateInterviewDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InterviewTimeValidator implements ConstraintValidator<ValidInterviewTime, CreateInterviewDTO> {
     private final MessageSource messageSource;

     @Override
     public boolean isValid(CreateInterviewDTO value, ConstraintValidatorContext context) {
          if (value.getInterview_schedule() == null || value.getStartTime() == null || value.getEndTime() == null) {
               return false;
          }

          LocalDate schedule = value.getInterview_schedule();
          LocalTime startTime = value.getStartTime();
          LocalTime endTime = value.getEndTime();
          String error = "";

          // Check if schedule is not in the past
          if (schedule.isBefore(LocalDate.now())) {
               context.disableDefaultConstraintViolation();
               error = messageSource.getMessage("ME017", null, Locale.getDefault());
               context.buildConstraintViolationWithTemplate(error)
                         .addPropertyNode("interview_schedule")
                         .addConstraintViolation();
               return false;
          }

          // check if startTime is not in the past
          if (schedule.isEqual(LocalDate.now()) && (startTime.isBefore(LocalTime.now()) || endTime.isBefore(LocalTime.now()))) {
               context.disableDefaultConstraintViolation();
               error = messageSource.getMessage("ME022.5", null, Locale.getDefault());
               context.buildConstraintViolationWithTemplate(error)
                         .addPropertyNode("startTime")
                         .addConstraintViolation();
               return false;
          }

          // Check if startTime is before endTime
          if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
               context.disableDefaultConstraintViolation();
               error = messageSource.getMessage("ME018", null, Locale.getDefault());
               context.buildConstraintViolationWithTemplate(error)
                         .addPropertyNode("startTime")
                         .addConstraintViolation();
               return false;
          }

          return true;
     }

}
