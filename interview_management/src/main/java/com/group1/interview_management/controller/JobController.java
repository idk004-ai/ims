package com.group1.interview_management.controller;

import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.dto.JobDTO.request.JobCreationRequest;
import com.group1.interview_management.dto.JobDTO.request.SearchJob;
import com.group1.interview_management.dto.JobDTO.response.ApiResponse;
import com.group1.interview_management.dto.JobDTO.response.JobResponse;
import com.group1.interview_management.dto.JobDTO.response.JobSearchResponse;
import com.group1.interview_management.dto.Offer.OfferExportDTO;
import com.group1.interview_management.entities.Master;
import com.group1.interview_management.entities.User;
import com.group1.interview_management.services.MasterService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.experimental.NonFinal;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSheetViewType;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import com.group1.interview_management.services.JobService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/jobs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JobController {

    JobService jobService;
    MasterService masterService;
    MessageSource messageSource;

    // Show list jobs
    @GetMapping("/list-job")
    public String getJobs(Model model) {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userPrincipal = new User();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            userPrincipal = (User) authentication.getPrincipal();
        }
        model.addAttribute("user", userPrincipal);

        List<Master> statuses = masterService.getAllJobStatuses();
        model.addAttribute("statuses", statuses);
        model.addAttribute("fail", messageSource.getMessage("ME015", null, Locale.getDefault()));
        model.addAttribute("success", messageSource.getMessage("ME016", null, Locale.getDefault()));
        return "job/job_list";
    }

    // Send list jobs by search
    @PostMapping("/search")
    @ResponseBody
    public Page<JobSearchResponse> searchJob(@RequestBody SearchJob request) {
        return jobService.searchJobs(request);
    }

    //Detail job
    @GetMapping("/detail-job/{jobId}")
    public String getJobDetail(@PathVariable("jobId") Integer jobId, Model model) {
        JobResponse job = jobService.getJobByJobId(jobId);
        model.addAttribute("job", job);

        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userPrincipal = new User();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            userPrincipal = (User) authentication.getPrincipal();
        }
        model.addAttribute("user", userPrincipal);
        return "job/job_detail";
    }

    // Edit job
    @GetMapping("/edit-job/{jobId}")
    public String editJob(@PathVariable("jobId") Integer jobId, Model model) {
        JobResponse job = jobService.getJobByJobId(jobId);
        List<Master> skills = masterService.findByCategory(ConstantUtils.SKILLS);
        List<Master> benefits = masterService.findByCategory(ConstantUtils.BENEFIT);
        List<Master> levels = masterService.findByCategory(ConstantUtils.LEVEL);

        model.addAttribute("job", job);
        model.addAttribute("skills", skills);
        model.addAttribute("benefits", benefits);
        model.addAttribute("levels", levels);
        model.addAttribute("fail", messageSource.getMessage("ME015.1", null, Locale.getDefault()));
        model.addAttribute("success", messageSource.getMessage("ME016.1", null, Locale.getDefault()));

        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userPrincipal = new User();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            userPrincipal = (User) authentication.getPrincipal();
        }
        model.addAttribute("user", userPrincipal);
        return "job/create_job";
    }

    // Receive data to update job
    @PostMapping("/edit-job")
    @ResponseBody
    public ResponseEntity<?> editJob(@Valid @RequestBody JobCreationRequest request, BindingResult errors,
                                     Authentication authenticatedUser) {
        jobService.saveJob(request, "update", errors, authenticatedUser);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(errors.getAllErrors());
        }
        return ResponseEntity.ok().build();
    }

    // create job
    @GetMapping("/create-job")
    public String createJob(Model model) {
        List<Master> skills = masterService.findByCategory(ConstantUtils.SKILLS);
        List<Master> benefits = masterService.findByCategory(ConstantUtils.BENEFIT);
        List<Master> levels = masterService.findByCategory(ConstantUtils.LEVEL);

        model.addAttribute("skills", skills);
        model.addAttribute("benefits", benefits);
        model.addAttribute("levels", levels);
        model.addAttribute("fail", messageSource.getMessage("ME015", null, Locale.getDefault()));
        model.addAttribute("success", messageSource.getMessage("ME016", null, Locale.getDefault()));

        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userPrincipal = new User();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            userPrincipal = (User) authentication.getPrincipal();
        }
        if (userPrincipal.getRoleId() == 4) {
            return "redirect:/jobs/list-job";
        }
        model.addAttribute("user", userPrincipal);
        return "job/create_job";
    }

    // Receive data to create new job
    @PostMapping("/create-job")
    public ResponseEntity<?> createJob(@Valid @RequestBody JobCreationRequest request, BindingResult errors,
                                       Authentication authenticatedUser) {
        jobService.saveJob(request, "create", errors, authenticatedUser);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(errors.getAllErrors());
        }

        return ResponseEntity.ok().build();
    }

    // Delete job
    @DeleteMapping("/delete-job/{jobId}")
    public ResponseEntity<?> deleteJob(@PathVariable("jobId") Integer jobId) {
        jobService.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }

    // Receive file excel from client
    @PostMapping("/upload-excel")
    @ResponseBody
    public ApiResponse<?> uploadExcelFile(@Valid @RequestParam("file") MultipartFile file) {
        // code 400 wrong format empty
        if (file.isEmpty()) {
            return jobService.buildResponse(400, "ME040", null);
        }
        // code 415 not file Excel
        String fileName = file.getOriginalFilename();
        if (fileName == null || !(fileName.endsWith(".xls") || fileName.endsWith(".xlsx"))) {
            return jobService.buildResponse(415, "ME041", null);
        }
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            return jobService.handleExcel(workbook);
            // code 500 Error read file
        } catch (IOException e) {
            return jobService.buildResponse(500, "ME042", e);
        }
    }

    @GetMapping("/export-errors")
    public void eportErrors(HttpServletResponse response) throws IOException {

        // Create workbook
        Workbook workbook = new XSSFWorkbook();

        // Create sheet
        Sheet sheet = workbook.createSheet("Error List");

        // Set Page Break Preview
        XSSFSheet xssfSheet = (XSSFSheet) sheet;
        xssfSheet.lockSelectLockedCells(true);
        xssfSheet.getCTWorksheet().getSheetViews().getSheetViewArray(0).setView(STSheetViewType.PAGE_BREAK_PREVIEW);

        // Create styles
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setWrapText(true);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dataStyle.setAlignment(HorizontalAlignment.CENTER);

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Row", "Fields", "Error Message"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        Map<Integer, BindingResult> getErrorMapJob = jobService.getErrorMapJob();

        // Create data rows
        int rowNum = 1;
        int index = 1;
        for (Map.Entry<Integer, BindingResult> entry : getErrorMapJob.entrySet()) {
            BindingResult errors = entry.getValue();
            for (FieldError fieldError : errors.getFieldErrors()) {
                String field = fieldError.getField();
                String defaultMessage = fieldError.getDefaultMessage();
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(field);
                row.createCell(2).setCellValue(defaultMessage);
            }
        }

        // Auto size columns
        for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            sheet.autoSizeColumn(i);
        }
        // Set response content type and header
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ErrorsListJob.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }


}
