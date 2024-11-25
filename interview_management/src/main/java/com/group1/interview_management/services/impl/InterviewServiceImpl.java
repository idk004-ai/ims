package com.group1.interview_management.services.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.data.domain.Pageable;
import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.common.EmailTemplateName;
import com.group1.interview_management.dto.OfferCreateDTO;
import com.group1.interview_management.entities.Interview;
import com.group1.interview_management.dto.interview.InterviewDTO;
import com.group1.interview_management.dto.interview.InterviewFilterDTO;
import com.group1.interview_management.dto.interview.ScheduleConflictDTO;
import com.group1.interview_management.dto.interview.ScheduleRequest;
import com.group1.interview_management.repositories.MasterRepository;
import com.group1.interview_management.dto.interview.CreateInterviewDTO;
import com.group1.interview_management.dto.interview.EditInterviewDTO;
import com.group1.interview_management.entities.Candidate;
import com.group1.interview_management.entities.InterviewAssignment;
import com.group1.interview_management.entities.Job;
import com.group1.interview_management.entities.User;
import com.group1.interview_management.repositories.CandidateRepository;
import com.group1.interview_management.repositories.InterviewAssignmentRepository;
import com.group1.interview_management.repositories.InterviewRepository;
import com.group1.interview_management.services.CandidateService;
import com.group1.interview_management.services.InterviewResultProcess;
import com.group1.interview_management.services.JobService;
import com.group1.interview_management.services.UserService;
import com.group1.interview_management.services.InterviewService;
import com.group1.interview_management.services.MasterService;
import com.group1.interview_management.services.impl.Interview.InterviewDirector;
import com.group1.interview_management.services.impl.Interview.InterviewBuilder;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InterviewServiceImpl implements InterviewService {

     private final InterviewRepository interviewRepository;
     private final MasterRepository masterRepository;
     private final MasterService masterService;
     private final MessageSource messageSource;
     private final JobService jobService;
     private final CandidateService candidateService;
     private final UserService userService;
     private final CandidateRepository candidateRepository;
     private final InterviewAssignmentRepository iaRepository;
     private final EmailService emailService;
     private final ScheduleValidationService scheduleValidationService;
     private final InterviewResultProcess resultProcessor;

     private void validateUser(Authentication authenticatedUser) {
          User user = (User) authenticatedUser.getPrincipal();
          if (user == null) {
               String err = messageSource.getMessage("ME002.1", null, Locale.getDefault());
               throw new AccessDeniedException(err);
          }
     }

     @Override
     public List<Interview> getAllInterview(LocalDate startDate, LocalDate endDate) {
          return interviewRepository.findUpcomingInterviewsInDateRange(startDate, endDate,
                    List.of(ConstantUtils.INTERVIEW_STATUS_CLOSED, ConstantUtils.INTERVIEW_STATUS_CANCELLED));
     }

     @Override
     public Page<InterviewDTO> getAllInterview(InterviewFilterDTO filter, Authentication auth) {
          validateUser(auth);
          Pageable pageable = PageRequest.of(filter.getPage(), filter.getPageSize(),
                    Sort.by("createdDate").descending());
          Page<InterviewDTO> interviewPageDTO = interviewRepository.findAllByCondition(
                    filter.getStatusId(),
                    filter.getInterviewerId(),
                    filter.getQuery(),
                    ConstantUtils.INTERVIEW_RESULT,
                    ConstantUtils.INTERVIEW_STATUS,
                    pageable);
          return interviewPageDTO;

     }

     public List<OfferCreateDTO> getinterviewnulloffer() {
          List<Interview> interviews = interviewRepository.findAll();
          List<OfferCreateDTO> list = new ArrayList<>();

          for (Interview interview : interviews) {
               if (interview.getContractTypeId() == 0 || interview.getSalary() == 0
                         || interview.getOfferdepartment() == 0) {
                    OfferCreateDTO listDTOs = new OfferCreateDTO();
                    listDTOs.setInterviewId(interview.getInterviewId());
                    listDTOs.setCandidateName(interview.getCandidate().getName());
                    listDTOs.setPosition(masterRepository.findByCategoryAndCategoryId(ConstantUtils.POSITION,
                              interview.getCandidate().getPositionId()).get().getCategory());
                    String level = masterService.getAllLevelsById(interview.getJob().getLevel()).toString();
                    listDTOs.setLevel(level.substring(1, level.length() - 1));
                    listDTOs.setRecruiter(interview.getCandidate().getRecruiter().getFullname());
                    listDTOs.setInterviewinfo(interview.getTitle());
                    listDTOs.setCreateDate(LocalDateTime.now());
                    listDTOs.setModifiedDate(LocalDateTime.now());
                    listDTOs.setInterviewNote(interview.getInterviewNote());
                    list.add(listDTOs);
               }
          }
          return list;
     }

     public OfferCreateDTO getinterviewByID(Integer id) {
          List<OfferCreateDTO> interviews = getinterviewnulloffer();
          return interviews.stream()
                    .filter(offer -> offer.getInterviewId().equals(id))
                    .findFirst()
                    .orElse(null); // or throw an exception if preferred
     }

     /**
      * Get interviewers by their ids
      * Get the interviewer by id and role id -> check if the interviewer is
      * available -> if not available, add error message to the errors
      * If the interviewer is available, add the interviewer to the list
      * 
      * @param interviewerIds
      * @param errors
      * @param fields
      * @return List<User> interviewers
      */

     private List<User> getInterviewersByIds(Integer[] interviewerIds, BindingResult errors, List<Field> fields) {
          List<User> interviewers = new ArrayList<>();
          List<User> availableInterviewers = userService.getUserByIdAndRoleIds(Arrays.asList(interviewerIds), errors,
                    fields.get(4).getName(),
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

     /**
      * Inject the schedule conflicts to the errors
      *
      * @param conflicts
      * @param errors
      * @param field
      */
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

     private Set<InterviewAssignment> generateInterviewAssignments(List<User> interviewers, Interview interview) {
          return interviewers.stream()
                    .map(interviewer -> InterviewAssignment.builder()
                              .interview(interview)
                              .interviewer(interviewer)
                              .build())
                    .collect(Collectors.toSet());
     }

     /**
      * Create interview
      *
      * @param dto
      * @param authenticatedUser
      * @param errors
      * @return InterviewDTO
      */
     @Override
     public InterviewDTO createInterview(CreateInterviewDTO dto, Authentication authenticatedUser, BindingResult errors)
               throws Exception {

          List<Field> fields = Arrays.asList(CreateInterviewDTO.class.getDeclaredFields());

          Job job = jobService.getJobByIdAndStatusIds(dto.getInterview_job(), errors, fields.get(1).getName(),
                    List.of(ConstantUtils.JOB_OPEN));
          Candidate candidate = candidateService.getCandidateByIdAndStatusIds(dto.getInterview_candidate(), errors,
                    fields.get(2).getName(),
                    List.of(ConstantUtils.CANDIDATE_OPEN, ConstantUtils.CANDIDATE_WAITING_FOR_INTERVIEW));
          List<User> recruiters = userService.getUserByIdAndRoleIds(List.of(dto.getInterview_recruiter()), errors,
                    fields.get(7).getName(),
                    List.of(ConstantUtils.RECRUITER_ROLE));
          User recruiter = recruiters.isEmpty() ? null : recruiters.get(0);
          List<User> interviewers = getInterviewersByIds(dto.getInterviewer_tag(), errors, fields);

          ScheduleRequest request = ScheduleRequest.builder()
                    .schedule(dto.getInterview_schedule())
                    .startTime(dto.getStartTime())
                    .endTime(dto.getEndTime())
                    .candidateId(dto.getInterview_candidate())
                    .interviewerIds(Arrays.asList(dto.getInterviewer_tag()))
                    .currentInterviewId(null)
                    .build();

          List<ScheduleConflictDTO> conflicts = scheduleValidationService.validateSchedule(request);
          if (!conflicts.isEmpty()) {
               injectConflictsToErrors(conflicts, errors, fields);
          }

          if (errors.hasErrors()) {
               throw new BindException(errors);
          }
          InterviewBuilder builder = new InterviewBuilder();
          Interview interview = InterviewDirector.constructNewInterview(builder, dto, recruiter, job, candidate);
          Interview savedInterview = interviewRepository.save(interview);
          if (savedInterview == null) {
               String errorMessage = messageSource.getMessage("ME021", null, Locale.getDefault());
               errors.rejectValue(fields.get(11).getName(), "ME021", errorMessage);
               throw new BindException(errors);
          }
          candidate.setStatusId(ConstantUtils.CANDIDATE_WAITING_FOR_INTERVIEW);
          candidateRepository.save(candidate);
          Set<InterviewAssignment> interviewAssignments = generateInterviewAssignments(interviewers, savedInterview);
          iaRepository.saveAll(interviewAssignments);
          interview.setInterviewAssignments(interviewAssignments);
          return resultProcessor.getInterviewDTO(savedInterview);
     }

     @Override
     public EditInterviewDTO getInterviewDisplayableInfo(Integer id) {
          Interview interview = interviewRepository.findById(id).get();
          String status = masterService.findByCategoryAndCategoryId(ConstantUtils.INTERVIEW_STATUS,
                    interview.getStatusInterviewId()).getCategoryValue();
          return EditInterviewDTO.builder()
                    .interview_title(interview.getTitle())
                    .interview_job(interview.getJob().getJobId())
                    .interview_candidate(interview.getCandidate().getCandidateId())
                    .interviewer_tag(
                              interview.getInterviewAssignments().stream()
                                        .map(i -> i.getInterviewer().getId())
                                        .toArray(Integer[]::new))
                    .interview_schedule(interview.getSchedule())
                    .startTime(interview.getStartTime())
                    .endTime(interview.getEndTime())
                    .interview_recruiter(interview.getCreatedBy())
                    .interview_location(interview.getLocation())
                    .note(interview.getInterviewNote())
                    .meetingLink(interview.getMeetingId())
                    .interview_result(interview.getResultInterviewId())
                    .status(status)
                    .build();
     }

     @Override
     public InterviewDTO submitResult(Integer id, EditInterviewDTO submitInterviewDTO,
               Authentication authenticatedUser, BindingResult errors, boolean mandatory) throws BindException {
          Interview interview = interviewRepository.findById(id).orElse(null);
          return resultProcessor.processResult(interview, submitInterviewDTO, errors, mandatory);
     }

     /**
      * Count the number interviews of candidate then updating the status of
      * candidate to "Open"
      * if number of Open interviews is 0
      * 
      * @param oldCandidate: Candidate
      * @return void
      */
     private void updateOldCandidate(Candidate oldCandidate) {
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

     @Override
     public InterviewDTO editInterview(Integer id, EditInterviewDTO editInterviewDTO, Authentication authenticatedUser,
               BindingResult errors) throws BindException {
          Interview interview = interviewRepository.findById(id).orElse(null);
          if (interview == null) {
               String err = messageSource.getMessage("ME008", null, Locale.getDefault());
               errors.rejectValue(ConstantUtils.ERROR, "ME008", err);
               throw new BindException(errors);
          }
          resultProcessor.validateStatus(interview);
          List<Field> createFields = Arrays.asList(CreateInterviewDTO.class.getDeclaredFields());

          Job job = jobService.getJobByIdAndStatusIds(editInterviewDTO.getInterview_job(), errors,
                    createFields.get(1).getName(), List.of(ConstantUtils.JOB_OPEN));

          Candidate oldCandidate = interview.getCandidate();
          Candidate newCandidate = candidateService.getCandidateByIdAndStatusIds(
                    editInterviewDTO.getInterview_candidate(), errors, createFields.get(2).getName(),
                    List.of(ConstantUtils.CANDIDATE_OPEN, ConstantUtils.CANDIDATE_WAITING_FOR_INTERVIEW));
          boolean isCandidateChanged = oldCandidate != null && newCandidate != null && oldCandidate.getCandidateId() != newCandidate.getCandidateId();
          List<User> interviewers = getInterviewersByIds(editInterviewDTO.getInterviewer_tag(), errors, createFields);

          List<User> recruiters = userService.getUserByIdAndRoleIds(List.of(editInterviewDTO.getInterview_recruiter()),
                    errors,
                    createFields.get(7).getName(),
                    List.of(ConstantUtils.RECRUITER_ROLE));
          User recruiter = recruiters.isEmpty() ? null : recruiters.get(0);

          ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                    .schedule(editInterviewDTO.getInterview_schedule())
                    .startTime(editInterviewDTO.getStartTime())
                    .endTime(editInterviewDTO.getEndTime())
                    .oldStartTime(interview.getStartTime())
                    .oldEndTime(interview.getEndTime())
                    .candidateId(editInterviewDTO.getInterview_candidate())
                    .interviewerIds(Arrays.asList(editInterviewDTO.getInterviewer_tag()))
                    .currentInterviewId(id)
                    .build();

          List<ScheduleConflictDTO> conflicts = scheduleValidationService.validateSchedule(scheduleRequest);
          if (!conflicts.isEmpty()) {
               injectConflictsToErrors(conflicts, errors, createFields);
          }

          if (errors.hasErrors()) {
               throw new BindException(errors);
          }
          InterviewBuilder builder = new InterviewBuilder(interview);
          InterviewDirector.constructEditInterview(builder, editInterviewDTO, recruiter, job, newCandidate);
          Interview updatedInterview = interviewRepository.save(interview);
          if (updatedInterview == null) {
               String err = messageSource.getMessage("ME013", null, Locale.getDefault());
               errors.rejectValue(createFields.get(11).getName(), "ME013", err);
               throw new BindException(errors);
          }

          if (isCandidateChanged) {
               updateOldCandidate(oldCandidate);
          }

          newCandidate.setStatusId(ConstantUtils.CANDIDATE_WAITING_FOR_INTERVIEW);
          candidateRepository.save(newCandidate);

          iaRepository.deleteAllByInterviewId(updatedInterview.getInterviewId());
          interview.getInterviewAssignments().clear();
          Set<InterviewAssignment> interviewAssignments = generateInterviewAssignments(interviewers, interview);
          iaRepository.saveAll(interviewAssignments);
          interview.setInterviewAssignments(interviewAssignments);
          return submitResult(id, editInterviewDTO, authenticatedUser, errors, false);
     }

     public InterviewDTO cancelInterview(Integer id, Authentication authenticatedUser) throws Exception {
          Interview interview = interviewRepository.findById(id).orElse(null);
          if (interview == null) {
               String err = messageSource.getMessage("ME008", null, Locale.getDefault());
               throw new AccessDeniedException(err);
          } else if (interview.getStatusInterviewId() != ConstantUtils.INTERVIEW_STATUS_OPEN) {
               String err = messageSource.getMessage("ME022.3", null, Locale.getDefault());
               throw new AccessDeniedException(err);
          }

          interview.setStatusInterviewId(ConstantUtils.INTERVIEW_STATUS_CANCELLED);
          interviewRepository.save(interview);
          Candidate oldCandidate = interview.getCandidate();
          updateOldCandidate(oldCandidate);
          return resultProcessor.getInterviewDTO(interview);
     }

     @Override
     public boolean sendReminderNow(Integer id, Authentication authenticatedUser) throws MessagingException {

          Interview interview = interviewRepository.findById(id).orElse(null);
          if (interview == null) {
               String err = messageSource.getMessage("ME008", null, Locale.getDefault());
               throw new AccessDeniedException(err);
          }
          // send email
          Map<String, Object> props = new HashMap<>();
          String candidateName = interview.getCandidate().getName();
          LocalDate schedule = interview.getSchedule();
          LocalTime startTime = interview.getStartTime();
          LocalTime endTime = interview.getEndTime();
          List<User> interviewers = interview.getInterviewAssignments().stream()
                    .map(i -> i.getInterviewer())
                    .toList();
          for (User interviewer : interviewers) {
               props.put("interviewerName", interviewer.getFullname());
               props.put("email", interviewer.getEmail());
               props.put("candidateName", candidateName);
               props.put("scheduleDate", schedule);
               props.put("startTime", startTime);
               props.put("endTime", endTime);
               props.put("interviewURL", "https://jobnet.click/api/v1/interview/view/" + id);
               props.put("jobTitle", interview.getJob().getTitle());
               props.put("location", interview.getLocation());
               props.put("meetingLink", interview.getMeetingId());
               props.put("daysUntilInterview", null);
               emailService.sendMail(ConstantUtils.INTERVIEW_SCHEDULE_REMINDER, EmailService.DEFAULT_SENDER,
                         interviewer.getEmail(),
                         EmailTemplateName.INTERVIEW_SCHEDULE_REMINDER, props, true);
          }
          interview.setStatusInterviewId(ConstantUtils.INTERVIEW_STATUS_INVITED);
          Interview updatedInterview = interviewRepository.save(interview);
          return updatedInterview != null;
     }
}
