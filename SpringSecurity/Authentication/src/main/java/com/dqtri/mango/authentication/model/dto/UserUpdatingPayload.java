package com.dqtri.mango.authentication.model.dto;

import com.dqtri.mango.authentication.model.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserUpdatingPayload {
    @NotNull
    private Role role;
}
