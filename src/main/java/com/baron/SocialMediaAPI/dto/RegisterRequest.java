package com.baron.SocialMediaAPI.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "username cannot be blank")
    @NotEmpty(message = "username cannot be empty")
    @NotNull(message = "username cannot be null")
    @Size(min = 4, max = 50, message = "username should be between 4 and 50 characters long")
    private String username;

    @NotBlank(message = "email cannot be empty")
    @NotNull(message = "email cannot be null")
    @Email(message = "invalid format of email address")
    private String email;

    @NotBlank(message = "email cannot be empty")
    @NotNull(message = "password cannot be null")
    @NotEmpty(message = "password cannot be empty")
    @Size(min = 4, max = 50, message = "password should be between 4 and 50 characters long")
    private String password;

}