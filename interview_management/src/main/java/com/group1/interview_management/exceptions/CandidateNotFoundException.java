package com.group1.interview_management.exceptions;

public class CandidateNotFoundException extends RuntimeException {
     public CandidateNotFoundException(String message) {
          super(message);
     }
}
