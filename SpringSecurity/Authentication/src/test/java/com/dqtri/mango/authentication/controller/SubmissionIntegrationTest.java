/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.controller;


import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@Nested
@WebMvcTest(controllers = {SubmissionController.class})
@AutoConfigureMockMvc(addFilters = false)
public class SubmissionIntegrationTest extends AbstractIntegrationTest {

//TODO:
}
