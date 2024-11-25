package com.group1.interview_management.dto;

public class UserUpdateRequestDTO {
    private UserDTO userDTO;
    private UserContactDTO UserContactDTO;

    // Getters and Setters
    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    public UserContactDTO getUserContactDTO() {
        return UserContactDTO;
    }

    public void setUserContactDTO(UserContactDTO UserContactDTO) {
        this.UserContactDTO = UserContactDTO;
    }
}
