package com.group1.interview_management.services;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import com.group1.interview_management.dto.interview.EditInterviewDTO;
import com.group1.interview_management.dto.interview.InterviewDTO;
import com.group1.interview_management.entities.Candidate;
import com.group1.interview_management.entities.Interview;

@Component
public abstract class InterviewResultProcess {

     protected abstract void validateResult(Interview interview, EditInterviewDTO dto, BindingResult errors, boolean mandatory)
               throws BindException;

     protected abstract void updateInterviewResult(Interview interview, EditInterviewDTO submitInterviewDTO);

     protected abstract void updateCandidateStatus(Candidate candidate, List<Interview> activeInterviews);

     protected abstract void updateInterviewStatus(Interview interview, EditInterviewDTO submitInterviewDTO);

     public final InterviewDTO processResult(Interview interview, EditInterviewDTO submitInterviewDTO,
               BindingResult errors, boolean mandatory) throws BindException, IllegalArgumentException {
          // Template method defining the algorithm structure
          validateStatus(interview);
          validateResult(interview, submitInterviewDTO, errors, mandatory);
          updateInterviewResult(interview, submitInterviewDTO);
          updateInterviewStatus(interview, submitInterviewDTO);
          List<Interview> activeInterviews = getActiveInterviews(interview.getCandidate().getCandidateId());
          updateCandidateStatus(interview.getCandidate(), activeInterviews);

          return getInterviewDTO(interview);
     }

     protected abstract List<Interview> getActiveInterviews(Integer candidateId);

     public abstract InterviewDTO getInterviewDTO(Interview interview);

     public abstract void validateStatus(Interview interview);
}
