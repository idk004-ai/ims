package com.group1.interview_management.controller;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.common.StatusValidator;
import com.group1.interview_management.dto.UserDTO;
import com.group1.interview_management.dto.JobDTO.response.JobResponse;
import com.group1.interview_management.dto.candidate.CandidateDetailDTO;
import com.group1.interview_management.dto.interview.EditInterviewDTO;
import com.group1.interview_management.services.CandidateService;
import com.group1.interview_management.services.JobService;
import com.group1.interview_management.dto.interview.CreateInterviewDTO;
import com.group1.interview_management.dto.interview.InterviewDTO;
import com.group1.interview_management.dto.interview.InterviewFilterDTO;
import com.group1.interview_management.entities.Master;
import com.group1.interview_management.entities.User;
import com.group1.interview_management.services.InterviewService;
import com.group1.interview_management.services.MasterService;
import com.group1.interview_management.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.context.MessageSource;

@Controller
@RequestMapping("/interview")
@RequiredArgsConstructor
@Slf4j
public class InterviewController {
     private final InterviewService interviewService;
     private final UserService userService;
     private final MasterService masterService;
     private final CandidateService candidateService;
     private final JobService jobService;
     private final MessageSource messageSource;

     @GetMapping
     public String listInterviews(Model model) {
          return "interview/interview-list";
     }

     @PostMapping
     @ResponseBody
     public ResponseEntity<Page<InterviewDTO>> getInterview(
               @RequestBody(required = false) InterviewFilterDTO filter,
               Authentication authenticatedUser) {
          User user = (User) authenticatedUser.getPrincipal();
          if (user == null) {
               String err = messageSource.getMessage("ME002.1", null, Locale.getDefault());
               throw new AccessDeniedException(err);
          }
          return ResponseEntity.ok(interviewService.getAllInterview(filter, authenticatedUser));
     }

     // not Interviewer role
     @PostMapping("/create")
     @ResponseBody
     public ResponseEntity<?> createInterview(@Valid @RequestBody CreateInterviewDTO interviewDto, BindingResult errors,
               Authentication authenticatedUser) throws BindException, Exception {
          User user = (User) authenticatedUser.getPrincipal();
          if (user == null || user.getRoleId() == ConstantUtils.INTERVIEWER_ROLE) {
               String err = messageSource.getMessage("ME002.1", null, Locale.getDefault());
               throw new AccessDeniedException(err);
          }
          InterviewDTO createdInterviewDto = interviewService.createInterview(interviewDto, authenticatedUser, errors);
          return ResponseEntity.status(HttpStatus.CREATED).body(createdInterviewDto);
     }

     @GetMapping("/create")
     public String createInterview(Model model) {
          model.addAttribute("job_list", jobService.getAllInterviewJobs());
          model.addAttribute("candidate_list", candidateService.getAllCandidates());
          model.addAttribute("recruiter_list", userService.getAllUsersByRole(ConstantUtils.RECRUITER_ROLE));
          model.addAttribute("interviewers", userService.getAllUsersByRole(ConstantUtils.INTERVIEWER_ROLE));
          return "interview/create-interview";
     }

     private List<UserDTO> convertInterviewerIdsToUserDTOs(Integer[] interviewerIds) {
          List<UserDTO> interviewers = userService.getAllUsersByRole(ConstantUtils.INTERVIEWER_ROLE);
          List<Integer> interviewerIdList = Arrays.asList(interviewerIds);
          return interviewers.stream()
                    .filter(interviewer -> interviewerIdList.contains(interviewer.getId()))
                    .collect(Collectors.toList());
     }

     private void renderFormData(EditInterviewDTO interview, Model model) {
          JobResponse job = jobService.getJobByJobId(interview.getInterview_job());
          CandidateDetailDTO candidate = candidateService.getCandidateById(interview.getInterview_candidate());
          List<UserDTO> interviewersAssigned = convertInterviewerIdsToUserDTOs(interview.getInterviewer_tag());
          String listOfInterviewers = interviewersAssigned.stream().map(UserDTO::getFullname)
                    .collect(Collectors.joining(", "));
          UserDTO recruiter = userService.getUserById(interview.getInterview_recruiter());
          String result = null;
          if (interview.getInterview_result() > 0) {
               result = masterService
                         .findByCategoryAndCategoryId(ConstantUtils.INTERVIEW_RESULT, interview.getInterview_result())
                         .getCategoryValue();
          }
          model.addAttribute("interview", interview);
          model.addAttribute("job", job.getTitle());
          model.addAttribute("candidate", candidate.getFullName());
          model.addAttribute("interviewers", listOfInterviewers);
          model.addAttribute("recruiter", recruiter.getFullname());
          model.addAttribute("result", result);
     }

     @GetMapping("/view/{interviewId}")
     public String getDetailPage(@PathVariable Integer interviewId, Model model) {
          EditInterviewDTO interview = interviewService.getInterviewDisplayableInfo(interviewId);
          renderFormData(interview, model);
          return "interview/interview-information";
     }

     @GetMapping("/submit/{interviewId}")
     public String getSubmitResultPage(@PathVariable Integer interviewId, Model model) {
          EditInterviewDTO interview = interviewService.getInterviewDisplayableInfo(interviewId);
          int statusId = masterService.findByCategoryAndValue(ConstantUtils.INTERVIEW_STATUS, interview.getStatus())
                    .get().getCategoryId();
          boolean isInvalidStatus = StatusValidator.isInvalidStatus(statusId,
                    List.of(ConstantUtils.INTERVIEW_STATUS_CANCELLED, ConstantUtils.INTERVIEW_STATUS_CLOSED));
          if (isInvalidStatus) {
               return "redirect:/interview";
          }
          List<Master> masterResults = masterService.findByCategory(ConstantUtils.INTERVIEW_RESULT);
          renderFormData(interview, model);
          model.addAttribute("results", masterResults);
          return "interview/interview-submit";
     }

     @PostMapping("/submit/{interviewId}")
     @ResponseBody
     public ResponseEntity<?> submitResult(
               @PathVariable Integer interviewId,
               @RequestBody EditInterviewDTO submitInterviewDTO,
               Authentication authenticatedUser,
               BindingResult errors) throws BindException {
          User user = (User) authenticatedUser.getPrincipal();
          if (user == null) {
               String err = messageSource.getMessage("ME002.1", null, Locale.getDefault());
               throw new AccessDeniedException(err);
          }
          return ResponseEntity.ok()
                    .body(interviewService.submitResult(interviewId, submitInterviewDTO, authenticatedUser, errors,
                              true));
     }

     @GetMapping("/edit/{interviewId}")
     public String getEditForm(@PathVariable Integer interviewId, Model model) {
          EditInterviewDTO interview = interviewService.getInterviewDisplayableInfo(interviewId);
          int statusId = masterService.findByCategoryAndValue(ConstantUtils.INTERVIEW_STATUS, interview.getStatus())
                    .get().getCategoryId();
          boolean isInvalidStatus = StatusValidator.isInvalidStatus(statusId,
                    List.of(ConstantUtils.INTERVIEW_STATUS_CANCELLED, ConstantUtils.INTERVIEW_STATUS_CLOSED));
          if (isInvalidStatus) {
               return "redirect:/interview";
          }
          model.addAttribute("job_list", jobService.getAllInterviewJobs());
          model.addAttribute("candidate_list", candidateService.getAllCandidates());
          model.addAttribute("recruiter_list", userService.getAllUsersByRole(ConstantUtils.RECRUITER_ROLE));
          model.addAttribute("interview", interview);
          model.addAttribute("results", masterService.findByCategory(ConstantUtils.INTERVIEW_RESULT));
          model.addAttribute("interviewers", userService.getAllUsersByRole(ConstantUtils.INTERVIEWER_ROLE));
          return "interview/interview-edit";
     }

     @PostMapping("/edit/{interviewId}")
     @ResponseBody
     public ResponseEntity<?> editInterview(
               @PathVariable Integer interviewId,
               @Valid @RequestBody EditInterviewDTO interviewDTO,
               Authentication authenticatedUser,
               BindingResult errors) throws BindException, AccessDeniedException {
          User user = (User) authenticatedUser.getPrincipal();
          if (user == null || user.getRoleId() == ConstantUtils.INTERVIEWER_ROLE) {
               String err = messageSource.getMessage("ME002.1", null, Locale.getDefault());
               throw new AccessDeniedException(err);
          }
          return ResponseEntity.ok()
                    .body(interviewService.editInterview(interviewId, interviewDTO, authenticatedUser, errors));
     }

     @PostMapping("/cancel/{interviewId}")
     @ResponseBody
     public ResponseEntity<?> cancelInterview(@PathVariable Integer interviewId, Authentication authenticatedUser)
               throws BindException {
          User user = (User) authenticatedUser.getPrincipal();
          if (user == null || user.getRoleId() == ConstantUtils.INTERVIEWER_ROLE) {
               String err = messageSource.getMessage("ME002.1", null, Locale.getDefault());
               throw new AccessDeniedException(err);
          }
          try {
               return ResponseEntity.ok().body(interviewService.cancelInterview(interviewId, authenticatedUser));
          } catch (Exception e) {
               return ResponseEntity.badRequest().body(e.getMessage());
          }
     }

     @PostMapping("/send-reminder-now/{interviewId}")
     @ResponseBody
     public ResponseEntity<?> postMethodName(@PathVariable Integer interviewId, Authentication authentication) {
          User user = (User) authentication.getPrincipal();
          if (user == null || user.getRoleId() == ConstantUtils.INTERVIEWER_ROLE) {
               String err = messageSource.getMessage("ME002.1", null, Locale.getDefault());
               throw new AccessDeniedException(err);
          }
          try {
               return ResponseEntity.ok().body(interviewService.sendReminderNow(interviewId, authentication));
          } catch (Exception e) {
               log.error(e.getMessage());
               return ResponseEntity.badRequest().body(e.getMessage());
          }
     }

     @GetMapping("/me")
     @ResponseBody
     public ResponseEntity<UserDTO> assignMe() {
          Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
          User userPrincipal = new User();
          if (authentication != null && authentication.isAuthenticated()
                    && !(authentication instanceof AnonymousAuthenticationToken)) {
               userPrincipal = (User) authentication.getPrincipal();
          }
          if (userPrincipal.getRoleId() == ConstantUtils.RECRUITER_ROLE) {
               UserDTO userDto = UserDTO.builder()
                         .id(userPrincipal.getId())
                         .fullname(userPrincipal.getFullname())
                         .username(userPrincipal.getUsername())
                         .phoneNo(userPrincipal.getPhone())
                         .build();
               return ResponseEntity.ok().body(userDto);
          } else {
               return ResponseEntity.badRequest().build();
          }
     }

}
