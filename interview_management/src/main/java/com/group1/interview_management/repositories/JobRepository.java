package com.group1.interview_management.repositories;

import com.group1.interview_management.dto.JobDTO.response.JobSearchResponse;
import com.group1.interview_management.entities.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {

    @Query("""
            SELECT j
            FROM Job j
            WHERE j.deleteFlag = false
            """)
    @Override
    List<Job> findAll();

        @Query("""
                        SELECT j
                        FROM Job j
                        WHERE j.jobId = :jobId
                        AND j.deleteFlag = false
                        AND j.statusJobId IN (:statusIds)
                        """)
        Optional<Job> findJobByIdAndStatusIds(@Param("jobId") Integer jobId, @Param("statusIds") List<Integer> statusIds);

    @Modifying
    @Query("""
                    UPDATE Job j SET j.deleteFlag = true WHERE j.jobId = :jobId
            """)
    void deleteJobByJobId(@Param("jobId") Integer jobId);

    @Modifying
    @Query("""
                    UPDATE Job j SET j.statusJobId = 2 WHERE j.statusJobId = 1 and j.startDate >= :dateNow
            """)
    void openJob(@Param("dateNow") LocalDate dateNow);

    @Modifying
    @Query("""
                    UPDATE Job j SET j.statusJobId = 3 WHERE j.statusJobId = 2 and j.endDate < :dateNow
            """)
    void closeJob(@Param("dateNow") LocalDate date);

    @Query("""
                    SELECT j.startDate from Job j where j.jobId = :jobId
            """)
    LocalDate findJobStartDate(@Param("jobId") Integer jobId);

    @Query("""
            SELECT new com.group1.interview_management.dto.JobDTO.response.JobSearchResponse(
                j.jobId, j.title, j.startDate, j.endDate, j.level, j.skills, s.categoryValue
            )
            FROM Job j
            JOIN Master s ON j.statusJobId = s.categoryId AND s.category = :statusCategory
            WHERE (:keyword IS NULL OR :keyword = '' OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:status IS NULL OR s.categoryId = :status) and j.deleteFlag = false
            ORDER BY j.modifiedDate DESC
            """)
    Page<JobSearchResponse> searchByKeyword(@Param("keyword") String keyword,
                                            @Param("status") Integer status,
                                            Pageable pageable, String statusCategory);

    Job getJobByJobId(Integer jobId);

    @Query("""
                        select j.startDate
                        from Job j
                        where j.jobId = :jobId
            """)
    LocalDate getStartDateByJobId(Integer jobId);

    @Query("""
            SELECT j
            FROM Job j
            WHERE j.deleteFlag = false
            AND j.statusJobId = :statusId
            """)
    List<Job> findAllByStatusJobId(Integer statusId);
}
