/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.audit;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuditInfo {
    private String action;
    private String method;
    private String uri;
    private String email;
    private String description;
}
