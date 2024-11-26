package com.group1.interview_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.common.LinkUtil;
import com.group1.interview_management.common.RegexUtil;
import com.group1.interview_management.common.validator.UserValidator;
import com.group1.interview_management.dto.ApiResponse;
import com.group1.interview_management.dto.UserContactDTO;
import com.group1.interview_management.dto.UserDTO;
import com.group1.interview_management.dto.UserResetPasswordRequest;
import com.group1.interview_management.dto.UserSearchRequest;
import com.group1.interview_management.dto.UserStatusRequest;
import com.group1.interview_management.dto.UserUpdateRequestDTO;
import com.group1.interview_management.entities.Master;
import com.group1.interview_management.entities.User;
import com.group1.interview_management.services.MasterService;
import com.group1.interview_management.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

     private final UserService userService;
     private final MessageSource messageSource;
     private final LinkUtil linkUtil;
     private final MasterService masterService;
     private final UserValidator userValidator;

     @Secured("ROLE_ADMIN")
     @GetMapping("/get-all-user")
     public String getAllUser(
               @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
               @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
               Model model) {
          model.addAttribute("userList", userService.getAllUsers(pageNo, pageSize));
          List<Master> userStatus = masterService.getUserStatus(ConstantUtils.USER_STATUS);
          model.addAttribute("status", userStatus);
          return "user/user_list";
     }

     @GetMapping("/info")
     @ResponseBody
     public ResponseEntity<UserDTO> getUserDetail(@RequestParam String email) {
          return ResponseEntity.ok(userService.getUserByEmail(email));
     }

     @PostMapping("/reset-password")
     public ResponseEntity<ApiResponse> resetPassword(@RequestBody UserResetPasswordRequest request) {
          if (request.getNewPassword() == null || request.getConfirmPassword().isEmpty()) {
               return ResponseEntity.badRequest()
                         .contentType(MediaType.APPLICATION_JSON)
                         .body(new ApiResponse(false, messageSource.getMessage("ME002.2", null, Locale.getDefault())));
          }
          if (!request.getNewPassword().matches(RegexUtil.PASSWORD_REGEX)) {
               return ResponseEntity.badRequest()
                         .contentType(MediaType.APPLICATION_JSON)
                         .body(new ApiResponse(false, messageSource.getMessage("ME007", null, Locale.getDefault())));
          }
          if (!request.getNewPassword().equals(request.getConfirmPassword())) {
               return ResponseEntity.badRequest()
                         .contentType(MediaType.APPLICATION_JSON)
                         .body(new ApiResponse(false, messageSource.getMessage("ME006", null, Locale.getDefault())));
          }
          userService.changePassword(request.getUuid(), request.getNewPassword());

          return ResponseEntity
                    .ok(new ApiResponse(true, messageSource.getMessage("reset.password", null, Locale.getDefault())));
     }

     @GetMapping("/reset-password/{uuid}")
     public String resetPassword(@PathVariable String uuid, Model model, RedirectAttributes redirectAttributes) {
          if (linkUtil.isLinkValid(uuid)) {
               model.addAttribute("User", User.builder().build());
               model.addAttribute("uuid", uuid);
               return "reset_password";
          } else {
               return "auth/link_expired";
          }
     }

     @Secured("ROLE_ADMIN")
     @PostMapping("/search")
     @ResponseBody
     public Page<UserDTO> searchUsers(@RequestBody UserSearchRequest searchRequest) {
          Page<UserDTO> users = userService.searchUsers(searchRequest.getQuery(), searchRequest.getStatus(),
                    searchRequest.getPage(), searchRequest.getSize());
          return users;
     }

     @Secured("ROLE_ADMIN")
     @GetMapping("/user_detail/{id}")
     public String redirectUserDetail(@PathVariable int id, Model model) {
          UserDTO userDTO = userService.getUserById(id);
          model.addAttribute("userDetail", userDTO);
          return "user/user_detail";
     }

     @GetMapping("/user-role/{roleId}")
     @ResponseBody
     public ResponseEntity<List<UserDTO>> getMethodName(@PathVariable Integer roleId) {
          return ResponseEntity.ok(userService.getAllUsersByRole(roleId));
     }

     @Secured("ROLE_ADMIN")
     @GetMapping("/create-user")
     public String forwardCreateUser(Model model) {
          model.addAttribute("UserDTO", UserDTO.builder().build());
          model.addAttribute("genders", masterService.findByCategory(ConstantUtils.GENDER));
          model.addAttribute("roles", masterService.findByCategory(ConstantUtils.USER_ROLE));
          model.addAttribute("departments", masterService.findByCategory(ConstantUtils.DEPARTMENT));
          model.addAttribute("statuses", masterService.getUserStatus(ConstantUtils.USER_STATUS));
          return "user/create_user";
     }

     @Secured("ROLE_ADMIN")
     @PostMapping("/add-user")
     public ResponseEntity<Map<String, Object>> addUser(@RequestBody UserDTO userDTO, Authentication session) {
          Map<String, Object> response = new HashMap<>();
          Map<String, String> errors = new HashMap<>();
          userValidator.validateEmail(userDTO, errors);
          userValidator.validatePhone(userDTO, errors);
          userValidator.validateName(userDTO, errors);
          userValidator.validateDob(userDTO, errors);
          userValidator.checkRequiredFields(userDTO, errors);
          if (!errors.isEmpty()) {
               response.put("success", false);
               response.put("errors", errors);
               return ResponseEntity.badRequest().body(response);
          }
          userService.addUser(userDTO, session);
          response.put("success", true);
          response.put("message", messageSource.getMessage("ME027", null, Locale.getDefault()));
          return ResponseEntity.ok(response);
     }

     @Secured("ROLE_ADMIN")
     @GetMapping("/edit-user/{id}")
     public String forwardEdit(@PathVariable int id, Model model) {
          UserDTO userDTO = userService.getUserById(id);
          model.addAttribute("UserDTO", userDTO);
          model.addAttribute("genders", masterService.findByCategory(ConstantUtils.GENDER));
          model.addAttribute("roles", masterService.findByCategory(ConstantUtils.USER_ROLE));
          model.addAttribute("departments", masterService.findByCategory(ConstantUtils.DEPARTMENT));
          model.addAttribute("statuses", masterService.getUserStatus(ConstantUtils.USER_STATUS));
          return "user/edit_user";
     }

     @Secured("ROLE_ADMIN")
     @PostMapping("/update-user")
     public ResponseEntity<?> updateUser(@RequestBody UserUpdateRequestDTO requestDTO) {
          UserDTO userDTO = requestDTO.getUserDTO();
          UserContactDTO userContactDTO = requestDTO.getUserContactDTO();
          Map<String, String> errors = new HashMap<>();
          userValidator.checkRequiredFields(userDTO, errors);
          userValidator.validateName(userDTO, errors);
          if (!userDTO.getEmail().trim().equals(userContactDTO.getOriginalEmail())) {
               userValidator.validateEmail(userDTO, errors);
          }
          if (!userDTO.getPhoneNo().trim().equals(userContactDTO.getOriginalPhoneNo())) {
               userValidator.validatePhone(userDTO, errors);
          }
          userValidator.validateDob(userDTO, errors);
          if (!errors.isEmpty()) {
               return ResponseEntity.badRequest().body(errors);
          }
          if (userDTO.getId() == null) {
               return ResponseEntity.badRequest()
                         .body(messageSource.getMessage("userId.invalid", null, Locale.getDefault()));
          }
          UserDTO updatedUser = userService.updateUser(userDTO);
          return ResponseEntity.ok(updatedUser);
     }

     @Secured("ROLE_ADMIN")
     @PostMapping("/change-status")
     public ResponseEntity<?> changeUserStatus(@RequestBody UserStatusRequest userStatusRequest) {
          userService.changeStatus(userStatusRequest.getUserId(), userStatusRequest.getStatus());
          return ResponseEntity.ok("User status updated successfully.");
     }
}
