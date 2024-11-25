package com.group1.interview_management.services.impl.Interview;

import com.group1.interview_management.dto.interview.CreateInterviewDTO;
import com.group1.interview_management.dto.interview.EditInterviewDTO;

import com.group1.interview_management.entities.Candidate;
import com.group1.interview_management.entities.Interview;
import com.group1.interview_management.entities.User;
import com.group1.interview_management.entities.Job;

public class InterviewDirector {
     public static Interview constructNewInterview(InterviewBuilder builder, CreateInterviewDTO dto, User recruiter, Job job, Candidate candidate) {
          return builder.fromCreateDTO(dto, recruiter, job, candidate).build();
     }

     public static Interview constructEditInterview(InterviewBuilder builder, EditInterviewDTO dto, User recruiter, Job job, Candidate candidate) {
          return builder.fromEditDTO(dto, recruiter, job, candidate).build();
     }
}
