/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.model.dto;

import com.dqtri.mango.authentication.model.MangoUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterPayload {

    @Email
    @NotNull
    private String email;

    @NotBlank
    private String password;
}
