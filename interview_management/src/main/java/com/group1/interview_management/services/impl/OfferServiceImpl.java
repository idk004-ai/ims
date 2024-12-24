package com.group1.interview_management.services.impl;

import com.group1.interview_management.dto.Offer.OfferExportDTO;
import com.group1.interview_management.entities.InterviewAssignment;
import com.group1.interview_management.repositories.*;

import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.dto.OfferCreateDTO;
import com.group1.interview_management.dto.OfferDTO;
import com.group1.interview_management.dto.OfferSearchDTO;
import com.group1.interview_management.dto.Offer.OfferDetailDTO;
import com.group1.interview_management.entities.Interview;
import com.group1.interview_management.services.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OfferServiceImpl implements OfferService {
    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private MasterRepository masterRepository;

    @Autowired
    private MasterService masterService;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private InterviewAssignmentRepository iaRepository;

    private String getCategoryValue(String category, Integer categoryId) {
        return masterRepository.findByCategoryAndCategoryId(category, categoryId).get().getCategoryValue();
    }

    @Override
    public Page<OfferDTO> searchOffers(OfferSearchDTO request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());
        return offerRepository.searchByKeyword(request.getQuery(), request.getDepartment(), request.getStatus(),
                pageable);
    }

    @Override
    public List<OfferExportDTO> getOfferByDueDate(LocalDate periodFrom, LocalDate periodTo) {
        String position = ConstantUtils.POSITION;
        String contractType = ConstantUtils.CONTRACT_TYPE;
        String department = ConstantUtils.DEPARTMENT;

        // Fetch offers in one go
        List<OfferExportDTO> listExport = offerRepository.findByPeriod(periodFrom, periodTo, position, contractType,
                department);

        // Collect interview IDs for batch fetching
        Set<Integer> interviewIds = listExport.stream()
                .map(OfferExportDTO::getInterviewId)
                .collect(Collectors.toSet());

        // Fetch all InterviewAssignments in one go
        Map<Integer, List<InterviewAssignment>> interviewAssignmentsMap = new HashMap<>();
        for (Integer interviewId : interviewIds) {
            List<InterviewAssignment> assignments = iaRepository.findByInterviewId(interviewId);
            interviewAssignmentsMap.put(interviewId, assignments);
        }

        // Collect unique levels for batch fetching
        Set<String> uniqueLevels = listExport.stream()
                .map(OfferExportDTO::getLevel)
                .collect(Collectors.toSet());

        // Create a map to store level values
        Map<String, String> levelValuesMap = new HashMap<>();
        for (String level : uniqueLevels) {
            String categoryValue = masterService.getListCategoryValue(level, ConstantUtils.LEVEL);
            levelValuesMap.put(level, categoryValue);
        }

        // Transform levels and set interviewers
        for (OfferExportDTO offer : listExport) {

            // Set the level using the pre-fetched map
            offer.setLevel(levelValuesMap.get(offer.getLevel()));

            // Retrieve the interview assignments from the map
            List<InterviewAssignment> assignments = interviewAssignmentsMap.get(offer.getInterviewId());
            StringBuilder interviewer = new StringBuilder();

            // Construct interviewer string
            if (assignments != null) {
                for (InterviewAssignment assignment : assignments) {
                    interviewer.append(assignment.getInterviewer().getUsername_()).append(", ");
                }
            }

            // Remove the trailing comma and space, if any
            if (!interviewer.isEmpty()) {
                interviewer.setLength(interviewer.length() - 2); // Remove the last ", "
            }

            // Set the interviewers string in the OfferExportDTO (assuming you have a setter
            // for this)
            offer.setInterviewer(interviewer.toString());
        }

        return listExport;
    }

    @Override
    public OfferDetailDTO getOfferById(Integer id) {
        Interview interview = interviewRepository.findById(id).orElse(null);
        if (interview == null) {
            return null;
        }
        OfferDetailDTO offerDetailDTO = new OfferDetailDTO();
        offerDetailDTO.setInterviewId(interview.getInterviewId());
        offerDetailDTO.setCandidateName(interview.getCandidate().getName());
        offerDetailDTO.setContractType(getCategoryValue(ConstantUtils.CONTRACT_TYPE, interview.getContractTypeId()));
        offerDetailDTO.setPosition(getCategoryValue(ConstantUtils.POSITION, interview.getCandidate().getPositionId()));
        String level = masterService.getAllLevelsById(interview.getJob().getLevel()).toString();
        offerDetailDTO.setLevel(level.substring(1, level.length() - 1));
        offerDetailDTO.setApprover(interview.getOfferCreator().getFullname());
        offerDetailDTO.setDepartment(getCategoryValue(ConstantUtils.DEPARTMENT, interview.getOfferdepartment()));
        offerDetailDTO.setInterviewinfo(interview.getTitle());
        offerDetailDTO.setRecruiter(interview.getCandidate().getRecruiter().getFullname());
        offerDetailDTO.setPeriodfrom(interview.getContractFrom());
        offerDetailDTO.setPeriodto(interview.getContractTo());
        offerDetailDTO.setDuedate(interview.getDueDate());
        offerDetailDTO.setNote(interview.getInterviewNote());
        offerDetailDTO.setSalary(interview.getSalary());
        offerDetailDTO.setOfferNote(interview.getOfferNote());
        offerDetailDTO.setStatus(interview.getStatusOfferId());
        offerDetailDTO.setModifiedBy(userService.getUserById(interview.getModifiedBy()).getUsername());
        offerDetailDTO.setModifiedDate(interview.getModifiedDate());
        offerDetailDTO.setCreateDate(interview.getCreatedDate());
        return offerDetailDTO;
    }

    public OfferCreateDTO getOfferCreateDTOById(Integer id) {
        Interview interview = interviewRepository.findById(id).get();
        OfferCreateDTO offerCreateDTO = new OfferCreateDTO();
        offerCreateDTO.setCandidateName(interview.getCandidate().getName());
        offerCreateDTO.setPosition(getCategoryValue(ConstantUtils.POSITION, interview.getCandidate().getPositionId()));
        String level = masterService.getAllLevelsById(interview.getJob().getLevel()).toString();
        offerCreateDTO.setLevel(level.substring(1, level.length() - 1));
        offerCreateDTO.setApprover(interview.getOfferCreator().getFullname());
        offerCreateDTO.setRecruiter(interview.getCandidate().getRecruiter().getFullname());
        offerCreateDTO.setContractType(getCategoryValue(ConstantUtils.CONTRACT_TYPE, interview.getContractTypeId()));
        offerCreateDTO.setDepartment(getCategoryValue(ConstantUtils.DEPARTMENT, interview.getOfferdepartment()));
        offerCreateDTO.setInterviewinfo(interview.getTitle());
        offerCreateDTO.setPeriodfrom(interview.getContractFrom());
        offerCreateDTO.setPeriodto(interview.getContractTo());
        offerCreateDTO.setDuedate(interview.getDueDate());
        offerCreateDTO.setInterviewNote(interview.getInterviewNote());
        offerCreateDTO.setSalary(interview.getSalary());
        offerCreateDTO.setOfferNote(interview.getOfferNote());
        offerCreateDTO.setEmail(interview.getCandidate().getEmail());
        offerCreateDTO.setInterviewId(id);
        offerCreateDTO.setStatus(interview.getStatusOfferId());
        return offerCreateDTO;
    }

    public void saveOffer(OfferCreateDTO offerCreateDTO) {
        Interview interview = interviewRepository.findById(offerCreateDTO.getInterviewId())
                .orElseThrow(() -> new NoSuchElementException("Interview not found"));
        interview.setContractTypeId(masterRepository.findByCategoryAndCategoryId(ConstantUtils.CONTRACT_TYPE,
                Integer.parseInt(offerCreateDTO.getContractType())).get().getCategoryId());
        interview.setOfferdepartment(masterRepository
                .findByCategoryAndCategoryId(ConstantUtils.DEPARTMENT, Integer.parseInt(offerCreateDTO.getDepartment()))
                .get().getCategoryId());
        interview.setOfferCreator(userRepository.findById(Integer.parseInt(offerCreateDTO.getApprover())).get());
        interview.setContractFrom(offerCreateDTO.getPeriodfrom());
        interview.setContractTo(offerCreateDTO.getPeriodto());
        interview.setDueDate(offerCreateDTO.getDuedate());
        interview.setSalary(offerCreateDTO.getSalary());
        interview.setOfferNote(offerCreateDTO.getOfferNote());
        interview.setInterviewId(interview.getInterviewId());
        interview.setStatusOfferId(
                masterRepository.findByCategoryAndCategoryId(ConstantUtils.OFFER_STATUS, ConstantUtils.OFFER_STATUS_WAITING_FOR_APPROVAL).get().getCategoryId());
        interviewRepository.save(interview);
    }

    @Override
    public void approveOffer(Integer id) {
        Interview offer = interviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Offer not found"));
        offer.setStatusOfferId(ConstantUtils.OFFER_STATUS_APPROVED_OFFER);
        offer.getCandidate().setStatusId(ConstantUtils.CANDIDATE_APPROVED_OFFER);
        offerRepository.save(offer);
    }

    @Override
    public void rejectOffer(Integer id) {
        Interview offer = interviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Offer not found"));
        offer.setStatusOfferId(ConstantUtils.OFFER_STATUS_REJECTED_OFFER);
        offer.getCandidate().setStatusId(ConstantUtils.CANDIDATE_REJECTED_OFFER);
        offerRepository.save(offer);
    }

    @Override
    public void sentOffer(Integer id) {
        Interview offer = interviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Offer not found"));
        offer.setStatusOfferId(ConstantUtils.OFFER_STATUS_WAITING_FOR_RESPONSE);
        offer.getCandidate().setStatusId(ConstantUtils.CANDIDATE_WAITING_FOR_INTERVIEW);
        offerRepository.save(offer);
    }

    @Override
    public void acceptOffer(Integer id) {
        Interview offer = interviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Offer not found"));
        offer.setStatusOfferId(ConstantUtils.OFFER_STATUS_ACCEPTED_OFFER);
        offer.getCandidate().setStatusId(ConstantUtils.CANDIDATE_ACCEPTED_OFFER);
        offerRepository.save(offer);
    }

    @Override
    public void declinedOffer(Integer id) {
        Interview offer = interviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Offer not found"));
        offer.setStatusOfferId(ConstantUtils.OFFER_STATUS_DECLINED_OFFER);
        offer.getCandidate().setStatusId(ConstantUtils.CANDIDATE_DECLINED_OFFER);
        offerRepository.save(offer);
    }

    public void cancelOffer(Integer id) {
        Interview offer = interviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Offer not found"));
        offer.setStatusOfferId(ConstantUtils.OFFER_STATUS_CANCELLED);
        offer.getCandidate().setStatusId(ConstantUtils.CANDIDATE_CANCELLED_OFFER);
        offerRepository.save(offer);
    }

    public List<Interview> getAllUpcomingOffer(LocalDate start, LocalDate end) {
        return offerRepository.findUpcomingOffersInDateRange(start, end, List.of(ConstantUtils.OFFER_STATUS_WAITING_FOR_APPROVAL));
    }
}
