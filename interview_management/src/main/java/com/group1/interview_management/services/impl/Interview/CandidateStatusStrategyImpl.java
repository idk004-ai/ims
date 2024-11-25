package com.group1.interview_management.services.impl.Interview;

import java.util.List;

import org.springframework.stereotype.Component;

import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.entities.Interview;
import com.group1.interview_management.services.impl.CandidateStatusStrategy;

@Component
public class CandidateStatusStrategyImpl implements CandidateStatusStrategy {

     @Override
     public int determineNewStatus(List<Interview> interviewSchedules) {
          boolean hasInterviewScheduleNoResult = interviewSchedules.stream()
                    .anyMatch(i -> i.getResultInterviewId() == ConstantUtils.INTERVIEW_RESULT_NA);
          if (hasInterviewScheduleNoResult) {
               return ConstantUtils.CANDIDATE_WAITING_FOR_INTERVIEW;
          }

          boolean hasPassedInterviewSchedule = interviewSchedules.stream()
                    .anyMatch(i -> i.getResultInterviewId() == ConstantUtils.INTERVIEW_RESULT_PASSED);

          return hasPassedInterviewSchedule ? ConstantUtils.CANDIDATE_PASSED_INTERVIEW : ConstantUtils.CANDIDATE_FAILED_INTERVIEW;
     }

}
