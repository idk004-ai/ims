package com.group1.interview_management.common;

import java.util.List;

public class StatusValidator {

     /**
      * Validates if the given status is in the list of disapproved statuses
      * 
      * @param currentStatus    The status to check
      * @param approvedStatuses List of invalid/disapproved statuses
      * @return true if status is invalid, false otherwise
      */
     public static boolean isInvalidStatus(Integer currentStatus, List<Integer> approvedStatuses) {
          if (currentStatus == null || approvedStatuses == null || approvedStatuses.isEmpty()) {
               return false;
          }
          return approvedStatuses.contains(currentStatus);
     }
}
