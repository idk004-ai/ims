package com.group1.interview_management.services.impl.Interview;

import java.util.List;

import org.springframework.stereotype.Component;

import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.dto.interview.ScheduleConflictDTO;
import com.group1.interview_management.dto.interview.ScheduleRequest;
import com.group1.interview_management.repositories.InterviewRepository;
import com.group1.interview_management.services.ScheduleValidator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CandidateScheduleValidator implements ScheduleValidator {
     private final InterviewRepository interviewRepository;

     @Override
     public List<ScheduleConflictDTO> validate(ScheduleRequest request) {
          return interviewRepository.findOverlappingInterviewsByCandidateId(
               request.getCandidateId(), 
               request.getSchedule(), 
               request.getStartTime(), 
               request.getEndTime(),
               request.getCurrentInterviewId(),
               List.of(ConstantUtils.INTERVIEW_STATUS_CANCELLED, ConstantUtils.INTERVIEW_STATUS_CLOSED)); 
     }

}
