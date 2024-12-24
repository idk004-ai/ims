package com.group1.interview_management.services;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import com.group1.interview_management.dto.interview.CreateInterviewDTO;
import com.group1.interview_management.dto.interview.EditInterviewDTO;
import com.group1.interview_management.dto.interview.InterviewDTO;
import com.group1.interview_management.entities.User;

import jakarta.mail.MessagingException;

public interface InterviewScheduleManager {
     InterviewDTO createSchedule(CreateInterviewDTO dto, User auth, BindingResult errors) throws Exception;

     InterviewDTO editSchedule(Integer id, EditInterviewDTO dto, User auth, BindingResult errors)
               throws BindException;

     InterviewDTO cancelSchedule(Integer id, User auth) throws Exception;

     boolean sendReminderNow(Integer id, User auth) throws MessagingException;
}
