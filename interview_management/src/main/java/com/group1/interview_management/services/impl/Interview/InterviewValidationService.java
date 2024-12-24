package com.group1.interview_management.services.impl.Interview;

import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.dto.interview.CreateInterviewDTO;
import com.group1.interview_management.dto.interview.InterviewContext;
import com.group1.interview_management.dto.interview.ScheduleConflictDTO;
import com.group1.interview_management.dto.interview.ScheduleValidationStrategyFactory;
import com.group1.interview_management.dto.interview.ScheduleValidationStrategyFactory.SelectedMode;
import com.group1.interview_management.entities.Candidate;
import com.group1.interview_management.entities.Job;
import com.group1.interview_management.entities.User;
import com.group1.interview_management.services.CandidateService;
import com.group1.interview_management.services.JobService;
import com.group1.interview_management.services.ScheduleValidationStrategy;
import com.group1.interview_management.services.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InterviewValidationService {

     private final ScheduleValidationStrategyFactory strategyFactory;
     private final CandidateService candidateService;
     private final JobService jobService;
     private final UserService userService;
     private final MessageSource messageSource;

     public InterviewContext validateNewInterview(CreateInterviewDTO dto, BindingResult errors) {
          return validateInterview(dto, errors, SelectedMode.CREATE, null);
     }

     public InterviewContext validateEditInterview(CreateInterviewDTO dto, BindingResult errors, Integer interviewId, LocalTime oldStartTime, LocalTime oldEndTime) {
          InterviewContext context = InterviewContext.builder()
                         .interviewId(interviewId)
                         .oldStarTime(oldStartTime)
                         .oldEndTime(oldEndTime)
                         .validationMode(SelectedMode.EDIT)
                         .build();
          return validateInterview(dto, errors, SelectedMode.EDIT, context);
     }

     private InterviewContext validateInterview(CreateInterviewDTO dto, BindingResult errors, SelectedMode mode,
               InterviewContext existingContext) {
          InterviewContext interviewContext = existingContext != null ? existingContext
                    : InterviewContext.builder().validationMode(mode).build();

          List<Field> fields = Arrays.asList(CreateInterviewDTO.class.getDeclaredFields());
          validateParticipants(dto, interviewContext, errors, fields);
          ScheduleValidationStrategy strategy = strategyFactory.getStrategy(mode);
          List<ScheduleConflictDTO> conflicts = strategy.validateSchedule(dto, interviewContext, errors, fields);
          if (!conflicts.isEmpty()) {
               injectConflictsToErrors(conflicts, errors, fields);
          }
          return interviewContext;
     }

     private void injectConflictsToErrors(List<ScheduleConflictDTO> conflicts, BindingResult errors,
               List<Field> fields) {
          StringBuilder interviewersErrorMessage = new StringBuilder();
          boolean hasInterviewerConflicts = false;
          boolean hasCandidateConflicts = false;

          // First, collect all interviewer conflict messages
          for (ScheduleConflictDTO conflict : conflicts) {
               if (conflict.getRoleId() == ConstantUtils.INTERVIEWER_ROLE) {
                    hasInterviewerConflicts = true;
                    String err = messageSource.getMessage("ME022.1", new Object[] {
                              conflict.getUserName(),
                              conflict.getStart(),
                              conflict.getEnd()
                    }, Locale.getDefault());
                    interviewersErrorMessage.append(err).append("\n");
               } else if (conflict.getRoleId() == ConstantUtils.CANDIDATE_ROLE) {
                    hasCandidateConflicts = true;
               }
          }

          // Add interviewer conflicts error once
          if (hasInterviewerConflicts) {
               errors.rejectValue(fields.get(3).getName(), "ME022.1", interviewersErrorMessage.toString().trim());
          }

          // Add candidate conflicts error once
          if (hasCandidateConflicts) {
               String errorMessage = messageSource.getMessage("ME022.2", null, Locale.getDefault());
               errors.rejectValue(fields.get(2).getName(), "ME022.2", errorMessage);
          }
     }

     private void validateParticipants(CreateInterviewDTO dto, InterviewContext context, BindingResult errors,
               List<Field> createFields) {
          // validate and save job
          Job job = jobService.getJobByIdAndStatusIds(dto.getInterview_job(), errors,
                    createFields.get(1).getName(), List.of(ConstantUtils.JOB_OPEN));
          if (job != null) {
               context.setJob(job);
               context.markAsValidated(createFields.get(1).getName());
          }

          /**
           * Modify thi @@@@@@@@@@
           */
          // validate and save candidate
          Candidate candidate = candidateService.getCandidateByIdAndStatusIds(dto.getInterview_candidate(), errors,
                    createFields.get(2).getName(),
                    List.of(ConstantUtils.CANDIDATE_BANNED));
          if (candidate != null) {
               context.setCandidate(candidate);
               context.markAsValidated(createFields.get(2).getName());
          }

          // validate and save recruiter
          List<User> recruiters = userService.getUserByIdAndRoleIds(List.of(dto.getInterview_recruiter()), errors,
                    createFields.get(7).getName(),
                    List.of(ConstantUtils.RECRUITER_ROLE));
          if (recruiters != null && !recruiters.isEmpty()) {
               context.setRecruiter(recruiters.get(0));
               context.markAsValidated(createFields.get(7).getName());
          }

          List<User> interviewers = getInterviewersByIds(dto.getInterviewer_tag(), errors, createFields);
          if (!interviewers.isEmpty()) {
               context.setInterviewers(interviewers);
               context.markAsValidated(createFields.get(4).getName());
          }
     }

     private List<User> getInterviewersByIds(Integer[] interviewerIds, BindingResult errors,
               List<Field> createFields) {
          List<User> interviewers = new ArrayList<>();
          List<User> availableInterviewers = userService.getUserByIdAndRoleIds(Arrays.asList(interviewerIds), errors,
                    createFields.get(4).getName(),
                    List.of(ConstantUtils.INTERVIEWER_ROLE));
          for (User interviewer : availableInterviewers) {
               if (interviewer != null) {
                    interviewers.add(interviewer);
               } else {
                    break;
               }
          }
          return interviewers;
     }
}
