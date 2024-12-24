package com.group1.interview_management.repositories;

import com.group1.interview_management.entities.Job;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    @Modifying
    @Query("""
                    UPDATE Job j SET j.statusJobId = 2 WHERE j.statusJobId = 1 and j.startDate >= :dateNow
            """)
    void openJob(@Param("dateNow") LocalDate dateNow);

    @Transactional
    @Modifying
    @Query("""
                    UPDATE Job j SET j.statusJobId = 3 WHERE j.statusJobId = 2 and j.endDate < :dateNow
            """)
    void closeJob(@Param("dateNow") LocalDate date);

    @Query(value = """
            SELECT * FROM (
                    SELECT 
                t1.job_id AS jobId,
                t1.job_title AS title,
                t1.start_date AS startDate,
                t1.end_date AS endDate,
                GROUP_CONCAT(DISTINCT m2.category_value ORDER BY m2.category_id SEPARATOR ', ') AS level,
                GROUP_CONCAT(DISTINCT m1.category_value ORDER BY m1.category_id SEPARATOR ', ') AS skills,
                m3.category_value AS categoryValue
                    FROM job t1
                    LEFT JOIN masters m1
                ON FIND_IN_SET(m1.category_id, REPLACE(REPLACE(t1.skills, ' ', ''), ',,', ',')) > 0
                AND m1.category like :categorySkills
                    LEFT JOIN masters m2
                ON FIND_IN_SET(m2.category_id, REPLACE(REPLACE(t1.level, ' ', ''), ',,', ',')) > 0
                AND m2.category like :categoryLevel
                    LEFT JOIN masters m3
                ON m3.category = :categoryJobStatus AND m3.category_id = t1.status_job_id
                    WHERE (:status IS NULL OR m3.category_id = :status)
                    AND t1.delete_flag = false
                    GROUP BY t1.job_id
                    ORDER BY t1.modified_date DESC
                    ) n
                     
            WHERE (:keyword IS NULL OR :keyword = '' 
                       OR LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                       OR LOWER(n.startDate) LIKE LOWER(CONCAT('%', :keyword, '%'))
                       OR LOWER(n.endDate) LIKE LOWER(CONCAT('%', :keyword, '%'))
                       OR LOWER(n.level) LIKE LOWER(CONCAT('%', :keyword, '%'))
                       OR LOWER(n.skills) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """, nativeQuery = true)
    Page<Object[]> searchByKeyword(@Param("keyword") String keyword,
                                   @Param("status") Integer status,
                                   Pageable pageable,
                                   @Param("categorySkills") String categorySkills,
                                   @Param("categoryLevel") String categoryLevel,
                                   @Param("categoryJobStatus") String categoryJobStatus);

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
