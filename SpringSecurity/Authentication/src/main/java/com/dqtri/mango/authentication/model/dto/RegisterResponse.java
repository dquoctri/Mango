/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;

@Setter
@Getter
@AllArgsConstructor
public class RegisterResponse {
    URI url;

}