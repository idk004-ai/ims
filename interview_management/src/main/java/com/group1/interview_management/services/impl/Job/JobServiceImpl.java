package com.group1.interview_management.services.impl.Job;

import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.dto.JobDTO.request.JobCreationRequest;
import com.group1.interview_management.dto.JobDTO.request.SearchJob;
import com.group1.interview_management.dto.JobDTO.response.ApiResponse;
import com.group1.interview_management.dto.JobDTO.response.JobResponse;
import com.group1.interview_management.dto.JobDTO.response.JobSearchResponse;
import com.group1.interview_management.dto.job.JobListInterviewDTO;
import com.group1.interview_management.entities.Job;
import com.group1.interview_management.mapper.JobMapper;
import com.group1.interview_management.repositories.JobRepository;
import com.group1.interview_management.services.JobService;
import com.group1.interview_management.services.MasterService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@PropertySource("classpath:messages.properties")
public class JobServiceImpl implements JobService {
    JobRepository jobRepository;
    JobMapper jobMapper;
    MasterService masterService;
    BatchJob batchJob;
    MessageSource messageSource;
    ModelMapper mapper;
    Validator validator;

    @NonFinal
    Map<Integer, BindingResult> errorMapJob;

    //Create job
    public void saveJob(JobCreationRequest request, String isWork, BindingResult errors,
                        Authentication authenticatedUser) {

        //Get all fields declared in JobCreationRequest class
        Field[] fields = JobCreationRequest.class.getDeclaredFields();

        // Validate general fields and populate errors if any
        checkGeneralError(request, fields, errors);

        if (isWork.equals("create")) {
            checkForCreate(request, fields, errors);
        } else {
            checkForUpdate(request, fields, errors);
        }

        // Return early if there are validation errors
        if (errors.hasErrors()) {
            return;
        }

        // Map JobCreationRequest to Job entity
        Job job = jobMapper.toJob(request);

        // Change list to string (skill, benefit, level)
        changeListToString(job, request);

        // Set default job status to "1" is "Draft"
        if (isWork.equals("create")) {
            job.setStatusJobId(1);
        } else {
            Job j = jobRepository.getJobByJobId(job.getJobId());
            job.updateCreatedBy(j.getCreatedBy());
            job.updateCreatedDate(j.getCreatedDate());
            if (!compareDate(job.getStartDate(), LocalDate.now())) {
                job.setStatusJobId(1);
            }else if (!compareDate(job.getEndDate(), LocalDate.now())) {
                job.setStatusJobId(2);
            }else {
                job.setStatusJobId(3);
            }
        }

        //save new job into database
        jobRepository.save(job);
    }

    //check for update
    private void checkForUpdate(JobCreationRequest request, Field[] fields, BindingResult errors) {
        if (errors.getFieldError("startDate") == null && request.getStartDate().isBefore(jobRepository.getStartDateByJobId(request.getJobId()))) {
            String errorMessage = messageSource.getMessage("ME017.1", null, Locale.getDefault());
            errors.rejectValue(fields[4].getName(), "ME017.1", errorMessage);
        }
    }

    //Search job
    public Page<JobSearchResponse> searchJobs(SearchJob request) {

        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());
        // get list jobs
        Page<JobSearchResponse> jobPage = jobRepository.searchByKeyword(request.getQuery(), request.getStatus(),
                pageable,
                ConstantUtils.JOB_STATUS);

        List<JobSearchResponse> modifiedList = jobPage.stream()
                //Change String id to String value
                .peek(job -> job.setSkills(masterService.getListCategoryValue(job.getSkills(), ConstantUtils.SKILLS)))
                .peek(job -> job.setLevel(masterService.getListCategoryValue(job.getLevel(), ConstantUtils.SKILLS)))
                .collect(Collectors.toList());

        // Return new Page with list changed
        return new PageImpl<>(modifiedList, pageable, jobPage.getTotalElements());
    }

    //Change from List<String> to String for job(skills, benefits, levels)
    private void changeListToString(Job job, JobCreationRequest request) {
        String skills = request.getSkills().toString();
        job.setSkills(skills.substring(1, skills.length() - 1));
        String benefits = request.getBenefits().toString();
        job.setBenefits(benefits.substring(1, benefits.length() - 1));
        String level = request.getLevel().toString();
        job.setLevel(level.substring(1, level.length() - 1));
    }

    //Get job detail by jobId and response JobResponse
    public JobResponse getJobByJobId(Integer jobId) {
        Job job = jobRepository.getJobByJobId(jobId);
        return getJobResponse(job);
    }

    //Change Job to JobResponse
    private JobResponse getJobResponse(Job job) {
        JobResponse jobResponse = jobMapper.toJobResponse(job);
        jobResponse.setSkills(masterService.getListCategoryValue(jobResponse.getSkills(), ConstantUtils.SKILLS));
        jobResponse.setBenefits(masterService.getListCategoryValue(jobResponse.getBenefits(), ConstantUtils.BENEFIT));
        jobResponse.setLevel(masterService.getListCategoryValue(jobResponse.getLevel(), ConstantUtils.LEVEL));
        switch (jobResponse.getStatus()) {
            case "1":
                jobResponse.setStatus(ConstantUtils.JOB_1);
                break;
            case "2":
                jobResponse.setStatus(ConstantUtils.JOB_2);
                break;
            case "3":
                jobResponse.setStatus(ConstantUtils.JOB_3);
                break;
        }
        return jobResponse;
    }

    //Change status of jobs
    public void changeStatusJob() {
        jobRepository.openJob(LocalDate.now());
        jobRepository.closeJob(LocalDate.now());
    }

    // Delete Job
    public void deleteJob(Integer jobId) {
        Job job = jobRepository.getJobByJobId(jobId);
        job.setDeleteFlag(true);
        jobRepository.save(job);
    }

    //Handle excel
    public ApiResponse<?> handleExcel(@Valid Workbook workbook) {
        try {
            int numberOfSheets = workbook.getNumberOfSheets();
            // code 400 wrong format file excel 1 sheet
            if (numberOfSheets != 1) {
                return buildResponse(400, "ME044", null);
            }
            Sheet sheet = workbook.getSheetAt(0);
            Row firstRow = sheet.getRow(0);

            // Setup first row
            String[] expectedHeaders = {"No.", "Job Title", "Required skills", "Start date",
                    "End date", "Salary from", "Salary to", "Benefits", "Working address", "Level", "Description"};
            // check wrong format first row
            int j = 0;
            for (String s : expectedHeaders) {
                if (!s.trim().equalsIgnoreCase(firstRow.getCell(j).getStringCellValue().trim())) {
                    return buildResponse(400, "ME045", null);
                }
                j++;
            }
            //Get all Skills, Benefits, Levels
            List<String> skills = masterService.findByCategory(ConstantUtils.SKILLS).stream()
                    .map(master -> master.getCategoryId().toString())
                    .toList();
            List<String> levels = masterService.findByCategory(ConstantUtils.LEVEL).stream()
                    .map(master -> master.getCategoryId().toString())
                    .toList();
            List<String> benefits = masterService.findByCategory(ConstantUtils.BENEFIT).stream()
                    .map(master -> master.getCategoryId().toString())
                    .toList();

            List<Job> jobs = new ArrayList<>();
            Field[] fields = JobCreationRequest.class.getDeclaredFields();
            Map<Integer, BindingResult> errorMap = new HashMap<>();
            Set<String> map = new HashSet<>();
            Set<ConstraintViolation<JobCreationRequest>> violations;

            // Check record
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.isFormatted() || row.getZeroHeight())
                    continue;

                String title = getStringFromCell(row.getCell(1));// Handle "Title"
                String skill1 = getStringFromCell(row.getCell(2));// Handle "Skills"
                String startDate = getStringFromCell(row.getCell(3));// Handle "Start Date"
                String endDate = getStringFromCell(row.getCell(4));// Handle "End Date"
                String salaryFrom = getStringFromCell(row.getCell(5));// Handle "Salary From"
                String salaryTo = getStringFromCell(row.getCell(6));// Handle "Salary To"
                String benefit1 = getStringFromCell(row.getCell(7));// Handle "Benefits"
                String workAddress = getStringFromCell(row.getCell(8));// Handle "workAddress"
                String level1 = getStringFromCell(row.getCell(9));// Handle "Levels"
                String description = getStringFromCell(row.getCell(10));// Handle "description"

                // Check empty row
                if ("".equals(title) && "".equals(skill1) &&
                        "".equals(startDate) && "".equals(endDate) &&
                        "".equals(salaryFrom) && "".equals(salaryTo) &&
                        "".equals(benefit1) && "".equals(workAddress) &&
                        "".equals(level1) && "".equals(description)) {
                    continue;
                }

                // create Job request
                JobCreationRequest request = JobCreationRequest.builder()
                        .title(title)
                        .salaryFrom(getDoubleFromString(salaryFrom))
                        .salaryTo(getDoubleFromString(salaryTo))
                        .startDate(getLocalDateFromString(startDate))
                        .endDate(getLocalDateFromString(endDate))
                        .description(description)
                        .workingAddress(workAddress)
                        .statusJob("1")
                        .skills(getListFromString(skill1))
                        .benefits(getListFromString(benefit1))
                        .level(getListFromString(level1))
                        .build();

                // catch error
                violations = validator.validate(request);
                // save error to custom
                BindingResult errors = new BeanPropertyBindingResult(request, "request");
                if (!violations.isEmpty()) {
                    for (ConstraintViolation<JobCreationRequest> violation : violations) {
                        // filter error duplicate
                        if (!map.contains(violation.getPropertyPath().toString())) {
                            map.add(violation.getPropertyPath().toString());
                            errors.rejectValue(
                                    violation.getPropertyPath().toString(),
                                    null,
                                    violation.getMessage()
                            );
                        }
                    }
                }
                map.clear();
                violations.clear();

                // check data (skill, benefit, level) with database
                checkListExistForExcel(skill1, skills, "Skills", fields[8].getName(), errors);
                checkListExistForExcel(benefit1, benefits, "Benefits", fields[7].getName(), errors);
                checkListExistForExcel(level1, levels, "Levels", fields[6].getName(), errors);
                checkGeneralError(request, fields, errors);
                checkForCreate(request, fields, errors);
                log.info(errors.toString());

                // Have error put into map error
                if (errors.hasErrors()) {
                    errorMap.put(i, errors);
                }

                // If don't have errors, so add one into list
                if (errorMap.isEmpty()) {
                    jobs.add(Job.builder()
                            .title(title)
                            .skills(skill1)
                            .startDate(getLocalDateFromString(startDate))
                            .endDate(getLocalDateFromString(endDate))
                            .salaryFrom(getDoubleFromString(salaryFrom))
                            .salaryTo(getDoubleFromString(salaryTo))
                            .benefits(benefit1)
                            .workingAddress(workAddress)
                            .level(level1)
                            .description(description)
                            .statusJobId(1)
                            .build());
                }
            }
            // code 422 error data file excel
            if (!errorMap.isEmpty()) {
                errorMapJob = errorMap;
                return buildResponse(422, "ME015", "errorMap");
            }
            // code 200 If there is no error, then save and return message
//            batchJob.saveJobsInBatch(jobs);
            return buildResponse(200, "ME016", jobs);
            //  code 500 error server
        } catch (NullPointerException n) {
            return buildResponse(500, "ME046", n);
        } catch (IllegalStateException i) {
            return buildResponse(500, "ME047", i);
        } catch (DateTimeParseException d) {
            return buildResponse(500, "ME042", d);
        }
    }

    @Override
    public Map<Integer, BindingResult> getErrorMapJob() {
        return errorMapJob;
    }


    //Create response to return for client
    public ApiResponse<?> buildResponse(int code, String messageKey, Object t) {
        return ApiResponse.builder()
                .code(code)
                .message(messageSource.getMessage(messageKey, null, Locale.getDefault()))
                .result(t)
                .build();
    }

    //check (skill, benefit, level) for excel
    private void checkListExistForExcel(String listString, List<String> list, String name, String field, BindingResult errors) {
        for (String str : listString.split(",")) {
            if (!list.contains(str.trim())) {
                String errorMessage = messageSource.getMessage("ME048", null, Locale.getDefault());
                errors.rejectValue(field, "ME048", errorMessage.replace("{0}", name));
            }
        }
    }

    //change String to LocalDate
    private LocalDate getLocalDateFromString(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    //change String to double
    private Double getDoubleFromString(String num) {
        try {
            return Double.parseDouble(num);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    //Get data from cell for Excel
    private String getStringFromCell(Cell cell) {
        if (cell != null) {
            if (cell.getCellType() == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    return sdf.format(cell.getDateCellValue());
                } else {
                    try {
                        return String.valueOf((long) cell.getNumericCellValue()).trim();
                    } catch (Exception e) {
                        return "0";
                    }
                }
            } else {
                return cell.getStringCellValue().trim();
            }
        }
        return "";
    }

    //change String to List
    private List<String> getListFromString(String listString) {
        if (listString == null || listString.isEmpty()) {
            return null;
        } else {
            return Arrays.asList(listString.split(","));
        }
    }

    //Check error for (create and edit)
    private void checkGeneralError(JobCreationRequest request, Field[] fields, BindingResult errors) {
        if (errors.getFieldError("salaryTo") == null && request.getSalaryTo() < request.getSalaryFrom()) {
            String errorMessage = messageSource.getMessage("ME037", null, Locale.getDefault());
            errors.rejectValue(fields[3].getName(), "ME037", errorMessage);
        }

        if (errors.getFieldError("startDate") == null && errors.getFieldError("endDate") == null && !compareDate(request.getStartDate(), request.getEndDate())) {
            String errorMessage = messageSource.getMessage("ME018", null, Locale.getDefault());
            errors.rejectValue(fields[5].getName(), "ME018", errorMessage);
        }
    }

    private void checkForCreate(JobCreationRequest request, Field[] fields, BindingResult errors) {
        if (errors.getFieldError("startDate") == null && request.getStartDate().isBefore(LocalDate.now())) {
            String errorMessage = messageSource.getMessage("ME017", null, Locale.getDefault());
            errors.rejectValue(fields[4].getName(), "ME017", errorMessage);
        }
    }

    //Validate time
    private boolean compareDate(LocalDate start, LocalDate end) {
        return start.isEqual(end) || start.isBefore(end);
    }

    @Override
    public List<JobListInterviewDTO> getAllInterviewJobs() {
        List<Job> jobs = jobRepository.findAllByStatusJobId(ConstantUtils.JOB_OPEN);
        return jobs.stream().map(job -> mapper.map(job, JobListInterviewDTO.class)).toList();
    }

    @Override
    public Job getJobByIdAndStatusIds(Integer jobId, BindingResult errors, String field, List<Integer> statusIds) {
        Job job = jobRepository.findJobByIdAndStatusIds(jobId, statusIds).orElse(null);
        if (job == null && errors.getFieldError(field) == null) {
            String errorMessage = messageSource.getMessage("ME008", null, Locale.getDefault());
            errors.rejectValue(field, "ME008", errorMessage);
        }
        return job;
    }

}
