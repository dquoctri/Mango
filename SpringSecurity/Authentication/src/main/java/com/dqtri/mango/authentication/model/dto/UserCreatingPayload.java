package com.dqtri.mango.authentication.model.dto;

import com.dqtri.mango.authentication.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCreatingPayload {
    @NotNull
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private Role role;
}
