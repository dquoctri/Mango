package com.dqtri.mango.authentication.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResetPasswordPayload {
    @NotBlank
    private String password;
}
