package com.group1.interview_management.common;

public enum JwtName {
     REFRESH_TOKEN("refresh_token"), 
     ACCESS_TOKEN("access_token");

     private String value;

     JwtName(String value) {
          this.value = value;
     }
     public String getValue() {
          return value;
     }
}
