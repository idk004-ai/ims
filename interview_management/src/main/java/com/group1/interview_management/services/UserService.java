package com.group1.interview_management.services;

import java.util.List;

import com.group1.interview_management.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;

import com.group1.interview_management.dto.UserDTO;

public interface UserService {
    List<UserDTO> getAllUsers(int pageNo, int pageSize);

    UserDTO getUserByEmail(String email);

    public void changePassword(String uuid, String newPassword);

    Page<UserDTO> searchUsers(String name, String status, int page, int pageSize);

    User getUserByIdNone(int id);

    UserDTO getUserById(int id);

    List<UserDTO> getAllUsersByRole(int roleId);

    UserDTO addUser(UserDTO userDTO,Authentication session);

    boolean emailExists(String email);

    UserDTO updateUser(UserDTO userDTO);

    void changeStatus(int userId, String status);

    boolean phoneExists(String phoneNo);

    /**
     * Get user by id and role id with active status and enabled
     * @param userId
     * @param errors
     * @param field
     * @param roleIds
     * @return
     * 
     * Author: KhoiLNM
     * Date: 2024-11-11
     */
    List<User> getUserByIdAndRoleIds(List<Integer> userId, BindingResult errors, String field, List<Integer> roleIds);

    List<User> getUsersByRole(Integer roleId);
}
