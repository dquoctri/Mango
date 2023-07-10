/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.model.dto.response;

import com.dqtri.mango.core.model.SafeguardUser;
import com.dqtri.mango.core.model.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private Role role;

    public UserResponse(SafeguardUser appUser) {
        this.id = appUser.getPk();
        this.email = appUser.getEmail();
        this.role = appUser.getRole();
    }

    public static List<UserResponse> buildFromUsers(List<SafeguardUser> appUsers) {
        return appUsers.stream().map(UserResponse::new).toList();
    }
}
