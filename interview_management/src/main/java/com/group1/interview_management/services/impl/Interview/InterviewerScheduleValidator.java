package com.group1.interview_management.services.impl.Interview;

import java.util.List;

import org.springframework.stereotype.Component;

import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.dto.interview.ScheduleConflictDTO;
import com.group1.interview_management.dto.interview.ScheduleRequest;
import com.group1.interview_management.entities.User;
import com.group1.interview_management.repositories.InterviewRepository;
import com.group1.interview_management.services.ScheduleValidator;
import com.group1.interview_management.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterviewerScheduleValidator implements ScheduleValidator {
     private final InterviewRepository interviewRepository;
     private final UserService userService;

     @Override
     public List<ScheduleConflictDTO> validate(ScheduleRequest request) {
          List<User> interviewers = userService.getUserByIdAndRoleIds(request.getInterviewerIds(), null, null,
                    List.of(ConstantUtils.INTERVIEWER_ROLE));
          return interviewRepository.findOverlappingInterviews(
                    request.getSchedule(),
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getCurrentInterviewId(),
                    List.of(ConstantUtils.INTERVIEW_STATUS_CANCELLED, ConstantUtils.INTERVIEW_STATUS_CLOSED))
                    .stream()
                    .filter(conflict -> interviewers.stream()
                              .anyMatch(interviewer -> interviewer.getId().equals(conflict.getUserId())))
                    .toList();
     }

}
