package com.group1.interview_management.services.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import com.group1.interview_management.common.ConstantUtils;
import com.group1.interview_management.common.EmailTemplateName;
import com.group1.interview_management.common.PasswordUtils;
import com.group1.interview_management.common.UserUtils;
import com.group1.interview_management.dto.UserDTO;
import com.group1.interview_management.entities.EmailPeriod;
import com.group1.interview_management.entities.Master;
import com.group1.interview_management.entities.User;
import com.group1.interview_management.mapper.UserMapper;
import com.group1.interview_management.repositories.EmailPeriodRepository;
import com.group1.interview_management.repositories.MasterRepository;
import com.group1.interview_management.repositories.UserRepository;
import com.group1.interview_management.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

     private final UserRepository userRepository;
     private final UserMapper usermapper;
     private final MessageSource messageSource;
     private final EmailPeriodRepository emailPeriodServiceRepository;
     private final PasswordEncoder passwordEncoder;
     private final EmailService emailService;
     private final MasterRepository masterRepository;

     @Override
     public List<UserDTO> getAllUsers(int pageNo, int pageSize) {
          Pageable pageable = PageRequest.of(pageNo, pageSize);
          Page<User> users = userRepository.findAll(pageable);
          List<User> listOfUsers = users.getContent();
          List<UserDTO> usersDto = listOfUsers.stream().map(usermapper::toUserDTO).toList();
          return usersDto;
     }

     @Override
     public UserDTO getUserByEmail(String email) {
          User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                         String errorMessage = messageSource.getMessage("email.notfound", new Object[] { email },
                                   Locale.getDefault());
                         return new UsernameNotFoundException(errorMessage);
                    });
          return usermapper.toUserDTO(user);
     }

     @Override
     public void changePassword(@RequestParam("uuid") String uuid, String newPassword) {
          EmailPeriod emailPeriod = emailPeriodServiceRepository.findByUuid(uuid).get();
          User user = emailPeriod.getUser();
          user.setPassword(passwordEncoder.encode(newPassword));
          userRepository.save(user);
     }

     @Override
     public Page<UserDTO> searchUsers(String keyword, String status, int page, int pageSize) {
          Pageable pageable = PageRequest.of(page, pageSize);
          return userRepository.searchByKeyword(keyword,
                    (status != null && !status.isEmpty()) ? Integer.parseInt(status) : null, pageable);
     }

     @Override
     public UserDTO getUserById(int id) {
          User user = userRepository.findById(id).get();
          return usermapper.toUserDTO(user);
     }

     public List<UserDTO> getAllUsersByRole(int roleId) {
          List<User> users = userRepository.findByRoleId(roleId);
          List<UserDTO> usersDto = users.stream().map(usermapper::toUserDTO).toList();
          return usersDto;
     }

     public List<User> getUsersByRole(Integer role) {
          return userRepository.findByRoleId(role);
     }

     @Override
     public UserDTO addUser(UserDTO userDTO, Authentication session) {
          User createUser = usermapper.toUser(userDTO);
          String randomPassword = PasswordUtils.generateRandomPassword(8);
          createUser.setPassword(passwordEncoder.encode(randomPassword));
          createUser.setEnabled(true);
          createUser.setModifiedDate(LocalDateTime.now());
          createUser.setUsername_(UserUtils.encodeUsername(userDTO.getFullname()));
          User user = userRepository.save(createUser);
          User owner = (User) session.getPrincipal();
          if (owner != null) {
               try {
                    Map<String, Object> props = new HashMap<>();
                    props.put("email", userDTO.getEmail());
                    props.put("password", randomPassword);
                    props.put("owner", owner.getEmail());
                    emailService.sendMail("no-reply-email-IMS-system " + userDTO.getEmail(),
                              owner.getEmail(),
                              userDTO.getEmail(),
                              EmailTemplateName.CREATE_ACCOUNT_MAIL, props, false);
               } catch (Exception ex) {

               }
          }
          return usermapper.toUserDTO(user);
     }

     @Override
     public boolean emailExists(String email) {
          return userRepository.findByEmail(email).isPresent();
     }

     @Override
     public UserDTO updateUser(UserDTO userDTO) {
          User user = userRepository.findById(userDTO.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
          user.setFullname(userDTO.getFullname());
          user.setEmail(userDTO.getEmail());
          user.setDob(userDTO.getDob());
          user.setUsername_(UserUtils.encodeUsername(userDTO.getFullname()));
          user.setPhone(userDTO.getPhoneNo());
          user.setRoleId(Integer.parseInt(userDTO.getRole()));
          user.setStatus(Integer.parseInt(userDTO.getStatus()));
          user.setAddress(userDTO.getAddress());
          user.setGender(Integer.parseInt(userDTO.getGender()));
          user.setDepartmentId(Integer.parseInt(userDTO.getDepartment()));
          user.setNote(userDTO.getNote());
          user = userRepository.save(user);
          return usermapper.toUserDTO(user);
     }

     @Override
     public void changeStatus(int userId, String status) {
          User user = userRepository.findById(userId).get();
          Master master = masterRepository.findByCategoryAndCategoryValue(ConstantUtils.USER_STATUS, status).get();
          user.setStatus(master.getCategoryId());
          userRepository.save(user);
     }

     @Override
     public User getUserByIdNone(int id) {
          return userRepository.findById(id).get();
     }

     @Override
     public List<User> getUserByIdAndRoleIds(List<Integer> userIds, BindingResult errors, String field, List<Integer> roleIds) {
          List<User> users = userRepository.findByIdsAndRoleIds(userIds, roleIds).orElse(null);
          if (users == null && errors != null && field != null && errors.getFieldError(field) == null) {
               String errorMessage = messageSource.getMessage("ME008", null, Locale.getDefault());
               errors.rejectValue(field, "ME008", errorMessage);
          }
          return users;
     }

     public boolean phoneExists(String phoneNo) {
         return userRepository.existsByPhone(phoneNo);
     }
}
