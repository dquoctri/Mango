/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.controller;


import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
public class MangoController {

    @GetMapping("/mango")
    @PreAuthorize("hasAnyRole('SUBMITTER')")
    List<String> mango() {
        return List.of("payload1a", "payload1b");
    }
}
