package com.group1.interview_management.dto.interview;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequest {

     private Integer currentInterviewId;
     private LocalDate schedule;
     private LocalTime startTime;
     private LocalTime endTime;
     private LocalTime oldStartTime;
     private LocalTime oldEndTime;
     private Integer candidateId;
     private List<Integer> interviewerIds;
}
