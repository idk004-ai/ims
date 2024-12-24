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
import com.group1.interview_management.repositories.MasterRepository;
import com.group1.interview_management.dto.interview.CreateInterviewDTO;
import com.group1.interview_management.dto.interview.EditInterviewDTO;
import com.group1.interview_management.dto.interview.InterviewContext;
import com.group1.interview_management.entities.Candidate;
import com.group1.interview_management.entities.InterviewAssignment;
import com.group1.interview_management.entities.User;
import com.group1.interview_management.repositories.CandidateRepository;
import com.group1.interview_management.repositories.InterviewAssignmentRepository;
import com.group1.interview_management.repositories.InterviewRepository;
import com.group1.interview_management.services.InterviewQueryManager;
import com.group1.interview_management.services.InterviewResultManager;
import com.group1.interview_management.services.InterviewResultProcess;
import com.group1.interview_management.services.InterviewScheduleManager;
import com.group1.interview_management.services.InterviewService;
import com.group1.interview_management.services.MasterService;
import com.group1.interview_management.services.impl.Interview.InterviewDirector;
import com.group1.interview_management.services.impl.Interview.InterviewValidationService;
import com.group1.interview_management.services.impl.Interview.InterviewBuilder;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InterviewServiceImpl implements InterviewService, InterviewQueryManager, InterviewResultManager, InterviewScheduleManager {

     private final InterviewRepository interviewRepository;
     private final MasterRepository masterRepository;
     private final MasterService masterService;
     private final MessageSource messageSource;
     private final CandidateRepository candidateRepository;
     private final InterviewAssignmentRepository iaRepository;
     private final EmailService emailService;
     private final InterviewResultProcess resultProcessor;
     private final CandidateStatusStrategy candidateStatusStrategy;
     private final InterviewValidationService interviewValidationService;

     @Override
     public List<Interview> getInterviewsByDateRange(LocalDate startDate, LocalDate endDate) {
          return interviewRepository.findUpcomingInterviewsInDateRange(startDate, endDate,
                    List.of(ConstantUtils.INTERVIEW_STATUS_CLOSED, ConstantUtils.INTERVIEW_STATUS_CANCELLED));
     }

     @Override
     public Page<InterviewDTO> getAllInterview(InterviewFilterDTO filter, User auth) {

          Pageable pageable = PageRequest.of(filter.getPage(), filter.getPageSize(),
                    Sort.by("modifiedDate").descending());
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
               if ((interview.getContractTypeId() == 0 || interview.getSalary() == 0
                         || interview.getOfferdepartment() == 0) && interview.getResultInterviewId() == ConstantUtils.INTERVIEW_RESULT_PASSED) {
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
                    .orElse(null);
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
     public InterviewDTO createSchedule(CreateInterviewDTO dto, User authenticatedUser, BindingResult errors)
               throws Exception {

          List<Field> fields = Arrays.asList(CreateInterviewDTO.class.getDeclaredFields());
          InterviewContext context = interviewValidationService.validateNewInterview(dto, errors);
          if (errors.hasErrors()) {
               throw new BindException(errors);
          }
          InterviewBuilder builder = new InterviewBuilder();
          Interview interview = InterviewDirector.constructNewInterview(builder, dto, context);
          Interview savedInterview = interviewRepository.save(interview);
          if (savedInterview == null) {
               String errorMessage = messageSource.getMessage("ME021", null, Locale.getDefault());
               errors.rejectValue(fields.get(11).getName(), "ME021", errorMessage);
               throw new BindException(errors);
          }
          Candidate candidate = context.getCandidate();
          candidate.setStatusId(ConstantUtils.CANDIDATE_WAITING_FOR_INTERVIEW);
          candidateRepository.save(candidate);
          Set<InterviewAssignment> interviewAssignments = generateInterviewAssignments(context.getInterviewers(), savedInterview);
          iaRepository.saveAll(interviewAssignments);
          interview.setInterviewAssignments(interviewAssignments);
          return resultProcessor.getInterviewDTO(savedInterview);
     }

     @Override
     public EditInterviewDTO getInterviewDetails(Integer id) {
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
     public InterviewDTO submitInterviewResult(Integer id, EditInterviewDTO submitInterviewDTO,
               User authenticatedUser, BindingResult errors, boolean mandatory) throws BindException {
          Interview interview = interviewRepository.findById(id).orElse(null);
          Set<InterviewAssignment> interviewAssignments = interview.getInterviewAssignments();
          Integer[] interviewerIds = interviewAssignments.stream()
                    .map(i -> i.getInterviewer().getId())
                    .toArray(Integer[]::new);
          if (mandatory && Arrays.stream(interviewerIds).noneMatch(interviewerId -> interviewerId == authenticatedUser.getId())) {
               throw new AccessDeniedException(messageSource.getMessage("ME002.1", null, Locale.getDefault()));
          }
          return resultProcessor.processResult(interview, submitInterviewDTO, errors, mandatory);
     }

     @Override
     public InterviewDTO editSchedule(Integer id, EditInterviewDTO editInterviewDTO, User authenticatedUser,
               BindingResult errors) throws BindException {
          Interview interview = interviewRepository.findById(id).orElse(null);
          if (interview == null) {
               String err = messageSource.getMessage("ME008", null, Locale.getDefault());
               errors.rejectValue(ConstantUtils.ERROR, "ME008", err);
               throw new BindException(errors);
          }
          resultProcessor.validateStatus(interview);
          List<Field> createFields = Arrays.asList(CreateInterviewDTO.class.getDeclaredFields());
          
          InterviewContext context = interviewValidationService.validateEditInterview(editInterviewDTO, errors, id, interview.getStartTime(), interview.getEndTime());
          if (errors.hasErrors()) {
               throw new BindException(errors);
          }
          InterviewBuilder builder = new InterviewBuilder(interview);
          InterviewDirector.constructEditInterview(builder, editInterviewDTO, context);
          Interview updatedInterview = interviewRepository.save(interview);
          if (updatedInterview == null) {
               String err = messageSource.getMessage("ME013", null, Locale.getDefault());
               errors.rejectValue(createFields.get(11).getName(), "ME013", err);
               throw new BindException(errors);
          }

          Candidate newCandidate = context.getCandidate();
          Candidate oldCandidate = interview.getCandidate();
          boolean isCandidateChanged = oldCandidate != null && newCandidate != null
                    && oldCandidate.getCandidateId() != newCandidate.getCandidateId();
          if (isCandidateChanged) {
               candidateStatusStrategy.determineStatusForOldCandidate(oldCandidate);
          }

          newCandidate.setStatusId(ConstantUtils.CANDIDATE_WAITING_FOR_INTERVIEW);
          candidateRepository.save(newCandidate);

          iaRepository.deleteAllByInterviewId(updatedInterview.getInterviewId());
          interview.getInterviewAssignments().clear();
          Set<InterviewAssignment> interviewAssignments = generateInterviewAssignments(context.getInterviewers(), interview);
          iaRepository.saveAll(interviewAssignments);
          interview.setInterviewAssignments(interviewAssignments);
          return submitInterviewResult(id, editInterviewDTO, authenticatedUser, errors, false);
     }

     @Override
     public InterviewDTO cancelSchedule(Integer id, User authenticatedUser) throws Exception {
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
          candidateStatusStrategy.determineStatusForOldCandidate(oldCandidate);
          return resultProcessor.getInterviewDTO(interview);
     }

     @Override
     public boolean sendReminderNow(Integer id, User authenticatedUser) throws MessagingException {
          Interview interview = interviewRepository.findById(id).orElse(null);
          if (interview == null) {
               String err = messageSource.getMessage("ME008", null, Locale.getDefault());
               throw new AccessDeniedException(err);
          }
          
          try {
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
                    props.put("interviewURL", "http://localhost:9090/api/v1/interview/view/" + id);
                    props.put("jobTitle", interview.getJob().getTitle());
                    props.put("location", interview.getLocation());
                    props.put("meetingLink", interview.getMeetingId());
                    props.put("daysUntilInterview", null);
                    
                    emailService.sendMail(ConstantUtils.INTERVIEW_SCHEDULE_REMINDER, 
                         EmailService.DEFAULT_SENDER,
                         interviewer.getEmail(),
                         EmailTemplateName.INTERVIEW_SCHEDULE_REMINDER, 
                         props, 
                         true);
               }
               
               interview.setStatusInterviewId(ConstantUtils.INTERVIEW_STATUS_INVITED);
               Interview updatedInterview = interviewRepository.save(interview);
               return updatedInterview != null;
          
          } catch (Exception e) {
               log.error("Failed to send reminder email: {}", e.getMessage());
               return false;
          }
     }
}
