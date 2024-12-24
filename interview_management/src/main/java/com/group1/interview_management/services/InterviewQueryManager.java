package com.group1.interview_management.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;

import com.group1.interview_management.dto.interview.EditInterviewDTO;
import com.group1.interview_management.dto.interview.InterviewDTO;
import com.group1.interview_management.dto.interview.InterviewFilterDTO;
import com.group1.interview_management.entities.Interview;
import com.group1.interview_management.entities.User;

public interface InterviewQueryManager {

     Page<InterviewDTO> getAllInterview(InterviewFilterDTO status, User authenticatedUser);

     List<Interview> getInterviewsByDateRange(LocalDate startDate, LocalDate endDate);

     EditInterviewDTO getInterviewDetails(Integer id);
}
