package com.group1.interview_management.services.impl.Interview;

import java.util.List;

import org.springframework.stereotype.Component;

import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.entities.Candidate;
import com.group1.interview_management.entities.Interview;
import com.group1.interview_management.repositories.CandidateRepository;
import com.group1.interview_management.repositories.InterviewRepository;
import com.group1.interview_management.services.impl.CandidateStatusStrategy;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CandidateStatusStrategyImpl implements CandidateStatusStrategy {
     private final InterviewRepository interviewRepository;
     private final CandidateRepository candidateRepository;

     @Override
     public int determineNewStatus(List<Interview> interviewSchedules) {
          boolean hasInterviewScheduleNoResult = interviewSchedules.stream()
                    .anyMatch(i -> i.getResultInterviewId() == ConstantUtils.INTERVIEW_RESULT_NA);
          if (hasInterviewScheduleNoResult) {
               return ConstantUtils.CANDIDATE_WAITING_FOR_INTERVIEW;
          }

          boolean hasPassedInterviewSchedule = interviewSchedules.stream()
                    .anyMatch(i -> i.getResultInterviewId() == ConstantUtils.INTERVIEW_RESULT_PASSED);

          return hasPassedInterviewSchedule ? ConstantUtils.CANDIDATE_PASSED_INTERVIEW
                    : ConstantUtils.CANDIDATE_FAILED_INTERVIEW;
     }

     @Override
     public void determineStatusForOldCandidate(Candidate oldCandidate) {
          if (oldCandidate == null) {
               return;
          }
          int numberOfInterviews = interviewRepository
                    .countInterviewsByCandidateId(
                              oldCandidate.getCandidateId(),
                              List.of(
                                        ConstantUtils.INTERVIEW_STATUS_CLOSED,
                                        ConstantUtils.INTERVIEW_STATUS_CANCELLED));
          if (numberOfInterviews == 0) {
               oldCandidate.setStatusId(ConstantUtils.CANDIDATE_OPEN);
               candidateRepository.save(oldCandidate);
          }
     }

}
