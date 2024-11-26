package com.group1.interview_management.schedule;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import com.group1.interview_management.entities.User;
import com.group1.interview_management.repositories.MasterRepository;
import com.group1.interview_management.repositories.UserRepository;
import com.group1.interview_management.services.OfferService;
import com.group1.interview_management.services.impl.EmailService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.common.EmailTemplateName;
import com.group1.interview_management.entities.Interview;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OfferReminderScheduler {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final MasterRepository masterRepository;
    private final OfferService offerService;

    private static final int REMINDER_DAYS_BEFORE = 3;

    // Constructor to inject dependencies into the scheduler.
    public OfferReminderScheduler(UserRepository userRepository,
            EmailService emailService, MasterRepository masterRepository, OfferService offerService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.masterRepository = masterRepository;
        this.offerService = offerService;
    }

    /**
     * Scheduled task that runs every day at 8 AM.
     * It checks all offers and sends a reminder email if the offer's due date is
     * within the next 3 days.
     */
    @Scheduled(cron = "0 34 11 * * ?") // Cron expression to run every day at 8 AM
    public void sendDailyOfferReminders() {
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(REMINDER_DAYS_BEFORE);

        // Fetch all offers from the offer repository.
        List<Interview> upComingOffers = offerService.getAllUpcomingOffer(today, dueDate);
        if (upComingOffers.isEmpty())
            return;
        List<User> managers = userRepository.findByRoleId(ConstantUtils.MANAGER_ROLE);
        int totalReminders = upComingOffers.size();
        int successCount = 0;

        for (Interview interview : upComingOffers) {
            try {
                if (sendOfferReminder(interview, managers))
                    successCount++;
            } catch (Exception e) {
                log.error("Failed to send reminder for interview with ID: {}", interview.getInterviewId());
                e.printStackTrace();
            }
        }

        log.info("Total reminders to be sent: {}", totalReminders);
        log.info("Total reminders sent successfully: {}", successCount);

    }

    /**
     * Sends a reminder email about the offer to all managers.
     * 
     * @param offer The offer details to include in the reminder email.
     * @throws MessagingException If there is an issue sending the email.
     */
    private boolean sendOfferReminder(Interview offer, List<User> managers) throws MessagingException {
        // Retrieve all managers who will receive the reminder email.
       
        int totalManagers = managers.size();
        int count = 0;
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
            try {
                emailService.sendMail(
                        ConstantUtils.REMINDER_OFFER, // Email subject or template
                        EmailService.DEFAULT_SENDER, // Default sender email
                        manager.getEmail(), // Recipient's email address
                        EmailTemplateName.REMINDER, // Email template
                        emailProps, // Email content
                        true);

                count++;
            } catch (Exception e) {
                count--;
            }
        }
        return count < totalManagers;
    }
}
