package com.group1.interview_management.dto.interview;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.group1.interview_management.dto.interview.ScheduleValidationStrategyFactory.SelectedMode;
import com.group1.interview_management.entities.Candidate;
import com.group1.interview_management.entities.Job;
import com.group1.interview_management.entities.User;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class InterviewContext {
     private Job job;
     private Candidate candidate;
     private User recruiter;
     private List<User> interviewers;
     private List<ScheduleConflictDTO> scheduleConflicts;
     private boolean isValidated;

     private Integer interviewId;
     private LocalTime oldStarTime;
     private LocalTime oldEndTime;
     private SelectedMode validationMode;

     @Builder.Default
     // Để track xem đối tượng nào đã được validate
     private Set<String> validatedObjects = new HashSet<>();

     public void markAsValidated(String objectType) {
          validatedObjects.add(objectType);
     }

     public boolean isObjectValidated(String objectType) {
          return validatedObjects.contains(objectType);
     }
}

