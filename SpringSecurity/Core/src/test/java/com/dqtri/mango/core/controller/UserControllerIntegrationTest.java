/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.controller;

import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@Nested
@WebMvcTest(controllers = {UserController.class})
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerIntegrationTest extends AbstractIntegrationTest {
//TODO:
}
