package com.group1.interview_management.controller;

import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.dto.candidate.CandidateCreateDTO;
import com.group1.interview_management.dto.candidate.CandidateDTO;
import com.group1.interview_management.dto.candidate.CandidateDetailDTO;
import com.group1.interview_management.dto.candidate.CandidateSearchDTO;
import com.group1.interview_management.dto.UserDTO;
import com.group1.interview_management.entities.Master;
import com.group1.interview_management.entities.User;
import com.group1.interview_management.services.CandidateService;
import com.group1.interview_management.services.MasterService;
import com.group1.interview_management.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.context.MessageSource;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/candidates")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private MasterService masterService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    /**
     * Get all candidates
     *
     * @param model model need to display in candidate list page
     * @return candidate_list.html
     */
    @GetMapping("/list_candidates")
    public String listCandidate(Model model) {
        // Get all candidate statuses
        List<Master> statuses = masterService.findByCategory(ConstantUtils.CANDIDATE_STATUS);

        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userPrincipal = new User();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            userPrincipal = (User) authentication.getPrincipal();
        }
        // Add statuses to model
        model.addAttribute("statuses", statuses);
        model.addAttribute("user", userPrincipal);
        return ConstantUtils.CANDIDATE_LIST;
    }


    /**
     * Search candidates by dto
     *
     * @param request search object contain all property need to search
     * @return List of candidates
     */
    @PostMapping("/search")
    @ResponseBody
    public Page<CandidateDTO> searchCandidates(@RequestBody CandidateSearchDTO request) {
        return candidateService.searchCandidates(request);
    }


    /**
     * Create a new candidate
     *
     * @return create_candidate.html
     */
    @GetMapping("/create_candidate")
    public String createCandidate(Model model) {
        // Get all candidate statuses
        Master openStatus = null;
        Master bannedStatus = null;

        Optional<Master> openStatusOpt = masterService.findByCategoryAndValue(ConstantUtils.CANDIDATE_STATUS, ConstantUtils.STATUS_OPEN);
        Optional<Master> bannedStatusOpt = masterService.findByCategoryAndValue(ConstantUtils.CANDIDATE_STATUS, ConstantUtils.STATUS_BANNED);

        if (openStatusOpt.isPresent() && bannedStatusOpt.isPresent()) {
            openStatus = openStatusOpt.get();
            bannedStatus = bannedStatusOpt.get();
        } else {
            throw new RuntimeException("Status not found");
        }

        List<Master> statuses = new ArrayList<>();
        statuses.add(openStatus);
        statuses.add(bannedStatus);

        // Get all positions
        List<Master> positions = masterService.findByCategory(ConstantUtils.POSITION);

        // Get all gender
        List<Master> gender = masterService.findByCategory(ConstantUtils.GENDER);

        // Get level of education
        List<Master> highestLevel = masterService.findByCategory(ConstantUtils.HIGHEST_LEVEL);

        // Get all skills
        List<Master> skills = masterService.findByCategory(ConstantUtils.SKILLS);

        // Get all Recruiters
        Optional<Master> recruiter = masterService.findByCategoryAndValue(ConstantUtils.USER_ROLE, ConstantUtils.RECRUITER);
        if (recruiter.isEmpty()) {
            throw new RuntimeException("Recruiter not found");
        }
        List<UserDTO> recruiters = userService.getAllUsersByRole(recruiter.get().getId());

        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userPrincipal = new User();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            userPrincipal = (User) authentication.getPrincipal();
        }


        // Add attributes to model
        model.addAttribute("statuses", statuses);
        model.addAttribute("positions", positions);
        model.addAttribute("gender", gender);
        model.addAttribute("highestLevel", highestLevel);
        model.addAttribute("skills", skills);
        model.addAttribute("recruiters", recruiters);
        model.addAttribute("user", userPrincipal);

        return ConstantUtils.CREATE_CANDIDATE;
    }

    /**
     * Create a new candidate
     *
     * @param candidateDTO createObject
     * @return ResponseEntity
     */
    @PostMapping("/create")
    public ResponseEntity<?> createCandidate(@Valid @RequestBody CandidateCreateDTO candidateDTO, BindingResult result, Locale locale) {
        // Check exist mail
        if (candidateService.existsByEmail(candidateDTO.getEmail())) {
            // Create email exist message
            String emailExistsMessage = messageSource.getMessage("ME035", null, locale);
            result.addError(new FieldError("candidateDTO", "email", emailExistsMessage));
        }
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        // Save candidate
        candidateService.saveCandidate(candidateDTO);

        // Create success message
        String successMessage = messageSource.getMessage("ME012", null, locale);
        return ResponseEntity.ok(successMessage);
    }

    /**
     * Get candidate by id
     *
     * @param id id of candidate
     * @return CandidateDTO
     */
    @GetMapping("/candidate_detail/{id}")
    public String getCandidateById(@PathVariable Integer id, Model model) {
        CandidateDetailDTO candidate = candidateService.getCandidateById(id);

        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userPrincipal = new User();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            userPrincipal = (User) authentication.getPrincipal();
        }
        model.addAttribute("candidate", candidate);
        model.addAttribute("user", userPrincipal);
        return ConstantUtils.CANDIDATE_INFORMATION;
    }

    /**
     * Update candidate by id
     *
     * @param id id of candidate need to update
     * @return CandidateDTO
     */
    @GetMapping("/update_candidate/{id}")
    public String updateCandidate(@PathVariable Integer id, Model model) {
        CandidateCreateDTO candidate = candidateService.getCandidateCreateDTOById(id);

        // Get all candidate statuses
        List<Master> statuses = masterService.findByCategory(ConstantUtils.CANDIDATE_STATUS);

        // Get all positions
        List<Master> positions = masterService.findByCategory(ConstantUtils.POSITION);

        // Get all gender
        List<Master> gender = masterService.findByCategory(ConstantUtils.GENDER);

        // Get level of education
        List<Master> highestLevel = masterService.findByCategory(ConstantUtils.HIGHEST_LEVEL);

        // Get all skills
        List<Master> skills = masterService.findByCategory(ConstantUtils.SKILLS);

        // Get all Recruiters
        Optional<Master> recruiter = masterService.findByCategoryAndValue(ConstantUtils.USER_ROLE, ConstantUtils.RECRUITER);
        if (recruiter.isEmpty()) {
            throw new RuntimeException("Recruiter not found");
        }
        List<UserDTO> recruiters = userService.getAllUsersByRole(recruiter.get().getId());

        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userPrincipal = new User();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            userPrincipal = (User) authentication.getPrincipal();
        }

        // Add attributes to model
        model.addAttribute("statuses", statuses);
        model.addAttribute("positions", positions);
        model.addAttribute("gender", gender);
        model.addAttribute("highestLevel", highestLevel);
        model.addAttribute("skills", skills);
        model.addAttribute("recruiters", recruiters);
        model.addAttribute("user", userPrincipal);
        model.addAttribute("candidate", candidate);

        return ConstantUtils.CANDIDATE_UPDATE;
    }

    /**
     * Create a new candidate
     *
     * @param candidateDTO update object
     * @return ResponseEntity
     */
    @PostMapping("/edit_candidate")
    public ResponseEntity<?> editCandidate(@Valid @RequestBody CandidateCreateDTO candidateDTO, BindingResult result, Locale locale) {
        // Get existing candidate by id
        CandidateCreateDTO existingCandidate = candidateService.getCandidateCreateDTOById(candidateDTO.getId());

        // Check if email not belong to current candidate and exist
        if (!existingCandidate.getEmail().equals(candidateDTO.getEmail())
                && candidateService.existsByEmail(candidateDTO.getEmail())) {
            String emailExistsMessage = messageSource.getMessage("ME035", null, locale);
            result.addError(new FieldError("candidateDTO", "email", emailExistsMessage));
        }
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        // Save candidate
        candidateService.saveCandidate(candidateDTO);
        String successUpdate = messageSource.getMessage("ME014", null, locale);
        return ResponseEntity.ok(successUpdate);
    }

    /**
     * Delete candidate(soft delete)
     *
     * @param id id of candidate need to delete
     * @return responseEntity
     */
    @DeleteMapping("/delete_candidate/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Integer id) {
        if (candidateService.existsById(id)) {
            candidateService.deleteCandidate(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/ban/{id}")
    public ResponseEntity<Void> banCandidate(@PathVariable Integer id) {
        if (candidateService.existsById(id)) {
            candidateService.banCandidate(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/unban/{id}")
    public ResponseEntity<Void> unbanCandidate(@PathVariable Integer id) {
        if (candidateService.existsById(id)) {
            candidateService.unbanCandidate(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
