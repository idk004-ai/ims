package com.group1.interview_management.repositories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group1.interview_management.dto.interview.InterviewDTO;
import com.group1.interview_management.dto.interview.ScheduleConflictDTO;
import com.group1.interview_management.entities.Interview;

public interface InterviewRepository extends JpaRepository<Interview, Integer> {
     @Query("""
               SELECT new com.group1.interview_management.dto.interview.InterviewDTO(
                    i.interviewId, i.title, c.name, itver.fullname, CAST(i.schedule AS string),
                    CASE WHEN i.resultInterviewId = 0 THEN NULL ELSE r.categoryValue END,
                    s.categoryValue, j.title
               )
               FROM Interview i
               LEFT JOIN i.interviewAssignments ia
               JOIN ia.interviewer itver
               JOIN i.candidate c
               JOIN i.job j
               LEFT JOIN Master r ON i.resultInterviewId = r.categoryId AND r.category = :resultCategory
               JOIN Master s ON i.statusInterviewId = s.categoryId AND s.category = :statusCategory
               WHERE (:query IS NULL OR :query = '' OR
                    LOWER(i.title) LIKE LOWER(CONCAT('%', :query, '%')) OR
                    CAST(i.schedule AS string) LIKE CONCAT('%', :query, '%') OR
                    LOWER(i.candidate.name) LIKE LOWER(CONCAT('%', :query, '%')) OR
                    LOWER(i.job.title) LIKE LOWER(CONCAT('%', :query, '%')) OR
                    LOWER(r.categoryValue) LIKE LOWER(CONCAT('%', :query, '%')) OR
                    LOWER(s.categoryValue) LIKE LOWER(CONCAT('%', :query, '%')))
               AND (:statusId IS NULL OR i.statusInterviewId = :statusId)
               AND (:interviewerId IS NULL OR ia.interviewer.id = :interviewerId)
               """)
     Page<InterviewDTO> findAllByCondition(
               @Param("statusId") Integer statusId,
               @Param("interviewerId") Integer interviewerId,
               @Param("query") String query,
               String resultCategory,
               String statusCategory,
               Pageable pageable);

     @Query("""
               SELECT DISTINCT i FROM Interview i
               LEFT JOIN FETCH i.interviewAssignments ia
               LEFT JOIN FETCH ia.interviewer u
               LEFT JOIN FETCH i.candidate c
               LEFT JOIN FETCH i.job j
               WHERE i.schedule BETWEEN :startDate AND :endDate
               AND i.statusInterviewId NOT IN (:excludedStatuses)
               ORDER BY i.schedule ASC, i.startTime ASC
               """)
     List<Interview> findUpcomingInterviewsInDateRange(LocalDate startDate, LocalDate endDate,
               List<Integer> excludedStatuses);

     /**
      * Find interviews that overlap with the given time range,
      * do not include the interview with the old start time and end time
      * but not closed and not canceled
      * 
      * @param date
      * @param startTime
      * @param endTime
      * @param oldStartTime
      * @param oldEndTime
      * @param interviewStatuses
      * @return
      */
     @Query("""
               SELECT new com.group1.interview_management.dto.interview.ScheduleConflictDTO(
                    i.interviewId, ia.interviewer.id, ia.interviewer.fullname, i.schedule, i.startTime, i.endTime, 4
               )
               FROM Interview i
               JOIN i.interviewAssignments ia
               WHERE i.schedule = :date
               AND (:excludeInterviewId IS NULL OR i.interviewId != :excludeInterviewId)
               AND i.statusInterviewId NOT IN (:excludedStatuses)
               AND (i.startTime <= :endTime AND i.endTime >= :startTime)
               """)
     List<ScheduleConflictDTO> findOverlappingInterviews(
               @Param("date") LocalDate date,
               @Param("startTime") LocalTime startTime,
               @Param("endTime") LocalTime endTime,
               @Param("excludeInterviewId") Integer excludeInterviewId,
               @Param("excludedStatuses") List<Integer> excludedStatuses);

     /**
      * Count the number of interviews that a candidate has
      * but not closed and not canceled
      * 
      * @param candidateId
      * @param interviewStatusClosed
      * @return
      */
     @Query("""
               SELECT COUNT(i)
               FROM Interview i
               WHERE i.candidate.id = :candidateId
               AND i.statusInterviewId NOT IN (:interviewStatuses)
               """)
     int countInterviewsByCandidateId(@Param("candidateId") Integer candidateId,
               @Param("interviewStatuses") List<Integer> interviewStatuses);

     /**
      * Find interviews that a candidate has and the status is not closed
      * 
      * @param candidateId
      * @param interviewStatusClosed
      * @param date
      * @param startTime
      * @param endTime
      * @return
      */
     @Query("""
                   SELECT new com.group1.interview_management.dto.interview.ScheduleConflictDTO(
                       i.interviewId, i.candidate.candidateId, i.candidate.name, i.schedule, i.startTime, i.endTime, 0
                   )
                   FROM Interview i
                   WHERE i.candidate.candidateId = :candidateId
                   AND (:excludeInterviewId IS NULL OR i.interviewId != :excludeInterviewId)
                   AND i.schedule = :schedule
                   AND i.statusInterviewId NOT IN :excludedStatuses
                   AND (i.startTime <= :endTime AND i.endTime >= :startTime)
               """)
     List<ScheduleConflictDTO> findOverlappingInterviewsByCandidateId(
               @Param("candidateId") Integer candidateId,
               @Param("schedule") LocalDate schedule,
               @Param("startTime") LocalTime startTime,
               @Param("endTime") LocalTime endTime,
               @Param("excludeInterviewId") Integer excludeInterviewId,
               @Param("excludedStatuses") List<Integer> excludedStatuses);

     /**
      * Find all active interviews schedule with all statuses by candidate id.
      * 
      * @param candidateId      The ID of the candidate to find interviews for
      * @param excludedStatuses List of status IDs to exclude from the search
      * @return {@link java.util.List List} of
      *         {@link com.group1.interview_management.entities.Interview
      *         InterviewSchedule}
      *         containing all active interviews for the candidate
      * @see com.group1.interview_management.entities.Interview
      */
     @Query("""
               SELECT i FROM Interview i
               WHERE i.candidate.candidateId = :candidateId
               AND i.statusInterviewId NOT IN :excludedStatuses
               """)
     List<Interview> findActiveInterviewsByCandidateId(
               @Param("candidateId") Integer candidateId,
               @Param("excludedStatuses") List<Integer> excludedStatuses);

     @Query("""
               SELECT i FROM Interview i
               LEFT JOIN FETCH i.interviewAssignments ia
               LEFT JOIN FETCH ia.interviewer
               LEFT JOIN FETCH i.candidate
               LEFT JOIN FETCH i.job
               WHERE i.schedule < :date
               AND i.statusInterviewId NOT IN (:excludedStatuses)
               ORDER BY i.schedule ASC
                          """)
     List<Interview> findByScheduleLessThanAndStatusInterviewIdNotIn(@Param("date") LocalDate currentDate,
               @Param("excludedStatuses") List<Integer> excludedStatuses);

     @Query("""
               SELECT i FROM Interview i
               LEFT JOIN FETCH i.interviewAssignments ia
               LEFT JOIN FETCH ia.interviewer
               LEFT JOIN FETCH i.candidate
               LEFT JOIN FETCH i.job
               WHERE i.statusInterviewId NOT IN (:excludedStatuses)
               """)
     List<Interview> findByStatusNotIn(List<Integer> excludedStatuses);

}
