package com.group1.interview_management.services;

import com.group1.interview_management.dto.JobDTO.request.JobCreationRequest;
import com.group1.interview_management.dto.JobDTO.request.SearchJob;
import com.group1.interview_management.dto.JobDTO.response.ApiResponse;
import com.group1.interview_management.dto.JobDTO.response.JobResponse;
import com.group1.interview_management.dto.JobDTO.response.JobSearchResponse;
import com.group1.interview_management.dto.job.JobListInterviewDTO;
import com.group1.interview_management.entities.Job;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.Map;

public interface JobService {
    void saveJob(JobCreationRequest request, String isWork, BindingResult errors,
                              Authentication authenticatedUser);

    JobResponse getJobByJobId(Integer jobId);

    Page<JobSearchResponse> searchJobs(SearchJob request);

    void changeStatusJob();

    void deleteJob(Integer jobId);

    ApiResponse<?> handleExcel(Workbook workbook);

    ApiResponse<?> buildResponse(int code, String messageKey, Object t);

    List<JobListInterviewDTO> getAllInterviewJobs();

    Map<Integer, BindingResult> getErrorMapJob();

    /**
     * Get job by id
     * @param jobId: id of job
     * @param errors: BindingResult
     * @param fields: List of fields
     * @return Job
     * 
     * Author: KhoiLNM
     * Date: 2024-11-11
     */
    Job getJobByIdAndStatusIds(Integer jobId, BindingResult errors, String field, List<Integer> statusIds);
}
