package com.group1.interview_management.exceptions;

public class UserNotFoundException extends RuntimeException {
     public UserNotFoundException(String messgae) {
          super(messgae);
     }
}
