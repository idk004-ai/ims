package com.group1.interview_management.exceptions;

public class OperationNotPermittedException extends RuntimeException {
     public OperationNotPermittedException(String msg) {
          super(msg);
     }
}
