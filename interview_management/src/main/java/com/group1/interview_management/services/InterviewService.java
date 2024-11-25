package com.group1.interview_management.services;

import java.time.LocalDate;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import com.group1.interview_management.dto.OfferCreateDTO;
import org.springframework.data.domain.Page;

import com.group1.interview_management.dto.interview.InterviewDTO;
import com.group1.interview_management.dto.interview.InterviewFilterDTO;
import com.group1.interview_management.entities.Interview;

import jakarta.mail.MessagingException;

import com.group1.interview_management.dto.interview.CreateInterviewDTO;
import com.group1.interview_management.dto.interview.EditInterviewDTO;

public interface InterviewService {
     // PageDTO<InterviewDTO> getAllInterview(InterviewFilterDTO status, Authentication authenticatedUser);

     // public List<OfferCreateDTO> getinterview();
     public List<OfferCreateDTO> getinterviewnulloffer();

     OfferCreateDTO getinterviewByID(Integer id);
     Page<InterviewDTO> getAllInterview(InterviewFilterDTO status, Authentication authenticatedUser);

     List<Interview> getAllInterview(LocalDate startDate, LocalDate endDate);

     InterviewDTO createInterview(CreateInterviewDTO interviewDTO, Authentication authenticatedUser, BindingResult errors) throws BindException, Exception;

     EditInterviewDTO getInterviewDisplayableInfo(Integer id);

     InterviewDTO submitResult(Integer id, EditInterviewDTO submitInterviewDTO, Authentication authenticatedUser, BindingResult errors, boolean mandatory) throws BindException;

     InterviewDTO editInterview(Integer id, EditInterviewDTO editInterviewDTO, Authentication authenticatedUser, BindingResult errors) throws BindException;

     InterviewDTO cancelInterview(Integer id, Authentication authenticatedUser) throws Exception;

     boolean sendReminderNow(Integer id, Authentication authenticatedUser) throws MessagingException;
}
