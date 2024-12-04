package com.group1.interview_management.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.entities.Candidate;
import com.group1.interview_management.entities.Interview;
import com.group1.interview_management.entities.InterviewAssignment;
import com.group1.interview_management.entities.Job;
import com.group1.interview_management.repositories.InterviewAssignmentRepository;
import com.group1.interview_management.repositories.InterviewRepository;
import com.group1.interview_management.services.InterviewIntermediaryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterviewIntermediaryServiceImpl implements InterviewIntermediaryService {
     private final InterviewRepository interviewRepository;
     private final InterviewAssignmentRepository iaRepository;
     private final CandidateStatusStrategy candidateStatusStrategy;

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
                    break;
               case "candidate":
                    cancelByCandidate();
                    break;
               default:
                    break;
          }
     }

     private List<Interview> getInterviews() {
          return interviewRepository.findByStatusNotIn(
                    List.of(ConstantUtils.INTERVIEW_STATUS_CANCELLED, ConstantUtils.INTERVIEW_STATUS_CLOSED));
     }

     private void cancelByJob() {
          // TODO Auto-generated method stub
          List<Interview> interviews = getInterviews();

          for (Interview i : interviews) {
               Job job = i.getJob();
               if (job.getStatusJobId() == ConstantUtils.JOB_CLOSE) {
                    i.setStatusInterviewId(ConstantUtils.INTERVIEW_STATUS_CANCELLED);
                    Candidate candidate = i.getCandidate();
                    candidateStatusStrategy.determineStatusForOldCandidate(candidate);
               }
          }
     }

     private void cancelByInterviewer() {
          // TODO Auto-generated method stub
          List<InterviewAssignment> interviewAssignments = iaRepository.findAll();

          for (InterviewAssignment ia : interviewAssignments) {
               if (ia.getInterview().getStatusInterviewId() != ConstantUtils.INTERVIEW_STATUS_CANCELLED
                         && ia.getInterview().getStatusInterviewId() != ConstantUtils.INTERVIEW_STATUS_CLOSED
                         && ia.getInterviewer().getStatus() == ConstantUtils.USER_INACTIVE_ID) {
                    ia.getInterview().setStatusInterviewId(ConstantUtils.INTERVIEW_STATUS_CANCELLED);
                    Candidate candidate = ia.getInterview().getCandidate();
                    candidateStatusStrategy.determineStatusForOldCandidate(candidate);
               }
          }
     }

     private void cancelByCandidate() {
          // TODO Auto-generated method stub
          List<Interview> interviews = getInterviews();

          for (Interview i : interviews) {
               if (i.getCandidate().getStatusId() == ConstantUtils.CANDIDATE_BANNED) {
                    i.setStatusInterviewId(ConstantUtils.INTERVIEW_STATUS_CANCELLED);
                    Candidate candidate = i.getCandidate();
                    candidateStatusStrategy.determineStatusForOldCandidate(candidate);
               }
          }
     }

}
