package com.group1.interview_management.common;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
     ACTIVATE_ACCOUNT("activate_account"),
     RESET_PASSWORD_EMAIL("reset_password_mail"),
     CREATE_ACCOUNT_MAIL("create_account_mail"),
     INTERVIEW_SCHEDULE_REMINDER("/interview/interview-reminder"),
     INTERVIEW_SCHEDULE_CANCELLATION("/interview/interview-cancellation"),
     REMINDER("reminder");
     private final String name;

     EmailTemplateName(String name) {
          this.name = name;
     }
}
