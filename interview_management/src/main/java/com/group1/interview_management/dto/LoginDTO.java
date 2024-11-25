package com.group1.interview_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class LoginDTO {
     @Email(message = "{email.invalid}")
     @NotEmpty(message = "{email.blank}")
     @NotNull(message = "{email.blank}")
     private String email;

     @NotEmpty(message = "{password.blank}")
     @NotNull(message = "{password.blank}")
     @Size(min = 8, message = "{password.notenough}")
     private String password;
}
