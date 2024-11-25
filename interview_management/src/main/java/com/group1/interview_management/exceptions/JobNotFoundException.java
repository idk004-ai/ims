package com.group1.interview_management.exceptions;

public class JobNotFoundException extends RuntimeException {
     public JobNotFoundException(String message) {
          super(message);
     }
}
