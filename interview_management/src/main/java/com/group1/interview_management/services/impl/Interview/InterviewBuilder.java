package com.group1.interview_management.services.impl.Interview;

import java.time.LocalDate;

import com.group1.interview_management.dto.interview.CreateInterviewDTO;
import com.group1.interview_management.dto.interview.EditInterviewDTO;

import com.group1.interview_management.common.ConstantUtils;

import com.group1.interview_management.entities.Candidate;
import com.group1.interview_management.entities.Interview;
import com.group1.interview_management.entities.User;
import com.group1.interview_management.entities.Job;

/**
 * Builder Pattern for complex Interview objects
 */
public class InterviewBuilder {
     private Interview interview;

     public InterviewBuilder() {
          interview = new Interview();
     }

     public InterviewBuilder(Interview existingInterview) {
          this.interview = existingInterview;
     }

     public InterviewBuilder withBasicInfo(CreateInterviewDTO dto) {
          interview.setTitle(dto.getInterview_title());
          interview.setSchedule(dto.getInterview_schedule());
          interview.setStartTime(dto.getStartTime());
          interview.setEndTime(dto.getEndTime());
          interview.setLocation(dto.getInterview_location());
          interview.setInterviewNote(dto.getNote());
          interview.setMeetingId(dto.getMeetingLink());
          interview.setOfferdepartment(0);
          interview.setStatusOfferId(0);
          interview.setContractTypeId(0);
          interview.setDueDate(LocalDate.now());
          interview.setContractFrom(LocalDate.now());
          interview.setContractTo(LocalDate.now());
          interview.setSalary(0.0);
          interview.setOfferNote(null);
          return this;
     }

     // need test
     public InterviewBuilder withCreatedRecruiter(User recruiter) {
          interview.setCreatedBy(recruiter.getId());
          return this;
     }

     public InterviewBuilder withEditedRecruiter(User recruiter) {
          interview.updateCreatedBy(recruiter.getId());
          return this;
     }

     public InterviewBuilder withJob(Job job) {
          interview.setJob(job);
          return this;
     }

     public InterviewBuilder withCandidate(Candidate candidate) {
          interview.setCandidate(candidate);
          return this;
     }

     public InterviewBuilder withStatus(Integer statusId) {
          interview.setStatusInterviewId(statusId);
          return this;
     }

     public InterviewBuilder withResult(Integer resultId) {
          interview.setResultInterviewId(resultId);
          return this;
     }

     public InterviewBuilder withDeleteFlag(Boolean deleteFlag) {
          interview.setDeleteFlag(deleteFlag);
          return this;
     }

     public InterviewBuilder fromCreateDTO(CreateInterviewDTO dto, User recruiter, Job job, Candidate candidate) {
          return this.withBasicInfo(dto)
                    .withCreatedRecruiter(recruiter)
                    .withJob(job)
                    .withCandidate(candidate)
                    .withStatus(ConstantUtils.INTERVIEW_STATUS_OPEN)
                    .withResult(ConstantUtils.INTERVIEW_RESULT_NA)
                    .withDeleteFlag(false);
     }

     public InterviewBuilder fromEditDTO(EditInterviewDTO dto, User recruiter, Job job, Candidate candidate) {
          return this.withBasicInfo(dto)
                    .withEditedRecruiter(recruiter)
                    .withJob(job)
                    .withCandidate(candidate)
                    .withResult(dto.getInterview_result());
     }

     public Interview build() {
          return interview;
     }
}
