package com.group1.interview_management.mapper;

import com.group1.interview_management.dto.JobDTO.request.JobCreationRequest;
import com.group1.interview_management.dto.JobDTO.response.JobResponse;
import com.group1.interview_management.entities.Job;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class JobMapper extends PropertyMap<JobCreationRequest, Job> {

    private final ModelMapper modelMapper;

    public JobMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    protected void configure() {
        skip(destination.getModifiedBy()); // Replace with actual field getter
        skip(destination.getModifiedDate()); // Replace with actual field getter
        skip(destination.getStartDate()); // Replace with actual field getter
        skip(destination.getCreatedBy()); // Replace with actual field getter
    }

    public Job toJob(JobCreationRequest request) {
        return modelMapper.map(request, Job.class);
    }

    public JobResponse toJobResponse(Job job) {
        return modelMapper.map(job, JobResponse.class);
    }
}
