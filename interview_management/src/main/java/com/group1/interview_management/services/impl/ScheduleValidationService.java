package com.group1.interview_management.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.group1.interview_management.dto.interview.ScheduleConflictDTO;
import com.group1.interview_management.dto.interview.ScheduleRequest;
import com.group1.interview_management.services.ScheduleValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleValidationService {
     private final List<ScheduleValidator> validators;

     public List<ScheduleConflictDTO> validateSchedule(ScheduleRequest request) {
          return validators.stream()
                    .flatMap(validator -> validator.validate(request).stream())
                    .toList();
     }
}
