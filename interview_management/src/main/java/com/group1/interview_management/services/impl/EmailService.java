package com.group1.interview_management.services.impl;

import java.util.Map;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.group1.interview_management.common.EmailTemplateName;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Service
@RequiredArgsConstructor
@Data
public class EmailService {

     private final JavaMailSender mailSender;
     private final SpringTemplateEngine templateEngine;
     public static final String DEFAULT_SENDER = "minhkhoilenhat04@gmail.com";

     /**
      * Send email
      * 
      * @param subject       : Subject of email
      * @param from          : Email sender
      * @param to            : Email receiver
      * @param emailTemplate : Email template
      * @param props         : Email content -> We will create a map to store the
      *                      content of the email then pass it to the method
      * @throws MessagingException
      */
      @Async
     public void sendMail(
               String subject,
               String from,
               String to,
               EmailTemplateName emailTemplate,
               Map<String, Object> props,
               boolean multiThread) throws MessagingException {

          if (multiThread) {
               new Thread(() -> {
                    try {
                         send(subject, from, to, emailTemplate, props);
                    } catch (MessagingException e) {
                         e.printStackTrace();
                    }
               }).start();
          } else {
               send(subject, from, to, emailTemplate, props);
          }
     }

     private void send(String subject, String from, String to, EmailTemplateName emailTemplate,
               Map<String, Object> props) throws MessagingException {
          String templateName;
          if (emailTemplate == null) {
               templateName = "confirm-email";
          } else {
               templateName = emailTemplate.getName();
          }
          MimeMessage mimeMessage = mailSender.createMimeMessage();
          MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MULTIPART_MODE_MIXED,
                    UTF_8.name());

          Context context = new Context();
          context.setVariables(props);

          helper.setFrom(from);
          helper.setTo(to);
          helper.setSubject(subject);

          String template = templateEngine.process(templateName, context);

          helper.setText(template, true);

          mailSender.send(mimeMessage);
     }
}
