package com.group1.interview_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
     private Integer id;
     private String fullname;
     private String username;
     private String phoneNo;
     private String role; 
     private String status;
     private String email;
     private String gender; 
     private String address;
     private LocalDate dob;
     private String department;
     private String note;

  
     
}
