package com.group1.interview_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationDTO {

     @NotEmpty(message = "{firstname.blank}")
     @NotBlank(message = "{firstname.blank}")
     private String firstname;
     @NotEmpty(message = "{lastname.blank}")
     @NotBlank(message = "{lastname.blank}")
     private String lastname;
     @Email(message = "{email.invalid}")
     @NotEmpty(message = "{email.blank}")
     @NotBlank(message = "{email.blank}")
     private String email;
     @NotEmpty(message = "{password.blank}")
     @NotBlank(message = "{password.blank}")
     @Size(min = 8, message = "{password.notenough}")
     private String password;
}
