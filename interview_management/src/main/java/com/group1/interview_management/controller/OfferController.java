package com.group1.interview_management.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.group1.interview_management.dto.Offer.OfferExportDTO;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSheetViewType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.dto.OfferCreateDTO;
import com.group1.interview_management.dto.OfferDTO;
import com.group1.interview_management.dto.OfferSearchDTO;
import com.group1.interview_management.dto.UserDTO;
import com.group1.interview_management.dto.Offer.OfferDetailDTO;
import com.group1.interview_management.entities.Master;
import com.group1.interview_management.services.InterviewService;
import com.group1.interview_management.services.MasterService;
import com.group1.interview_management.services.OfferService;
import com.group1.interview_management.services.UserService;
import com.group1.interview_management.entities.User;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/offer")
@Secured({ "ROLE_ADMIN", "ROLE_MANAGER", "ROLE_RECRUITER" })

@Slf4j
public class OfferController {

    @Autowired
    private OfferService offerService;

    @Autowired
    private MasterService masterService;

    @Autowired
    private InterviewService interviewService;
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserService userService;

    /**
     * Get all candidates
     *
     * @param model model need to display in offer list page
     * @return offer_list.html
     */
    @GetMapping("/offer-list")
    public String offer_list(Model model) {
        List<Master> statuses = masterService.findByCategory("OFFER_STATUS");
        List<Master> departments = masterService.findByCategory("DEPARTMENT");

        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userPrincipal = new User();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            userPrincipal = (User) authentication.getPrincipal();
        }

        model.addAttribute("statuses", statuses);
        model.addAttribute("departments", departments);
        model.addAttribute("user", userPrincipal);

        return "offer/offer_list";
    }

    /**
     * Searches for offers by criteria provided in OfferSearchDTO.
     * Search Offer by name
     *
     * @param request
     * @return List of Offer
     */
    @ResponseBody
    @PostMapping("/search-offer")
    public Page<OfferDTO> searchOffers(@RequestBody OfferSearchDTO request) {
        return offerService.searchOffers(request);
    }

    /**
     * Displays the form to create a new offer, including available contract types,
     * departments, and a list of interviews that do not yet have an associated
     * offer.
     * 
     * @param model The Model object to add attributes for the view.
     * @return The view name for the offer creation page.
     */
    @GetMapping("/add-new-offer")
    public String showOfferCreateForm(Model model) {
        // Retrieve lists of contract types and departments by their categories.
        List<Master> contracttypes = masterService.findByCategory(ConstantUtils.CONTRACT_TYPE);
        List<Master> departments = masterService.findByCategory(ConstantUtils.DEPARTMENT);

        // Retrieve a list of interviews that currently have no associated offer.
        List<OfferCreateDTO> interviews = interviewService.getinterviewnulloffer();

        // Get the current authenticated user to pass user information to the view.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userPrincipal = new User();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            userPrincipal = (User) authentication.getPrincipal();
        }

        // Find the Manager role in the master data. If not found, throw an exception.
        Optional<Master> manager = masterService.findByCategoryAndValue(ConstantUtils.USER_ROLE, ConstantUtils.MANAGER);
        if (manager.isEmpty()) {
            throw new RuntimeException("Manager not found");
        }

        // Retrieve a list of all users with the Manager role to display as options in
        // the form.
        List<UserDTO> managers = userService.getAllUsersByRole(manager.get().getId());

        String errorMessage = messageSource.getMessage("ME023", null, Locale.getDefault());
        String successMessage = messageSource.getMessage("ME024", null, Locale.getDefault());

        // Add contract types, departments, interviews, current user, and manager,
        // etc...
        // to the model.
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("successMessage", successMessage);
        model.addAttribute("contracttypes", contracttypes);
        model.addAttribute("departments", departments);
        model.addAttribute("interviews", interviews);
        model.addAttribute("user", userPrincipal);
        model.addAttribute("managers", managers);

        // Return the view name for the offer creation form.
        return "offer/offer_create";
    }

    /**
     * Handles the creation of a new offer.
     * This method processes the incoming request, validates the provided data,
     * and saves the offer information if the validation is successful.
     * 
     * @param offerCreateDTO The data transfer object containing the offer details.
     * @param result         The binding result that holds any validation errors.
     * @return A ResponseEntity containing the status of the offer creation process.
     * @throws MessagingException If an error occurs while sending any messages
     *                            (e.g., email).
     */
    @PostMapping("/offer_create")
    public ResponseEntity<?> createOffer(@Valid @RequestBody OfferCreateDTO offerCreateDTO, BindingResult result)
            throws MessagingException {
        // Check if there are any validation errors
        if (result.hasErrors()) {
            // If validation fails, print an error message and return a bad request response
            // with errors
            System.out.println("Offer not created");
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        // If validation passes, save the offer using the offer service
        offerService.saveOffer(offerCreateDTO);

        // Return a success response after the offer is created
        return ResponseEntity.ok("Offer created successfully");
    }

    /**
     * Retrieves information of an offer by its ID.
     * This method processes the incoming GET request and returns the details of the
     * offer
     * based on the provided offer ID.
     * 
     * @param id The ID of the offer to be retrieved.
     * @return The details of the offer in the form of an OfferCreateDTO.
     */
    @GetMapping("/get-offer-info")
    @ResponseBody
    public OfferCreateDTO getoffer(@RequestParam(value = "id") Integer id) {
        // Call the interviewService to retrieve the offer information by its ID
        return interviewService.getinterviewByID(id);
    }

    /**
     * Displays the offer details page with user role-based permissions for viewing.
     * This method handles the request for displaying offer details and checks the
     * user's role-based permissions for viewing the offer.
     * 
     * @param id    The ID of the offer to be displayed.
     * @param model The Model object to add attributes for the view.
     * @return The view name for the offer detail page.
     */
    @GetMapping("/offer-detail/{id}")
    public String offer_detail(@PathVariable Integer id, Model model) {
        // Retrieve the offer details by ID using the service layer.
        OfferDetailDTO offer = offerService.getOfferById(id);

        // Get the current authentication context to determine the logged-in user's
        // role.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userPrincipal = new User();

        // Flags to check user permissions based on role.
        boolean isManagerOrAdmin = false; // True if the user is a Manager or Admin.
        boolean isNotEnterview = false; // True if the user is a Manager, Admin, or Recruiter.

        // Check if the user is authenticated and not an anonymous user.
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            userPrincipal = (User) authentication.getPrincipal();

            // Get the user's role ID to set permissions.
            Integer roleId = userPrincipal.getRoleId();
            if (roleId != null) {
                // If role ID is 1 (Admin) or 2 (Manager), set isManagerOrAdmin to true.
                if (roleId == 1 || roleId == 2) {
                    isManagerOrAdmin = true;
                }
                // If role ID is 1 (Admin), 2 (Manager), or 3 (Recruiter), set isNotEnterview to
                // true.
                if (roleId == 1 || roleId == 2 || roleId == 3) {
                    isNotEnterview = true;
                }
            }
        }

        // Add the offer details and permission flags to the model for the view.
        model.addAttribute("offer", offer);
        model.addAttribute("isManagerOrAdmin", isManagerOrAdmin);
        model.addAttribute("user", userPrincipal);
        model.addAttribute("isNotEnterview", isNotEnterview);

        // Return the view name for the offer detail page.
        return "offer/offer_detail";
    }

    /**
     * Handles the editing of an existing offer.
     * 
     * This method receives an offer update request, validates the input,
     * and returns an appropriate response based on whether the validation
     * passes or fails.
     * 
     * @param offerCreateDTO The data transfer object containing the offer details
     *                       to be updated.
     * @param result         The binding result to capture any validation errors.
     * @return A response indicating the success or failure of the offer update.
     */
    @PostMapping("/offer_edit")
    public ResponseEntity<?> editOffer(@Valid @RequestBody OfferCreateDTO offerCreateDTO, BindingResult result) {
        // If there are validation errors, log the failure and return a bad request
        // response.
        if (result.hasErrors()) {
            System.out.println("Offer edit fail");
            // Return a bad request response with the validation errors.
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        // If validation passes, return a success response indicating the offer was
        // updated successfully.
        return ResponseEntity.ok("Update Offer successfully");
    }

    /**
     * Export offer to excel
     *
     * @param periodFrom - Date from
     * @param periodTo   - Date to
     * @param response   - HttpServletResponse
     * @param locale     - Locale
     * @throws IOException - IOException
     */
    @GetMapping("/export_offer")
    public void exportToExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            HttpServletResponse response, Locale locale) throws IOException {

        // Check if periodFrom is after periodTo
        if (periodFrom.isAfter(periodTo)) {
            String messageDate = messageSource.getMessage("ME036", null, locale);

            // Set status to bad request
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            // Write message to response
            response.getWriter().write(messageDate);
            return;
        }
        // Get offer by period
        List<OfferExportDTO> offerDetails = offerService.getOfferByDueDate(periodFrom, periodTo);

        // Check if offerDetails is empty
        if (offerDetails.isEmpty()) {
            String message = messageSource.getMessage("ME025", null, locale);

            // Set status to bad request
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            // Write message to response
            response.getWriter().write(message);
            return;
        }

        // Create workbook
        Workbook workbook = new XSSFWorkbook();

        // Create sheet
        Sheet sheet = workbook.createSheet("Offer Details");

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
        String[] headers = { "No.", "Candidate ID", "Candidate name", "Approved by", "Contract type", "Position",
                "Level", "Department", "Recruiter owner", "Interviewer", "Contract start from",
                "Contract to", "Basic salary", "Interview notes", "Notes" };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Create data rows
        int rowNum = 1;
        int index = 1;
        for (OfferExportDTO offer : offerDetails) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(index++);
            row.createCell(1).setCellValue(offer.getCandidateId());
            row.createCell(2).setCellValue(offer.getCandidateName());
            row.createCell(3).setCellValue(offer.getApprovedBy());
            row.createCell(4).setCellValue(offer.getContractType());
            row.createCell(5).setCellValue(offer.getPosition());
            row.createCell(6).setCellValue(offer.getLevel());
            row.createCell(7).setCellValue(offer.getDepartment());
            row.createCell(8).setCellValue(offer.getRecruiter());
            row.createCell(9).setCellValue(offer.getInterviewer());
            row.createCell(10).setCellValue(offer.getContractFrom().toString()); // Adjusted for date format
            row.createCell(11).setCellValue(offer.getContractTo().toString()); // Adjusted for date format
            row.createCell(12).setCellValue(offer.getSalary());
            row.createCell(13).setCellValue(offer.getInterviewNote());
            row.createCell(14).setCellValue(offer.getOfferNote());

            // Apply data style to all cells in the row
            for (int i = 0; i < headers.length; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }

        // Auto size columns
        for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Set file name
        String dateFrom = periodFrom.toString();
        String dateTo = periodTo.toString();
        String fileName = "OfferList_" + dateFrom + "_to_" + dateTo + ".xlsx";

        // Set response content type and header
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * Displays the edit form for an offer by its ID.
     * 
     * @param id - id
     * 
     */
    /**
     * Displays the offer edit form with necessary data pre-populated.
     * 
     * This method retrieves the details of an offer by its ID, along with relevant
     * contract types, departments, and managers. It also adds error and success
     * messages to the model, which will be displayed in the offer edit form view.
     * 
     * @param id    The ID of the offer to be edited.
     * @param model The Model object to add attributes for the view.
     * @return The name of the view to display the offer edit form.
     */
    @GetMapping("/offer-edit/{id}")
    public String showOfferEditForm(@PathVariable("id") Integer id, Model model) {
        // Retrieve the offer details by ID using the service layer.
        OfferCreateDTO offer = offerService.getOfferCreateDTOById(id);

        // Retrieve the list of contract types and departments from the master service.
        List<Master> contracttypes = masterService.findByCategory(ConstantUtils.CONTRACT_TYPE);
        List<Master> departments = masterService.findByCategory(ConstantUtils.DEPARTMENT);

        // Retrieve the manager role from the master service to filter users by role.
        Optional<Master> manager = masterService.findByCategoryAndValue(ConstantUtils.USER_ROLE, ConstantUtils.MANAGER);
        // Get all users who are managers using the manager's role ID.
        List<UserDTO> managers = userService.getAllUsersByRole(manager.get().getId());

        // Retrieve localized error and success messages.
        String errorMessage = messageSource.getMessage("ME013", null, Locale.getDefault());
        String successMessage = messageSource.getMessage("ME014", null, Locale.getDefault());

        // Add the necessary attributes to the model for the view.
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("successMessage", successMessage);
        model.addAttribute("contracttypes", contracttypes);
        model.addAttribute("offer", offer);
        model.addAttribute("managers", managers);
        model.addAttribute("departments", departments);

        // Return the view name for the offer edit page.
        return "offer/offer_edit";
    }

    /**
     * Approves an offer by its ID.
     * This method updates the offer status to approved using the offer service.
     * 
     * @param id The ID of the offer to be approved.
     * @return ResponseEntity with a success message.
     */
    @PostMapping("/approve/{id}")
    public ResponseEntity<?> approveOffer(@PathVariable Integer id) {
        // Approve the offer by passing its ID to the service layer.
        offerService.approveOffer(id);
        // Return a success response after the offer has been approved.
        return ResponseEntity.ok("Offer approved successfully");
    }

    /**
     * Rejects an offer by its ID.
     * This method updates the offer status to rejected using the offer service.
     * 
     * @param id The ID of the offer to be rejected.
     * @return ResponseEntity with a success message.
     */
    @PostMapping("/reject/{id}")
    public ResponseEntity<?> rejectOffer(@PathVariable Integer id) {
        // Reject the offer by passing its ID to the service layer.
        offerService.rejectOffer(id);
        // Return a success response after the offer has been rejected.
        return ResponseEntity.ok("Offer rejected successfully");
    }

    /**
     * Sends an offer by its ID.
     * This method updates the offer status to sent using the offer service.
     * 
     * @param id The ID of the offer to be sent.
     * @return ResponseEntity with a success message.
     */
    @PostMapping("/sentOffer/{id}")
    public ResponseEntity<?> sentOffer(@PathVariable Integer id) {
        // Send the offer by passing its ID to the service layer.
        offerService.sentOffer(id);
        // Return a success response after the offer has been sent.
        return ResponseEntity.ok("Offer sent successfully");
    }

    /**
     * Accepts an offer by its ID.
     * This method updates the offer status to accepted using the offer service.
     * 
     * @param id The ID of the offer to be accepted.
     * @return ResponseEntity with a success message.
     */
    @PostMapping("/accept/{id}")
    public ResponseEntity<?> acceptOffer(@PathVariable Integer id) {
        // Accept the offer by passing its ID to the service layer.
        offerService.acceptOffer(id);
        // Return a success response after the offer has been accepted.
        return ResponseEntity.ok("Offer accepted successfully");
    }

    /**
     * Declines an offer by its ID.
     * This method updates the offer status to declined using the offer service.
     * 
     * @param id The ID of the offer to be declined.
     * @return ResponseEntity with a success message.
     */
    @PostMapping("/declined/{id}")
    public ResponseEntity<?> declinedOffer(@PathVariable Integer id) {
        // Decline the offer by passing its ID to the service layer.
        offerService.declinedOffer(id);
        // Return a success response after the offer has been declined.
        return ResponseEntity.ok("Offer declined successfully");
    }

    /**
     * Cancels an offer by its ID.
     * This method updates the offer status to canceled using the offer service.
     * 
     * @param id The ID of the offer to be canceled.
     * @return ResponseEntity with a success message.
     */
    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancelOffer(@PathVariable Integer id) {
        // Cancel the offer by passing its ID to the service layer.
        offerService.cancelOffer(id);
        // Return a success response after the offer has been canceled.
        return ResponseEntity.ok("Offer canceled successfully");
    }

}
