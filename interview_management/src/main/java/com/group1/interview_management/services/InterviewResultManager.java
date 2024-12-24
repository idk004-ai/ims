package com.group1.interview_management.services;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import com.group1.interview_management.dto.interview.EditInterviewDTO;
import com.group1.interview_management.dto.interview.InterviewDTO;
import com.group1.interview_management.entities.User;

public interface InterviewResultManager {
     InterviewDTO submitInterviewResult(Integer id, EditInterviewDTO dto, User auth, BindingResult errors,
               boolean mandatory) throws BindException;

}
