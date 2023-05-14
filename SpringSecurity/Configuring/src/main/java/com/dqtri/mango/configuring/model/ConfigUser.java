package com.dqtri.mango.configuring.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConfigUser {
    private String email;
    private Role role;
}