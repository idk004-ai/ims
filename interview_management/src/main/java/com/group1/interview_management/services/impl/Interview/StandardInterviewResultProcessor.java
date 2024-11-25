package com.group1.interview_management.services.impl.Interview;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.common.StatusValidator;
import com.group1.interview_management.dto.interview.EditInterviewDTO;
import com.group1.interview_management.dto.interview.InterviewDTO;
import com.group1.interview_management.entities.Candidate;
import com.group1.interview_management.entities.Interview;
import com.group1.interview_management.repositories.CandidateRepository;
import com.group1.interview_management.repositories.InterviewRepository;
import com.group1.interview_management.services.InterviewResultProcess;
import com.group1.interview_management.services.MasterService;
import com.group1.interview_management.services.impl.CandidateStatusStrategy;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StandardInterviewResultProcessor extends InterviewResultProcess {
     private final InterviewRepository interviewRepository;
     private final CandidateStatusStrategy candidateStatusStrategy;
     private final CandidateRepository candidateRepository;
     private final MessageSource messageSource;
     private final MasterService masterService;

     @Override
     protected void validateResult(Interview interview, EditInterviewDTO dto, BindingResult errors, boolean mandatory)
               throws BindException {
          if (interview == null) {
               String err = messageSource.getMessage("ME008", null, Locale.getDefault());
               errors.rejectValue(ConstantUtils.ERROR, "ME008", err);
               throw new BindException(errors);
          }

          List<Field> editFields = Arrays.asList(EditInterviewDTO.class.getDeclaredFields());
          if (mandatory && dto.getInterview_result() == ConstantUtils.INTERVIEW_RESULT_NA) {
               String err = messageSource.getMessage("ME002", null, Locale.getDefault());
               errors.rejectValue(editFields.get(0).getName(), "ME002", err);
               throw new BindException(errors);
          }
     }

     @Override
     protected void updateInterviewResult(Interview interview, EditInterviewDTO submitInterviewDTO) {
          interview.setResultInterviewId(submitInterviewDTO.getInterview_result());
          interview.setInterviewNote(submitInterviewDTO.getNote());
          interviewRepository.save(interview);
     }

     @Override
     protected void updateCandidateStatus(Candidate candidate, List<Interview> activeInterviews) {
          int newStatus = candidateStatusStrategy.determineNewStatus(activeInterviews);
          candidate.setStatusId(newStatus);
          candidateRepository.save(candidate);
     }

     @Override
     protected List<Interview> getActiveInterviews(Integer candidateId) {
          return interviewRepository.findActiveInterviewsByCandidateId(candidateId, List.of(ConstantUtils.INTERVIEW_STATUS_CANCELLED));
     }

     @Override
     public InterviewDTO getInterviewDTO(Interview interview) {
          return InterviewDTO.builder()
                    .interviewId(interview.getInterviewId())
                    .title(interview.getTitle())
                    .candidate(interview.getCandidate().getName())
                    .interviewer("" + interview.getInterviewAssignments().stream()
                              .map(i -> i.getInterviewer().getId())
                              .toArray(Integer[]::new))
                    .time(interview.getSchedule() + " " + interview.getStartTime() + " - " + interview.getEndTime())
                    .result(masterService.findByCategoryAndCategoryId(ConstantUtils.INTERVIEW_RESULT,
                              interview.getResultInterviewId()).getCategoryValue())
                    .statusInterview(masterService.findByCategoryAndCategoryId(ConstantUtils.INTERVIEW_STATUS,
                              interview.getStatusInterviewId()).getCategoryValue())
                    .job(interview.getJob().getTitle())
                    .build();
     }

     @Override
     protected void updateInterviewStatus(Interview interview, EditInterviewDTO submitInterviewDTO) {
          if (submitInterviewDTO.getInterview_result() != ConstantUtils.INTERVIEW_RESULT_NA) {
               interview.setStatusInterviewId(ConstantUtils.INTERVIEW_STATUS_CLOSED);
               interviewRepository.save(interview);
          }
     }

     @Override
     public void validateStatus(Interview interview) {
          boolean res = StatusValidator.isInvalidStatus(interview.getStatusInterviewId(),
                    List.of(ConstantUtils.INTERVIEW_STATUS_CANCELLED, ConstantUtils.INTERVIEW_STATUS_CLOSED));
          if (res) {
               String error = messageSource.getMessage("ME050", null, Locale.getDefault());
               throw new IllegalArgumentException(error);
          }
     }
}
