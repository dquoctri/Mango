/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.URI;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class RegisterResponse {
    URI url;
}
