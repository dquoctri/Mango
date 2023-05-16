package com.dqtri.mango.submission.model.dto.payload;

import com.dqtri.mango.submission.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCreatingPayload {
    @NotNull
    @Email
    private String email;

    @NotNull
    private Role role;
}
