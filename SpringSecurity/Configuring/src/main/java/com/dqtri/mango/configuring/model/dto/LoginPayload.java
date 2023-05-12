package com.dqtri.mango.configuring.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginPayload {
    @NotNull
    @Email
    private String email;
    @NotBlank
    private String password;
}
