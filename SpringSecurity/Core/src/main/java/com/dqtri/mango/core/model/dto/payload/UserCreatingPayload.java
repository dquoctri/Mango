/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.model.dto.payload;

import com.dqtri.mango.core.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserCreatingPayload {
    @NotNull
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private Role role;
}
