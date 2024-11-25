package com.group1.interview_management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.group1.interview_management.common.EmailUtils;
import com.group1.interview_management.dto.UserDTO;
import com.group1.interview_management.entities.User;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {


     @GetMapping
     @ResponseBody
     public ResponseEntity<UserDTO> getUsername(Authentication authenticatedUser) {
          if (authenticatedUser == null) {
               return ResponseEntity.badRequest().build();
          } else {
               User user = (User) authenticatedUser.getPrincipal();
               
               return ResponseEntity.ok().body(UserDTO.builder()
                         .id(user.getId())
                         .fullname(user.getFullname())
                         .username(EmailUtils.extractMail(user.getUsername()))
                         .phoneNo(user.getPhone())
                         .role(String.valueOf(user.getRoleId()))
                         .status(String.valueOf(user.getStatus()))
                         .email(user.getUsername())
                         .gender(String.valueOf(user.getGender()))
                         .address(user.getAddress())
                         .dob(user.getDob())
                         .department(String.valueOf(user.getDepartmentId()))
                         .note(user.getNote())
                         .build());
          }
     }

}
