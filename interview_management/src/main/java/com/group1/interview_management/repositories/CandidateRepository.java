package com.group1.interview_management.repositories;

import com.group1.interview_management.dto.candidate.CandidateDTO;
import com.group1.interview_management.dto.candidate.CandidateDetailDTO;
import com.group1.interview_management.entities.Candidate;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Integer> {

    /**
     * Search candidate by keyword and status
     *
     * @param keyword the keyword user enter into search bar
     * @param status the status of selection bar
     * @param pageable page of list
     * @return Page<CandidateDTO>
     */
    @Query("""
            SELECT new com.group1.interview_management.dto.candidate.CandidateDTO(
                c.candidateId, c.name, c.email, c.phoneNumber, 
                p.categoryValue, u.email, s.categoryValue
            )
            FROM Candidate c 
            JOIN c.recruiter u 
            JOIN Master s ON c.statusId = s.categoryId AND s.category = :statusCategory 
            JOIN Master p ON c.positionId = p.categoryId AND p.category = :positionCategory 
            WHERE (:keyword IS NULL OR :keyword = '' OR 
                LOWER(p.categoryValue) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                LOWER(c.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:status IS NULL OR s.categoryId = :status)
            AND c.deleteFlag = false
            ORDER BY c.modifiedDate DESC
            """)
    Page<CandidateDTO> searchByKeyword(@Param("keyword") String keyword,
                                       @Param("status") Integer status,
                                       Pageable pageable, String statusCategory, String positionCategory);

    /**
     * Find a candidate by their email.
     *
     * @param email the email of the candidate to find
     * @return the Candidate object with the specified email, or null if not found
     */
    Candidate findByEmail(String email);

    @Query("""
            SELECT new com.group1.interview_management.dto.candidate.CandidateDetailDTO(
                c.candidateId,c.name,c.email,c.phoneNumber,p.categoryValue,CONCAT(r.fullname, ' (', r.email, ')'),
                s.categoryValue,c.address,c.dob,g.categoryValue,
                c.cv,hl.categoryValue,c.skill,c.yoe,c.note,c.cvFileName,c.createdDate,
                c.modifiedDate,u.email
            )
            FROM Candidate c
            JOIN c.recruiter r
            JOIN Master g ON c.genderId = g.categoryId AND g.category = :genderCategory
            JOIN Master p ON c.positionId = p.categoryId AND p.category = :positionCategory
            JOIN Master s ON c.statusId = s.categoryId AND s.category = :statusCategory
            JOIN Master hl ON c.highestLevelId = hl.categoryId AND hl.category = :highestLevelCategory
            JOIN User u ON c.modifiedBy = u.id
            WHERE c.candidateId = :id AND c.deleteFlag = false
            """)
    CandidateDetailDTO findCandidateDetailById(@Param("id") Integer id,
                                               @Param("genderCategory") String genderCategory,
                                               @Param("positionCategory") String positionCategory,
                                               @Param("statusCategory") String statusCategory,
                                               @Param("highestLevelCategory") String highestLevelCategory);

    /**
     * Check candidate id
     *
     * @param id the id of the candidate to check
     * @return true if a candidate id exists and not deleted, false otherwise
     */
    @Query("SELECT COUNT(c) > 0 FROM Candidate c WHERE c.candidateId = :id AND c.deleteFlag = false")
    boolean existsByIdAndNotDeleted(@Param("id") Integer id);



    /**
     * Get candidate by id with waiting status
     * @param id : Identifier of candidate
     * @return
     */
    @Query("""
            SELECT c
            FROM Candidate c
            WHERE c.candidateId = :candidateId
            AND c.statusId NOT IN :excludeStatuses
            AND c.deleteFlag = false
            """)
    Optional<Candidate> findCandidateByIdAndStatusId(@Param("candidateId") Integer candidateId, @Param("excludeStatuses")List<Integer> excludeStatuses);

    @Query("""
            SELECT new com.group1.interview_management.dto.candidate.CandidateDTO(
                c.candidateId, c.name, c.email, c.phoneNumber, 
                "", u.email, s.categoryValue
            )
            FROM Candidate c 
            JOIN c.recruiter u 
            JOIN Master s ON c.statusId = s.categoryId AND s.category = :statusCategory 
            WHERE c.deleteFlag = false
            AND c.statusId NOT IN (:excludeStatuses)
            """)
    List<CandidateDTO> findAllIntervCandidateDTOs(@Param("statusCategory") String statusCategory, @Param("excludeStatuses") List<Integer> excludeStatuses);

    Candidate findByPhoneNumber(String phoneNumber);
}
