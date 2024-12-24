package com.group1.interview_management.services;

import java.util.List;

import com.group1.interview_management.dto.OfferCreateDTO;

public interface InterviewService {

     public List<OfferCreateDTO> getinterviewnulloffer();

     OfferCreateDTO getinterviewByID(Integer id);
}
