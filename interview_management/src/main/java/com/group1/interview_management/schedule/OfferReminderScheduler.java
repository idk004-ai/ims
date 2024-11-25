package com.group1.interview_management.schedule;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import com.group1.interview_management.entities.User;
import com.group1.interview_management.repositories.MasterRepository;
import com.group1.interview_management.repositories.OfferRepository;
import com.group1.interview_management.repositories.UserRepository;
import com.group1.interview_management.services.impl.EmailService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.common.EmailTemplateName;
import com.group1.interview_management.entities.Interview;

import jakarta.mail.MessagingException;

@Service
public class OfferReminderScheduler {

    private final OfferRepository offerRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final MasterRepository masterRepository;

    // Constructor to inject dependencies into the scheduler.
    public OfferReminderScheduler(OfferRepository offerRepository, UserRepository userRepository,
            EmailService emailService, MasterRepository masterRepository) {
        this.offerRepository = offerRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.masterRepository = masterRepository;
    }

    /**
     * Scheduled task that runs every day at 8 AM.
     * It checks all offers and sends a reminder email if the offer's due date is
     * within the next 3 days.
     */
    @Scheduled(cron = "0 0 8 * * ?") // Cron expression to run every day at 8 AM
    public void sendDailyOfferReminders() {
        // Get today's date to calculate remaining days until the due date of each
        // offer.
        LocalDate today = LocalDate.now();

        // Fetch all offers from the offer repository.
        List<Interview> offers = offerRepository.findAll();

        // Flag to track whether any reminders have been sent.
        boolean isOfferReminder = false;

        // Loop through each offer to check if it needs a reminder.
        for (Interview offer : offers) {
            LocalDate dueDate = offer.getDueDate();
            long daysUntilDueDate = ChronoUnit.DAYS.between(today, dueDate);

            // Check if the offer is active (status 1) and if the due date is within the
            // next 3 days.
            if (offer.getStatusOfferId() == 1 && daysUntilDueDate > 0 && daysUntilDueDate <= 3) {
                try {
                    // Send reminder if conditions are met.
                    sendOfferReminder(offer);
                    isOfferReminder = true;
                } catch (MessagingException e) {
                    // Log error if sending the email fails.
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Sends a reminder email about the offer to all managers.
     * 
     * @param offer The offer details to include in the reminder email.
     * @throws MessagingException If there is an issue sending the email.
     */
    private void sendOfferReminder(Interview offer) throws MessagingException {
        // Retrieve all managers who will receive the reminder email.
        List<User> managers = userRepository.findByRoleId(ConstantUtils.MANAGER_ROLE);

        // Prepare the email content to be sent to each manager.
        for (User manager : managers) {
            Map<String, Object> emailProps = new HashMap<>();

            // Add relevant offer details to the email properties map.
            emailProps.put("offerLink", "/api/v1/offer/offer-detail/" + offer.getInterviewId());
            emailProps.put("candidateName", offer.getCandidate().getName());
            emailProps.put("position",
                    masterRepository.findByCategoryAndCategoryId(ConstantUtils.DEPARTMENT, offer.getOfferdepartment())
                            .get().getCategoryValue());
            emailProps.put("duedate", offer.getDueDate());
            emailProps.put("approver", offer.getOfferCreator().getFullname());

            // Send the reminder email using the email service.
            emailService.sendMail(
                    ConstantUtils.REMINDER_OFFER, // Email subject or template
                    EmailService.DEFAULT_SENDER, // Default sender email
                    manager.getEmail(), // Recipient's email address
                    EmailTemplateName.REMINDER, // Email template
                    emailProps, // Email content
                    false); 
        }
    }
}
