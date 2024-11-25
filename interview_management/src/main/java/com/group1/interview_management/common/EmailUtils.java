package com.group1.interview_management.common;


public class EmailUtils {
     public static String extractMail(String email) {
          int index = email.indexOf('@');
          return email.substring(0, index);
     }

     public static String createURL(int id) {
          return "/api/v1/user/reset-password?id=" + id;
     }
}
