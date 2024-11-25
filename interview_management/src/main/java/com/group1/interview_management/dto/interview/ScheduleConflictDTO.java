package com.group1.interview_management.dto.interview;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleConflictDTO {
     private Integer interviewScheduleId;
     private Integer userId;
     private String userName;
     private LocalDate interviewDate;
     private LocalTime start;
     private LocalTime end;
     private Integer roleId;
}
