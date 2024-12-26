package com.group1.interview_management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.dto.UserDTO;
import com.group1.interview_management.entities.User;
import com.group1.interview_management.services.MasterService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

     private final MasterService masterService;

     @GetMapping
     @ResponseBody
     public ResponseEntity<UserDTO> getUsername(Authentication authenticatedUser) {
          if (authenticatedUser == null) {
               return ResponseEntity.badRequest().build();
          } else {
               User user = (User) authenticatedUser.getPrincipal();
               String department = masterService.findByCategoryAndCategoryId(ConstantUtils.DEPARTMENT, user.getDepartmentId()).getCategoryValue();
               return ResponseEntity.ok().body(UserDTO.builder()
                         .id(user.getId())
                         .fullname(user.getFullname())
                         .username(user.getUsername_())
                         .phoneNo(user.getPhone())
                         .role(String.valueOf(user.getRoleId()))
                         .status(String.valueOf(user.getStatus()))
                         .email(user.getUsername())
                         .gender(String.valueOf(user.getGender()))
                         .address(user.getAddress())
                         .dob(user.getDob())
                         .department(department)
                         .note(user.getNote())
                         .build());
          }
     }

}
