package com.group1.interview_management.services;

import com.group1.interview_management.dto.candidate.CandidateCreateDTO;
import com.group1.interview_management.dto.candidate.CandidateDTO;
import com.group1.interview_management.dto.candidate.CandidateDetailDTO;
import com.group1.interview_management.dto.candidate.CandidateSearchDTO;
import com.group1.interview_management.entities.Candidate;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;

public interface CandidateService {

    /**
     * Search candidates by name
     *
     * @param request search request of user
     * @return List of candidates
     */
    Page<CandidateDTO> searchCandidates(CandidateSearchDTO request);

    /**
     * Save candidate
     *
     * @param candidateDTO save data
     */
    void saveCandidate(CandidateCreateDTO candidateDTO);

    /**
     * Check if email exists
     *
     * @param email the email need to check
     * @return true if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Get candidate by id
     *
     * @param id id of candidate
     * @return CandidateDTO
     */
    CandidateDetailDTO getCandidateById(Integer id);

    /**
     * Get candidate create DTO by id
     *
     * @param id id of candidate
     * @return CandidateCreateDTO
     */
    CandidateCreateDTO getCandidateCreateDTOById(Integer id);

    /**
     * Delete candidate(soft delete)
     *
     * @param id id of candidate
     */
    void deleteCandidate(Integer id);

    /**
     * Check candidate exist by id
     *
     * @param id id of candidate need to check
     * @return true if candidate exist
     */
    boolean existsById(Integer id);

    /**
     * Ban candidate
     *
     * @param id id of candidate
     */
    void banCandidate(Integer id);

    /**
     * Unban candidate
     *
     * @param id - id of candidate
     */
    void unbanCandidate(Integer id);

    /**
     * Get all candidates
     *
     * @return List of candidates
     * 
     * Author: KhoiLNM
     * Date: 2022-11-11
     */
    List<CandidateDTO> getAllInterviewScheduleCandidates();

    /**
     * Get candidate by id and list of status
     * @param candidateId
     * @param errors
     * @param fields
     * @param status
     * @return
     * 
     * Author: KhoiLNM
     * Date: 2022-11-11
     */
    Candidate getCandidateByIdAndStatusIds(Integer candidateId, BindingResult errors, String field, List<Integer> status);

    boolean existsByPhoneNumber(String phoneNumber);
}
