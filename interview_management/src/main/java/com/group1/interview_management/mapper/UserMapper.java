package com.group1.interview_management.mapper;

import org.springframework.stereotype.Service;

import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.dto.UserDTO;
import com.group1.interview_management.entities.User;
import com.group1.interview_management.repositories.MasterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserMapper {
     private final MasterRepository masterRepo;
     public UserDTO toUserDTO(User user) {
          return UserDTO.builder()
                    .id(user.getId())
                    .fullname(user.getFullname())
                    .username(user.getUsername_())
                    .phoneNo(user.getPhone())
                    .dob(user.getDob()) 
                    .address(user.getAddress())
                    .gender(masterRepo.findByCategoryAndCategoryId(ConstantUtils.GENDER,user.getGender()).get().getCategoryValue())
                    .department(masterRepo.findByCategoryAndCategoryId(ConstantUtils.DEPARTMENT,user.getDepartmentId()).get().getCategoryValue())
                    .role(masterRepo.findByCategoryAndCategoryId(ConstantUtils.USER_ROLE,user.getRoleId()).get().getCategoryValue())
                    .email(user.getEmail())
                    .note(user.getNote())
                    .status(masterRepo.findByCategoryAndCategoryId(ConstantUtils.USER_STATUS,user.getStatus()).get().getCategoryValue())
                    .build();
     }

     public User toUser(UserDTO userDTO){
          return User.builder()
          .email(userDTO.getEmail())
          .fullname(userDTO.getFullname())
          .username_(userDTO.getUsername())
          .departmentId(Integer.parseInt(userDTO.getDepartment()))
          .roleId(Integer.parseInt(userDTO.getRole()))
          .status(Integer.parseInt(userDTO.getStatus()))
          .gender(Integer.parseInt(userDTO.getGender()))
          .phone(userDTO.getPhoneNo())
          .dob(userDTO.getDob())
          .note(userDTO.getNote())
          .address(userDTO.getAddress())
          .build();
     }

}
