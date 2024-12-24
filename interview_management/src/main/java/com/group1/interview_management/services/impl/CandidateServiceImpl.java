package com.group1.interview_management.services.impl;

import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.dto.candidate.CandidateCreateDTO;
import com.group1.interview_management.dto.candidate.CandidateDTO;
import com.group1.interview_management.dto.candidate.CandidateDetailDTO;
import com.group1.interview_management.dto.candidate.CandidateSearchDTO;
import com.group1.interview_management.repositories.CandidateRepository;
import com.group1.interview_management.services.CandidateService;
import com.group1.interview_management.services.InterviewIntermediaryService;
import com.group1.interview_management.entities.Candidate;
import com.group1.interview_management.entities.Interview;
import com.group1.interview_management.services.MasterService;
import com.group1.interview_management.services.UserService;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;

import java.util.Arrays;

@Service
public class CandidateServiceImpl implements CandidateService {
    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MasterService masterService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private InterviewIntermediaryService interviewService;

    @Override
    public Page<CandidateDTO> searchCandidates(CandidateSearchDTO request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());
        return candidateRepository.searchByKeyword(request.getQuery(), request.getStatus(), pageable,
                ConstantUtils.CANDIDATE_STATUS, ConstantUtils.POSITION);
    }

    @Override
    public void saveCandidate(CandidateCreateDTO candidateDTO) {
        Candidate candidate;

        // If id not null,it's update
        // If id null, it's create
        if (candidateDTO.getId() != null) {
            candidate = candidateRepository.findById(candidateDTO.getId()).orElse(null);
        } else {
            candidate = new Candidate();
        }

        // Check nullable of candidate
        if (candidate != null) {
            // Set personal information
            candidate.setName(candidateDTO.getFullName());
            candidate.setEmail(candidateDTO.getEmail());
            candidate.setGenderId(candidateDTO.getGender());
            candidate.setPhoneNumber(candidateDTO.getPhoneNumber());
            candidate.setAddress(candidateDTO.getAddress());
            candidate.setDob(candidateDTO.getDob());

            // Set professional information
            candidate.setPositionId(candidateDTO.getPosition());
            candidate.setSkill(listToString(candidateDTO.getSkills()));
            candidate.setStatusId(candidateDTO.getStatus());
            candidate.setHighestLevelId(candidateDTO.getHighestLevel());
            candidate.setRecruiter(userService.getUserByIdNone(candidateDTO.getRecruiter()));
            candidate.setCv(candidateDTO.getCvLink());
            candidate.setCvFileName(candidateDTO.getCvFileName());
            candidate.setYoe(candidateDTO.getYoe());
            candidate.setNote(candidateDTO.getNote());

            // Save candidate
            candidateRepository.save(candidate);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        return candidateRepository.findByEmail(email) != null;
    }

    @Override
    public CandidateDetailDTO getCandidateById(Integer id) {

        // Get candidate detail
        CandidateDetailDTO candidateDetailDTO = candidateRepository.findCandidateDetailById(
                id, ConstantUtils.GENDER, ConstantUtils.POSITION,
                ConstantUtils.CANDIDATE_STATUS, ConstantUtils.HIGHEST_LEVEL);

        // Get skills
        candidateDetailDTO
                .setSkills(masterService.getListCategoryValue(candidateDetailDTO.getSkills(), ConstantUtils.SKILLS));

        // Get recruiter username
        String recruiter = candidateDetailDTO.getOwnerHR();
        recruiter = recruiter.split("@")[0] + ")";

        // Get modified by username
        String modifiedBy = candidateDetailDTO.getModifiedBy();
        modifiedBy = modifiedBy.split("@")[0];

        // Set recruiter and modified by
        candidateDetailDTO.setModifiedBy(modifiedBy);
        candidateDetailDTO.setOwnerHR(recruiter);
        return candidateDetailDTO;
    }

    @Override
    public CandidateCreateDTO getCandidateCreateDTOById(Integer id) {
        Candidate candidate = candidateRepository.findById(id).orElse(null);
        if (candidate == null) {
            return null;
        }

        // Convert candidate to candidateCreateDTO
        CandidateCreateDTO candidateCreateDTO = new CandidateCreateDTO();
        candidateCreateDTO.setId(candidate.getCandidateId());
        candidateCreateDTO.setFullName(candidate.getName());
        candidateCreateDTO.setEmail(candidate.getEmail());
        candidateCreateDTO.setPhoneNumber(candidate.getPhoneNumber());
        candidateCreateDTO.setAddress(candidate.getAddress());
        candidateCreateDTO.setDob(candidate.getDob());
        candidateCreateDTO.setGender(candidate.getGenderId());
        candidateCreateDTO.setPosition(candidate.getPositionId());
        candidateCreateDTO.setCvLink(candidate.getCv());
        candidateCreateDTO.setCvFileName(candidate.getCvFileName());
        candidateCreateDTO.setStatus(candidate.getStatusId());
        candidateCreateDTO.setRecruiter(candidate.getRecruiter().getId());
        candidateCreateDTO.setHighestLevel(candidate.getHighestLevelId());
        candidateCreateDTO.setSkills(Arrays.asList(candidate.getSkill().split(",")));
        candidateCreateDTO.setYoe(candidate.getYoe());
        candidateCreateDTO.setNote(candidate.getNote());
        return candidateCreateDTO;
    }

    private void cancelInterview(Candidate candidate) {
        List<Interview> interviews = candidate.getInterviews();
        for (Interview interview : interviews) {
            interview.setStatusInterviewId(ConstantUtils.INTERVIEW_STATUS_CANCELLED);
        }
    }

    @Override
    public void deleteCandidate(Integer id) {
        Candidate candidate = candidateRepository.findById(id).orElse(null);
        if (candidate != null) {
            if (!candidate.getDeleteFlag()) {
                cancelInterview(candidate);
                candidate.setDeleteFlag(true);
                candidateRepository.save(candidate);
            }
        }
    }

    @Override
    public boolean existsById(Integer id) {
        return candidateRepository.existsByIdAndNotDeleted(id);
    }

    @Override
    public void banCandidate(Integer id) {
        Candidate candidate = candidateRepository.findById(id).orElse(null);
        if (candidate != null && !candidate.getDeleteFlag()) {
            cancelInterview(candidate);
            candidate.setStatusId(ConstantUtils.CANDIDATE_BANNED);
            candidateRepository.save(candidate);
        }
        interviewService.cancelInterviews(Candidate.class);
    }

    @Override
    public void unbanCandidate(Integer id) {
        Candidate candidate = candidateRepository.findById(id).orElse(null);
        if (candidate != null && !candidate.getDeleteFlag()) {
            candidate.setStatusId(ConstantUtils.CANDIDATE_OPEN);
            candidateRepository.save(candidate);
        }
    }

    /**
     * Convert list of integer to string
     *
     * @param list list of skills
     * @return String
     */
    private String listToString(List<String> list) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            result.append(list.get(i));
            if (i != list.size() - 1) {
                result.append(",");
            }
        }
        return result.toString();
    }

    /**
     * Get all candidates with status is open or waiting for interview
     *
     * @return List<CandidateDTO>
     */
    @Override
    public List<CandidateDTO> getAllInterviewScheduleCandidates() {
        return candidateRepository.findAllIntervCandidateDTOs(ConstantUtils.CANDIDATE_STATUS, List.of(ConstantUtils.CANDIDATE_BANNED));
    }

    @Override
    public Candidate getCandidateByIdAndStatusIds(Integer candidateId, BindingResult errors, String field,
            List<Integer> status) {
        Candidate candidate = candidateRepository
                .findCandidateByIdAndStatusId(candidateId, status)
                .orElse(null);
        if (candidate == null && errors.getFieldError(field) == null) {
            String errorMessage = messageSource.getMessage("ME022.2", null, Locale.getDefault());
            errors.rejectValue(field, "ME022.2", errorMessage);
        }
        return candidate;
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return candidateRepository.findByPhoneNumber(phoneNumber) != null;
    }
}
