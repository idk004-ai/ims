package com.group1.interview_management.services.impl.Interview;

import com.group1.interview_management.dto.interview.CreateInterviewDTO;
import com.group1.interview_management.dto.interview.EditInterviewDTO;
import com.group1.interview_management.dto.interview.InterviewContext;
import com.group1.interview_management.entities.Interview;

public class InterviewDirector {
     public static Interview constructNewInterview(InterviewBuilder builder, CreateInterviewDTO dto, InterviewContext context) {
          return builder.fromCreateDTO(dto, context.getRecruiter(), context.getJob(), context.getCandidate()).build();
     }

     public static Interview constructEditInterview(InterviewBuilder builder, EditInterviewDTO dto, InterviewContext context) {
          return builder.fromEditDTO(dto, context.getRecruiter(), context.getJob(), context.getCandidate()).build();
     }
}
