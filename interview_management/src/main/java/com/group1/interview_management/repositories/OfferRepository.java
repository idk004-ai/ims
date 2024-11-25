package com.group1.interview_management.repositories;

import com.group1.interview_management.dto.Offer.OfferDetailDTO;
import com.group1.interview_management.dto.Offer.OfferExportDTO;
import com.group1.interview_management.dto.OfferDTO;
import com.group1.interview_management.entities.Interview;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository extends JpaRepository<Interview, Integer> {
    @Query("""
                SELECT new com.group1.interview_management.dto.OfferDTO(i.interviewId,
                    c.name, c.email, u.fullname, d.categoryValue, i.offerNote, s.categoryValue)
                FROM Interview i
                JOIN i.candidate c
                JOIN i.offerCreator u
                JOIN Master s ON i.statusOfferId = s.categoryId AND s.category = 'OFFER_STATUS'
                JOIN Master d ON i.offerdepartment = d.categoryId AND d.category = 'DEPARTMENT'
                AND (:keyword IS NULL OR :keyword = '' OR
                    LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                    LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                    LOWER(u.fullname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                    LOWER(i.offerNote) LIKE LOWER(CONCAT('%', :keyword, '%')))
                AND (:status IS NULL OR s.categoryId = :status)
                AND (:department IS NULL OR d.categoryId = :department)
                ORDER BY c.modifiedDate DESC
            """)
    Page<OfferDTO> searchByKeyword(
            @Param("keyword") String query,
            @Param("department") Integer department,
            @Param("status") Integer status,
            Pageable pageable);

    /**
     * Find offer by due date
     *
     * @param periodFrom - Date from
     * @param periodTo - Date to
     * @return  return List of OfferDetailDTO
     */
    @Query("""
            SELECT new com.group1.interview_management.dto.Offer.OfferExportDTO(
                i.interviewId,c.candidateId,c.name,u.email,ct.categoryValue,ps.categoryValue,j.level, dp.categoryValue,
                c.recruiter.email,"",i.contractFrom,i.contractTo,i.salary,i.interviewNote,i.offerNote)
            FROM Interview i
            JOIN i.candidate c
            JOIN i.approver u
            JOIN i.job j
            JOIN Master ps ON c.positionId = ps.categoryId and ps.category = :position
            JOIN Master ct ON i.contractTypeId = ct.categoryId and ct.category = :contractType
            JOIN Master dp ON i.offerdepartment = dp.categoryId and dp.category = :department
            WHERE i.dueDate >= :periodFrom
            AND i.dueDate <= :periodTo
            """)
    List<OfferExportDTO> findByPeriod(
            @Param("periodFrom") LocalDate periodFrom,
            @Param("periodTo") LocalDate periodTo,
            @Param("position") String position,
            @Param("contractType") String contractType,
            @Param("department") String department
    );

}
