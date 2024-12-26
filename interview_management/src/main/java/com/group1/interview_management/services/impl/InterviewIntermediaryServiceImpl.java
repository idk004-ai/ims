package com.group1.interview_management.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.entities.Candidate;
import com.group1.interview_management.entities.Interview;
import com.group1.interview_management.entities.InterviewAssignment;
import com.group1.interview_management.entities.Job;
import com.group1.interview_management.entities.User;
import com.group1.interview_management.repositories.InterviewAssignmentRepository;
import com.group1.interview_management.repositories.InterviewRepository;
import com.group1.interview_management.repositories.UserRepository;
import com.group1.interview_management.services.InterviewIntermediaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewIntermediaryServiceImpl implements InterviewIntermediaryService {
     private final InterviewRepository interviewRepository;
     private final InterviewAssignmentRepository iaRepository;
     private final CandidateStatusStrategy candidateStatusStrategy;
     private final UserRepository userRepository;

     @Override
     public <T> void cancelInterviews(Class<T> clazz) {
          if (clazz == null) {
               return;
          }

          String className = clazz.getSimpleName().toLowerCase();

          switch (className) {
               case "job":
                    cancelByJob();
                    break;
               case "user":
                    cancelByInterviewer();
                    cancelByRecruiter();
                    break;
               case "candidate":
                    cancelByCandidate();
                    break;
               default:
                    break;
          }
     }

     private void cancelByRecruiter() {
          List<Interview> interviews = getInterviews();

          for (Interview i : interviews) {
               Integer recruiterId = i.getCreatedBy();
               User recruiter = userRepository.findById(recruiterId).orElse(null);
               if (recruiter != null && recruiter.getStatus() == ConstantUtils.USER_INACTIVE_ID) {
                    i.setStatusInterviewId(ConstantUtils.INTERVIEW_STATUS_CANCELLED);
                    interviewRepository.save(i);
                    Candidate candidate = i.getCandidate();
                    candidateStatusStrategy.determineStatusForOldCandidate(candidate);
               }
          }
     }

     private List<Interview> getInterviews() {
          return interviewRepository.findByStatusNotIn(
                    List.of(ConstantUtils.INTERVIEW_STATUS_CANCELLED, ConstantUtils.INTERVIEW_STATUS_CLOSED));
     }

     private void cancelByJob() {
          List<Interview> interviews = getInterviews();

          for (Interview i : interviews) {
               Job job = i.getJob();
               if (job.getStatusJobId() == ConstantUtils.JOB_CLOSE || job.getDeleteFlag()) {
                    i.setStatusInterviewId(ConstantUtils.INTERVIEW_STATUS_CANCELLED);
                    interviewRepository.save(i);
                    Candidate candidate = i.getCandidate();
                    candidateStatusStrategy.determineStatusForOldCandidate(candidate);
               }
          }
     }

     private void cancelByInterviewer() {
          List<InterviewAssignment> interviewAssignments = iaRepository.findAll();
          Map<Interview, List<InterviewAssignment>> interviewAssignmentMap = new HashMap<>();

          // Group assignments by interview
          for (InterviewAssignment ia : interviewAssignments) {
               Interview interview = ia.getInterview();
               if (interview.getStatusInterviewId() != ConstantUtils.INTERVIEW_STATUS_CANCELLED
                         && interview.getStatusInterviewId() != ConstantUtils.INTERVIEW_STATUS_CLOSED) {
                    interviewAssignmentMap.computeIfAbsent(interview, k -> new ArrayList<>()).add(ia);
               }
          }

          // Process each interview
          for (Map.Entry<Interview, List<InterviewAssignment>> entry : interviewAssignmentMap.entrySet()) {
               Interview interview = entry.getKey();
               List<InterviewAssignment> assignments = entry.getValue();

               // Count active and inactive interviewers
               long inactiveCount = assignments.stream()
                         .filter(ia -> ia.getInterviewer().getStatus() == ConstantUtils.USER_INACTIVE_ID)
                         .count();

               if (inactiveCount == assignments.size()) {
                    // All interviewers are inactive, cancel the interview
                    interview.setStatusInterviewId(ConstantUtils.INTERVIEW_STATUS_CANCELLED);
                    interviewRepository.save(interview);
                    Candidate candidate = interview.getCandidate();
                    candidateStatusStrategy.determineStatusForOldCandidate(candidate);
               } else if (inactiveCount > 0) {
                    // Remove only the inactive interviewers
                    assignments.stream()
                              .filter(ia -> ia.getInterviewer().getStatus() == ConstantUtils.USER_INACTIVE_ID)
                              .forEach(ia -> iaRepository.delete(ia));
               }
          }
     }

     private void cancelByCandidate() {
          List<Interview> interviews = getInterviews();

          for (Interview i : interviews) {
               if (i.getCandidate().getStatusId() == ConstantUtils.CANDIDATE_BANNED) {
                    i.setStatusInterviewId(ConstantUtils.INTERVIEW_STATUS_CANCELLED);
                    interviewRepository.save(i);
                    Candidate candidate = i.getCandidate();
                    candidateStatusStrategy.determineStatusForOldCandidate(candidate);
               }
          }
     }

}
