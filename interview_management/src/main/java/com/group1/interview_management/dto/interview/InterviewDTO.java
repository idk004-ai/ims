package com.group1.interview_management.dto.interview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewDTO {
     private Integer interviewId;
     private String title;
     private String candidate;
     private String interviewer;
     private String time;
     private String result;
     private String statusInterview;
     private String job;

}
