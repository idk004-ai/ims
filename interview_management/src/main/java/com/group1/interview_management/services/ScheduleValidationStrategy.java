package com.group1.interview_management.services;

import java.lang.reflect.Field;
import java.util.List;

import org.springframework.validation.BindingResult;

import com.group1.interview_management.dto.interview.CreateInterviewDTO;
import com.group1.interview_management.dto.interview.InterviewContext;
import com.group1.interview_management.dto.interview.ScheduleConflictDTO;

public interface ScheduleValidationStrategy {
     List<ScheduleConflictDTO> validateSchedule(CreateInterviewDTO dto, InterviewContext context, BindingResult errors, List<Field> fields);
}
