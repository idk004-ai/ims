package com.group1.interview_management.services;

import java.util.List;

import com.group1.interview_management.dto.interview.ScheduleConflictDTO;
import com.group1.interview_management.dto.interview.ScheduleRequest;

public interface ScheduleValidator {
     List<ScheduleConflictDTO> validate(ScheduleRequest request); 
}
