package com.group1.interview_management.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import com.group1.interview_management.entities.InterviewAssignment;

import java.util.List;

public interface InterviewAssignmentRepository extends JpaRepository<InterviewAssignment, Integer> {
    /**
     * delete all interview assignment by interview id
     *
     * @param interviewId id of interview schedule
     */
    @Modifying
    @Transactional
    @Query("""
            DELETE FROM InterviewAssignment ia WHERE ia.interview.id = :interviewId
            """)
    void deleteAllByInterviewId(@Param("interviewId") Integer interviewId);

    /**
     * find interviewer by id
     * 
     * @param interviewId id of interview schedule
     * @return list
     */
    @Query("SELECT ia FROM InterviewAssignment ia WHERE ia.interview.interviewId = :interviewId")
    List<InterviewAssignment> findByInterviewId(@Param("interviewId") Integer interviewId);
}
