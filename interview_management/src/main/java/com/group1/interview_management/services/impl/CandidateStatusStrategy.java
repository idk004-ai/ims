package com.group1.interview_management.services.impl;

import java.util.List;
import com.group1.interview_management.entities.Interview;

/**
 * Strategy interface for determining candidate status based on interview results.
 * @see com.group1.interview_management.common.ConstantUtils
 */
public interface CandidateStatusStrategy {
    /**
     * <p> This function handles the status of candidate after an interview schedule is updated: </p>
     * <ul>
     * <li> If the candidate has only one interview schedule N/A result, 
     *      the status of the candidate remains {@link com.group1.interview_management.common.ConstantUtils#CANDIDATE_WAITING_FOR_INTERVIEW "Waiting for interview"} </li>
     * <li> Else if the candidate has at least one interview schedule passed, 
     *      the status of the candidate becomes {@link com.group1.interview_management.common.ConstantUtils#CANDIDATE_PASSED_INTERVIEW "Passed Interview"} </li>
     * <li> Else if the candidate has all interview schedules failed, 
     *      the status of the candidate becomes {@link com.group1.interview_management.common.ConstantUtils#CANDIDATE_FAILED_INTERVIEW "Failed Interview"} </li>
     * </ul>
     * 
     * @param interviewSchedules List of interview schedules for the candidate
     * @return The new status code for the candidate. Possible values are:
     *         <ul>
     *         <li>{@link com.group1.interview_management.common.ConstantUtils#CANDIDATE_WAITING_FOR_INTERVIEW}</li>
     *         <li>{@link com.group1.interview_management.common.ConstantUtils#CANDIDATE_PASSED_INTERVIEW}</li>
     *         <li>{@link com.group1.interview_management.common.ConstantUtils#CANDIDATE_FAILED_INTERVIEW}</li>
     *         </ul>
     * @see com.group1.interview_management.common.ConstantUtils
     */
    int determineNewStatus(List<Interview> interviewSchedules);
}
