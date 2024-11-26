package com.group1.interview_management.schedule;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.common.EmailTemplateName;
import com.group1.interview_management.config.DomainConfig;
import com.group1.interview_management.entities.Interview;
import com.group1.interview_management.entities.InterviewAssignment;
import com.group1.interview_management.entities.User;
import com.group1.interview_management.repositories.InterviewRepository;
import com.group1.interview_management.services.InterviewService;
import com.group1.interview_management.services.impl.EmailService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterviewScheduleReminder {

     private final EmailService emailService;
     private final InterviewService interviewService;
     private final InterviewRepository interviewRepository;
     private static final int REMINDER_DAYS_BEFORE = 3;
     private static final int MAX_RETRIES = 3;
     private final DomainConfig domainConfig;

     private List<Interview> getUpcomingInterveiws(LocalDate startDate, LocalDate endDate) {
          return interviewService.getAllInterview(startDate, endDate);
     }

     @Scheduled(cron = "0 0 8 * * ?")
     public void sendEmailReminder() throws MessagingException {
          LocalDate currenDate = LocalDate.now();
          LocalDate dueDate = currenDate.plusDays(REMINDER_DAYS_BEFORE);

          List<Interview> upcomingInterviews = getUpcomingInterveiws(currenDate, dueDate);
          if (upcomingInterviews.isEmpty()) {
               return;
          }

          int totalReminders = 0;
          int successCount = 0;

          for (Interview interview : upcomingInterviews) {
               try {
                    int sentCount = processInterviewReminders(interview);
                    successCount += sentCount;
                    totalReminders += interview.getInterviewAssignments().size();
               } catch (Exception e) {
                    log.error("Failed to send reminder for interview with ID: {}", interview.getInterviewId());
                    e.printStackTrace();
               }
          }

          log.info("Total reminders to be sent: {}", totalReminders);
          log.info("Total reminders sent successfully: {}", successCount);

     }

     private int processInterviewReminders(Interview interview) {
          int successCount = 0;
          LocalDate interviewDate = interview.getSchedule();
          long daysUntilInterview = ChronoUnit.DAYS.between(LocalDate.now(), interviewDate);
          boolean allEmailSent = true;

          for (InterviewAssignment assignment : interview.getInterviewAssignments()) {
               Map<String, Object> baseProps = prepareEmailProperties(interview, daysUntilInterview);
               if (sendReminderToInterviewer(assignment.getInterviewer(), baseProps, interview)) {
                    successCount++;
               } else {
                    allEmailSent = false;
               }
          }

          if (allEmailSent && successCount > 0) {
               interview.setStatusInterviewId(ConstantUtils.INTERVIEW_STATUS_INVITED);
               interviewRepository.save(interview);
          }

          return successCount;
     }

     private Map<String, Object> prepareEmailProperties(Interview interview, long daysUntilInterview) {
          Map<String, Object> props = new HashMap<>();
          props.put("candidateName", interview.getCandidate().getName());
          props.put("jobTitle", interview.getJob().getTitle());
          props.put("scheduleDate", interview.getSchedule());
          props.put("startTime", interview.getStartTime());
          props.put("endTime", interview.getEndTime());
          props.put("daysUntilInterview", daysUntilInterview);
          props.put("location", interview.getLocation());
          props.put("meetingLink", interview.getMeetingId());
          props.put("interviewURL", domainConfig.domainUrl() + "/api/v1/interview/view/" + interview.getInterviewId());
          return props;
     }

     private boolean sendReminderToInterviewer(User interviewer,
               Map<String, Object> baseProps,
               Interview interview) {
          try {
               Map<String, Object> emailProps = new HashMap<>(baseProps);
               emailProps.put("interviewerName", interviewer.getFullname());
               emailProps.put("email", interviewer.getEmail());

               retrySendMail(ConstantUtils.INTERVIEW_SCHEDULE_REMINDER, interviewer.getEmail(), EmailTemplateName.INTERVIEW_SCHEDULE_REMINDER, emailProps);

               return true;

          } catch (MessagingException e) {
               return false;
          }
     }
     @Retryable(value = MessagingException.class, maxAttempts = MAX_RETRIES, backoff = @Backoff(delay = 1000))
     private void retrySendMail(String subject, String recipientEmail, EmailTemplateName templateName, Map<String, Object> props)
               throws MessagingException {
          emailService.sendMail(
                    subject,
                    EmailService.DEFAULT_SENDER,
                    recipientEmail,
                    templateName,
                    props,
                    true);
     }

     @Scheduled(cron = "0 0 14 * * ?")
     public void cancelOverdueSchedules() throws MessagingException {
          LocalDate currentDate = LocalDate.now();

          List<Interview> overdueSchedules = interviewRepository.findByScheduleLessThanAndStatusInterviewIdNotIn(
                    currentDate,
                    List.of(
                              ConstantUtils.INTERVIEW_STATUS_CANCELLED,
                              ConstantUtils.INTERVIEW_STATUS_CLOSED));

          if (overdueSchedules.isEmpty()) {
               log.info("No overdue interview schedules found to cancel");
               return;
          }

          int cancelledCount = 0;
          for (Interview interview : overdueSchedules) {
               try {
                    interview.setStatusInterviewId(ConstantUtils.INTERVIEW_STATUS_CANCELLED);
                    interviewRepository.save(interview);

                    // Send cancellation notifications
                    notifyInterviewCancellation(interview);

                    cancelledCount++;
                    log.info("Successfully cancelled overdue interview ID: {}", interview.getInterviewId());
               } catch (Exception e) {
                    throw new MessagingException("Failed to cancel interview ID: " + interview.getInterviewId());
               }
          }
          log.info("Cancelled {} overdue interviews", cancelledCount);
     }

     private void notifyInterviewCancellation(Interview interview) throws MessagingException {
          Map<String, Object> props = new HashMap<>();
          props.put("candidateName", interview.getCandidate().getName());
          props.put("jobTitle", interview.getJob().getTitle());
          props.put("scheduleDate", interview.getSchedule());
          props.put("reason", "Interview date has passed");

          // Notify each interviewer
          for (InterviewAssignment assignment : interview.getInterviewAssignments()) {
               User interviewer = assignment.getInterviewer();
               props.put("interviewerName", interviewer.getFullname());
               retrySendMail(ConstantUtils.INTERVIEW_CANCELLATION_NOTICE, interviewer.getEmail(), EmailTemplateName.INTERVIEW_SCHEDULE_CANCELLATION, props);
          }
     }
}
