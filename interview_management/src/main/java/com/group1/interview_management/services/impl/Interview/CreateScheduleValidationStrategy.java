package com.group1.interview_management.services.impl.Interview;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import com.group1.interview_management.dto.interview.CreateInterviewDTO;
import com.group1.interview_management.dto.interview.InterviewContext;
import com.group1.interview_management.dto.interview.ScheduleConflictDTO;
import com.group1.interview_management.dto.interview.ScheduleRequest;
import com.group1.interview_management.services.ScheduleValidationStrategy;
import com.group1.interview_management.services.impl.ScheduleValidationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreateScheduleValidationStrategy implements ScheduleValidationStrategy {
     private final ScheduleValidationService scheduleValidationService;

     @Override
     public List<ScheduleConflictDTO> validateSchedule(CreateInterviewDTO dto, InterviewContext context, BindingResult errors,
               List<Field> fields) {
          ScheduleRequest request = ScheduleRequest.builder()
                    .schedule(dto.getInterview_schedule())
                    .startTime(dto.getStartTime())
                    .endTime(dto.getEndTime())
                    .candidateId(dto.getInterview_candidate())
                    .interviewerIds(Arrays.asList(dto.getInterviewer_tag()))
                    .currentInterviewId(null)
                    .build();
          return scheduleValidationService.validateSchedule(request);
     }

}
